/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.tracing.LocalTracingSaturationState.TracedContext;
import org.semanticweb.elk.reasoner.saturation.tracing.LocalTracingSaturationState.TracingWriter;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Non-recursively visits all inferences for a given conclusion and traces the
 * context, if necessary. This implementation is single-threaded.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class OnDemandTracingReader implements TraceStore.Reader {
	
	private static final Logger LOGGER_ = LoggerFactory.getLogger(OnDemandTracingReader.class);

	private final TracingWriter tracingContextWriter_;
	
	private final TraceStore.Reader inferenceReader_;
	
	private final ContextTracingFactory tracingFactory_;
	
	public OnDemandTracingReader(
			LocalTracingSaturationState tracingState,
			TraceStore.Reader inferenceReader,
			ContextTracingFactory tracingFactory) {
		inferenceReader_ = inferenceReader;
		tracingContextWriter_  = tracingState.getTracingWriter();
		tracingFactory_ = tracingFactory;
	}
	
	@Override
	public void accept(final Context context, final Conclusion conclusion, final TracedConclusionVisitor<?, ?> visitor) {
		Context conclusionContext = conclusion.getSourceContext(context);
		TracedContext tracedContext = tracingContextWriter_.getCreateContext(conclusionContext.getRoot());	
		
		while (!tracedContext.isSaturated()) {
			LOGGER_.trace("Need to trace {} to read inferences for {}", context, conclusion);
			
			InputProcessor<ContextTracingJob> tracingEngine = tracingFactory_.getEngine();
			//the context needs to be traced.
			//we don't care if it is *being* traced since the factory will handle it. 
			tracingEngine.submit(new ContextTracingJob(context.getRoot()));

			try {
				tracingEngine.process();
			} catch (InterruptedException e) {
				return;
			}
			finally {
				tracingEngine.finish();
			}
		}

		inferenceReader_.accept(context, conclusion, visitor);
	}
}
