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
import org.semanticweb.elk.reasoner.saturation.rules.disjointsubsumer.ContradicitonCompositionRule;
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
 * An object which can be used to measure the methods invocations of a
 * {@link RuleVisitor}. The fields of the counter correspond to the methods of
 * {@link RuleVisitor}.
 * 
 * @author "Yevgeny Kazakov"
 */
public class RuleCounter {

	/**
	 * counter for {@link BackwardLinkChainFromBackwardLinkRule}
	 */
	long countBackwardLinkChainFromBackwardLinkRule;

	/**
	 * counter for {@link BackwardLinkFromForwardLinkRule}
	 */
	long countBackwardLinkFromForwardLinkRule;

	/**
	 * counter for {@link ContradicitonCompositionRule}
	 */
	long countContradicitonCompositionRule;

	/**
	 * counter for {@link ContradictionFromDisjointnessRule}
	 */
	long countContradictionFromDisjointnessRule;

	/**
	 * counter for {@link ContradictionFromNegationRule}
	 */
	long countContradictionFromNegationRule;

	/**
	 * counter for {@link ContradictionFromOwlNothingRule}
	 */
	long countContradictionFromOwlNothingRule;

	/**
	 * counter for {@link ContradictionOverBackwardLinkRule}
	 */
	long countContradictionOverBackwardLinkRule;

	/**
	 * counter for {@link ContradictionPropagationRule}
	 */
	long countContradictionPropagationRule;

	/**
	 * counter for {@link DisjointSubsumerFromMemberRule}
	 */
	long countDisjointSubsumerFromMemberRule;

	/**
	 * counter for {@link IndexedObjectComplementOfDecomposition}
	 */
	long countIndexedObjectComplementOfDecomposition;

	/**
	 * counter for {@link IndexedObjectIntersectionOfDecomposition}
	 */
	long countIndexedObjectIntersectionOfDecomposition;

	/**
	 * counter for {@link IndexedObjectSomeValuesFromDecomposition}
	 */
	long countIndexedObjectSomeValuesFromDecomposition;

	/**
	 * counter for {@link NonReflexiveBackwardLinkCompositionRule}
	 */
	long countNonReflexiveBackwardLinkCompositionRule;

	/**
	 * counter for {@link NonReflexivePropagationRule}
	 */
	long countNonReflexivePropagationRule;

	/**
	 * counter for {@link ObjectIntersectionFromConjunctRule}
	 */
	long countObjectIntersectionFromConjunctRule;

	/**
	 * counter for {@link ObjectUnionFromDisjunctRule}
	 */
	long countObjectUnionFromDisjunctRule;

	/**
	 * counter for {@link OwlThingContextInitRule}
	 */
	long countOwlThingContextInitRule;

	/**
	 * counter for {@link PropagationFromExistentialFillerRule}
	 */
	long countPropagationFromExistentialFillerRule;

	/**
	 * counter for {@link PropagationInitializationRule}
	 */
	long countPropagationInitializationRule;

	/**
	 * counter for {@link ReflexiveBackwardLinkCompositionRule}
	 */
	long countReflexiveBackwardLinkCompositionRule;

	/**
	 * counter for {@link ReflexivePropagationRule}
	 */
	long countReflexivePropagationRule;

	/**
	 * counter for {@link RootContextInitializationRule}
	 */
	long countRootContextInitializationRule;

	/**
	 * counter for {@link SubsumerBackwardLinkRule}
	 */
	long countSubsumerBackwardLinkRule;

	/**
	 * counter for {@link SuperClassFromSubClassRule}
	 */
	long countSuperClassFromSubClassRule;

	/**
	 * counter for {@link ReflexivePropertyRangesContextInitRule};
	 */
	long countReflexivePropertyRangesContextInitRule;

	/**
	 * Add the values the corresponding values of the given counter
	 * 
	 * @param counter
	 */
	public synchronized void add(RuleCounter counter) {
		countOwlThingContextInitRule += counter.countOwlThingContextInitRule;
		countRootContextInitializationRule += counter.countRootContextInitializationRule;
		countDisjointSubsumerFromMemberRule += counter.countDisjointSubsumerFromMemberRule;
		countContradictionFromDisjointnessRule += counter.countContradictionFromDisjointnessRule;
		countContradictionFromNegationRule += counter.countContradictionFromNegationRule;
		countObjectIntersectionFromConjunctRule += counter.countObjectIntersectionFromConjunctRule;
		countSuperClassFromSubClassRule += counter.countSuperClassFromSubClassRule;
		countPropagationFromExistentialFillerRule += counter.countPropagationFromExistentialFillerRule;
		countObjectUnionFromDisjunctRule += counter.countObjectUnionFromDisjunctRule;
		countBackwardLinkChainFromBackwardLinkRule += counter.countBackwardLinkChainFromBackwardLinkRule;
		countSubsumerBackwardLinkRule += counter.countSubsumerBackwardLinkRule;
		countContradictionOverBackwardLinkRule += counter.countContradictionOverBackwardLinkRule;
		countContradictionPropagationRule += counter.countContradictionPropagationRule;
		countContradicitonCompositionRule += counter.countContradicitonCompositionRule;
		countNonReflexiveBackwardLinkCompositionRule += counter.countNonReflexiveBackwardLinkCompositionRule;
		countIndexedObjectIntersectionOfDecomposition += counter.countIndexedObjectIntersectionOfDecomposition;
		countIndexedObjectSomeValuesFromDecomposition += counter.countIndexedObjectSomeValuesFromDecomposition;
		countIndexedObjectComplementOfDecomposition += counter.countIndexedObjectComplementOfDecomposition;
		countContradictionFromOwlNothingRule += counter.countContradictionFromOwlNothingRule;
		countNonReflexivePropagationRule += counter.countNonReflexivePropagationRule;
		countReflexivePropagationRule += counter.countReflexivePropagationRule;
		countReflexiveBackwardLinkCompositionRule += counter.countReflexiveBackwardLinkCompositionRule;
		countPropagationInitializationRule += counter.countPropagationInitializationRule;
		countBackwardLinkFromForwardLinkRule += counter.countBackwardLinkFromForwardLinkRule;
		countReflexivePropertyRangesContextInitRule += counter.countReflexivePropertyRangesContextInitRule;
	}

	public long getTotalRuleAppCount() {
		return countOwlThingContextInitRule
				+ countRootContextInitializationRule
				+ countDisjointSubsumerFromMemberRule
				+ countContradictionFromDisjointnessRule
				+ countContradictionFromNegationRule
				+ countObjectIntersectionFromConjunctRule
				+ countSuperClassFromSubClassRule
				+ countPropagationFromExistentialFillerRule
				+ countObjectUnionFromDisjunctRule
				+ countBackwardLinkChainFromBackwardLinkRule
				+ countSubsumerBackwardLinkRule
				+ countContradictionOverBackwardLinkRule
				+ countContradictionPropagationRule
				+ countContradicitonCompositionRule
				+ countNonReflexiveBackwardLinkCompositionRule
				+ countReflexiveBackwardLinkCompositionRule
				+ countIndexedObjectIntersectionOfDecomposition
				+ countIndexedObjectSomeValuesFromDecomposition
				+ countIndexedObjectComplementOfDecomposition
				+ countContradictionFromOwlNothingRule
				+ countNonReflexivePropagationRule
				+ countReflexivePropagationRule
				+ countPropagationInitializationRule
				+ countBackwardLinkFromForwardLinkRule
				+ countReflexivePropertyRangesContextInitRule;
	}

	/**
	 * Reset all counters to zero.
	 */
	public void reset() {
		countOwlThingContextInitRule = 0;
		countRootContextInitializationRule = 0;
		countDisjointSubsumerFromMemberRule = 0;
		countContradictionFromDisjointnessRule = 0;
		countContradictionFromNegationRule = 0;
		countObjectIntersectionFromConjunctRule = 0;
		countSuperClassFromSubClassRule = 0;
		countPropagationFromExistentialFillerRule = 0;
		countObjectUnionFromDisjunctRule = 0;
		countBackwardLinkChainFromBackwardLinkRule = 0;
		countSubsumerBackwardLinkRule = 0;
		countContradictionOverBackwardLinkRule = 0;
		countContradictionPropagationRule = 0;
		countContradicitonCompositionRule = 0;
		countNonReflexiveBackwardLinkCompositionRule = 0;
		countReflexiveBackwardLinkCompositionRule = 0;
		countIndexedObjectIntersectionOfDecomposition = 0;
		countIndexedObjectSomeValuesFromDecomposition = 0;
		countIndexedObjectComplementOfDecomposition = 0;
		countContradictionFromOwlNothingRule = 0;
		countNonReflexivePropagationRule = 0;
		countReflexivePropagationRule = 0;
		countPropagationInitializationRule = 0;
		countBackwardLinkFromForwardLinkRule = 0;
		countReflexivePropertyRangesContextInitRule = 0;
	}
}
