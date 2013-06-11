/**
 * 
 */
package org.semanticweb.elk.reasoner.stages;

/*
 * #%L
 * ELK Benchmarking Package
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

import java.lang.reflect.Field;

import org.semanticweb.elk.benchmark.Metrics;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;

/**
 * Stores total rule and processed conclusion counts after every stage is
 * executed
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class RuleAndConclusionCountMeasuringExecutor extends
		AbstractStageExecutor {
	
	//private static final Logger LOGGER_ = Logger.getLogger(RuleAndConclusionCountMeasuringExecutor.class);

	public static final String RULE_COUNT = ".count.rule-applications";
	public static final String NEW_CONTEXT_COUNT = ".count.new-contexts";
	public static final String PRODUCED_CONCLUSION_COUNT = ".count.produced-conclusions";
	public static final String UNIQUE_CONCLUSION_COUNT = ".count.used-conclusions";
	public static final String PROCESSED_CONCLUSION_COUNT = ".count.processed-conclusions";
	public static final String MODIFIED_CONTEXT_COUNT = ".count.modified-contexts";
	public static final String CHANGE_INIT_PROC_TIME = ".time.change-init-context-proc-time";
	public static final String CHANGE_INIT_CONTEX_COLLECTION_PROC_TIME = ".time.change-init-context-collection-proc-time";
	public static final String TOTAL_RULE_TIME = ".rule-time";
	public static final String CONTEXT_COUNT = ".count.all-contexts";
	public static final String CONTEXT_SUBSUMER_COUNT = ".count.subsumers-per-context";
	
	private final AbstractStageExecutor executor_;
	
	protected final Metrics metrics;

	public RuleAndConclusionCountMeasuringExecutor(AbstractStageExecutor e, Metrics m) {
		executor_ = e;
		metrics = m;
	}
	
	protected boolean measure(ReasonerStage stage) {
		return true;
	}
	
	protected void doMeasure(ReasonerStage stage, SaturationStatistics stats) {		
		recordMetrics(stage.getName(), stats);
	}
	
	protected void recordMetrics(String prefix, SaturationStatistics stats) {
		metrics.updateLongMetric(prefix + NEW_CONTEXT_COUNT,
				stats.getContextStatistics().countCreatedContexts);
		metrics.updateLongMetric(prefix + PRODUCED_CONCLUSION_COUNT, stats
				.getConclusionStatistics().getProducedConclusionCounts()
				.getTotalCount());
		metrics.updateLongMetric(prefix + UNIQUE_CONCLUSION_COUNT, stats
				.getConclusionStatistics().getUsedConclusionCounts()
				.getTotalCount());
		metrics.updateLongMetric(prefix + MODIFIED_CONTEXT_COUNT,
				stats.getContextStatistics().countModifiedContexts);
		metrics.updateLongMetric(prefix + CHANGE_INIT_PROC_TIME, stats
				.getIncrementalProcessingStatistics()
				.getChangeInitContextProcessingTime());
		metrics.updateLongMetric(prefix + CHANGE_INIT_PROC_TIME, stats
				.getIncrementalProcessingStatistics()
				.getChangeInitContextProcessingTime());
		metrics.updateLongMetric(prefix + CONTEXT_COUNT, stats
				.getIncrementalProcessingStatistics().getContextCount());
		metrics.updateLongMetric(prefix + CONTEXT_SUBSUMER_COUNT, stats
				.getIncrementalProcessingStatistics().getSubsumersPerContextCount());
		/*metrics.updateLongMetric(prefix + CHANGE_INIT_CONTEX_COLLECTION_PROC_TIME, stats
				.getIncrementalProcessingStatistics()
				.getChangeInitContextCollectionProcessingTime());*/
		metrics.updateDoubleMetric(prefix + TOTAL_RULE_TIME,
				stats.getRuleStatistics().getTotalRuleTime());
		
		//addregateRuleCounters(stats.getRuleStatistics().ruleTimer, metrics_, prefix + ".rule.");
	}
	
	protected void executeStage(ReasonerStage stage, SaturationStatistics stats) throws ElkException {
		stats.reset();
		executor_.execute(stage);
	}

	@Override
	protected void execute(ReasonerStage stage) throws ElkException {
		SaturationStatistics stats = ((AbstractReasonerStage) stage)
				.getRuleAndConclusionStatistics();		
				
		if (measure(stage)) {
			executeStage(stage, stats);
			doMeasure(stage, stats);		
		}
		else {
			executeStage(stage, stats);
		}
	}

	void addregateRuleCounters(Object ruleCounter, Metrics metrics,
			String prefix) {
		// reflection based
		// TODO perhaps better to use custom annotations to mark counter fields?
		for (Field field : ruleCounter.getClass().getDeclaredFields()) {
			if (field.getType().equals(Integer.TYPE)) {
				// this must be a counter, get the value
				try {
					field.setAccessible(true);
					
					Integer counter = (Integer) field.get(ruleCounter);

					if (counter > 0) {
						metrics.updateLongMetric(prefix + field.getName(), counter);
					}
				} catch (Exception e) {
					// log it?
				}
			}
		}
	}

}
