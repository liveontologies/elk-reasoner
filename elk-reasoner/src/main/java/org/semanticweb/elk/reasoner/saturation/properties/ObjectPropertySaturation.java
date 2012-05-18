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
package org.semanticweb.elk.reasoner.saturation.properties;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedBinaryPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.util.collections.AbstractHashMultimap;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.collections.Pair;
import org.semanticweb.elk.util.concurrent.computation.ConcurrentComputation;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;

/**
 * Computes the transitive closure of object property inclusions. Sets up
 * multimaps for fast retrieval of property compositions.
 * 
 * @author Frantisek Simancik
 * 
 */
public class ObjectPropertySaturation {
	
	/**
	 * Logger for events.
	 */
	protected final static Logger LOGGER_ = Logger.getLogger(ObjectPropertySaturation.class);
	

	protected final ExecutorService executor;
	protected final int maxWorkers;
	protected final OntologyIndex ontologyIndex;

	public ObjectPropertySaturation(ExecutorService executor, int maxWorkers,
			OntologyIndex ontologyIndex) {
		this.executor = executor;
		this.maxWorkers = maxWorkers;
		this.ontologyIndex = ontologyIndex;
	}

	public void compute() {
		try {
			tryCompute();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * @throws InterruptedException
	 * 
	 */
	protected void tryCompute() throws InterruptedException {
		// set up property hierarchy
		ConcurrentComputation<IndexedPropertyChain> roleHierarchyComputation = new ConcurrentComputation<IndexedPropertyChain>(
				new RoleHierarchyComputationEngine(), executor, maxWorkers,
				2 * maxWorkers, 128);

		roleHierarchyComputation.start();

		for (IndexedPropertyChain ipc : ontologyIndex
				.getIndexedPropertyChains()) {
			ipc.resetSaturated();
			roleHierarchyComputation.submit(ipc);
		}

		roleHierarchyComputation.waitCompletion();

		// find auxiliary IndexedBinaryPropertyChains that occur on the right of some (longer) chain
		Set<IndexedBinaryPropertyChain> auxiliaryChains = new HashSet<IndexedBinaryPropertyChain>();
		for (IndexedBinaryPropertyChain chain : Operations.filter(
				ontologyIndex.getIndexedPropertyChains(),
				IndexedBinaryPropertyChain.class)) {

			if (chain.getRightProperty() instanceof IndexedBinaryPropertyChain)
				auxiliaryChains.add((IndexedBinaryPropertyChain) chain
						.getRightProperty());
		}

		// set up property compositions
		Map<Pair<IndexedPropertyChain, IndexedPropertyChain>, Vector<IndexedPropertyChain>> compositions = 
				new HashMap<Pair<IndexedPropertyChain, IndexedPropertyChain>, Vector<IndexedPropertyChain>>();

		for (IndexedBinaryPropertyChain chain : Operations.filter(
				ontologyIndex.getIndexedPropertyChains(),
				IndexedBinaryPropertyChain.class))
			for (IndexedPropertyChain rightSubProperty : chain
					.getRightProperty().getSaturated().getSubProperties())
				for (IndexedPropertyChain leftSubProperty : chain
						.getLeftProperty().getSaturated().getSubProperties()) {

					// SaturatedPropertyChain right = rightSubProperty
					// .getSaturated();
					// if (right.compositionsByLeftSubProperty == null)
					// right.compositionsByLeftSubProperty = new
					// HashListMultimap<IndexedPropertyChain,
					// IndexedBinaryPropertyChain>();
					// right.compositionsByLeftSubProperty.add(
					// leftSubProperty, chain);
					//
					// SaturatedPropertyChain left = leftSubProperty
					// .getSaturated();
					// if (left.compositionsByRightSubProperty == null)
					// left.compositionsByRightSubProperty = new
					// HashListMultimap<IndexedPropertyChain,
					// IndexedBinaryPropertyChain>();
					// left.compositionsByRightSubProperty.add(
					// rightSubProperty, chain);

					Pair<IndexedPropertyChain, IndexedPropertyChain> key = new Pair<IndexedPropertyChain, IndexedPropertyChain>(
							leftSubProperty, rightSubProperty);
					Vector<IndexedPropertyChain> value = compositions.get(key);

					if (value == null) {
						value = new Vector<IndexedPropertyChain>();
						compositions.put(key, value);
					}

					if (auxiliaryChains.contains(chain))
						value.add(chain);
					else
						for (IndexedObjectProperty superProperty : chain
								.getToldSuperProperties())
							value.add(superProperty);
				}
		
		if (compositions.isEmpty())
			return;
		
		ConcurrentComputation<Vector<IndexedPropertyChain>> redundantCompositionsElimination = new ConcurrentComputation<Vector<IndexedPropertyChain>>(
				new RedundantCompositionsEliminationEngine(), executor, maxWorkers,
				2 * maxWorkers, 128);
		 redundantCompositionsElimination.start();
		 
		for (Map.Entry<Pair<IndexedPropertyChain, IndexedPropertyChain>, Vector<IndexedPropertyChain>> e : compositions
				.entrySet()) {
			
			SaturatedPropertyChain firstSat = e.getKey().getFirst()
					.getSaturated();
			if (firstSat.compositionsByRightSubProperty == null)
				firstSat.compositionsByRightSubProperty = new CompositionMultimap();
			firstSat.compositionsByRightSubProperty.put(e.getKey().getSecond(),
					e.getValue());

			SaturatedPropertyChain secondSat = e.getKey().getSecond()
					.getSaturated();
			if (secondSat.compositionsByLeftSubProperty == null)
				secondSat.compositionsByLeftSubProperty = new CompositionMultimap();
			secondSat.compositionsByLeftSubProperty.put(e.getKey().getFirst(),
					e.getValue());
			
			redundantCompositionsElimination.submit(e.getValue());
		}

		redundantCompositionsElimination.waitCompletion();
	}

	private class RoleHierarchyComputationEngine implements
			InputProcessor<IndexedPropertyChain> {

		@Override
		public void submit(IndexedPropertyChain ipc) {
			SaturatedPropertyChain saturated = new SaturatedPropertyChain(ipc);
			ipc.setSaturated(saturated);

			// compute all subproperties
			ArrayDeque<IndexedPropertyChain> queue = new ArrayDeque<IndexedPropertyChain>();
			saturated.derivedSubProperties.add(ipc);
			queue.addLast(ipc);
			while (!queue.isEmpty()) {
				IndexedPropertyChain r = queue.removeLast();
				if (r.getToldSubProperties() != null)
					for (IndexedPropertyChain s : r.getToldSubProperties())
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
					for (IndexedPropertyChain s : r.getToldSuperProperties())
						if (saturated.derivedSuperProperties.add(s))
							queue.addLast(s);
			}

		}

		@Override
		public void process() throws InterruptedException {
			// nothing to do here, everything should be processed during the
			// submission
		}

		@Override
		public boolean canProcess() {
			return false;
		}
	}

	/*
	 * if R1.R2 -> S1 and R1.R2 -> S2 with S1 -> S2, then the latter composition
	 * is redundant and is removed
	 */

	private class RedundantCompositionsEliminationEngine implements
			InputProcessor<Vector<IndexedPropertyChain>> {

		@Override
		public void submit(Vector<IndexedPropertyChain> v) {
			for (int i = 0; i < v.size(); i++)
				if (v.get(i) != null) {
					Set<IndexedPropertyChain> superProperties = v.get(i)
							.getSaturated().getSuperProperties();

					for (int j = 0; j < v.size(); j++)
						if (j != i && v.get(j) != null
								&& superProperties.contains(v.get(j)))
							v.set(j, null);
				}

			int next = 0;
			for (int i = 0; i < v.size(); i++)
				if (v.get(i) != null) {
					v.set(next++, v.get(i));
				}

			v.setSize(next);
		}

		@Override
		public void process() throws InterruptedException {
			// nothing to do here, everything should be processed during the
			// submission
		}

		@Override
		public boolean canProcess() {
			return false;
		}
	}
	
	private class CompositionMultimap extends AbstractHashMultimap<IndexedPropertyChain, IndexedPropertyChain> {
		@Override
		protected Collection<IndexedPropertyChain> newRecord() {
			throw new UnsupportedOperationException();
		}
	}
}
