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
	final DecompositionRuleApplicationCounter decompositionRuleCounter = new DecompositionRuleApplicationCounter();
	final DecompositionRuleApplicationTimer decompositionRuleTimer = new DecompositionRuleApplicationTimer();

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
	}

	public synchronized void add(RuleStatistics stats) {
		addCounter++;
		decompositionRuleCounter.add(stats.decompositionRuleCounter);
		decompositionRuleTimer.add(stats.decompositionRuleTimer);
		ruleCounter.add(stats.ruleCounter);
		ruleTimer.add(stats.ruleTimer);
	}

	public void print(Logger logger) {
		if (!logger.isDebugEnabled() || addCounter <= 0)
			return;

		if (ruleCounter.countForwardLinkBackwardLinkRule > 0)
			logger.debug("Rule for producing backward links from forward links "
					+ ruleCounter.countForwardLinkBackwardLinkRule
					+ " ("
					+ ruleTimer.timeForwardLinkBackwardLinkRule
					/ addCounter
					+ " ms)");

		if (ruleCounter.countDisjointnessAxiomContradictionRule > 0)
			logger.debug("Disjointness axiom contradiction rule "
					+ ruleCounter.countDisjointnessAxiomContradictionRule
					+ " (" + ruleTimer.timeDisjointnessAxiomContradictionRule
					/ addCounter + " ms)");

		if (ruleCounter.countDisjointnessAxiomCompositionRule > 0)
			logger.debug("Disjointness axiom composition rule "
					+ ruleCounter.countDisjointnessAxiomCompositionRule + " ("
					+ ruleTimer.timeDisjointnessAxiomCompositionRule
					/ addCounter + " ms)");

		if (ruleCounter.countOwlThingContextInitializationRule > 0)
			logger.debug("owl:Thing context init rule "
					+ ruleCounter.countOwlThingContextInitializationRule + " ("
					+ ruleTimer.timeOwlThingContextInitializationRule
					/ addCounter + " ms)");

		if (ruleCounter.countSubClassOfAxiomCompositionRule > 0)
			logger.debug("Subclass rule "
					+ ruleCounter.countSubClassOfAxiomCompositionRule + " ("
					+ ruleTimer.timeSubClassOfAxiomCompositionRule / addCounter
					+ " ms)");

		if (ruleCounter.countContradictionBottomBackwardLinkRule > 0)
			logger.debug("Rule for propagating owl:Nothing "
					+ ruleCounter.countContradictionBottomBackwardLinkRule
					+ " (" + ruleTimer.timeContradictionBottomBackwardLinkRule
					/ addCounter + " ms)");

		if (decompositionRuleCounter.countIndexedClass > 0)
			logger.debug("Class decomposition rule "
					+ decompositionRuleCounter.countIndexedClass + " ("
					+ decompositionRuleTimer.timeIndexedClass / addCounter
					+ " ms)");

		if (ruleCounter.countPropagationBackwardLinkRule > 0)
			logger.debug("Rule for propagation via backward links "
					+ ruleCounter.countPropagationBackwardLinkRule + " ("
					+ ruleTimer.timePropagationBackwardLinkRule / addCounter
					+ " ms)");

		if (decompositionRuleCounter.countIndexedObjectSomeValuesFrom > 0)
			logger.debug("Some values from decomposition rule "
					+ decompositionRuleCounter.countIndexedObjectSomeValuesFrom
					+ " ("
					+ decompositionRuleTimer.timeIndexedObjectSomeValuesFrom
					/ addCounter + " ms)");

		if (decompositionRuleCounter.countIndexedObjectSomeValuesFrom > 0)
			logger.debug("Some values from composition rule "
					+ decompositionRuleCounter.countIndexedObjectSomeValuesFrom
					+ " ("
					+ decompositionRuleTimer.timeIndexedObjectSomeValuesFrom
					/ addCounter + " ms)");

		if (decompositionRuleCounter.countIndexedObjectIntersectionOf > 0)
			logger.debug("Intersection decomposition rule "
					+ decompositionRuleCounter.countIndexedObjectIntersectionOf
					+ " ("
					+ decompositionRuleTimer.timeIndexedObjectIntersectionOf
					/ addCounter + " ms)");

		if (ruleCounter.countObjectIntersectionOfCompositionRule > 0)
			logger.debug("Intersection composition rule "
					+ ruleCounter.countObjectIntersectionOfCompositionRule
					+ " (" + ruleTimer.timeObjectIntersectionOfCompositionRule
					/ addCounter + " ms)");
	}

}
