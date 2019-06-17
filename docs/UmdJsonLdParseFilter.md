# umd-parsefilter-jsonld

A HtmlParseFilter implementation that retrieves JSON-LD scripts from
web pages, adding selected information from the scripts into the parse
metadata.

The current implementation looks for a JSON-LD script containing a
[http://schema.org/WebPage][webpage] object, and extracts the "text"
value, storing it in a "documentContent" field in the parse content
metadata.

## Configuration

In keeping with the guidelines indicated in the "Scope of Customizations"
section in the [README-UMD.md](README-UMD.md), this plugin is not
configured to run as-is.

The following uses the configuration for the "searchumd" application as
an example of how to set up the Apache Nutch and Solr configuration
files to use this plugin.

### Configuring Nutch

To configure Nutch to use "umd-parsefilter-jsonld" plugin:

1) Configure Nutch to use the plugin as part of the parsing step.

The first part simply requires adding the "umd-parsefilter-jsonld"
plugin to the "plugin.includes" XML stanza in the conf/nutch-site.xml
file:

```
<configuration>
  <property>
    <name>plugin.includes</name>
    <value>protocol-http|urlfilter-(regex|validator)|parse-(html|tika)|index-(basic|anchor|metadata)|indexer-solr|scoring-opic|urlnormalizer-(pass|regex|basic)|umd-parsefilter-jsonld</value>
    ...
```

(Note the addition of "umd-parsefilter-jsonld" at the end of the "value"
entry).

2) Configure Nutch to include the "documentContent" field added by this
plugin to the parse content metadata into the Solr index.

For this part, the "index-metadata" plugin (included in the official
Apache Nutch release) simply needs to be configured to include the
"documentContent" from the parse content metadata to the index. To
do this, add the following to the conf/nutch-site.xml file:

```
  <property>
    <name>index.content.md</name>
    <value>documentContent</value>
    <description>Comma-separated list of keys to be taken from the content
    metadata to generate fields.
    </description>
  </property>
```

3) When indexing the Nutch data into Solr, add the following line to the
"fields" XML stanza in the conf/solrindex-mapping.xml file:

```
<fields>
  ...
  <field dest="documentContent" source="documentContent"/>
</fields>
```

This maps the "documentContent" field in Nutch to the "documentContent"
field in Solr.

### Configuring Solr

In order for Solr to be able to index the "documentContent" field
provided by this plugin, the Solr "schema.xml" file must be configured
as follows:

1) Add the "documentContent" field to the "fields" stanza:

```
<fields>
  ...
  <field name="documentContent" type="text_general" stored="true" indexed="true"/>
  ...
 </fields>
 ```
 
 2) Add the "documentContent" as a "copy" field at the bottom of the
 file, so that the value is part of the "text" field:
 
 ```
  <copyField source="documentContent" dest="text"/>
 ```
 
 [webpage]: http://schema.org/WebPage
 