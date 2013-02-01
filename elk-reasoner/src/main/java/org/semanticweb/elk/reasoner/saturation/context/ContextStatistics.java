/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.context;
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

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 * Keeps statistics about the number of created and modified contexts
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ContextStatistics {

	/**
	 * The number of created contexts
	 */
	public int countCreatedContexts;
	
	/**
	 * the number of times a context has been processed
	 */
	public int countProcessedContexts;

	public int countModifiedContexts;

	/**
	 * the time spent on processing
	 */
	public long timeContextProcess;
	
	private int addCounter_ = 0;

	/**
	 * Reset all timers to zero.
	 */
	public void reset() {
		addCounter_ = 0;
		countCreatedContexts = 0;
		countProcessedContexts = 0;
		countModifiedContexts = 0;
		timeContextProcess = 0;
	}

	public synchronized void add(ContextStatistics stats) {
		addCounter_++;
		countCreatedContexts += stats.countCreatedContexts;
		countProcessedContexts += stats.countProcessedContexts;
		countModifiedContexts += stats.countModifiedContexts;
		timeContextProcess += stats.timeContextProcess;
	}

	public void print(Logger logger, Priority level) {
		if (!logger.isDebugEnabled() || addCounter_ <= 0)
			return;

		if (countCreatedContexts > 0) {
			logger.log(level, "Contexts created: " + countCreatedContexts);
		}
		
		if (countProcessedContexts > 0) {
			logger.log(level, "Contexts processed: " + countProcessedContexts + " (" +
					(timeContextProcess / addCounter_) + " ms)");
		}
		
		if (countModifiedContexts > 0) {
			logger.log(level, "Contexts modified: " + countModifiedContexts);
		}
	}
	
	public void check(Logger logger) {
		if (countCreatedContexts > countProcessedContexts) {
			logger.error("More contexts than context activations!");
		}
	}
	
}
