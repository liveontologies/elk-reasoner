package org.semanticweb.elk.reasoner.saturation;

import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * A {@link SaturationStateWriter} that does not produce conclusions if the
 * corresponding context does not exist.
 * 
 * @author "Yevgeny Kazakov"
 */
public class ContextExistenceCheckingWriter<C extends Context> extends
		SaturationStateWriterWrap<C> {

	private final SaturationState<? extends C> state_;

	public ContextExistenceCheckingWriter(
			SaturationStateWriter<? extends C> writer,
			SaturationState<? extends C> state) {
		super(writer);
		this.state_ = state;
	}

	@Override
	public void produce(ClassConclusion conclusion) {
		Context context = state_.getContext(conclusion.getConclusionRoot());

		if (context != null) {
			super.produce(conclusion);
		}
	}

}