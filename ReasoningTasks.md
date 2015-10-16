# Reasoning Tasks Supported by ELK #

There are a number of different reasoning tasks that OWL reasoners may support. Many of them can be logically reduced to each other, but in practice this is often not desirable and sometimes not feasible at all. Moreover, many specific tasks are required for integration in tools like [Protégé](ElkProtege.md). Therefore, this page points out which reasoning tasks are _directly_ supported by ELK at the moment. All these tasks are supported using either the [command-line interface of ELK](ElkCommandLine.md),  [ELK from OWL API](ElkOwlApi.md), or [ELK in Protégé](ElkProtege.md).

## Consistency Checking ##

Consistency checking is the task of computing whether or not an ontology is free of logical contradictions. Formally, an ontology is defined to be _consistent_ if it has at least one model.

## TBox Classification ##

Classification is the task of computing the implied subclass/superclass relationships between all _named_ classes in an ontology. Besides finding out whether a class is subsumed by another one or not, this task involves the _transitive reduction_ of the computed class taxonomy: only direct subclass/superclass relations are returned in the result. Note that this task is meaningful only if the ontology is consistent; an inconsistent ontology makes all classes mutually equivalent. ELK automatically triggers consistency checking before classification.

## ABox Realization ##

Realization is the task of computing the implied instance/type
relationships between all _named_ individuals and _named_ classes in an ontology. Similarly to classification, only direct instance/type relations are returned in the result. In order to determine which instance/type relations are direct, one needs to know all subclass/superclass between named classes in the ontology. Therefore, ELK automatically triggers TBox classification before ABox realization.

## Incremental Reasoning ##

From version 0.4.0 ELK can perform the above reasoning task incrementally after changes in ontology (additions, deletions, as well as modification of axioms). This means that the reasoner will try to reuse as many results from the previous computation as possible. At the moment, incremental reasoning is not supported after changes with role axioms (SubObjectPropertyOf, EquivalentObjectProperties, TransitiveObjectProperty, ReflexiveObjectProperty).

## Querying Complex Class Expressions ##

From version 0.4.0 ELK supports querying (retrieving subclasses, superclasses, and instances) of complex (unnamed) class expressions through incremental reasoning. Essentially the reasoner internally defines a new named class equivalent to the desired complex class and queries the new named class instead. See the the corresponding user page for more [details](QueryingComplexClasses.md).