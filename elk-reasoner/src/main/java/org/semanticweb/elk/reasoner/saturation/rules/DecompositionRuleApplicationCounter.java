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
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;

/**
 * An object which can be used to measure the methods invocations of a
 * {@link DecompositionRuleApplicationVisitor}. The fields of the counter
 * correspond to the methods of {@link DecompositionRuleApplicationVisitor}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class DecompositionRuleApplicationCounter {

	/**
	 * counter for {@link IndexedClass}
	 */
	int countIndexedClass;
	/**
	 * counter for {@link IndexedObjectIntersectionOf}
	 */
	int countIndexedObjectIntersectionOf;
	/**
	 * counter for {@link IndexedObjectSomeValuesFrom}
	 */
	int countIndexedObjectSomeValuesFrom;
	/**
	 * counter for {@link IndexedIndexedDataHasValue}
	 */
	int countIndexedDataHasValue;

	/**
	 * Reset all counters to zero.
	 */
	public void reset() {
		countIndexedClass = 0;
		countIndexedObjectIntersectionOf = 0;
		countIndexedObjectSomeValuesFrom = 0;
		countIndexedDataHasValue = 0;
	}

	/**
	 * Add the values the corresponding values of the given counter
	 * 
	 * @param counter
	 */
	public synchronized void add(DecompositionRuleApplicationCounter counter) {
		countIndexedClass += counter.countIndexedClass;
		countIndexedObjectIntersectionOf += counter.countIndexedObjectIntersectionOf;
		countIndexedObjectSomeValuesFrom += counter.countIndexedObjectSomeValuesFrom;
		countIndexedDataHasValue += counter.countIndexedDataHasValue;
	}

}
