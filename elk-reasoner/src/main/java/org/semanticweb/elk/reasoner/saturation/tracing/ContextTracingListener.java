/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;


/**
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
	public void notifyFinished(ContextTracingJob job);
	
	public static final ContextTracingListener DUMMY = new ContextTracingListener() {
		
		@Override
		public void notifyFinished(ContextTracingJob job) {
			//no-op
		}
	};
}
