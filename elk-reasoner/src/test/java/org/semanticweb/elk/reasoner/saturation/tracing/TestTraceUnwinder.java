/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ObjectPropertyConclusion;

/**
 * Recursively visits all conclusions which were used to produce a given
 * conclusion. It can notify the caller if some visited conclusion has not been
 * traced (this is useful for testing).
 * 
 * TODO it's a bit kludgy that this class extends the recursive unwinder and
 * thus requires clients to pass visitors which return boolean (to know when
 * stop the recursion). Since this class never stops recursion, it's better to
 * encompass the recursive visitor and accept any visitors.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class TestTraceUnwinder extends RecursiveTraceUnwinder {

	private final UntracedConclusionListener listener_;

	public TestTraceUnwinder(InferenceSet inferences,
			UntracedConclusionListener listener) {
		super(inferences);
		listener_ = listener;
	}

	@Override
	protected void handleUntraced(ClassConclusion untraced) {
		listener_.notifyUntraced(untraced);
	}

	@Override
	protected void handleUntraced(ObjectPropertyConclusion untraced) {
		listener_.notifyUntraced(untraced);
	}

}
