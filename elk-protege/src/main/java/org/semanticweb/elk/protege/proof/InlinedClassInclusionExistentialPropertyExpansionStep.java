package org.semanticweb.elk.protege.proof;

/*-
 * #%L
 * ELK OWL API Binding
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

import java.util.AbstractList;
import java.util.Collection;
import java.util.List;

import org.liveontologies.proof.util.Inference;
import org.liveontologies.proof.util.ProofNode;
import org.liveontologies.proof.util.ProofStep;
import org.semanticweb.elk.owl.inferences.ElkClassInclusionExistentialComposition;
import org.semanticweb.elk.owl.inferences.ElkClassInclusionExistentialTransitivity;
import org.semanticweb.elk.owl.inferences.ElkPropertyInclusionOfTransitiveObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owlapi.proofs.ElkOwlInference;
import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * An {@link ProofStep} resulting from rewriting nested
 * {@link ElkClassInclusionExistentialComposition} inferences into one
 * {@link ElkClassInclusionExistentialTransitivity} inference.
 * 
 * For example the sequence of inferences
 * 
 * <pre>
 *                             
 *       B ⊑ ∃T.C   C ⊑ ∃T.D   T∘T ⊑ T  
 *       ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯       
 * A ⊑ ∃T.B   B ⊑ ∃T.D   T∘T ⊑ T
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 *         A ⊑ ∃T.D
 * </pre>
 * 
 * with T∘T ⊑ T derived only by
 * {@link ElkPropertyInclusionOfTransitiveObjectProperty}
 * 
 * <pre>
 * TransitiveObjectProperty(T)
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 *         T∘T ⊑ T
 * </pre>
 * 
 * is replaced with one inference
 * 
 * <pre>
 * A ⊑ ∃T.B   B ⊑ ∃T.C   C ⊑ ∃T.D   TransitiveObjectProperty(T)
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 *                   A ⊑ ∃T.D
 * </pre>
 * 
 * The replacement is done provided there is only one inference deriving each
 * premise of the inference and these premises are derived by either a similar
 * {@link ElkClassInclusionExistentialComposition} inference or
 * {@link ElkPropertyInclusionOfTransitiveObjectProperty} (in case of the last
 * premise).
 * 
 * @author Yevgeny Kazakov
 */
public class InlinedClassInclusionExistentialPropertyExpansionStep
		extends AbstractInlinedStep {

	public final static String NAME = ElkClassInclusionExistentialTransitivity.NAME;

	private InlinedClassInclusionExistentialPropertyExpansionStep(
			ProofStep<OWLAxiom> step, ProofNode<OWLAxiom> transitivityNode) {
		super(step);
		addPremise(transitivityNode);
	}

	@Override
	public String getName() {
		return NAME;
	}

	static ProofStep<OWLAxiom> convert(ProofStep<OWLAxiom> step) {
		ProofNode<OWLAxiom> transitivityNode = canConvertStep(step);
		if (transitivityNode == null) {
			return null;
		}
		// else
		return new InlinedClassInclusionExistentialPropertyExpansionStep(step,
				transitivityNode);
	}

	/**
	 * Checks if {@link ProofStep} is derived by
	 * {@link ElkClassInclusionExistentialComposition} inference where the last
	 * premise is derived from
	 * {@link ElkPropertyInclusionOfTransitiveObjectProperty}
	 * 
	 * @param step
	 * @return the transitivity axiom node that is the premise of the inference
	 *         for the last premise
	 */
	static ProofNode<OWLAxiom> canConvertStep(ProofStep<OWLAxiom> step) {
		if (step.getName() != ElkClassInclusionExistentialComposition.NAME) {
			return null;
		}
		List<? extends ProofNode<OWLAxiom>> premises = step.getPremises();
		ProofNode<OWLAxiom> lastPremise = premises.get(premises.size() - 1);
		Collection<? extends ProofStep<OWLAxiom>> lastPremiseSteps = lastPremise
				.getInferences();
		if (lastPremiseSteps.size() != 1) {
			return null;
		}
		// else
		for (ProofStep<OWLAxiom> lastPremiseStep : lastPremiseSteps) {
			if (lastPremiseStep
					.getName() == ElkPropertyInclusionOfTransitiveObjectProperty.NAME) {
				return lastPremiseStep.getPremises().get(0);
			}
		}
		// else
		return null;
	}

	@Override
	void process(ProofStep<OWLAxiom> step) {
		List<? extends ProofNode<OWLAxiom>> premises = step.getPremises();
		for (int i = 0; i < premises.size() - 1; i++) {
			ProofNode<OWLAxiom> premise = premises.get(i);
			if (!process(premise)) {
				// inferences for the premise cannot be flattened
				addPremise(premise);
			}
		}
	}

	private boolean process(ProofNode<OWLAxiom> node) {
		Collection<? extends ProofStep<OWLAxiom>> steps = node.getInferences();
		if (steps.size() > 1) {
			// don't expand multiple inferences
			return false;
		}
		for (ProofStep<OWLAxiom> step : steps) {
			// just one step
			if (canConvertStep(step) != null) {
				process(step);
				return true;
			}
		}
		// else
		return false;
	}

	@Override
	public Inference<OWLAxiom> getInference() {
		return new ElkOwlInference(
				FACTORY.getElkClassInclusionExistentialTransitivity(
						getElkSuperClassExistential(getPremises().get(0))
								.getProperty(),
						new AbstractList<ElkClassExpression>() {

							@Override
							public ElkClassExpression get(int index) {
								switch (index) {
								case 0:
									return getElkSubClassOfAxiom(
											getPremises().get(0))
													.getSubClassExpression();
								default:
									return getElkSuperClassExistential(
											getPremises().get(index - 1))
													.getFiller();
								}
							}

							@Override
							public int size() {
								return getPremises().size();
							}
						}));
	}

	private ElkSubClassOfAxiom getElkSubClassOfAxiom(ProofNode<OWLAxiom> node) {
		return (ElkSubClassOfAxiom) convertNodeMember(node);
	}

	private ElkObjectSomeValuesFrom getElkSuperClassExistential(
			ProofNode<OWLAxiom> node) {
		return (ElkObjectSomeValuesFrom) getElkSubClassOfAxiom(node)
				.getSuperClassExpression();
	}

}
