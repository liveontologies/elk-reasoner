package org.semanticweb.elk.reasoner.saturation.rules;

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedSubClassOfAxiom;

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
public class RuleStatistics {
	
	/**
	 * Counts the number of times other rule stats were added to this one, for
	 * averaging purposes
	 */
	private int addCounter = 0;

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
	//@Override
	public void reset() {
		//super.reset();
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

	public synchronized void add(RuleStatistics stats) {
		addCounter++;
		countObjectIntersectionOfCompositionRule += stats.countObjectIntersectionOfCompositionRule;
		timeObjectIntersectionOfCompositionRule += stats.timeObjectIntersectionOfCompositionRule;
		countObjectIntersectionOfDecompositionRule += stats.countObjectIntersectionOfDecompositionRule;
		timeObjectIntersectionOfDecompositionRule += stats.timeObjectIntersectionOfDecompositionRule;
		countObjectSomeValuesFromCompositionRule += stats.countObjectSomeValuesFromCompositionRule;
		timeObjectSomeValuesFromCompositionRule += stats.timeObjectSomeValuesFromCompositionRule;
		countObjectSomeValuesFromDecompositionRule += stats.countObjectSomeValuesFromDecompositionRule;
		timeObjectSomeValuesFromDecompositionRule += stats.timeObjectSomeValuesFromDecompositionRule;
		countPropagationBackwardLinkRule += stats.countPropagationBackwardLinkRule;
		timePropagationBackwardLinkRule += stats.timePropagationBackwardLinkRule;
		countClassDecompositionRule += stats.countClassDecompositionRule;
		timeClassDecompositionRule += stats.timeClassDecompositionRule;
		countContradictionBackwardLinkRule += stats.countContradictionBackwardLinkRule;
		timeContradictionBackwardLinkRule += stats.timeContradictionBackwardLinkRule;
		countSubClassOfRule += stats.countSubClassOfRule;
		timeSubClassOfRule += stats.timeSubClassOfRule;
		countOwlThingContextInitializationRule += stats.countOwlThingContextInitializationRule;
		timeOwlThingContextInitializationRule += stats.timeOwlThingContextInitializationRule;
		countDisjointnessAxiomCompositionRule += stats.countDisjointnessAxiomCompositionRule;
		timeDisjointnessAxiomCompositionRule += stats.timeDisjointnessAxiomCompositionRule;
		countDisjointnessAxiomContradictionRule += stats.countDisjointnessAxiomContradictionRule;
		timeDisjointnessAxiomContradictionRule += stats.timeDisjointnessAxiomContradictionRule;
		countBackwardLinkFromForwardLinkRule += stats.countBackwardLinkFromForwardLinkRule;
		timeBackwardLinkFromForwardLinkRule += stats.timeBackwardLinkFromForwardLinkRule;		
	}

	public void print(Logger logger) {
		if (!logger.isDebugEnabled() || addCounter <= 0)
			return;
		
		if (countBackwardLinkFromForwardLinkRule > 0) 
			logger.debug("Rule for producing backward links from forward links "
					+ countBackwardLinkFromForwardLinkRule + " ("
					+ timeBackwardLinkFromForwardLinkRule / addCounter + " ms)");
		
		if (countDisjointnessAxiomContradictionRule > 0) 
			logger.debug("Disjointness axiom contradiction rule " 
					+ countDisjointnessAxiomContradictionRule + " ("
					+ timeDisjointnessAxiomContradictionRule / addCounter + " ms)");
		
		if (countDisjointnessAxiomCompositionRule > 0) 
			logger.debug("Disjointness axiom composition rule "
					+ countDisjointnessAxiomCompositionRule + " ("
					+ timeDisjointnessAxiomCompositionRule / addCounter + " ms)");
		
		if (countOwlThingContextInitializationRule > 0) 
			logger.debug("owl:Thing context init rule "
					+ countOwlThingContextInitializationRule + " ("
					+ timeOwlThingContextInitializationRule / addCounter + " ms)");
		
		if (countSubClassOfRule > 0) 
			logger.debug("Subclass rule "
					+ countSubClassOfRule + " ("
					+ timeSubClassOfRule / addCounter + " ms)");
		
		if (countContradictionBackwardLinkRule > 0) 
			logger.debug("Rule for propagating owl:Nothing "
					+ countContradictionBackwardLinkRule + " ("
					+ timeContradictionBackwardLinkRule / addCounter + " ms)");
		
		if (countClassDecompositionRule > 0) 
			logger.debug("Class decomposition rule "
					+ countClassDecompositionRule + " ("
					+ timeClassDecompositionRule / addCounter + " ms)");
		
		if (countPropagationBackwardLinkRule > 0) 
			logger.debug("Rule for propagation via backward links "
					+ countPropagationBackwardLinkRule + " ("
					+ timePropagationBackwardLinkRule / addCounter + " ms)");		
		
		if (countObjectSomeValuesFromDecompositionRule > 0) 
			logger.debug("Some values from decomposition rule "
					+ countObjectSomeValuesFromDecompositionRule + " ("
					+ timeObjectSomeValuesFromDecompositionRule / addCounter + " ms)");
		
		if (countObjectSomeValuesFromCompositionRule > 0) 
			logger.debug("Some values from composition rule "
					+ countObjectSomeValuesFromCompositionRule + " ("
					+ timeObjectSomeValuesFromCompositionRule / addCounter + " ms)");
		
		if (countObjectIntersectionOfDecompositionRule > 0) 
			logger.debug("Intersection decomposition rule "
					+ countObjectIntersectionOfDecompositionRule + " ("
					+ timeObjectIntersectionOfDecompositionRule / addCounter + " ms)");
		
		if (countObjectIntersectionOfCompositionRule > 0) 
			logger.debug("Intersection composition rule "
					+ countObjectIntersectionOfCompositionRule + " ("
					+ timeObjectIntersectionOfCompositionRule / addCounter + " ms)");
	}
	
	
}
