/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.rules;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.ContextCreationListener;
import org.semanticweb.elk.reasoner.saturation.ContextModificationListener;
import org.semanticweb.elk.reasoner.saturation.ExtendedSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.MapSaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.SaturationUtils;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.AndConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionOccurrenceCheckingVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.HybridRuleApplicationConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Applies rules to all conclusions of partially completed contexts to close
 * them deductively. Uses a local saturation state to iterate over all
 * conclusions, adds previously non-existent conclusions to the ToDo queues in
 * the main saturation state.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ContextCompletionFactory extends RuleApplicationFactory {

	// logger for this class
	protected static final Logger LOGGER_ = LoggerFactory
			.getLogger(ContextCompletionFactory.class);

	private final SaturationState localState_;

	public ContextCompletionFactory(SaturationState saturationState) {
		super(saturationState);
		localState_ = new MapSaturationState(saturationState.getOntologyIndex());
	}

	@Override
	public SaturationState getSaturationState() {
		return localState_;
	}

	@Override
	public InputProcessor<IndexedClassExpression> getDefaultEngine(
			ContextCreationListener listener,
			ContextModificationListener modListener) {
		return new ContextCompletionEngine(listener, modListener);
	}

	private static ConclusionVisitor<Boolean> getGapFillingConclusionProcessor(
			RuleVisitor ruleVisitor, ConclusionProducer mainProducer,
			ConclusionProducer trackingProducer) {
		return new AndConclusionVisitor(
		// check if conclusion occurs in the main saturation state
				new ConclusionOccurrenceCheckingVisitor(),
				// if so, apply the non-redundant rules using both producers
				// and redundant using only the tracking producer
				new HybridRuleApplicationConclusionVisitor(ruleVisitor,
						ruleVisitor, new CombinedConclusionProducer(
								mainProducer, trackingProducer),
						trackingProducer));
	}

	/**
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 * @author "Yevgeny Kazakov"
	 */
	private class ContextCompletionEngine extends
			AbstractRuleEngineWithStatistics {

		private final ExtendedSaturationStateWriter mainWriter_;
		private final ExtendedSaturationStateWriter trackingWriter_;

		ContextCompletionEngine(ExtendedSaturationStateWriter mainWriter,
				ExtendedSaturationStateWriter trackingWriter,
				SaturationStatistics localStatistics) {
			super(getGapFillingConclusionProcessor(
					SaturationUtils.getStatsAwareRuleVisitor(localStatistics
							.getRuleStatistics()), mainWriter, trackingWriter),
					aggregatedStats, localStatistics);
			mainWriter_ = mainWriter;
			trackingWriter_ = trackingWriter;
		}

		private ContextCompletionEngine(final ContextCreationListener listener,
				final ContextModificationListener modificationListener,
				final SaturationStatistics localStatistics) {

			this(
					// mainWriter
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
											.getStatsAwareRuleVisitor(localStatistics
													.getRuleStatistics()),
									SaturationUtils
											.addStatsToConclusionVisitor(localStatistics
													.getConclusionStatistics()),
									trackModifiedContexts_),
					// trackingWriter
					localState_
							.getExtendedWriter(
									ContextCreationListener.DUMMY,
									ContextModificationListener.DUMMY,
									SaturationUtils
											.getStatsAwareRuleVisitor(localStatistics
													.getRuleStatistics()),
									SaturationUtils
											.addStatsToConclusionVisitor(localStatistics
													.getConclusionStatistics()),
									false),
					// localStatistics
					localStatistics);

		}

		protected ContextCompletionEngine(
				final ContextCreationListener listener,
				final ContextModificationListener modListener) {
			this(listener, modListener, new SaturationStatistics());
		}

		@Override
		public void submit(IndexedClassExpression job) {
			trackingWriter_.getCreateContext(job);
		}

		@Override
		Context getNextActiveContext() {
			return trackingWriter_.pollForActiveContext();
		}

		@Override
		Context getContextToProcess(Context activeContext) {
			// return the corresponding context in the main saturationState, or
			// create one if it does not exist
			return mainWriter_.getCreateContext(activeContext.getRoot());
		}

	}

}
