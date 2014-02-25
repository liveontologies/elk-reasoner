/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.ContextCreatingSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.ContextCreationListener;
import org.semanticweb.elk.reasoner.saturation.ContextModificationListener;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.tracing.LocalTracingSaturationState.TracedContext;
import org.semanticweb.elk.reasoner.saturation.tracing.factories.ContextTracingFactory;
import org.semanticweb.elk.reasoner.saturation.tracing.factories.ContextTracingJob;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.InferenceVisitor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Non-recursively visits all inferences for a given conclusion and traces the
 * context, if necessary. This implementation is single-threaded and synchronous.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class OnDemandTracingReader extends DelegatingTraceReader {
	
	private static final Logger LOGGER_ = LoggerFactory.getLogger(OnDemandTracingReader.class);

	private final ContextCreatingSaturationStateWriter<TracedContext> tracingContextWriter_;
	
	private final ContextTracingFactory tracingFactory_;
	
	public OnDemandTracingReader(
			SaturationState<TracedContext> tracingState,
			TraceStore.Reader inferenceReader,
			ContextTracingFactory tracingFactory) {
		super(inferenceReader);
		tracingContextWriter_  = tracingState.getContextCreatingWriter(ContextCreationListener.DUMMY, ContextModificationListener.DUMMY);
		tracingFactory_ = tracingFactory;
	}
	
	@Override
	public void accept(final IndexedClassExpression root, final Conclusion conclusion, final InferenceVisitor<?, ?> visitor) {
		IndexedClassExpression conclusionContextRoot = conclusion.getSourceRoot(root);
		TracedContext tracedContext = tracingContextWriter_.getCreateContext(conclusionContextRoot);	
		
		while (!tracedContext.isInitialized() || !tracedContext.isSaturated()) {
			LOGGER_.trace("Need to trace {} to read inferences for {}", tracedContext, conclusion);
			
			InputProcessor<ContextTracingJob> tracingEngine = tracingFactory_.getEngine();
			//the context needs to be traced.
			//we don't care if it is *being* traced since the factory will handle it. 
			tracingEngine.submit(new ContextTracingJob(tracedContext.getRoot()));

			try {
				tracingEngine.process();
			} catch (InterruptedException e) {
				return;
			}
			finally {
				tracingEngine.finish();
			}
		}

		reader.accept(root, conclusion, visitor);
	}
}
