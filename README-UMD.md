# Apache Nutch - UMD Customizations

This page provides information about UMD development and customization
of Apache Nutch.

## Scope of Customizations

The UMD customizations in this repository should only include
"generic" changes to the official Apache Nutch code.

This will typically be limited to Java source code changes, such as the
addition of new UMD-specific plugins.

Changes to Nutch configuration files for particular projects should not
generally be made in this repository. Consider placing project-specific
configuration changes in the project that uses them, perhaps with an
associated Dockerfile (see the "Dockerfile-nutch" file in the
[umd-lib/searchumd][searchumd] application for an example).

## UMD Customizations

See [UmdCustomizations.md](UmdCustomizations.md) for additional
information about the specific customizations made in this repository.

## Branches and Tags

This project follows the "git flow" branch model described in
[https://nvie.com/posts/a-successful-git-branching-model/][gitflow],
with the following changes in branch names:

* develop -> umd-develop
* master -> umd-master

For tagging, tags should have the form `<UPSTREAM BASE TAG>-umd-<UMD VERSION NUMBER>`
where the `<UPSTREAM BASE TAG>` corresponds to the Apache Nutch version
the customizations are based on, and the `<UMD VERSION NUMBER>` is the
version of the customizations.

## Build Prerequisites

In order to build this project, the Apache Ant must be installed. See
[http://ant.apache.org/][ant]. On Mac OS X, Ant can be installed via
Homebrew:

```
> brew install ant
```

## Running Nutch

### Running Solr

UMD projects using Nutch are typically configured to assume that an
Apache Solr instance is running to index the results. 

Using the "searchumd" application as an example,  the following steps
(drawn from the "Development Setup" section of the README.md in the
"umd-lib/searchumd" GitHub repository), run a Solr instance in Docker
so that it is accessible from the host machine:

1) Checkout the "searchumd" GitHub repository and switch into the
directory:

```
> git clone https://github.com/umd-lib/searchumd.git
> cd searchumd
```

2) Run the following commands to build and run Solr using Docker:

```
> docker build -t searchumd-solr:dev -f Dockerfile-solr .
> docker network create dev_network
> docker run --rm -p 8983:8983 --network dev_network --name solr_app --mount source=solr-data,destination=/opt/solr/server/solr/nutch searchumd-solr:dev
```

The above commands do the following:

* Creates a Docker network named "dev_network", so that a searchumd
    application running in Docker can communicate with Solr.
* The Solr instance is exposed (via the "-p 8983:8983" option) on
    port 8983 of the localhost, so it is accessible to Nutch (or a
    searchumd application) running on the host machine.
* The Solr data is saved in a persistent Docker volume named
    "solr-data". If you want to run a "clean" instance of Solr, delete
    the "solr-data" Docker volume before running the Solr container:
    ```
    > docker volume rm solr-data
    ```
* The Solr should be accessible at [http://localhost:8983/solr][solr]

### Building Nutch

Before building and running Nutch for the first time, copy the Nutch
configuration files from the associated project into into this
repository. For example, for the "searchumd" application, and
assuming that the "searchumd" repository and this repository are
checked out into the same parent directory, the
following command will copy the files:

```
> cp -r ../searchumd/docker_config/nutch/* .
```

This will copy over the Nutch configuration files, and the "urls"
directory containing the seed URL to use for the search.

To build Nutch, simply run the "ant" command without any arguments:

```
> ant
```

This builds the source code and places an runnable instance of Nutch in
the runtime/local/ subdirectory.

### Running Nutch

When running Nutch, the URL of the running Solr instance must be
specified. 

To run Nutch from the root project directory:

```
> runtime/local/bin/crawl -i -D solr.server.url=http://localhost:8983/solr/nutch -s urls/ LibCrawl/ 2
```

This will run two crawl iterations, and index the results in the Solr
instance. The crawl data will be stored in the "LibCrawl/" subdirectory.

## Eclipse Setup

For development work, the project can be set up in Eclipse as follows:
 
### Apache IvyDE plugin

In Eclipse, install the "Apache IvyDE" plugin via the Eclipse
Marketplace (Help | Eclipse Marketplace...).

### Code Formatting Setup

Set up Eclipse code formatting using the official Apache Nutch
conventions as follows:

1) In Eclipse, open the "Preferences" dialog by selecting
"Eclipse | Preferences..." from the menubar (if using the Spring Too
 Suite version of Eclipse, this will be
 "Spring Tool Suite | Preferences...")
 
2) In the "Preferences" dialog, select "Java | Code Style | Formatter".
The "Formatter" pane will be shown on the right side of the dialog.
 
3) In the "Formatter" page, left-click the "Configure Project Specific 
Settings..." link, and select "nutch" in the resulting dialog. A
"Properties for nutch" modal dialog will be displayed.
 
4) In the "Properties for nutch" modal dialog, do the following:
 
    a) Left-click the "Enable project specific settings" checkbox.
 
    b) Left-click the "Import..." button. In the resulting file dialog,
       select the "eclipse-codeformat.xml" file in the project directory.
 
    c) Left-click the "Apply and Close" buttons to close the dialogs.
 
### Importing Nutch into Eclipse

To import the Nutch source code into Eclipse:

1) Run the following command to generate the .classpath and .project
files needed for Eclipse.

```
> ant eclipse
```

2) Run Eclipse, and import the source code using the "File | Import..."
command, selecting "General | Existing Projects into Workspace" in the
dialog.

[ant]: http://ant.apache.org/
[gitflow]: https://nvie.com/posts/a-successful-git-branching-model/
[searchumd]: https://github.com/umd-lib/searchumd
[solr]: http://localhost:8983/solr

