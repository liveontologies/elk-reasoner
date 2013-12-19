/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import java.util.concurrent.ConcurrentHashMap;

import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple {@link TraceStore} which uses centralized concurrent data structures to
 * store and retrieve {@link Inference}s.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class SimpleCentralizedTraceStore implements TraceStore {
	
	private final static Logger LOGGER_ = LoggerFactory.getLogger(SimpleCentralizedTraceStore.class);	

	private final ConcurrentHashMap<Context, ContextTracer> storage_ = new ConcurrentHashMap<Context, ContextTracer>();
	
	@Override
	public TraceStore.Reader getReader() {
		return new Reader();
	}

	@Override
	public TraceStore.Writer getWriter() {
		return new Writer();
	}
	
	/**
	 * 
	 *
	*/
	private class Reader implements TraceStore.Reader {

		@Override
		public void accept(Context context, Conclusion conclusion, TracedConclusionVisitor<?,?> visitor) {
			ContextTracer tracer = storage_.get(context);
			
			if (tracer != null) {
				tracer.accept(conclusion, visitor);
			}
		}
		
	}
	
	private class Writer implements TraceStore.Writer {

		@Override
		public boolean addInference(Context context, TracedConclusion conclusion) {
			ContextTracer tracer = storage_.get(context);
			
			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace("Writing inference for {} in {}: {}", conclusion, context, conclusion.acceptTraced(new InferencePrinter(), null));
			}
			
			if (tracer == null) {
				tracer = new SimpleContextTraceStore();
				storage_.putIfAbsent(context, tracer);
			}
			
			return tracer.addInference(conclusion);
		}
		
	}

}
