package org.semanticweb.elk.reasoner.saturation;

import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionOccurrenceCheckingVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;

/**
 * A {@link MapSaturationState} whose writers produce conclusions only if they
 * are present in the corresponding {@link Context} of the given primary
 * {@link SaturationState}.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author "Yevgeny Kazakov"
 */
public class SubSaturationState extends MapSaturationState {

	/**
	 * The {@link ConclusionVisitor} used for checking membership of
	 * {@link Conclusion}s in {@link Context}s
	 */
	private final static ConclusionVisitor<Boolean> CONCLUSION_CHECKER_ = new ConclusionOccurrenceCheckingVisitor();

	/**
	 * The {@link SaturationState} used for checking if
	 */
	private final SaturationState primarySaturationState_;

	public SubSaturationState(SaturationState primarySaturationState,
			OntologyIndex index) {
		super(index);
		this.primarySaturationState_ = primarySaturationState;
	}

	@Override
	public ExtendedSaturationStateWriter getExtendedWriter(
			ContextCreationListener contextCreationListener,
			ContextModificationListener contextModificationListener,
			RuleVisitor ruleAppVisitor, ConclusionVisitor<?> conclusionVisitor,
			boolean trackNewContextsAsUnsaturated) {
		return getCheckingWriter(super.getExtendedWriter(
				contextCreationListener, contextModificationListener,
				ruleAppVisitor, conclusionVisitor,
				trackNewContextsAsUnsaturated));
	}

	@Override
	public SaturationStateWriter getWriter(
			ContextModificationListener contextModificationListener,
			ConclusionVisitor<?> conclusionVisitor) {
		return getCheckingWriter(super.getWriter(contextModificationListener,
				conclusionVisitor));
	}

	@Override
	public SaturationStateWriter getWriter(
			ConclusionVisitor<?> conclusionVisitor) {
		return getCheckingWriter(super.getWriter(conclusionVisitor));
	}

	@Override
	public ExtendedSaturationStateWriter getExtendedWriter(
			ConclusionVisitor<?> conclusionVisitor) {
		return getCheckingWriter(super.getExtendedWriter(conclusionVisitor));
	}

	void produceIfExists(SaturationStateWriter mainWriter, Context context,
			Conclusion conclusion) {
		// the corresponding context of the primary saturation state
		Context primaryContext = primarySaturationState_.getContext(context
				.getRoot());
		// only produce if the conclusion occurs in the primary context
		if (conclusion.accept(CONCLUSION_CHECKER_, primaryContext))
			mainWriter.produce(context, conclusion);
	}

	private <W extends SaturationStateWriter> SaturationStateWriter getCheckingWriter(
			final W writer) {
		return new SaturationStateWriterWrap<W>(writer) {
			@Override
			public void produce(Context context, Conclusion conclusion) {
				produceIfExists(writer, context, conclusion);
			}
		};
	}

	private <W extends ExtendedSaturationStateWriter> ExtendedSaturationStateWriter getCheckingWriter(
			final W writer) {
		return new ExtendedSaturationStateWriterWrap<W>(writer) {
			@Override
			public void produce(Context context, Conclusion conclusion) {
				produceIfExists(writer, context, conclusion);
			}
		};
	}
}