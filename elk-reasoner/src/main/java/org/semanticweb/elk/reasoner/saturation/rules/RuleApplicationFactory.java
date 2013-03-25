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
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.BasicSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.ContextCreationListener;
import org.semanticweb.elk.reasoner.saturation.ContextModificationListener;
import org.semanticweb.elk.reasoner.saturation.ExtendedSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStateImpl;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.conclusions.CombinedConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionApplicationVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionInsertionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionSourceUnsaturationVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionStatistics;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.CountingConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.PreprocessedConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.TimedConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.context.ContextStatistics;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.logging.CachedTimeThread;

/**
 * The factory for engines for concurrently computing the saturation of class
 * expressions. This is the class that implements the application of inference
 * rules.
 * 
 * @author Frantisek Simancik
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch
 * @author Pavel Klinov
 * 
 */
public class RuleApplicationFactory {

	// logger for this class
	protected static final Logger LOGGER_ = Logger
			.getLogger(RuleApplicationFactory.class);

	static final boolean COLLECT_CONCLUSION_COUNTS = LOGGER_.isDebugEnabled();
	static final boolean COLLECT_CONCLUSION_TIMES = LOGGER_.isDebugEnabled();
	static final boolean COLLECT_RULE_COUNTS = LOGGER_.isDebugEnabled();
	static final boolean COLLECT_RULE_TIMES = LOGGER_.isDebugEnabled();

	final SaturationState saturationState;

	/**
	 * The {@link SaturationStatistics} aggregated for all workers
	 */
	private final SaturationStatistics aggregatedStats_;
	/**
	 * If true, the factory will keep track of contexts which get modified
	 * during saturation. This is needed, for example, for cleaning contexts
	 * modified during de-saturation or for cleaning taxonomy nodes which
	 * correspond to modified contexts (during de-saturation and re-saturation)
	 */
	private final boolean trackModifiedContexts_;
	
	public RuleApplicationFactory(final SaturationStateImpl saturationState) {
		this(saturationState, false);
	}

	public RuleApplicationFactory(final SaturationStateImpl saturationState,
			final boolean trackModifiedContexts) {
		this.aggregatedStats_ = new SaturationStatistics();
		this.saturationState = saturationState;
		this.trackModifiedContexts_ = true;
	}

	/*
	 * This method is supposed to be overridden in subclasses
	 */
	public BaseEngine getDefaultEngine(ContextCreationListener listener,
			ContextModificationListener modListener) {
		return new DefaultEngine(listener, modListener);
	}

	public void finish() {
		//aggregatedStats_.check(LOGGER_);
	}

	public SaturationStatistics getSaturationStatistics() {
		return aggregatedStats_;
	}

	
	static ContextCreationListener getEngineContextCreationListener(
			final ContextCreationListener listener,
			final ContextStatistics contextStats) {
		return new ContextCreationListener() {
			@Override
			public void notifyContextCreation(Context newContext) {
				contextStats.countCreatedContexts++;
				listener.notifyContextCreation(newContext);
			}
		};
	}
	
	static ContextModificationListener getEngineContextModificationListener(
			final ContextModificationListener listener,
			final ContextStatistics contextStats) {
		return new ContextModificationListener() {
			@Override
			public void notifyContextModification(Context context) {
				contextStats.countModifiedContexts++;
				listener.notifyContextModification(context);
			}
		};
	}	

	/**
	 * 
	 * @param localStatistics
	 * @return
	 */
	static RuleApplicationVisitor getEngineCompositionRuleApplicationVisitor(
			RuleStatistics localStatistics) {
		RuleApplicationVisitor ruleAppVisitor = new BasicCompositionRuleApplicationVisitor();

		if (COLLECT_RULE_COUNTS) {
			ruleAppVisitor = new RuleApplicationCounterVisitor(ruleAppVisitor,
					localStatistics.ruleCounter);
		}

		if (COLLECT_RULE_TIMES) {
			ruleAppVisitor = new RuleApplicationTimerVisitor(ruleAppVisitor,
					localStatistics.ruleTimer);
		}

		return ruleAppVisitor;
	}

	static DecompositionRuleApplicationVisitor getEngineDecompositionRuleApplicationVisitor(
			DecompositionRuleApplicationVisitor decompRuleAppVisitor,
			RuleStatistics localStatistics) {
		if (COLLECT_RULE_COUNTS) {
			decompRuleAppVisitor = new DecompositionRuleApplicationCounterVisitor(
					decompRuleAppVisitor, localStatistics.decompositionRuleCounter);
		}

		if (COLLECT_RULE_TIMES) {
			decompRuleAppVisitor = new DecompositionRuleApplicationTimerVisitor(
					decompRuleAppVisitor, localStatistics.decompositionRuleTimer);
		}

		return decompRuleAppVisitor;
	}
	
	
	static ConclusionVisitor<?> getEngineConclusionVisitor(
			ConclusionStatistics localStatistics) {
		return COLLECT_CONCLUSION_COUNTS ? new CountingConclusionVisitor(
				localStatistics.getProducedConclusionCounts())
				: ConclusionVisitor.DUMMY;
	}	

	/**
	 * This engine has all the functionality for applying rules but needs to be
	 * extended if new contexts may need to be created
	 */
	public abstract class BaseEngine implements InputProcessor<IndexedClassExpression>,
			RuleEngine {

		private ConclusionVisitor<?> conclusionProcessor_;

		/**
		 * Local {@link SaturationStatistics} created for every worker
		 */
		protected final SaturationStatistics localStatistics;
		
		protected final ContextStatistics localContextStatistics;

		protected BaseEngine(SaturationStatistics localStatistics) {
			this.localStatistics = localStatistics;
			this.localContextStatistics = localStatistics.getContextStatistics();
		}

		protected abstract BasicSaturationStateWriter getSaturationStateWriter();

		@Override
		public void process() {
			localContextStatistics.timeContextProcess -= CachedTimeThread.currentTimeMillis;
			
			BasicSaturationStateWriter writer = getSaturationStateWriter();
			
			if (conclusionProcessor_ == null) {
				conclusionProcessor_ = getConclusionProcessor(writer, localStatistics);
			}
			
			for (;;) {
				if (Thread.currentThread().isInterrupted())
					break;

				Context nextContext = writer.pollForActiveContext();

				if (nextContext == null) {
					break;
				} else {
					process(nextContext);
				}
			}
			
			localContextStatistics.timeContextProcess += CachedTimeThread.currentTimeMillis;
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
			localContextStatistics.countProcessedContexts++;
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
				SaturationStatistics localStatistics) {
			if (COLLECT_CONCLUSION_COUNTS) {
				return new PreprocessedConclusionVisitor<Boolean>(
						new CountingConclusionVisitor(localStatistics
								.getConclusionStatistics()
								.getUsedConclusionCounts()), ruleProcessor);
			} else
				return ruleProcessor;
		}

		/**
		 * Returns the base {@link ConclusionVisitor} that performs processing
		 * of {@code Conclusion}s within a {@link Context}. This can be further
		 * wrapped in some other code.
		 * 
		 * @param saturationStateWriter
		 *            the {@link SaturationStateImpl.AbstractWriter} using which one can
		 *            produce new {@link Conclusion}s in {@link Context}s
		 * @param localStatistics
		 *            the object accumulating local statistics for this worker
		 * @return the base {@link ConclusionVisitor} that performs processing
		 *         of {@code Conclusion}s within a {@link Context}
		 */
		protected ConclusionVisitor<Boolean> getBaseConclusionProcessor(
				BasicSaturationStateWriter saturationStateWriter,
				SaturationStatistics localStatistics) {

			return new CombinedConclusionVisitor(
					new ConclusionInsertionVisitor(),
					filterRuleConclusionProcessor(
							new ConclusionApplicationVisitor(
									saturationStateWriter,
									getEngineCompositionRuleApplicationVisitor(localStatistics.getRuleStatistics()),
									getEngineDecompositionRuleApplicationVisitor(
											getDecompositionRuleApplicationVisitor(),
											localStatistics.getRuleStatistics())), localStatistics));
		}

		/**
		 * Returns the final {@link ConclusionVisitor} that is used by this
		 * {@link DefaultEngine} for processing {@code Conclusion}s within
		 * {@link Context}s
		 * 
		 * @param saturationStateWriter
		 *            the {@link SaturationStateImpl.AbstractWriter} using which one can
		 *            produce new {@link Conclusion}s in {@link Context}s
		 * @param localStatistics
		 *            the object accumulating local statistics for this worker
		 * @return the final {@link ConclusionVisitor} that is used by this
		 *         {@link DefaultEngine} for processing {@code Conclusion}s within
		 *         {@link Context}s
		 */
		protected ConclusionVisitor<?> getConclusionProcessor(
				BasicSaturationStateWriter saturationStateWriter,
				SaturationStatistics localStatistics) {
			ConclusionVisitor<Boolean> result = getBaseConclusionProcessor(
					saturationStateWriter, localStatistics);
			if (trackModifiedContexts_)
				result = new CombinedConclusionVisitor(result,
						new ConclusionSourceUnsaturationVisitor(
								saturationStateWriter));
			if (COLLECT_CONCLUSION_COUNTS) {
				result = new PreprocessedConclusionVisitor<Boolean>(
						new CountingConclusionVisitor(localStatistics
								.getConclusionStatistics()
								.getProcessedConclusionCounts()), result);
			}
			if (COLLECT_CONCLUSION_TIMES)
				return new TimedConclusionVisitor(localStatistics
						.getConclusionStatistics().getConclusionTimers(),
						result);
			else
				return result;
		}
		
		protected abstract DecompositionRuleApplicationVisitor getDecompositionRuleApplicationVisitor();
	}
	
	
	/**
	 * Default rule application engine which can create new contexts via
	 * {@link SaturationStateImpl.ExtendedWriter} (either directly when a new
	 * {@link IndexedClassExpression} is submitted or during decomposition
	 */
	public class DefaultEngine extends BaseEngine {

		private final ExtendedSaturationStateWriter saturationStateWriter_;

		protected DefaultEngine(ExtendedSaturationStateWriter saturationStateWriter,
				SaturationStatistics localStatistics) {
			super(localStatistics);
			this.saturationStateWriter_ = saturationStateWriter;
		}

		protected DefaultEngine(final ContextCreationListener listener, final ContextModificationListener modListener) {
			this(listener, modListener, new SaturationStatistics());
		}

		private DefaultEngine(final ContextCreationListener listener,
				final ContextModificationListener modificationListener,
				final SaturationStatistics localStatistics) {
			this(saturationState.getExtendedWriter(
					getEngineContextCreationListener(listener, localStatistics.getContextStatistics()),
					getEngineContextModificationListener(modificationListener, localStatistics.getContextStatistics()),
					getEngineCompositionRuleApplicationVisitor(localStatistics.getRuleStatistics()),
					getEngineConclusionVisitor(localStatistics.getConclusionStatistics()),
					trackModifiedContexts_),
					localStatistics
					);
		}

		@Override
		public void submit(IndexedClassExpression job) {
			saturationStateWriter_.getCreateContext(job);
		}

		@Override
		protected ExtendedSaturationStateWriter getSaturationStateWriter() {
			return saturationStateWriter_;
		}

		@Override
		protected DecompositionRuleApplicationVisitor getDecompositionRuleApplicationVisitor() {
			//here we need an extended writer to pass to the decomposer which can create new contexts
			DecompositionRuleApplicationVisitor visitor = new ForwardDecompositionRuleApplicationVisitor(
					saturationStateWriter_);

			return getEngineDecompositionRuleApplicationVisitor(visitor,
					localStatistics.getRuleStatistics());
		}
	}

}
