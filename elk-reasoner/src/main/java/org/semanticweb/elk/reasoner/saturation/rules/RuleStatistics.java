package org.semanticweb.elk.reasoner.saturation.rules;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionStatistics;

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
public class RuleStatistics extends ConclusionStatistics {

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
	int countPropagationBackwardLinkRule;

	/**
	 * the time spent within the backward link rule of
	 * {@link IndexedObjectSomeValuesFrom}
	 */
	long timePropagationBackwardLinkRule;

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
	int countContradictionBackwardLinkRule;

	/**
	 * the time spent within the backward link rule of {@link IndexedClass}
	 */
	long timeContradictionBackwardLinkRule;

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
	 * 
	 */
	int countOwlThingContextInitializationRule;
	
	/**
	 * 
	 */
	long timeOwlThingContextInitializationRule;

	/**
	 * 
	 */
	int countDisjointnessAxiomCompositionRule;
	
	/**
	 * 
	 */
	long timeDisjointnessAxiomCompositionRule;

	/**
	 * 
	 */
	int countDisjointnessAxiomContradictionRule;
	
	/**
	 * 
	 */
	long timeDisjointnessAxiomContradictionRule;

	/**
	 * 
	 */
	int countBackwardLinkFromForwardLinkRule;
	
	/**
	 * 
	 */
	long timeBackwardLinkFromForwardLinkRule;

	/**
	 * Reset all timers to zero.
	 */
	@Override
	public void reset() {
		super.reset();
		countObjectSomeValuesFromCompositionRule = 0;
		timeObjectSomeValuesFromCompositionRule = 0;
		countObjectSomeValuesFromDecompositionRule = 0;
		timeObjectSomeValuesFromDecompositionRule = 0;
		countPropagationBackwardLinkRule = 0;
		timePropagationBackwardLinkRule = 0;
		countObjectIntersectionOfDecompositionRule = 0;
		timeObjectIntersectionOfDecompositionRule = 0;
		countObjectIntersectionOfCompositionRule = 0;
		timeObjectIntersectionOfCompositionRule = 0;
		countObjectIntersectionOfDecompositionRule = 0;
		timeObjectIntersectionOfDecompositionRule = 0;
		countClassDecompositionRule = 0;
		timeClassDecompositionRule = 0;
		countContradictionBackwardLinkRule = 0;
		timeContradictionBackwardLinkRule = 0;
		countSubClassOfRule = 0;
		timeSubClassOfRule = 0;
		countOwlThingContextInitializationRule = 0;
		timeOwlThingContextInitializationRule = 0;
		countDisjointnessAxiomCompositionRule = 0;
		timeDisjointnessAxiomCompositionRule = 0;
		countDisjointnessAxiomContradictionRule = 0;
		timeDisjointnessAxiomContradictionRule = 0;
		countBackwardLinkFromForwardLinkRule = 0;
		timeBackwardLinkFromForwardLinkRule = 0;
	}

	public synchronized void merge(RuleStatistics stats) {
		super.add(stats);
		this.countObjectIntersectionOfCompositionRule += stats.countObjectIntersectionOfCompositionRule;
		this.timeObjectIntersectionOfCompositionRule += stats.timeObjectIntersectionOfCompositionRule;
		this.countObjectIntersectionOfDecompositionRule += stats.countObjectIntersectionOfDecompositionRule;
		this.timeObjectIntersectionOfDecompositionRule += stats.timeObjectIntersectionOfDecompositionRule;
		this.countObjectSomeValuesFromCompositionRule += stats.countObjectSomeValuesFromCompositionRule;
		this.timeObjectSomeValuesFromCompositionRule += stats.timeObjectSomeValuesFromCompositionRule;
		this.countObjectSomeValuesFromDecompositionRule += stats.countObjectSomeValuesFromDecompositionRule;
		this.timeObjectSomeValuesFromDecompositionRule += stats.timeObjectSomeValuesFromDecompositionRule;
		this.countPropagationBackwardLinkRule += stats.countPropagationBackwardLinkRule;
		this.timePropagationBackwardLinkRule += stats.timePropagationBackwardLinkRule;
		this.countClassDecompositionRule += stats.countClassDecompositionRule;
		this.timeClassDecompositionRule += stats.timeClassDecompositionRule;
		this.countContradictionBackwardLinkRule += stats.countContradictionBackwardLinkRule;
		this.timeContradictionBackwardLinkRule += stats.timeContradictionBackwardLinkRule;
		this.countSubClassOfRule += stats.countSubClassOfRule;
		this.timeSubClassOfRule += stats.timeSubClassOfRule;
		this.countOwlThingContextInitializationRule += stats.countOwlThingContextInitializationRule;
		this.timeOwlThingContextInitializationRule += stats.timeOwlThingContextInitializationRule;
		this.countDisjointnessAxiomCompositionRule += stats.countDisjointnessAxiomCompositionRule;
		this.timeDisjointnessAxiomCompositionRule += stats.timeDisjointnessAxiomCompositionRule;
		this.countDisjointnessAxiomContradictionRule += stats.countDisjointnessAxiomContradictionRule;
		this.timeDisjointnessAxiomContradictionRule += stats.timeDisjointnessAxiomContradictionRule;
		this.countBackwardLinkFromForwardLinkRule += stats.countBackwardLinkFromForwardLinkRule;
		this.timeBackwardLinkFromForwardLinkRule += stats.timeBackwardLinkFromForwardLinkRule;		
	}
}
