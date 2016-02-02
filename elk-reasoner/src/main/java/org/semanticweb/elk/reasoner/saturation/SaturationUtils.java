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

import java.util.Arrays;

import org.semanticweb.elk.reasoner.saturation.conclusions.classes.ClassConclusionCounter;
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.ClassConclusionStatistics;
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.ComposedClassConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.CountingClassConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.TimedClassConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.context.ContextStatistics;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.inferences.ComposedClassInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.BasicRuleVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.RuleStatistics;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitors;
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
	 * -------------------------------------------------------------------------
	 * - METHODS WHICH ADD TIMERS AND COUNTERS TO VARIOUS VISITORS AND LISTENERS
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

	public static RuleVisitor<?> getStatsAwareRuleVisitor(
			RuleStatistics localStatistics) {
		RuleVisitor<?> ruleAppVisitor = new BasicRuleVisitor();

		if (COLLECT_RULE_COUNTS) {
			ruleAppVisitor = RuleVisitors.getCountingVisitor(ruleAppVisitor,
					localStatistics.ruleCounter);
		}

		if (COLLECT_RULE_TIMES) {
			localStatistics.startMeasurements();

			ruleAppVisitor = RuleVisitors.getTimedVisitor(ruleAppVisitor,
					localStatistics.ruleTimer);
		}

		return ruleAppVisitor;
	}

	/**
	 * @param visitors
	 * @return A {@link ClassConclusion.Visitor} that applies the given
	 *         {@link ClassConclusion.Visitor}s consequently until one of them
	 *         returns {@code false}. {@link ClassConclusion.Visitor}s that are
	 *         {@code null} are ignored.
	 */
	public static ClassConclusion.Visitor<Boolean> compose(
			ClassConclusion.Visitor<Boolean>... visitors) {
		return new ComposedClassConclusionVisitor(removeNulls(visitors));

	}

	/**
	 * @param visitors
	 * @return A {@link ClassInference.Visitor} that applies the given
	 *         {@link ClassInference.Visitor}s consequently until one of them
	 *         returns {@code false}. {@link ClassInference.Visitor}s that are
	 *         {@code null} are ignored.
	 */
	public static ClassInference.Visitor<Boolean> compose(
			ClassInference.Visitor<Boolean>... visitors) {
		return new ComposedClassInferenceVisitor(removeNulls(visitors));

	}

	/**
	 * @param input
	 * @return the array obtained from the input array by removing the
	 *         {@code null} values. The order of the remaining elements is
	 *         preserved.
	 */
	private static <T> T[] removeNulls(T[] input) {
		int pos = 0;
		for (int i = 0; i < input.length; i++) {
			if (input[i] != null) {
				if (i > pos)
					input[pos] = input[i];
				pos++;
			}
		}
		return Arrays.copyOf(input, pos);
	}

	public static ClassConclusion.Visitor<Boolean> getCountingConclusionVisitor(
			ClassConclusionCounter counter) {
		if (!COLLECT_CONCLUSION_COUNTS)
			return null;
		// else
		return new CountingClassConclusionVisitor(counter);
	}

	public static ClassConclusion.Visitor<Boolean> getClassInferenceCountingVisitor(
			SaturationStatistics statistics) {
		return getCountingConclusionVisitor(
				statistics.getConclusionStatistics().getInferenceCounts());
	}

	public static ClassConclusion.Visitor<Boolean> getClassConclusionCountingVisitor(
			SaturationStatistics statistics) {
		return getCountingConclusionVisitor(
				statistics.getConclusionStatistics().getConclusionCounts());
	}

	public static <O> ClassConclusion.Visitor<O> getTimedConclusionVisitor(
			ClassConclusion.Visitor<O> conclusionVisitor,
			SaturationStatistics localStatistics) {

		ClassConclusionStatistics stats = localStatistics
				.getConclusionStatistics();
		if (COLLECT_CONCLUSION_TIMES) {
			stats.startMeasurements();
			return new TimedClassConclusionVisitor<O>(
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
