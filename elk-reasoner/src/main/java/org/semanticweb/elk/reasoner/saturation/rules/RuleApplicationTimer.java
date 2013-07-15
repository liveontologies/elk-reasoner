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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointnessAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;

/**
 * An object which can be used to measure time spent within a methods of a
 * {@link RuleApplicationVisitor}. The fields of the timer correspond to the
 * methods of {@link RuleApplicationVisitor}.
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
	 * timer for {@link DirectIndex.ContextRootInitializationRule}
	 */
	int timeContextRootInitializationRule;

	/**
	 * timer for {@link IndexedDisjointnessAxiom.ThisCompositionRule}
	 */
	int timeDisjointnessAxiomCompositionRule;

	/**
	 * timer for {@link IndexedDisjointnessAxiom.ThisContradictionRule}
	 */
	int timeDisjointnessAxiomContradictionRule;

	/**
	 * timer for {@link IndexedObjectComplementOf.ThisCompositionRule}
	 */
	int timeObjectComplementOfCompositionRule;
	
	/**
	 * timer for {@link IndexedObjectIntersectionOf.ThisCompositionRule}
	 */
	int timeObjectIntersectionOfCompositionRule;

	/**
	 * timer for {@link IndexedSubClassOfAxiom.ThisCompositionRule}
	 */
	public int timeSubClassOfAxiomCompositionRule;

	/**
	 * timer for {@link IndexedObjectSomeValuesFrom.ThisCompositionRule}
	 */
	int timeObjectSomeValuesFromCompositionRule;

	/**
	 * timer for {@link ForwardLink.ThisBackwardLinkRule}
	 */
	int timeForwardLinkBackwardLinkRule;

	/**
	 * timer for {@link Propagation.ThisBackwardLinkRule}
	 */
	int timePropagationBackwardLinkRule;

	/**
	 * timer for {@link Contradiction.ContradictionBackwardLinkRule}
	 */
	int timeContradictionBottomBackwardLinkRule;

	/**
	 * Reset all timers to zero.
	 */
	public void reset() {
		timeOwlThingContextInitializationRule = 0;
		timeContextRootInitializationRule = 0;
		timeDisjointnessAxiomCompositionRule = 0;
		timeDisjointnessAxiomContradictionRule = 0;
		timeObjectComplementOfCompositionRule = 0;
		timeObjectIntersectionOfCompositionRule = 0;
		timeSubClassOfAxiomCompositionRule = 0;
		timeObjectSomeValuesFromCompositionRule = 0;
		timeForwardLinkBackwardLinkRule = 0;
		timePropagationBackwardLinkRule = 0;
		timeContradictionBottomBackwardLinkRule = 0;
	}

	/**
	 * Add the values the corresponding values of the given timer
	 * 
	 * @param timer
	 */
	public synchronized void add(RuleApplicationTimer timer) {
		timeOwlThingContextInitializationRule += timer.timeOwlThingContextInitializationRule;
		timeContextRootInitializationRule += timer.timeContextRootInitializationRule;
		timeDisjointnessAxiomCompositionRule += timer.timeDisjointnessAxiomCompositionRule;
		timeDisjointnessAxiomContradictionRule += timer.timeDisjointnessAxiomContradictionRule;
		timeObjectComplementOfCompositionRule += timer.timeObjectComplementOfCompositionRule;
		timeObjectIntersectionOfCompositionRule += timer.timeObjectIntersectionOfCompositionRule;
		timeSubClassOfAxiomCompositionRule += timer.timeSubClassOfAxiomCompositionRule;
		timeObjectSomeValuesFromCompositionRule += timer.timeObjectSomeValuesFromCompositionRule;
		timeForwardLinkBackwardLinkRule += timer.timeForwardLinkBackwardLinkRule;
		timePropagationBackwardLinkRule += timer.timePropagationBackwardLinkRule;
		timeContradictionBottomBackwardLinkRule += timer.timeContradictionBottomBackwardLinkRule;
	}

	public int getTotalRuleAppTime() {
		return timeOwlThingContextInitializationRule
				+ timeContextRootInitializationRule
				+ timeDisjointnessAxiomCompositionRule
				+ timeDisjointnessAxiomContradictionRule
				+ timeObjectComplementOfCompositionRule
				+ timeObjectIntersectionOfCompositionRule
				+ timeSubClassOfAxiomCompositionRule
				+ timeObjectSomeValuesFromCompositionRule
				+ timeForwardLinkBackwardLinkRule
				+ timePropagationBackwardLinkRule
				+ timeContradictionBottomBackwardLinkRule;
	}

}
