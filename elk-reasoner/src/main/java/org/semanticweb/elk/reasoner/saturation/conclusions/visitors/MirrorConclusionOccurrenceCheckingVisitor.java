package org.semanticweb.elk.reasoner.saturation.conclusions.visitors;

import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * 
 * A {@link ConclusionVisitor} that checks if visited {@link Conclusion} is
 * contained the mirror of the given {@link Context} in the provided
 * {@link SaturationState}. A {@link Context} is a mirror of a given
 * {@link Context} in a {@link SaturationState} if it has the same root. The
 * visit method returns {@link true} if the {@link Context} is occurs in the
 * mirror {@link Context} and {@link false} otherwise.
 * 
 * @see ConclusionOccurrenceCheckingVisitor
 * @see Context#getRoot()
 * 
 * @author "Yevgeny Kazakov"
 */
public class MirrorConclusionOccurrenceCheckingVisitor extends
		AbstractConclusionVisitor<Boolean> implements
		ConclusionVisitor<Boolean> {

	/**
	 * the standard checker for occurrences of {@link Conclusion}s in
	 * {@link Context}s
	 */
	private final ConclusionVisitor<Boolean> OCCURRENCE_CHECKER_ = new ConclusionOccurrenceCheckingVisitor();

	/**
	 * The {@link SaturationState} in which to
	 */
	private final SaturationState mirrorSaturationState_;

	public MirrorConclusionOccurrenceCheckingVisitor(
			SaturationState mirrorSaturationState) {
		this.mirrorSaturationState_ = mirrorSaturationState;
	}

	Context getMirrorContext(Context context) {
		return mirrorSaturationState_.getContext(context.getRoot());
	}

	@Override
	Boolean defaultVisit(Conclusion conclusion, Context context) {
		return conclusion
				.accept(OCCURRENCE_CHECKER_, getMirrorContext(context));
	}

}