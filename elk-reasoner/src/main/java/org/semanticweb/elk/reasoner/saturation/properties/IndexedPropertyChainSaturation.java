/**
 * 
 */
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedBinaryPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedPropertyChainVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedPropertyChainVisitorEx;
import org.semanticweb.elk.reasoner.saturation.properties.SaturatedPropertyChain.REFLEXIVITY;
import org.semanticweb.elk.util.collections.AbstractHashMultimap;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.collections.Multimap;
import org.semanticweb.elk.util.collections.Operations;

/**
 * Saturates indexed property chains (that is, populates the
 * {@link SaturatedPropertyChain} object for a given
 * {@link IndexedPropertyChain}).
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class IndexedPropertyChainSaturation {
	/**
	 * Used internally instead of boolean flags for clarity
	 */
	enum SIDE {
		LEFT, RIGHT
	};

	private static final Logger LOGGER_ = Logger
			.getLogger(IndexedPropertyChainSaturation.class);
	
	/*
	 * A stateless and thread-safe visitor to check reflexivity of property chains
	 */
	private static final ReflexivityCheckVisitor REFLEXIVITY_VISITOR = new ReflexivityCheckVisitor();

	/**
	 * The main method which takes a chain and saturates it.
	 * 
	 * @param ipc
	 * @return
	 */
	public static SaturatedPropertyChain saturate(final IndexedPropertyChain ipc) {
		SaturatedPropertyChain saturated_ = ipc.getSaturated(false);

		if (saturated_ != null && saturated_.isComputed()) {
			return saturated_;
		} else {
			saturated_ = new SaturatedPropertyChain(ipc, isReflexive(ipc));
		}

		if (LOGGER_.isTraceEnabled()) {
			LOGGER_.trace("Saturating property chain " + ipc);
		}

		//make vars final to access them in visitors
		final SaturatedPropertyChain saturated = saturated_;
		final ArrayDeque<IndexedPropertyChain> queue = new ArrayDeque<IndexedPropertyChain>();
		final ArrayDeque<IndexedPropertyChain> leftComposable = new ArrayDeque<IndexedPropertyChain>();
		
		IndexedPropertyChain next = null;
		// compute all transitively closed super-properties
		queue.add(ipc);

		while ((next = queue.poll()) != null) {
			if (saturated.derivedSuperProperties.add(next)) {
				/*
				 * This visitor is what previously was in the composition computation stage
				 */
				next.accept(new IndexedPropertyChainVisitor<ElkObject>() {

					@Override
					public ElkObject visit(IndexedObjectProperty ipc) {
						for (IndexedBinaryPropertyChain chain : emptyIfNull(ipc	.getLeftChains())) {
							if (saturated.compositionsByRightSubProperty == null) {
								saturated.compositionsByRightSubProperty = new CompositionByPropertyMultimap();
							}

							registerComposition(chain,
									saturated.compositionsByRightSubProperty,
									SIDE.RIGHT);
						}
						
						return defaultVisit(ipc, saturated);
					}
					
					/*
					 * Binary chains can't occur on the left side of another chain
					 * so we only look where they appear on the right
					 */
					@Override
					public ElkObject visit(IndexedBinaryPropertyChain ipc) {
						return defaultVisit(ipc, saturated);
					}
					
					private ElkObject defaultVisit(IndexedPropertyChain ipc, SaturatedPropertyChain saturated) {
						for (IndexedBinaryPropertyChain chain : emptyIfNull(ipc.getRightChains())) {
							if (saturated.compositionsByLeftSubProperty == null) {
								saturated.compositionsByLeftSubProperty = new CompositionByPropertyMultimap();
							}

							registerComposition(chain,
									saturated.compositionsByLeftSubProperty, SIDE.LEFT);
						}
						
						return null;
					}
				});

				// precompute left-composable R' o root' (that means root is
				// composable w/ R '
				// and all its sub-properties). Sub-properties of R will be
				// accounted for later.
				for (IndexedBinaryPropertyChain chain : emptyIfNull(next
						.getRightChains())) {
					leftComposable.add(chain.getLeftProperty());
				}

				addDirectSuperProperties(next, queue);
			}
		}

		if (SaturatedPropertyChain.ELIMINATE_IMPLIED_COMPOSITIONS) {
			eliminateImpliedCompositions(saturated);
		}

		// compute all transitively closed sub-properties
		// and mark the chain as reflexive if one of its sub-properties
		// is reflexive
		queue.add(ipc);

		while ((next = queue.poll()) != null) {
			if (saturated.derivedSubProperties.add(next)) {
				addDirectSubProperties(next, queue, saturated);
			}
		}

		// compute all transitively closed right sub-properties
		// i.e. such R that S1,...,Sn (n>=0) for which S1 o ... o Sn o R => root
		queue.add(ipc);
		processRightSubProperties(saturated.derivedRightSubProperties, queue);
		// compute all left-composable properties:
		// S1 o ... o Sn o R o root => T
		processRightSubProperties(saturated.leftComposableProperties,
				leftComposable);

		saturated.computed = true;

		return saturated;
	}

	/*
	 * Goes through sub-properties and right properties of binary chains and
	 * populates the passed collection
	 */
	private static void processRightSubProperties(
			final Collection<IndexedPropertyChain> toPopulate,
			final Queue<IndexedPropertyChain> queue) {
		IndexedPropertyChain next = null;

		while ((next = queue.poll()) != null) {
			if (toPopulate.add(next)) {
				next.accept(new IndexedPropertyChainVisitor<ElkObject>() {

					@Override
					public ElkObject visit(IndexedObjectProperty prop) {
						queue.addAll(emptyIfNull(prop.getToldSubProperties()));

						return null;
					}

					@Override
					public ElkObject visit(IndexedBinaryPropertyChain chain) {
						queue.add(chain.getRightProperty());

						if (isReflexive(chain.getRightProperty())) {
							queue.add(chain.getLeftProperty());
						}

						return null;
					}
				});
			}
		}
	}

	private static <T> Collection<T> emptyIfNull(Collection<T> collection) {
		return collection == null ? Collections.<T> emptyList() : collection;
	}

	private static void eliminateImpliedCompositions(
			SaturatedPropertyChain saturated) {
		if (saturated.compositionsByLeftSubProperty != null) {
			for (Collection<IndexedPropertyChain> compositions : saturated.compositionsByLeftSubProperty
					.values()) {
				eliminateImpliedCompositions(compositions);
			}
		}

		if (saturated.compositionsByRightSubProperty != null) {
			for (Collection<IndexedPropertyChain> compositions : saturated.compositionsByRightSubProperty
					.values()) {
				eliminateImpliedCompositions(compositions);
			}
		}
	}

	/**
	 * If R and S are in the vector and R is a sub-property of S, then S is
	 * removed from the vector.
	 */
	private static void eliminateImpliedCompositions(
			Collection<IndexedPropertyChain> v) {
		List<IndexedPropertyChain> toDelete = null;

		for (IndexedPropertyChain ipc : v) {
			SaturatedPropertyChain saturated = ipc.getSaturated(false);
			Collection<? extends IndexedPropertyChain> superProperties = saturated != null ? saturated
					.getSuperProperties() : ipc.getToldSuperProperties();

			for (IndexedPropertyChain prop : v) {
				if (ipc != prop && superProperties.contains(prop)) {
					toDelete = toDelete == null ? new ArrayList<IndexedPropertyChain>()
							: toDelete;
					toDelete.add(prop);
					break;
				}
			}
		}

		if (toDelete != null) {
			v.removeAll(toDelete);
		}
	}

	private static boolean isReflexive(final IndexedPropertyChain ipc) {
		SaturatedPropertyChain saturated = ipc.getSaturated(false);

		if (saturated == null || !saturated.reflexivityDetermined()) {
			REFLEXIVITY_VISITOR.isReflexive(ipc);
		}

		return ipc.getSaturated(false).isReflexive();
	}

	// Returns told super-properties and also
	// {R | S o ipc -> R or ipc o S -> R, S is reflexive}
	private static void addDirectSuperProperties(
			final IndexedPropertyChain ipc,
			final Collection<IndexedPropertyChain> props) {

		ipc.accept(new IndexedPropertyChainVisitor<ElkObject>() {
			
			/*
			 * For object properties we look at both right and left chains
			 */
			@Override
			public ElkObject visit(IndexedObjectProperty prop) {
				for (IndexedBinaryPropertyChain chain : Operations.concat(
						emptyIfNull(prop.getLeftChains()),
						emptyIfNull(prop.getRightChains()))) {
					addSuperChain(chain, prop);
				}
				
				props.addAll(ipc.getToldSuperProperties());
				
				return null;
			}

			/*
			 * For chains it's sufficient to only look at right chains
			 */
			@Override
			public ElkObject visit(IndexedBinaryPropertyChain chain) {
				for (IndexedBinaryPropertyChain right : emptyIfNull(ipc.getRightChains())) {
					addSuperChain(right, chain);
				}
				
				return null;
			}
			
			private void addSuperChain(IndexedBinaryPropertyChain chain, IndexedPropertyChain ipc) {
				IndexedPropertyChain composable = chain.getComposable(ipc);
				
				if (isReflexive(composable)) {
					props.addAll(chain.getToldSuperProperties());
					props.add(chain);
				}
			}
		});
	}


	/*
	 * Adds told sub-properties (for named properties) and derived through
	 * reflexivity sub-propertied (for chains) to the supplied collection. Also,
	 * if saturated is provided, it registers derived sub-compositions in it
	 */
	private static void addDirectSubProperties(final IndexedPropertyChain ipc,
			final Collection<IndexedPropertyChain> props,
			final SaturatedPropertyChain saturated) {
		ipc.accept(new IndexedPropertyChainVisitor<ElkObject>() {

			@Override
			public ElkObject visit(IndexedObjectProperty prop) {
				props.addAll(emptyIfNull(prop.getToldSubProperties()));

				return null;
			}

			@Override
			public ElkObject visit(IndexedBinaryPropertyChain chain) {
				if (isReflexive(chain.getLeftProperty())) {
					props.add(chain.getRightProperty());
				}

				if (isReflexive(chain.getRightProperty())) {
					props.add(chain.getLeftProperty());
				}

				if (saturated != null) {
					saturated.derivedSubCompositions.add(chain);
				}

				return null;
			}
		});
	}

	/**
	 * Add (S, chain) to the multimap (compositionsByLeftSubProperty or
	 * compositionsByRightSubProperty) for each sub-property of the chain.
	 */
	private static void registerComposition(
			IndexedBinaryPropertyChain chain,
			Multimap<IndexedPropertyChain, IndexedPropertyChain> compositionMultimap,
			SIDE side) {

		ArrayDeque<IndexedPropertyChain> queue = new ArrayDeque<IndexedPropertyChain>();
		IndexedPropertyChain next = null;

		queue.add(side == SIDE.LEFT ? chain.getLeftProperty() : chain
				.getRightProperty());

		while ((next = queue.poll()) != null) {
			boolean added = false;

			if (SaturatedPropertyChain.REPLACE_CHAINS_BY_TOLD_SUPER_PROPERTIES
					&& chain.getRightChains() == null) {
				for (IndexedPropertyChain superChain : chain.getToldSuperProperties()) {
					added |= compositionMultimap.add(next, superChain);
				}
			} else {
				added = compositionMultimap.add(next, chain);
			}

			if (added) {
				addDirectSubProperties(next, queue, null);
			}
		}
	}
}

/**
 * Figures out whether each submitted indexed property is reflexive or not. A
 * property R is reflexive if 
 * i) it is a named property and is told reflexive
 * ii) S -> R and S is reflexive 
 * iii) it is a chain S o H and both S and H are
 * reflexive
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
class ReflexivityCheckVisitor implements IndexedPropertyChainVisitorEx<Boolean, Set<IndexedPropertyChain>> {

	@Override
	public Boolean visit(IndexedObjectProperty property, Set<IndexedPropertyChain> visited) {
		return !property.isToldReflexive() ? defaultVisit(property, visited) : true;
	}

	@Override
	public Boolean visit(IndexedBinaryPropertyChain binaryChain, Set<IndexedPropertyChain> visited) {
		return isReflexive(binaryChain.getLeftProperty(), visited)
				&& isReflexive(binaryChain.getRightProperty(), visited);
	}

	boolean isReflexive(final IndexedPropertyChain propChain) {
		return isReflexive(propChain, new ArrayHashSet<IndexedPropertyChain>());
	}
	
	boolean isReflexive(final IndexedPropertyChain propChain, final Set<IndexedPropertyChain> visited) {
		SaturatedPropertyChain saturated = getCreateSaturated(propChain);

		if (visited.contains(propChain)) {
			return saturated.isReflexive();
		} else {
			visited.add(propChain);
			boolean reflexive = propChain.accept(this, visited);

			saturated.isReflexive.set(reflexive ? REFLEXIVITY.TRUE
					: REFLEXIVITY.FALSE);

			return reflexive;
		}
	}

	private boolean defaultVisit(final IndexedPropertyChain propChain, final Set<IndexedPropertyChain> visited) {
		// go through sub-properties to see if some is reflexive
		// stop if so
		if (propChain.getToldSubProperties() != null) {
			for (IndexedPropertyChain subChain : propChain
					.getToldSubProperties()) {
				if (isReflexive(subChain, visited)) {
					// TODO optimisation: mark all super-roles as reflexive?
					// break;
					return true;
				}
			}
		}

		return false;
	}

	SaturatedPropertyChain getCreateSaturated(final IndexedPropertyChain ipc) {
		// synch required, otherwise may have two saturation objects for the
		// same property
		synchronized (ipc) {
			SaturatedPropertyChain saturated = ipc.getSaturated(false);

			if (saturated == null) {
				saturated = new SaturatedPropertyChain(ipc);
				ipc.setSaturated(saturated);
			}

			return saturated;
		}
	}
}

/**
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
class CompositionByPropertyMultimap extends
		AbstractHashMultimap<IndexedPropertyChain, IndexedPropertyChain> {

	@Override
	protected Collection<IndexedPropertyChain> newRecord() {
		return new ArrayHashSet<IndexedPropertyChain>(8);
	}
}