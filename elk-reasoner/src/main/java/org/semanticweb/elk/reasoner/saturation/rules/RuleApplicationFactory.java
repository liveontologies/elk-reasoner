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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.BasicSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.ContextCreationListener;
import org.semanticweb.elk.reasoner.saturation.ContextModificationListener;
import org.semanticweb.elk.reasoner.saturation.ExtendedSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.SaturationUtils;
import org.semanticweb.elk.reasoner.saturation.conclusions.CombinedConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionApplicationVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionInsertionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionSourceUnsaturationVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
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
	protected static final Logger LOGGER_ = LoggerFactory
			.getLogger(RuleApplicationFactory.class);

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

	public RuleApplicationFactory(final SaturationState saturationState) {
		this(saturationState, false);
	}

	public RuleApplicationFactory(final SaturationState saturationState,
			final boolean trackModifiedContexts) {
		this.aggregatedStats_ = new SaturationStatistics();
		this.saturationState = saturationState;
		this.trackModifiedContexts_ = trackModifiedContexts;
	}

	/*
	 * This method is supposed to be overridden in subclasses
	 */
	public BaseEngine getDefaultEngine(ContextCreationListener listener,
			ContextModificationListener modListener) {
		return new DefaultEngine(listener, modListener);
	}

	public void finish() {
		// aggregatedStats_.check(LOGGER_);
	}

	public SaturationStatistics getSaturationStatistics() {
		return aggregatedStats_;
	}

	public SaturationState getSaturationState() {
		return saturationState;
	}

	/**
	 * This engine has all the functionality for applying rules but needs to be
	 * extended if new contexts may need to be created
	 */
	public abstract class BaseEngine implements
			InputProcessor<IndexedClassExpression>, RuleEngine {

		private ConclusionVisitor<?> conclusionProcessor_;

		/**
		 * Local {@link SaturationStatistics} created for every worker
		 */
		protected final SaturationStatistics localStatistics;

		protected final ContextStatistics localContextStatistics;

		protected BaseEngine(SaturationStatistics localStatistics) {
			this.localStatistics = localStatistics;
			this.localContextStatistics = localStatistics
					.getContextStatistics();
		}

		@Override
		public void process() {
			localContextStatistics.timeContextProcess -= CachedTimeThread
					.getCurrentTimeMillis();

			BasicSaturationStateWriter writer = getSaturationStateWriter();

			if (conclusionProcessor_ == null) {
				conclusionProcessor_ = getConclusionProcessor(writer);
			}

			for (;;) {
				if (Thread.currentThread().isInterrupted())
					break;

				Context nextContext = writer.pollForActiveContext();

				if (nextContext == null) {
					break;
				}
				process(nextContext);
			}

			localContextStatistics.timeContextProcess += CachedTimeThread
					.getCurrentTimeMillis();
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
		 * @return the input {@link ConclusionVisitor} possibly wrapped with
		 *         some code for producing statistics
		 */
		protected ConclusionVisitor<Boolean> getUsedConclusionsCountingVisitor(
				ConclusionVisitor<Boolean> ruleProcessor) {
			return SaturationUtils.getUsedConclusionCountingProcessor(
					ruleProcessor, localStatistics);
		}

		/**
		 * Returns the base {@link ConclusionVisitor} that performs processing
		 * of {@code Conclusion}s within a {@link Context}. This can be further
		 * wrapped in some other code.
		 * 
		 * @param saturationStateWriter
		 *            the {@link SaturationStateImpl.AbstractWriter} using which
		 *            one can produce new {@link Conclusion}s in {@link Context}
		 *            s
		 * @return the base {@link ConclusionVisitor} that performs processing
		 *         of {@code Conclusion}s within a {@link Context}
		 */
		protected ConclusionVisitor<Boolean> getBaseConclusionProcessor(
				BasicSaturationStateWriter saturationStateWriter) {

			return new CombinedConclusionVisitor(
					new ConclusionInsertionVisitor(),
					getUsedConclusionsCountingVisitor(new ConclusionApplicationVisitor(
							saturationStateWriter,
							SaturationUtils
									.getStatsAwareCompositionRuleAppVisitor(localStatistics
											.getRuleStatistics()),
							SaturationUtils
									.getStatsAwareDecompositionRuleAppVisitor(
											getDecompositionRuleApplicationVisitor(),
											localStatistics.getRuleStatistics()))));
		}

		/**
		 * Returns the final {@link ConclusionVisitor} that is used by this
		 * {@link DefaultEngine} for processing {@code Conclusion}s within
		 * {@link Context}s
		 * 
		 * @param saturationStateWriter
		 *            the {@link SaturationStateImpl.AbstractWriter} using which
		 *            one can produce new {@link Conclusion}s in {@link Context}
		 *            s
		 * @return the final {@link ConclusionVisitor} that is used by this
		 *         {@link DefaultEngine} for processing {@code Conclusion}s
		 *         within {@link Context}s
		 */
		protected ConclusionVisitor<?> getConclusionProcessor(
				BasicSaturationStateWriter saturationStateWriter) {
			ConclusionVisitor<Boolean> result = getBaseConclusionProcessor(saturationStateWriter);
			if (trackModifiedContexts_) {
				result = new CombinedConclusionVisitor(result,
						new ConclusionSourceUnsaturationVisitor(
								saturationStateWriter));
			}

			return SaturationUtils.getProcessedConclusionCountingProcessor(
					result, localStatistics);
		}

		protected abstract DecompositionRuleApplicationVisitor getDecompositionRuleApplicationVisitor();

		protected abstract BasicSaturationStateWriter getSaturationStateWriter();
	}

	/**
	 * Default rule application engine which can create new contexts via
	 * {@link ExtendedSaturationStateWriter} (either directly when a new
	 * {@link IndexedClassExpression} is submitted or during decomposition
	 */
	public class DefaultEngine extends BaseEngine {

		private final ExtendedSaturationStateWriter saturationStateWriter_;

		protected DefaultEngine(
				ExtendedSaturationStateWriter saturationStateWriter,
				SaturationStatistics localStatistics) {
			super(localStatistics);
			this.saturationStateWriter_ = saturationStateWriter;
		}

		protected DefaultEngine(final ContextCreationListener listener,
				final ContextModificationListener modListener) {
			this(listener, modListener, new SaturationStatistics());
		}

		private DefaultEngine(final ContextCreationListener listener,
				final ContextModificationListener modificationListener,
				final SaturationStatistics localStatistics) {

			this(
					saturationState
							.getExtendedWriter(
									SaturationUtils
											.addStatsToContextCreationListener(
													listener,
													localStatistics
															.getContextStatistics()),
									SaturationUtils
											.addStatsToContextModificationListener(
													modificationListener,
													localStatistics
															.getContextStatistics()),
									SaturationUtils
											.getStatsAwareCompositionRuleAppVisitor(localStatistics
													.getRuleStatistics()),
									SaturationUtils
											.addStatsToConclusionVisitor(localStatistics
													.getConclusionStatistics()),
									trackModifiedContexts_), localStatistics);

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
			// here we need an extended writer to pass to the decomposer which
			// can create new contexts
			DecompositionRuleApplicationVisitor visitor = new ForwardDecompositionRuleApplicationVisitor(
					saturationStateWriter_);

			return SaturationUtils.getStatsAwareDecompositionRuleAppVisitor(
					visitor, localStatistics.getRuleStatistics());

		}
	}

}
