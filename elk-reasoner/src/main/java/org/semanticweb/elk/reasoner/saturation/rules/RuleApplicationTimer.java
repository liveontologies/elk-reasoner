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

import org.semanticweb.elk.reasoner.indexing.hierarchy.DirectIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ContradictionFromDisjointnessRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ObjectIntersectionFromConjunctRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.PropagationFromExistentialFillerRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.DisjointSubsumerFromMemberRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ObjectUnionFromDisjunctRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.SuperClassFromSubClassRule;

/**
 * An object which can be used to measure time spent within a methods of a
 * {@link CompositionRuleVisitor}. The fields of the timer correspond to the
 * methods of {@link CompositionRuleVisitor}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class RuleApplicationTimer {

	/**
	 * timer for {@link IndexedClass.OwlThingContextInitializationRule}
	 */
	int timeOwlThingContextInitializationRule;

	/**
	 * timer for {@link DirectIndex.RootContextInitializationRule}
	 */
	int timeContextRootInitializationRule;

	/**
	 * timer for {@link DisjointSubsumerFromMemberRule}
	 */
	int timeIndexedDisjointnessAxiomCompositionRule;

	/**
	 * timer for {@link ContradictionFromDisjointnessRule}
	 */
	int timeIndexedDisjointnessAxiomContradictionRule;

	/**
	 * timer for {@link ContradictionFromDisjointnessRule}
	 */
	int timeObjectIndexedComplementOfCompositionRule;

	/**
	 * timer for {@link ObjectIntersectionFromConjunctRule}
	 */
	int timeObjectIndexedIntersectionOfCompositionRule;

	/**
	 * timer for {@link SuperClassFromSubClassRule}
	 */
	public int timeIndexedSubClassOfAxiomCompositionRule;

	/**
	 * timer for {@link PropagationFromExistentialFillerRule}
	 */
	int timeIndexedObjectSomeValuesFromCompositionRule;

	/**
	 * timer for {@link ObjectUnionFromDisjunctRule}
	 */
	int timeIndexedObjectUnionOfCompositionRule;

	/**
	 * timer for {@link ForwardLink.ThisBackwardLinkRule}
	 */
	int timeForwardLinkBackwardLinkRule;

	/**
	 * timer for {@link Propagation.SubsumerBackwardLinkRule}
	 */
	int timePropagationBackwardLinkRule;

	/**
	 * timer for {@link Contradiction.ContradictionBackwardLinkRule}
	 */
	int timeContradictionBottomBackwardLinkRule;

	/**
	 * time for {@link BackwardLink.ThisCompositionRule}
	 */
	int timeBackwardLinkCompositionRule;

	/**
	 * time for {@link Contradiction.ContradictionPropagationRule}
	 */
	int timeContradictionCompositionRule;

	/**
	 * time for {@link DisjointSubsumer.ContradicitonCompositionRule}
	 */
	int timeDisjointnessAxiomCompositionRule;

	/**
	 * time for {@link ForwardLink.BackwardLinkCompositionRule}
	 */
	int timeForwardLinkCompositionRule;

	/**
	 * Reset all timers to zero.
	 */
	public void reset() {
		timeOwlThingContextInitializationRule = 0;
		timeContextRootInitializationRule = 0;
		timeIndexedDisjointnessAxiomCompositionRule = 0;
		timeIndexedDisjointnessAxiomContradictionRule = 0;
		timeObjectIndexedComplementOfCompositionRule = 0;
		timeObjectIndexedIntersectionOfCompositionRule = 0;
		timeIndexedSubClassOfAxiomCompositionRule = 0;
		timeIndexedObjectSomeValuesFromCompositionRule = 0;
		timeIndexedObjectUnionOfCompositionRule = 0;
		timeForwardLinkBackwardLinkRule = 0;
		timePropagationBackwardLinkRule = 0;
		timeContradictionBottomBackwardLinkRule = 0;
		timeBackwardLinkCompositionRule = 0;
		timeContradictionCompositionRule = 0;
		timeDisjointnessAxiomCompositionRule = 0;
		timeForwardLinkCompositionRule = 0;

	}

	/**
	 * Add the values the corresponding values of the given timer
	 * 
	 * @param timer
	 */
	public synchronized void add(RuleApplicationTimer timer) {
		timeOwlThingContextInitializationRule += timer.timeOwlThingContextInitializationRule;
		timeContextRootInitializationRule += timer.timeContextRootInitializationRule;
		timeIndexedDisjointnessAxiomCompositionRule += timer.timeIndexedDisjointnessAxiomCompositionRule;
		timeIndexedDisjointnessAxiomContradictionRule += timer.timeIndexedDisjointnessAxiomContradictionRule;
		timeObjectIndexedComplementOfCompositionRule += timer.timeObjectIndexedComplementOfCompositionRule;
		timeObjectIndexedIntersectionOfCompositionRule += timer.timeObjectIndexedIntersectionOfCompositionRule;
		timeIndexedSubClassOfAxiomCompositionRule += timer.timeIndexedSubClassOfAxiomCompositionRule;
		timeIndexedObjectSomeValuesFromCompositionRule += timer.timeIndexedObjectSomeValuesFromCompositionRule;
		timeIndexedObjectUnionOfCompositionRule += timer.timeIndexedObjectUnionOfCompositionRule;
		timeForwardLinkBackwardLinkRule += timer.timeForwardLinkBackwardLinkRule;
		timePropagationBackwardLinkRule += timer.timePropagationBackwardLinkRule;
		timeContradictionBottomBackwardLinkRule += timer.timeContradictionBottomBackwardLinkRule;
		timeBackwardLinkCompositionRule += timeBackwardLinkCompositionRule;
		timeContradictionCompositionRule += timeContradictionCompositionRule;
		timeDisjointnessAxiomCompositionRule += timeDisjointnessAxiomCompositionRule;
		timeForwardLinkCompositionRule += timeForwardLinkCompositionRule;
	}

	public int getTotalRuleAppTime() {
		return timeOwlThingContextInitializationRule
				+ timeContextRootInitializationRule
				+ timeIndexedDisjointnessAxiomCompositionRule
				+ timeIndexedDisjointnessAxiomContradictionRule
				+ timeObjectIndexedComplementOfCompositionRule
				+ timeObjectIndexedIntersectionOfCompositionRule
				+ timeIndexedSubClassOfAxiomCompositionRule
				+ timeIndexedObjectSomeValuesFromCompositionRule
				+ timeIndexedObjectUnionOfCompositionRule
				+ timeForwardLinkBackwardLinkRule
				+ timePropagationBackwardLinkRule
				+ timeContradictionBottomBackwardLinkRule
				+ timeBackwardLinkCompositionRule
				+ timeContradictionCompositionRule
				+ timeDisjointnessAxiomCompositionRule
				+ timeForwardLinkCompositionRule;
	}

}
