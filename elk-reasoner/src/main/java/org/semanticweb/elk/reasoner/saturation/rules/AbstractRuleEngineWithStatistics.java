package org.semanticweb.elk.reasoner.saturation.rules;

import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.context.ContextStatistics;
import org.semanticweb.elk.util.logging.CachedTimeThread;

public abstract class AbstractRuleEngineWithStatistics extends
		AbstractRuleEngine {

	/**
	 * The global {@link SaturationStatistics} in which the aggregated
	 * statistics over all workers is accumulated
	 */
	private final SaturationStatistics aggregatedStats_;

	/**
	 * The local {@link SaturationStatistics} used by this worker; it does not
	 * require synchronization when modified
	 */
	final SaturationStatistics localStatistics;

	/**
	 * The reference to {@link ContextStatistics} of local
	 * {@link SaturationStatistics} for frequent access
	 */
	protected final ContextStatistics localContextStatistics;

	public AbstractRuleEngineWithStatistics(
			ConclusionVisitor<?> conclusionProcessor,
			SaturationStatistics aggregatedStats,
			SaturationStatistics localStatistics) {
		super(conclusionProcessor);
		this.aggregatedStats_ = aggregatedStats;
		this.localStatistics = localStatistics;
		this.localContextStatistics = localStatistics.getContextStatistics();
	}

	@Override
	public void process() throws InterruptedException {
		localContextStatistics.timeContextProcess -= CachedTimeThread
				.getCurrentTimeMillis();
		super.process();
		localContextStatistics.timeContextProcess += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	protected void process(Context context) {
		localContextStatistics.countProcessedContexts++;
		super.process(context);
	}

	@Override
	public void finish() {
		aggregatedStats_.add(localStatistics);
		localStatistics.reset();
	}

}
