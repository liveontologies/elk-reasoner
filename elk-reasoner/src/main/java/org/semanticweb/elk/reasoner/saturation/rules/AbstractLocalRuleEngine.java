package org.semanticweb.elk.reasoner.saturation.rules;

import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.SaturationUtils;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.CombinedConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionInsertionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionSourceContextProcessorVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.NonRedundantRuleApplicationConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationFactory.DefaultEngine;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.SubsumerDecompositionVisitor;

/**
 * A skeleton for implementing {@link RuleEngine}s applied by individual workers
 * 
 * @author "Yevgeny Kazakov"
 */
public abstract class AbstractLocalRuleEngine extends
		AbstractRuleEngineWithStatistics {

	private final boolean trackModifiedContexts_;

	public AbstractLocalRuleEngine(ConclusionVisitor<?> conclusionProcessor,
			SaturationStatistics aggregatedStats, boolean trackModifiedContexts) {
		super(conclusionProcessor, aggregatedStats);
		this.trackModifiedContexts_ = trackModifiedContexts;
	}

	/**
	 * Returns the base {@link ConclusionVisitor} that performs processing of
	 * {@code Conclusion}s within a {@link Context}. This can be further wrapped
	 * in some other code.
	 * 
	 * @param producer
	 *            the {@link SaturationStateImpl.AbstractWriter} using which one
	 *            can produce new {@link Conclusion}s in {@link Context} s
	 * @return the base {@link ConclusionVisitor} that performs processing of
	 *         {@code Conclusion}s within a {@link Context}
	 */
	protected ConclusionVisitor<Boolean> getBaseConclusionProcessor(
			ConclusionProducer producer) {

		return new CombinedConclusionVisitor(new ConclusionInsertionVisitor(),
				new NonRedundantRuleApplicationConclusionVisitor(
						SaturationUtils
								.getStatsAwareRuleVisitor(localStatistics
										.getRuleStatistics()), producer));
	}

	/**
	 * Returns the final {@link ConclusionVisitor} that is used by this
	 * {@link DefaultEngine} for processing {@code Conclusion}s within
	 * {@link Context}s
	 * 
	 * @param producer
	 *            the {@link SaturationStateImpl.AbstractWriter} using which one
	 *            can produce new {@link Conclusion}s in {@link Context} s
	 * @return the final {@link ConclusionVisitor} that is used by this
	 *         {@link DefaultEngine} for processing {@code Conclusion}s within
	 *         {@link Context}s
	 */
	protected ConclusionVisitor<?> getConclusionProcessor(
			ConclusionProducer producer) {
		ConclusionVisitor<Boolean> result = getBaseConclusionProcessor(producer);
		// if (this.ruleApplicationFactory.trackModifiedContexts_)
		if (trackModifiedContexts_) {
			result = new CombinedConclusionVisitor(result,
					new ConclusionSourceContextProcessorVisitor(producer));
		}

		return SaturationUtils.getProcessedConclusionCountingProcessor(result,
				localStatistics);
	}

	protected abstract SubsumerDecompositionVisitor getDecompositionRuleApplicationVisitor();

	protected abstract ConclusionProducer getConclusionProducer();
}