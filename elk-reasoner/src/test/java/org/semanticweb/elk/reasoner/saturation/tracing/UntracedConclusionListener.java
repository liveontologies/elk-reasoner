/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;

/**
 * Gets notifications if there're no inferences stored for a particular
 * {@link Conclusion}
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public interface UntracedConclusionListener {

	public void notifyUntraced(Conclusion conclusion, IndexedClassExpression root);
	
	public static final UntracedConclusionListener DUMMY = new UntracedConclusionListener() {

		@Override
		public void notifyUntraced(Conclusion conclusion, IndexedClassExpression root) {
			//no-op			
		}
		
	};
}
