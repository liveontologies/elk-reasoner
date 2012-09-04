/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.saturation.properties;

import java.util.ArrayDeque;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedBinaryPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.properties.ObjectPropertyHierarchyComputationFactory.Engine;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorFactory;

/**
 * The factory for engines that reset and compute the transitive closure of the
 * suproperties and superproperties relations for each submitted
 * {@link IndexedPropertyChain}. The engines are not thread safe at the moment
 * (only one engine can be used at a time).
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 */
public class ObjectPropertyHierarchyComputationFactory implements
		InputProcessorFactory<IndexedPropertyChain, Engine> {

	/**
	 * We use a single engine for this factory
	 */
	private final Engine engine;

	ObjectPropertyHierarchyComputationFactory() {
		this.engine = new Engine();
	}

	/**
	 * The engine for resetting the saturation and computing the transitively
	 * closed suproperties and superproperties of each submitted property chain.
	 * 
	 * @author Frantisek Simancik
	 * @author "Yevgeny Kazakov"
	 */
	class Engine implements InputProcessor<IndexedPropertyChain> {

		// don't allow creating of engines directly; only through the factory
		private Engine() {
		}

		@Override
		public void submit(IndexedPropertyChain ipc) {
			// reset the saturation of this property chain
			ipc.resetSaturated();
			SaturatedPropertyChain saturated = new SaturatedPropertyChain(ipc);
			ipc.setSaturated(saturated);

			// compute all transitively closed subproperties
			ArrayDeque<IndexedPropertyChain> queue = new ArrayDeque<IndexedPropertyChain>();
			queue.add(ipc);
			for (;;) {
				IndexedPropertyChain r = queue.poll();
				if (r == null)
					break;
				if (saturated.derivedSubProperties.add(r)) {
					if (r instanceof IndexedBinaryPropertyChain)
						saturated.derivedSubCompositions
								.add((IndexedBinaryPropertyChain) r);
					if (r instanceof IndexedObjectProperty
							&& ((IndexedObjectProperty) r).isToldReflexive())
						saturated.isReflexive = true;
					if (r.getToldSubProperties() != null)
						for (IndexedPropertyChain s : r.getToldSubProperties())
							queue.add(s);
				}
			}

			// compute all transitively closed superproperties
			queue.add(ipc);
			for (;;) {
				IndexedPropertyChain r = queue.poll();
				if (r == null)
					break;
				if (saturated.derivedSuperProperties.add(r)
						&& r.getToldSuperProperties() != null)
					for (IndexedPropertyChain s : r.getToldSuperProperties())
						queue.add(s);
			}

			// compute all transitively closed right subproperties
			queue.add(ipc);
			for (;;) {
				IndexedPropertyChain r = queue.poll();
				if (r == null)
					break;
				if (saturated.derivedRightSubProperties.add(r)) {
					if (r instanceof IndexedObjectProperty
							&& ((IndexedObjectProperty) r).isToldReflexive())
						saturated.hasReflexiveRightSubProperty = true;
					if (r.getToldSubProperties() != null)
						for (IndexedPropertyChain s : r.getToldSubProperties())
							queue.add(s);
					if (r instanceof IndexedBinaryPropertyChain) {
						IndexedPropertyChain s = ((IndexedBinaryPropertyChain) r)
								.getRightProperty();
						queue.add(s);
					}
				}
			}

			// compute all left-composible properties
			for (IndexedPropertyChain r : saturated.derivedSuperProperties) {
				if (r.getRightChains() != null) {
					for (IndexedBinaryPropertyChain chain : r.getRightChains())
						queue.add(chain.getLeftProperty());
				}
			}
			for (;;) {
				IndexedPropertyChain r = queue.poll();
				if (r == null)
					break;
				if (saturated.leftComposableProperties.add(r)) {
					if (r instanceof IndexedObjectProperty
							&& ((IndexedObjectProperty) r).isToldReflexive())
						saturated.hasReflexiveLeftComposableProperty = true;
					if (r.getToldSubProperties() != null)
						for (IndexedPropertyChain s : r.getToldSubProperties())
							queue.add(s);
					if (r instanceof IndexedBinaryPropertyChain) {
						IndexedPropertyChain s = ((IndexedBinaryPropertyChain) r)
								.getRightProperty();
						queue.add(s);
					}
				}
			}

		}

		@Override
		public void process() throws InterruptedException {
			// nothing to do here, everything should be processed during the
			// submission
		}

		@Override
		public void finish() {
		}
	}

	@Override
	public Engine getEngine() {
		return this.engine;
	}
}
