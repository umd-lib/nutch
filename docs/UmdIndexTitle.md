# umd-index-title

An IndexingFilter implementation that sets the "title" attribute of the
NutchDocument to the last subpath in a URL, if the "title" attribute is
null.

This fixes an issue when crawling a website with PDF documents, where the
Apache Tika parser only sets the "title" attribute if there is a title
set in the PDF file. In these cases, the title will be set to the filename
of the PDF.

The code and behavior in this plugin is similar to that in the stock
"index-more" plugin, but does _not_ require the "Content-Disposition"
HTTP header to be set.

## Configuration

In keeping with the guidelines indicated in the "Scope of Customizations"
section in the [README-UMD.md](README-UMD.md), this plugin is not
configured to run as-is.

The following uses the configuration for the "searchumd" application as
an example of how to set up the Apache Nutch and Solr configuration
files to use this plugin.

### Configuring Nutch

To configure Nutch to use the "umd-index-title" plugin:

1) Configure Nutch to use the plugin as part of the indexing step.

The first part simply requires adding the "umd-index-title"
plugin to the "plugin.includes" XML stanza in the conf/nutch-site.xml
file:

```
<configuration>
  <property>
    <name>plugin.includes</name>
    <value>protocol-http|urlfilter-(regex|validator)|parse-(html|tika)|index-(basic|anchor|metadata)|indexer-solr|scoring-opic|urlnormalizer-(pass|regex|basic)|umd-parsefilter-jsonld|umd-index-title</value>
    ...
```

(Note the addition of "umd-index-title" at the end of the "value" entry).

### Configuring Solr

Solr should not require any configuration, as a "title" field should already
be present.
