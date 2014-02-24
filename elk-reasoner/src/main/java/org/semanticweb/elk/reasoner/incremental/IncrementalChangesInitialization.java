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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.reasoner.ReasonerComputationWithInputs;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.ContextModificationListener;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.SaturationUtils;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.contextinit.LinkedContextInitRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ChainableSubsumerRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.LinkedSubsumerRule;
import org.semanticweb.elk.util.concurrent.computation.BaseInputProcessor;
import org.semanticweb.elk.util.concurrent.computation.ComputationExecutor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorFactory;
import org.semanticweb.elk.util.logging.CachedTimeThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Goes through the input class expressions and puts each context's superclass
 * for which there are changes into the ToDo queue
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 */
public class IncrementalChangesInitialization
		extends
		ReasonerComputationWithInputs<ArrayList<Context>, ContextInitializationFactory> {

	public IncrementalChangesInitialization(
			Collection<ArrayList<Context>> inputs,
			LinkedContextInitRule changedInitRules,
			Map<IndexedClassExpression, ChainableSubsumerRule> changes,
			SaturationState state, ComputationExecutor executor,
			SaturationStatistics stageStats, int maxWorkers,
			ProgressMonitor progressMonitor) {
		super(inputs, new ContextInitializationFactory(state, changes,
				changedInitRules, stageStats), executor, maxWorkers,
				progressMonitor);
	}
}

/**
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
class ContextInitializationFactory
		implements
		InputProcessorFactory<ArrayList<Context>, InputProcessor<ArrayList<Context>>> {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ContextInitializationFactory.class);

	private final SaturationState saturationState_;
	private final Map<IndexedClassExpression, ? extends LinkedSubsumerRule> indexChanges_;
	private final IndexedClassExpression[] indexChangesKeys_;
	private final LinkedContextInitRule changedGlobalRuleHead_;
	private AtomicInteger ruleHits = new AtomicInteger(0);
	private final SaturationStatistics stageStatistics_;

	public ContextInitializationFactory(
			SaturationState state,
			Map<IndexedClassExpression, ? extends LinkedSubsumerRule> indexChanges,
			LinkedContextInitRule changedGlobalRuleHead,
			SaturationStatistics stageStats) {

		saturationState_ = state;
		indexChanges_ = indexChanges;
		indexChangesKeys_ = new IndexedClassExpression[indexChanges.keySet()
				.size()];
		indexChanges.keySet().toArray(indexChangesKeys_);
		changedGlobalRuleHead_ = changedGlobalRuleHead;
		stageStatistics_ = stageStats;
	}

	@Override
	public InputProcessor<ArrayList<Context>> getEngine() {
		return getEngine(getBaseContextProcessor());
	}

	private ContextProcessor getBaseContextProcessor() {

		final SaturationStatistics localStatistics = new SaturationStatistics();

		final RuleVisitor ruleAppVisitor = SaturationUtils
				.getStatsAwareRuleVisitor(localStatistics.getRuleStatistics());
		final SaturationStateWriter saturationStateWriter = SaturationUtils
				.getStatAwareWriter(
						saturationState_.getContextModifyingWriter(),
						localStatistics);

		localStatistics.getConclusionStatistics().startMeasurements();

		return new ContextProcessor() {

			int localRuleHits = 0;

			@Override
			public void process(Context context) {
				// apply all changed context initialization rules
				// TODO: do the initialization using the context initialization
				// conclusion
				LinkedContextInitRule nextGlobalRule = changedGlobalRuleHead_;
				while (nextGlobalRule != null) {
					if (LOGGER_.isTraceEnabled())
						LOGGER_.trace(context + ": applying rule "
								+ nextGlobalRule.getName());
					nextGlobalRule.accept(ruleAppVisitor, null, context,
							saturationStateWriter);
					nextGlobalRule = nextGlobalRule.next();
				}
				// apply all changed rules for indexed class expressions
				Set<IndexedClassExpression> subsumers = context.getSubsumers();
				if (subsumers.size() > indexChangesKeys_.length >> 2) {
					// iterate over changes, check subsumers
					for (int j = 0; j < indexChangesKeys_.length; j++) {
						IndexedClassExpression changedICE = indexChangesKeys_[j];
						if (subsumers.contains(changedICE)) {
							applyLocalRules(context, changedICE);
						}
					}
				} else {
					// iterate over subsumers, check changes
					for (IndexedClassExpression changedICE : subsumers) {
						applyLocalRules(context, changedICE);
					}
				}
			}

			@Override
			public void finish() {
				stageStatistics_.add(localStatistics);
				ruleHits.addAndGet(localRuleHits);
			}

			private void applyLocalRules(Context context,
					IndexedClassExpression changedICE) {
				LinkedSubsumerRule nextLocalRule = indexChanges_
						.get(changedICE);
				if (nextLocalRule != null) {
					localRuleHits++;

					LOGGER_.trace("{}: applying rules for {}", context,
							changedICE);
				}
				while (nextLocalRule != null) {
					nextLocalRule.accept(ruleAppVisitor, changedICE, context,
							saturationStateWriter);
					nextLocalRule = nextLocalRule.next();
				}
			}
		};
	}

	private InputProcessor<ArrayList<Context>> getEngine(
			final ContextProcessor baseProcessor) {
		if (SaturationUtils.COLLECT_PROCESSING_TIMES) {
			return new TimedContextCollectionProcessor(baseProcessor,
					stageStatistics_.getIncrementalProcessingStatistics());
		}
		// else
		return new ContextCollectionProcessor(baseProcessor);

	}

	@Override
	public void finish() {
		if (LOGGER_.isDebugEnabled())
			LOGGER_.debug("Rule hits: " + ruleHits.get());
	}

	/**
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	private static class TimedContextCollectionProcessor extends
			BaseInputProcessor<ArrayList<Context>> {

		private final ContextProcessor contextProcessor_;

		private final IncrementalProcessingStatistics stageStats_;

		private final IncrementalProcessingStatistics localStats_ = new IncrementalProcessingStatistics();

		private int procNumber_ = 0;

		TimedContextCollectionProcessor(ContextProcessor baseProcessor,
				IncrementalProcessingStatistics stageStats) {
			contextProcessor_ = new TimedContextProcessor(baseProcessor,
					localStats_);
			stageStats_ = stageStats;
			localStats_.startMeasurements();
		}

		@Override
		protected void process(ArrayList<Context> contexts) {
			long ts = CachedTimeThread.getCurrentTimeMillis();
			int contextCount = 0;
			int subsumerCount = 0;

			procNumber_++;

			for (Context context : contexts) {
				contextProcessor_.process(context);
				contextCount++;
				subsumerCount += context.getSubsumers().size();
			}

			localStats_.changeInitContextCollectionProcessingTime += (CachedTimeThread
					.getCurrentTimeMillis() - ts);
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
	 *         pavel.klinov@uni-ulm.de
	 */
	private static class ContextCollectionProcessor extends
			BaseInputProcessor<ArrayList<Context>> {

		private final ContextProcessor contextProcessor_;

		ContextCollectionProcessor(ContextProcessor contextProcessor) {
			contextProcessor_ = contextProcessor;
		}

		@Override
		protected void process(ArrayList<Context> contexts) {
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
	 *         pavel.klinov@uni-ulm.de
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
	 *         pavel.klinov@uni-ulm.de
	 */
	private static class TimedContextProcessor implements ContextProcessor {

		private final IncrementalProcessingStatistics localStats_;

		private final ContextProcessor processor_;

		TimedContextProcessor(ContextProcessor p,
				IncrementalProcessingStatistics localStats) {
			processor_ = p;
			localStats_ = localStats;
			localStats_.startMeasurements();
		}

		@Override
		public void process(Context context) {
			long ts = CachedTimeThread.getCurrentTimeMillis();

			processor_.process(context);

			localStats_.changeInitContextProcessingTime += (CachedTimeThread
					.getCurrentTimeMillis() - ts);
		}

		@Override
		public void finish() {
			processor_.finish();
			// no need to add the local stats to the stage-level stats, as it's
			// done by the caller
		}
	}

}
