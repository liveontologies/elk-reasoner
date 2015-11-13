/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.saturation.conclusions.classes;

import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionDecomposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubContextInitialization;

/**
 * An object which can be used to count {@link ClassConclusion}s. The fields of the
 * counter correspond to the methods of {@link ClassConclusion.Visitor}.
 * 
 * @author "Yevgeny Kazakov"
 */
public class ClassConclusionCounter {

	/**
	 * counter for {@link BackwardLink}s
	 */
	long countBackwardLinks;

	/**
	 * counter for {@link ContextInitialization}s
	 */
	long countContextInitializations;

	/**
	 * counter for {@link Contradiction}s
	 */
	long countContradictions;

	/**
	 * counter for {@link DisjointSubsumer}s
	 */
	long countDisjointSubsumers;

	/**
	 * counter for {@link ForwardLink}s
	 */
	long countForwardLinks;

	/**
	 * counter for {@link SubClassInclusionDecomposed}s
	 */
	long countDecomposedSubsumers;

	/**
	 * counter for {@link SubClassInclusionComposed}s
	 */
	long countComposedSubsumers;

	/**
	 * counter for {@link Propagation}s
	 */
	long countPropagations;

	/**
	 * counter for {@link SubContextInitialization}s
	 */
	long countSubContextInitializations;

	/**
	 * Adds all counters of the argument to the corresponding counters of this
	 * object. The counters should not be directly modified other than using
	 * this method during this operation. The counter in the argument will be
	 * reseted after this operation.
	 * 
	 * @param counter
	 *            the object which counters should be added
	 */
	public synchronized void add(ClassConclusionCounter counter) {
		this.countDecomposedSubsumers += counter.countDecomposedSubsumers;
		this.countComposedSubsumers += counter.countComposedSubsumers;
		this.countBackwardLinks += counter.countBackwardLinks;
		this.countForwardLinks += counter.countForwardLinks;
		this.countContradictions += counter.countContradictions;
		this.countPropagations += counter.countPropagations;
		this.countDisjointSubsumers += counter.countDisjointSubsumers;
		this.countContextInitializations += counter.countContextInitializations;
		this.countSubContextInitializations += counter.countSubContextInitializations;
	}

	public long getCountBackwardLinks() {
		return countBackwardLinks;
	}

	public long getCountContradictions() {
		return countContradictions;
	}

	public long getCountContextInitializations() {
		return countContextInitializations;
	}

	public long getCountDisjointSubsumers() {
		return countDisjointSubsumers;
	}

	public long getCountForwardLinks() {
		return countForwardLinks;
	}

	public long getCountDecomposedSubsumers() {
		return countDecomposedSubsumers;
	}

	public long getCountComposedSubsumers() {
		return countComposedSubsumers;
	}

	public long getCountPropagations() {
		return countPropagations;
	}

	public long getCountSubContextInitializations() {
		return countSubContextInitializations;
	}

	public long getTotalCount() {
		return countDecomposedSubsumers + countComposedSubsumers
				+ countBackwardLinks + countForwardLinks + countContradictions
				+ countPropagations + countDisjointSubsumers
				+ countContextInitializations + countSubContextInitializations;
	}

	/**
	 * Reset all counters to zero.
	 */
	public void reset() {
		countDecomposedSubsumers = 0;
		countComposedSubsumers = 0;
		countBackwardLinks = 0;
		countForwardLinks = 0;
		countContradictions = 0;
		countPropagations = 0;
		countDisjointSubsumers = 0;
		countContextInitializations = 0;
		countSubContextInitializations = 0;
	}

}
