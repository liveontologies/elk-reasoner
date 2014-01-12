package org.semanticweb.elk.reasoner.saturation.conclusions.visitors;

import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ConclusionVisitor} that localizes the input {@link Conclusion} and
 * {@link Context} within the given {@link SaturationState} and passes them to
 * the given internal {@link ConclusionVisitor}. Localization means that all
 * {@link Context}s (including those occurring in the {@link Conclusion}s are
 * converted to the corresponding {@link Context}s of the given
 * {@link SaturationState} (i.e., with the same root). If the localized context
 * does not exist, the visitor returns {@code false}
 * 
 * @see ConclusionLocalizer
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class LocalizedConclusionVisitor extends
		AbstractConclusionVisitor<Context, Boolean> {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(LocalizedConclusionVisitor.class);

	private static ConclusionVisitor<SaturationState, Conclusion> CONCLUSION_LOCALIZER_ = new ConclusionLocalizer();

	/**
	 * the {@link ConclusionVisitor} to be localized
	 */
	ConclusionVisitor<Context, Boolean> visitor_;
	SaturationState state_;

	public LocalizedConclusionVisitor(
			ConclusionVisitor<Context, Boolean> visitor, SaturationState state) {
		this.visitor_ = visitor;
		this.state_ = state;
	}

	@Override
	Boolean defaultVisit(Conclusion conclusion, Context input) {
		Context localContext = state_.getContext(input.getRoot());
		if (localContext == null) {
			LOGGER_.trace("{}: local context does not exist", input);
			return false;
		}
		Conclusion localConclusion = conclusion.accept(CONCLUSION_LOCALIZER_,
				state_);
		if (localConclusion == null) {
			LOGGER_.trace("{}: conclusion cannot be localized", input);
			return false;
		}
		LOGGER_.trace("{}: localized {}", localContext, localConclusion);
		return localConclusion.accept(visitor_, localContext);
	}
}
