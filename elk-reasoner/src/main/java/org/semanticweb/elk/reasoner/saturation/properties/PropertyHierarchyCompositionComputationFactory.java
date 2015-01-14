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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedBinaryPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedPropertyChainVisitor;
import org.semanticweb.elk.util.collections.AbstractHashMultimap;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A factory of engines for computing sub-properties and compositions induced by
 * property inclusions, property chains, and reflexive properties
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class PropertyHierarchyCompositionComputationFactory
		implements
		InputProcessorFactory<IndexedPropertyChain, PropertyHierarchyCompositionComputationFactory.Engine> {

	// logger for this class
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(PropertyHierarchyCompositionComputationFactory.class);

	@Override
	public Engine getEngine() {
		return new Engine();
	}

	@Override
	public void finish() {
		// nothing to do
	}

	class Engine implements InputProcessor<IndexedPropertyChain> {

		@Override
		public void submit(IndexedPropertyChain job) {
			job.accept(PROCESSOR_);
		}

		@Override
		public void process() throws InterruptedException {
			// everything should be process during submission
		}

		@Override
		public void finish() {
			// nothing to do
		}

	}

	private final static IndexedPropertyChainVisitor<Void> PROCESSOR_ = new IndexedPropertyChainVisitor<Void>() {

		@Override
		public Void visit(IndexedObjectProperty element) {
			// ensure that sub-properties are computed
			SubPropertyExplorer.getSubPropertyChains(element);
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.semanticweb.elk.reasoner.indexing.visitors.
		 * IndexedBinaryPropertyChainVisitor
		 * #visit(org.semanticweb.elk.reasoner.indexing
		 * .hierarchy.IndexedBinaryPropertyChain)
		 */
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.semanticweb.elk.reasoner.indexing.visitors.
		 * IndexedBinaryPropertyChainVisitor
		 * #visit(org.semanticweb.elk.reasoner.indexing
		 * .hierarchy.IndexedBinaryPropertyChain)
		 */
		@Override
		public Void visit(IndexedBinaryPropertyChain element) {
			LOGGER_.trace("{}: computing compositions", element);

			IndexedObjectProperty left = element.getLeftProperty();
			IndexedPropertyChain right = element.getRightPropertyChain();
			Set<IndexedObjectProperty> leftSubProperties = SubPropertyExplorer
					.getSubProperties(left);
			if (leftSubProperties.isEmpty())
				return null;
			Set<IndexedPropertyChain> rightSubProperties = SubPropertyExplorer
					.getSubPropertyChains(right);
			if (rightSubProperties.isEmpty())
				return null;

			for (IndexedPropertyChain rightSubPropertyChain : rightSubProperties) {

				SaturatedPropertyChain rightSaturation = rightSubPropertyChain
						.getSaturated();
				synchronized (rightSaturation) {
					if (rightSaturation.compositionsByLeftSubProperty == null)
						rightSaturation.compositionsByLeftSubProperty = new CompositionMultimap<IndexedObjectProperty>();
				}

				AbstractHashMultimap<IndexedObjectProperty, IndexedBinaryPropertyChain> compositionsByLeft = rightSaturation.compositionsByLeftSubProperty;

				// computing left properties for which composition with the
				// current right sub-property is redundant
				Collection<IndexedObjectProperty> redundantLeftProperties = Collections
						.emptySet();

				if (rightSubPropertyChain instanceof IndexedBinaryPropertyChain) {
					IndexedBinaryPropertyChain composition = (IndexedBinaryPropertyChain) rightSubPropertyChain;
					IndexedPropertyChain rightRightSubProperty = composition
							.getRightPropertyChain();
					if (rightSubProperties.contains(rightRightSubProperty)) {
						IndexedObjectProperty rightLeftSubProperty = composition
								.getLeftProperty();
						redundantLeftProperties = SubPropertyExplorer
								.getLeftSubComposableSubPropertiesByRightProperties(
										left).get(rightLeftSubProperty);
					}
				}

				for (IndexedObjectProperty leftSubProperty : leftSubProperties) {
					boolean newRecord = false;

					if (redundantLeftProperties.contains(leftSubProperty)) {
						LOGGER_.trace(
								"{} o {} => {}: composition is redundant",
								leftSubProperty, rightSubPropertyChain, element);
						continue;
					}

					LOGGER_.trace("{} o {} => {}: new composition",
							leftSubProperty, rightSubPropertyChain, element);

					Collection<IndexedBinaryPropertyChain> compositionsSoFar;
					synchronized (compositionsByLeft) {
						compositionsSoFar = compositionsByLeft
								.getValues(leftSubProperty);
						if (compositionsSoFar == null) {
							compositionsSoFar = new ArrayHashSet<IndexedBinaryPropertyChain>(
									2);
							compositionsByLeft.put(leftSubProperty,
									compositionsSoFar);
							newRecord = true;
						}
					}

					if (newRecord) {
						SaturatedPropertyChain leftSaturation = leftSubProperty
								.getSaturated();
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

	};

	private static class CompositionMultimap<P extends IndexedPropertyChain>
			extends AbstractHashMultimap<P, IndexedBinaryPropertyChain> {

		@Override
		protected Collection<IndexedBinaryPropertyChain> newRecord() {
			return new ArrayHashSet<IndexedBinaryPropertyChain>(2);
		}
	}

}
