package org.semanticweb.elk.reasoner.saturation.properties;

/*
 * #%L
 * ELK Reasoner
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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedBinaryPropertyChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Counter accumulating statistical information about property compositions.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
class PropertyCompositionStatistics {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(PropertyCompositionStatistics.class);

	/**
	 * The number of {@link IndexedBinaryPropertyChain}s processed
	 */
	int roleChainsProcessed;

	/**
	 * The number of composition records created
	 */
	int compositionsCreated;

	/**
	 * The number of redundant composition records redundant
	 */
	int compositionsRedundant;

	synchronized void merge(PropertyCompositionStatistics statistics) {
		this.roleChainsProcessed += statistics.roleChainsProcessed;
		this.compositionsCreated += statistics.compositionsCreated;
		this.compositionsRedundant += statistics.compositionsRedundant;
	}

	void print() {
		LOGGER_.debug("role chains processed: {}", roleChainsProcessed);
		LOGGER_.debug("compositions created: {}", compositionsCreated);
		LOGGER_.debug("redundant compositions: {}", compositionsRedundant);
	}

}