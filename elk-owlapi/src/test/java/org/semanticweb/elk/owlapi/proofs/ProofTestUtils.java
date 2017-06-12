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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.liveontologies.owlapi.proof.OWLProver;
import org.liveontologies.puli.InferenceJustifier;
import org.liveontologies.puli.Proof;
import org.liveontologies.puli.Proofs;
import org.liveontologies.puli.justifications.InterruptMonitor;
import org.liveontologies.puli.justifications.MinimalSubsetCollector;
import org.liveontologies.puli.justifications.TopDownRepairComputation;
import org.semanticweb.elk.owl.inferences.TestUtils;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.RemoveAxiom;
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

	public static <C> void provabilityTest(final Proof<C> proof,
			final C conclusion) {
		assertTrue(String.format("Conclusion %s not derivable!", conclusion),
				isDerivable(proof, conclusion));
	}

	public static <C> boolean isDerivable(final Proof<C> proof,
			final C conclusion) {
		return Proofs.isDerivable(proof, conclusion);
	}

	public static void provabilityTest(OWLProver prover, final OWLAxiom axiom) {
		assertTrue(String.format("Entailment %s not derivable!", axiom),
				isDerivable(prover.getProof(axiom), axiom,
						prover.getRootOntology()));
	}

	public static boolean isDerivable(Proof<OWLAxiom> proof,
			OWLAxiom conclusion, OWLOntology ontology) {
		return Proofs.isDerivable(proof, conclusion,
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

	public static void proofCompletenessTest(final OWLProver prover,
			final OWLAxiom conclusion) {
		final OWLOntology ontology = prover.getRootOntology();
		final Proof<OWLAxiom> proof = Proofs.addAssertedInferences(
				prover.getProof(conclusion),
				ontology.getAxioms(Imports.INCLUDED));
		final InferenceJustifier<OWLAxiom, ? extends Set<? extends OWLAxiom>> justifier = Proofs
				.justifyAssertedInferences();
		proofCompletenessTest(prover, conclusion, conclusion, proof, justifier);
	}

	public static <C> void proofCompletenessTest(final OWLProver prover,
			final OWLAxiom entailment, final C conclusion, final Proof<C> proof,
			final InferenceJustifier<C, ? extends Set<? extends OWLAxiom>> justifier) {

		final OWLOntology ontology = prover.getRootOntology();
		final OWLOntologyManager manager = ontology.getOWLOntologyManager();

		// compute repairs

		final Set<Set<? extends OWLAxiom>> repairs = new HashSet<Set<? extends OWLAxiom>>();
		TopDownRepairComputation.<C, OWLAxiom> getFactory()
				.create(proof, justifier, InterruptMonitor.DUMMY)
				.newEnumerator(conclusion)
				.enumerate(new MinimalSubsetCollector<OWLAxiom>(repairs));

		for (final Set<? extends OWLAxiom> repair : repairs) {

			final List<OWLOntologyChange> deletions = new ArrayList<OWLOntologyChange>();
			final List<OWLOntologyChange> additions = new ArrayList<OWLOntologyChange>();
			for (final OWLAxiom axiom : repair) {
				deletions.add(new RemoveAxiom(ontology, axiom));
				additions.add(new AddAxiom(ontology, axiom));
			}

			manager.applyChanges(deletions);

			final boolean conclusionDerived = prover.isEntailed(entailment);

			manager.applyChanges(additions);

			assertFalse("Not all proofs were found!\n" + "Conclusion: "
					+ conclusion + "\n" + "Repair: " + repair,
					conclusionDerived);
		}

	}

}
