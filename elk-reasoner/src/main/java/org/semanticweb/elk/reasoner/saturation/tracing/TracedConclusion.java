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
public interface TracedConclusion extends Conclusion {

	public <R, C> R acceptTraced(TracedConclusionVisitor<R, C> visitor, C parameter);
	
	/**
	 * Returns the context in which the inference has been made (may be
	 * different from the context to which the conclusion logically belongs).
	 * 
	 * @param defaultContext
	 * @return
	 */
	public Context getInferenceContext(Context defaultContext);

}
