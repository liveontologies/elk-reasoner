# Introduction #

So far the testing infrastructure of ELK provides some initial support for testing correctness of the classification procedure. It goes through the collection of test EL ontologies, loads each of them into the reasoner, invokes classification (resp. realization), and then verifies that the actual class (resp. instance) taxonomy corresponds to the expected taxonomy created by the user. The taxonomy comparison is currently based on hash codes but will be eventually extended to a more appropriate taxonomy diff procedure.

The same tests are run through different interfaces that ELK supports, namely, the OWL API and CLI.


# Classification Test Details #

The infrastructure takes care of running all tests. Developers are responsible for providing test data and the expected results. To add a classification test, do the following:

  * Put your ontology in a file (in the [OWL 2 Functional Syntax](http://www.w3.org/TR/owl2-syntax/)) and save it in _/elk-reasoner/src/test/resources/classification\_test\_input_
  * Prepare the expected taxonomy for the test. You may create it manually or use another reasoner. The taxonomy file should be named {test\_ontology\_name}.expected and should be saved in the same directory as the test ontology. See below for the allowed OWL 2 constructs to represent a taxonomy.

Then you can invoke the tests via the OWL API or CLI by running

_/elk-owlapi/src/test/java/org/semanticweb/elk/owlapi/OWLAPIDiffClassificationCorrectnessTest_ (in elk-owl-api)

or

_/elk-cli/src/test/java/org/semanticweb/elk/cli/CLIDiffClassificationCorrectnessTest_ (in elk-cli).

The tests will be executed by Maven if you install either of these modules. They also should work from your IDE.

# Taxonomy Representation in OWL 2 Functional-Style Syntax #

The following OWL constructs are allowed to represent taxonomies: class declarations, equivalent class axioms to represent taxonomy nodes with more than one member, and subsumptions to represent parent/child relationships between nodes. Obviously, all node members must be named classes (complex class expressions aren't allowed).

You don't have to declare **all** classes but it never hurts to do so. Declaration is mandatory for those classes which don't occur in logical axioms, e.g., are only subclasses of owl:Thing and have no subclasses in the taxonomy.

A small example:

```xml

```
Ontology(
Declaration(Class(:A))
Declaration(Class(:D))
Declaration(Class(:B))
Declaration(Class(:C))
Declaration(Class(:E))
EquivalentClasses(:B :C)
SubClassOf(:A :D)
SubClassOf(:A :E)
)
```
```

More examples can be found in:

_/elk-reasoner/src/test/resources/classification\_test\_input_

# ABox Realization Test Details #

ABox reasoning tests work the basically same way **except** that loading of instance taxonomies isn't yet supported. As such, instead of providing the expected taxonomy, you should prepare the hash code of the expected taxonomy and put it in the file {test\_ontology\_name}.expected.hash. The easiest way to do so is to use a tiny utility class _/elk-cli/src/test/java/org/semanticweb/elk/cli/util/ComputeTaxonomyHashCodes_ which assumes that single-threaded ELK is correct and computes expected hash codes for all test ontologies and generate .expected.hash files.

The location of all ABox test files and expected hash codes is:

_/elk-reasoner/src/test/resources/realization\_test\_input_

Tests are invoked by running

_/elk-owlapi/src/test/java/org/semanticweb/elk/owlapi/OWLAPIHashRealizationCorrectnessTest_ (in elk-owl-api)

or

_/elk-cli/src/test/java/org/semanticweb/elk/cli/CLIHashRealizationCorrectnessTest_ (in elk-cli).


# Future Improvements #

Listed in the order of importance:

  * Implement loading of instance taxonomies for ABox tests
  * Provide a better report, i.e., a taxonomy diff-based one, in case of the actual taxonomy mismatching the expected taxonomy
  * Add other reasoning tests

Any questions, please contact pavel.klinov@gmail.com