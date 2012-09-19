package org.semanticweb.elk.reasoner.indexing.hierarchy;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
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

/**
 * The object that is used to measure the time spent inside rules.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class RuleStatistics extends
		org.semanticweb.elk.reasoner.saturation.conclusions.RuleStatistics {

	/**
	 * the number of rule applications for composition of
	 * {@link IndexedObjectIntersectionOf}
	 */
	int countObjectIntersectionOfCompositionRule;

	/**
	 * the time spent within the composition rule of
	 * {@link IndexedObjectIntersectionOf}
	 */
	long timeObjectIntersectionOfCompositionRule;

	/**
	 * the number of rule applications for decomposition of
	 * {@link IndexedObjectIntersectionOf}
	 */
	int countObjectIntersectionOfDecompositionRule;

	/**
	 * the time spent within the decomposition rule of
	 * {@link IndexedObjectIntersectionOf}
	 */
	long timeObjectIntersectionOfDecompositionRule;

	/**
	 * the number of rule applications for composition of
	 * {@link IndexedObjectIntersectionOf}
	 */
	int countObjectSomeValuesFromCompositionRule;

	/**
	 * the time spent within the composition rule of
	 * {@link IndexedObjectSomeValuesFrom}
	 */
	long timeObjectSomeValuesFromCompositionRule;

	/**
	 * the number of rule applications for decomposition of
	 * {@link IndexedObjectSomeValuesFrom}
	 */
	int countObjectSomeValuesFromDecompositionRule;

	/**
	 * the time spent within the decomposition rule of
	 * {@link IndexedObjectSomeValuesFrom}
	 */
	long timeObjectSomeValuesFromDecompositionRule;

	/**
	 * the number of applications of the backward link rule in
	 * {@link IndexedObjectSomeValuesFrom}
	 */
	int countObjectSomeValuesFromBackwardLinkRule;

	/**
	 * the time spent within the backward link rule of
	 * {@link IndexedObjectSomeValuesFrom}
	 */
	long timeObjectSomeValuesFromBackwardLinkRule;

	/**
	 * the number of rule applications for decomposition of {@link IndexedClass}
	 */
	int countClassDecompositionRule;

	/**
	 * the time spent within the decomposition rule of {@link IndexedClass}
	 */
	long timeClassDecompositionRule;

	/**
	 * the number of rule applications of the backward link rule in
	 * {@link IndexedClass}
	 */
	int countClassBottomBackwardLinkRule;

	/**
	 * the time spent within the backward link rule of {@link IndexedClass}
	 */
	long timeClassBottomBackwardLinkRule;

	/**
	 * the number of rule applications unfolding {@link IndexedSubClassOfAxiom}
	 */
	int countSubClassOfRule;

	/**
	 * the time spent within the composition rule of
	 * {@link IndexedSubClassOfAxiom}
	 */
	long timeSubClassOfRule;

	/**
	 * @return the number of rule applications for composition of
	 *         {@link IndexedObjectIntersectionOf}
	 */
	public long getObjectIntersectionOfCompositionRuleCount() {
		return countObjectIntersectionOfCompositionRule;
	}

	/**
	 * @return the time spent within the composition rule of
	 *         {@link IndexedObjectIntersectionOf}
	 */
	public long getObjectIntersectionOfCompositionRuleTime() {
		return timeObjectIntersectionOfCompositionRule;
	}

	/**
	 * @return the number of rule applications for decomposition of
	 *         {@link IndexedObjectIntersectionOf}
	 */
	public long getObjectIntersectionOfDecompositionRuleCount() {
		return countObjectIntersectionOfDecompositionRule;
	}

	/**
	 * @return the time spent within the decomposition rule of
	 *         {@link IndexedObjectIntersectionOf}
	 */
	public long getObjectIntersectionOfDecompositionRuleTime() {
		return timeObjectIntersectionOfDecompositionRule;
	}

	/**
	 * @return the number of rule applications for composition of
	 *         {@link IndexedObjectIntersectionOf}
	 */
	public long getObjectSomeValuesFromCompositionRuleCount() {
		return countObjectSomeValuesFromCompositionRule;
	}

	/**
	 * @return the time spent within the composition rule of
	 *         {@link IndexedObjectSomeValuesFrom}
	 */
	public long getObjectSomeValuesFromCompositionRuleTime() {
		return timeObjectSomeValuesFromCompositionRule;
	}

	/**
	 * @return the number of rule applications for decomposition of
	 *         {@link IndexedObjectSomeValuesFrom}
	 */
	public long getObjectSomeValuesFromDecompositionRuleCount() {
		return countObjectSomeValuesFromDecompositionRule;
	}

	/**
	 * @return the time spent within the decomposition rule of
	 *         {@link IndexedObjectSomeValuesFrom}
	 */
	public long getObjectSomeValuesFromDecompositionRuleTime() {
		return timeObjectSomeValuesFromDecompositionRule;
	}

	/**
	 * @return the number of applications of the backward link rule in
	 *         {@link IndexedObjectSomeValuesFrom}
	 */
	public long getObjectSomeValuesFromBackwardLinkRuleCount() {
		return countObjectSomeValuesFromBackwardLinkRule;
	}

	/**
	 * @return the time spent within the backward link rule of
	 *         {@link IndexedObjectSomeValuesFrom}
	 */
	public long getObjectSomeValuesFromBackwardLinkRuleTime() {
		return timeObjectSomeValuesFromBackwardLinkRule;
	}

	/**
	 * @return the number of rule applications for decomposition of
	 *         {@link IndexedClass}
	 */
	public long getClassDecompositionRuleCount() {
		return countClassDecompositionRule;
	}

	/**
	 * @return the time spent within the decomposition rule of
	 *         {@link IndexedClass}
	 */
	public long getClassDecompositionRuleTime() {
		return timeClassDecompositionRule;
	}

	/**
	 * @return the number of rule applications of the backward link rule in
	 *         {@link IndexedClass}
	 */
	public long getClassBottomBackwardLinkRuleCount() {
		return countClassBottomBackwardLinkRule;
	}

	/**
	 * @return the time spent within the backward link rule of
	 *         {@link IndexedClass}
	 */
	public long getClassBottomBackwardLinkRuleTime() {
		return timeClassBottomBackwardLinkRule;
	}

	/**
	 * @return the number of rule applications unfolding
	 *         {@link IndexedSubClassOfAxiom}
	 */
	public long getSubClassOfRuleCount() {
		return countSubClassOfRule;
	}

	/**
	 * @return the time spent within the composition rule of
	 *         {@link IndexedSubClassOfAxiom}
	 */
	public long getSubClassOfRuleTime() {
		return timeSubClassOfRule;
	}

	/**
	 * Reset all timers to zero.
	 */
	public void reset() {
		super.reset();
		countObjectSomeValuesFromCompositionRule = 0;
		timeObjectSomeValuesFromCompositionRule = 0;
		countObjectSomeValuesFromDecompositionRule = 0;
		timeObjectSomeValuesFromDecompositionRule = 0;
		countObjectSomeValuesFromBackwardLinkRule = 0;
		timeObjectSomeValuesFromBackwardLinkRule = 0;
		countObjectIntersectionOfDecompositionRule = 0;
		timeObjectIntersectionOfDecompositionRule = 0;
		countObjectIntersectionOfCompositionRule = 0;
		timeObjectIntersectionOfCompositionRule = 0;
		countObjectIntersectionOfDecompositionRule = 0;
		timeObjectIntersectionOfDecompositionRule = 0;
		countClassDecompositionRule = 0;
		timeClassDecompositionRule = 0;
		countClassBottomBackwardLinkRule = 0;
		timeClassBottomBackwardLinkRule = 0;
		countSubClassOfRule = 0;
		timeSubClassOfRule = 0;
	}

	public synchronized void merge(RuleStatistics stats) {
		super.merge(stats);
		this.countObjectIntersectionOfCompositionRule += stats.countObjectIntersectionOfCompositionRule;
		this.timeObjectIntersectionOfCompositionRule += stats.timeObjectIntersectionOfCompositionRule;
		this.countObjectIntersectionOfDecompositionRule += stats.countObjectIntersectionOfDecompositionRule;
		this.timeObjectIntersectionOfDecompositionRule += stats.timeObjectIntersectionOfDecompositionRule;
		this.countObjectSomeValuesFromCompositionRule += stats.countObjectSomeValuesFromCompositionRule;
		this.timeObjectSomeValuesFromCompositionRule += stats.timeObjectSomeValuesFromCompositionRule;
		this.countObjectSomeValuesFromDecompositionRule += stats.countObjectSomeValuesFromDecompositionRule;
		this.timeObjectSomeValuesFromDecompositionRule += stats.timeObjectSomeValuesFromDecompositionRule;
		this.countObjectSomeValuesFromBackwardLinkRule += stats.countObjectSomeValuesFromBackwardLinkRule;
		this.timeObjectSomeValuesFromBackwardLinkRule += stats.timeObjectSomeValuesFromBackwardLinkRule;
		this.countClassDecompositionRule += stats.countClassDecompositionRule;
		this.timeClassDecompositionRule += stats.timeClassDecompositionRule;
		this.countClassBottomBackwardLinkRule += stats.countClassBottomBackwardLinkRule;
		this.timeClassBottomBackwardLinkRule += stats.timeClassBottomBackwardLinkRule;
		this.countSubClassOfRule += stats.countSubClassOfRule;
		this.timeSubClassOfRule += stats.timeSubClassOfRule;
	}

}
