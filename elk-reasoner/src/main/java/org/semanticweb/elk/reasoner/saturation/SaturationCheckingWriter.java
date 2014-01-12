package org.semanticweb.elk.reasoner.saturation;

import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * A {@link SaturationStateWriter} that does not produce conclusions if their
 * source context is already saturated.
 * 
 * @author Pavel Klinov
 * 
 * @author "Yevgeny Kazakov"
 */
public class SaturationCheckingWriter extends
		SaturationStateWriterWrap<SaturationStateWriter> {

	public SaturationCheckingWriter(SaturationStateWriter writer) {
		super(writer);
	}

	@Override
	public void produce(Context context, Conclusion conclusion) {
		Context sourceContext = conclusion.getSourceContext(context);

		if (sourceContext == null || !sourceContext.isSaturated()) {
			super.produce(context, conclusion);
		}
	}

}