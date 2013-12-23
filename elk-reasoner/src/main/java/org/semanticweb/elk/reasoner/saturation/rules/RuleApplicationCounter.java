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
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointnessAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectComplementOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectUnionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.DisjointnessAxiom;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;

/**
 * An object which can be used to measure the methods invocations of a
 * {@link CompositionRuleVisitor}. The fields of the counter correspond to the
 * methods of {@link CompositionRuleVisitor}.
 * 
 * @author "Yevgeny Kazakov"
 */
public class RuleApplicationCounter {

	/**
	 * counter for {@link IndexedClass.OwlThingContextInitializationRule}
	 */
	int countOwlThingContextInitializationRule;

	/**
	 * counter for {@link DirectIndex.ContextRootInitializationRule}
	 */
	int countContextRootInitializationRule;

	/**
	 * counter for {@link IndexedDisjointnessAxiom.ThisCompositionRule}
	 */
	int countIndexedDisjointnessAxiomCompositionRule;

	/**
	 * counter for {@link IndexedDisjointnessAxiom.ThisContradictionRule}
	 */
	int countIndexedDisjointnessAxiomContradictionRule;

	/**
	 * counter for {@link IndexedObjectComplementOf.ThisCompositionRule}
	 */
	int countIndexedObjectComplementOfCompositionRule;

	/**
	 * counter for {@link IndexedObjectIntersectionOf.ThisCompositionRule}
	 */
	int countIndexedObjectIntersectionOfCompositionRule;

	/**
	 * counter for {@link IndexedSubClassOfAxiom.ThisCompositionRule}
	 */
	int countIndexedSubClassOfAxiomCompositionRule;

	/**
	 * counter for {@link IndexedObjectSomeValuesFrom.ThisCompositionRule}
	 */
	int countIndexedObjectSomeValuesFromCompositionRule;

	/**
	 * counter for {@link IndexedObjectUnionOf.ThisCompositionRule}
	 */
	int countIndexedObjectUnionOfCompositionRule;

	/**
	 * counter for {@link ForwardLink.ThisBackwardLinkRule}
	 */
	int countForwardLinkBackwardLinkRule;

	/**
	 * counter for {@link Propagation.ThisBackwardLinkRule}
	 */
	int countPropagationBackwardLinkRule;

	/**
	 * counter for {@link Contradiction.ContradictionBackwardLinkRule}
	 */
	int countContradictionBottomBackwardLinkRule;

	/**
	 * counter for {@link BackwardLink.ThisCompositionRule}
	 */
	int countBackwardLinkCompositionRule;

	/**
	 * counter for {@link Contradiction.ThisCompositionRule}
	 */
	int countContradictionCompositionRule;

	/**
	 * counter for {@link DisjointnessAxiom.ThisCompositionRule}
	 */
	int countDisjointnessAxiomCompositionRule;

	/**
	 * counter for {@link ForwardLink.ThisCompositionRule}
	 */
	int countForwardLinkCompositionRule;

	/**
	 * Add the values the corresponding values of the given counter
	 * 
	 * @param counter
	 */
	public synchronized void add(RuleApplicationCounter counter) {
		countOwlThingContextInitializationRule += counter.countOwlThingContextInitializationRule;
		countContextRootInitializationRule += counter.countContextRootInitializationRule;
		countIndexedDisjointnessAxiomCompositionRule += counter.countIndexedDisjointnessAxiomCompositionRule;
		countIndexedDisjointnessAxiomContradictionRule += counter.countIndexedDisjointnessAxiomContradictionRule;
		countIndexedObjectComplementOfCompositionRule += counter.countIndexedObjectComplementOfCompositionRule;
		countIndexedObjectIntersectionOfCompositionRule += counter.countIndexedObjectIntersectionOfCompositionRule;
		countIndexedSubClassOfAxiomCompositionRule += counter.countIndexedSubClassOfAxiomCompositionRule;
		countIndexedObjectSomeValuesFromCompositionRule += counter.countIndexedObjectSomeValuesFromCompositionRule;
		countIndexedObjectUnionOfCompositionRule += counter.countIndexedObjectUnionOfCompositionRule;
		countForwardLinkBackwardLinkRule += counter.countForwardLinkBackwardLinkRule;
		countPropagationBackwardLinkRule += counter.countPropagationBackwardLinkRule;
		countContradictionBottomBackwardLinkRule += counter.countContradictionBottomBackwardLinkRule;
		countBackwardLinkCompositionRule += counter.countBackwardLinkCompositionRule;
		countContradictionCompositionRule += counter.countContradictionCompositionRule;
		countDisjointnessAxiomCompositionRule += counter.countDisjointnessAxiomCompositionRule;
		countForwardLinkCompositionRule += counter.countForwardLinkCompositionRule;
	}

	public long getTotalRuleAppCount() {
		return countOwlThingContextInitializationRule
				+ countContextRootInitializationRule
				+ countIndexedDisjointnessAxiomCompositionRule
				+ countIndexedDisjointnessAxiomContradictionRule
				+ countIndexedObjectComplementOfCompositionRule
				+ countIndexedObjectIntersectionOfCompositionRule
				+ countIndexedSubClassOfAxiomCompositionRule
				+ countIndexedObjectSomeValuesFromCompositionRule
				+ countIndexedObjectUnionOfCompositionRule
				+ countForwardLinkBackwardLinkRule
				+ countPropagationBackwardLinkRule
				+ countContradictionBottomBackwardLinkRule
				+ countBackwardLinkCompositionRule
				+ countContradictionCompositionRule
				+ countDisjointnessAxiomCompositionRule
				+ countForwardLinkCompositionRule;
	}

	/**
	 * Reset all counters to zero.
	 */
	public void reset() {
		countOwlThingContextInitializationRule = 0;
		countContextRootInitializationRule = 0;
		countIndexedDisjointnessAxiomCompositionRule = 0;
		countIndexedDisjointnessAxiomContradictionRule = 0;
		countIndexedObjectComplementOfCompositionRule = 0;
		countIndexedObjectIntersectionOfCompositionRule = 0;
		countIndexedSubClassOfAxiomCompositionRule = 0;
		countIndexedObjectSomeValuesFromCompositionRule = 0;
		countIndexedObjectUnionOfCompositionRule = 0;
		countForwardLinkBackwardLinkRule = 0;
		countPropagationBackwardLinkRule = 0;
		countContradictionBottomBackwardLinkRule = 0;
		countBackwardLinkCompositionRule = 0;
		countContradictionCompositionRule = 0;
		countDisjointnessAxiomCompositionRule = 0;
		countForwardLinkCompositionRule = 0;

	}
}
