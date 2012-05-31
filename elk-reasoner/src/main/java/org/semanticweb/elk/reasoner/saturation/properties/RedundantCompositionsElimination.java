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

import java.util.Collection;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutorService;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.util.collections.AbstractHashMultimap;
import org.semanticweb.elk.util.collections.Pair;
import org.semanticweb.elk.util.concurrent.computation.ConcurrentComputation;
import org.semanticweb.elk.util.concurrent.computation.Interrupter;

//TODO: Document this class
//TODO: Add progress monitor
/**
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 */
public class RedundantCompositionsElimination extends
		ConcurrentComputation<Vector<IndexedPropertyChain>> {

	// TODO: add progress monitor
	/**
	 * the interrupter used to interrupt and monitor interruption for the
	 * computation
	 */
	protected final Interrupter interrupter;

	/**
	 * the compositions for which to eliminate redundancy
	 */
	protected final Map<Pair<IndexedPropertyChain, IndexedPropertyChain>, Vector<IndexedPropertyChain>> compositions;

	public RedundantCompositionsElimination(
			Interrupter interrupter,
			ExecutorService executor,
			int maxWorkers,
			Map<Pair<IndexedPropertyChain, IndexedPropertyChain>, Vector<IndexedPropertyChain>> compositions) {
		super(new RedundantCompositionsEliminationEngine(), interrupter,
				executor, maxWorkers, 2 * maxWorkers, 128);
		this.interrupter = interrupter;
		this.compositions = compositions;
	}

	public void compute() {

		if (compositions.isEmpty())
			return;

		start();

		try {

			for (Map.Entry<Pair<IndexedPropertyChain, IndexedPropertyChain>, Vector<IndexedPropertyChain>> e : compositions
					.entrySet()) {
				if (interrupter.isInterrupted())
					return;
				SaturatedPropertyChain firstSat = e.getKey().getFirst()
						.getSaturated();
				if (firstSat.compositionsByRightSubProperty == null)
					firstSat.compositionsByRightSubProperty = new CompositionMultimap();
				firstSat.compositionsByRightSubProperty.put(e.getKey()
						.getSecond(), e.getValue());

				SaturatedPropertyChain secondSat = e.getKey().getSecond()
						.getSaturated();
				if (secondSat.compositionsByLeftSubProperty == null)
					secondSat.compositionsByLeftSubProperty = new CompositionMultimap();
				secondSat.compositionsByLeftSubProperty.put(e.getKey()
						.getFirst(), e.getValue());

				submit(e.getValue());
			}

			finish();
			waitWorkersToStop();
		} catch (InterruptedException e) {
			interrupter.interrupt();
			Thread.interrupted();
			// wait until all workers are killed
			for (;;) {
				try {
					waitWorkersToStop();
					break;
				} catch (InterruptedException ex) {
					continue;
				}
			}
		}
	}

	private class CompositionMultimap extends
			AbstractHashMultimap<IndexedPropertyChain, IndexedPropertyChain> {
		@Override
		protected Collection<IndexedPropertyChain> newRecord() {
			throw new UnsupportedOperationException();
		}
	}

}
