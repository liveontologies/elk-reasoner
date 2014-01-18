/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.saturation.tracing.TracingSaturationState.TracedContext;

/**
 * TODO
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface ContextTracingPostProcessor {

	void process(TracedContext context, TraceStore traceStore);
}
