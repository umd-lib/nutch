package edu.umd.lib.indexer.title;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.nutch.parse.Parse;

import org.apache.nutch.indexer.IndexingFilter;
import org.apache.nutch.indexer.IndexingException;
import org.apache.nutch.indexer.NutchDocument;

import org.apache.nutch.crawl.CrawlDatum;
import org.apache.nutch.crawl.Inlinks;
import org.apache.nutch.parse.ParseData;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;

import java.lang.invoke.MethodHandles;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * If the "title" field has not been set, sets the "title" field to the
 * filename from the url.
 */

public class UmdTitleIndexingFilter implements IndexingFilter {
  private static final Logger LOG = LoggerFactory
      .getLogger(MethodHandles.lookup().lookupClass());
  
  private Configuration conf;

  public NutchDocument filter(NutchDocument doc, Parse parse, Text url,
      CrawlDatum datum, Inlinks inlinks) throws IndexingException {

    String url_s = url.toString();

    resetTitle(doc, parse.getData(), url_s);

    return doc;
  }

  // Reset title to the URL, if title is not set.

  // Patterns used to extract filename from URL
  static Pattern patterns[] = { null };

  static {
    try {
      // order here is important
      patterns[0] = Pattern.compile("^https*:(.*/)+");
    } catch (PatternSyntaxException e) {
      LOG.error("Error parsing pattern", e);
    }
  }

  private NutchDocument resetTitle(NutchDocument doc, ParseData data, String url) {
    if ((doc.getFieldValue("title") != null) || (url == null))
      return doc;
    
    for (int i = 0; i < patterns.length; i++) {
      Matcher matcher = patterns[i].matcher(url);
      if (matcher.find()) {
        String replacementText = matcher.replaceFirst("");
        doc.add("title", replacementText);
        break;
      }
    }

    return doc;
  }
  
  public void setConf(Configuration conf) {
    this.conf = conf;
  }

  public Configuration getConf() {
    return this.conf;
  }
}

