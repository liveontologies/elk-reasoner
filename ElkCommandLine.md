# Using ELK from the Command Line #

ELK can be invoked via a simple command line interface that is provided by the ELK standalone package.

## Installation ##

To use the command line client, it suffices to download and unzip the most recent version of elk-standalone from the [download section](http://code.google.com/p/elk-reasoner/downloads/list).

## Running the Command-Line Client ##

You can invoke the client with the following command:

`java -jar elk-standalone.jar`

provided that you are in the directory where the jar file is found. Otherwise, the full or relative path to the file must be used (syntax depending on your operating system). You may want to specify further Java parameters for increasing available memory for classifying larger ontologies, e.g. by setting

`java -XX:+AggressiveHeap -jar elk-standalone.jar`

or by providing a increased maximum heap size such as `-Xmx3000m`.

## Parameters for Controlling the Client ##

Invoking the ELK Client without further parameters displays a list of supported options. A typical example call is as follows:

`java -jar elk-standalone.jar -i ontology.owl --classify -o taxonomy.owl`

This will load the ontology from the file ontology.owl, compute its [classification](ReasoningTasks.md) and store the result in the file taxonomy.owl. Without the output parameter ELK will still compute the result but it will neither store nor display the result anywhere; this can be used for performance experiments.

If ELK cannot parse your ontology, this is probably because it is in the RDF/XML syntax. The command line client can only read ontologies in [OWL 2 Functional-Style Syntax](http://www.w3.org/TR/owl2-syntax/).  OWL ontologies in other formats can be converted into Functional-Style Syntax using [Protégé](http://protege.stanford.edu/) version 4.1 or higher. To convert a file, open it in Protege and save using the menu:

> File > Save as... > OWL Functional Syntax.

Alternatively, you can also use the [EKL Protégé plugin](ElkProtege.md) directly.