/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface UntracedConclusionListener {

	public void notifyUntraced(Conclusion conclusion, Context context);
	
	public static final UntracedConclusionListener DUMMY = new UntracedConclusionListener() {

		@Override
		public void notifyUntraced(Conclusion conclusion, Context context) {
			//no-op			
		}
		
	};
}
