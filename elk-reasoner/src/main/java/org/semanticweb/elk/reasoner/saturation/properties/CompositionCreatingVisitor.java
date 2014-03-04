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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedBinaryPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedPropertyChainVisitor;
import org.semanticweb.elk.util.collections.AbstractHashMultimap;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An {@link IndexedPropertyChainVisitor} that creates compositions producing
 * the visited {@link IndexedPropertyChain}s and writes them into the
 * corresponding {@link SaturatedPropertyChain}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
final class CompositionCreatingVisitor implements
		IndexedPropertyChainVisitor<Void> {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(CompositionCreatingVisitor.class);

	private final PropertyCompositionStatistics localStatistics_ = new PropertyCompositionStatistics();

	PropertyCompositionStatistics getStatistics() {
		return localStatistics_;
	}

	@Override
	public Void visit(IndexedObjectProperty element) {
		// ensure that sub-properties are computed
		SubPropertyExplorer.getSubProperties(element);
		return null;
	}

	@Override
	public Void visit(IndexedBinaryPropertyChain element) {
		LOGGER_.trace("{}: computing compositions", element);
		localStatistics_.roleChainsProcessed++;
		if (element.getToldSuperProperties().size() > 1)
			LOGGER_.debug("{}: has sevaral super-properties: {}", element,
					element.getToldSuperProperties());

		IndexedObjectProperty left = element.getLeftProperty();
		IndexedPropertyChain right = element.getRightProperty();
		Set<IndexedObjectProperty> leftSubProperties = SubPropertyExplorer
				.getSubProperties(left);
		if (leftSubProperties.isEmpty())
			return null;
		Set<IndexedPropertyChain> rightSubProperties = SubPropertyExplorer
				.getSubPropertyChains(right);
		if (rightSubProperties.isEmpty())
			return null;

		for (IndexedPropertyChain rightSubPropertyChain : rightSubProperties) {

			SaturatedPropertyChain rightSaturation = SaturatedPropertyChain
					.getCreate(rightSubPropertyChain);
			synchronized (rightSaturation) {
				if (rightSaturation.compositionsByLeftSubProperty == null)
					rightSaturation.compositionsByLeftSubProperty = new CompositionMultimap<IndexedObjectProperty>();
			}

			Map<IndexedObjectProperty, Collection<IndexedBinaryPropertyChain>> compositionsByLeft = rightSaturation.compositionsByLeftSubProperty;

			// computing left properties for which composition with the
			// current right sub-property is redundant

			Collection<IndexedObjectProperty> redundantSimple = Collections
					.emptySet();

			Collection<IndexedObjectProperty> redundantComplex = Collections
					.emptySet();

			if (rightSubPropertyChain instanceof IndexedBinaryPropertyChain) {
				IndexedBinaryPropertyChain composition = (IndexedBinaryPropertyChain) rightSubPropertyChain;
				IndexedPropertyChain rightRightSubProperty = composition
						.getRightProperty();

				// simple method
				if (rightSubProperties.contains(rightRightSubProperty)) {
					IndexedObjectProperty rightLeftSubProperty = composition
							.getLeftProperty();
					redundantSimple = SubPropertyExplorer
							.getLeftSubComposableSubPropertiesByRightProperties(
									left).get(rightLeftSubProperty);
				}

				// complex method
				for (IndexedObjectProperty superProperty : element
						.getToldSuperProperties()) {
					Set<IndexedObjectProperty> newRedundantProperties = new HashSet<IndexedObjectProperty>(
							64);
					for (IndexedBinaryPropertyChain leftSubComposition : SubPropertyExplorer
							.getSubPropertyChainsByRightSubProperties(
									superProperty).get(rightRightSubProperty)) {
						for (IndexedBinaryPropertyChain leftLeftSubComposition : SubPropertyExplorer
								.getSubPropertyChainsByRightSubProperties(
										leftSubComposition.getLeftProperty())
								.get(composition.getLeftProperty())) {
							newRedundantProperties.addAll(SubPropertyExplorer
									.getSubProperties(leftLeftSubComposition
											.getLeftProperty()));
						}
					}
					if (redundantComplex.isEmpty()) {
						redundantComplex = newRedundantProperties;
					} else {
						redundantComplex.retainAll(newRedundantProperties);
					}
				}

			}

			for (IndexedObjectProperty leftSubProperty : leftSubProperties) {
				boolean newRecord = false;

				if (redundantComplex.contains(leftSubProperty)) {
					LOGGER_.trace("{} o {} => {}: composition is redundant",
							leftSubProperty, rightSubPropertyChain, element);
					localStatistics_.compositionsRedundant++;
					continue;
				}

				LOGGER_.trace("{} o {} => {}: new composition",
						leftSubProperty, rightSubPropertyChain, element);
				localStatistics_.compositionsCreated++;

				Collection<IndexedBinaryPropertyChain> compositionsSoFar;
				synchronized (compositionsByLeft) {
					compositionsSoFar = compositionsByLeft.get(leftSubProperty);
					if (compositionsSoFar.isEmpty()) {
						compositionsSoFar = new ArrayHashSet<IndexedBinaryPropertyChain>(
								2);
						compositionsByLeft.put(leftSubProperty,
								compositionsSoFar);
						newRecord = true;
					}
				}

				if (newRecord) {
					SaturatedPropertyChain leftSaturation = SaturatedPropertyChain
							.getCreate(leftSubProperty);
					synchronized (leftSaturation) {
						if (leftSaturation.compositionsByRightSubProperty == null)
							leftSaturation.compositionsByRightSubProperty = new CompositionMultimap<IndexedPropertyChain>();
					}
					Map<IndexedPropertyChain, Collection<IndexedBinaryPropertyChain>> compositionsByRight = leftSaturation.compositionsByRightSubProperty;
					synchronized (compositionsByRight) {
						compositionsByRight.put(rightSubPropertyChain,
								compositionsSoFar);
					}
				}

				synchronized (compositionsSoFar) {
					compositionsSoFar.add(element);
				}
			}
		}
		return null;
	}

	static class CompositionMultimap<P extends IndexedPropertyChain> extends
			AbstractHashMultimap<P, IndexedBinaryPropertyChain> {

		@Override
		protected Collection<IndexedBinaryPropertyChain> newRecord() {
			return new ArrayHashSet<IndexedBinaryPropertyChain>(2);
		}
	}

}