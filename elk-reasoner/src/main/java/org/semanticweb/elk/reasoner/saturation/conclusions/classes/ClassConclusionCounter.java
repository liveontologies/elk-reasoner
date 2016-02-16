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
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassInconsistency;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionDecomposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubContextInitialization;

/**
 * An object which can be used to count {@link ClassConclusion}s. The fields of
 * the counter correspond to the methods of {@link ClassConclusion.Visitor}.
 * 
 * @author "Yevgeny Kazakov"
 */
public class ClassConclusionCounter {

	/**
	 * counter for {@link BackwardLink}
	 */
	long countBackwardLink;

	/**
	 * counter for {@link ContextInitialization}
	 */
	long countContextInitialization;

	/**
	 * counter for {@link ClassInconsistency}
	 */
	long countContradiction;

	/**
	 * counter for {@link DisjointSubsumer}
	 */
	long countDisjointSubsumer;

	/**
	 * counter for {@link ForwardLink}
	 */
	long countForwardLink;

	/**
	 * counter for {@link SubClassInclusionDecomposed}
	 */
	long countSubClassInclusionDecomposed;

	/**
	 * counter for {@link SubClassInclusionComposed}
	 */
	long countSubClassInclusionComposed;

	/**
	 * counter for {@link Propagation}
	 */
	long countPropagation;

	/**
	 * counter for {@link SubContextInitialization}
	 */
	long countSubContextInitialization;

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
		this.countSubClassInclusionDecomposed += counter.countSubClassInclusionDecomposed;
		this.countSubClassInclusionComposed += counter.countSubClassInclusionComposed;
		this.countBackwardLink += counter.countBackwardLink;
		this.countForwardLink += counter.countForwardLink;
		this.countContradiction += counter.countContradiction;
		this.countPropagation += counter.countPropagation;
		this.countDisjointSubsumer += counter.countDisjointSubsumer;
		this.countContextInitialization += counter.countContextInitialization;
		this.countSubContextInitialization += counter.countSubContextInitialization;
	}

	public long getCountBackwardLink() {
		return countBackwardLink;
	}

	public long getCountContradiction() {
		return countContradiction;
	}

	public long getCountContextInitialization() {
		return countContextInitialization;
	}

	public long getCountDisjointSubsumer() {
		return countDisjointSubsumer;
	}

	public long getCountForwardLink() {
		return countForwardLink;
	}

	public long getCountSubClassInclusionDecomposed() {
		return countSubClassInclusionDecomposed;
	}

	public long getCountSubClassInclusionComposed() {
		return countSubClassInclusionComposed;
	}

	public long getCountPropagation() {
		return countPropagation;
	}

	public long getCountSubContextInitialization() {
		return countSubContextInitialization;
	}

	public long getTotalCount() {
		return countSubClassInclusionDecomposed + countSubClassInclusionComposed
				+ countBackwardLink + countForwardLink + countContradiction
				+ countPropagation + countDisjointSubsumer
				+ countContextInitialization + countSubContextInitialization;
	}

	/**
	 * Reset all counters to zero.
	 */
	public void reset() {
		countSubClassInclusionDecomposed = 0;
		countSubClassInclusionComposed = 0;
		countBackwardLink = 0;
		countForwardLink = 0;
		countContradiction = 0;
		countPropagation = 0;
		countDisjointSubsumer = 0;
		countContextInitialization = 0;
		countSubContextInitialization = 0;
	}

}
