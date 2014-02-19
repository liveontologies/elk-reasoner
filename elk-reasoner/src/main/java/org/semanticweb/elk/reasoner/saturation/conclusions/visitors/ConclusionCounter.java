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
package org.semanticweb.elk.reasoner.saturation.conclusions.visitors;

import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.DecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.SubContextInitialization;

/**
 * An object which can be used to count {@link Conclusion}s. The fields of the
 * counter correspond to the methods of {@link ConclusionVisitor}.
 * 
 * @author "Yevgeny Kazakov"
 */
public class ConclusionCounter {

	/**
	 * counter for {@link BackwardLink}s
	 */
	int countBackwardLinks;

	/**
	 * counter for {@link ContextInitialization}s
	 */
	int countContextInitializations;

	/**
	 * counter for {@link Contradiction}s
	 */
	int countContradictions;

	/**
	 * counter for {@link DisjointSubsumer}s
	 */
	int countDisjointSubsumers;

	/**
	 * counter for {@link ForwardLink}s
	 */
	int countForwardLinks;

	/**
	 * counter for {@link DecomposedSubsumer}s
	 */
	int countDecomposedSubsumers;

	/**
	 * counter for {@link ComposedSubsumer}s
	 */
	int countComposedSubsumers;

	/**
	 * counter for {@link Propagation}s
	 */
	int countPropagations;

	/**
	 * counter for {@link SubContextInitialization}s
	 */
	int countSubContextInitializations;

	/**
	 * Adds all counters of the argument to the corresponding counters of this
	 * object. The counters should not be directly modified other than using
	 * this method during this operation. The counter in the argument will be
	 * reseted after this operation.
	 * 
	 * @param counter
	 *            the object which counters should be added
	 */
	public synchronized void add(ConclusionCounter counter) {
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

	public int getCountBackwardLinks() {
		return countBackwardLinks;
	}

	public int getCountBottoms() {
		return countContradictions;
	}

	public int getCountContextInitializations() {
		return countContextInitializations;
	}

	public int getCountDisjointSubsumers() {
		return countDisjointSubsumers;
	}

	public int getCountForwardLinks() {
		return countForwardLinks;
	}

	public int getCountDecomposedSubsumers() {
		return countDecomposedSubsumers;
	}

	public int getCountComposedSubsumers() {
		return countComposedSubsumers;
	}

	public int getCountPropagations() {
		return countPropagations;
	}

	public int getCountSubContextInitializations() {
		return countSubContextInitializations;
	}

	public int getTotalCount() {
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
