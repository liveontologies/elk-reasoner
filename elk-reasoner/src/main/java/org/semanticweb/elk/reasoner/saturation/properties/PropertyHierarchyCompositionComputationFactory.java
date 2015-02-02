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
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedBinaryPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedPropertyChainVisitor;
import org.semanticweb.elk.util.collections.AbstractHashMultimap;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorFactory;
import org.semanticweb.elk.util.concurrent.computation.SimpleInterrupter;

public class PropertyHierarchyCompositionComputationFactory extends
		SimpleInterrupter
		implements
		InputProcessorFactory<IndexedPropertyChain, PropertyHierarchyCompositionComputationFactory.Engine> {

	// logger for this class
	private static final Logger LOGGER_ = Logger
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
			SubPropertyExplorer.getSetRelevantSubProperties(element);
			return null;
		}

		@Override
		public Void visit(IndexedBinaryPropertyChain element) {
			if (LOGGER_.isTraceEnabled())
				LOGGER_.trace("Computing compositions for " + element);
			IndexedObjectProperty left = element.getLeftProperty();
			IndexedPropertyChain right = element.getRightProperty();
			Set<IndexedPropertyChain> leftSubProperties = SubPropertyExplorer
					.getSetRelevantSubProperties(left);
			if (leftSubProperties.isEmpty())
				return null;
			Set<IndexedPropertyChain> rightSubProperties = SubPropertyExplorer
					.getSetRelevantSubProperties(right);
			if (rightSubProperties.isEmpty())
				return null;
			CompositionClosure closure = SaturatedPropertyChain.ELIMINATE_IMPLIED_COMPOSITIONS ? new ReducingCompositionClosure(
					element) : new CompositionClosure(element);
			for (IndexedPropertyChain leftSubProperty : leftSubProperties) {
				SaturatedPropertyChain leftSaturation = SaturatedPropertyChain
						.getCreate(leftSubProperty);
				synchronized (leftSaturation) {
					if (leftSaturation.compositionsByRightSubProperty == null)
						leftSaturation.compositionsByRightSubProperty = new CompositionMultimap();
				}
				AbstractHashMultimap<IndexedPropertyChain, IndexedPropertyChain> compositionsByRight = leftSaturation.compositionsByRightSubProperty;
				for (IndexedPropertyChain rightSubProperty : rightSubProperties) {
					boolean newRecord = false;
					Collection<IndexedPropertyChain> compositionsSoFar;
					synchronized (compositionsByRight) {
						compositionsSoFar = compositionsByRight
								.getOld(rightSubProperty);
						if (compositionsSoFar == null) {
							compositionsSoFar = new ArrayHashSet<IndexedPropertyChain>(
									2);
							compositionsByRight.put(rightSubProperty,
									compositionsSoFar);
							newRecord = true;
						}
					}
					if (newRecord) {
						SaturatedPropertyChain rightSaturation = SaturatedPropertyChain
								.getCreate(rightSubProperty);
						synchronized (rightSaturation) {
							if (rightSaturation.compositionsByLeftSubProperty == null)
								rightSaturation.compositionsByLeftSubProperty = new CompositionMultimap();
						}
						Map<IndexedPropertyChain, Collection<IndexedPropertyChain>> compositionsByLeft = rightSaturation.compositionsByLeftSubProperty;
						synchronized (compositionsByLeft) {
							compositionsByLeft.put(leftSubProperty,
									compositionsSoFar);
						}
					}
					synchronized (compositionsSoFar) {
						closure.applyTo(compositionsSoFar);
						// the logger should be within synchronized
						if (LOGGER_.isTraceEnabled())
							LOGGER_.trace("updated compositions: "
									+ leftSubProperty + " o "
									+ rightSubProperty + " => "
									+ compositionsSoFar);
					}
				}
			}
			return null;
		}

	};

	private static class CompositionMultimap extends
			AbstractHashMultimap<IndexedPropertyChain, IndexedPropertyChain> {

		@Override
		protected Collection<IndexedPropertyChain> newRecord() {
			return new ArrayHashSet<IndexedPropertyChain>(2);
		}
	}

}
