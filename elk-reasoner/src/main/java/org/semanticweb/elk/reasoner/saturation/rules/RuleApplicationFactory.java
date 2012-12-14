/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.saturation.rules;

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.indexing.hierarchy.BasicDecompositionRuleApplicationVisitor;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.ContextCreationListener;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.RuleAndConclusionStatistics;
import org.semanticweb.elk.reasoner.saturation.conclusions.CombinedConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionApplicationVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionInsertionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionSourceUnsaturationVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.CountingConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.PreprocessedConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.TimedConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationFactory.Engine;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorFactory;
import org.semanticweb.elk.util.logging.CachedTimeThread;

/**
 * The factory for engines for concurrently computing the saturation of class
 * expressions. This is the class that implements the application of inference
 * rules.
 * 
 * @author Frantisek Simancik
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch
 * 
 */
public class RuleApplicationFactory implements
		InputProcessorFactory<IndexedClassExpression, Engine> {

	// logger for this class
	protected static final Logger LOGGER_ = Logger
			.getLogger(RuleApplicationFactory.class);

	static final boolean COLLECT_CONCLUSION_COUNTS = LOGGER_.isDebugEnabled();
	static final boolean COLLECT_CONCLUSION_TIMES = LOGGER_.isDebugEnabled();
	static final boolean COLLECT_RULE_COUNTS = true;// LOGGER_.isDebugEnabled();
	static final boolean COLLECT_RULE_TIMES = true;// LOGGER_.isDebugEnabled();

	final SaturationState saturationState;

	/**
	 * The {@link ThisStatistics} aggregated for all workers
	 */
	private final RuleAndConclusionStatistics aggregatedStats_;

	private final boolean trackModifiedContexts_;

	public RuleApplicationFactory(final SaturationState saturationState) {
		this(saturationState, false);
	}

	public RuleApplicationFactory(final SaturationState saturationState,
			boolean trackModifiedContexts) {
		this.aggregatedStats_ = new RuleAndConclusionStatistics();
		this.saturationState = saturationState;
		this.trackModifiedContexts_ = trackModifiedContexts;
	}

	@Override
	public Engine getEngine() {
		return new Engine();
	}

	public Engine getEngine(ContextCreationListener listener) {
		return new Engine(listener);
	}

	@Override
	public void finish() {
		aggregatedStats_.check(LOGGER_);
	}

	public RuleAndConclusionStatistics getStatistics() {
		return aggregatedStats_;
	}

	static ContextCreationListener getEngineListener(
			final ContextCreationListener listener,
			final RuleAndConclusionStatistics factoryStats) {
		return new ContextCreationListener() {
			@Override
			public void notifyContextCreation(Context newContext) {
				factoryStats.countCreatedContexts++;
				listener.notifyContextCreation(newContext);
			}
		};
	}
	
	/**
	 * 
	 * @param localStatistics
	 * @return
	 */
	static RuleApplicationVisitor getEngineCompositionRuleApplicationVisitor(
			RuleAndConclusionStatistics localStatistics) {
		RuleApplicationVisitor ruleAppVisitor = new BasicCompositionRuleApplicationVisitor();
		RuleStatistics ruleStats = localStatistics.getRuleStatistics();

		if (COLLECT_RULE_COUNTS) {
			ruleAppVisitor = new CombinedRuleApplicationVisitor(
					ruleAppVisitor, new CountingRuleApplicationVisitor(
							ruleStats));
		}

		if (COLLECT_RULE_TIMES) {
			ruleAppVisitor = TimeRuleApplicationVisitor.getTimeCompositionRuleApplicationVisitor(ruleAppVisitor, ruleStats);
		}

		return ruleAppVisitor;
	}	
	
	static DecompositionRuleApplicationVisitor getEngineDecompositionRuleApplicationVisitor(
			RuleAndConclusionStatistics localStatistics) {
		DecompositionRuleApplicationVisitor ruleAppVisitor = new BasicDecompositionRuleApplicationVisitor();
		RuleStatistics ruleStats = localStatistics.getRuleStatistics();

		if (COLLECT_RULE_COUNTS) {
			ruleAppVisitor = new CombinedDecompositionRuleApplicationVisitor(
					ruleAppVisitor, new CountingRuleApplicationVisitor(
							ruleStats));
		}

		if (COLLECT_RULE_TIMES) {
			ruleAppVisitor = TimeRuleApplicationVisitor.getTimeDecompositionRuleApplicationVisitor(ruleAppVisitor, ruleStats);
		}

		return ruleAppVisitor;
	}	

	/**
	 * 
	 */
	public class Engine implements InputProcessor<IndexedClassExpression>,
			RuleEngine {

		private final SaturationState.Writer saturationStateWriter_;

		private final ConclusionVisitor<?> conclusionProcessor_;

		/**
		 * Local {@link ThisStatistics} created for every worker
		 */
		protected final RuleAndConclusionStatistics localStatistics;

		protected Engine(SaturationState.Writer saturationStateWriter,
				RuleAndConclusionStatistics localStatistics) {
			this.conclusionProcessor_ = getConclusionProcessor(
					saturationStateWriter, localStatistics);
			this.saturationStateWriter_ = saturationStateWriter;
			this.localStatistics = localStatistics;
		}

		protected Engine(SaturationState.Writer saturationStateWriter) {
			this(saturationStateWriter, new RuleAndConclusionStatistics());
		}

		protected Engine() {
			this(saturationState.getWriter());
		}

		protected Engine(final ContextCreationListener listener,
				final RuleAndConclusionStatistics factoryStats) {
			this(saturationState.getWriter(
					getEngineListener(listener, factoryStats),
					getEngineCompositionRuleApplicationVisitor(factoryStats)),
					factoryStats);
		}

		protected Engine(final ContextCreationListener listener) {
			this(listener, new RuleAndConclusionStatistics());
		}

		@Override
		public void submit(IndexedClassExpression job) {
			saturationStateWriter_.getCreateContext(job);
		}

		@Override
		public void process() {
			localStatistics.timeContextProcess -= CachedTimeThread.currentTimeMillis;
			for (;;) {
				if (Thread.currentThread().isInterrupted())
					break;

				Context nextContext = saturationStateWriter_.pollForContext();

				if (nextContext == null) {
					break;
				} else {
					process(nextContext);
				}
			}
			localStatistics.timeContextProcess += CachedTimeThread.currentTimeMillis;
		}

		@Override
		public void finish() {
			aggregatedStats_.add(localStatistics);
			localStatistics.reset();
		}

		/**
		 * Process all scheduled items in the given context
		 * 
		 * @param context
		 *            the context in which to process the scheduled items
		 */
		protected void process(Context context) {
			localStatistics.contContextProcess++;
			for (;;) {
				Conclusion conclusion = context.takeToDo();
				if (conclusion == null)
					return;
				conclusion.accept(conclusionProcessor_, context);
			}
		}

		/**
		 * Filters the {@link ConclusionVisitor} that applies inference rules to
		 * {@link Conclusion}s by wrapping, if necessary, with the code
		 * producing statistics
		 * 
		 * @param ruleProcessor
		 *            the {@link ConclusionVisitor} to be wrapped
		 * @param localStatistics
		 *            the object accumulating local statistics for this worker
		 * @return the input {@link ConclusionVisitor} possibly wrapped with
		 *         some code for producing statistics
		 */
		protected ConclusionVisitor<Boolean> filterRuleConclusionProcessor(
				ConclusionVisitor<Boolean> ruleProcessor,
				RuleAndConclusionStatistics localStatistics) {
			if (COLLECT_CONCLUSION_COUNTS) {
				return new PreprocessedConclusionVisitor<Boolean>(
						new CountingConclusionVisitor(
								localStatistics.getConclusionStatistics()
										.getUsedConclusionCounts()),
						ruleProcessor);
			} else
				return ruleProcessor;
		}

		/**
		 * Returns the base {@link ConclusionVisitor} that performs processing
		 * of {@code Conclusion}s within a {@link Context}. This can be further
		 * wrapped in some other code.
		 * 
		 * @param saturationStateWriter
		 *            the {@link SaturationState.Writer} using which one can
		 *            produce new {@link Conclusion}s in {@link Context}s
		 * @param localStatistics
		 *            the object accumulating local statistics for this worker
		 * @return the base {@link ConclusionVisitor} that performs processing
		 *         of {@code Conclusion}s within a {@link Context}
		 */
		protected ConclusionVisitor<Boolean> getBaseConclusionProcessor(
				SaturationState.Writer saturationStateWriter,
				RuleAndConclusionStatistics localStatistics) {
			
			return new CombinedConclusionVisitor(
					new ConclusionInsertionVisitor(),
					filterRuleConclusionProcessor(
							new ConclusionApplicationVisitor(
									saturationStateWriter,
									getEngineCompositionRuleApplicationVisitor(localStatistics),
									getEngineDecompositionRuleApplicationVisitor(localStatistics)),
							localStatistics));
		}



		/**
		 * Returns the final {@link ConclusionVisitor} that is used by this
		 * {@link Engine} for processing {@code Conclusion}s within
		 * {@link Context}s
		 * 
		 * @param saturationStateWriter
		 *            the {@link SaturationState.Writer} using which one can
		 *            produce new {@link Conclusion}s in {@link Context}s
		 * @param localStatistics
		 *            the object accumulating local statistics for this worker
		 * @return the final {@link ConclusionVisitor} that is used by this
		 *         {@link Engine} for processing {@code Conclusion}s within
		 *         {@link Context}s
		 */
		protected ConclusionVisitor<?> getConclusionProcessor(
				SaturationState.Writer saturationStateWriter,
				RuleAndConclusionStatistics localStatistics) {
			ConclusionVisitor<Boolean> result = getBaseConclusionProcessor(
					saturationStateWriter, localStatistics);
			if (trackModifiedContexts_)
				result = new CombinedConclusionVisitor(result,
						new ConclusionSourceUnsaturationVisitor(
								saturationStateWriter));
			if (COLLECT_CONCLUSION_COUNTS) {
				result = new PreprocessedConclusionVisitor<Boolean>(
						new CountingConclusionVisitor(
								localStatistics.getConclusionStatistics()
										.getProcessedConclusionCounts()),
						result);
			}
			if (COLLECT_CONCLUSION_TIMES)
				return new TimedConclusionVisitor(
						localStatistics.getConclusionStatistics()
								.getConclusionTimers(),
						result);
			else
				return result;
		}
	}

}
