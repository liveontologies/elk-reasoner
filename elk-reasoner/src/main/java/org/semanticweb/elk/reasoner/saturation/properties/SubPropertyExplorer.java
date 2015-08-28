package org.semanticweb.elk.reasoner.saturation.properties;

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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedComplexPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.ObjectPropertyInference;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.PropertyChainInitialization;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.SubPropertyChainInference;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.ToldSubProperty;
import org.semanticweb.elk.reasoner.saturation.tracing.ObjectPropertyInferenceProducer;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.collections.HashSetMultimap;
import org.semanticweb.elk.util.collections.LazySetIntersection;
import org.semanticweb.elk.util.collections.Multimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A collection of utilities for computing entailed sub-properties of
 * {@link IndexedObjectProperty}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
class SubPropertyExplorer {

	// logger for this class
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(SubPropertyExplorer.class);

	/**
	 * The element for which the sub-property (chains) are computed
	 */
	final private IndexedPropertyChain superChain_;
	/**
	 * the set that will be closed under told sub-properties
	 */
	final private Set<IndexedPropertyChain> subPropertyChains_;
	/**
	 * the subset of {@link #subProperties_} consisting of
	 * {@link IndexedObjectProperty}s
	 */
	final private Set<IndexedObjectProperty> subProperties_;
	/**
	 * the sub-properties for which the told sub-properties may not be yet
	 * expanded
	 */
	final private Queue<IndexedObjectProperty> toDoSubProperties_ = new LinkedList<IndexedObjectProperty>();
	/**
	 * used to record sub-property inferences
	 */
	final private ObjectPropertyInferenceProducer inferenceProducer_;

	SubPropertyExplorer(IndexedPropertyChain element,
			Set<IndexedPropertyChain> subPropertyChains,
			Set<IndexedObjectProperty> subProperties,
			ObjectPropertyInferenceProducer inferenceProducer) {
		this.superChain_ = element;
		this.subPropertyChains_ = subPropertyChains;
		this.subProperties_ = subProperties;
		this.inferenceProducer_ = inferenceProducer;
		toDo(new PropertyChainInitialization(element));
	}

	private void toDo(SubPropertyChainInference<?, ?> inference) {
		LOGGER_.trace("{}: new inference", inference);
		inferenceProducer_.produce(inference);
		IndexedPropertyChain subChain = inference.getSubPropertyChain();
		if (subPropertyChains_.add(subChain)) {
			if (subChain instanceof IndexedObjectProperty) {
				IndexedObjectProperty subProperty = (IndexedObjectProperty) subChain;
				subProperties_.add(subProperty);
				toDoSubProperties_.add(subProperty);
			}
		}

	}

	private void process() {
		for (;;) {
			IndexedObjectProperty next = toDoSubProperties_.poll();
			if (next == null)
				break;
			ArrayList<IndexedPropertyChain> toldSubChains = next
					.getToldSubChains();
			ArrayList<ElkAxiom> reasons = next.getToldSubChainsReasons();
			for (int i = 0; i < toldSubChains.size(); i++) {
				IndexedPropertyChain sub = toldSubChains.get(i);
				ElkAxiom reason = reasons.get(i);
				toDo(new ToldSubProperty(sub, next, superChain_, reason));
			}
		}
	}

	private static void expandUnderSubProperties(IndexedPropertyChain property,
			Set<IndexedPropertyChain> currentSubPropertyChains,
			Set<IndexedObjectProperty> currentSubProperties,
			ObjectPropertyInferenceProducer inferenceProducer) {
		new SubPropertyExplorer(property, currentSubPropertyChains,
				currentSubProperties, inferenceProducer).process();
		LOGGER_.trace("{} sub-property chains: {}, sub-properties: {}",
				property, currentSubPropertyChains, currentSubProperties);
	}

	private static SaturatedPropertyChain computeSubProperties(
			IndexedPropertyChain element,
			ObjectPropertyInferenceProducer inferenceProducer) {
		SaturatedPropertyChain saturation = element.getSaturated();
		if (saturation.derivedSubPropertiesComputed)
			return saturation;
		// else
		synchronized (saturation) {
			if (saturation.derivedSubProperyChains == null)
				saturation.derivedSubProperyChains = new ArrayHashSet<IndexedPropertyChain>(
						8);
		}
		synchronized (saturation.derivedSubProperyChains) {
			if (saturation.derivedSubPropertiesComputed)
				return saturation;
			// else
			if (saturation.derivedSubProperties == null)
				saturation.derivedSubProperties = new ArrayHashSet<IndexedObjectProperty>(
						8);
			expandUnderSubProperties(element,
					saturation.derivedSubProperyChains,
					saturation.derivedSubProperties, inferenceProducer);
			saturation.derivedSubPropertiesComputed = true;
		}
		return saturation;
	}

	/**
	 * Computes all sub-{@link IndexedPropertyChain}s of the given
	 * {@link IndexedPropertyChain}, if not computing before, recording all
	 * {@link ObjectPropertyInference}s using the provided
	 * {@link ObjectPropertyInferenceProducer}. It is ensured that all
	 * {@link ObjectPropertyInference}s are applied only once even if the method
	 * is called multiple times.
	 * 
	 * @param element
	 *            the {@link IndexedPropertyChain} for which to find the sub-
	 *            {@link IndexedPropertyChain}s.
	 * @param inferenceProducer
	 *            the {@link ObjectPropertyInferenceProducer} using which all
	 *            applied {@link ObjectPropertyInference}s are recorded.
	 * @return the sub-{@link IndexedPropertyChain}s of the given
	 *         {@link IndexedPropertyChain}
	 */
	static Set<IndexedPropertyChain> getSubPropertyChains(
			IndexedPropertyChain element,
			ObjectPropertyInferenceProducer inferenceProducer) {
		return computeSubProperties(element, inferenceProducer)
				.getSubPropertyChains();
	}

	/**
	 * Computes all sub-{@link IndexedPropertyChain}s of the given
	 * {@link IndexedPropertyChain}, if not computing before, recording all
	 * {@link ObjectPropertyInference}s using the provided
	 * {@link ObjectPropertyInferenceProducer}. It is ensured that all
	 * {@link ObjectPropertyInference}s are applied only once even if the method
	 * is called multiple times.
	 * 
	 * @param element
	 * @param inferenceProducer
	 *            the {@link ObjectPropertyInferenceProducer} using which all
	 *            applied {@link ObjectPropertyInference}s are recorded.
	 * @return the sub-{@link IndexedObjectProperty} of the given
	 *         {@link IndexedPropertyChain}
	 */
	static Set<IndexedObjectProperty> getSubProperties(
			IndexedPropertyChain element,
			ObjectPropertyInferenceProducer inferenceProducer) {
		return computeSubProperties(element, inferenceProducer)
				.getSubProperties();
	}

	/**
	 * Given an {@link IndexedObjectProperty} Computes a {@link Multimap} from
	 * {@link IndexedObjectProperty}s to {@link IndexedObjectProperty}
	 * consisting of the the assignments T -> S such that both S and
	 * ObjectPropertyChain(S, T) are sub-properties of the given
	 * {@link IndexedObjectProperty}. The provided
	 * {@link ObjectPropertyInferenceProducer} is used to record all
	 * {@link ObjectPropertyInference}s that are applied in this computation of
	 * sub-{@link IndexedPropertyChain}s involved. It is ensured that the
	 * computation is performed only once.
	 * 
	 * @param element
	 *            a given {@link IndexedObjectProperty}
	 * @param inferenceProducer
	 * @return a multimap consisting of assignments T -> S such that both S and
	 *         ObjectPropertyChain(S, T) are sub-properties of the given
	 *         {@link IndexedObjectProperty}
	 */
	static Multimap<IndexedObjectProperty, IndexedObjectProperty> getLeftSubComposableSubPropertiesByRightProperties(
			IndexedObjectProperty element,
			ObjectPropertyInferenceProducer inferenceProducer) {
		SaturatedPropertyChain saturation = element.getSaturated();
		if (saturation.leftSubComposableSubPropertiesByRightPropertiesComputed)
			return saturation.leftSubComposableSubPropertiesByRightProperties;
		// else
		synchronized (saturation) {
			if (saturation.leftSubComposableSubPropertiesByRightProperties == null)
				saturation.leftSubComposableSubPropertiesByRightProperties = new HashSetMultimap<IndexedObjectProperty, IndexedObjectProperty>();
		}
		synchronized (saturation.leftSubComposableSubPropertiesByRightProperties) {
			if (saturation.leftSubComposableSubPropertiesByRightPropertiesComputed)
				return saturation.leftSubComposableSubPropertiesByRightProperties;
			// else compute it
			Set<IndexedObjectProperty> subProperties = getSubProperties(
					element, inferenceProducer);
			for (IndexedPropertyChain subPropertyChain : getSubPropertyChains(
					element, inferenceProducer)) {
				if (subPropertyChain instanceof IndexedComplexPropertyChain) {
					IndexedComplexPropertyChain composition = (IndexedComplexPropertyChain) subPropertyChain;
					Set<IndexedObjectProperty> leftSubProperties = getSubProperties(
							composition.getFirstProperty(), inferenceProducer);
					Set<IndexedObjectProperty> commonSubProperties = new LazySetIntersection<IndexedObjectProperty>(
							subProperties, leftSubProperties);
					if (commonSubProperties.isEmpty())
						continue;
					for (IndexedObjectProperty rightSubProperty : getSubProperties(
							composition.getSuffixChain(), inferenceProducer))
						for (IndexedObjectProperty commonLeft : commonSubProperties)
							saturation.leftSubComposableSubPropertiesByRightProperties
									.add(rightSubProperty, commonLeft);
				}
			}
			saturation.leftSubComposableSubPropertiesByRightPropertiesComputed = true;
		}
		return saturation.leftSubComposableSubPropertiesByRightProperties;
	}
}
