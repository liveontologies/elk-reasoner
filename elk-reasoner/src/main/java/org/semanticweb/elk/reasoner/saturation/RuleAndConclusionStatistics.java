/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation;

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionStatistics;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationFactory.Engine;
import org.semanticweb.elk.reasoner.saturation.rules.RuleStatistics;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class RuleAndConclusionStatistics {
	
	private final ConclusionStatistics conclusionsStatistics_ = new ConclusionStatistics();

	private final RuleStatistics ruleStatistics_ = new RuleStatistics();

	/**
	 * The number of created contexts
	 */
	public int countCreatedContexts;
	/**
	 * the number of times a context has been processed using
	 * {@link Engine#process(Context)}
	 */
	public int contContextProcess;

	/**
	 * the time spent within {@link Engine#process()}
	 */
	public long timeContextProcess;

	public void reset() {
		conclusionsStatistics_.reset();
		ruleStatistics_.reset();
		countCreatedContexts = 0;
		timeContextProcess = 0;
	}

	public synchronized void add(RuleAndConclusionStatistics statistics) {
		this.conclusionsStatistics_.add(statistics.conclusionsStatistics_);
		this.ruleStatistics_.add(statistics.ruleStatistics_);
		this.contContextProcess += statistics.contContextProcess;
		this.timeContextProcess += statistics.timeContextProcess;
	}

	public void check(Logger logger) {
		if (countCreatedContexts > contContextProcess)
			logger.error("More contexts than context activations!");
		conclusionsStatistics_.check();
		ruleStatistics_.check();
	}

	public void print(Logger logger) {
		if (!logger.isDebugEnabled())
			return;
		if (countCreatedContexts > 0)
			logger.debug("Contexts created: " + countCreatedContexts);
		if (countCreatedContexts > 0)
			logger.debug("Contexts processsing: " + contContextProcess
					+ " (" + timeContextProcess + " ms)");
		conclusionsStatistics_.print();
		ruleStatistics_.print();
	}
	
	public RuleStatistics getRuleStatistics() {
		return ruleStatistics_;
	}
	
	public ConclusionStatistics getConclusionStatistics() {
		return conclusionsStatistics_;
	}
}
