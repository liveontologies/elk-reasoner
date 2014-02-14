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
package org.semanticweb.elk.reasoner.saturation.rules.factories;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.ContextCreationListener;
import org.semanticweb.elk.reasoner.saturation.ContextModificationListener;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.SaturationUtils;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.contextinit.ContextInitRule;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A skeleton for implementing {@link RuleApplicationFactory}
 * 
 * @author "Yevgeny Kazakov"
 */
public abstract class AbstractRuleApplicationFactory implements
		RuleApplicationFactory {

	// logger for this class
	protected static final Logger LOGGER_ = LoggerFactory
			.getLogger(AbstractRuleApplicationFactory.class);

	/**
	 * The main {@link SaturationState} this factory works with
	 */
	private final SaturationState saturationState_;

	/**
	 * The {@link Conclusion} used to initialize contexts using
	 * {@link ContextInitRule}s
	 */
	final Conclusion contextInitConclusion;

	/**
	 * The {@link SaturationStatistics} aggregated for all workers
	 */
	final SaturationStatistics aggregatedStats;

	public AbstractRuleApplicationFactory(final SaturationState saturationState) {
		this.saturationState_ = saturationState;
		this.contextInitConclusion = new ContextInitialization(
				saturationState.getOntologyIndex());
		this.aggregatedStats = new SaturationStatistics();
	}

	/**
	 * @param conclusionProcessor
	 * @param saturationStateWriter
	 * @param localStatistics
	 * @return an {@link InputProcessor} that processes {@link Conclusion}s in
	 *         {@link Context}s within an individual worker thread for the input
	 *         root {@link IndexedClassExpression} using the supplied
	 *         {@link SaturationStateWriter} and updates the supplied local
	 *         {@link SaturationStatistics} accordingly
	 */
	InputProcessor<IndexedClassExpression> getEngine(
			ConclusionVisitor<Context, Boolean> conclusionProcessor,
			SaturationStateWriter saturationStateWriter,
			SaturationStatistics localStatistics) {
		return new Engine(conclusionProcessor, saturationStateWriter,
				localStatistics);
	}

	abstract InputProcessor<IndexedClassExpression> getEngine(
			RuleVisitor ruleVisitor, SaturationStateWriter writer,
			SaturationStatistics localStatistics);

	@Override
	public final InputProcessor<IndexedClassExpression> getEngine(
			ContextCreationListener creationListener,
			ContextModificationListener modificationListener) {
		SaturationStatistics localStatistics = new SaturationStatistics();
		creationListener = SaturationUtils.addStatsToContextCreationListener(
				creationListener, localStatistics.getContextStatistics());
		modificationListener = SaturationUtils
				.addStatsToContextModificationListener(modificationListener,
						localStatistics.getContextStatistics());
		SaturationStateWriter writer = saturationState_
				.getContextCreatingWriter(creationListener,
						modificationListener);
		writer = SaturationUtils.getStatsAwareWriter(writer, localStatistics);
		RuleVisitor ruleVisitor = SaturationUtils
				.getStatsAwareRuleVisitor(localStatistics.getRuleStatistics());
		return getEngine(ruleVisitor, writer, localStatistics);
	}

	@Override
	public void dispose() {
		// aggregatedStats_.check(LOGGER_);
	}

	@Override
	public SaturationStatistics getSaturationStatistics() {
		return aggregatedStats;
	}

	@Override
	public SaturationState getSaturationState() {
		return saturationState_;
	}

	/**
	 * Default rule application engine which can create new contexts via
	 * {@link ExtendedSaturationStateWriter} (either directly when a new
	 * {@link IndexedClassExpression} is submitted or during decomposition)
	 */
	private class Engine extends AbstractRuleEngineWithStatistics {

		private final SaturationStateWriter writer_;

		Engine(ConclusionVisitor<Context, Boolean> conclusionProcessor,
				SaturationStateWriter saturationStateWriter,
				SaturationStatistics localStatistics) {
			super(conclusionProcessor, aggregatedStats, localStatistics);
			writer_ = saturationStateWriter;
		}

		@Override
		public void submit(IndexedClassExpression job) {
			writer_.produce(job, contextInitConclusion);
		}

		@Override
		Context getNextActiveContext() {
			return writer_.pollForActiveContext();
		}

		@Override
		public void finish() {
			super.finish();
			writer_.dispose();
		}

	}

}
