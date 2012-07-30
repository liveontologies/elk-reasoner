package org.semanticweb.elk.reasoner.saturation.properties;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
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

import java.util.ArrayDeque;
import java.util.Queue;

import org.semanticweb.elk.owl.interfaces.ElkDataProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDataProperty;
import org.semanticweb.elk.reasoner.saturation.properties.DataPropertyHierarchyComputationFactory.Engine;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorFactory;

/**
 * The factory for engines that compute the transitive closure of
 * {@link ElkDataProperty} hierarchy.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class DataPropertyHierarchyComputationFactory implements
		InputProcessorFactory<IndexedDataProperty, Engine> {

	/**
	 * We use a single engine for this factory
	 */
	private final Engine engine;

	DataPropertyHierarchyComputationFactory() {
		this.engine = new Engine();
	}

	class Engine implements InputProcessor<IndexedDataProperty> {

		// don't allow creating of engines directly; only through the factory
		private Engine() {
		}

		@Override
		public void submit(IndexedDataProperty idp) {
			// reset the saturation of this property chain
			idp.resetSaturated();
			SaturatedDataProperty saturated = new SaturatedDataProperty(idp);
			idp.setSaturated(saturated);

			// compute all transitively closed super-properties
			Queue<IndexedDataProperty> todo = new ArrayDeque<IndexedDataProperty>();
			saturated.derivedSuperProperties.add(idp);
			todo.add(idp);
			for (;;) {
				IndexedDataProperty next = todo.poll();
				if (next == null)
					break;
				Iterable<IndexedDataProperty> toldSuperProperties = next
						.getToldSuperProperties();
				if (toldSuperProperties == null)
					break;
				for (IndexedDataProperty sup : toldSuperProperties)
					if (saturated.derivedSuperProperties.add(sup))
						todo.add(sup);
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
