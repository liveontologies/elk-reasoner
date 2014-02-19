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
import org.semanticweb.elk.reasoner.saturation.rules.backwardlinks.ForwardLinkFromBackwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.backwardlinks.SubsumerBackwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.contextinit.OwlThingContextInitRule;
import org.semanticweb.elk.reasoner.saturation.rules.contextinit.RootContextInitializationRule;
import org.semanticweb.elk.reasoner.saturation.rules.contradiction.ContradictionPropagationRule;
import org.semanticweb.elk.reasoner.saturation.rules.disjointsubsumer.ContradicitonCompositionRule;
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
	int countBackwardLinkChainFromBackwardLinkRule;

	/**
	 * counter for {@link NonReflexiveBackwardLinkCompositionRule}
	 */
	int countNonReflexiveBackwardLinkCompositionRule;

	/**
	 * counter for {@link ContradicitonCompositionRule}
	 */
	int countContradicitonCompositionRule;

	/**
	 * counter for {@link ContradictionFromDisjointnessRule}
	 */
	int countContradictionFromDisjointnessRule;

	/**
	 * counter for {@link ContradictionFromNegationRule}
	 */
	int countContradictionFromNegationRule;

	/**
	 * counter for {@link ContradictionFromOwlNothingRule}
	 */
	int countContradictionFromOwlNothingRule;

	/**
	 * counter for {@link ContradictionOverBackwardLinkRule}
	 */
	int countContradictionOverBackwardLinkRule;

	/**
	 * counter for {@link ContradictionPropagationRule}
	 */
	int countContradictionPropagationRule;

	/**
	 * counter for {@link DisjointSubsumerFromMemberRule}
	 */
	int countDisjointSubsumerFromMemberRule;

	/**
	 * counter for {@link ForwardLinkFromBackwardLinkRule}
	 */
	int countForwardLinkFromBackwardLinkRule;

	/**
	 * counter for {@link IndexedObjectComplementOfDecomposition}
	 */
	int countIndexedObjectComplementOfDecomposition;

	/**
	 * counter for {@link IndexedObjectIntersectionOfDecomposition}
	 */
	int countIndexedObjectIntersectionOfDecomposition;

	/**
	 * counter for {@link IndexedObjectSomeValuesFromDecomposition}
	 */
	int countIndexedObjectSomeValuesFromDecomposition;

	/**
	 * counter for {@link NonReflexivePropagationRule}
	 */
	int countNonReflexivePropagationRule;

	/**
	 * counter for {@link ObjectIntersectionFromConjunctRule}
	 */
	int countObjectIntersectionFromConjunctRule;

	/**
	 * counter for {@link ObjectUnionFromDisjunctRule}
	 */
	int countObjectUnionFromDisjunctRule;

	/**
	 * counter for {@link OwlThingContextInitRule}
	 */
	int countOwlThingContextInitRule;

	/**
	 * counter for {@link PropagationFromExistentialFillerRule}
	 */
	int countPropagationFromExistentialFillerRule;

	/**
	 * counter for {@link ReflexivePropagationRule}
	 */
	int countReflexivePropagationRule;

	/**
	 * counter for {@link RootContextInitializationRule}
	 */
	int countRootContextInitializationRule;

	/**
	 * counter for {@link SubsumerBackwardLinkRule}
	 */
	int countSubsumerBackwardLinkRule;

	/**
	 * counter for {@link SuperClassFromSubClassRule}
	 */
	int countSuperClassFromSubClassRule;

	/**
	 * counter for {@link ReflexiveBackwardLinkCompositionRule}
	 */
	int countReflexiveBackwardLinkCompositionRule;

	/**
	 * counter for {@link PropagationInitializationRule}
	 */
	int countPropagationInitializationRule;

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
		countForwardLinkFromBackwardLinkRule += counter.countForwardLinkFromBackwardLinkRule;
		countIndexedObjectComplementOfDecomposition += counter.countIndexedObjectComplementOfDecomposition;
		countContradictionFromOwlNothingRule += counter.countContradictionFromOwlNothingRule;
		countNonReflexivePropagationRule += counter.countNonReflexivePropagationRule;
		countReflexivePropagationRule += counter.countReflexivePropagationRule;
		countReflexiveBackwardLinkCompositionRule += counter.countReflexiveBackwardLinkCompositionRule;
		countPropagationInitializationRule += counter.countPropagationInitializationRule;
	}

	public int getTotalRuleAppCount() {
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
				+ countForwardLinkFromBackwardLinkRule
				+ countIndexedObjectComplementOfDecomposition
				+ countContradictionFromOwlNothingRule
				+ countNonReflexivePropagationRule
				+ countReflexivePropagationRule
				+ countPropagationInitializationRule;
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
		countForwardLinkFromBackwardLinkRule = 0;
		countIndexedObjectComplementOfDecomposition = 0;
		countContradictionFromOwlNothingRule = 0;
		countNonReflexivePropagationRule = 0;
		countReflexivePropagationRule = 0;
		countPropagationInitializationRule = 0;
	}
}
