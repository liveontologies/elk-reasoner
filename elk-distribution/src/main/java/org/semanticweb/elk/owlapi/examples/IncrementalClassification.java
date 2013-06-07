/**
 * 
 */
package org.semanticweb.elk.owlapi.examples;
/*
 * #%L
 * ELK Distribution
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import java.io.File;

import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

/**
 * A small example to demonstrate that incremental reasoning mode is on by
 * default when ELK is used via the OWL API.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class IncrementalClassification {

	public static void main(String[] args) throws OWLOntologyStorageException,
			OWLOntologyCreationException {
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
	}

}
