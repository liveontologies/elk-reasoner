/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.rules.factories;

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
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.CombinedConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionInsertionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionOccurrenceCheckingVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.HybridLocalRuleApplicationConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.LocalizedConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.CombinedConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;
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
	public InputProcessor<IndexedClassExpression> getDefaultEngine(
			ContextCreationListener listener,
			ContextModificationListener modListener) {
		return new ContextCompletionEngine(listener, modListener);
	}

	ConclusionVisitor<Context, Boolean> getConclusionProcessor(
			RuleVisitor ruleVisitor, ConclusionProducer mainProducer,
			ConclusionProducer trackingProducer) {
		return new CombinedConclusionVisitor<Context>(
		// checking the conclusion against the main saturation state
				new LocalizedConclusionVisitor(
						// conclusion already occurs there
						new ConclusionOccurrenceCheckingVisitor(),
						saturationState),
				// if all fine,
				new CombinedConclusionVisitor<Context>(
				// insert the conclusion
						new ConclusionInsertionVisitor(),
						// apply local rules
						new HybridLocalRuleApplicationConclusionVisitor(
								saturationState, ruleVisitor, ruleVisitor,
								// the conclusions of non-redundant rules are
								// inserted to both main and tracing saturation
								// states
								new CombinedConclusionProducer(mainProducer,
										trackingProducer),
								// whereas the conclusion of redundant rules are
								// needed only for tracking
								trackingProducer)));
	}

	/**
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 * @author "Yevgeny Kazakov"
	 */
	private class ContextCompletionEngine extends
			AbstractRuleEngineWithStatistics {

		private final ExtendedSaturationStateWriter trackingWriter_;

		ContextCompletionEngine(ExtendedSaturationStateWriter mainWriter,
				ExtendedSaturationStateWriter trackingWriter,
				SaturationStatistics localStatistics) {
			super(getConclusionProcessor(
					SaturationUtils.getStatsAwareRuleVisitor(localStatistics
							.getRuleStatistics()), mainWriter, trackingWriter),
					aggregatedStats, localStatistics);
			trackingWriter_ = trackingWriter;
		}

		private ContextCompletionEngine(final ContextCreationListener listener,
				final ContextModificationListener modificationListener,
				final SaturationStatistics localStatistics) {

			this(
					// mainWriter with statistics
					SaturationUtils
							.getStatsAwareWriter(
									saturationState.getExtendedWriter(
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
											trackModifiedContexts_),
									localStatistics),
					// trackingWriter
					localState_.getExtendedWriter(
							ContextCreationListener.DUMMY,
							ContextModificationListener.DUMMY, false),
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
			LOGGER_.trace("{}: to complete",
					trackingWriter_.getCreateContext(job));
		}

		@Override
		Context getNextActiveContext() {
			return trackingWriter_.pollForActiveContext();
		}

	}

}
