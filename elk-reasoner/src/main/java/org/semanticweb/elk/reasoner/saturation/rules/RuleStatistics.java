package org.semanticweb.elk.reasoner.saturation.rules;

import org.semanticweb.elk.reasoner.saturation.rules.backwardlinks.BackwardLinkChainFromBackwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.backwardlinks.ContradictionOverBackwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.backwardlinks.SubsumerBackwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.contextinit.OwlThingContextInitRule;
import org.semanticweb.elk.reasoner.saturation.rules.contextinit.RootContextInitializationRule;
import org.semanticweb.elk.reasoner.saturation.rules.contradiction.ContradictionPropagationRule;
import org.semanticweb.elk.reasoner.saturation.rules.disjointsubsumer.ContradictionCompositionRule;
import org.semanticweb.elk.reasoner.saturation.rules.forwardlink.BackwardLinkFromForwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.forwardlink.NonReflexiveBackwardLinkCompositionRule;
import org.semanticweb.elk.reasoner.saturation.rules.forwardlink.ReflexiveBackwardLinkCompositionRule;
import org.semanticweb.elk.reasoner.saturation.rules.propagations.SubsumerPropagationRule;
import org.semanticweb.elk.reasoner.saturation.rules.subcontextinit.PropagationInitializationRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ComposedFromDecomposedSubsumerRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ContradictionFromNegationRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ContradictionFromOwlNothingRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.DisjointSubsumerFromMemberRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.EquivalentClassFirstFromSecondRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.EquivalentClassSecondFromFirstRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.IndexedClassDecompositionRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.IndexedClassFromDefinitionRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.IndexedObjectComplementOfDecomposition;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.IndexedObjectHasSelfDecomposition;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.IndexedObjectIntersectionOfDecomposition;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.IndexedObjectSomeValuesFromDecomposition;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ObjectIntersectionFromFirstConjunctRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ObjectIntersectionFromSecondConjunctRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ObjectUnionFromDisjunctRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.PropagationFromExistentialFillerRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.SuperClassFromSubClassRule;
import org.semanticweb.elk.util.logging.statistics.AbstractStatistics;
import org.semanticweb.elk.util.logging.statistics.StatisticsPrinter;
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
public class RuleStatistics extends AbstractStatistics {

	// TODO: limit access
	public final RuleCounter ruleCounter = new RuleCounter();
	public final RuleApplicationTimer ruleTimer = new RuleApplicationTimer();

	public synchronized void add(RuleStatistics stats) {
		super.add(stats);
		ruleCounter.add(stats.ruleCounter);
		ruleTimer.add(stats.ruleTimer);
	}

	public long getTotalRuleAppCount() {
		return ruleCounter.getTotalRuleAppCount();
	}

	public double getTotalRuleTime() {
		return getNumberOfMeasurements() == 0 ? 0 : 1d
				* ruleTimer.getTotalRuleAppTime() / getNumberOfMeasurements();
	}

	public void print(Logger logger) {
		if (!logger.isDebugEnabled() || !measurementsTaken())
			return;

		if (ruleCounter.getTotalRuleAppCount() == 0)
			return;

		StatisticsPrinter printer = new StatisticsPrinter(logger,
				"%{RULES:}s %,{count}d [%,{time}d ms]", "TOTAL RULES",
				ruleCounter.getTotalRuleAppCount(),
				ruleTimer.getTotalRuleAppTime());

		// TODO: sort in a better order

		printer.printHeader();

		print(printer, BackwardLinkChainFromBackwardLinkRule.NAME,
				ruleCounter.countBackwardLinkChainFromBackwardLinkRule,
				ruleTimer.timeBackwardLinkChainFromBackwardLinkRule);

		print(printer, BackwardLinkFromForwardLinkRule.NAME,
				ruleCounter.countBackwardLinkFromForwardLinkRule,
				ruleTimer.timeBackwardLinkFromForwardLinkRule);

		print(printer, NonReflexiveBackwardLinkCompositionRule.NAME,
				ruleCounter.countNonReflexiveBackwardLinkCompositionRule,
				ruleTimer.timeNonReflexiveBackwardLinkCompositionRule);

		print(printer, ComposedFromDecomposedSubsumerRule.NAME,
				ruleCounter.countComposedFromDecomposedSubsumerRule,
				ruleTimer.timeComposedFromDecomposedSubsumerRule);

		print(printer, ContradictionCompositionRule.NAME,
				ruleCounter.countContradictionCompositionRule,
				ruleTimer.timeContradictionCompositionRule);

		print(printer, ContradictionFromNegationRule.NAME,
				ruleCounter.countContradictionFromNegationRule,
				ruleTimer.timeContradictionFromNegationRule);

		print(printer, ContradictionFromOwlNothingRule.NAME,
				ruleCounter.countContradictionFromOwlNothingRule,
				ruleTimer.timeContradictionFromOwlNothingRule);

		print(printer, ContradictionOverBackwardLinkRule.NAME,
				ruleCounter.countContradictionOverBackwardLinkRule,
				ruleTimer.timeContradictionOverBackwardLinkRule);

		print(printer, ContradictionPropagationRule.NAME,
				ruleCounter.countContradictionPropagationRule,
				ruleTimer.timeContradictionPropagationRule);

		print(printer, DisjointSubsumerFromMemberRule.NAME,
				ruleCounter.countDisjointSubsumerFromMemberRule,
				ruleTimer.timeDisjointSubsumerFromMemberRule);

		print(printer, IndexedClassDecompositionRule.NAME,
				ruleCounter.countIndexedClassDecompositionRule,
				ruleTimer.timeIndexedClassDecompositionRule);

		print(printer, IndexedObjectComplementOfDecomposition.NAME,
				ruleCounter.countIndexedObjectComplementOfDecomposition,
				ruleTimer.timeIndexedObjectComplementOfDecomposition);

		print(printer, IndexedObjectIntersectionOfDecomposition.NAME,
				ruleCounter.countIndexedObjectIntersectionOfDecomposition,
				ruleTimer.timeIndexedObjectIntersectionOfDecomposition);

		print(printer, IndexedObjectSomeValuesFromDecomposition.NAME,
				ruleCounter.countIndexedObjectSomeValuesFromDecomposition,
				ruleTimer.timeIndexedObjectSomeValuesFromDecomposition);

		print(printer, IndexedObjectHasSelfDecomposition.NAME,
				ruleCounter.countIndexedObjectHasSelfDecomposition,
				ruleTimer.timeIndexedObjectHasSelfDecomposition);

		print(printer, SubsumerPropagationRule.NAME,
				ruleCounter.countSubsumerPropagationRule,
				ruleTimer.timeSubsumerPropagationRule);

		print(printer, ObjectIntersectionFromFirstConjunctRule.NAME,
				ruleCounter.countObjectIntersectionFromFirstConjunctRule,
				ruleTimer.timeObjectIntersectionFromFirstConjunctRule);

		print(printer, ObjectIntersectionFromSecondConjunctRule.NAME,
				ruleCounter.countObjectIntersectionFromSecondConjunctRule,
				ruleTimer.timeObjectIntersectionFromSecondConjunctRule);

		print(printer, ObjectUnionFromDisjunctRule.NAME,
				ruleCounter.countObjectUnionFromDisjunctRule,
				ruleTimer.timeObjectUnionFromDisjunctRule);

		print(printer, OwlThingContextInitRule.NAME,
				ruleCounter.countOwlThingContextInitRule,
				ruleTimer.timeOwlThingContextInitRule);

		print(printer, PropagationFromExistentialFillerRule.NAME,
				ruleCounter.countPropagationFromExistentialFillerRule,
				ruleTimer.timePropagationFromExistentialFillerRule);

		print(printer, RootContextInitializationRule.NAME,
				ruleCounter.countRootContextInitializationRule,
				ruleTimer.timeRootContextInitializationRule);

		print(printer, SubsumerBackwardLinkRule.NAME,
				ruleCounter.countSubsumerBackwardLinkRule,
				ruleTimer.timeSubsumerBackwardLinkRule);

		print(printer, SuperClassFromSubClassRule.NAME,
				ruleCounter.countSuperClassFromSubClassRule,
				ruleTimer.timeSuperClassFromSubClassRule);
		
		print(printer, EquivalentClassFirstFromSecondRule.NAME,
				ruleCounter.countEquivalentClassFirstFromSecondRule,
				ruleTimer.timeEquivalentClassFirstFromSecondRule);
		
		print(printer, EquivalentClassSecondFromFirstRule.NAME,
				ruleCounter.countEquivalentClassSecondFromFirstRule,
				ruleTimer.timeEquivalentClassSecondFromFirstRule);

		print(printer, IndexedClassFromDefinitionRule.NAME,
				ruleCounter.countIndexedClassFromDefinitionRule,
				ruleTimer.timeIndexedClassFromDefinitionRule);

		print(printer, ReflexiveBackwardLinkCompositionRule.NAME,
				ruleCounter.countReflexiveBackwardLinkCompositionRule,
				ruleTimer.timeReflexiveBackwardLinkCompositionRule);

		print(printer, PropagationInitializationRule.NAME,
				ruleCounter.countPropagationInitializationRule,
				ruleTimer.timePropagationInitializationRule);

		printer.printSeparator();

		print(printer, "TOTAL RULES:", ruleCounter.getTotalRuleAppCount(),
				ruleTimer.getTotalRuleAppTime());

		printer.printSeparator();
	}

	void print(StatisticsPrinter printer, String name, long count, long time) {
		if (count == 0)
			return;

		printer.print(name, count, time / getNumberOfMeasurements());

	}

	/**
	 * Reset all timers to zero.
	 */
	@Override
	public void reset() {
		super.reset();
		ruleCounter.reset();
		ruleTimer.reset();
	}
}
