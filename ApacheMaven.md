# Using ELK with Apache Maven #

ELK and all its components are developed using [Apache Maven](http://maven.apache.org/). Starting from version 0.3.1, ELK and all its programming libraries are available from the [Maven Central Repository](http://search.maven.org). The `GroupId` of ELK is:

```
 org.semanticweb.elk
```

For example to use ELK, through the [OWL API](http://owlapi.sourceforge.net) reasoner interface, add the following dependency to your Maven project configuration:

```
<dependency> 
   <groupId>org.semanticweb.elk</groupId> 
   <artifactId>elk-owlapi</artifactId> 
   <version>0.4.0</version> 
</dependency> 
```

See also [how to use ELK from OWL API](http://code.google.com/p/elk-reasoner/wiki/ElkOwlApi).

For further information about the Maven artifacts of ELK, please refer to the automatically generated [ELK Maven documentation](http://elk.semanticweb.org/maven/latest/).