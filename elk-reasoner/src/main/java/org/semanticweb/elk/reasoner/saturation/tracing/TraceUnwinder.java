/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * TODO
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface TraceUnwinder {

	public void accept(Context context, Conclusion conclusion, ConclusionVisitor<?, Context> conclusionVisitor, TracedConclusionVisitor<?, Context> inferenceVisitor);
}
