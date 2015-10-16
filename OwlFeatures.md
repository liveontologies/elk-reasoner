

# OWL Features Supported by ELK #

ELK currently supports a subset of the light-weight [OWL EL](http://www.w3.org/TR/owl2-profiles/#OWL_2_EL) ontology language that is documented on this page. It is intended to support all of OWL EL in upcoming versions of ELK.

## Supported OWL constructs ##

The following constructs of the OWL ontology language are supported in ELK 0.4.0.

Axiom types:
  * SubClassOf
  * EquivalentClasses
  * DisjointClasses
  * SubObjectPropertyOf
  * EquivalentObjectProperties
  * TransitiveObjectProperty
  * ReflexiveObjectProperty
  * ObjectPropertyDomain
  * ClassAssertion
  * ObjectPropertyAssertion

Class expressions:
  * owl:Thing
  * owl:Nothing
  * ObjectComplementOf (only positive occurrences, see below)
  * ObjectIntersectionOf
  * ObjectUnionOf (only negative occurrences, see below)
  * ObjectSomeValuesFrom
  * ObjectHasValue
  * DataHasValue (preliminary support, see below)

Property expressions:
  * ObjectPropertyChain

Individual expressions:
  * NamedIndividual

Literal expressions:
  * datatype literals in arbitrary datatypes (preliminary support, see below)

The ObjectComplementOf constructor is supported only in positive positions i.e., in the second concept of SubClassOf axioms, provided it does not occur under another ObjectComplementOf. The ObjectUnionOf constructor is supported only in negative positions, i.e., in the first concept of SubClassOf axioms. In these cases the constructors can be expressed using other constructors.

Support for data literals is still preliminary. In particular, the lexical-value mapping of data literals is not supported, so the equality of values in different syntactic forms is not recognized.

## Supported syntactic forms ##

ELK contains a parser for [OWL Functional-Style Syntax](http://www.w3.org/TR/owl2-syntax/), and can read according files from the [command line](ElkCommandLine.md). Ontologies in other formats can be used via the [OWL API bindings](ElkOwlApi.md) or the [Protégé plugin](ElkProtege.md).

## Corresponding description logic expressivity ##

The semantics of OWL is closely related to that of description logics (DLs); for a first introduction, please see the [DL Primer](http://arxiv.org/abs/1201.4089).
The above features correspond to the description logic EL++ restricted to safe nominals (defined in [this paper](http://korrekt.org/page/Practical_Reasoning_with_Nominals_in_the_EL_Family_of_Description_Logics)) with partial support for concrete roles, ObjectComplementOf, and ObjectUnionOf.

Concept constructors:
  * Top concept
  * Bottom concept
  * Negation (ObjectComplementOf, only positive occurrences, see above)
  * Conjunctions (ObjectIntersectionOf)
  * Disjunctions (ObjectUnionOf, only negative occurrences, see above)
  * Existential role restrictions (ObjectSomeValuesFrom)
  * Safe nominals (ObjectHasValue)
  * Existential restrictions on concrete roles with a nominal value (DataHasValue), preliminary support (see above)

Axiom types:
  * General concept inclusions (GCIs)
  * Disjoint concepts
  * Role hierarchies (abstract roles)
  * Role transitivity, role reflexivity, and role chains (abstract roles)
  * Concept assertions and role assertions