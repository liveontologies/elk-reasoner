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
import org.semanticweb.elk.reasoner.saturation.rules.contextinit.ReflexivePropertyRangesContextInitRule;
import org.semanticweb.elk.reasoner.saturation.rules.contextinit.RootContextInitializationRule;
import org.semanticweb.elk.reasoner.saturation.rules.contradiction.ContradictionPropagationRule;
import org.semanticweb.elk.reasoner.saturation.rules.disjointsubsumer.ContradictionCompositionRule;
import org.semanticweb.elk.reasoner.saturation.rules.forwardlink.BackwardLinkFromForwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.forwardlink.NonReflexiveBackwardLinkCompositionRule;
import org.semanticweb.elk.reasoner.saturation.rules.forwardlink.ReflexiveBackwardLinkCompositionRule;
import org.semanticweb.elk.reasoner.saturation.rules.propagations.NonReflexivePropagationRule;
import org.semanticweb.elk.reasoner.saturation.rules.propagations.ReflexivePropagationRule;
import org.semanticweb.elk.reasoner.saturation.rules.subcontextinit.PropagationInitializationRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ContradictionFromDisjointnessRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ContradictionFromNegationRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ContradictionFromOwlNothingRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.DisjointSubsumerFromMemberRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.IndexedObjectComplementOfDecomposition;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.IndexedObjectIntersectionOfDecomposition;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.IndexedObjectSomeValuesFromDecomposition;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ObjectIntersectionFromConjunctRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ObjectUnionFromDisjunctRule;
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
	 * time for {@link ContradictionCompositionRule}
	 */
	long timeContradicitonCompositionRule;

	/**
	 * timer for {@link ContradictionFromDisjointnessRule}
	 */
	long timeContradictionFromDisjointnessRule;

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
	 * timer for {@link IndexedObjectComplementOfDecomposition}
	 */
	long timeIndexedObjectComplementOfDecomposition;

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
	 * timer for {@link NonReflexivePropagationRule}
	 */
	long timeNonReflexivePropagationRule;

	/**
	 * timer for {@link ObjectIntersectionFromConjunctRule}
	 */
	long timeObjectIntersectionFromConjunctRule;

	/**
	 * timer for {@link ObjectUnionFromDisjunctRule}
	 */
	long timeObjectUnionFromDisjunctRule;

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
	 * timer for {@link ReflexivePropagationRule}
	 */
	long timeReflexivePropagationRule;

	/**
	 * timer for {@link RootContextInitializationRule}
	 */
	long timeRootContextInitializationRule;

	/**
	 * timer for {@link SubsumerBackwardLinkRule}
	 */
	long timeSubsumerBackwardLinkRule;

	/**
	 * timer for {@link SuperClassFromSubClassRule}
	 */
	long timeSuperClassFromSubClassRule;

	/**
	 * timer for {@link ReflexivePropertyRangesContextInitRule}
	 */
	long timeReflexivePropertyRangesContextInitRule;

	/**
	 * Add the values the corresponding values of the given timer
	 * 
	 * @param timer
	 */
	public synchronized void add(RuleApplicationTimer timer) {
		timeOwlThingContextInitRule += timer.timeOwlThingContextInitRule;
		timeRootContextInitializationRule += timer.timeRootContextInitializationRule;
		timeDisjointSubsumerFromMemberRule += timer.timeDisjointSubsumerFromMemberRule;
		timeContradictionFromDisjointnessRule += timer.timeContradictionFromDisjointnessRule;
		timeContradictionFromNegationRule += timer.timeContradictionFromNegationRule;
		timeObjectIntersectionFromConjunctRule += timer.timeObjectIntersectionFromConjunctRule;
		timeSuperClassFromSubClassRule += timer.timeSuperClassFromSubClassRule;
		timePropagationFromExistentialFillerRule += timer.timePropagationFromExistentialFillerRule;
		timeObjectUnionFromDisjunctRule += timer.timeObjectUnionFromDisjunctRule;
		timeBackwardLinkChainFromBackwardLinkRule += timer.timeBackwardLinkChainFromBackwardLinkRule;
		timeReflexiveBackwardLinkCompositionRule += timer.timeReflexiveBackwardLinkCompositionRule;
		timeNonReflexiveBackwardLinkCompositionRule += timer.timeNonReflexiveBackwardLinkCompositionRule;
		timeSubsumerBackwardLinkRule += timer.timeSubsumerBackwardLinkRule;
		timeContradictionOverBackwardLinkRule += timer.timeContradictionOverBackwardLinkRule;
		timeContradictionPropagationRule += timer.timeContradictionPropagationRule;
		timeContradicitonCompositionRule += timer.timeContradicitonCompositionRule;
		timeIndexedObjectIntersectionOfDecomposition += timer.timeIndexedObjectIntersectionOfDecomposition;
		timeIndexedObjectSomeValuesFromDecomposition += timer.timeIndexedObjectSomeValuesFromDecomposition;
		timeIndexedObjectComplementOfDecomposition += timer.timeIndexedObjectComplementOfDecomposition;
		timeContradictionFromOwlNothingRule += timer.timeContradictionFromOwlNothingRule;
		timeNonReflexivePropagationRule += timer.timeNonReflexivePropagationRule;
		timeReflexivePropagationRule += timer.timeReflexivePropagationRule;
		timePropagationInitializationRule += timer.timePropagationInitializationRule;
		timeBackwardLinkFromForwardLinkRule += timer.timeBackwardLinkFromForwardLinkRule;
		timeReflexivePropertyRangesContextInitRule += timer.timeReflexivePropertyRangesContextInitRule;
	}

	public long getTotalRuleAppTime() {
		return timeOwlThingContextInitRule + timeRootContextInitializationRule
				+ timeDisjointSubsumerFromMemberRule
				+ timeContradictionFromDisjointnessRule
				+ timeContradictionFromNegationRule
				+ timeObjectIntersectionFromConjunctRule
				+ timeSuperClassFromSubClassRule
				+ timePropagationFromExistentialFillerRule
				+ timeObjectUnionFromDisjunctRule
				+ timeBackwardLinkChainFromBackwardLinkRule
				+ timeSubsumerBackwardLinkRule
				+ timeContradictionOverBackwardLinkRule
				+ timeContradictionPropagationRule
				+ timeContradicitonCompositionRule
				+ timeNonReflexiveBackwardLinkCompositionRule
				+ timeReflexiveBackwardLinkCompositionRule
				+ timeIndexedObjectIntersectionOfDecomposition
				+ timeIndexedObjectSomeValuesFromDecomposition
				+ timeIndexedObjectComplementOfDecomposition
				+ timeContradictionFromOwlNothingRule
				+ timeNonReflexivePropagationRule
				+ timeReflexivePropagationRule
				+ timePropagationInitializationRule
				+ timeBackwardLinkFromForwardLinkRule
				+ timeReflexivePropertyRangesContextInitRule;
	}

	/**
	 * Reset all timers to zero.
	 */
	public void reset() {
		timeOwlThingContextInitRule = 0;
		timeRootContextInitializationRule = 0;
		timeDisjointSubsumerFromMemberRule = 0;
		timeContradictionFromDisjointnessRule = 0;
		timeContradictionFromNegationRule = 0;
		timeObjectIntersectionFromConjunctRule = 0;
		timeSuperClassFromSubClassRule = 0;
		timePropagationFromExistentialFillerRule = 0;
		timeObjectUnionFromDisjunctRule = 0;
		timeBackwardLinkChainFromBackwardLinkRule = 0;
		timeSubsumerBackwardLinkRule = 0;
		timeContradictionOverBackwardLinkRule = 0;
		timeContradictionPropagationRule = 0;
		timeContradicitonCompositionRule = 0;
		timeNonReflexiveBackwardLinkCompositionRule = 0;
		timeReflexiveBackwardLinkCompositionRule = 0;
		timeIndexedObjectIntersectionOfDecomposition = 0;
		timeIndexedObjectSomeValuesFromDecomposition = 0;
		timeIndexedObjectComplementOfDecomposition = 0;
		timeContradictionFromOwlNothingRule = 0;
		timeNonReflexivePropagationRule = 0;
		timeReflexivePropagationRule = 0;
		timePropagationInitializationRule = 0;
		timeBackwardLinkFromForwardLinkRule = 0;
		timeReflexivePropertyRangesContextInitRule = 0;
	}

}
