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
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.util.collections.AbstractHashMultimap;
import org.semanticweb.elk.util.collections.Pair;
import org.semanticweb.elk.util.concurrent.computation.ConcurrentComputation;
import org.semanticweb.elk.util.concurrent.computation.Interrupter;

//TODO: Document this class
//TODO: make it an extension of ReasonerComputation: move some method into the Engine class or create a new engine class to process todos
/**
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 */
public class RedundantCompositionsElimination
		extends
		ConcurrentComputation<Vector<IndexedPropertyChain>, RedundantCompositionsEliminationEngine> {

	/**
	 * the interrupter used to interrupt and monitor interruption for the
	 * computation
	 */
	protected final Interrupter interrupter;

	protected final Iterator<Map.Entry<Pair<IndexedPropertyChain, IndexedPropertyChain>, Vector<IndexedPropertyChain>>> todo;

	/**
	 * the next input to process
	 */
	Map.Entry<Pair<IndexedPropertyChain, IndexedPropertyChain>, Vector<IndexedPropertyChain>> nextInput;

	public RedundantCompositionsElimination(
			Interrupter interrupter,
			int maxWorkers,
			Map<Pair<IndexedPropertyChain, IndexedPropertyChain>, Vector<IndexedPropertyChain>> inputs) {
		super(new RedundantCompositionsEliminationEngine(), interrupter,
				maxWorkers);
		this.interrupter = interrupter;
		this.nextInput = null;
		this.todo = inputs.entrySet().iterator();
	}

	/**
	 * Eliminates redundancies in compositions; if interrupted by calling
	 * {@link Interrupter#interrupt()} of the provided interrupter, it can be
	 * started again to continue the computation
	 */
	public void process() {

		if (!todo.hasNext() && nextInput != null)
			return;

		start();

		try {
			// submit the leftover from the previous run
			if (nextInput != null)
				processInput(nextInput);
			// submit the next inputs from todo
			while (todo.hasNext()) {
				nextInput = todo.next();
				processInput(nextInput);
			}

			finish();
		} catch (InterruptedException e) {
			// request all workers to stop as soon as possible
			interrupter.interrupt();
			// wait until all workers are killed
			for (;;) {
				try {
					finish();
					break;
				} catch (InterruptedException e1) {
					// we'll still wait until all workers stop
					continue;
				}
			}
		}
	}

	// TODO: possible move this to a manager and make this class an extension of
	// ReasonerComputation
	private void processInput(
			Map.Entry<Pair<IndexedPropertyChain, IndexedPropertyChain>, Vector<IndexedPropertyChain>> next)
			throws InterruptedException {
		if (interrupter.isInterrupted())
			return;
		SaturatedPropertyChain firstSat = next.getKey().getFirst()
				.getSaturated();
		if (firstSat.compositionsByRightSubProperty == null)
			firstSat.compositionsByRightSubProperty = new CompositionMultimap();
		firstSat.compositionsByRightSubProperty.put(next.getKey().getSecond(),
				next.getValue());

		SaturatedPropertyChain secondSat = next.getKey().getSecond()
				.getSaturated();
		if (secondSat.compositionsByLeftSubProperty == null)
			secondSat.compositionsByLeftSubProperty = new CompositionMultimap();
		secondSat.compositionsByLeftSubProperty.put(next.getKey().getFirst(),
				next.getValue());

		submit(next.getValue());
	}

	private class CompositionMultimap extends
			AbstractHashMultimap<IndexedPropertyChain, IndexedPropertyChain> {
		@Override
		protected Collection<IndexedPropertyChain> newRecord() {
			throw new UnsupportedOperationException();
		}
	}

}
