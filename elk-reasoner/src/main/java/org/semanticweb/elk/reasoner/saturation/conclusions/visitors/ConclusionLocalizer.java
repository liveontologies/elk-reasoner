package org.semanticweb.elk.reasoner.saturation.conclusions.visitors;

import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * A {@link ConclusionVisitor} that converts {@link Conclusion}s to the
 * corresponding {@link Conclusion}s in which {@link Context} parameters are
 * replaced with the corresponding {@link Context}s (with the same root) of the
 * provided {@link SaturationState} argument. If such {@link Context} does not
 * exist, the visitor returns {@code null}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class ConclusionLocalizer extends
		AbstractConclusionVisitor<SaturationState, Conclusion> {

	private Context localize(Context context, SaturationState state) {
		return state.getContext(context.getRoot());
	}

	@Override
	Conclusion defaultVisit(Conclusion conclusion, SaturationState state) {
		return conclusion;
	}

	@Override
	public Conclusion visit(BackwardLink conclusion, SaturationState state) {
		Context localContext = localize(conclusion.getSource(), state);
		if (localContext == null)
			return null;
		return new BackwardLink(localContext, conclusion.getRelation());
	}

	@Override
	public Conclusion visit(ForwardLink conclusion, SaturationState state) {
		Context localContext = localize(conclusion.getTarget(), state);
		if (localContext == null)
			return null;
		return new ForwardLink(conclusion.getRelation(), localContext);
	}

	@Override
	public Conclusion visit(ContextInitialization conclusion,
			SaturationState state) {
		return new ContextInitialization(state.getOntologyIndex());
	}

}
