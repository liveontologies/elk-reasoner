package org.semanticweb.elk.reasoner.saturation.properties;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A collection of utilities for computing entailed ranges of
 * {@link IndexedObjectProperty}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class RangeExplorer {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(RangeExplorer.class);

	/**
	 * the set that will be closed under super-properties
	 */
	final private Set<IndexedObjectProperty> superProperties_;
	/**
	 * the ranges of the properties in {@link #superProperties_}
	 */
	final private Set<IndexedClassExpression> ranges_;
	/**
	 * the super-properties for which ranges are not yet taken into account in
	 * {@link #ranges_}
	 */
	final private Queue<IndexedObjectProperty> toDoSuperProperties_ = new LinkedList<IndexedObjectProperty>();

	RangeExplorer(IndexedObjectProperty element,
			Set<IndexedObjectProperty> currentSuperProperties,
			Set<IndexedClassExpression> currentRanges) {
		superProperties_ = currentSuperProperties;
		ranges_ = currentRanges;
		toDo(element);
	}

	private void toDo(IndexedObjectProperty element) {
		if (superProperties_.add(element)) {
			toDoSuperProperties_.add(element);
		}
	}

	void process() {
		for (;;) {
			IndexedObjectProperty next = toDoSuperProperties_.poll();
			if (next == null)
				break;
			ranges_.addAll(next.getToldRanges());
			for (IndexedObjectProperty superProperty : next
					.getToldSuperProperties()) {
				toDo(superProperty);
			}
		}
	}

	private static void expandUnderSuperProperties(
			IndexedObjectProperty property,
			Set<IndexedObjectProperty> currentSuperProperties,
			Set<IndexedClassExpression> currentRanges) {
		new RangeExplorer(property, currentSuperProperties, currentRanges)
				.process();
		if (LOGGER_.isTraceEnabled()) {
			LOGGER_.trace("{} super-properties: {}, ranges: {}", property,
					currentSuperProperties, currentRanges);
		}
	}

	private static SaturatedPropertyChain computeRanges(
			IndexedObjectProperty element) {
		SaturatedPropertyChain saturation = element.getSaturated();
		if (saturation.derivedRangesComputed)
			return saturation;
		// else
		synchronized (saturation) {
			if (saturation.derivedRanges == null)
				saturation.derivedRanges = new ArrayHashSet<IndexedClassExpression>(
						8);
		}
		synchronized (saturation.derivedRanges) {
			if (saturation.derivedRangesComputed)
				return saturation;
			// else
			expandUnderSuperProperties(element,
					new ArrayHashSet<IndexedObjectProperty>(8),
					saturation.derivedRanges);
			saturation.derivedRangesComputed = true;
		}
		return saturation;
	}

	/**
	 * @param element
	 * @return the sub-{@link IndexedPropertyChain}s of the given
	 *         {@link IndexedPropertyChain}
	 */
	static Set<IndexedClassExpression> getRanges(IndexedObjectProperty element) {
		return computeRanges(element).getRanges();
	}
}
