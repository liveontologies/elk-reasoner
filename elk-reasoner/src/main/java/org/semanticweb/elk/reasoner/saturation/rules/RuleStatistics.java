package org.semanticweb.elk.reasoner.saturation.rules;

import org.apache.log4j.Logger;

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
 * The object that is used to measure the number of applied rules and time spent
 * inside rules.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class RuleStatistics {

	// TODO: limit access
	public final RuleApplicationCounter ruleCounter = new RuleApplicationCounter();
	public final RuleApplicationTimer ruleTimer = new RuleApplicationTimer();
	public final DecompositionRuleApplicationCounter decompositionRuleCounter = new DecompositionRuleApplicationCounter();
	public final DecompositionRuleApplicationTimer decompositionRuleTimer = new DecompositionRuleApplicationTimer();

	/**
	 * Counts the number of times other rule statistics were added to this one,
	 * for averaging purposes during printing
	 */
	private int addCounter = 0;

	/**
	 * Reset all timers to zero.
	 */
	public void reset() {
		decompositionRuleCounter.reset();
		decompositionRuleTimer.reset();
		ruleCounter.reset();
		ruleTimer.reset();
		addCounter = 0;
	}

	public synchronized void add(RuleStatistics stats) {
		addCounter++;
		decompositionRuleCounter.add(stats.decompositionRuleCounter);
		decompositionRuleTimer.add(stats.decompositionRuleTimer);
		ruleCounter.add(stats.ruleCounter);
		ruleTimer.add(stats.ruleTimer);
	}

	// TODO: can use rule names for printing
	public void print(Logger logger) {
		if (!logger.isDebugEnabled() || addCounter <= 0)
			return;

		if (ruleCounter.countForwardLinkBackwardLinkRule > 0)
			logger.debug("Forward link from backward link rules: "
					+ ruleCounter.countForwardLinkBackwardLinkRule + " ("
					+ ruleTimer.timeForwardLinkBackwardLinkRule / addCounter
					+ " ms)");

		if (ruleCounter.countDisjointnessAxiomContradictionRule > 0)
			logger.debug("Disjointness axiom contradiction rules: "
					+ ruleCounter.countDisjointnessAxiomContradictionRule
					+ " (" + ruleTimer.timeDisjointnessAxiomContradictionRule
					/ addCounter + " ms)");

		if (ruleCounter.countDisjointnessAxiomCompositionRule > 0)
			logger.debug("Disjointness axiom composition rules: "
					+ ruleCounter.countDisjointnessAxiomCompositionRule + " ("
					+ ruleTimer.timeDisjointnessAxiomCompositionRule
					/ addCounter + " ms)");

		if (ruleCounter.countOwlThingContextInitializationRule > 0)
			logger.debug("owl:Thing context init rules: "
					+ ruleCounter.countOwlThingContextInitializationRule + " ("
					+ ruleTimer.timeOwlThingContextInitializationRule
					/ addCounter + " ms)");
		
		if (ruleCounter.countContextRootInitializationRule > 0)
			logger.debug("Context root init rules: "
					+ ruleCounter.countContextRootInitializationRule + " ("
					+ ruleTimer.timeContextRootInitializationRule
					/ addCounter + " ms)");

		if (ruleCounter.countSubClassOfAxiomCompositionRule > 0)
			logger.debug("Subclass expansions: "
					+ ruleCounter.countSubClassOfAxiomCompositionRule + " ("
					+ ruleTimer.timeSubClassOfAxiomCompositionRule / addCounter
					+ " ms)");

		if (ruleCounter.countContradictionBottomBackwardLinkRule > 0)
			logger.debug("Propagations of inconsistency: "
					+ ruleCounter.countContradictionBottomBackwardLinkRule
					+ " (" + ruleTimer.timeContradictionBottomBackwardLinkRule
					/ addCounter + " ms)");

		if (ruleCounter.countPropagationBackwardLinkRule > 0)
			logger.debug("Propagations via backward links: "
					+ ruleCounter.countPropagationBackwardLinkRule + " ("
					+ ruleTimer.timePropagationBackwardLinkRule / addCounter
					+ " ms)");

		if (ruleCounter.countObjectSomeValuesFromCompositionRule
				+ decompositionRuleCounter.countIndexedObjectSomeValuesFromDecompositionRule > 0)
			logger.debug("ObjectSomeValuesFrom composition/decomposition rules: "
					+ ruleCounter.countObjectSomeValuesFromCompositionRule
					+ "/"
					+ decompositionRuleCounter.countIndexedObjectSomeValuesFromDecompositionRule
					+ " (" + ruleTimer.timeObjectSomeValuesFromCompositionRule
					/ addCounter + "/"
					+ decompositionRuleTimer.timeIndexedObjectSomeValuesFrom
					/ addCounter + " ms)");

		if (ruleCounter.countObjectIntersectionOfCompositionRule
				+ decompositionRuleCounter.countIndexedObjectIntersectionOfDecompositionRule > 0)
			logger.debug("ObjectIntersectionOf composition/decomposition rules: "
					+ ruleCounter.countObjectIntersectionOfCompositionRule
					+ "/"
					+ decompositionRuleCounter.countIndexedObjectIntersectionOfDecompositionRule
					+ " ("
					+ ruleTimer.timeObjectIntersectionOfCompositionRule
					/ addCounter
					+ "/"
					+ decompositionRuleTimer.timeIndexedObjectIntersectionOf
					/ addCounter + " ms)");

		if (decompositionRuleCounter.countIndexedClassDecompositionRule > 0)
			logger.debug("Class decomposition rules: "
					+ decompositionRuleCounter.countIndexedClassDecompositionRule + " ("
					+ decompositionRuleTimer.timeIndexedClass / addCounter
					+ " ms)");

		logger.debug("Total rule time: "
				+ (ruleTimer.timeContradictionBottomBackwardLinkRule
						+ ruleTimer.timeDisjointnessAxiomCompositionRule
						+ ruleTimer.timeDisjointnessAxiomContradictionRule
						+ ruleTimer.timeForwardLinkBackwardLinkRule
						+ ruleTimer.timeObjectIntersectionOfCompositionRule
						+ ruleTimer.timeObjectSomeValuesFromCompositionRule
						+ ruleTimer.timeOwlThingContextInitializationRule
						+ ruleTimer.timeContextRootInitializationRule
						+ ruleTimer.timePropagationBackwardLinkRule
						+ ruleTimer.timeSubClassOfAxiomCompositionRule
						+ decompositionRuleTimer.timeIndexedClass
						+ decompositionRuleTimer.timeIndexedDataHasValue
						+ decompositionRuleTimer.timeIndexedObjectIntersectionOf + decompositionRuleTimer.timeIndexedObjectSomeValuesFrom)
				/ addCounter + " ms");
	}

	public long getTotalRuleAppCount() {
		return ruleCounter.getTotalRuleAppCount() + decompositionRuleCounter.getTotalRuleAppCount();
	}
}
