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

import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionStatistics;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.CountingConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.PreprocessedConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.TimedConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.context.ContextStatistics;
import org.semanticweb.elk.reasoner.saturation.rules.BasicCompositionRuleApplicationVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.CompositionRuleApplicationVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.DecompositionRuleApplicationCounterVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.DecompositionRuleApplicationTimerVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.DecompositionRuleApplicationVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.LinkRule0;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationCounterVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationTimerVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.RuleStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities for common saturation tasks
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class SaturationUtils {

	protected static final Logger LOGGER_ = LoggerFactory.getLogger(SaturationUtils.class);
	
	
	
	/**
	 * Applies all initialization rules to the context
	 * 
	 * @param context
	 * @param writer
	 * @param index
	 * @param ruleAppVisitor
	 */
	public static void initContext(Context context, BasicSaturationStateWriter writer,
			LinkRule0<Context> initRuleHead, CompositionRuleApplicationVisitor ruleAppVisitor) {
		// apply all context initialization rules
		while (initRuleHead != null) {
			initRuleHead.accept(ruleAppVisitor, writer, context);
			initRuleHead = initRuleHead.next();
		}
	}
	
	
	/*
	 * ------------------------------------------------------------------------------------
	 * METHODS WHICH ADD TIMERS AND COUNTERS TO VARIOUS VISITORS AND LISTENERS
	 * ------------------------------------------------------------------------------------
	 */
	
	public static final boolean COLLECT_CONCLUSION_COUNTS = LOGGER_.isDebugEnabled();
	public static final boolean COLLECT_CONCLUSION_TIMES = LOGGER_.isDebugEnabled();
	public static final boolean COLLECT_RULE_COUNTS = LOGGER_.isDebugEnabled();
	public static final boolean COLLECT_RULE_TIMES = LOGGER_.isDebugEnabled();
	public static final boolean COLLECT_PROCESSING_TIMES = LOGGER_.isDebugEnabled();
	
	public static CompositionRuleApplicationVisitor getStatsAwareCompositionRuleAppVisitor(
			RuleStatistics localStatistics) {
		CompositionRuleApplicationVisitor ruleAppVisitor = new BasicCompositionRuleApplicationVisitor();

		if (COLLECT_RULE_COUNTS) {
			ruleAppVisitor = new RuleApplicationCounterVisitor(ruleAppVisitor,
					localStatistics.ruleCounter);
		}

		if (COLLECT_RULE_TIMES) {
			localStatistics.startMeasurements();
			
			ruleAppVisitor = new RuleApplicationTimerVisitor(ruleAppVisitor,
					localStatistics.ruleTimer);
		}

		return ruleAppVisitor;
	}

	public static DecompositionRuleApplicationVisitor getStatsAwareDecompositionRuleAppVisitor(
			DecompositionRuleApplicationVisitor decompRuleAppVisitor,
			RuleStatistics localStatistics) {
		if (COLLECT_RULE_COUNTS) {
			decompRuleAppVisitor = new DecompositionRuleApplicationCounterVisitor(
					decompRuleAppVisitor, localStatistics.decompositionRuleCounter);
		}

		if (COLLECT_RULE_TIMES) {
			localStatistics.startMeasurements();
			
			decompRuleAppVisitor = new DecompositionRuleApplicationTimerVisitor(
					decompRuleAppVisitor, localStatistics.decompositionRuleTimer);
		}

		return decompRuleAppVisitor;
	}
	
	
	public static ConclusionVisitor<?, Context> addStatsToConclusionVisitor(
			ConclusionStatistics localStatistics) {
		return COLLECT_CONCLUSION_COUNTS ? new CountingConclusionVisitor(
				localStatistics.getProducedConclusionCounts())
				: ConclusionVisitor.DUMMY;
	}
	
	public static ConclusionVisitor<Boolean, Context> getUsedConclusionCountingProcessor(
			ConclusionVisitor<Boolean, Context> ruleProcessor,
			SaturationStatistics localStatistics) {
		if (COLLECT_CONCLUSION_COUNTS) {
			return new PreprocessedConclusionVisitor<Boolean, Context>(
					new CountingConclusionVisitor(localStatistics
							.getConclusionStatistics()
							.getUsedConclusionCounts()), ruleProcessor);
		}
		return ruleProcessor;
	}
	
	public static ConclusionVisitor<?, Context> getProcessedConclusionCountingProcessor(
			ConclusionVisitor<Boolean, Context> conclusionVisitor,
			SaturationStatistics localStatistics) {
		
		ConclusionStatistics stats = localStatistics.getConclusionStatistics();

		if (COLLECT_CONCLUSION_COUNTS) {
			conclusionVisitor = new PreprocessedConclusionVisitor<Boolean, Context>(
					new CountingConclusionVisitor(
							stats.getProcessedConclusionCounts()),
					conclusionVisitor);
		}
		if (COLLECT_CONCLUSION_TIMES) {
			stats.startMeasurements();

			return new TimedConclusionVisitor(stats.getConclusionTimers(),
					conclusionVisitor);
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
