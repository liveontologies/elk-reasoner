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
import java.util.Collections;
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
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapitools.proofs.ExplainingOWLReasoner;
import org.semanticweb.owlapitools.proofs.OWLInference;
import org.semanticweb.owlapitools.proofs.exception.ProofGenerationException;
import org.semanticweb.owlapitools.proofs.expressions.OWLAxiomExpression;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;
import org.semanticweb.owlapitools.proofs.util.OWLInferenceGraph;
import org.semanticweb.owlapitools.proofs.util.OWLProofUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO this is adapted from {@link TestUtils}, see if we can get rid of copy-paste.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ProofTestUtils {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ProofTestUtils.class);

	public static void provabilityTest(ExplainingOWLReasoner reasoner,
			OWLSubClassOfAxiom axiom) throws ProofGenerationException {
		OWLExpression root = reasoner.getDerivedExpression(axiom);
		OWLInferenceGraph graph = OWLProofUtils.computeInferenceGraph(root);

		provabilityTest(graph, graph.getExpressions());
	}

	public static void provabilityOfInconsistencyTest(
			ExplainingOWLReasoner reasoner) throws ProofGenerationException {
		OWLExpression root = reasoner.getDerivedExpressionForInconsistency();
		OWLInferenceGraph graph = OWLProofUtils.computeInferenceGraph(root);

		provabilityTest(graph, Collections.singleton(root));
	}

	// tests that each derived expression is provable
	static void provabilityTest(OWLInferenceGraph graph,
			Iterable<OWLExpression> expressions)
			throws ProofGenerationException {
		Set<OWLExpression> proved = new HashSet<OWLExpression>(
				graph.getExpressions().size());
		Queue<OWLExpression> toDo = new LinkedList<OWLExpression>(
				graph.getRootExpressions());

		for (;;) {
			OWLExpression next = toDo.poll();

			if (next == null) {
				break;
			}			

			if (proved.add(next)) {
				LOGGER_.trace("{}: proved", next);
				for (OWLInference inf : graph.getInferencesForPremise(next)) {
					if (proved.containsAll(inf.getPremises())) {
						LOGGER_.trace("{}: inference", inf);
						toDo.add(inf.getConclusion());
					}
				}
			}
		}

		for (OWLExpression expr : expressions) {
			if (!proved.contains(expr) && !OWLProofUtils.isAsserted(expr)) {
				throw new AssertionError(
						String.format("There is no acyclic proof of %s", expr));
			}
		}
	}

	// checks that each axiom premise is in fact an axiom in the source ontology
	// raises an assertion error if it's not the case
	public static OWLInferenceVisitor getAxiomBindingChecker(
			final OWLOntology ontology) {
		return new OWLInferenceVisitor() {

			@Override
			public void visit(OWLInference inference) {
				for (OWLExpression premise : inference.getPremises()) {
					// all asserted premises must be present in the ontology
					if (premise instanceof OWLAxiomExpression) {
						OWLAxiomExpression expr = (OWLAxiomExpression) premise;

						assertTrue(
								"Asserted premise is not found in the ontology: "
										+ expr.getAxiom(),
								!expr.isAsserted() || isAsserted(ontology,
										expr.getAxiom()));
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

	public static <T extends Exception> void visitAllSubsumptionsForProofTests(
			OWLReasoner reasoner, ProofTestVisitor<T> visitor) throws T {
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

			List<OWLClass> membersList = new ArrayList<OWLClass>(
					nextNode.getEntities());

			if (nextNode.isBottomNode()) {
				// do not check inconsistent concepts for now
				continue;
			}

			// else visit all subsumptions within the node

			for (int i = 0; i < membersList.size() - 1; i++) {
				for (int j = i + 1; j < membersList.size(); j++) {
					OWLClass sub = membersList.get(i);
					OWLClass sup = membersList.get(j);

					if (!sub.equals(sup)) {
						if (!sup.equals(factory.getOWLThing())
								&& !sub.equals(factory.getOWLNothing())) {
							visitor.visit(sub, sup);
						}

						if (!sub.equals(factory.getOWLThing())
								&& !sup.equals(factory.getOWLNothing())) {
							visitor.visit(sup, sub);
						}
					}
				}
			}
			// go one level down
			for (Node<OWLClass> subNode : reasoner
					.getSubClasses(nextNode.getRepresentativeElement(), true)) {
				if (visited.add(subNode)) {
					toDo.add(subNode);
				}
			}

			for (OWLClass sup : nextNode.getEntities()) {
				for (Node<OWLClass> subNode : reasoner.getSubClasses(sup,
						true)) {
					if (subNode.isBottomNode())
						continue;
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
