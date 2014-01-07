package org.semanticweb.elk.reasoner.saturation;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * An implementation of {@link SaturationStateWriter} that just mirrors all
 * methods of the given {@link SaturationStateWriter}. This class can be used
 * for convenience if some methods of a {@link SaturationStateWriter} should be
 * redefined.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <W>
 *            the type of the wrapped {@link SaturationStateWriter}
 */
public class SaturationStateWriterWrap<W extends SaturationStateWriter>
		implements SaturationStateWriter {

	protected final W mainWriter;

	public SaturationStateWriterWrap(W mainWriter) {
		this.mainWriter = mainWriter;
	}

	@Override
	public void produce(Context context, Conclusion conclusion) {
		mainWriter.produce(context, conclusion);
	}

	@Override
	public void produce(IndexedClassExpression root, Conclusion conclusion) {
		mainWriter.produce(root, conclusion);
	}

	@Override
	public Context pollForActiveContext() {
		return mainWriter.pollForActiveContext();
	}

	@Override
	public boolean markAsNotSaturated(Context context) {
		return mainWriter.markAsNotSaturated(context);
	}

	@Override
	public void clearNotSaturatedContexts() {
		mainWriter.clearNotSaturatedContexts();
	}

	@Override
	public void resetContexts() {
		mainWriter.resetContexts();
	}

}
