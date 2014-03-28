/**
 * 
 */
package org.semanticweb.elk.alc.saturation;

import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.LocalConclusion;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface BacktrackingListener {

	public void notifyBacktracking(Context context, LocalConclusion revertingConclusion);
	
	public void notifyEndOfBacktracking(Context context, LocalConclusion lastBacktracked);
	
	public static final BacktrackingListener DUMMY = new BacktrackingListener() {
		
		@Override
		public void notifyBacktracking(Context context, LocalConclusion conclusionToBackTrack) {
			// no-op
		}

		@Override
		public void notifyEndOfBacktracking(Context context, LocalConclusion lastBacktracked) {
			// no-op
		}
	};
}
