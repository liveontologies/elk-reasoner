# Using ELK in Snow Owl #
Snow Owl 1.0.0 ships using ELK as its default reasoner. If you're using an older version, a plugin is available for using ELK in the [Snow Owl](http://www.b2international.com/portal/snow-owl) ontology editor.

## Installation ##
The plugin requires [Snow Owl 0.8.0](http://www.b2international.com/portal/snow-owl/registration-form) or higher to run. To install, simply download and unzip the most recent version of the elk-protege package from the [download section](http://code.google.com/p/elk-reasoner/downloads/list), and place the file `org.semanticweb.elk.jar` in the `dropins` directory of your Snow Owl installation.

## Supported Features ##
Snow Owl interacts with ELK by calling OWL API functions. Since not all of these functions are supported at the moment, errors might be issued when using some advanced reasoning task. Please see the page about [supported reasoning tasks](ReasoningTasks.md) for further information.