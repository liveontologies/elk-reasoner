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

	/**
	 * collects all encountered sub-properties to make sure the procedure does
	 * not loop
	 */
	final private Set<IndexedPropertyChain> allSubProperties_ = new ArrayHashSet<IndexedPropertyChain>();
	/**
	 * the sub-properties for which the told sub-properties may not be yet
	 * expanded
	 */
	final private Queue<IndexedPropertyChain> toDoSubProperties_ = new LinkedList<IndexedPropertyChain>();
	/**
	 * the set that will be extended with relevant sub-propeties
	 */
	final private Set<IndexedPropertyChain> relevantSubProperties_;

	SubPropertyExplorer(IndexedPropertyChain element,
			Set<IndexedPropertyChain> relevantSubProperties) {
		this.relevantSubProperties_ = relevantSubProperties;
		toDo(element);
	}

	@Override
	public Void visit(IndexedObjectProperty element) {
		for (IndexedPropertyChain sub : element.getToldSubProperties())
			toDo(sub);
		return null;
	}

	@Override
	public Void visit(IndexedBinaryPropertyChain element) {
		IndexedPropertyChain left = element.getLeftProperty();
		IndexedPropertyChain right = element.getRightProperty();
		SaturatedPropertyChain leftSaturation = left.getSaturated();
		SaturatedPropertyChain rightSaturation = right.getSaturated();
		if (leftSaturation != null && leftSaturation.isDerivedReflexive())
			toDo(right);
		if (rightSaturation != null && rightSaturation.isDerivedReflexive())
			toDo(left);
		return null;
	}

	private void toDo(IndexedPropertyChain element) {
		if (allSubProperties_.add(element)) {
			toDoSubProperties_.add(element);
			if (SaturatedPropertyChain.isRelevant(element)) {
				relevantSubProperties_.add(element);
			}
		}
	}

	void process() {
		for (;;) {
			IndexedPropertyChain next = toDoSubProperties_.poll();
			if (next == null)
				break;
			next.accept(this);
		}
	}

	private static void addRelevantSubProperties(IndexedPropertyChain property,
			Set<IndexedPropertyChain> relevantSubProperties) {
		new SubPropertyExplorer(property, relevantSubProperties).process();

		LOGGER_.trace("{} relevant subproperties: {}", property,
				relevantSubProperties);
	}

	/**
	 * @param element
	 * @return the relevant sub-properties of the given
	 *         {@link IndexedPropertyChain}
	 * @see SaturatedPropertyChain#isRelevant(IndexedPropertyChain)
	 */
	static Set<IndexedPropertyChain> getRelevantSubProperties(
			IndexedPropertyChain element) {
		SaturatedPropertyChain saturation = SaturatedPropertyChain
				.getCreate(element);
		if (saturation.derivedSubPropertiesComputed)
			return saturation.derivedSubProperties;
		// else
		if (saturation.derivedSubProperties == null) {
			synchronized (saturation) {
				if (saturation.derivedSubProperties == null)
					saturation.derivedSubProperties = new ArrayHashSet<IndexedPropertyChain>(
							8);
			}
		}
		synchronized (saturation.derivedSubProperties) {
			if (saturation.derivedSubPropertiesComputed)
				return saturation.derivedSubProperties;
			// else
			addRelevantSubProperties(element, saturation.derivedSubProperties);
			saturation.derivedSubPropertiesComputed = true;
		}
		return saturation.derivedSubProperties;
	}

	/**
	 * @param element
	 * @return a multimap T -> {S} such that both S and ObjectPropertyChain(S,
	 *         T) are sub-properties of the given {@link IndexedObjectProperty}
	 */
	static Multimap<IndexedObjectProperty, IndexedPropertyChain> getLeftSubComposableSubPropertiesByRightProperties(
			IndexedObjectProperty element) {
		SaturatedPropertyChain saturation = SaturatedPropertyChain
				.getCreate(element);
		if (saturation.leftSubComposableSubPropertiesByRightPropertiesComputed)
			return saturation.leftSubComposableSubPropertiesByRightProperties;
		// else
		if (saturation.leftSubComposableSubPropertiesByRightProperties == null) {
			synchronized (saturation) {
				if (saturation.leftSubComposableSubPropertiesByRightProperties == null)
					saturation.leftSubComposableSubPropertiesByRightProperties = new HashSetMultimap<IndexedObjectProperty, IndexedPropertyChain>();
			}
		}
		synchronized (saturation.leftSubComposableSubPropertiesByRightProperties) {
			if (saturation.leftSubComposableSubPropertiesByRightPropertiesComputed)
				return saturation.leftSubComposableSubPropertiesByRightProperties;
			// else compute it
			Set<IndexedPropertyChain> subProperties = getRelevantSubProperties(element);
			for (IndexedPropertyChain subProperty : subProperties) {
				if (subProperty instanceof IndexedBinaryPropertyChain) {
					IndexedBinaryPropertyChain composition = (IndexedBinaryPropertyChain) subProperty;
					Set<IndexedPropertyChain> leftSubProperties = getRelevantSubProperties(composition
							.getLeftProperty());
					Set<IndexedPropertyChain> commonSubProperties = new LazySetIntersection<IndexedPropertyChain>(
							subProperties, leftSubProperties);
					if (commonSubProperties.isEmpty())
						continue;
					for (IndexedPropertyChain rightSubProperty : getRelevantSubProperties(composition
							.getRightProperty()))
						if (rightSubProperty instanceof IndexedObjectProperty)
							for (IndexedPropertyChain commonLeft : commonSubProperties)
								saturation.leftSubComposableSubPropertiesByRightProperties
										.add((IndexedObjectProperty) rightSubProperty,
												commonLeft);
				}
			}
			saturation.leftSubComposableSubPropertiesByRightPropertiesComputed = true;
			return saturation.leftSubComposableSubPropertiesByRightProperties;
		}
	}
}
