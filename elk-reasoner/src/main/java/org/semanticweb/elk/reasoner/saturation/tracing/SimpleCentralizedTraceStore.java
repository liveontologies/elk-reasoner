/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.Inference;
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
		public Iterable<Inference> getInferences(Context context, Conclusion conclusion) {
			ContextTracer tracer = storage_.get(context);
			
			return tracer == null ? Collections.<Inference>emptyList() : tracer.getInference(conclusion);
		}

		@Override
		public Iterable<Inference> getSubsumerInferences(Context context, 	IndexedClassExpression conclusion) {
			ContextTracer tracer = storage_.get(context);
			
			return tracer == null ? Collections.<Inference>emptyList() : tracer.getSubsumerInferences(conclusion);
		}

		@Override
		public Iterable<Inference> getBackwardLinkInferences(Context context, 	IndexedPropertyChain linkRelation, Context linkSource) {
			ContextTracer tracer = storage_.get(context);
			
			return tracer == null ? Collections.<Inference>emptyList() : tracer.getBackwardLinkInferences(linkRelation, linkSource);
		}
		
	}
	
	private class Writer implements TraceStore.Writer {

		@Override
		public boolean addInference(Context context, Conclusion conclusion, Inference inference) {
			ContextTracer tracer = storage_.get(context);
			
			LOGGER_.trace("Adding inference for {} in {}: {}", conclusion, context, inference);
			
			if (tracer == null) {
				tracer = new SimpleContextTraceStore();
				storage_.putIfAbsent(context, tracer);
			}
			
			return tracer.addInference(conclusion, inference);
		}
		
	}

}
