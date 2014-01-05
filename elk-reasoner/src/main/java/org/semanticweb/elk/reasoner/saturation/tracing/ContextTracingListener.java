/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * Receives notifications when an inference is produced in a non-traced context so that subscribers can trace it. 
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface ContextTracingListener {

	/**
	 * 
	 * @param context
	 */
	public void notifyNonTraced(Context context);
	
	public static final ContextTracingListener DUMMY = new ContextTracingListener() {
		
		@Override
		public void notifyNonTraced(Context context) {
			//no-op
		}
	};
}
