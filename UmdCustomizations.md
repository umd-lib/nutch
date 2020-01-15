# UMD Customizations

This page provides information about UMD customizations to the official
Apache Nutch codebase.

* umd-parsefilter-jsonld - [docs/UmdJsonLdParseFilter.md](docs/UmdJsonLdParseFilter.md)
* umd-index-title - [docs/UmdIndexTitle.md](docs/UmdIndexTitle.md)

## Maven Repository Changes

On January 15, 2020, the Maven Central repository was updated to not allow
communicaton over http. See https://support.sonatype.com/hc/en-us/articles/360041287334.

Updated the following files to work around this issue:

* default.properties - Updated the "ivy.repo.url" property
* ivy/ivysettings.xml - Changed "repo.maven.org" property value
