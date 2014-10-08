/**
 * 
 */
package org.semanticweb.elk.reasoner.stages;

import org.semanticweb.elk.reasoner.saturation.tracing.RecursiveTraceUnwinder;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceStore;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceUnwinder;


/**
 * Similar to {@link ReasonerInferenceReader} but uses the internal
 * {@link RecursiveTraceUnwinder} which implementts recusive unwinding for
 * low-level inferences.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class RecursiveReasonerInferenceReader extends ReasonerInferenceReader {

	public RecursiveReasonerInferenceReader(AbstractReasonerState r) {
		super(r);
	}
	
	@Override
	protected TraceUnwinder getTraceUnwinder(TraceStore.Reader reader) {
		return new RecursiveTraceUnwinder(reader);
	}

}
