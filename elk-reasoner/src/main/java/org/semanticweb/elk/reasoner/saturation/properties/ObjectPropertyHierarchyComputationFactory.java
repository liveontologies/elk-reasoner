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
import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedBinaryPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedPropertyChainVisitor;
import org.semanticweb.elk.reasoner.saturation.properties.ObjectPropertyHierarchyComputationFactory.Engine;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorFactory;

/**
 * The factory for engines that reset and compute the transitive closure of the
 * sub-properties and super-properties relations for each submitted
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
	 * closed sub-properties and super-properties of each submitted property chain.
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

			// compute all transitively closed sub-properties
			// and mark the chain as reflexive if one of its sub-properties
			// is reflexive
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

			// compute all transitively closed super-properties
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

			// compute all transitively closed right sub-properties
			// i.e. such R that S1,...,Sn (n>=0) for which S1 o ... o Sn o R => root
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

			// compute all left-composable properties
			// i.e. such R that exist S1,..., Sn  (n>=0) and T for which S1 o ... o Sn o R o root => T
			for (IndexedPropertyChain r : saturated.derivedSuperProperties) {
				// walking through the super-properties of root to find all
				// composable R' o root' (that means root is composable w/ R '
				// and all its sub-properties)
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

/**
 * Figures out whether each submitted indexed property is reflexive or not.
 * A property R is reflexive if
 * i) it is a named property and is told reflexive
 * ii) S -> R and S is reflexive
 * iii) it is a chain S o H and both S and H are reflexive
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
class FindReflexivePropertiesVisitor<O> implements IndexedPropertyChainVisitor<O> {

	boolean reflexive = false;
	//used to prevent infinite recursion in case of cyclic told hierarchies
	//TODO can be avoided if properties are partially saturated in this visitor
	final Set<IndexedPropertyChain> visited_ = new ArrayHashSet<IndexedPropertyChain>();
	
	@Override
	public O visit(IndexedObjectProperty property) {
		reflexive = property.isToldReflexive();
		
		if (!reflexive) {
			defaultVisit(property);
		}
		
		return null;
	}


	@Override
	public O visit(IndexedBinaryPropertyChain binaryChain) {
		reflexive = isReflexive(binaryChain.getLeftProperty())
				&& isReflexive(binaryChain.getRightProperty());

		return null;
	}
	
	boolean isReflexive(final IndexedPropertyChain propChain) {
		visited_.add(propChain);
		propChain.accept(this);
		
		return reflexive;
	}

	private void defaultVisit(IndexedPropertyChain propChain) {
		// go through sub-properties to see if some is reflexive
		// stop if so
		if (propChain.getToldSubProperties() != null) {
			for (IndexedPropertyChain subChain : propChain
					.getToldSubProperties()) {
				if (!visited_.contains(subChain)) {
					if (reflexive = isReflexive(subChain)) {
						break;
					}
				}
			}
		}
	}
}