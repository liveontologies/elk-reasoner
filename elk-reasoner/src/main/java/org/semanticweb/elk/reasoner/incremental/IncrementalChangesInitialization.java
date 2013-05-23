/**
 * 
 */
package org.semanticweb.elk.reasoner.incremental;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.reasoner.ReasonerComputation;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.BasicSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.ContextModificationListener;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.SaturationUtils;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ChainableRule;
import org.semanticweb.elk.reasoner.saturation.rules.LinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationVisitor;
import org.semanticweb.elk.util.collections.LazySetIntersection;
import org.semanticweb.elk.util.concurrent.computation.BaseInputProcessor;
import org.semanticweb.elk.util.concurrent.computation.ComputationExecutor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorFactory;
import org.semanticweb.elk.util.logging.CachedTimeThread;

/**
 * Goes through the input class expressions and puts each context's superclass
 * for which there're changes into the ToDo queue
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 */
public class IncrementalChangesInitialization extends
		ReasonerComputation<Collection<Context>, ContextInitializationFactory> {

	public IncrementalChangesInitialization(
			Collection<Collection<Context>> inputs,
			ChainableRule<Context> changedGlobalRules,
			Map<IndexedClassExpression, ChainableRule<Context>> changes,
			SaturationState state, ComputationExecutor executor,
			SaturationStatistics stageStats,
			int maxWorkers,
			ProgressMonitor progressMonitor) {
		super(inputs, new ContextInitializationFactory(state, changes,
				changedGlobalRules, stageStats), executor, maxWorkers,
				progressMonitor);
	}
}

/**
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
class ContextInitializationFactory
		implements
		InputProcessorFactory<Collection<Context>, InputProcessor<Collection<Context>>> {

	private static final Logger LOGGER_ = Logger
			.getLogger(ContextInitializationFactory.class);

	private final SaturationState saturationState_;
	private final Map<IndexedClassExpression, ? extends LinkRule<Context>> indexChanges_;
	private final LinkRule<Context> changedGlobalRuleHead_;
	private final SaturationStatistics stageStatistics_;

	public ContextInitializationFactory(
			SaturationState state,
			Map<IndexedClassExpression, ? extends LinkRule<Context>> indexChanges,
			LinkRule<Context> changedGlobalRuleHead,
			SaturationStatistics stageStats) {
		
		saturationState_ = state;
		indexChanges_ = indexChanges;
		changedGlobalRuleHead_ = changedGlobalRuleHead;
		stageStatistics_ = stageStats;
	}

	@Override
	public InputProcessor<Collection<Context>> getEngine() {
		return getEngine(getBaseContextProcessor());
	}

	private ContextProcessor getBaseContextProcessor() {
		
		final SaturationStatistics localStatistics = new SaturationStatistics();
		final ConclusionVisitor<?> conclusionVisitor = SaturationUtils.addStatsToConclusionVisitor(localStatistics.getConclusionStatistics());
		final RuleApplicationVisitor ruleAppVisitor = SaturationUtils.getStatsAwareCompositionRuleAppVisitor(localStatistics.getRuleStatistics());
		final BasicSaturationStateWriter saturationStateWriter = saturationState_.getWriter(ContextModificationListener.DUMMY, conclusionVisitor);
		
		localStatistics.getConclusionStatistics().startMeasurements();
		
		return new ContextProcessor() {
			
			@Override
			public void process(Context context) {
				// apply all changed global context rules
				LinkRule<Context> nextGlobalRule = changedGlobalRuleHead_;
				while (nextGlobalRule != null) {
					if (LOGGER_.isTraceEnabled())
						LOGGER_.trace(context + ": applying rule "
								+ nextGlobalRule.getName());
					nextGlobalRule.accept(ruleAppVisitor,
							saturationStateWriter, context);
					nextGlobalRule = nextGlobalRule.next();
				}

				// apply all changed rules for indexed class expressions
				for (IndexedClassExpression changedICE : new LazySetIntersection<IndexedClassExpression>(
						indexChanges_.keySet(), context.getSubsumers())) {
					if (LOGGER_.isTraceEnabled())
						LOGGER_.trace(context + ": applying rules for "
								+ changedICE);
					LinkRule<Context> nextLocalRule = indexChanges_
							.get(changedICE);
					while (nextLocalRule != null) {
						nextLocalRule.accept(ruleAppVisitor,
								saturationStateWriter, context);
						nextLocalRule = nextLocalRule.next();
					}
				}
			}
			
			@Override
			public void finish() {
				stageStatistics_.add(localStatistics);
			}
		};
	}

	private InputProcessor<Collection<Context>> getEngine(final ContextProcessor baseProcessor) {	
		if (SaturationUtils.COLLECT_PROCESSING_TIMES) {
			return new TimedContextCollectionProcessor(baseProcessor, stageStatistics_.getIncrementalProcessingStatistics());
		}
		else {
			return new ContextCollectionProcessor(baseProcessor);	
		}
		
		
	}

	@Override
	public void finish() {
	}
	
	/**
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	private static class TimedContextCollectionProcessor extends BaseInputProcessor<Collection<Context>> {
		
		private final ContextProcessor contextProcessor_;
		
		private final IncrementalProcessingStatistics stageStats_;
		
		private final IncrementalProcessingStatistics localStats_ = new IncrementalProcessingStatistics();
		
		private int procNumber_ = 0;
		
		TimedContextCollectionProcessor(ContextProcessor baseProcessor, IncrementalProcessingStatistics stageStats) {
			contextProcessor_ = new TimedContextProcessor(baseProcessor, localStats_);
			stageStats_ = stageStats;
			localStats_.startMeasurements();
		}
		
		@Override
		protected void process(Collection<Context> contexts) {
			long ts = CachedTimeThread.getCurrentTimeMillis();
			int contextCount = 0;
			int subsumerCount = 0;
			
			procNumber_++;
			
			for (Context context : contexts) {					
				contextProcessor_.process(context);
				contextCount++;
				subsumerCount += context.getSubsumers().size();
			}
			
			localStats_.changeInitContextCollectionProcessingTime += (CachedTimeThread.getCurrentTimeMillis() - ts);
			localStats_.countContexts += contextCount;
			
			if (contextCount > 0) {
				localStats_.countContextSubsumers += (subsumerCount / contextCount);
			}
		}
		
		@Override
		public void finish() {
			super.finish();
			contextProcessor_.finish();
			
			if (procNumber_ > 0) {
				localStats_.countContextSubsumers /= procNumber_;
			}
			
			stageStats_.add(localStats_);
		}
	}
	
	/**
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	private static class ContextCollectionProcessor extends BaseInputProcessor<Collection<Context>> {

		private final ContextProcessor contextProcessor_;
		
		ContextCollectionProcessor(ContextProcessor contextProcessor) {
			contextProcessor_ = contextProcessor;
		}
		
		@Override
		protected void process(Collection<Context> contexts) {
			for (Context context : contexts) {					
				contextProcessor_.process(context);
			}
		}
		
		@Override
		public void finish() {
			super.finish();
			contextProcessor_.finish();
		}
		
	}

	/**
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	private static interface ContextProcessor {
		
		public void process(Context context);
		
		public void finish();
	}
	
	/**
	 * Measures time it takes to init changes for a context
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	private static class TimedContextProcessor implements ContextProcessor {

		private final IncrementalProcessingStatistics localStats_;
		
		private final ContextProcessor processor_;
		
		TimedContextProcessor(ContextProcessor p, IncrementalProcessingStatistics localStats) {
			processor_ = p;
			localStats_ = localStats;
			localStats_.startMeasurements();
		}
		
		@Override
		public void process(Context context) {
			long ts = CachedTimeThread.getCurrentTimeMillis();
			
			processor_.process(context);
			
			localStats_.changeInitContextProcessingTime += (CachedTimeThread.getCurrentTimeMillis() - ts);
		}

		@Override
		public void finish() {
			processor_.finish();
			// no need to add the local stats to the stage-level stats, as it's
			// done by the caller
		}
		
	}

}
