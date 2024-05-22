/*
 * #%L
 * ELK Reasoner
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
package org.semanticweb.elk.reasoner.tracing.factories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.semanticweb.elk.reasoner.proof.ReasonerProducer;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference;
import org.semanticweb.elk.reasoner.tracing.ConclusionBaseFactory;
import org.semanticweb.elk.reasoner.tracing.DummyConclusionVisitor;
import org.semanticweb.elk.reasoner.tracing.ModifiableTracingProof;
import org.semanticweb.elk.reasoner.tracing.TracingInference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An {@link Producer} that filters out the unnecessary {@link ClassInference}s
 * and saves the remaining ones into the provided
 * {@link ModifiableTracingProof}. A {@link ClassInference} is unnecessary if
 * one of the local premises of the inference is either its conclusion (cycle of
 * length one) or can only be a conclusion of inferences in the provided
 * {@link ModifiableTracingProof} which have this conclusion as one of the
 * premises (cycle of length two).
 * 
 * @author Yevgeny Kazakov
 */
class ClassInferenceBlockingFilter implements ReasonerProducer<ClassInference> {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ClassInferenceBlockingFilter.class);

	private final static ClassConclusion.Factory CONCLUSION_FACTORY_ = new ConclusionBaseFactory();

	private final static ClassInference.Visitor<ClassConclusion> CONCLUSION_GETTER_ = new ClassInferenceConclusionGettingVisitor(
			CONCLUSION_FACTORY_);

	private final ModifiableTracingProof<ClassInference> output_;

	/**
	 * inferences such that one of their local premises cannot be obtained as a
	 * conclusion of other inferences in {@link #output_} that do not use the
	 * conclusion of the inference as one of the premises; the key is one of
	 * such premises
	 */
	private final Map<ClassConclusion, List<ClassInference>> blocked_ = new HashMap<ClassConclusion, List<ClassInference>>();

	/**
	 * the inferences that are (no longer) blocked as a result of adding other
	 * (unblocked) inferences to {@link #output_}
	 */
	private final Queue<ClassInference> unblocked_ = new LinkedList<ClassInference>();

	ClassInferenceBlockingFilter(
			ModifiableTracingProof<ClassInference> output) {
		this.output_ = output;
	}

	@Override
	public void produce(ClassInference next) {
		checkBlocked(next);
		while ((next = unblocked_.poll()) != null) {
			output_.produce(next);
			List<ClassInference> blockedByNext = blocked_
					.remove(getConclusion(next));
			if (blockedByNext == null) {
				continue;
			}
			// else
			for (ClassInference inf : blockedByNext) {
				checkBlocked(inf);
			}
		}
	}

	private void checkBlocked(ClassInference inference) {
		if (hasPremise(inference, getConclusion(inference))) {
			LOGGER_.trace("{}: permanently blocked", inference);
			return;
		}
		ClassConclusion blockedPremise = getBlockedPremise(inference);
		if (blockedPremise == null) {
			LOGGER_.trace("{}: unblocked", inference);
			unblocked_.add(inference);
		} else {
			LOGGER_.trace("{}: blocked by {}", inference, blockedPremise);
			block(inference, blockedPremise);
		}
	}

	/**
	 * Records that the given {@link ClassInference} has the given premise
	 * {@code ClassConclusion} that is not a conclusion of any inference in
	 * {@link #output_} that do not use the conclusion of this
	 * {@link ClassInference} as one of the premises
	 * 
	 * @param inference
	 * @param conclusion
	 */
	private void block(ClassInference inference, ClassConclusion conclusion) {
		List<ClassInference> blockedForConclusion = blocked_.get(conclusion);
		if (blockedForConclusion == null) {
			blockedForConclusion = new ArrayList<ClassInference>();
			blocked_.put(conclusion, blockedForConclusion);
		}
		blockedForConclusion.add(inference);
	}

	private static ClassConclusion getConclusion(ClassInference inference) {
		return inference.accept(CONCLUSION_GETTER_);
	}

	/**
	 * @param inference
	 * @param premise
	 * @return {@code true} if the given {@code ClassInference} has the given
	 *         {@code ClassConclusion} as one of the premises
	 */
	private static boolean hasPremise(ClassInference inference,
			ClassConclusion premise) {
		ConclusionEqualityChecker checker = new ConclusionEqualityChecker(
				premise);
		inference.accept(new ClassInferenceLocalPremiseVisitor<Void>(
				CONCLUSION_FACTORY_, checker));
		return checker.getResult();
	}

	/**
	 * @param inference
	 * @return a local premise of the given {@code ClassInference} that cannot
	 *         be derived by other inferences in {@link #output_} that do not
	 *         use the conclusion of this {@code ClassInference} as one of the
	 *         premises; returns {@code null} if such a premise does not exist
	 */
	private ClassConclusion getBlockedPremise(ClassInference inference) {
		DerivabilityChecker checker = new DerivabilityChecker(
				getConclusion(inference));
		inference.accept(new ClassInferenceLocalPremiseVisitor<Void>(
				CONCLUSION_FACTORY_, checker));
		return checker.getResult();
	}

	/**
	 * @param conclusion
	 * @param premise
	 * @return {@code true} if there exists an inference in {@link #output_}
	 *         with the given conclusion which does not use the given premise
	 */
	private boolean derivableWithoutPremise(ClassConclusion conclusion,
			ClassConclusion premise) {
		for (TracingInference inf : output_.getInferences(conclusion)) {
			if (!hasPremise((ClassInference) inf, premise)) {
				return true;
			}
		}
		// else
		return false;
	}

	/**
	 * A convenience class for checking if series of {@code ClassConclusion}s
	 * are derivable by inferences in {@link #output_} without using a given
	 * premise. For checking this, it is sufficient to visit these conclusions
	 * in any order by {@link DerivabilityChecker} and then take
	 * {@link DerivabilityChecker#getResult()}.
	 * 
	 * @author Yevgeny Kazakov
	 */
	private class DerivabilityChecker extends DummyConclusionVisitor<Void> {

		private final ClassConclusion premise_;

		/**
		 * the first visited {@code ClassConclusion} that is not derivable
		 * without {@link #premise_}
		 */
		private ClassConclusion result_ = null;

		DerivabilityChecker(ClassConclusion premise) {
			this.premise_ = premise;
		}

		@Override
		protected Void defaultVisit(ClassConclusion candidate) {
			if (result_ == null
					&& !derivableWithoutPremise(candidate, premise_)) {
				result_ = candidate;
			}
			return null;
		}

		ClassConclusion getResult() {
			return result_;
		}

	}

}
