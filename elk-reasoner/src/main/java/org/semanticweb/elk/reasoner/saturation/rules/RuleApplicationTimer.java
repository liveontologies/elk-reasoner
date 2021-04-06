package org.semanticweb.elk.reasoner.saturation.rules;

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
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.OwlNothingDecompositionRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.PropagationFromExistentialFillerRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.SuperClassFromSubClassRule;

/**
 * An object which can be used to measure time spent within a methods of a
 * {@link RuleVisitor}. The fields of the timer correspond to the methods of
 * {@link RuleVisitor}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class RuleApplicationTimer {

	/**
	 * timer for {@link BackwardLinkChainFromBackwardLinkRule}
	 */
	long timeBackwardLinkChainFromBackwardLinkRule;

	/**
	 * timer for {@link BackwardLinkFromForwardLinkRule}
	 */
	long timeBackwardLinkFromForwardLinkRule;

	/**
	 * timer for {@link ComposedFromDecomposedSubsumerRule}
	 */
	long timeComposedFromDecomposedSubsumerRule;

	/**
	 * time for {@link ContradictionCompositionRule}
	 */
	long timeContradictionCompositionRule;

	/**
	 * timer for {@link ContradictionFromNegationRule}
	 */
	long timeContradictionFromNegationRule;

	/**
	 * timer for {@link ContradictionFromOwlNothingRule}
	 */
	long timeContradictionFromOwlNothingRule;

	/**
	 * timer for {@link ContradictionOverBackwardLinkRule}
	 */
	long timeContradictionOverBackwardLinkRule;

	/**
	 * time for {@link ContradictionPropagationRule}
	 */
	long timeContradictionPropagationRule;

	/**
	 * timer for {@link DisjointSubsumerFromMemberRule}
	 */
	long timeDisjointSubsumerFromMemberRule;

	/**
	 * timer for {@link IndexedClassDecompositionRule}
	 */
	long timeIndexedClassDecompositionRule;

	/**
	 * timer for {@link IndexedClassFromDefinitionRule}
	 */
	long timeIndexedClassFromDefinitionRule;

	/**
	 * timer for {@link IndexedObjectComplementOfDecomposition}
	 */
	long timeIndexedObjectComplementOfDecomposition;

	/**
	 * timer for {@link IndexedObjectHasSelfDecomposition}
	 */
	long timeIndexedObjectHasSelfDecomposition;

	/**
	 * time for {@link IndexedObjectIntersectionOfDecomposition}
	 */
	long timeIndexedObjectIntersectionOfDecomposition;

	/**
	 * time for {@link IndexedObjectSomeValuesFromDecomposition}
	 */
	long timeIndexedObjectSomeValuesFromDecomposition;

	/**
	 * time for {@link NonReflexiveBackwardLinkCompositionRule}
	 */
	long timeNonReflexiveBackwardLinkCompositionRule;

	/**
	 * timer for {@link ObjectIntersectionFromFirstConjunctRule}
	 */
	long timeObjectIntersectionFromFirstConjunctRule;

	/**
	 * timer for {@link ObjectIntersectionFromSecondConjunctRule}
	 */
	long timeObjectIntersectionFromSecondConjunctRule;

	/**
	 * timer for {@link ObjectUnionFromDisjunctRule}
	 */
	long timeObjectUnionFromDisjunctRule;

	/**
	 * timer for {@link OwlNothingDecompositionRule}
	 */
	long timeOwlNothingDecompositionRule;
	
	/**
	 * timer for {@link OwlThingContextInitRule}
	 */
	long timeOwlThingContextInitRule;

	/**
	 * timer for {@link PropagationFromExistentialFillerRule}
	 */
	long timePropagationFromExistentialFillerRule;

	/**
	 * timer for {@link PropagationInitializationRule}
	 */
	long timePropagationInitializationRule;

	/**
	 * timer for {@link ReflexiveBackwardLinkCompositionRule}
	 */
	long timeReflexiveBackwardLinkCompositionRule;

	/**
	 * timer for {@link RootContextInitializationRule}
	 */
	long timeRootContextInitializationRule;

	/**
	 * timer for {@link SubsumerBackwardLinkRule}
	 */
	long timeSubsumerBackwardLinkRule;

	/**
	 * timer for {@link SubsumerPropagationRule}
	 */
	long timeSubsumerPropagationRule;

	/**
	 * timer for {@link SuperClassFromSubClassRule}
	 */
	long timeSuperClassFromSubClassRule;

	/**
	 * timer for {@link EquivalentClassFirstFromSecondRule}
	 */
	long timeEquivalentClassFirstFromSecondRule;

	/**
	 * timer for {@link EquivalentClassSecondFromFirstRule}
	 */
	long timeEquivalentClassSecondFromFirstRule;


	/**
	 * Add the values the corresponding values of the given timer
	 * 
	 * @param timer
	 */
	public synchronized void add(RuleApplicationTimer timer) {
		timeOwlThingContextInitRule += timer.timeOwlThingContextInitRule;
		timeRootContextInitializationRule += timer.timeRootContextInitializationRule;
		timeDisjointSubsumerFromMemberRule += timer.timeDisjointSubsumerFromMemberRule;
		timeContradictionFromNegationRule += timer.timeContradictionFromNegationRule;
		timeObjectIntersectionFromFirstConjunctRule += timer.timeObjectIntersectionFromFirstConjunctRule;
		timeObjectIntersectionFromSecondConjunctRule += timer.timeObjectIntersectionFromSecondConjunctRule;
		timeSuperClassFromSubClassRule += timer.timeSuperClassFromSubClassRule;
		timePropagationFromExistentialFillerRule += timer.timePropagationFromExistentialFillerRule;
		timeObjectUnionFromDisjunctRule += timer.timeObjectUnionFromDisjunctRule;
		timeOwlNothingDecompositionRule += timer.timeOwlNothingDecompositionRule;
		timeBackwardLinkChainFromBackwardLinkRule += timer.timeBackwardLinkChainFromBackwardLinkRule;
		timeReflexiveBackwardLinkCompositionRule += timer.timeReflexiveBackwardLinkCompositionRule;
		timeNonReflexiveBackwardLinkCompositionRule += timer.timeNonReflexiveBackwardLinkCompositionRule;
		timeSubsumerBackwardLinkRule += timer.timeSubsumerBackwardLinkRule;
		timeContradictionOverBackwardLinkRule += timer.timeContradictionOverBackwardLinkRule;
		timeContradictionPropagationRule += timer.timeContradictionPropagationRule;
		timeContradictionCompositionRule += timer.timeContradictionCompositionRule;
		timeIndexedObjectIntersectionOfDecomposition += timer.timeIndexedObjectIntersectionOfDecomposition;
		timeIndexedObjectSomeValuesFromDecomposition += timer.timeIndexedObjectSomeValuesFromDecomposition;
		timeIndexedObjectComplementOfDecomposition += timer.timeIndexedObjectComplementOfDecomposition;
		timeIndexedObjectHasSelfDecomposition += timer.timeIndexedObjectHasSelfDecomposition;
		timeContradictionFromOwlNothingRule += timer.timeContradictionFromOwlNothingRule;
		timeSubsumerPropagationRule += timer.timeSubsumerPropagationRule;
		timePropagationInitializationRule += timer.timePropagationInitializationRule;
		timeBackwardLinkFromForwardLinkRule += timer.timeBackwardLinkFromForwardLinkRule;
		timeComposedFromDecomposedSubsumerRule += timer.timeComposedFromDecomposedSubsumerRule;
		timeIndexedClassDecompositionRule += timer.timeIndexedClassDecompositionRule;
		timeIndexedClassFromDefinitionRule += timer.timeIndexedClassFromDefinitionRule;
		timeEquivalentClassFirstFromSecondRule += timer.timeEquivalentClassFirstFromSecondRule;
		timeEquivalentClassSecondFromFirstRule += timer.timeEquivalentClassSecondFromFirstRule;
	}

	public long getTotalRuleAppTime() {
		return timeOwlThingContextInitRule + timeRootContextInitializationRule
				+ timeDisjointSubsumerFromMemberRule
				+ timeContradictionFromNegationRule
				+ timeObjectIntersectionFromFirstConjunctRule
				+ timeObjectIntersectionFromSecondConjunctRule
				+ timeSuperClassFromSubClassRule
				+ timePropagationFromExistentialFillerRule
				+ timeObjectUnionFromDisjunctRule
				+ timeOwlNothingDecompositionRule
				+ timeBackwardLinkChainFromBackwardLinkRule
				+ timeSubsumerBackwardLinkRule
				+ timeContradictionOverBackwardLinkRule
				+ timeContradictionPropagationRule
				+ timeContradictionCompositionRule
				+ timeNonReflexiveBackwardLinkCompositionRule
				+ timeReflexiveBackwardLinkCompositionRule
				+ timeIndexedObjectIntersectionOfDecomposition
				+ timeIndexedObjectSomeValuesFromDecomposition
				+ timeIndexedObjectComplementOfDecomposition
				+ timeIndexedObjectHasSelfDecomposition
				+ timeContradictionFromOwlNothingRule
				+ timeSubsumerPropagationRule
				+ timePropagationInitializationRule
				+ timeBackwardLinkFromForwardLinkRule
				+ timeComposedFromDecomposedSubsumerRule
				+ timeIndexedClassDecompositionRule
				+ timeIndexedClassFromDefinitionRule
				+ timeEquivalentClassFirstFromSecondRule
				+ timeEquivalentClassSecondFromFirstRule;
	}

	/**
	 * Reset all timers to zero.
	 */
	public void reset() {
		timeOwlThingContextInitRule = 0;
		timeRootContextInitializationRule = 0;
		timeDisjointSubsumerFromMemberRule = 0;
		timeContradictionFromNegationRule = 0;
		timeObjectIntersectionFromFirstConjunctRule = 0;
		timeObjectIntersectionFromSecondConjunctRule = 0;
		timeSuperClassFromSubClassRule = 0;
		timePropagationFromExistentialFillerRule = 0;		
		timeObjectUnionFromDisjunctRule = 0;
		timeOwlNothingDecompositionRule = 0;
		timeBackwardLinkChainFromBackwardLinkRule = 0;
		timeSubsumerBackwardLinkRule = 0;
		timeContradictionOverBackwardLinkRule = 0;
		timeContradictionPropagationRule = 0;
		timeContradictionCompositionRule = 0;
		timeNonReflexiveBackwardLinkCompositionRule = 0;
		timeReflexiveBackwardLinkCompositionRule = 0;
		timeIndexedObjectIntersectionOfDecomposition = 0;
		timeIndexedObjectSomeValuesFromDecomposition = 0;
		timeIndexedObjectComplementOfDecomposition = 0;
		timeIndexedObjectHasSelfDecomposition = 0;
		timeContradictionFromOwlNothingRule = 0;
		timeSubsumerPropagationRule = 0;
		timePropagationInitializationRule = 0;
		timeBackwardLinkFromForwardLinkRule = 0;
		timeComposedFromDecomposedSubsumerRule = 0;
		timeIndexedClassDecompositionRule = 0;
		timeIndexedClassFromDefinitionRule = 0;
		timeEquivalentClassFirstFromSecondRule = 0;
		timeEquivalentClassSecondFromFirstRule = 0;
	}

}
