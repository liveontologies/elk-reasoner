package org.semanticweb.elk.owl.inferences;

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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.liveontologies.owlapi.proof.util.Delegator;
import org.liveontologies.owlapi.proof.util.ProofNode;
import org.liveontologies.owlapi.proof.util.ProofNodes;
import org.liveontologies.owlapi.proof.util.ProofStep;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.visitors.DummyElkAxiomVisitor;

public class FlattenedElkInferenceSet
		extends DummyElkAxiomVisitor<Collection<? extends ElkInference>>
		implements ElkInferenceSet {

	private final ElkInferenceSet originalInferences_;

	private final ElkInference.Factory inferenceFactory = new ElkInferenceBaseFactory();

	private final Map<ElkAxiom, Collection<? extends ElkInference>> transformed_ = new HashMap<ElkAxiom, Collection<? extends ElkInference>>();

	private List<ElkClassExpression> currentPremises_ = new ArrayList<ElkClassExpression>();

	public FlattenedElkInferenceSet(ElkInferenceSet inferences) {
		this.originalInferences_ = inferences;
	}

	@Override
	public Collection<? extends ElkInference> get(ElkAxiom conclusion) {
		Collection<? extends ElkInference> result = transformed_
				.get(conclusion);
		if (result == null) {
			result = conclusion.accept(this);
			transformed_.put(conclusion, result);
		}
		return result;
	}

	@Override
	protected Collection<? extends ElkInference> defaultVisit(ElkAxiom axiom) {
		return originalInferences_.get(axiom);
	}

	@Override
	public Collection<? extends ElkInference> visit(ElkSubClassOfAxiom axiom) {
		boolean transform = false;
		Collection<? extends ElkInference> original = originalInferences_
				.get(axiom);
		for (ElkInference inf : original) {
			if (inf instanceof ElkClassInclusionHierarchy) {
				transform = true;
				break;
			}
		}
		if (!transform) {
			return original;
		}
		ProofNode<ElkAxiom> node = new ElkProofNode(axiom, originalInferences_);
		node = ProofNodes.eliminateNotDerivableAndCycles(node);
		if (node == null) {
			// not derivable anyway
			return Collections.emptySet();
		}
		List<ElkInference> result = new ArrayList<ElkInference>();
		for (ProofStep<ElkAxiom> step : node.getInferences()) {
			if (!flatten(step)) {
				result.add(getInference(step));
			} else {
				currentPremises_.add(axiom.getSuperClassExpression());
				result.add(inferenceFactory
						.getElkClassInclusionHierarchy(currentPremises_));
				currentPremises_ = new ArrayList<ElkClassExpression>();
			}
		}
		return result;
	}

	ElkInference getInference(ProofStep<?> step) {
		for (;;) {
			if (step instanceof ElkProofStep) {
				return ((ElkProofStep) step).getElkInference();
			}
			// else
			if (step instanceof Delegator<?>) {
				Object delegate = ((Delegator<?>) step).getDelegate();
				if (delegate instanceof ProofStep<?>) {
					step = (ProofStep<?>) delegate;
					continue;
				}
			}
			// else
			return null;
		}
	}

	boolean flatten(ProofStep<ElkAxiom> step) {
		ElkInference inference = getInference(step);
		if (!(inference instanceof ElkClassInclusionHierarchy)) {
			// inference cannot be expanded
			return false;
		}
		List<? extends ElkClassExpression> expressions = ((ElkClassInclusionHierarchy) inference)
				.getExpressions();
		int pos = 0;
		for (ProofNode<ElkAxiom> premise : step.getPremises()) {
			if (!flatten(premise)) {
				// inferences for the premise cannot be flattened
				currentPremises_.add(expressions.get(pos));
			}
			pos++;
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
			if (!flatten(step)) {
				return false;
			}
		}
		return true;
	}

}