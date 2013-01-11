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
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionStatistics;
import org.semanticweb.elk.reasoner.saturation.context.Context;
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
	 * {@link DeapplicationEngine#process(Context)}
	 */
	public int contContextProcess;

	/**
	 * the time spent within {@link DeapplicationEngine#process()}
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
		conclusionsStatistics_.check(logger);
	}

	public void print(Logger logger) {
		if (!logger.isDebugEnabled())
			return;
		if (countCreatedContexts > 0)
			logger.debug("Contexts created: " + countCreatedContexts);
		if (countCreatedContexts > 0)
			logger.debug("Contexts processsing: " + contContextProcess
					+ " (" + timeContextProcess + " ms)");
		conclusionsStatistics_.print(logger);
		ruleStatistics_.print(logger);
	}
	
	public RuleStatistics getRuleStatistics() {
		return ruleStatistics_;
	}
	
	public ConclusionStatistics getConclusionStatistics() {
		return conclusionsStatistics_;
	}
}
