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

import org.semanticweb.elk.benchmark.Metrics;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.reasoner.saturation.RuleAndConclusionStatistics;

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

	public static final String RULE_COUNT = "count.rule-applications";
	public static final String UNIQUE_CONCLUSION_COUNT = "count.unique-conclusions";

	private final Metrics metrics_;

	public RuleAndConclusionCountMeasuringExecutor(Metrics m) {
		metrics_ = m;
	}

	@Override
	protected void execute(ReasonerStage stage) throws ElkException {

		RuleAndConclusionStatistics stats = ((AbstractReasonerStage) stage)
				.getRuleAndConclusionStatistics();

		stats.reset();
		stage.execute();

		// System.err.println(stats.getRuleStatistics().getTotalRuleAppCount());

		metrics_.updateLongMetric(stage.getName() + "." + RULE_COUNT, stats
				.getRuleStatistics().getTotalRuleAppCount());
		metrics_.updateLongMetric(stage.getName() + "."
				+ UNIQUE_CONCLUSION_COUNT, stats.getConclusionStatistics()
				.getProcessedConclusionCounts().getTotalCount());
	}

}
