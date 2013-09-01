package org.semanticweb.elk.reasoner.saturation.rules;

import org.slf4j.Logger;

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
	 * The number of times measurements were taken in different threads. Used to
	 * average the wall time results.
	 */
	private int numOfMeasurements_ = 0;

	public void startMeasurements() {
		if (numOfMeasurements_ < 1) {
			numOfMeasurements_ = 1;
		}
	}

	private boolean measurementsTaken() {
		return numOfMeasurements_ > 0;
	}

	/**
	 * Reset all timers to zero.
	 */
	public void reset() {
		decompositionRuleCounter.reset();
		decompositionRuleTimer.reset();
		ruleCounter.reset();
		ruleTimer.reset();
		numOfMeasurements_ = 0;
	}

	public synchronized void add(RuleStatistics stats) {
		if (stats.measurementsTaken()) {
			numOfMeasurements_ += stats.numOfMeasurements_;
			decompositionRuleCounter.add(stats.decompositionRuleCounter);
			decompositionRuleTimer.add(stats.decompositionRuleTimer);
			ruleCounter.add(stats.ruleCounter);
			ruleTimer.add(stats.ruleTimer);
		}
	}

	// TODO: can use rule names for printing
	public void print(Logger logger) {
		if (!logger.isDebugEnabled() || !measurementsTaken())
			return;

		if (ruleCounter.countForwardLinkBackwardLinkRule > 0)
			logger.debug("Forward link from backward link rules: "
					+ ruleCounter.countForwardLinkBackwardLinkRule + " ("
					+ ruleTimer.timeForwardLinkBackwardLinkRule
					/ numOfMeasurements_ + " ms)");

		if (ruleCounter.countDisjointnessAxiomContradictionRule > 0)
			logger.debug("Disjointness axiom contradiction rules: "
					+ ruleCounter.countDisjointnessAxiomContradictionRule
					+ " (" + ruleTimer.timeDisjointnessAxiomContradictionRule
					/ numOfMeasurements_ + " ms)");

		if (ruleCounter.countDisjointnessAxiomCompositionRule > 0)
			logger.debug("Disjointness axiom composition rules: "
					+ ruleCounter.countDisjointnessAxiomCompositionRule + " ("
					+ ruleTimer.timeDisjointnessAxiomCompositionRule
					/ numOfMeasurements_ + " ms)");

		if (ruleCounter.countOwlThingContextInitializationRule > 0)
			logger.debug("owl:Thing context init rules: "
					+ ruleCounter.countOwlThingContextInitializationRule + " ("
					+ ruleTimer.timeOwlThingContextInitializationRule
					/ numOfMeasurements_ + " ms)");

		if (ruleCounter.countContextRootInitializationRule > 0)
			logger.debug("Context root init rules: "
					+ ruleCounter.countContextRootInitializationRule + " ("
					+ ruleTimer.timeContextRootInitializationRule
					/ numOfMeasurements_ + " ms)");

		if (ruleCounter.countSubClassOfAxiomCompositionRule > 0)
			logger.debug("Subclass expansions: "
					+ ruleCounter.countSubClassOfAxiomCompositionRule + " ("
					+ ruleTimer.timeSubClassOfAxiomCompositionRule
					/ numOfMeasurements_ + " ms)");

		if (ruleCounter.countContradictionBottomBackwardLinkRule > 0)
			logger.debug("Propagations of inconsistency: "
					+ ruleCounter.countContradictionBottomBackwardLinkRule
					+ " (" + ruleTimer.timeContradictionBottomBackwardLinkRule
					/ numOfMeasurements_ + " ms)");

		if (ruleCounter.countPropagationBackwardLinkRule > 0)
			logger.debug("Propagations via backward links: "
					+ ruleCounter.countPropagationBackwardLinkRule + " ("
					+ ruleTimer.timePropagationBackwardLinkRule
					/ numOfMeasurements_ + " ms)");

		if (ruleCounter.countObjectSomeValuesFromCompositionRule
				+ decompositionRuleCounter.countIndexedObjectSomeValuesFromDecompositionRule > 0)
			logger.debug("ObjectSomeValuesFrom composition/decomposition rules: "
					+ ruleCounter.countObjectSomeValuesFromCompositionRule
					+ "/"
					+ decompositionRuleCounter.countIndexedObjectSomeValuesFromDecompositionRule
					+ " ("
					+ ruleTimer.timeObjectSomeValuesFromCompositionRule
					/ numOfMeasurements_
					+ "/"
					+ decompositionRuleTimer.timeIndexedObjectSomeValuesFrom
					/ numOfMeasurements_ + " ms)");

		if (ruleCounter.countObjectIntersectionOfCompositionRule
				+ decompositionRuleCounter.countIndexedObjectIntersectionOfDecompositionRule > 0)
			logger.debug("ObjectIntersectionOf composition/decomposition rules: "
					+ ruleCounter.countObjectIntersectionOfCompositionRule
					+ "/"
					+ decompositionRuleCounter.countIndexedObjectIntersectionOfDecompositionRule
					+ " ("
					+ ruleTimer.timeObjectIntersectionOfCompositionRule
					/ numOfMeasurements_
					+ "/"
					+ decompositionRuleTimer.timeIndexedObjectIntersectionOf
					/ numOfMeasurements_ + " ms)");

		if (ruleCounter.countObjectComplementOfCompositionRule
				+ decompositionRuleCounter.countIndexedObjectComplementOfDecompositionRule > 0)
			logger.debug("ObjectComplementOf composition/decomposition rules: "
					+ ruleCounter.countObjectComplementOfCompositionRule
					+ "/"
					+ decompositionRuleCounter.countIndexedObjectComplementOfDecompositionRule
					+ " (" + ruleTimer.timeObjectComplementOfCompositionRule
					/ numOfMeasurements_ + "/"
					+ decompositionRuleTimer.timeIndexedObjectComplementOf
					/ numOfMeasurements_ + " ms)");

		if (ruleCounter.countObjectUnionOfCompositionRule > 0)
			logger.debug("ObjectUnionOf composition rules: "
					+ ruleCounter.countObjectUnionOfCompositionRule + " ("
					+ ruleTimer.timeObjectUnionOfCompositionRule
					/ numOfMeasurements_ + " ms)");

		if (decompositionRuleCounter.countIndexedClassDecompositionRule > 0)
			logger.debug("Class decomposition rules: "
					+ decompositionRuleCounter.countIndexedClassDecompositionRule
					+ " (" + decompositionRuleTimer.timeIndexedClass
					/ numOfMeasurements_ + " ms)");

		logger.debug("Total rule time: "
				+ (ruleTimer.timeContradictionBottomBackwardLinkRule
						+ ruleTimer.timeDisjointnessAxiomCompositionRule
						+ ruleTimer.timeDisjointnessAxiomContradictionRule
						+ ruleTimer.timeForwardLinkBackwardLinkRule
						+ ruleTimer.timeObjectIntersectionOfCompositionRule
						+ ruleTimer.timeObjectSomeValuesFromCompositionRule
						+ ruleTimer.timeObjectComplementOfCompositionRule
						+ ruleTimer.timeObjectUnionOfCompositionRule
						+ ruleTimer.timeOwlThingContextInitializationRule
						+ ruleTimer.timeContextRootInitializationRule
						+ ruleTimer.timePropagationBackwardLinkRule
						+ ruleTimer.timeSubClassOfAxiomCompositionRule
						+ decompositionRuleTimer.timeIndexedClass
						+ decompositionRuleTimer.timeIndexedDataHasValue
						+ decompositionRuleTimer.timeIndexedObjectIntersectionOf
						+ decompositionRuleTimer.timeIndexedObjectSomeValuesFrom + decompositionRuleTimer.timeIndexedObjectComplementOf)
				/ numOfMeasurements_ + " ms");
	}

	public long getTotalRuleAppCount() {
		return ruleCounter.getTotalRuleAppCount()
				+ decompositionRuleCounter.getTotalRuleAppCount();
	}

	public double getTotalRuleTime() {
		double compTotal = numOfMeasurements_ == 0 ? 0 : 1d
				* ruleTimer.getTotalRuleAppTime() / numOfMeasurements_;
		double decompTotal = numOfMeasurements_ == 0 ? 0 : 1d
				* decompositionRuleTimer.getTotalRuleAppTime()
				/ numOfMeasurements_;

		return compTotal + decompTotal;
	}
}
