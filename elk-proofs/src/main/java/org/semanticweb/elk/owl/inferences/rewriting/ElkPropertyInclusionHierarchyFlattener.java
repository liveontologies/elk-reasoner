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
import org.semanticweb.elk.owl.inferences.ElkInference;
import org.semanticweb.elk.owl.inferences.ElkInferenceProducer;
import org.semanticweb.elk.owl.inferences.ElkPropertyInclusionHierarchy;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;

/**
 * A transformation that rewrites nested {@link ElkPropertyInclusionHierarchy}
 * inferences into one {@link ElkPropertyInclusionHierarchy} inference.
 * 
 * For example the sequence of inferences
 * 
 * <pre>
 *       S ⊑ H    H ⊑ T
 *       ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 * R ⊑ S     S ⊑ T
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 *       R ⊑ T
 * </pre>
 *
 * is replaced with one inference
 * 
 * <pre>
 * R ⊑ S   S ⊑ H   H ⊑ T
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 *         R ⊑ T
 * </pre>
 * 
 * The replacement is done provided there is only one inference deriving each
 * premise of the inference and it is {@link ElkPropertyInclusionHierarchy}.
 * 
 * @author Yevgeny Kazakov
 */
class ElkPropertyInclusionHierarchyFlattener implements ElkInferenceFlattener {

	/**
	 * the inference to be rewritten
	 */
	private final ElkPropertyInclusionHierarchy inference_;

	/**
	 * the factory for creating rewritten inferences
	 */
	private final ElkInference.Factory factory_;

	/**
	 * the object using which the new inferences are produced
	 */
	private final ElkInferenceProducer producer_;

	/**
	 * a temporary list to accumulate the parameters of the resulting
	 * {@link ElkPropertyInclusionHierarchy} inference
	 */
	private List<ElkObjectPropertyExpression> propertyExpressions_;

	ElkPropertyInclusionHierarchyFlattener(
			ElkPropertyInclusionHierarchy inference,
			ElkInference.Factory factory, ElkInferenceProducer producer) {
		this.inference_ = inference;
		this.factory_ = factory;
		this.producer_ = producer;
	}

	@Override
	public void flatten(ProofStep<ElkAxiom> step) {
		propertyExpressions_ = new ArrayList<ElkObjectPropertyExpression>();
		flatten(inference_, step);
		producer_.produce(factory_.getElkPropertyInclusionHierarchy(
				inference_.getSubExpression(), propertyExpressions_));
	}

	/**
	 * Perform the transformation for the inference using the proof step to
	 * which it corresponds
	 * 
	 * @param inf
	 * @param step
	 */
	void flatten(ElkPropertyInclusionHierarchy inf, ProofStep<ElkAxiom> step) {
		List<? extends ElkObjectPropertyExpression> expressions = inf
				.getExpressions();
		List<? extends ProofNode<ElkAxiom>> premises = step.getPremises();
		for (int i = 0; i < premises.size(); i++) {
			if (!flatten(premises.get(i))) {
				// inferences for the premise cannot be flattened
				propertyExpressions_.add(expressions.get(i));
			}
		}
	}

	boolean flatten(ProofNode<ElkAxiom> node) {
		Collection<? extends ProofStep<ElkAxiom>> steps = node.getInferences();
		if (steps.size() > 1) {
			// don't expand multiple inferences
			return false;
		}
		for (ProofStep<ElkAxiom> step : steps) {
			ElkInference inf = FlattenedElkInferenceSet.getInference(step);
			if (inf instanceof ElkPropertyInclusionHierarchy) {
				flatten((ElkPropertyInclusionHierarchy) inf, step);
				return true;
			}
		}
		// else
		return false;
	}

}
