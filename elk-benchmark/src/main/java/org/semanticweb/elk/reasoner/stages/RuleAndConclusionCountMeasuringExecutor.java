/**
 * 
 */
package org.semanticweb.elk.reasoner.stages;

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
