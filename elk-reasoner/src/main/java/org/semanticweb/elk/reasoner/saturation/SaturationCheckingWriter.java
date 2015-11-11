package org.semanticweb.elk.reasoner.saturation;

import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * A {@link SaturationStateWriter} that does not produce conclusions if their
 * source context is already saturated.
 * 
 * @author Pavel Klinov
 * 
 * @author "Yevgeny Kazakov"
 */
public class SaturationCheckingWriter<C extends Context> extends
		SaturationStateWriterWrap<C> {

	private final SaturationState<? extends C> state_;

	public SaturationCheckingWriter(SaturationStateWriter<? extends C> writer,
			SaturationState<? extends C> state) {
		super(writer);
		this.state_ = state;
	}

	@Override
	public void produce(ClassConclusion conclusion) {
		Context sourceContext = state_.getContext(conclusion.getOriginRoot());

		if (sourceContext == null || !sourceContext.isSaturated()) {
			super.produce(conclusion);
		}
	}

}