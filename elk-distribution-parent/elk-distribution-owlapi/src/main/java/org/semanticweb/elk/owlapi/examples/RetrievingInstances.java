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
 * This examples illustrates how to retrieve instances for each
 * class in an ontology.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class RetrievingInstances {

	public static void main(String[] args) throws OWLOntologyCreationException {
		OWLOntologyManager man = OWLManager.createOWLOntologyManager();

		// Load your ontology.
		OWLOntology ont = man
				.loadOntologyFromOntologyDocument(new File(args[0]));
		
		// Create an ELK reasoner.
		OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();
		OWLReasoner reasoner = reasonerFactory.createReasoner(ont);
		
		// Precompute instances for each named class in the ontology
		reasoner.precomputeInferences(InferenceType.CLASS_ASSERTIONS);

		// List representative instances for each class.
		for (OWLClass clazz : ont.getClassesInSignature()) {
			for (Node<OWLNamedIndividual> individual : reasoner.getInstances(
					clazz, true)) {
				System.out.println(clazz + "("
						+ individual.getRepresentativeElement() + ")");
			}
		}

		// Terminate the worker threads used by the reasoner.
		reasoner.dispose();
	}

}
