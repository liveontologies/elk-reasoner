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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.liveontologies.owlapi.proof.util.Delegator;
import org.liveontologies.owlapi.proof.util.ProofNode;
import org.liveontologies.owlapi.proof.util.ProofNodes;
import org.liveontologies.owlapi.proof.util.ProofStep;
import org.semanticweb.elk.owl.inferences.ElkClassInclusionExistentialPropertyExpansion;
import org.semanticweb.elk.owl.inferences.ElkClassInclusionHierarchy;
import org.semanticweb.elk.owl.inferences.ElkInference;
import org.semanticweb.elk.owl.inferences.ElkInferenceBaseFactory;
import org.semanticweb.elk.owl.inferences.ElkInferenceDummyVisitor;
import org.semanticweb.elk.owl.inferences.ElkInferenceProducer;
import org.semanticweb.elk.owl.inferences.ElkInferenceSet;
import org.semanticweb.elk.owl.inferences.ElkPropertyInclusionHierarchy;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An {@link ElkInferenceSet} that provides a "flat" view of the given
 * {@link ElkInferenceSet}. In the new set, certain combinations of inferences
 * are replaced by one inference. This process is performed incrementally upon
 * requesting of inferences for conclusions using the {@link #get(ElkAxiom)}
 * method.
 * 
 * @author Yevgeny Kazakov
 *
 */
public class FlattenedElkInferenceSet
		implements ElkInferenceSet, ElkInferenceSet.ChangeListener {

	// logger for this class
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(FlattenedElkInferenceSet.class);

	/**
	 * the inferences being transformed
	 */
	private final ElkInferenceSet originalInferences_;

	/**
	 * the factory used for creating the transformed inferences
	 */
	private final ElkInference.Factory inferenceFactory_ = new ElkInferenceBaseFactory();

	/**
	 * the new inferences indexed by conclusions
	 */
	private final Map<ElkAxiom, Collection<? extends ElkInference>> transformed_ = new HashMap<ElkAxiom, Collection<? extends ElkInference>>();

	private final List<ChangeListener> listeners_ = new ArrayList<ChangeListener>();

	/**
	 * selects an appropriate {@link ElkInferenceFlattener} for an inference
	 */
	private final ElkInference.Visitor<ElkInferenceFlattener> flattenerSelector_ = new ElkInferenceFlattenerSelector();

	/**
	 * a temporary object to collect the (transformed) inferences for
	 * conclusions
	 */
	private Set<ElkInference> conclusionInferences_;

	public FlattenedElkInferenceSet(ElkInferenceSet inferences) {
		this.originalInferences_ = inferences;
		inferences.add(this); // listener
	}

	@Override
	public Collection<? extends ElkInference> get(ElkAxiom conclusion) {
		Collection<? extends ElkInference> result = transformed_
				.get(conclusion);
		if (result == null) {
			// not yet computed
			transform(conclusion);
			if (conclusionInferences_ == null) {
				// inference don't change
				result = originalInferences_.get(conclusion);
			} else {
				result = conclusionInferences_;
				conclusionInferences_ = null;
			}
			transformed_.put(conclusion, result);
		}
		return result;
	}

	void transform(ElkAxiom axiom) {
		// check if some inference for axiom can be transformed
		boolean transform = false;
		Collection<? extends ElkInference> original = originalInferences_
				.get(axiom);
		for (ElkInference inf : original) {
			if (inf.accept(flattenerSelector_) != null) {
				transform = true;
				break;
			}
		}
		if (!transform) {
			// inferences don't change
			return;
		}
		// (recursively) filter out unnecessary inferences
		ProofNode<ElkAxiom> node = new ElkProofNode(axiom, originalInferences_);
		node = ProofNodes.eliminateNotDerivableAndCycles(node);
		if (node == null) {
			// axiom is not derivable
			conclusionInferences_ = Collections.emptySet();
			return;
		}
		conclusionInferences_ = new HashSet<ElkInference>();
		for (ProofStep<ElkAxiom> step : node.getInferences()) {
			ElkInference inf = getInference(step);
			ElkInferenceFlattener flattener = inf.accept(flattenerSelector_);
			if (flattener == null) {
				// inference cannot transform
				conclusionInferences_.add(inf);
			} else {
				LOGGER_.trace("{}: processing", inf);
				flattener.flatten(step);
			}
		}
	}

	static ElkInference getInference(ProofStep<?> step) {
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

	@Override
	public void add(ChangeListener listener) {
		listeners_.add(listener);
	}

	@Override
	public void remove(ChangeListener listener) {
		listeners_.remove(listener);
	}

	public void dispose() {
		originalInferences_.remove(this);
	}

	@Override
	public void inferencesChanged() {
		transformed_.clear();
		for (ChangeListener listener : listeners_) {
			listener.inferencesChanged();
		}
	}

	private class ElkInferenceFlattenerSelector
			extends ElkInferenceDummyVisitor<ElkInferenceFlattener>
			implements ElkInferenceProducer {

		@Override
		public ElkInferenceFlattener visit(
				ElkClassInclusionHierarchy inference) {
			return new ElkClassInclusionHierarchyFlattener(inference,
					inferenceFactory_, this);
		}

		@Override
		public ElkInferenceFlattener visit(
				ElkClassInclusionExistentialPropertyExpansion inference) {
			return new ElkClassInclusionExistentialPropertyExpansionFlattener(
					inference, inferenceFactory_, this);
		}

		@Override
		public ElkInferenceFlattener visit(
				ElkPropertyInclusionHierarchy inference) {
			return new ElkPropertyInclusionHierarchyFlattener(inference,
					inferenceFactory_, this);
		}

		@Override
		public void produce(ElkInference inference) {
			LOGGER_.trace("{}: flattened", inference);
			conclusionInferences_.add(inference);
		}

	}

}