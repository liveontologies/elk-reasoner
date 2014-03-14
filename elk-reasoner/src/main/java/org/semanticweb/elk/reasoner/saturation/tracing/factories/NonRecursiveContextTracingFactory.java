/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.factories;

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

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturationFactory;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturationNoInputFactory;
import org.semanticweb.elk.reasoner.saturation.ContextCreatingSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.ContextCreationListener;
import org.semanticweb.elk.reasoner.saturation.ContextModificationListener;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.ContextInitializationImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationAdditionFactory;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationFactory;
import org.semanticweb.elk.reasoner.saturation.tracing.LocalTracingSaturationState.TracedContext;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceStore;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.collections.HashListMultimap;
import org.semanticweb.elk.util.collections.Multimap;
import org.semanticweb.elk.util.collections.MultimapQueue;
import org.semanticweb.elk.util.collections.MultimapQueueImpl;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basically a wrapper around a {@link ClassExpressionSaturationFactory} with a
 * {@link CycleDroppingRuleApplicationFactory} as the rule application factory
 * but transparently handles the situation when the context is being tracing
 * when another tracing job for the same root comes over. It puts it in a
 * pending queue and sends notifications once the context has been traced.
 * 
 * TODO When do we mark the contexts in the main state saturated on-demand as saturated?
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class NonRecursiveContextTracingFactory implements ContextTracingFactory {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(NonRecursiveContextTracingFactory.class);

	/**
	 * The factory for context saturation with the tracing-enabled rule
	 * application factory.
	 */
	private final ClassExpressionSaturationNoInputFactory tracingFactory_;
	/**
	 * This factory saturates contexts which were not saturated before due to non-redundancy
	 */
	private final ClassExpressionSaturationNoInputFactory saturationFactory_;

	private final SaturationState<TracedContext> tracingState_;
	/**
	 * Pending tracing jobs indexed by the context roots (there could be more
	 * than one job for the same context)
	 */
	private final MultimapQueue<IndexedClassExpression, ContextTracingJob> pendingJobsByRoot_;
	
	private final SaturationState<?> mainSaturationState_;
	
	public NonRecursiveContextTracingFactory(
			SaturationState<?> saturationState,
			SaturationState<TracedContext> tracingState, TraceStore traceStore) {
		// this factory applies all local rules (non-redundant and redundant)
		RuleApplicationFactory<TracedContext> ruleTracingFactory = new CycleBlockingRuleApplicationFactory(
				saturationState, tracingState, traceStore);

		mainSaturationState_ = saturationState;
		tracingState_ = tracingState;
		tracingFactory_ = new ClassExpressionSaturationNoInputFactory(
				ruleTracingFactory);
		// this factory applies only non-redundant rules
		RuleApplicationFactory<?> ruleAppFactory = new RuleApplicationAdditionFactory(saturationState);
		
		saturationFactory_ = new ClassExpressionSaturationNoInputFactory(ruleAppFactory);
		pendingJobsByRoot_ = new MultimapQueueImpl<IndexedClassExpression, ContextTracingJob>(new HashListMultimap<IndexedClassExpression, ContextTracingJob>());
	}

	@Override
	public Engine getEngine() {
		return new Engine();
	}

	@Override
	public void finish() {
		tracingFactory_.finish();
		saturationFactory_.finish();
	}

	private void notifyCallers(Collection<ContextTracingJob> pendingJobs) {
		for (ContextTracingJob job : pendingJobs) {
			job.getCallback().notifyFinished(job);
		}
	}

	private synchronized void addPendingJob(ContextTracingJob job) {
		pendingJobsByRoot_.add(job.getInput(), job);
	}
	
	private synchronized Map.Entry<IndexedClassExpression, Collection<ContextTracingJob>> takeTracingJob() {
		return pendingJobsByRoot_.takeEntry();
	}
	
	@Override
	public SaturationStatistics getStatistics() {
		return tracingFactory_.getRuleAndConclusionStatistics();
	}

	/**
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	public class Engine implements InputProcessor<ContextTracingJob> {

		private final ClassExpressionSaturationNoInputFactory.Engine tracingEngine_ = tracingFactory_
				.getEngine();

		private final ClassExpressionSaturationNoInputFactory.Engine saturationEngine_ = saturationFactory_
				.getEngine(new ContextModificationListener() {
					/*
					 * All contexts modified during the saturation completion
					 * procedure should be treated as saturated when the
					 * procedure finishes. We collect them using this visitor.
					 * 
					 * TODO It'd be better if we could simply mark these
					 * contexts as saturated when the saturation engine
					 * finishes. I.e. if the factory could mark as saturated
					 * those contexts which got activated during processing.
					 */
					@Override
					public void notifyContextModification(Context context) {
						markAsModified(context.getRoot());
					}
				});

		private final ContextCreatingSaturationStateWriter<TracedContext> tracingContextWriter_ = tracingState_
				.getContextCreatingWriter(ContextCreationListener.DUMMY,
						ContextModificationListener.DUMMY);
		
		private final SaturationStateWriter<?> mainStateWriter_ = mainSaturationState_.getContextCreatingWriter();
		
		private Set<IndexedClassExpression> saturatedContexts_;
		
		private void markAsModified(IndexedClassExpression root) {
			if (saturatedContexts_ == null) {
				saturatedContexts_ = new ArrayHashSet<IndexedClassExpression>();
			}
			
			saturatedContexts_.add(root);
		}

		@Override
		public void submit(ContextTracingJob job) {
			IndexedClassExpression root = job.getInput();
			TracedContext context = tracingContextWriter_
					.getCreateContext(root);

			if (!context.isInitialized() || !context.isSaturated()) {
				addPendingJob(job);
				// if the context is being traced now (by the same factory), do
				// nothing as all notifications will be sent when tracing
				// finishes
				if (context.beingTracedCompareAndSet(false, true)) {
					LOGGER_.trace("{} first submitted for tracing", root);
				}
			} else {
				// if the context has been traced before, notify the caller
				// immediately
				job.getCallback().notifyFinished(job);
			}
		}

		@Override
		public void process() throws InterruptedException {
			for (;;) {
				Map.Entry<IndexedClassExpression, Collection<ContextTracingJob>> nextTracingJob = takeTracingJob();
				
				if (nextTracingJob == null) {
					break;
				}
				
				// the main trace'n'saturate loop
				IndexedClassExpression rootToTrace = nextTracingJob.getKey();
				
				tracingContextWriter_.produce(rootToTrace, new ContextInitializationImpl(mainSaturationState_.getOntologyIndex()));
				
				for (;;) {
					LOGGER_.trace("{}: started (re)tracing", rootToTrace);
					
					tracingEngine_.process();
					//now check if some gaps should be filled before we finish tracing
					TracedContext context = tracingState_.getContext(rootToTrace);
					Multimap<IndexedClassExpression, Conclusion> missingConlusions = context.getMissingConclusions();
					
					if (missingConlusions.isEmpty()) {
						//yay, we're done with tracing this context
						finishTracing(rootToTrace, nextTracingJob.getValue());
						break;
					}
					
					LOGGER_.trace(
							"{} will resume tracing after some missing contexts are saturated: {}",
							rootToTrace, missingConlusions.keySet());
					// Otherwise, need to complete the main closure by
					// saturating contexts which did not exist before. Since we
					// don't know yet if these contexts will be used for
					// tracing, we first saturate them under non-redundant
					// rules. 
					initSaturationCompletion(missingConlusions);
					saturationEngine_.process();
					// now, resume tracing by applying redundant rules.
					resumeTracing(context);
					context.clearMissingConclusions();
				}
			}
		}
		
		private final void resumeTracing(TracedContext context) {
			context.addSaturatedMainContexts(saturatedContexts_);
			saturatedContexts_.clear();
			
			for (IndexedClassExpression root : context.getMissingConclusions().keySet()) {
				for (Conclusion conclusion : context.getMissingConclusions().get(root)) {
					tracingContextWriter_.produce(root, conclusion);
				}
			}
		}
		
		private void finishTracing(IndexedClassExpression root, Collection<ContextTracingJob> pendingJobs) {
			TracedContext context = tracingState_.getContext(root);
			
			LOGGER_.trace("{} finished tracing", root);
			
			context.setSaturated(true);
			context.beingTracedCompareAndSet(true, false);
			// cleaning up the auxiliary data structures
			context.clearBlockedInferences();
			context.clearMissingConclusions();
			context.clearSaturatedMainContexts();
			notifyCallers(pendingJobs);
		}

		private void initSaturationCompletion(Multimap<IndexedClassExpression, Conclusion> missingConlusions) {
			for (IndexedClassExpression root : missingConlusions.keySet()) {
				markAsModified(root);
				
				for (Conclusion conclusion : missingConlusions.get(root)) {
					mainStateWriter_.produce(root, conclusion);
				}
			}
		}

		@Override
		public void finish() {
			tracingEngine_.finish();
			saturationEngine_.finish();
		}
	}

}
