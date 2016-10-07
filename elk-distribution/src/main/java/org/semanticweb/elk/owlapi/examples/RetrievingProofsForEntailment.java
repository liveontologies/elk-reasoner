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

import org.liveontologies.owlapi.proof.OWLProofNode;
import org.liveontologies.owlapi.proof.OWLProofStep;
import org.liveontologies.owlapi.proof.OWLProver;
import org.semanticweb.elk.owlapi.ElkProverFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;

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
	 */
	public static void main(String[] args) throws OWLOntologyCreationException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

		// Load your ontology.
		OWLOntology ont = manager.loadOntologyFromOntologyDocument(new File("/path/to/your/ontology/ontology.owl"));
		
		// Create an instance of ELK
		ElkProverFactory proverFactory = new ElkProverFactory();
		OWLProver prover = proverFactory.createReasoner(ont);		
		
		// Pre-compute classification
		prover.precomputeInferences(InferenceType.CLASS_HIERARCHY);

		// Pick the entailment for which we are interested in proofs
		OWLAxiom entailment = getEntailment();
		
		// Get the first derivable expression which corresponds to the entailment. "Derivable" means that it can provide access to inferences which directly derived it. 
		OWLProofNode derived = prover.getProof(entailment);
		
		// Now we can recursively request inferences and their premises. Print them to std.out in this example.
		unwindProofs(derived);

		// Terminate the worker threads used by the reasoner.
		prover.dispose();
	}

	private static void unwindProofs(OWLProofNode expression) {
		// Start recursive unwinding
		LinkedList<OWLProofNode> toDo = new LinkedList<OWLProofNode>();
		Set<OWLProofNode> done = new HashSet<OWLProofNode>();
		
		toDo.add(expression);
		done.add(expression);
		
		for (;;) {
			OWLProofNode next = toDo.poll();
			
			if (next == null) {
				break;
			}
			
			for (OWLProofStep inf : next.getInferences()) {
				System.out.println(inf);
				// Recursively unwind premise inferences
				for (OWLProofNode premise : inf.getPremises()) {
					
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
