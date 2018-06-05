package edu.umd.lib.parsefilter.jsonld;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.nutch.metadata.Metadata;
import org.apache.nutch.parse.HTMLMetaTags;
import org.apache.nutch.parse.HtmlParseFilter;
import org.apache.nutch.parse.Parse;
import org.apache.nutch.parse.ParseResult;
import org.apache.nutch.protocol.Content;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DocumentFragment;

import com.google.schemaorg.JsonLdSerializer;
import com.google.schemaorg.JsonLdSyntaxException;
import com.google.schemaorg.SchemaOrgType;
import com.google.schemaorg.core.Thing;
import com.google.schemaorg.core.WebPage;
import com.google.schemaorg.core.datatype.Text;

/**
 * Filters the raw HTML for JSON-LD scripts, adding selected information
 * from the scripts into the parse metadata.
 */
public class UmdJsonLdParseFilter implements HtmlParseFilter {
  private static final Logger LOG = LoggerFactory
      .getLogger(MethodHandles.lookup().lookupClass());

  /**
   * Pattern used to extract JSON-LD scripts from HTML
   */
  private static final Pattern JSON_LD_SCRIPT_PATTERN = Pattern.compile(
      ".*<script type=\\\"application\\/ld\\+json\\\">(.*?)</script>.*",
      Pattern.DOTALL);

  private Configuration conf;

  public UmdJsonLdParseFilter() {
  }

  public void setConf(Configuration conf) {
    this.conf = conf;
  }

  public Configuration getConf() {
    return this.conf;
  }

  /**
   * Extracts JSON-LD scripts from the HTML, and populates metadata with
   * selected JSON-LD information.
   */
  @Override
  public ParseResult filter(Content content, ParseResult parseResult,
      HTMLMetaTags metaTags, DocumentFragment doc) {

    Parse parse = parseResult.get(content.getUrl());
    String rawHtml = new String(content.getContent());

    Metadata metadata = parse.getData().getContentMeta();
    extractJsonLd(rawHtml, metadata);

    return parseResult;
  }

  /**
   * Extract JSON-LD scripts from the provided HTML, populating the
   * provided Metadata with information from the JSON-LD.
   * 
   * @param html
   *          the raw HTML of the web page
   * @param metadata
   *          the Metadata object to populate with JSON-LD data.
   */
  private void extractJsonLd(String html, Metadata metadata) {
    Pattern p = JSON_LD_SCRIPT_PATTERN;
    Matcher m = p.matcher(html);

    List<String> jsonLdMatches = new ArrayList<>();

    while (m.find()) {
      jsonLdMatches.add(m.group(1));
    }

    for (String jsonLdString : jsonLdMatches) {
      try {
        JsonLdSerializer serializer = new JsonLdSerializer(true);
        List<Thing> actual = serializer.deserialize(jsonLdString);
        Thing firstThing = actual.get(0);

        // For WebPage objects, store "text" in the metadata
        if ("http://schema.org/WebPage".equals(firstThing.getFullTypeName())) {
          WebPage webPage = (WebPage) firstThing;
          List<SchemaOrgType> textList = webPage.getTextList();
          if (textList.size() > 0) {
            Text text = (Text) textList.get(0);
            String webPageText = text.getValue();

            // Ignore text that is all whitespace, empty, or null.
            if (!StringUtils.isBlank(webPageText)) {
              // Trim whitespace, because some documents have opening/trailing
              // spaces
              metadata.add("documentContent", webPageText.trim());
              break;
            }
          }
        }
      } catch (JsonLdSyntaxException jse) {
        LOG.error("Error parsing JSON-LD", jse);
      }
    }
  }
}
