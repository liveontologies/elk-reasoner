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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionStatistics;
import org.semanticweb.elk.reasoner.saturation.context.ContextStatistics;
import org.semanticweb.elk.reasoner.saturation.rules.RuleStatistics;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class SaturationStatistics {
	
	private final ConclusionStatistics conclusionsStatistics_ = new ConclusionStatistics();

	private final RuleStatistics ruleStatistics_ = new RuleStatistics();
	
	private final ContextStatistics contextStatistics_ = new ContextStatistics();

	public void reset() {
		conclusionsStatistics_.reset();
		ruleStatistics_.reset();
		contextStatistics_.reset();
	}

	public synchronized void add(SaturationStatistics statistics) {
		this.conclusionsStatistics_.add(statistics.conclusionsStatistics_);
		this.ruleStatistics_.add(statistics.ruleStatistics_);
		this.contextStatistics_.add(statistics.contextStatistics_);
	}

	public void check(Logger logger) {
		conclusionsStatistics_.check(logger);
		conclusionsStatistics_.check(logger);
	}

	public void print(Logger logger) {
		if (!logger.isDebugEnabled()) {
			return;
		}

		conclusionsStatistics_.print(logger);
		ruleStatistics_.print(logger);
		contextStatistics_.print(logger, Level.DEBUG);
	}
	
	public RuleStatistics getRuleStatistics() {
		return ruleStatistics_;
	}
	
	public ConclusionStatistics getConclusionStatistics() {
		return conclusionsStatistics_;
	}
	
	public ContextStatistics getContextStatistics() {
		return contextStatistics_;
	}
}
