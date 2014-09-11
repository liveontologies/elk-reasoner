/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.concurrent.ArrayBlockingQueue;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.ContextCreatingSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.ContextCreationListener;
import org.semanticweb.elk.reasoner.saturation.ContextModificationListener;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.tracing.LocalTracingSaturationState.TracedContext;
import org.semanticweb.elk.reasoner.saturation.tracing.factories.ContextTracingFactory;
import org.semanticweb.elk.reasoner.saturation.tracing.factories.ContextTracingJob;
import org.semanticweb.elk.reasoner.saturation.tracing.factories.ContextTracingListener;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ClassInferenceVisitor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Non-recursively visits all inferences for a given conclusion and traces the
 * context, if necessary. This implementation is single-threaded, not thread-safe, and synchronous.
 * 
 * TODO make asynchronous and non-blocking (that would require changes in the trace unwinder).
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class OnDemandTracingReader extends DelegatingTraceReader {
	
	private static final Logger LOGGER_ = LoggerFactory.getLogger(OnDemandTracingReader.class);

	private final ContextCreatingSaturationStateWriter<TracedContext> tracingContextWriter_;
	
	private final ContextTracingFactory<ContextTracingJob> tracingFactory_;
	
	public OnDemandTracingReader(
			SaturationState<TracedContext> tracingState,
			TraceStore.Reader inferenceReader,
			ContextTracingFactory<ContextTracingJob> contextTracingFactory) {
		super(inferenceReader);
		tracingContextWriter_  = tracingState.getContextCreatingWriter(ContextCreationListener.DUMMY, ContextModificationListener.DUMMY);
		tracingFactory_ = contextTracingFactory;
	}
	
	@Override
	public void accept(final IndexedClassExpression root, final Conclusion conclusion, final ClassInferenceVisitor<IndexedClassExpression, ?> visitor) {
		IndexedClassExpression conclusionContextRoot = conclusion.getSourceRoot(root);
		TracedContext tracedContext = tracingContextWriter_.getCreateContext(conclusionContextRoot);	
		
		LOGGER_.trace("Reading inferences for {}", tracedContext);
		
		if (!tracedContext.isInitialized() || !tracedContext.isSaturated()) {
			LOGGER_.trace("Need to trace {} to read inferences for {}", tracedContext, conclusion);
			
			final ArrayBlockingQueue<ContextTracingJob> finishedJob = new ArrayBlockingQueue<ContextTracingJob>(1);
			InputProcessor<ContextTracingJob> tracingEngine = tracingFactory_.getEngine();
			//	the context needs to be traced.
			tracingEngine.submit(new ContextTracingJob(tracedContext.getRoot(), new ContextTracingListener() {
				
				@Override
				public void notifyFinished(ContextTracingJob job) {
					// the queue must be empty here
					finishedJob.add(job);
					LOGGER_.trace("{} is ready for inference reading", job.getInput());
				}
			}));

			try {
				tracingEngine.process();
			} catch (InterruptedException e) {
				LOGGER_.trace("Interrupted during on-demand tracing of {}", tracedContext);
				return;
			}
			
			try {
				// blocking here
				finishedJob.take();
			} catch (InterruptedException e) {
				LOGGER_.trace("Interrupted while waiting for tracing of {}", tracedContext);
				Thread.currentThread().interrupt();
				return;
			}

		}

		reader.accept(root, conclusion, visitor);
	}

}
