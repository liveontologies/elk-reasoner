package org.semanticweb.elk.util.logging.statistics;
/*
 * #%L
 * ELK Utilities for Logging
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

public abstract class AbstractStatistics {

	/**
	 * The number of times measurements were taken in different threads. Used to
	 * average the wall time results.
	 */
	private int numOfMeasurements_ = 0;

	public void startMeasurements() {
		if (numOfMeasurements_ < 1) {
			numOfMeasurements_ = 1;
		}
	}

	private boolean measurementsTaken() {
		return numOfMeasurements_ > 0;
	}

	/**
	 * Reset all measurements
	 */
	public void reset() {
		numOfMeasurements_ = 0;
	}

	public synchronized void add(AbstractStatistics stats) {
		if (stats.measurementsTaken()) {
			numOfMeasurements_ += stats.numOfMeasurements_;
		}
	}
}
