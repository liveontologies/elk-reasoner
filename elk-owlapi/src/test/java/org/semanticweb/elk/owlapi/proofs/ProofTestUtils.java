/**
 * 
 */
package org.semanticweb.elk.owlapi.proofs;
/*
 * #%L
 * ELK Proofs Package
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.semanticweb.elk.proofs.utils.TestUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;
import org.semanticweb.owlapitools.proofs.ExplainingOWLReasoner;
import org.semanticweb.owlapitools.proofs.OWLInference;
import org.semanticweb.owlapitools.proofs.exception.ProofGenerationException;
import org.semanticweb.owlapitools.proofs.expressions.OWLAxiomExpression;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpressionVisitor;
import org.semanticweb.owlapitools.proofs.expressions.OWLLemmaExpression;

/**
 * TODO this is adapted from {@link TestUtils}, see if we can get rid of copy-paste.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ProofTestUtils {

	// tests that each derived expression is provable. an expression is provable
	// if either it doesn't require a proof (i.e. is a tautology or asserted) or
	// returns at least one inference such that each of the premises is
	// provable.
	public static boolean provabilityTest(ExplainingOWLReasoner reasoner, OWLSubClassOfAxiom axiom) throws ProofGenerationException {
		OWLExpression next = reasoner.getDerivedExpression(axiom);

		return proved(next, new HashSet<OWLExpression>(Arrays.asList(next)));
	}

	private static boolean proved(OWLExpression expr, HashSet<OWLExpression> seen) throws ProofGenerationException {
		// check if the expression doesn't require a proof
		if (isAsserted(expr)) {
			return true;
		}

		for (OWLInference inf : expr.getInferences()) {
			// see if this inference proves the expression
			boolean proves = true;
			boolean newPremise = false;
			
			if (inf.getConclusion().equals(expr) && isAsserted(inf.getConclusion())) {
				return true;
			}

			for (OWLExpression premise : inf.getPremises()) {
				if (seen.add(premise)) {
					newPremise = true;
					proves &= proved(premise, seen);
				}
			}

			if (proves && newPremise) {
				return true;
			}
		}

		return false;
	}

	private static boolean isAsserted(OWLExpression expr) {
		return expr.accept(new OWLExpressionVisitor<Boolean>() {

			@Override
			public Boolean visit(OWLAxiomExpression expr) {
				return expr.isAsserted();
			}

			@Override
			public Boolean visit(OWLLemmaExpression arg0) {
				return false;
			}
			
		});
	}
	
	public static OWLOntology getInferredTaxonomy(OWLReasoner reasoner) throws OWLOntologyCreationException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		
		reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
		
		List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();
        gens.add(new InferredSubClassAxiomGenerator());
        gens.add(new InferredEquivalentClassAxiomGenerator());
        // put the inferred axioms into a fresh empty ontology.
        OWLOntology infOnt = manager.createOntology();
        InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner, gens);
        
        iog.fillOntology(manager, infOnt);

        return infOnt;
	}

	// checks that each axiom premise is in fact an axiom in the source ontology
	// raises an assertion error if it's not the case
	public static OWLInferenceVisitor getAxiomBindingChecker(final OWLOntology ontology) {
		return new OWLInferenceVisitor() {
			
			@Override
			public void visit(OWLInference inference) {
				for (OWLExpression premise : inference.getPremises()) {
					// all asserted premises must be present in the ontology
					if (premise instanceof OWLAxiomExpression) {
						OWLAxiomExpression expr = (OWLAxiomExpression) premise;
						
						assertTrue("Asserted premise is not found in the ontology", !expr.isAsserted() || isAsserted(ontology, expr.getAxiom()));
					}
				}
				
			}

			private boolean isAsserted(OWLOntology ontology, OWLAxiom axiom) {
				for (OWLAxiom ax : ontology.getAxioms()) {
					if (ax == axiom) {
						return true;
					}
				}
				
				return false;
			}
		};
	}
	
	public static <T extends Exception> void visitAllSubsumptionsForProofTests(OWLReasoner reasoner, ProofTestVisitor<T> visitor) throws T {
		Set<Node<OWLClass>> visited = new HashSet<Node<OWLClass>>();
		Queue<Node<OWLClass>> toDo = new LinkedList<Node<OWLClass>>();
		OWLDataFactory factory = OWLManager.getOWLDataFactory();
		
		toDo.add(reasoner.getTopClassNode());
		visited.add(reasoner.getTopClassNode());
		
		for (;;) {
			Node<OWLClass> nextNode = toDo.poll();
			
			if (nextNode == null) {
				break;
			}
			// first visit all subsumptions within the node
			List<OWLClass> eqClassList = new ArrayList<OWLClass>(nextNode.getEntities());
			
			for (int i = 0; i < eqClassList.size() - 1; i++) {
				for (int j = i + 1; j < eqClassList.size(); j++) {
					OWLClass sub = eqClassList.get(i);
					OWLClass sup = eqClassList.get(j);
					
					if (!sub.equals(sup)) {
						if (!sup.equals(factory.getOWLThing()) && !sub.equals(factory.getOWLNothing())) {
							visitor.visit(sub, sup);
						}
						
						if (!sub.equals(factory.getOWLThing()) && !sup.equals(factory.getOWLNothing())) {
							visitor.visit(sup, sub);
						}
					}
				}
			}
			// go one level down
			for (Node<OWLClass> subNode : reasoner.getSubClasses(nextNode.getRepresentativeElement(), true)) {
				if (visited.add(subNode)) {
					toDo.add(subNode);	
				}
			}
			
			for (OWLClass sup : nextNode.getEntities()) {
				for (Node<OWLClass> subNode : reasoner.getSubClasses(sup, true)) {
					for (OWLClass sub : subNode.getEntitiesMinusBottom()) {
						if (!sup.equals(factory.getOWLThing())) {
							visitor.visit(sub, sup);
						}
					}
				}
			}
		}
	}
	
}
