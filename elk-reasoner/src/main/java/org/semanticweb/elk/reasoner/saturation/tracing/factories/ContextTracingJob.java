/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.factories;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.SaturationJob;

/**
 * A job for non-recursive context tracing.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ContextTracingJob extends SaturationJob<IndexedClassExpression> {

	public ContextTracingJob(IndexedClassExpression input) {
		super(input);
	}

	public ContextTracingListener getCallback() {
		return ContextTracingListener.DUMMY; 
	}
}
