/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation;

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

import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionStatistics;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.CountingConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.PreprocessedConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.TimedConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.context.ContextStatistics;
import org.semanticweb.elk.reasoner.saturation.rules.BasicRuleVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationTimerVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.RuleCounterVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.RuleStatistics;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO: refactor, document
 * 
 * Utilities for common saturation tasks
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class SaturationUtils {

	protected static final Logger LOGGER_ = LoggerFactory
			.getLogger(SaturationUtils.class);

	/*
	 * --------------------------------------------------------------------------
	 * METHODS WHICH ADD TIMERS AND COUNTERS TO VARIOUS VISITORS AND LISTENERS
	 * ----------------------------------------------------------------
	 */

	public static final boolean COLLECT_CONCLUSION_COUNTS = LOGGER_
			.isDebugEnabled();
	public static final boolean COLLECT_CONCLUSION_TIMES = LOGGER_
			.isDebugEnabled();
	public static final boolean COLLECT_RULE_COUNTS = LOGGER_.isDebugEnabled();
	public static final boolean COLLECT_RULE_TIMES = LOGGER_.isDebugEnabled();
	public static final boolean COLLECT_PROCESSING_TIMES = LOGGER_
			.isDebugEnabled();

	public static RuleVisitor getStatsAwareRuleVisitor(
			RuleStatistics localStatistics) {
		RuleVisitor ruleAppVisitor = new BasicRuleVisitor();

		if (COLLECT_RULE_COUNTS) {
			ruleAppVisitor = new RuleCounterVisitor(ruleAppVisitor,
					localStatistics.ruleCounter);
		}

		if (COLLECT_RULE_TIMES) {
			localStatistics.startMeasurements();

			ruleAppVisitor = new RuleApplicationTimerVisitor(ruleAppVisitor,
					localStatistics.ruleTimer);
		}

		return ruleAppVisitor;
	}

	public static <C extends Context> SaturationStateWriter<C> getStatAwareWriter(
			SaturationStateWriter<C> writer, SaturationStatistics localStatistics) {
		return COLLECT_CONCLUSION_COUNTS ? new CountingSaturationStateWriter<C>(
				writer, localStatistics.getConclusionStatistics()
						.getProducedConclusionCounts()) : writer;
	}

	public static <C extends Context> SaturationStateWriter<C> getStatsAwareWriter(
			SaturationStateWriter<C> writer, SaturationStatistics localStatistics) {
		return COLLECT_CONCLUSION_COUNTS ? new CountingSaturationStateWriter<C>(
				writer, localStatistics.getConclusionStatistics()
						.getProducedConclusionCounts()) : writer;
	}

	public static ConclusionVisitor<Context, Boolean> getUsedConclusionCountingProcessor(
			ConclusionVisitor<Context, Boolean> ruleProcessor,
			SaturationStatistics localStatistics) {
		if (COLLECT_CONCLUSION_COUNTS) {
			return new PreprocessedConclusionVisitor<Context, Boolean>(
					new CountingConclusionVisitor<Context>(localStatistics
							.getConclusionStatistics()
							.getUsedConclusionCounts()), ruleProcessor);
		}
		return ruleProcessor;
	}

	public static <O> ConclusionVisitor<Context, O> getProcessedConclusionCountingProcessor(
			ConclusionVisitor<Context, O> conclusionVisitor,
			SaturationStatistics localStatistics) {

		ConclusionStatistics stats = localStatistics.getConclusionStatistics();

		if (COLLECT_CONCLUSION_COUNTS) {
			conclusionVisitor = new PreprocessedConclusionVisitor<Context, O>(
					new CountingConclusionVisitor<Context>(
							stats.getProcessedConclusionCounts()),
					conclusionVisitor);
		}
		if (COLLECT_CONCLUSION_TIMES) {
			stats.startMeasurements();

			return new TimedConclusionVisitor<Context, O>(
					stats.getConclusionTimers(), conclusionVisitor);
		}
		return conclusionVisitor;
	}

	public static ContextCreationListener addStatsToContextCreationListener(
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

	public static ContextModificationListener addStatsToContextModificationListener(
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
}
