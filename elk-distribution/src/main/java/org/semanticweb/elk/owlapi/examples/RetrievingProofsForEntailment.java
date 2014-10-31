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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapitools.proofs.ExplainingOWLReasoner;
import org.semanticweb.owlapitools.proofs.OWLInference;
import org.semanticweb.owlapitools.proofs.exception.ProofGenerationException;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;

/**
 * This examples illustrates how to retrieve proofs for an entailment via the OWL API.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class RetrievingProofsForEntailment {

	/**
	 * @param args
	 * @throws OWLOntologyCreationException
	 * @throws ProofGenerationException 
	 */
	public static void main(String[] args) throws OWLOntologyCreationException, ProofGenerationException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

		// Load your ontology.
		OWLOntology ont = manager.loadOntologyFromOntologyDocument(new File("/home/pavel/ulm/data/galens/EL-GALEN.owl"));
		
		// Create an instance of ELK
		OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();
		ExplainingOWLReasoner reasoner = (ExplainingOWLReasoner) reasonerFactory.createReasoner(ont);
		
		// Pre-compute classification
		reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);

		// Pick the entailment for which we are interested in proofs
		OWLAxiom entailment = getEntailment();
		
		// Get the first derivable expression which corresponds to the entailment. "Derivable" means that it can provide access to inferences which directly derived it. 
		OWLExpression derived = reasoner.getDerivedExpression(entailment);
		
		// Now we can recursively request inferences and their premises. Print them to std.out in this example.
		unwindProofs(derived);

		// Terminate the worker threads used by the reasoner.
		reasoner.dispose();
	}

	private static void unwindProofs(OWLExpression expression) throws ProofGenerationException {
		// Start recursive unwinding
		LinkedList<OWLExpression> toDo = new LinkedList<OWLExpression>();
		Set<OWLExpression> done = new HashSet<OWLExpression>();
		
		toDo.add(expression);
		done.add(expression);
		
		for (;;) {
			OWLExpression next = toDo.poll();
			
			if (next == null) {
				break;
			}
			
			for (OWLInference inf : next.getInferences()) {
				System.out.println(inf);
				// Recursively unwind premise inferences
				for (OWLExpression premise : inf.getPremises()) {
					
					if (done.add(premise)) {
						toDo.addFirst(premise);
					}
				}
				
				// Uncomment if only interested in one inference per derived expression (that is sufficient to reconstruct one proof)
				break;
			}
		}
	}

	private static OWLAxiom getEntailment() {
		// Let's pick some class subsumption we want to explain
		OWLDataFactory factory = OWLManager.getOWLDataFactory();
		
		OWLClass subsumee = factory.getOWLClass(IRI.create("http://www.co-ode.org/ontologies/galen#LiquidFood"));
		OWLClass subsumer = factory.getOWLClass(IRI.create("http://www.co-ode.org/ontologies/galen#Food"));
		
		return factory.getOWLSubClassOfAxiom(subsumee, subsumer);
	}

}
