/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.stages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.reasoner.indexing.classes.DifferentialIndex;
import org.semanticweb.elk.reasoner.indexing.classes.OntologyIndexDummyChangeListener;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkPolarityExpressionConverter;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkPolarityExpressionConverterImpl;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.OntologyIndex;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStateDummyChangeListener;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.taxonomy.ConcurrentClassTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.DummyNodeStoreListener;
import org.semanticweb.elk.reasoner.taxonomy.DummyTaxonomyListener;
import org.semanticweb.elk.reasoner.taxonomy.ElkClassKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;
import org.semanticweb.elk.reasoner.taxonomy.model.NodeStore;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableTaxonomy;
import org.semanticweb.elk.util.collections.Operations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stores information about the state of the class taxonomy
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author Peter Skocovsky
 */
public class ClassTaxonomyState {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ClassTaxonomyState.class);

	private UpdateableTaxonomy<ElkClass> taxonomy_ = null;

	/**
	 * Contains at least all classes that are in ontology, but either were
	 * removed from taxonomy or their super-nodes in taxonomy were removed since
	 * the last completed construction of the taxonomy.
	 */
	private final Queue<IndexedClass> toAdd_ = new ConcurrentLinkedQueue<IndexedClass>();

	/**
	 * Contains at least all classes that are in taxonomy, but either were
	 * removed from ontology or their context became not saturated since the
	 * last completed construction of the taxonomy.
	 */
	private final ConcurrentHashMap<IndexedClass,IndexedClass> toRemove_ = new ConcurrentHashMap<>();
	
	private final ElkObject.Factory elkFactory_;

	private final OntologyIndex ontologyIndex_;

	private final SaturationState<?> saturationState_;

	private final ElkPolarityExpressionConverter converter_;

	private final List<Listener> listeners_ = new ArrayList<Listener>();

	<C extends Context> ClassTaxonomyState(
			final SaturationState<C> saturationState,
			final DifferentialIndex ontologyIndex,
			final ElkObject.Factory elkFactory,
			final Collection<Listener> listeners) {

		this.elkFactory_ = elkFactory;
		this.ontologyIndex_ = ontologyIndex;
		this.saturationState_ = saturationState;
		this.converter_ = new ElkPolarityExpressionConverterImpl(elkFactory,
				ontologyIndex);
		this.listeners_.addAll(listeners);

		resetTaxonomy();

		ontologyIndex.addListener(new OntologyIndexDummyChangeListener() {

			@Override
			public void classAddition(final IndexedClass cls) {
				LOGGER_.trace("To add: {}", cls);
				toAdd_.add(cls);
			}

			@Override
			public void classRemoval(final IndexedClass cls) {
				toRemove(cls);
			}

		});

		saturationState
				.addListener(new SaturationStateDummyChangeListener<C>() {

					
					@Override
					public void contextMarkNonSaturated(final C context) {
						contextModified(context.getRoot());
					}

					@Override
					public void conclusionAdded(ClassConclusion conclusion) {
						contextModified(conclusion.getDestination());
					}					
					
					public void contextModified(IndexedContextRoot root) {
						if (root instanceof IndexedClass) {
							final IndexedClass cls = (IndexedClass) root;
							toRemove(cls);
						}
					}

					@Override
					public void contextsClear() {
						// The whole taxonomy must be computed from scratch.
						resetTaxonomy();
					}

				});

	}

	
	private void toRemove(IndexedClass cls) {
		LOGGER_.trace("To remove: {}", cls);
		if (toRemove_.get(cls) == null) {
			toRemove_.put(cls, cls);
		}
	}
	
	public <C extends Context> ClassTaxonomyState(
			final SaturationState<C> saturationState,
			final DifferentialIndex ontologyIndex,
			final ElkObject.Factory elkFactory) {
		this(saturationState, ontologyIndex, elkFactory,
				Collections.<Listener> emptyList());
	}

	private final NodeStore.Listener<ElkClass> nodeStoreListener_ = new DummyNodeStoreListener<ElkClass>() {

		@Override
		public void memberForNodeDisappeared(final ElkClass member,
				final Node<ElkClass> node) {
			addToAdd(member);
		}

	};

	private final Taxonomy.Listener<ElkClass> taxonomyListener_ = new DummyTaxonomyListener<ElkClass>() {

		@Override
		public void directSuperNodesDisappeared(
				final TaxonomyNode<ElkClass> subNode) {
			for (final ElkClass elkClass : subNode) {
				addToAdd(elkClass);
			}
		}

	};

	public UpdateableTaxonomy<ElkClass> getTaxonomy() {
		return taxonomy_;
	}

	private void addToAdd(final ElkClass elkClass) {
		final ModifiableIndexedClassExpression converted = elkClass
				.accept(converter_);
		if (converted != null && (converted instanceof IndexedClass)) {
			final IndexedClass cls = (IndexedClass) converted;
			toAdd_.add(cls);
		}
	}

	/**
	 * Prunes {@link #toAdd_}.
	 * <p>
	 * <strong>{@code taxonomy_} must not be {@code null}!</strong>
	 * 
	 * @return The resulting size of {@link #toAdd_}.
	 */
	private int pruneToAdd() {
		final Iterator<IndexedClass> iter = toAdd_.iterator();
		int size = 0;
		while (iter.hasNext()) {
			final IndexedClass cls = iter.next();
			/* @formatter:off
			 * 
			 * Should be pruned when:
			 * it is not in ontology, or
			 * it is in the top node, or
			 * it has super-nodes in taxonomy.
			 * 
			 * @formatter:on
			 */
			if (!cls.occurs()) {
				iter.remove();
				continue;
			}
			// else
			final Context context = saturationState_.getContext(cls);
			if (context == null || !context.isInitialized()
					|| !context.isSaturated()) {
				// it is not saturated.
				size++;
				continue;
			}
			// else
			final TaxonomyNode<ElkClass> node = taxonomy_
					.getNode(cls.getElkEntity());
			if (node == null) {
				// it is not in taxonomy
				size++;
				continue;
			}
			// else
			if (node.equals(taxonomy_.getTopNode())
					|| !node.getDirectSuperNodes().isEmpty()) {
				iter.remove();
				continue;
			}
			// else
			size++;
		}
		return size;
	}

	/**
	 * Returns collection that contains at least all classes that are in
	 * ontology, but either are removed from taxonomy or their super-nodes in
	 * taxonomy were removed.
	 * 
	 * @return collection that contains at least all classes that are in
	 *         ontology, but either are removed from taxonomy or their
	 *         super-nodes in taxonomy were removed.
	 */
	Collection<IndexedClass> getToAdd() {
		final int size = pruneToAdd();
		/*
		 * since getting the size of the queue is a linear operation, use the
		 * computed size
		 */
		return Operations.getCollection(toAdd_, size);
	}

	/**
	 * Prunes {@link #toRemoveQueue_}.
	 * <p>
	 * <strong>{@code taxonomy_} must not be {@code null}!</strong>
	 * 
	 * @return The resulting size of {@link #toRemoveQueue_}.
	 */
	private int pruneToRemove() {
		final Iterator<IndexedClass> iter = toRemove_.keySet().iterator();
		int size = 0;
		while (iter.hasNext()) {
			final IndexedClass cls = iter.next();
			/* @formatter:off
			 * 
			 * Should be pruned when
			 * it is not in taxonomy, or
			 * it is in the bottom class.
			 * 
			 * @formatter:on
			 */
			final TaxonomyNode<ElkClass> node = taxonomy_
					.getNode(cls.getElkEntity());
			if (node == null || cls == ontologyIndex_.getOwlNothing()) {
				iter.remove();
				continue;
			}
			size++;
		}
		return size;
	}

	/**
	 * Returns collection that contains at least all classes that are in
	 * taxonomy, but either are removed from ontology or their context became
	 * not saturated.
	 * 
	 * @return collection that contains at least all classes that are in
	 *         taxonomy, but either are removed from ontology or their context
	 *         became not saturated.
	 */
	Collection<IndexedClass> getToRemove() {
		pruneToRemove();
		return toRemove_.keySet();
	}

	void resetTaxonomy() {
		LOGGER_.trace("Reset class taxonomy");

		final UpdateableTaxonomy<ElkClass> old = taxonomy_;
		if (taxonomy_ != null) {
			taxonomy_.removeListener(nodeStoreListener_);
			taxonomy_.removeListener(taxonomyListener_);
		}
		taxonomy_ = new ConcurrentClassTaxonomy(elkFactory_,
				ElkClassKeyProvider.INSTANCE);
		for (final Listener listener : listeners_) {
			listener.taxonomyReset(old, taxonomy_);
		}
		taxonomy_.addListener(nodeStoreListener_);
		taxonomy_.addListener(taxonomyListener_);

		// All classes need to be added to the taxonomy
		toRemove_.clear();
		toAdd_.clear();
		toAdd_.addAll(ontologyIndex_.getClasses());

	}

	/**
	 * Notifies this {@link ClassTaxonomyState} that the taxonomy has just been
	 * completely construction.
	 */
	void taxonomyComplete() {
		// Clear pending classes.
		toRemove_.clear();
		toAdd_.clear();
	}

	void addListener(final Listener listener) {
		listeners_.add(listener);
	}

	void removeListener(final Listener listener) {
		listeners_.remove(listener);
	}

	static interface Listener {

		/**
		 * Called immediately after the result of calling
		 * {@link ClassTaxonomyState#getTaxonomy()} changes.
		 * 
		 * @param oldTaxonomy
		 *            value of {@link ClassTaxonomyState#getTaxonomy()} before
		 *            the change
		 * @param newTaxonomy
		 *            value of {@link ClassTaxonomyState#getTaxonomy()} after
		 *            the change
		 */
		void taxonomyReset(UpdateableTaxonomy<ElkClass> oldTaxonomy,
				UpdateableTaxonomy<ElkClass> newTaxonomy);

	}

}
