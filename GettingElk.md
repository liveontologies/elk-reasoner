# Obtaining ELK #

The ELK reasoner can be downloaded in various ways, depending on the desired usage.

## File Releases ##

File releases of ELK are published as ZIP files and can be found in the [Maven Central](http://repo1.maven.org/maven2/org/semanticweb/elk/elk-distribution/). The latest stable version is 0.4.2. There are currently three forms of packaged releases:

  * [elk-distribution-0.4.2-owlapi-library.zip](http://repo1.maven.org/maven2/org/semanticweb/elk/elk-distribution/0.4.2/elk-distribution-0.4.2-owlapi-library.zip) provides a package for using ELK from [OWL API](ElkOwlApi.md).
  * [elk-distribution-0.4.2-protege-plugin.zip](http://repo1.maven.org/maven2/org/semanticweb/elk/elk-distribution/0.4.2/elk-distribution-0.4.2-protege-plugin.zip) provides a package for using ELK as a [Protégé plugin](ElkProtege.md).
  * [elk-distribution-0.4.2-standalone-executable.zip](http://repo1.maven.org/maven2/org/semanticweb/elk/elk-distribution/0.4.2/elk-distribution-0.4.2-standalone-executable.zip) provides a package for using ELK as a standalone tool that is controlled via a [command line client](ElkCommandLine.md).

Previous releases can also be found at the [Maven Central](http://repo1.maven.org/maven2/org/semanticweb/elk/elk-distribution/) or on the [Downloads](https://code.google.com/p/elk-reasoner/downloads/list) page.

## Nightly Builds ##

We provide nightly builds of ELK. These are work in progress and may not function as reliably as regular releases. However, if you discover a bug, you can check the latest build to see if it has been already fixed.

  * [Protege Plugin](http://oss.sonatype.org/service/local/artifact/maven/content?r=snapshots&g=org.semanticweb.elk&a=elk-distribution&c=protege-plugin&e=zip&v=LATEST) latest build


  * [OWL API Library](http://oss.sonatype.org/service/local/artifact/maven/content?r=snapshots&g=org.semanticweb.elk&a=elk-distribution&c=owlapi-library&e=zip&v=LATEST) latest build


  * [Standalone Executable](http://oss.sonatype.org/service/local/artifact/maven/content?r=snapshots&g=org.semanticweb.elk&a=elk-distribution&c=standalone-executable&e=zip&v=LATEST) latest build


## Downloading the Source Code ##

The source code of ELK is publicly available from the project [GIT repository](http://code.google.com/p/elk-reasoner/source/checkout). This repository is a mirrored from the [ELK github repository](https://github.com/klinovp/elk) which is used by the ELK developers. You can clone the sources from there or make a pull request.