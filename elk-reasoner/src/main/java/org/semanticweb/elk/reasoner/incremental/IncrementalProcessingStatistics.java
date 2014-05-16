/**
 * 
 */
package org.semanticweb.elk.reasoner.incremental;

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

import org.semanticweb.elk.util.logging.LogLevel;
import org.semanticweb.elk.util.logging.LoggerWrap;
import org.semanticweb.elk.util.logging.statistics.AbstractStatistics;
import org.slf4j.Logger;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class IncrementalProcessingStatistics extends AbstractStatistics {

	/**
	 * Time it takes to process a context during the change initialization stage
	 * (time to intersect the subsumers with the changed classes + time to apply
	 * changed rules)
	 */
	long changeInitContextProcessingTime;

	long changeInitContextCollectionProcessingTime;

	long countContexts;

	long countContextSubsumers;

	public long getContextCount() {
		return countContexts;
	}

	public long getSubsumersPerContextCount() {
		if (!measurementsTaken()) {
			return 0;
		}
		// else
		return countContextSubsumers / getNumberOfMeasurements();
	}

	public long getChangeInitContextProcessingTime() {
		if (!measurementsTaken()) {
			return 0;
		}
		// else
		return changeInitContextProcessingTime / getNumberOfMeasurements();
	}

	public long getChangeInitContextCollectionProcessingTime() {
		if (!measurementsTaken()) {
			return 0;
		}
		// else
		return changeInitContextCollectionProcessingTime
				/ getNumberOfMeasurements();

	}

	@Override
	public void reset() {
		super.reset();
		changeInitContextProcessingTime = 0;
		changeInitContextCollectionProcessingTime = 0;
		countContexts = 0;
		countContextSubsumers = 0;
	}

	public synchronized void add(IncrementalProcessingStatistics stats) {
		super.add(stats);
		changeInitContextProcessingTime += stats.changeInitContextProcessingTime;
		changeInitContextCollectionProcessingTime += stats.changeInitContextCollectionProcessingTime;
		countContexts += stats.countContexts;
		countContextSubsumers += stats.countContextSubsumers;
	}

	public void print(Logger logger, LogLevel level) {
		if (!LoggerWrap.isEnabledFor(logger, level) || !measurementsTaken())
			return;

		if (changeInitContextProcessingTime > 0) {
			LoggerWrap.log(logger, level,
					"Total context processing time during change initialization: "
							+ changeInitContextProcessingTime
							/ getNumberOfMeasurements());
		}
	}
}
