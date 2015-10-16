# Introduction #

Starting with the version 0.4.0, ELK is capable of incrementally updating the inferred class and instance hierarchies after adding, removing, or modifying axioms on classes or instances. Since most changes are local in the sense that they affect only a small part of the class/instance hierarchy, it is beneficial to not re-compute the (large) parts which are not affected. Incremental reasoning does exactly that which helps to substantially speed up the ontology editing life cycle, that is, "edit - classify - edit - classify ..." workflow. In a sense, ELK behaves similarly to a modern programming IDE which transparently calls a compiler to incrementally compile your code (unfortunately, ontology engineering IDEs aren't there yet).

# Demo #

<a href='http://www.youtube.com/watch?feature=player_embedded&v=AUv7ur_4o-Y' target='_blank'><img src='http://img.youtube.com/vi/AUv7ur_4o-Y/0.jpg' width='425' height=344 /></a>

# Details #

Here is the list of types of axioms which can be changed incrementally:

```
SubClassOf
EquivalentClasses
DisjointClasses
ObjectPropertyDomain
ClassAssertion
ObjectPropertyAssertion
```

In contrast, changes in axioms which can affect the property hierarchy or property chains will still trigger the full re-classification. Such changes are typically non-local so incorporating them incrementally usually doesn't pay off.

```
SubObjectPropertyOf
SubDataPropertyOf
EquivalentObjectProperties
EquivalentDataProperties
TransitiveObjectProperty
ReflexiveObjectProperty
```

## Example ##

Incremental reasoning is enabled by default when using ELK via both the OWL API and the ELK API. Here is a small example:

```java

```
OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

// Load your ontology
OWLOntology ont = manager.loadOntologyFromOntologyDocument(new File("path-to-ontology"));

// Create an ELK reasoner.
OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();
OWLReasoner reasoner = reasonerFactory.createReasoner(ont);

// Classify the ontology.
reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);

OWLDataFactory factory = manager.getOWLDataFactory();
OWLClass subClass = factory.getOWLClass(IRI.create("http://www.co-ode.org/ontologies/galen#AbsoluteShapeState"));
OWLAxiom removed = factory.getOWLSubClassOfAxiom(subClass, factory.getOWLClass(IRI.create("http://www.co-ode.org/ontologies/galen#ShapeState")));

OWLAxiom added = factory.getOWLSubClassOfAxiom(subClass, factory.getOWLClass(IRI.create("http://www.co-ode.org/ontologies/galen#GeneralisedStructure")));
// Remove an existing axiom, add a new axiom
manager.addAxiom(ont, added);
manager.removeAxiom(ont, removed);
// This is a buffering reasoner, so you need to flush the changes
reasoner.flush();

// Re-classify the ontology, the changes should be accommodated
// incrementally (i.e. without re-inferring all subclass relationships)
// You should be able to see it from the log output
reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);		

// Terminate the worker threads used by the reasoner.
reasoner.dispose();
```
```