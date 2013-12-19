/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface ContextTracer {

	public void accept(Conclusion conclusion, TracedConclusionVisitor<?,?> visitor);
	
	public boolean addInference(TracedConclusion conclusion);
}
