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
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.util.collections.AbstractHashMultimap;
import org.semanticweb.elk.util.collections.Pair;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;

// TODO: Documentation

/*
 * if R1.R2 -> S1 and R1.R2 -> S2 with S1 -> S2, then the latter composition
 * is redundant and is removed
 * 
 * TODO: Make it thread safe!
 */

/**
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 */
class RedundantCompositionsEliminationEngine
		implements
		InputProcessor<Map.Entry<Pair<IndexedPropertyChain, IndexedPropertyChain>, Vector<IndexedPropertyChain>>> {

	@Override
	public void submit(
			Entry<Pair<IndexedPropertyChain, IndexedPropertyChain>, Vector<IndexedPropertyChain>> next) {
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

	public static void submit(Vector<IndexedPropertyChain> v) {
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

	private class CompositionMultimap extends
			AbstractHashMultimap<IndexedPropertyChain, IndexedPropertyChain> {
		@Override
		protected Collection<IndexedPropertyChain> newRecord() {
			throw new UnsupportedOperationException();
		}
	}

}
