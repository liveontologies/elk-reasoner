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
import java.util.Set;
import java.util.Vector;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedBinaryPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.properties.ObjectPropertyCompositionsPrecomputationFactory.Engine;
import org.semanticweb.elk.util.collections.AbstractHashMultimap;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorFactory;

/**
 * The factory for engines that set up the compositions multimap in
 * {@link SaturatedPropertyChain} that are used in {@link RuleRoleComposition}.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 */
public class ObjectPropertyCompositionsPrecomputationFactory implements
		InputProcessorFactory<IndexedPropertyChain, Engine> {

	/**
	 * We use a single engine for this factory
	 */
	private final Engine engine;

	ObjectPropertyCompositionsPrecomputationFactory() {
		this.engine = new Engine();
	}

	@Override
	public void finish() {
		// nothing to do so far
	}

	/**
	 * If R and S are in the vector and R is a subproperty of S, then S is
	 * removed from the vector.
	 */
	private static void eliminateImpliedCompositions(
			Vector<IndexedPropertyChain> v) {
		// replace all redundant elements by null
		for (int i = 0; i < v.size(); i++)
			if (v.get(i) != null) {
				Set<IndexedPropertyChain> superProperties = v.get(i)
						.getSaturated().getSuperProperties();

				for (int j = 0; j < v.size(); j++)
					if (v.get(j) != null && j != i
							&& superProperties.contains(v.get(j)))
						v.set(j, null);
			}

		// shift all non-null elements to the begin and resize
		int next = 0;
		for (int i = 0; i < v.size(); i++)
			if (v.get(i) != null) {
				v.set(next++, v.get(i));
			}
		v.setSize(next);
	}

	class Engine implements InputProcessor<IndexedPropertyChain> {

		/**
		 * If set to true, then binary property chains that do not occur
		 * negatively are skipped in the derivation and replaced directly by all
		 * their told superproperties. For example, given an inclusion
		 * SubObjectPropertyOf(ObjectPropertyChain(R1 R2) R), the composition of
		 * R1 and R2 derives directly R skipping the auxiliary binary chain
		 * representing [R1 R2].
		 */
		// private static final boolean REPLACE_CHAINS_BY_TOLD_SUPER_PROPERTIES
		// = true;

		/**
		 * If set to true, then compositions between each pair of R1 and R2 are
		 * reduced under role hierarchies. For example, given
		 * 
		 * SubObjectPropertyOf(ObjectPropertyChain(R1 R2) S1),
		 * SubObjectPropertyOf(ObjectPropertyChain(R1 R2) S2), and
		 * SubObjectPropertyOf(S1 S2),
		 * 
		 * the composition of R1 and R2 derives only S1 and not S2. Note that
		 * this only makes sense if REPLACE_CHAINS_BY_TOLD_SUPER_PROPERTIES is
		 * also on.
		 */
		private static final boolean ELIMINATE_IMPLIED_COMPOSITIONS = true;

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.semanticweb.elk.util.concurrent.computation.InputProcessor#submit
		 * (java.lang.Object)
		 * 
		 * Computes all compositions with the submitted property on the right.
		 */
		@Override
		public void submit(IndexedPropertyChain rightProperty) {

			SaturatedPropertyChain rightPropertySaturated = rightProperty
					.getSaturated();

			// find all chains that have some superproperty of
			// rightProperty on the right and register them for the
			// rightProperty
			for (IndexedPropertyChain rightSuperProperty : rightProperty
					.getSaturated().getSuperProperties())
				if (rightSuperProperty.getRightChains() != null)
					for (IndexedBinaryPropertyChain chain : rightSuperProperty
							.getRightChains())
						registerComposition(chain, rightPropertySaturated);

			if (rightPropertySaturated.compositionsByLeftSubProperty == null)
				return;

			// register all compositions computed in the previous step also for
			// their left components
			for (Map.Entry<IndexedPropertyChain, Collection<IndexedPropertyChain>> entry : rightPropertySaturated.compositionsByLeftSubProperty
					.entrySet()) {
				SaturatedPropertyChain leftPropertySaturated = entry.getKey()
						.getSaturated();
				Vector<IndexedPropertyChain> compositions = (Vector<IndexedPropertyChain>) entry
						.getValue();

				// the same leftProperty can be accessed from multiple
				// rightProperties so we synchronize
				synchronized (leftPropertySaturated) {
					if (leftPropertySaturated.compositionsByRightSubProperty == null)
						leftPropertySaturated.compositionsByRightSubProperty = new CompositionMultimap();
					leftPropertySaturated.compositionsByRightSubProperty.put(
							rightProperty, compositions);
				}

				if (ELIMINATE_IMPLIED_COMPOSITIONS)
					eliminateImpliedCompositions(compositions);
			}
		}

		@Override
		public void process() throws InterruptedException {
			// nothing to do here, everything should be processed during the
			// submission
		}

		/**
		 * Add (S, chain) to
		 * rightPropertySaturated.compositionsByLeftSubProperty for each
		 * subproperty S of chain.leftProperty. The compositions are not yet
		 * registered for the left components.
		 */
		private void registerComposition(IndexedBinaryPropertyChain chain,
				SaturatedPropertyChain rightPropertySaturated) {

			if (rightPropertySaturated.compositionsByLeftSubProperty == null) {
				rightPropertySaturated.compositionsByLeftSubProperty = new CompositionMultimap();
			}

			for (IndexedPropertyChain leftProperty : chain.getLeftProperty()
					.getSaturated().getSubProperties()) {

				/*
				 * If the chain does not occur negatively, then replace it by
				 * its told super properties.
				 */
				// if (REPLACE_CHAINS_BY_TOLD_SUPER_PROPERTIES
				// && chain.getRightChains() == null) {
				// for (IndexedObjectProperty superChain : chain
				// .getToldSuperProperties())
				// rightPropertySaturated.compositionsByLeftSubProperty
				// .add(leftProperty, superChain);
				// } else
				rightPropertySaturated.compositionsByLeftSubProperty.add(
						leftProperty, chain);
			}
		}

		/**
		 * An implementation of the {@link AbstractHashMultimap} that uses
		 * Vectors for collections of values.
		 */
		private class CompositionMultimap
				extends
				AbstractHashMultimap<IndexedPropertyChain, IndexedPropertyChain> {

			@Override
			protected Vector<IndexedPropertyChain> newRecord() {
				return new Vector<IndexedPropertyChain>();
			}
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
