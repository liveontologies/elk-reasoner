package org.semanticweb.elk.reasoner.saturation;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.context.Context;

public class ExtendedSaturationStateWriterWrap<W extends ExtendedSaturationStateWriter>
		extends SaturationStateWriterWrap<W> implements
		ExtendedSaturationStateWriter {

	public ExtendedSaturationStateWriterWrap(W mainWriter) {
		super(mainWriter);
	}

	@Override
	public Context getCreateContext(IndexedClassExpression root) {
		return mainWriter.getCreateContext(root);
	}

	@Override
	public void initContext(Context context) {
		mainWriter.initContext(context);
	}

	@Override
	public void removeContext(Context context) {
		mainWriter.removeContext(context);
	}

}
