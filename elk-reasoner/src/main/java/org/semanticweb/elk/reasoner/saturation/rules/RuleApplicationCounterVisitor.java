package org.semanticweb.elk.reasoner.saturation.rules;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointnessAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * A {@link RuleApplicationVisitor} wrapper for a given
 * {@link RuleApplicationVisitor} that additionally records the number of
 * invocations of the methods using the given {@link RuleApplicationCounter}.
 * 
 * @author "Yevgeny Kazakov"
 */
public class RuleApplicationCounterVisitor implements RuleApplicationVisitor {

	/**
	 * the visitor whose method applications to be counted
	 */
	private final RuleApplicationVisitor visitor_;
	/**
	 * the counter used to count the number of method applications of the
	 * visitor
	 */
	private final RuleApplicationCounter counter_;

	/**
	 * Creates a new {@link DecompositionRuleApplicationVisitor} that executes
	 * the corresponding methods of the given
	 * {@link DecompositionRuleApplicationVisitor} and counts the number of
	 * invocations of the corresponding methods using the given
	 * {@link RuleApplicationCounter}.
	 * 
	 * @param visitor
	 *            the {@link DecompositionRuleApplicationVisitor} used to
	 *            execute the methods
	 * @param counter
	 *            the {@link RuleApplicationCounter} used to count the number of
	 *            method invocations
	 */
	public RuleApplicationCounterVisitor(RuleApplicationVisitor visitor,
			RuleApplicationCounter counter) {
		this.visitor_ = visitor;
		this.counter_ = counter;
	}

	@Override
	public void visit(
			IndexedClass.OwlThingContextInitializationRule owlThingContextInitializationRule,
			SaturationState.Writer writer, Context context) {
		counter_.countOwlThingContextInitializationRule++;
		visitor_.visit(owlThingContextInitializationRule, writer, context);
	}

	@Override
	public void visit(
			IndexedDisjointnessAxiom.ThisCompositionRule thisCompositionRule,
			SaturationState.Writer writer, Context context) {
		counter_.countDisjointnessAxiomCompositionRule++;
		visitor_.visit(thisCompositionRule, writer, context);
	}

	@Override
	public void visit(
			IndexedDisjointnessAxiom.ThisContradictionRule thisContradictionRule,
			SaturationState.Writer writer, Context context) {
		counter_.countDisjointnessAxiomContradictionRule++;
		visitor_.visit(thisContradictionRule, writer, context);
	}

	@Override
	public void visit(
			IndexedObjectIntersectionOf.ThisCompositionRule thisCompositionRule,
			SaturationState.Writer writer, Context context) {
		counter_.countObjectIntersectionOfCompositionRule++;
		visitor_.visit(thisCompositionRule, writer, context);
	}

	@Override
	public void visit(
			IndexedSubClassOfAxiom.ThisCompositionRule thisCompositionRule,
			SaturationState.Writer writer, Context context) {
		counter_.countSubClassOfAxiomCompositionRule++;
		visitor_.visit(thisCompositionRule, writer, context);
	}

	@Override
	public void visit(
			IndexedObjectSomeValuesFrom.ThisCompositionRule thisCompositionRule,
			SaturationState.Writer writer, Context context) {
		counter_.countObjectSomeValuesFromCompositionRule++;
		visitor_.visit(thisCompositionRule, writer, context);
	}

	@Override
	public void visit(ForwardLink.ThisBackwardLinkRule thisBackwardLinkRule,
			SaturationState.Writer writer, BackwardLink backwardLink) {
		counter_.countForwardLinkBackwardLinkRule++;
		visitor_.visit(thisBackwardLinkRule, writer, backwardLink);

	}

	@Override
	public void visit(Propagation.ThisBackwardLinkRule thisBackwardLinkRule,
			SaturationState.Writer writer, BackwardLink backwardLink) {
		counter_.countPropagationBackwardLinkRule++;
		visitor_.visit(thisBackwardLinkRule, writer, backwardLink);
	}

	@Override
	public void visit(
			Contradiction.BottomBackwardLinkRule bottomBackwardLinkRule,
			SaturationState.Writer writer, BackwardLink backwardLink) {
		counter_.countContradictionBottomBackwardLinkRule++;
		visitor_.visit(bottomBackwardLinkRule, writer, backwardLink);
	}

}
