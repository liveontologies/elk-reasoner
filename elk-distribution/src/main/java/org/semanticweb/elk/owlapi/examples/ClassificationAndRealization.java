/**
 * 
 */
package org.semanticweb.elk.owlapi.examples;

import java.io.File;

import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

/**
 * Illustrates classification and realization (computing instances for each
 * class) of an ontology
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ClassificationAndRealization {

	/**
	 * @param args
	 * @throws OWLOntologyCreationException
	 */
	public static void main(String[] args) throws OWLOntologyCreationException {
		// Create an ELK reasoner factory.
		OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();

		OWLOntologyManager man = OWLManager.createOWLOntologyManager();
		// Load your ontology.
		OWLOntology ont = man
				.loadOntologyFromOntologyDocument(new File(args[0]));
		// Create an ELK reasoner.
		OWLReasoner reasoner = reasonerFactory.createReasoner(ont);
		// Classify the ontology.
		reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
		// List representative instances for each class.
		for (OWLClass clazz : ont.getClassesInSignature()) {
			for (Node<OWLNamedIndividual> individual : reasoner.getInstances(
					clazz, true)) {
				System.out.println(clazz + "("
						+ individual.getRepresentativeElement() + ")");
			}
			//System.out.println(reasoner.getInstances(clazz, true).getNodes().size());
		}
		
		reasoner.dispose();
	}

}
