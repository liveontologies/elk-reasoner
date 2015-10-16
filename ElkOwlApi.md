# Using ELK from OWL API #

ELK provides a partial implementation of the [OWL API](http://owlapi.sourceforge.net/) interfaces. Currently it only supports methods for computing and browsing the [class and instance hierarchy](ReasoningTasks.md) of an ontology.

## Installation ##

Download and extract the most recent version of the zipped elk-owlapi from the [download section](http://code.google.com/p/elk-reasoner/downloads/list). Add elk-owlapi.jar and all .jar files in the directory lib to your java class path. Of course, you will also need the OWL API itself, which can be downloaded from [here](http://owlapi.sourceforge.net/download.html).

## Usage ##

An instance of the ELK reasoner is created through the factory `org.semanticweb.elk.owlapi.ElkReasonerFactory` which implements the OWL API `OWLReasonerFactory` interface. It is important to call `reasoner.dispose()` before exiting the program to terminate all concurrent worker threads used by the reasoner.

As of v. 0.4.0, ELK fully supports the following queries:
```
getBottomClassNode
getEquivalentClasses
getInstances
getSubClasses
getSuperClasses
getTopClassNode
getTypes
getUnsatisfiableClasses
isConsistent
isSatisfiable
```

and the following non-logical methods:
```
dispose
flush
getPendingAxiomAdditions
getPendingAxiomRemovals
getPendingChanges
getPrecomputableInferenceTypes
getReasonerName
getReasonerVersion
getRootOntology
interrupt
isPrecomputed
precomputeInfereces
```


## Example ##

The following example program demonstrates how to use ELK to classify an ontology through OWL API. The program closely follows the example ["Saving Inferred Axioms"](http://owlapi.svn.sourceforge.net/viewvc/owlapi/v3/trunk/examples/src/main/java/org/coode/owlapi/examples/Example11.java?view=markup) from OWL API.

```java

```
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentClassAxiomGenerator;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import java.util.ArrayList;
import java.util.List;
import java.io.File;

import org.semanticweb.elk.owlapi.ElkReasonerFactory;

public class Example {

    public static void main(String[] args) throws OWLOntologyStorageException,
			OWLOntologyCreationException {
	OWLOntologyManager inputOntologyManager = OWLManager.createOWLOntologyManager();
	OWLOntologyManager outputOntologyManager = OWLManager.createOWLOntologyManager();

	// Load your ontology.
	OWLOntology ont = inputOntologyManager.loadOntologyFromOntologyDocument(new File("path to ontology"));

	// Create an ELK reasoner.
	OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();
	OWLReasoner reasoner = reasonerFactory.createReasoner(ont);

	// Classify the ontology.
	reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);

	// To generate an inferred ontology we use implementations of
	// inferred axiom generators
	List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();
	gens.add(new InferredSubClassAxiomGenerator());
	gens.add(new InferredEquivalentClassAxiomGenerator());

	// Put the inferred axioms into a fresh empty ontology.
	OWLOntology infOnt = outputOntologyManager.createOntology();
	InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner,
			gens);
	iog.fillOntology(outputOntologyManager, infOnt);

	// Save the inferred ontology.
	outputOntologyManager.saveOntology(infOnt,
			new OWLFunctionalSyntaxOntologyFormat(),
			IRI.create((new File("path to result").toURI())));

	// Terminate the worker threads used by the reasoner.
	reasoner.dispose();
    }
}
```
```

Assuming that $elk-owlapi is the path to the directory that was extracted from the elk-owlapi zip file, and $owlapi is the path to your installation of OWL API, then the above program can be compiled by executing the command

on Windows:
```
javac -cp ".;$elk-owlapi\elk-owlapi.jar;$elk-owlapi\lib\*;$owlapi\owlapi-bin.jar" Example.java
```

on Linux and Mac OS X:
```
javac -cp ".:$elk-owlapi/elk-owlapi.jar:$elk-owlapi/lib/*:$owlapi/owlapi-bin.jar" Example.java
```

After compilation, the program can be executed by running:

on Windows:
```
java -cp ".;$elk-owlapi\elk-owlapi.jar;$elk-owlapi\lib\*;$owlapi\owlapi-bin.jar" Example
```

on Linux and Mac OS X:
```
java -cp ".:$elk-owlapi/elk-owlapi.jar:$elk-owlapi/lib/*:$owlapi/owlapi-bin.jar" Example
```


## Disabling Logging Messages ##

By default ELK prints all information, warning, and error messages to standard output. If this is undesirable, it is possible to disable some or all messages, or reconfigure how they should be printed.

For printing log messages, ELK is using the [Apache log4j library](http://logging.apache.org/log4j/1.2/), which is included in the `lib` directory of the `elk-owlapi` package.

The easiest way to disable all or some of ELK messages is to lift the logging threshold. By default, the threshold is set to `WARN`, which means that all warning, and error messages are going to be printed. To disable all ELK logging messages completely, just add the following line somewhere before ELK is used:

```
Logger.getLogger("org.semanticweb.elk").setLevel(Level.OFF);
```

Instead of disabling all messages, one can leave only the error messages:

```
Logger.getLogger("org.semanticweb.elk").setLevel(Level.ERROR);
```

One can also have a more fine-grained control of messages depending on the components they come from. For example, the following disables warning messages only within the indexing component (which is used when the ontology is loaded into ELK):

```
Logger.getLogger("org.semanticweb.elk.reasoner.indexing").setLevel(Level.ERROR);
```

Disabling warning messages completely is may not be an ideal solution. In order to keep such messages, but do not pollute the standard output, one can configure, e.g., that all such messages are printed into a text files. This can be done as follows:

```
Logger logger = Logger.getRootLogger(); // this is the most general logger used to print all messages
logger.removeAllAppenders(); // we remove all appenders registered with this logger
FileAppender fa = new FileAppender(); // let us define an appender that prints messages to a file
fa.setFile("elk-reasoner.log"); // the file name where the messages should be printed to
fa.setLayout(new PatternLayout("%-5r [%t] %-5p %c %x - %m%n")); // this defines how the messages are printed
fa.setThreshold(Level.WARN); // all messages with the level lower then WARN will be ignored by this appender
fa.setAppend(false); // when the program starts over the messages are not appended at the end but overwritten
fa.activateOptions();	// apply the file and append options to this appender
logger.addAppender(fa); // adding the created appender to the logger
```

It is possible to add several independently configured appenders and use them with loggers for different components, but there should be at least one appender for the root logger defined --  one cannot just remove all appenders. For further information, see the [log4j documentation](http://logging.apache.org/log4j/1.2/apidocs/index.html).