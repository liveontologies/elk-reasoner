package org.semanticweb.elk.reasoner.saturation.rules;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;

public class DecompositionRuleApplicationTimer {

	/**
	 * timer for {@link IndexedClass}
	 */
	int timeIndexedClass;
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
		timeIndexedObjectIntersectionOf += timer.timeIndexedObjectIntersectionOf;
		timeIndexedObjectSomeValuesFrom += timer.timeIndexedObjectSomeValuesFrom;
		timeIndexedDataHasValue += timer.timeIndexedDataHasValue;
	}

}
