/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class DelegatingExtendedSaturationStateWriter extends
		DelegatingBasicSaturationStateWriter implements
		ExtendedSaturationStateWriter {

	public DelegatingExtendedSaturationStateWriter(ExtendedSaturationStateWriter writer) {
		super(writer);
	}
	
	private ExtendedSaturationStateWriter getWriter() {
		return (ExtendedSaturationStateWriter) writer;
	}

	@Override
	public Context getCreateContext(IndexedClassExpression root) {
		return getWriter().getCreateContext(root);
	}

	@Override
	public void initContext(Context context) {
		getWriter().initContext(context);
	}

}
