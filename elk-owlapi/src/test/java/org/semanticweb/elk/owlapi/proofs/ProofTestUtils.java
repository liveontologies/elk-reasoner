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
package org.semanticweb.elk.owlapi.proofs;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import org.liveontologies.owlapi.proof.OWLProver;
import org.liveontologies.owlapi.proof.util.ProofNodes;
import org.liveontologies.owlapi.proof.util.ProofNode;
import org.liveontologies.owlapi.proof.util.ProofStep;
import org.semanticweb.elk.proofs.utils.TestUtils;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.parameters.AxiomAnnotations;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

/**
 * TODO this is adapted from {@link TestUtils}, see if we can get rid of
 * copy-paste.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author Peter Skocovsky
 * 
 * @author Yevgeny Kazakov
 * 
 */
public class ProofTestUtils {

	public static void provabilityTest(OWLProver prover,
			OWLSubClassOfAxiom axiom) {
		assertTrue(String.format("Entailment %s not derivable!", axiom),
				ProofNodes.isDerivable(prover.getProof(axiom),
						prover.getRootOntology().getAxioms(Imports.INCLUDED)));
	}

	public static void visitAllSubsumptionsForProofTests(
			final OWLReasoner reasoner, final OWLDataFactory factory,
			final ProofTestVisitor visitor) {

		if (!reasoner.isConsistent()) {
			visitor.visit(factory.getOWLThing(), factory.getOWLNothing());
			return;
		}

		Set<Node<OWLClass>> visited = new HashSet<Node<OWLClass>>();
		Queue<Node<OWLClass>> toDo = new LinkedList<Node<OWLClass>>();

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

	public static Set<OWLAxiom> collectProofBreaker(
			final ProofNode<OWLAxiom> conclusion, final OWLOntology ontology,
			final Random random) {
		final Set<ProofNode<OWLAxiom>> visited = new HashSet<ProofNode<OWLAxiom>>();
		final Set<ProofNode<OWLAxiom>> tautologies = new HashSet<ProofNode<OWLAxiom>>();
		return collectProofBreaker(conclusion, visited, tautologies, ontology,
				random);
	}

	public static Set<OWLAxiom> collectProofBreaker(
			final ProofNode<OWLAxiom> conclusion,
			final Set<ProofNode<OWLAxiom>> visited,
			final Set<ProofNode<OWLAxiom>> tautologies, final OWLOntology ontology,
			final Random random) {

		/*
		 * If the expressions in visited are not provable and the result of this
		 * method is removed from the ontology, conclusion is not provable.
		 * TODO: Except if some expression is a tautology !!!
		 */

		final Set<OWLAxiom> collected = new HashSet<OWLAxiom>();

		if (!visited.add(conclusion)) {
			return collected;
		}

		// If conclusion is asserted, it must be collected.
		final OWLAxiom ax = conclusion.getMember();
		if (ontology.containsAxiom(ax, Imports.INCLUDED,
				AxiomAnnotations.IGNORE_AXIOM_ANNOTATIONS)) {
			collected.add(ax);
		}

		// For all inferences break proofs of one of their premises.
		for (final ProofStep<OWLAxiom> inf : conclusion.getInferences()) {

			final List<ProofNode<OWLAxiom>> premises = new ArrayList<ProofNode<OWLAxiom>>(
					inf.getPremises());
			Collections.shuffle(premises, random);

			boolean inferenceIsBroken = false;
			for (final ProofNode<OWLAxiom> premise : premises) {

				final Set<OWLAxiom> premiseBreaker = collectProofBreaker(
						premise, visited, tautologies, ontology, random);

				if (tautologies.contains(premise)) {
					// The premise is a tautology and we need to break a
					// different premise!
				} else if (premiseBreaker.isEmpty()) {
					// The premise was already visited, so this inference is
					// already broken.
					inferenceIsBroken = true;
					break;
				} else {
					collected.addAll(premiseBreaker);
					break;
				}

			}
			if (!inferenceIsBroken && collected.isEmpty()) {
				/*
				 * None of the premises of this inference is already broken and
				 * no axioms were collected, so conclusion is a tautology.
				 * 
				 * TODO: This is not complete, because some tautology may be
				 * asserted!
				 */
				tautologies.add(conclusion);
				return Collections.emptySet();
			}

		}

		return collected;
	}

}
