# The ELK Reasoner #

ELK is a reasoner for [OWL 2](http://www.w3.org/TR/owl2-overview/) ontologies that currently supports a part of the [OWL EL](http://www.w3.org/TR/owl2-profiles/#OWL_2_EL) ontology language.

The goal of ELK is to provide a **very fast** reasoning engine for OWL EL. Currently, the [supported OWL features](OwlFeatures.md) and [reasoning tasks](ReasoningTasks.md) are still limited (but already sufficient for important [ontologies](TestOntologies.md) such as SNOMED CT). The aim of the project is to complete the implementation for all OWL EL features and relevant reasoning functions (e.g. for unrestricted use [in Protégé](ElkProtege.md)) – but this will be done step-by-step so as to ensure [top performance](Performance.md) of each new feature.

For further information, please see the navigation items on the left.

## Feedback ##

ELK is currently under heavy development and we welcome feedback from users. Bugs and wishes can be reported via the [issue list](http://code.google.com/p/elk-reasoner/issues/list). Note, however, that ELK supports only some [OWL features](OwlFeatures.md) and [reasoning tasks](ReasoningTasks.md), so there is  no need to report bugs related to these known limitations. You can also check the latest [nightly builds](GettingElk.md) to see if your bug has been already fixed.

For further feedback and questions, you are also welcome to [contact the authors](Contact.md).