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
package org.semanticweb.elk.reasoner.saturation.conclusions;

public class ConclusionCounter {

	int countNegativeSubsumers;

	int countPositiveSubsumers;

	int countBackwardLinks;

	int countForwardLinks;

	int countBottoms;

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
		return countBottoms;
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
		countBottoms = 0;
		countPropagations = 0;
		countDisjointnessAxioms = 0;
	}

	/**
	 * Adds all counters of the argument to the corresponding counters of this
	 * object. The counters should not be directly modified other than using
	 * this method during this operation. The counter in the argument will be
	 * reseted after this operation.
	 * 
	 * @param statistics
	 *            the object which counters should be added
	 */
	public synchronized void add(ConclusionCounter statistics) {
		this.countNegativeSubsumers += statistics.countNegativeSubsumers;
		this.countPositiveSubsumers += statistics.countPositiveSubsumers;
		this.countBackwardLinks += statistics.countBackwardLinks;
		this.countForwardLinks += statistics.countForwardLinks;
		this.countBottoms += statistics.countBottoms;
		this.countPropagations += statistics.countPropagations;
		this.countDisjointnessAxioms += statistics.countDisjointnessAxioms;
	}

}
