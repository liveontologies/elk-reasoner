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
 * The factory for engines for concurrently processing {@link Conclusion}s
 * within {@link Context}s and applying rules to them.
 * 
 * @author Frantisek Simancik
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch
 * @author Pavel Klinov
 * 
 */
public abstract class AbstractRuleApplicationFactory {

	// logger for this class
	protected static final Logger LOGGER_ = LoggerFactory
			.getLogger(AbstractRuleApplicationFactory.class);

	/**
	 * The main {@link SaturationState} this factory works with
	 */
	final SaturationState saturationState;

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
		this.saturationState = saturationState;
		this.contextInitConclusion = new ContextInitialization(
				saturationState.getOntologyIndex());
		this.aggregatedStats = new SaturationStatistics();
	}

	/**
	 * @param saturationStateWriter
	 * @param localStatistics
	 * @return an {@link InputProcessor} that processes {@link Conclusion}s in
	 *         {@link Context}s within an individual worker thread for the input
	 *         root {@link IndexedClassExpression} using the supplied
	 *         {@link SaturationStateWriter} and updates the supplied local
	 *         {@link SaturationStatistics} accordingly
	 */
	InputProcessor<IndexedClassExpression> getEngine(
			SaturationStateWriter saturationStateWriter,
			SaturationStatistics localStatistics) {
		return new Engine(saturationStateWriter, localStatistics);
	}

	public void finish() {
		// aggregatedStats_.check(LOGGER_);
	}

	public SaturationStatistics getSaturationStatistics() {
		return aggregatedStats;
	}

	public SaturationState getSaturationState() {
		return saturationState;
	}

	/**
	 * @param ruleVisitor
	 *            A {@link RuleVisitor} used for rule application
	 * @param writer
	 *            A {@link SaturationStateWriter} to be used for rule
	 *            applications
	 * @return {@link ConclusionVisitor} that perform processing of
	 *         {@link Conclusion}s in {@link Context}s
	 */
	abstract ConclusionVisitor<Context, Boolean> getConclusionProcessor(
			RuleVisitor ruleVisitor, SaturationStateWriter writer);

	/**
	 * Default rule application engine which can create new contexts via
	 * {@link ExtendedSaturationStateWriter} (either directly when a new
	 * {@link IndexedClassExpression} is submitted or during decomposition)
	 */
	public class Engine extends AbstractRuleEngineWithStatistics {

		private final SaturationStateWriter writer_;

		Engine(SaturationStateWriter saturationStateWriter,
				SaturationStatistics localStatistics) {
			super(getConclusionProcessor(
					SaturationUtils.getStatsAwareRuleVisitor(localStatistics
							.getRuleStatistics()), saturationStateWriter),
					aggregatedStats, localStatistics);
			writer_ = saturationStateWriter;
		}

		@Override
		public void submit(IndexedClassExpression job) {
			if (saturationState.getContext(job) == null)
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
