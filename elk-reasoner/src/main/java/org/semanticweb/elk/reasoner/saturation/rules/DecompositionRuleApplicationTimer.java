package org.semanticweb.elk.reasoner.saturation.rules;

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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectComplementOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;

public class DecompositionRuleApplicationTimer {

	/**
	 * timer for {@link IndexedClass}
	 */
	int timeIndexedClass;
	/**
	 * timer for {@link IndexedObjectComplementOf}
	 */
	int timeIndexedObjectComplementOf;
	/**
	 * timer for {@link IndexedObjectIntersectionOf}
	 */
	int timeIndexedObjectIntersectionOf;
	/**
	 * timer for {@link IndexedObjectSomeValuesFrom}
	 */
	int timeIndexedObjectSomeValuesFrom;
	/**
	 * timer for {@link IndexedIndexedDataHasValue}
	 */
	int timeIndexedDataHasValue;

	/**
	 * Reset all timers zero.
	 */
	public void reset() {
		timeIndexedClass = 0;
		timeIndexedObjectComplementOf = 0;
		timeIndexedObjectIntersectionOf = 0;
		timeIndexedObjectSomeValuesFrom = 0;
		timeIndexedDataHasValue = 0;
	}

	/**
	 * Add the values the corresponding values of the given timer
	 * 
	 * @param timer
	 */
	public synchronized void add(DecompositionRuleApplicationTimer timer) {
		timeIndexedClass += timer.timeIndexedClass;
		timeIndexedObjectComplementOf += timer.timeIndexedObjectComplementOf;
		timeIndexedObjectIntersectionOf += timer.timeIndexedObjectIntersectionOf;
		timeIndexedObjectSomeValuesFrom += timer.timeIndexedObjectSomeValuesFrom;
		timeIndexedDataHasValue += timer.timeIndexedDataHasValue;
	}

	public int getTotalRuleAppTime() {
		return timeIndexedClass + timeIndexedObjectComplementOf
				+ timeIndexedObjectIntersectionOf
				+ timeIndexedObjectSomeValuesFrom + timeIndexedDataHasValue;
	}

}
