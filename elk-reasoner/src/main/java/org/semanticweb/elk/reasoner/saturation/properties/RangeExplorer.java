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
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.liveontologies.proof.util.Producer;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.PropertyRangeInference;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.PropertyRangeInherited;
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
	 * The element for which the ranges are computed
	 */
	final private IndexedObjectProperty input_;
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
	/**
	 * used to record {@link PropertyRangeInference}
	 */
	final private Producer<? super PropertyRangeInference> inferenceProducer_;

	RangeExplorer(IndexedObjectProperty input,
			Set<IndexedObjectProperty> currentSuperProperties,
			Set<IndexedClassExpression> currentRanges,
			Producer<? super PropertyRangeInference> inferenceProducer) {
		this.input_ = input;
		this.superProperties_ = currentSuperProperties;
		this.ranges_ = currentRanges;
		this.inferenceProducer_ = inferenceProducer;
		toDo(input);
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
			List<IndexedClassExpression> ranges = next.getToldRanges();
			List<ElkAxiom> reasons = next.getToldRangesReasons();
			for (int i = 0; i < ranges.size(); i++) {
				IndexedClassExpression range = ranges.get(i);
				ElkAxiom reason = reasons.get(i);
				ranges_.add(range);
				inferenceProducer_.produce(new PropertyRangeInherited(input_,
						next, range, reason));
			}
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
			Set<IndexedClassExpression> currentRanges,
			Producer<? super PropertyRangeInference> inferenceProducer) {
		new RangeExplorer(property, currentSuperProperties, currentRanges,
				inferenceProducer).process();
		if (LOGGER_.isTraceEnabled()) {
			LOGGER_.trace("{} super-properties: {}, ranges: {}", property,
					currentSuperProperties, currentRanges);
		}
	}

	private static SaturatedPropertyChain computeRanges(
			IndexedObjectProperty element,
			Producer<? super PropertyRangeInference> inferenceProducer) {
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
					saturation.derivedRanges, inferenceProducer);
			saturation.derivedRangesComputed = true;
		}
		return saturation;
	}

	/**
	 * @param element
	 * @return the sub-{@link IndexedPropertyChain}s of the given
	 *         {@link IndexedPropertyChain}
	 */
	static Set<IndexedClassExpression> getRanges(IndexedObjectProperty element,
			Producer<? super PropertyRangeInference> inferenceProducer) {
		return computeRanges(element, inferenceProducer).getRanges();
	}
}
