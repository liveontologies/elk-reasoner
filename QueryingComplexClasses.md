# Querying Complex Class Expressions #

**From version 0.4.0 ELK supports querying of complex (unnamed) class expressions (retrieving subclasses,
superclasses, and instances) through incremental reasoning.** See the [supported reasoning tasks](ReasoningTasks.md) for more detail.

Prior to version 0.4.0 and for more efficient querying involving several complex classes, one can introduce  new named class equivalent to the desired complex class(es) and query for the new named class instead as follows.

# In Protégé #
  * Go to the "Classes" tab. Add a new subclass of Thing. Make the new class equivalent to the desired complex class by clicking on the "+" button next to "Equivalent classes" and typing the complex class in the "Class expression editor" tab. Synchronize the reasoner.

  * One should get the same result by typing the desired complex class as the query in the "DL query" tab and clicking "Add to ontology". Unfortunately, there is a bug that prevents this to work in Protégé 4.1, but it should work in Protégé 4.2 or higher.

# Using OWL API #
```java

```
import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import java.io.File;

public class QueryingUnnamedClassExpressions {

  public static void main(String[] args) throws OWLOntologyCreationException {
    OWLOntologyManager man = OWLManager.createOWLOntologyManager();
    OWLDataFactory dataFactory = man.getOWLDataFactory();

    // Load your ontology.
    OWLOntology ont = man.loadOntologyFromOntologyDocument(new File("c:/ontologies/ontology.owl"));

    // Create an ELK reasoner.
    OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();
    OWLReasoner reasoner = reasonerFactory.createReasoner(ont);

    // Create your desired query class expression. In this example we
    // will query ObjectIntersectionOf(A ObjectSomeValuesFrom(R B)).
    PrefixManager pm = new DefaultPrefixManager("http://example.org/");
    OWLClass A = dataFactory.getOWLClass(":A", pm);
    OWLObjectProperty R = dataFactory.getOWLObjectProperty(":R", pm);
    OWLClass B = dataFactory.getOWLClass(":B", pm);
    OWLClassExpression query = dataFactory.getOWLObjectIntersectionOf(A, dataFactory.getOWLObjectSomeValuesFrom(R, B));

    // Create a fresh name for the query.
    OWLClass newName = dataFactory.getOWLClass(IRI.create("temp001"));
    
    // Make the query equivalent to the fresh class
    OWLAxiom definition = dataFactory.getOWLEquivalentClassesAxiom(newName, query);
    man.addAxiom(ont, definition);

    // Remember to either flush the reasoner after the ontology change
    // or create the reasoner in non-buffering mode. Note that querying
    // a reasoner after an ontology change triggers re-classification of
    // the whole ontology which might be costly. Therefore, if you plan
    // to query for multiple complex class expressions, it will be more
    // efficient to add the corresponding definitions to the ontology at
    // once before asking any queries to the reasoner.
    reasoner.flush();

    // You can now retrieve subclasses, superclasses, and instances of
    // the query class by using its new name instead.
    reasoner.getSubClasses(newName, true);
    reasoner.getSuperClasses(newName, true);
    reasoner.getInstances(newName, false);

    // After you are done with the query, you should remove the definition
    man.removeAxiom(ont, definition);

    // You can now add new definitions for new queries in the same way

    // After you are done with all queries, do not forget to free the
    // resources occupied by the reasoner
    reasoner.dispose();
  }
}
```
```

# Command-Line Client #
  * It is not possible to add new definitions to the ontology using the command line client. The only option in this case is to add the axiom `EquivalentClasses(new_class complex_class)` manually to the input ontology in functional-style syntax.