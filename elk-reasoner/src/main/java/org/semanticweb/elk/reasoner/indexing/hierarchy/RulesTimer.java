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
public class RulesTimer extends
		org.semanticweb.elk.reasoner.saturation.conclusions.RulesTimer {

	/**
	 * measures the time within the composition rule of
	 * {@link IndexedObjectIntersectionOf}
	 */
	long timeObjectIntersectionOfCompositionRule;

	/**
	 * measures the time within the decomposition rule of
	 * {@link IndexedObjectIntersectionOf}
	 */
	long timeObjectIntersectionOfDecompositionRule;

	/**
	 * measures the time within the composition rule of
	 * {@link IndexedObjectSomeValuesFrom}
	 */
	long timeObjectSomeValuesFromCompositionRule;

	/**
	 * measures the time within the decomposition rule of
	 * {@link IndexedObjectSomeValuesFrom}
	 */
	long timeObjectSomeValuesFromDecompositionRule;

	/**
	 * measures the time within the backward link rule of
	 * {@link IndexedObjectSomeValuesFrom}
	 */
	long timeObjectSomeValuesFromBackwardLinkRule;

	/**
	 * measures the time within the decomposition rule of {@link IndexedClass}
	 */
	long timeClassDecompositionRule;

	/**
	 * measures the time within the backward link rule of {@link IndexedClass}
	 */
	long timeClassBottomBackwardLinkRule;

	/**
	 * measures the time within the composition rule of
	 * {@link IndexedSubClassOfAxiom}
	 */
	long timeSubClassOfRule;

	public long getObjectIntersectionOfCompositionRuleTime() {
		return timeObjectIntersectionOfCompositionRule;
	}

	public long getObjectIntersectionOfDecompositionRuleTime() {
		return timeObjectIntersectionOfDecompositionRule;
	}

	public long getObjectSomeValuesFromCompositionRuleTime() {
		return timeObjectSomeValuesFromCompositionRule;
	}

	public long getObjectSomeValuesFromDecompositionRuleTime() {
		return timeObjectSomeValuesFromDecompositionRule;
	}

	public long getObjectSomeValuesFromBackwardLinkRuleTime() {
		return timeObjectSomeValuesFromBackwardLinkRule;
	}

	public long getClassDecompositionRuleTime() {
		return timeClassDecompositionRule;
	}

	public long getClassBottomBackwardLinkRuleTime() {
		return timeClassBottomBackwardLinkRule;
	}

	public long getSubClassOfRuleTime() {
		return timeSubClassOfRule;
	}

	/**
	 * Reset all timers to zero.
	 */
	public void reset() {
		super.reset();
		timeObjectSomeValuesFromCompositionRule = 0;
		timeObjectSomeValuesFromDecompositionRule = 0;
		timeObjectSomeValuesFromBackwardLinkRule = 0;
		timeObjectIntersectionOfDecompositionRule = 0;
		timeObjectIntersectionOfCompositionRule = 0;
		timeObjectIntersectionOfDecompositionRule = 0;
		timeClassDecompositionRule = 0;
		timeClassBottomBackwardLinkRule = 0;
		timeSubClassOfRule = 0;
	}

	public synchronized void merge(RulesTimer timer) {
		super.merge(timer);
		this.timeObjectIntersectionOfCompositionRule += timer.timeObjectIntersectionOfCompositionRule;
		this.timeObjectIntersectionOfDecompositionRule += timer.timeObjectIntersectionOfDecompositionRule;
		this.timeObjectSomeValuesFromCompositionRule += timer.timeObjectSomeValuesFromCompositionRule;
		this.timeObjectSomeValuesFromDecompositionRule += timer.timeObjectSomeValuesFromDecompositionRule;
		this.timeObjectSomeValuesFromBackwardLinkRule += timer.timeObjectSomeValuesFromBackwardLinkRule;
		this.timeClassDecompositionRule += timer.timeClassDecompositionRule;
		this.timeClassBottomBackwardLinkRule += timer.timeClassBottomBackwardLinkRule;
		this.timeSubClassOfRule += timer.timeSubClassOfRule;
	}

}
