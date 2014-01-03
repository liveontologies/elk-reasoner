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
	public final RuleCounter ruleCounter = new RuleCounter();
	public final RuleApplicationTimer ruleTimer = new RuleApplicationTimer();

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
		ruleCounter.reset();
		ruleTimer.reset();
		numOfMeasurements_ = 0;
	}

	public synchronized void add(RuleStatistics stats) {
		if (stats.measurementsTaken()) {
			numOfMeasurements_ += stats.numOfMeasurements_;
			ruleCounter.add(stats.ruleCounter);
			ruleTimer.add(stats.ruleTimer);
		}
	}

	// TODO: can use rule names for printing
	public void print(Logger logger) {
		if (!logger.isDebugEnabled() || !measurementsTaken())
			return;

		if (ruleCounter.countBackwardLinkChainFromBackwardLinkRule > 0)
			logger.debug("Forward link from backward link rules: "
					+ ruleCounter.countBackwardLinkChainFromBackwardLinkRule
					+ " ("
					+ ruleTimer.timeBackwardLinkChainFromBackwardLinkRule
					/ numOfMeasurements_ + " ms)");

		if (ruleCounter.countContradictionFromDisjointnessRule > 0)
			logger.debug("Disjointness axiom contradiction rules: "
					+ ruleCounter.countContradictionFromDisjointnessRule + " ("
					+ ruleTimer.timeContradictionFromDisjointnessRule
					/ numOfMeasurements_ + " ms)");

		if (ruleCounter.countDisjointSubsumerFromMemberRule > 0)
			logger.debug("Disjointness axiom composition rules: "
					+ ruleCounter.countDisjointSubsumerFromMemberRule + " ("
					+ ruleTimer.timeDisjointSubsumerFromMemberRule
					/ numOfMeasurements_ + " ms)");

		if (ruleCounter.countOwlThingContextInitRule > 0)
			logger.debug("owl:Thing context init rules: "
					+ ruleCounter.countOwlThingContextInitRule + " ("
					+ ruleTimer.timeOwlThingContextInitRule
					/ numOfMeasurements_ + " ms)");

		if (ruleCounter.countRootContextInitializationRule > 0)
			logger.debug("Context root init rules: "
					+ ruleCounter.countRootContextInitializationRule + " ("
					+ ruleTimer.timeRootContextInitializationRule
					/ numOfMeasurements_ + " ms)");

		if (ruleCounter.countSuperClassFromSubClassRule > 0)
			logger.debug("Subclass expansions: "
					+ ruleCounter.countSuperClassFromSubClassRule + " ("
					+ ruleTimer.timeSuperClassFromSubClassRule
					/ numOfMeasurements_ + " ms)");

		if (ruleCounter.countContradictionOverBackwardLinkRule > 0)
			logger.debug("Propagations of inconsistency: "
					+ ruleCounter.countContradictionOverBackwardLinkRule + " ("
					+ ruleTimer.timeContradictionOverBackwardLinkRule
					/ numOfMeasurements_ + " ms)");

		if (ruleCounter.countSubsumerBackwardLinkRule > 0)
			logger.debug("Propagations via backward links: "
					+ ruleCounter.countSubsumerBackwardLinkRule + " ("
					+ ruleTimer.timeSubsumerBackwardLinkRule
					/ numOfMeasurements_ + " ms)");

		if (ruleCounter.countPropagationFromExistentialFillerRule
				+ ruleCounter.countIndexedObjectSomeValuesFromDecomposition > 0)
			logger.debug("ObjectSomeValuesFrom composition/decomposition rules: "
					+ ruleCounter.countPropagationFromExistentialFillerRule
					+ "/"
					+ ruleCounter.countIndexedObjectSomeValuesFromDecomposition
					+ " ("
					+ ruleTimer.timePropagationFromExistentialFillerRule
					/ numOfMeasurements_
					+ "/"
					+ ruleTimer.timeIndexedObjectSomeValuesFromDecomposition
					/ numOfMeasurements_ + " ms)");

		if (ruleCounter.countObjectIntersectionFromConjunctRule
				+ ruleCounter.countIndexedObjectIntersectionOfDecomposition > 0)
			logger.debug("ObjectIntersectionOf composition/decomposition rules: "
					+ ruleCounter.countObjectIntersectionFromConjunctRule
					+ "/"
					+ ruleCounter.countIndexedObjectIntersectionOfDecomposition
					+ " ("
					+ ruleTimer.timeObjectIntersectionFromConjunctRule
					/ numOfMeasurements_
					+ "/"
					+ ruleTimer.timeIndexedObjectIntersectionOfDecomposition
					/ numOfMeasurements_ + " ms)");

		if (ruleCounter.countContradictionFromNegationRule
				+ ruleCounter.countIndexedObjectComplementOfDecomposition > 0)
			logger.debug("ObjectComplementOf composition/decomposition rules: "
					+ ruleCounter.countContradictionFromNegationRule + "/"
					+ ruleCounter.countIndexedObjectComplementOfDecomposition
					+ " (" + ruleTimer.timeContradictionFromNegationRule
					/ numOfMeasurements_ + "/"
					+ ruleTimer.timeIndexedObjectComplementOfDecomposition
					/ numOfMeasurements_ + " ms)");

		if (ruleCounter.countObjectUnionFromDisjunctRule > 0)
			logger.debug("ObjectUnionOf composition rules: "
					+ ruleCounter.countObjectUnionFromDisjunctRule + " ("
					+ ruleTimer.timeObjectUnionFromDisjunctRule
					/ numOfMeasurements_ + " ms)");

		logger.debug("Total rule time: "
				+ (ruleTimer.timeContradictionOverBackwardLinkRule
						+ ruleTimer.timeDisjointSubsumerFromMemberRule
						+ ruleTimer.timeContradictionFromDisjointnessRule
						+ ruleTimer.timeBackwardLinkChainFromBackwardLinkRule
						+ ruleTimer.timeObjectIntersectionFromConjunctRule
						+ ruleTimer.timePropagationFromExistentialFillerRule
						+ ruleTimer.timeContradictionFromNegationRule
						+ ruleTimer.timeObjectUnionFromDisjunctRule
						+ ruleTimer.timeOwlThingContextInitRule
						+ ruleTimer.timeRootContextInitializationRule
						+ ruleTimer.timeSubsumerBackwardLinkRule
						+ ruleTimer.timeSuperClassFromSubClassRule
						/ numOfMeasurements_ + " ms"));
	}

	public long getTotalRuleAppCount() {
		return ruleCounter.getTotalRuleAppCount();
	}

	public double getTotalRuleTime() {
		return numOfMeasurements_ == 0 ? 0 : 1d
				* ruleTimer.getTotalRuleAppTime() / numOfMeasurements_;
	}
}
