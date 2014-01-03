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

public class ConclusionCounter {

	int countNegativeSubsumers;

	int countPositiveSubsumers;

	int countBackwardLinks;

	int countForwardLinks;

	int countContradictions;

	int countPropagations;

	int countDisjointnessAxioms;

	public int getCountNegativeSubsumers() {
		return countNegativeSubsumers;
	}

	public int getCountPositiveSubsumers() {
		return countPositiveSubsumers;
	}

	public int getCountBackwardLinks() {
		return countBackwardLinks;
	}

	public int getCountForwardLinks() {
		return countForwardLinks;
	}

	public int getCountBottoms() {
		return countContradictions;
	}

	public int getCountPropagations() {
		return countPropagations;
	}

	public int getCountDisjointnessAxioms() {
		return countDisjointnessAxioms;
	}

	/**
	 * Reset all counters to zero.
	 */
	public void reset() {
		countNegativeSubsumers = 0;
		countPositiveSubsumers = 0;
		countBackwardLinks = 0;
		countForwardLinks = 0;
		countContradictions = 0;
		countPropagations = 0;
		countDisjointnessAxioms = 0;
	}

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
		this.countNegativeSubsumers += counter.countNegativeSubsumers;
		this.countPositiveSubsumers += counter.countPositiveSubsumers;
		this.countBackwardLinks += counter.countBackwardLinks;
		this.countForwardLinks += counter.countForwardLinks;
		this.countContradictions += counter.countContradictions;
		this.countPropagations += counter.countPropagations;
		this.countDisjointnessAxioms += counter.countDisjointnessAxioms;
	}

	public long getTotalCount() {
		return countNegativeSubsumers + countPositiveSubsumers
				+ countBackwardLinks + countForwardLinks + countContradictions
				+ countPropagations + countDisjointnessAxioms;
	}

}
