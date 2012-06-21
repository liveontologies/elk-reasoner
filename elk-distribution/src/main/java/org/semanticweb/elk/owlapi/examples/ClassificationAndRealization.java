/*
 * #%L
 * ELK Distribution
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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
