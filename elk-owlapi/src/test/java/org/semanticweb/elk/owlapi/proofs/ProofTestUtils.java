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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import org.liveontologies.owlapi.proof.OWLProver;
import org.liveontologies.proof.util.InferenceSet;
import org.liveontologies.proof.util.InferenceSets;
import org.liveontologies.proof.util.ProofNode;
import org.liveontologies.proof.util.ProofNodes;
import org.liveontologies.proof.util.ProofStep;
import org.semanticweb.elk.owl.inferences.TestUtils;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.RemoveAxiom;
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

	public static void provabilityTest(OWLProver prover, final OWLAxiom axiom) {
		assertTrue(String.format("Entailment %s not derivable!", axiom),
				isDerivable(prover.getProof(axiom), axiom,
						prover.getRootOntology()));
	}

	public static boolean isDerivable(InferenceSet<OWLAxiom> proof,
			OWLAxiom conclusion, OWLOntology ontology) {
		return InferenceSets.isDerivable(proof, conclusion,
				ontology.getAxioms(Imports.INCLUDED));
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

	public static void randomProofCompletenessTest(final OWLProver prover,
			final OWLAxiom conclusion, final Random random, final long seed) {
		final ProofNode<OWLAxiom> root = ProofNodes
				.create(prover.getProof(conclusion), conclusion);

		final OWLOntology ontology = prover.getRootOntology();
		final OWLOntologyManager manager = ontology.getOWLOntologyManager();

		final Set<OWLAxiom> proofBreaker = ProofTestUtils
				.collectProofBreaker(root, ontology, random);
		/*
		 * If proofBreaker is null, the conclusion cannot be broken and nothing
		 * can be tested!
		 */
		assumeNotNull(proofBreaker);
		// else
		final List<OWLOntologyChange> deletions = new ArrayList<OWLOntologyChange>();
		final List<OWLOntologyChange> additions = new ArrayList<OWLOntologyChange>();
		for (final OWLAxiom axiom : proofBreaker) {
			deletions.add(new RemoveAxiom(ontology, axiom));
			additions.add(new AddAxiom(ontology, axiom));
		}

		manager.applyChanges(deletions);

		final boolean conclusionDerived = prover.isEntailed(conclusion);

		manager.applyChanges(additions);

		assertFalse("Not all proofs were found!\n" + "Seed: " + seed + "\n"
				+ "Conclusion: " + conclusion + "\n" + "Proof Breaker: "
				+ proofBreaker, conclusionDerived);
	}

	public static Set<OWLAxiom> collectProofBreaker(
			final ProofNode<OWLAxiom> conclusion, final OWLOntology ontology,
			final Random random) {
		final Set<ProofNode<OWLAxiom>> visited = new HashSet<ProofNode<OWLAxiom>>();
		final Set<ProofNode<OWLAxiom>> tautologies = new HashSet<ProofNode<OWLAxiom>>();
		return collectProofBreaker(conclusion, visited, tautologies,
				new HashSet<ProofNode<OWLAxiom>>(), ontology, random);
	}

	public static Set<OWLAxiom> collectProofBreaker(
			final ProofNode<OWLAxiom> conclusion,
			final Set<ProofNode<OWLAxiom>> visited,
			final Set<ProofNode<OWLAxiom>> tautologies,
			final Set<ProofNode<OWLAxiom>> currentBranch,
			final OWLOntology ontology, final Random random) {
		/*
		 * If the expressions in visited are not provable or tautologies, and
		 * the result of this method is not null and removed from the ontology,
		 * then conclusion is not provable.
		 */

		if (currentBranch.contains(conclusion)) {
			/*
			 * Cycle detected. Cannot make sure that the conclusion is not a
			 * tautology, so assume that it is.
			 */
			return null;
		}

		if (tautologies.contains(conclusion)) {
			// We've already found out that the conclusion is a tautology.
			return null;
		}
		final Set<OWLAxiom> collected = new HashSet<OWLAxiom>();
		if (!visited.add(conclusion)) {
			// If already visited, its proof breaker was already collected.
			return collected;
		}

		// If conclusion is asserted, it must be collected.
		final OWLAxiom ax = conclusion.getMember();
		if (ontology.containsAxiom(ax, Imports.INCLUDED,
				AxiomAnnotations.IGNORE_AXIOM_ANNOTATIONS)) {
			collected.add(ax);
		}

		/*
		 * Even if the conclusion is an axiom, it may still be derived from
		 * other axioms. So we still need to break its proof.
		 */

		// For all inferences break proofs of one of their premises.
		final Set<ProofNode<OWLAxiom>> newBranch = new HashSet<ProofNode<OWLAxiom>>(
				currentBranch);
		newBranch.add(conclusion);
		for (final ProofStep<OWLAxiom> inf : conclusion.getInferences()) {

			final List<ProofNode<OWLAxiom>> premises = new ArrayList<ProofNode<OWLAxiom>>(
					inf.getPremises());
			Collections.shuffle(premises, random);

			boolean isSomePremiseBroken = false;
			for (final ProofNode<OWLAxiom> premise : premises) {

				final Set<OWLAxiom> premiseBreaker = collectProofBreaker(
						premise, visited, tautologies, newBranch, ontology,
						random);

				if (premiseBreaker == null) {
					/*
					 * The premise may be a tautology and we need to break a
					 * different premise!
					 */
				} else {
					collected.addAll(premiseBreaker);
					isSomePremiseBroken = true;
					break;
				}

			}
			if (!isSomePremiseBroken) {
				/*
				 * There is an inference whose all premises may be tautologies,
				 * hence also the conclusion may be a tautology.
				 */
				tautologies.add(conclusion);
				return null;
			}

		}

		/*
		 * We are sure that no inference has all premises that are tautologies,
		 * hence collected contains a proof breaker of conclusion.
		 */
		return collected;
	}

}
