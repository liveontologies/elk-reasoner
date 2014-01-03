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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.ContextCreationListener;
import org.semanticweb.elk.reasoner.saturation.ContextModificationListener;
import org.semanticweb.elk.reasoner.saturation.ExtendedSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.SaturationUtils;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	final SaturationStatistics aggregatedStats_;
	/**
	 * If {@code true}, the factory will keep track of contexts which get
	 * modified during saturation. This is needed, for example, for cleaning
	 * contexts modified during de-saturation or for cleaning taxonomy nodes
	 * which correspond to modified contexts (during de-saturation and
	 * re-saturation)
	 */
	final boolean trackModifiedContexts_;

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
	public InputProcessor<IndexedClassExpression> getDefaultEngine(
			ContextCreationListener listener,
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
	 * Default rule application engine which can create new contexts via
	 * {@link ExtendedSaturationStateWriter} (either directly when a new
	 * {@link IndexedClassExpression} is submitted or during decomposition
	 */
	public class DefaultEngine extends AbstractRuleEngineWithStatistics {

		private final ExtendedSaturationStateWriter saturationStateWriter_;

		DefaultEngine(ExtendedSaturationStateWriter saturationStateWriter,
				SaturationStatistics localStatistics) {
			super(ConclusionProcessors.getInsertionConclusionProcessor(
					SaturationUtils.getStatsAwareRuleVisitor(localStatistics
							.getRuleStatistics()), saturationStateWriter),
					aggregatedStats_, localStatistics);
			saturationStateWriter_ = saturationStateWriter;
		}		

		private DefaultEngine(final ContextCreationListener listener,
				final ContextModificationListener modificationListener,
				final SaturationStatistics localStatistics) {

			this(saturationState.getExtendedWriter(SaturationUtils
					.addStatsToContextCreationListener(listener,
							localStatistics.getContextStatistics()),
					SaturationUtils.addStatsToContextModificationListener(
							modificationListener,
							localStatistics.getContextStatistics()),
					SaturationUtils.getStatsAwareRuleVisitor(localStatistics
							.getRuleStatistics()), SaturationUtils
							.addStatsToConclusionVisitor(localStatistics
									.getConclusionStatistics()),
					trackModifiedContexts_), localStatistics);

		}
		
		protected DefaultEngine(final ContextCreationListener listener,
				final ContextModificationListener modListener) {
			this(listener, modListener, new SaturationStatistics());
		}

		@Override
		public void submit(IndexedClassExpression job) {
			saturationStateWriter_.getCreateContext(job);
		}

		@Override
		Context getNextActiveContext() {
			return saturationStateWriter_.pollForActiveContext();
		}
	}

}
