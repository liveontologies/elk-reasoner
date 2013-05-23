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

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class IncrementalProcessingStatistics {

	/**
	 * Time it takes to process a context during the change initialization stage
	 * (time to intersect the subsumers with the changed classes + time to apply
	 * changed rules)
	 */
	long changeInitContextProcessingTime;
	
	long changeInitContextCollectionProcessingTime;
	
	long countContexts;
	
	long countContextSubsumers;
	
	private int numOfMeasurements_ = 0;
	
	
	public long getContextCount() {
		return countContexts;
	}
	
	public long getSubsumersPerContextCount() {
		if (!measurementsTaken()) {
			return 0;
		}
		else {
			return countContextSubsumers / numOfMeasurements_;
		}
	}
	
	public long getChangeInitContextProcessingTime() {
		if (!measurementsTaken()) {
			return 0;
		}
		else {
			return changeInitContextProcessingTime / numOfMeasurements_;
		}
	}
	
	public long getChangeInitContextCollectionProcessingTime() {
		if (!measurementsTaken()) {
			return 0;
		}
		else {
			
			return changeInitContextCollectionProcessingTime / numOfMeasurements_;
		}
	}
	
	public void reset() {
		changeInitContextProcessingTime = 0;
		changeInitContextCollectionProcessingTime = 0;
		countContexts = 0;
		countContextSubsumers = 0;
		numOfMeasurements_ = 0;
	}
	
	public synchronized void add(IncrementalProcessingStatistics stats) {
		if (stats.measurementsTaken()) {
			numOfMeasurements_ += stats.numOfMeasurements_;
			changeInitContextProcessingTime += stats.changeInitContextProcessingTime;
			changeInitContextCollectionProcessingTime += stats.changeInitContextCollectionProcessingTime;
			countContexts += stats.countContexts;
			countContextSubsumers += stats.countContextSubsumers;
		}
	}
	
	public void startMeasurements() {
		if (numOfMeasurements_ < 1) {
			numOfMeasurements_ = 1;
		}
	}
	
	private boolean measurementsTaken() {
		return numOfMeasurements_ > 0;
	}
	
	public void print(Logger logger, Priority level) {
		if (!logger.isDebugEnabled() || !measurementsTaken())
			return;

		if (changeInitContextProcessingTime > 0) {
			logger.log(level, "Total context processing time during change initialization: " + changeInitContextProcessingTime / numOfMeasurements_);
		}
	}
}
