package org.semanticweb.elk.owl.inferences.rewriting;

/*-
 * #%L
 * ELK Proofs Package
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.liveontologies.owlapi.proof.util.ProofNode;
import org.liveontologies.owlapi.proof.util.ProofStep;
import org.semanticweb.elk.owl.inferences.ElkClassInclusionExistentialPropertyExpansion;
import org.semanticweb.elk.owl.inferences.ElkClassInclusionExistentialTransitivity;
import org.semanticweb.elk.owl.inferences.ElkInference;
import org.semanticweb.elk.owl.inferences.ElkInferenceProducer;
import org.semanticweb.elk.owl.inferences.ElkPropertyInclusionOfTransitiveObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;

/**
 * A transformation that rewrites nested
 * {@link ElkClassInclusionExistentialPropertyExpansion} inferences into one
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
 * is replaced with one inference
 * 
 * <pre>
 * A ⊑ ∃T.B   B ⊑ ∃T.C   C ⊑ ∃T.D   TransitiveObjectProperty(T)
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 *                   A ⊑ ∃T.D
 * </pre>
 * 
 * The replacement is done provided there is only one inference deriving the
 * each premise of the inference and these premises are derived by either
 * similar {@link ElkClassInclusionExistentialPropertyExpansion} inference or
 * {@link ElkPropertyInclusionOfTransitiveObjectProperty} (if it is the last
 * premise).
 * 
 * @author Yevgeny Kazakov
 */
class ElkClassInclusionExistentialPropertyExpansionFlattener
		implements ElkInferenceFlattener {

	/**
	 * the inference to be rewritten
	 */
	private final ElkClassInclusionExistentialPropertyExpansion inference_;

	/**
	 * the factory for creating rewritten inferences
	 */
	private final ElkInference.Factory factory_;

	/**
	 * the object using which the new inferences are produced
	 */
	private final ElkInferenceProducer producer_;

	/**
	 * the property in the existential restriction of the conclusion of
	 * {@link #inference_}; it must be transitive for rewriting to succeed
	 */
	private final ElkObjectPropertyExpression transitiveProperty_;

	/**
	 * a temporary list to accumulate the parameters of the resulting
	 * {@link ElkClassInclusionExistentialTransitivity} inference
	 */
	private List<ElkClassExpression> classExpressions_;

	ElkClassInclusionExistentialPropertyExpansionFlattener(
			ElkClassInclusionExistentialPropertyExpansion inference,
			ElkInference.Factory factory, ElkInferenceProducer producer) {
		this.inference_ = inference;
		this.factory_ = factory;
		this.producer_ = producer;
		this.transitiveProperty_ = inference.getSuperProperty();
	}

	@Override
	public void flatten(ProofStep<ElkAxiom> step) {
		classExpressions_ = new ArrayList<ElkClassExpression>();
		if (!flatten(inference_, step)) {
			producer_.produce(inference_);
			return;
		}
		List<? extends ElkClassExpression> expressions = inference_
				.getClassExpressions();
		classExpressions_.add(expressions.get(expressions.size() - 1));
		producer_.produce(factory_.getElkClassInclusionExistentialTransitivity(
				classExpressions_, transitiveProperty_));
	}

	/**
	 * Perform the transformation for the inference using the proof step to
	 * which it corresponds
	 * 
	 * @param inf
	 * @param step
	 * @return {@code true} if the inference can be transformed and
	 *         {@code false} otherwise
	 */
	boolean flatten(ElkClassInclusionExistentialPropertyExpansion inf,
			ProofStep<ElkAxiom> step) {
		// check if the last premise is only derived from the transitivity axiom
		List<? extends ProofNode<ElkAxiom>> premises = step.getPremises();
		ProofNode<ElkAxiom> lastPremise = premises.get(premises.size() - 1);
		Collection<? extends ProofStep<ElkAxiom>> lastPremiseSteps = lastPremise
				.getInferences();
		if (lastPremiseSteps.size() != 1) {
			return false;
		}
		for (ProofStep<ElkAxiom> lastPremiseStep : lastPremiseSteps) {
			ElkInference lastPremiseInference = FlattenedElkInferenceSet
					.getInference(lastPremiseStep);
			if (!(lastPremiseInference instanceof ElkPropertyInclusionOfTransitiveObjectProperty)) {
				return false;
			}
		}
		List<? extends ElkClassExpression> expressions = inf
				.getClassExpressions();
		for (int i = 0; i < premises.size() - 1; i++) {
			if (!flatten(premises.get(i))) {
				// inferences for the premise cannot be flattened
				classExpressions_.add(expressions.get(i));
			}
		}
		return true;
	}

	boolean flatten(ProofNode<ElkAxiom> node) {
		Collection<? extends ProofStep<ElkAxiom>> steps = node.getInferences();
		if (steps.size() > 1) {
			// don't expand multiple inferences
			return false;
		}
		for (ProofStep<ElkAxiom> step : steps) {
			ElkInference inf = FlattenedElkInferenceSet.getInference(step);
			if (inf instanceof ElkClassInclusionExistentialPropertyExpansion
					&& flatten(
							(ElkClassInclusionExistentialPropertyExpansion) inf,
							step)) {
				return true;
			}
		}
		// else
		return false;
	}

}
