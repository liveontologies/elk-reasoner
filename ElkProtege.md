# Using ELK in Protégé #

A plugin is available for using ELK in the [Protégé](http://protege.stanford.edu/) ontology editor.

## Installation ##

The plugin requires [Protégé 4.1](http://protege.stanford.edu/download/registered.html#p4.1) or later to run. To install, simply download and unzip the most recent version of the elk-protege package from the [download section](http://code.google.com/p/elk-reasoner/downloads/list), and place the file `org.semanticweb.elk.jar` in the `plugins` directory of your Protégé installation.

## Supported Features ##

Protégé interacts with ELK by calling OWL API functions. Since not all of these functions are supported at the moment, errors might be issued when using some advanced reasoning task. Please see the page about [supported reasoning tasks](ReasoningTasks.md) for further information.

In order to minimize the number of error messages, it is recommended to switch off some of the inference types. Please go to Reasoner > Configure > Displayed Inferences and uncheck:

  * Disjoint Classes in Displayed Class Inferences
  * All Displayed Object Property Inferences
  * All Displayed Data Property Inferences
  * Everything except Types in Displayed Individual Inferences

Also, ELK supports only certain OWL constructors as described on the page about [supported OWL features](OwlFeatures.md). ELK will issue warnings about unsupported features during ontology loading.