/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation;

import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface ContextModificationListener {

	/**
	 * Invoked right after the context has been marked as saturated or not saturated
	 */
	public void notifyContextModification(Context context);
	
	public static final ContextModificationListener DUMMY = new ContextModificationListener() {
		
		@Override
		public void notifyContextModification(Context context) {
			//doesn't do anything
		}
	};
}
