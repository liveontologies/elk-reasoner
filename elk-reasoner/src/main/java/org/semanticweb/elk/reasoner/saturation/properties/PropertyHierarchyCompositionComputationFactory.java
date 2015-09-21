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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedComplexPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedPropertyChainVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.ObjectPropertyInferenceProducer;
import org.semanticweb.elk.util.collections.AbstractHashMultimap;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorFactory;
import org.semanticweb.elk.util.concurrent.computation.SimpleInterrupter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A factory of engines for computing sub-properties and compositions induced by
 * property inclusions, property chains, and reflexive properties
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class PropertyHierarchyCompositionComputationFactory extends
		SimpleInterrupter
		implements
		InputProcessorFactory<IndexedPropertyChain, PropertyHierarchyCompositionComputationFactory.Engine> {

	// logger for this class
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(PropertyHierarchyCompositionComputationFactory.class);

	/**
	 * used to record sub-property inferences
	 */
	private final ObjectPropertyInferenceProducer inferenceProducer_;

	public PropertyHierarchyCompositionComputationFactory(
			ObjectPropertyInferenceProducer inferenceProducer) {
		this.inferenceProducer_ = inferenceProducer;
	}

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

	private final IndexedPropertyChainVisitor<Void> PROCESSOR_ = new IndexedPropertyChainVisitor<Void>() {

		@Override
		public Void visit(IndexedObjectProperty element) {
			LOGGER_.trace("{}: computing sub-property chains and ranges",
					element);
			// ensure that sub-properties are computed
			SubPropertyExplorer.getSubPropertyChains(element,
					inferenceProducer_);
			// ensure that property ranges are computed
			RangeExplorer.getRanges(element);// TODO: tracing
			// TODO: verify that global restrictions on range axioms are
			// satisfied:
			// http://www.w3.org/TR/owl2-profiles/#Global_Restrictions
			return null;
		}

		@Override
		public Void visit(IndexedComplexPropertyChain element) {
			LOGGER_.trace("{}: computing compositions", element);

			IndexedObjectProperty left = element.getFirstProperty();
			IndexedPropertyChain right = element.getSuffixChain();
			Set<IndexedObjectProperty> leftSubProperties = SubPropertyExplorer
					.getSubProperties(left, inferenceProducer_);
			if (leftSubProperties.isEmpty())
				return null;
			Set<IndexedPropertyChain> rightSubProperties = SubPropertyExplorer
					.getSubPropertyChains(right, inferenceProducer_);

			for (IndexedPropertyChain rightSubPropertyChain : rightSubProperties) {

				SaturatedPropertyChain rightSaturation = rightSubPropertyChain
						.getSaturated();
				synchronized (rightSaturation) {
					if (rightSaturation.compositionsByLeftSubProperty == null)
						rightSaturation.compositionsByLeftSubProperty = new CompositionMultimap<IndexedObjectProperty>();
				}

				AbstractHashMultimap<IndexedObjectProperty, IndexedComplexPropertyChain> compositionsByLeft = rightSaturation.compositionsByLeftSubProperty;

				// computing left properties for which composition with the
				// current right sub-property chain is redundant
				Collection<IndexedObjectProperty> redundantLeftProperties = Collections
						.emptySet();

				if (rightSubPropertyChain instanceof IndexedComplexPropertyChain) {
					IndexedComplexPropertyChain composition = (IndexedComplexPropertyChain) rightSubPropertyChain;
					IndexedPropertyChain rightRightSubProperty = composition
							.getSuffixChain();
					if (rightSubProperties.contains(rightRightSubProperty)) {
						IndexedObjectProperty rightLeftSubProperty = composition
								.getFirstProperty();
						redundantLeftProperties = SubPropertyExplorer
								.getLeftSubComposableSubPropertiesByRightProperties(
										left, inferenceProducer_).get(
										rightLeftSubProperty);
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

					Collection<IndexedComplexPropertyChain> compositionsSoFar;
					synchronized (compositionsByLeft) {
						compositionsSoFar = compositionsByLeft
								.getValues(leftSubProperty);
						if (compositionsSoFar == null) {
							compositionsSoFar = new ArrayHashSet<IndexedComplexPropertyChain>(
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
						Map<IndexedPropertyChain, Collection<IndexedComplexPropertyChain>> compositionsByRight = leftSaturation.compositionsByRightSubProperty;
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
			extends AbstractHashMultimap<P, IndexedComplexPropertyChain> {

		@Override
		protected Collection<IndexedComplexPropertyChain> newRecord() {
			return new ArrayHashSet<IndexedComplexPropertyChain>(2);
		}
	}

}
