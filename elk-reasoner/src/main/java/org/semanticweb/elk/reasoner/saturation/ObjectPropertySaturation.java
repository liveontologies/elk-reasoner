/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.saturation;

import java.util.ArrayDeque;
import java.util.concurrent.ExecutorService;

import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedBinaryPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.util.collections.HashListMultimap;
import org.semanticweb.elk.util.collections.Iterables;
import org.semanticweb.elk.util.concurrent.computation.AbstractConcurrentComputation;

/**
 * Computes the transitive closure of object property inclusions. Sets up
 * multimaps for fast retrieval of property compositions. 
 * 
 * @author Frantisek Simancik
 *
 */
/**
 * @author Frantisek Simancik
 * 
 */
public class ObjectPropertySaturation {

	protected final ExecutorService executor;
	protected final int maxWorkers;
	protected final OntologyIndex ontologyIndex;

	public ObjectPropertySaturation(ExecutorService executor, int maxWorkers,
			OntologyIndex ontologyIndex) {
		this.executor = executor;
		this.maxWorkers = maxWorkers;
		this.ontologyIndex = ontologyIndex;
	}

	/**
	 * @throws InterruptedException
	 * 
	 */
	public void compute() throws InterruptedException {
		// set up property hierarchy
		RoleHierarchyComputation roleHierarchyComputation = new RoleHierarchyComputation(
				executor, maxWorkers);

		roleHierarchyComputation.start();

		for (IndexedPropertyChain ipc : ontologyIndex
				.getIndexedPropertyChains()) {
			ipc.resetSaturated();
			roleHierarchyComputation.submit(ipc);
		}

		roleHierarchyComputation.waitCompletion();

		// set up property composition
//		HashMap<Pair<IndexedPropertyChain, IndexedPropertyChain>, ArrayList<IndexedPropertyChain>> m = new HashMap<Pair<IndexedPropertyChain, IndexedPropertyChain>, ArrayList<IndexedPropertyChain>>();

		for (IndexedBinaryPropertyChain chain : Iterables.filter(ontologyIndex
				.getIndexedPropertyChains(), IndexedBinaryPropertyChain.class))
			for (IndexedPropertyChain rightSubProperty : chain
					.getRightProperty().getSaturated().getSubProperties())
				for (IndexedPropertyChain leftSubProperty : chain
						.getLeftProperty().getSaturated().getSubProperties()) {
					
					SaturatedPropertyChain right = rightSubProperty.getSaturated();
					if (right.propertyCompositionsByLeftSubProperty == null)
						right.propertyCompositionsByLeftSubProperty = 
							new	HashListMultimap<IndexedPropertyChain, IndexedBinaryPropertyChain>();
					right.propertyCompositionsByLeftSubProperty.add(leftSubProperty, chain);
					
					SaturatedPropertyChain left = leftSubProperty.getSaturated();
					if (left.propertyCompositionsByRightSubProperty == null)
						left.propertyCompositionsByRightSubProperty = 
							new HashListMultimap<IndexedPropertyChain, IndexedBinaryPropertyChain>();
					left.propertyCompositionsByRightSubProperty.add(rightSubProperty, chain);
				
/*
					Pair<IndexedPropertyChain, IndexedPropertyChain> body = new Pair<IndexedPropertyChain, IndexedPropertyChain>(
							leftsubProperty, rightsubProperty);
					ArrayList<IndexedPropertyChain> list = m.get(body);

					if (list == null) {
						list = new ArrayList<IndexedPropertyChain>();
						m.put(body, list);
					}

					list.add(ria.getSuperProperty());
*/					
				}
/*
		RedundantCompositionsElimination elimination = new RedundantCompositionsElimination(
				executor, maxWorkers);

		elimination.start();
		
		for (Map.Entry<Pair<IndexedPropertyChain, IndexedPropertyChain>, ArrayList<IndexedPropertyChain>> e : m
				.entrySet()) {

			SaturatedPropertyChain firstSat = e.getKey().getFirst()
					.getSaturated();
			if (firstSat.propertyCompositionsByRightSubProperty == null)
				firstSat.propertyCompositionsByRightSubProperty = new HashListMultimap<IndexedPropertyChain, IndexedPropertyChain>();
			firstSat.propertyCompositionsByRightSubProperty.put(e.getKey()
					.getSecond(), e.getValue());

			SaturatedPropertyChain secondSat = e.getKey().getSecond()
					.getSaturated();
			if (secondSat.propertyCompositionsByLeftSubProperty == null)
				secondSat.propertyCompositionsByLeftSubProperty = new HashListMultimap<IndexedPropertyChain, IndexedPropertyChain>();
			secondSat.propertyCompositionsByLeftSubProperty.put(e.getKey()
					.getFirst(), e.getValue());

			elimination.submit(e.getValue());
		}
		m = null;
		elimination.waitCompletion();
*/		
	}

	class RoleHierarchyComputation extends
			AbstractConcurrentComputation<IndexedPropertyChain> {
		RoleHierarchyComputation(ExecutorService executor, int maxWorkers) {
			super(executor, maxWorkers, 2 * maxWorkers, 128);
		}

		@Override
		protected void process(IndexedPropertyChain ipc) {
			SaturatedPropertyChain saturated = new SaturatedPropertyChain(ipc);
			ipc.setSaturated(saturated);

			// compute all subproperties
			ArrayDeque<IndexedPropertyChain> queue = new ArrayDeque<IndexedPropertyChain>();
			saturated.derivedSubProperties.add(ipc);
			queue.addLast(ipc);
			while (!queue.isEmpty()) {
				IndexedPropertyChain r = queue.removeLast();
				if (r.getToldSubProperties() != null)
					for (IndexedPropertyChain s : r
							.getToldSubProperties())
						if (saturated.derivedSubProperties.add(s))
							queue.addLast(s);
			}

			// compute all superproperties
			queue.clear();
			saturated.derivedSuperProperties.add(ipc);
			queue.addLast(ipc);
			while (!queue.isEmpty()) {
				IndexedPropertyChain r = queue.removeLast();
				if (r.getToldSuperProperties() != null)
					for (IndexedPropertyChain s : r
							.getToldSuperProperties())
						if (saturated.derivedSuperProperties.add(s))
							queue.addLast(s);
			}

		}
	}

	
	/*
	 * if R1.R2 -> S1 and R1.R2 -> S2 with S1 -> S2, then the latter composition
	 * is redundant and is removed
	 */
/*	
	class RedundantCompositionsElimination extends
			AbstractConcurrentComputation<ArrayList<IndexedPropertyChain>> {

		public RedundantCompositionsElimination(ExecutorService executor,
				int maxWorkers) {
			super(executor, maxWorkers, 2 * maxWorkers, 128);
		}

		@Override
		protected void process(ArrayList<IndexedPropertyChain> list) {
			for (int i = 0; i < list.size(); i++)
				if (list.get(i) != null) {
					Set<IndexedPropertyChain> superProperties = list
							.get(i).getSaturated().getSuperProperties();

					for (int j = 0; j < list.size(); j++)
						if (j != i && list.get(j) != null
								&& superProperties.contains(list.get(j)))
							list.set(j, null);
				}

			Iterator<IndexedPropertyChain> iter = list.iterator();
			while (iter.hasNext()) {
				if (iter.next() == null) {
					iter.remove();
				}
			}

			list.trimToSize();
		}

	}
*/
}
