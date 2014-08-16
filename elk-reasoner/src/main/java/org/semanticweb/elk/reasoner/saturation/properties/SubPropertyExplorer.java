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

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedBinaryPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedPropertyChainVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceStore;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.LeftReflexiveSubPropertyChainInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ObjectPropertyInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.BottomUpPropertySubsumptionInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ReflexiveSubPropertyChainInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.RightReflexiveSubPropertyChainInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.TopDownPropertySubsumptionInference;
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
class SubPropertyExplorer implements IndexedPropertyChainVisitor<Void> {

	// logger for this class
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(SubPropertyExplorer.class);

	final private IndexedObjectProperty superProperty_;

	/**
	 * the set that will be closed under sub-properties
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
	final private Queue<IndexedPropertyChain> toDoSubProperties_ = new LinkedList<IndexedPropertyChain>();
	/**
	 * used to record sub-property inferences
	 */
	final private TraceStore.Writer traceWriter_;

	SubPropertyExplorer(IndexedPropertyChain element,
			Set<IndexedPropertyChain> subPropertyChains,
			Set<IndexedObjectProperty> subProperties,
			TraceStore.Writer traceWriter) {
		this.superProperty_ = element instanceof IndexedObjectProperty ? (IndexedObjectProperty) element
				: null;
		this.subPropertyChains_ = subPropertyChains;
		this.subProperties_ = subProperties;
		this.traceWriter_ = traceWriter;
		
		toDo(element);
	}

	@Override
	public Void visit(IndexedObjectProperty element) {
		for (IndexedPropertyChain sub : element.getToldSubProperties())
			if (superProperty_ != null) {
				// with tracing
				toDoWithTracing(sub, new TopDownPropertySubsumptionInference(sub, superProperty_, element));	
			} else {
				// without tracing
				toDo(sub);
			}
		return null;
	}

	@Override
	public Void visit(IndexedBinaryPropertyChain element) {
		IndexedObjectProperty left = element.getLeftProperty();
		IndexedPropertyChain right = element.getRightProperty();
		SaturatedPropertyChain leftSaturation = left.getSaturated();
		SaturatedPropertyChain rightSaturation = right.getSaturated();
		// reflexivity-based inferences
		if (leftSaturation != null && leftSaturation.isDerivedReflexive()) {
			toDoReflexive(element, left, right, new LeftReflexiveSubPropertyChainInference(right, element));
		}
		if (rightSaturation != null && rightSaturation.isDerivedReflexive()) {
			toDoReflexive(element, right, left, new RightReflexiveSubPropertyChainInference(left, element));
		}
		
		return null;
	}
	
	private void toDoReflexive(IndexedBinaryPropertyChain chain, IndexedPropertyChain reflexive, IndexedPropertyChain other, ReflexiveSubPropertyChainInference inference) {
		LOGGER_.trace("{} is reflexive thus {} is a sub-property of {}", reflexive, other, chain);
		
		traceWriter_.addObjectPropertyInference(inference);
		
		if (superProperty_ != null) {
			// with tracing
			toDoWithTracing(other, new BottomUpPropertySubsumptionInference(other, superProperty_, chain));	
		} else {
			
			LOGGER_.trace("NOT WRITING: {} is a sub-property of {}", other, chain);
			
			// without tracing
			toDo(other);
		}	
	}

	private void toDo(IndexedPropertyChain subProperty) {
		if (subPropertyChains_.add(subProperty)) {
			toDoSubProperties_.add(subProperty);

			if (subProperty instanceof IndexedObjectProperty) {
				subProperties_.add((IndexedObjectProperty) subProperty);
			}
		}
	}
	
	private void toDoWithTracing(IndexedPropertyChain subProperty,
			ObjectPropertyInference subPropertyInference) {
		if (subPropertyChains_.add(subProperty)) {
			toDoSubProperties_.add(subProperty);

			if (subProperty instanceof IndexedObjectProperty) {
				subProperties_.add((IndexedObjectProperty) subProperty);
			}
		}
		
		LOGGER_.trace("{}: new sub-property inference {}, inference {}", superProperty_, subProperty, subPropertyInference);
		// record the inference
		traceWriter_.addObjectPropertyInference(subPropertyInference);		
	}

	void process() {
		for (;;) {
			IndexedPropertyChain next = toDoSubProperties_.poll();
			if (next == null)
				break;
			next.accept(this);
		}
	}

	private static void expandUnderSubProperties(IndexedPropertyChain property,
			Set<IndexedPropertyChain> currentSubPropertyChains,
			Set<IndexedObjectProperty> currentSubProperties,
			TraceStore.Writer traceWriter) {
		new SubPropertyExplorer(property, currentSubPropertyChains,
				currentSubProperties, traceWriter).process();
		LOGGER_.trace("{} sub-property chains: {}, sub-properties", property,
				currentSubPropertyChains, currentSubProperties);
	}

	private static SaturatedPropertyChain computeSubProperties(
			IndexedPropertyChain element, TraceStore.Writer traceWriter) {
		SaturatedPropertyChain saturation = SaturatedPropertyChain
				.getCreate(element);
		if (saturation.derivedSubPropertiesComputed)
			return saturation;
		// else
		if (saturation.derivedSubProperyChains == null) {
			synchronized (saturation) {
				if (saturation.derivedSubProperyChains == null)
					saturation.derivedSubProperyChains = new ArrayHashSet<IndexedPropertyChain>(
							8);
			}
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
					saturation.derivedSubProperties, traceWriter);
			saturation.derivedSubPropertiesComputed = true;
		}
		return saturation;
	}

	/**
	 * @param element
	 * @return the sub-{@link IndexedPropertyChain}s of the given
	 *         {@link IndexedPropertyChain}
	 */
	static Set<IndexedPropertyChain> getSubPropertyChains(
			IndexedPropertyChain element, TraceStore.Writer traceWriter) {
		return computeSubProperties(element, traceWriter)
				.getSubPropertyChains();
	}

	/**
	 * @param element
	 * @return the sub-{@link IndexedObjectProperty} of the given
	 *         {@link IndexedPropertyChain}
	 */
	static Set<IndexedObjectProperty> getSubProperties(
			IndexedPropertyChain element, TraceStore.Writer traceWriter) {
		return computeSubProperties(element, traceWriter).getSubProperties();
	}

	/**
	 * @param element
	 * @return a multimap T -> {S} such that both S and ObjectPropertyChain(S,
	 *         T) are sub-properties of the given {@link IndexedObjectProperty}
	 */
	static Multimap<IndexedObjectProperty, IndexedObjectProperty> getLeftSubComposableSubPropertiesByRightProperties(
			IndexedObjectProperty element, TraceStore.Writer traceWriter) {
		SaturatedPropertyChain saturation = SaturatedPropertyChain
				.getCreate(element);
		if (saturation.leftSubComposableSubPropertiesByRightPropertiesComputed)
			return saturation.leftSubComposableSubPropertiesByRightProperties;
		// else
		if (saturation.leftSubComposableSubPropertiesByRightProperties == null) {
			synchronized (saturation) {
				if (saturation.leftSubComposableSubPropertiesByRightProperties == null)
					saturation.leftSubComposableSubPropertiesByRightProperties = new HashSetMultimap<IndexedObjectProperty, IndexedObjectProperty>();
			}
		}
		synchronized (saturation.leftSubComposableSubPropertiesByRightProperties) {
			if (saturation.leftSubComposableSubPropertiesByRightPropertiesComputed)
				return saturation.leftSubComposableSubPropertiesByRightProperties;
			// else compute it
			Set<IndexedObjectProperty> subProperties = getSubProperties(
					element, traceWriter);
			for (IndexedPropertyChain subPropertyChain : getSubPropertyChains(element, traceWriter)) {
				if (subPropertyChain instanceof IndexedBinaryPropertyChain) {
					IndexedBinaryPropertyChain composition = (IndexedBinaryPropertyChain) subPropertyChain;
					Set<IndexedObjectProperty> leftSubProperties = getSubProperties(
							composition.getLeftProperty(), traceWriter);
					Set<IndexedObjectProperty> commonSubProperties = new LazySetIntersection<IndexedObjectProperty>(
							subProperties, leftSubProperties);
					if (commonSubProperties.isEmpty())
						continue;
					for (IndexedObjectProperty rightSubProperty : getSubProperties(
							composition.getRightProperty(), traceWriter))
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
