package org.semanticweb.elk.reasoner.saturation.conclusions;

import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationVisitor;

/**
 * A {@link ConclusionVisitor} that applies decomposition rules for visited
 * {@link Conclusion}s using the provided {@link RuleApplicationVisitor} to
 * track rule applications and {@link SaturationStateWriter} to output the
 * {@link Conclusion}s of the applied rules.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class ConclusionCompositionRuleApplicationVisitor implements
		ConclusionVisitor<Boolean> {

	/**
	 * {@link RuleApplicationVisitor} to track rule applications
	 */
	private final RuleApplicationVisitor ruleAppVisitor_;

	/**
	 * {@link SaturationStateWriter} to output the {@link Conclusion}s of the
	 * applied rules
	 */
	private final SaturationStateWriter writer_;

	public ConclusionCompositionRuleApplicationVisitor(
			RuleApplicationVisitor ruleAppVisitor, SaturationStateWriter writer) {
		this.writer_ = writer;
		this.ruleAppVisitor_ = ruleAppVisitor;
	}

	public Boolean defaultVisit(Conclusion conclusion, Context context) {
		conclusion.accept(ruleAppVisitor_, writer_, context);
		return true;
	}

	@Override
	public Boolean visit(ComposedSubsumer negSCE, Context context) {
		return defaultVisit(negSCE, context);
	}

	@Override
	public Boolean visit(DecomposedSubsumer posSCE, Context context) {
		return defaultVisit(posSCE, context);
	}

	@Override
	public Boolean visit(BackwardLink link, Context context) {
		return defaultVisit(link, context);
	}

	@Override
	public Boolean visit(ForwardLink link, Context context) {
		return defaultVisit(link, context);
	}

	@Override
	public Boolean visit(Contradiction bot, Context context) {
		return defaultVisit(bot, context);
	}

	@Override
	public Boolean visit(Propagation propagation, Context context) {
		return defaultVisit(propagation, context);
	}

	@Override
	public Boolean visit(DisjointnessAxiom disjointnessAxiom, Context context) {
		return defaultVisit(disjointnessAxiom, context);
	}

}
