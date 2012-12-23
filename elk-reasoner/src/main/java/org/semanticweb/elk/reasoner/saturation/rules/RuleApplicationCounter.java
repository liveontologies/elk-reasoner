package org.semanticweb.elk.reasoner.saturation.rules;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointnessAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;

/**
 * An object which can be used to measure the methods invocations of a
 * {@link RuleApplicationVisitor}. The fields of the counter correspond to the
 * methods of {@link RuleApplicationVisitor}.
 * 
 * @author "Yevgeny Kazakov"
 */
public class RuleApplicationCounter {

	/**
	 * counter for {@link IndexedClass.OwlThingContextInitializationRule}
	 */
	int countOwlThingContextInitializationRule;

	/**
	 * counter for {@link IndexedDisjointnessAxiom.ThisCompositionRule}
	 */
	int countDisjointnessAxiomCompositionRule;

	/**
	 * counter for {@link IndexedDisjointnessAxiom.ThisContradictionRule}
	 */
	int countDisjointnessAxiomContradictionRule;

	/**
	 * counter for {@link IndexedObjectIntersectionOf.ThisCompositionRule}
	 */
	int countObjectIntersectionOfCompositionRule;

	/**
	 * counter for {@link IndexedSubClassOfAxiom.ThisCompositionRule}
	 */
	int countSubClassOfAxiomCompositionRule;

	/**
	 * counter for {@link IndexedObjectSomeValuesFrom.ThisCompositionRule}
	 */
	int countObjectSomeValuesFromCompositionRule;

	/**
	 * counter for {@link ForwardLink.ThisBackwardLinkRule}
	 */
	int countForwardLinkBackwardLinkRule;

	/**
	 * counter for {@link Propagation.ThisBackwardLinkRule}
	 */
	int countPropagationBackwardLinkRule;

	/**
	 * counter for {@link Contradiction.BottomBackwardLinkRule}
	 */
	int countContradictionBottomBackwardLinkRule;

	/**
	 * Reset all counters to zero.
	 */
	public void reset() {
		countOwlThingContextInitializationRule = 0;
		countDisjointnessAxiomCompositionRule = 0;
		countDisjointnessAxiomContradictionRule = 0;
		countObjectIntersectionOfCompositionRule = 0;
		countSubClassOfAxiomCompositionRule = 0;
		countObjectSomeValuesFromCompositionRule = 0;
		countForwardLinkBackwardLinkRule = 0;
		countPropagationBackwardLinkRule = 0;
		countContradictionBottomBackwardLinkRule = 0;
	}

	/**
	 * Add the values the corresponding values of the given counter
	 * 
	 * @param counter
	 */
	public synchronized void add(RuleApplicationCounter counter) {
		countOwlThingContextInitializationRule += counter.countOwlThingContextInitializationRule;
		countDisjointnessAxiomCompositionRule += counter.countDisjointnessAxiomCompositionRule;
		countDisjointnessAxiomContradictionRule += counter.countDisjointnessAxiomContradictionRule;
		countObjectIntersectionOfCompositionRule += counter.countObjectIntersectionOfCompositionRule;
		countSubClassOfAxiomCompositionRule += counter.countSubClassOfAxiomCompositionRule;
		countObjectSomeValuesFromCompositionRule += counter.countObjectSomeValuesFromCompositionRule;
		countForwardLinkBackwardLinkRule += counter.countForwardLinkBackwardLinkRule;
		countPropagationBackwardLinkRule += counter.countPropagationBackwardLinkRule;
		countContradictionBottomBackwardLinkRule += counter.countContradictionBottomBackwardLinkRule;
	}

}
