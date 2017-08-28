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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.reasoner.indexing.classes.DifferentialIndex;
import org.semanticweb.elk.reasoner.indexing.classes.OntologyIndexDummyChangeListener;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkPolarityExpressionConverter;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkPolarityExpressionConverterImpl;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.model.IndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.OntologyIndex;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStateDummyChangeListener;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.taxonomy.ConcurrentInstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.DummyInstanceTaxonomyListener;
import org.semanticweb.elk.reasoner.taxonomy.DummyNodeStoreListener;
import org.semanticweb.elk.reasoner.taxonomy.ElkIndividualKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;
import org.semanticweb.elk.reasoner.taxonomy.model.NodeStore;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableInstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableTaxonomy;
import org.semanticweb.elk.util.collections.Operations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stores information about the state of the instance taxonomy
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author Peter Skocovsky
 */
public class InstanceTaxonomyState {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(InstanceTaxonomyState.class);

	private UpdateableInstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy_ = null;

	/**
	 * Contains at least all individuals that are in ontology, but either were
	 * removed from taxonomy or their type nodes in taxonomy were removed since
	 * the last completed construction of the taxonomy.
	 */
	private final Queue<IndexedIndividual> toAdd_ = new ConcurrentLinkedQueue<IndexedIndividual>();

	/**
	 * Contains at least all individuals that are in taxonomy, but either were
	 * removed from ontology or their context became not saturated since the
	 * last completed construction of the taxonomy.
	 */
	private final Queue<IndexedIndividual> toRemove_ = new ConcurrentLinkedQueue<IndexedIndividual>();

	private final OntologyIndex ontologyIndex_;

	private final SaturationState<?> saturationState_;

	private final ElkPolarityExpressionConverter converter_;

	public <C extends Context> InstanceTaxonomyState(
			final SaturationState<C> saturationState,
			final DifferentialIndex ontologyIndex,
			final ElkObject.Factory elkFactory) {

		this.ontologyIndex_ = ontologyIndex;
		this.saturationState_ = saturationState;
		this.converter_ = new ElkPolarityExpressionConverterImpl(elkFactory,
				ontologyIndex);

		ontologyIndex.addListener(new OntologyIndexDummyChangeListener() {

			@Override
			public void individualAddition(final IndexedIndividual ind) {
				toAdd_.add(ind);
			}

			@Override
			public void individualRemoval(final IndexedIndividual ind) {
				toRemove_.add(ind);
			}

		});

		saturationState
				.addListener(new SaturationStateDummyChangeListener<C>() {

					@Override
					public void contextMarkNonSaturated(final C context) {
						final IndexedContextRoot root = context.getRoot();
						if (root instanceof IndexedIndividual) {
							final IndexedIndividual ind = (IndexedIndividual) root;
							toRemove_.add(ind);
						}
					}

				});

	}

	private final NodeStore.Listener<ElkNamedIndividual> nodeStoreListener_ = new DummyNodeStoreListener<ElkNamedIndividual>() {

		@Override
		public void memberForNodeDisappeared(final ElkNamedIndividual member,
				final Node<ElkNamedIndividual> node) {
			addToAdd(member);
		}

	};

	private final InstanceTaxonomy.Listener<ElkClass, ElkNamedIndividual> taxonomyListener_ = new DummyInstanceTaxonomyListener<ElkClass, ElkNamedIndividual>() {

		@Override
		public void directTypeNodesDisappeared(
				final InstanceNode<ElkClass, ElkNamedIndividual> instanceNode) {
			for (final ElkNamedIndividual elkIndividual : instanceNode) {
				addToAdd(elkIndividual);
			}
		};

	};

	public UpdateableInstanceTaxonomy<ElkClass, ElkNamedIndividual> getTaxonomy() {
		return taxonomy_;
	}

	private void addToAdd(final ElkNamedIndividual elkIndividual) {
		ModifiableIndexedClassExpression converted = elkIndividual
				.accept(converter_);
		if (converted != null && (converted instanceof IndexedIndividual)) {
			final IndexedIndividual ind = (IndexedIndividual) converted;
			toAdd_.add(ind);
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
		final Iterator<IndexedIndividual> iter = toAdd_.iterator();
		int size = 0;
		while (iter.hasNext()) {
			final IndexedIndividual ind = iter.next();
			/* @formatter:off
			 * 
			 * Should be pruned when:
			 * it is not in ontology, or
			 * it has types in taxonomy.
			 * 
			 * @formatter:on
			 */
			if (!ind.occurs()) {
				iter.remove();
				continue;
			}
			// else
			final Context context = saturationState_.getContext(ind);
			if (context == null || !context.isInitialized()
					|| !context.isSaturated()) {
				// it is not saturated.
				size++;
				continue;
			}
			// else
			final InstanceNode<ElkClass, ElkNamedIndividual> node = taxonomy_
					.getInstanceNode(ind.getElkEntity());
			if (node == null) {
				// it is not in taxonomy
				size++;
				continue;
			}
			// else
			if (!node.getDirectTypeNodes().isEmpty()) {
				iter.remove();
				continue;
			}
			// else
			size++;
		}
		return size;
	}

	/**
	 * Returns collection that contains at least all individuals that are in
	 * ontology, but either are removed from taxonomy or their type nodes in
	 * taxonomy were removed.
	 * 
	 * @return collection that contains at least all individuals that are in
	 *         ontology, but either are removed from taxonomy or their type
	 *         nodes in taxonomy were removed.
	 */
	Collection<IndexedIndividual> getToAdd() {
		if (taxonomy_ == null) {
			// No individual can be pruned.
			return toAdd_;
		}
		// else
		final int size = pruneToAdd();
		/*
		 * since getting the size of the queue is a linear operation, use the
		 * computed size
		 */
		return Operations.getCollection(toAdd_, size);
	}

	/**
	 * Prunes {@link #toRemove_}.
	 * <p>
	 * <strong>{@code taxonomy_} must not be {@code null}!</strong>
	 * 
	 * @return The resulting size of {@link #toRemove_}.
	 */
	private int pruneToRemove() {
		final Iterator<IndexedIndividual> iter = toRemove_.iterator();
		int size = 0;
		while (iter.hasNext()) {
			final IndexedIndividual cls = iter.next();
			/* @formatter:off
			 * 
			 * Should be pruned when
			 * it is not in taxonomy.
			 * 
			 * @formatter:on
			 */
			final InstanceNode<ElkClass, ElkNamedIndividual> node = taxonomy_
					.getInstanceNode(cls.getElkEntity());
			if (node == null) {
				iter.remove();
				continue;
			}
			// else
			size++;
		}
		return size;
	}

	/**
	 * Returns collection that contains at least all individuals that are in
	 * taxonomy, but either are removed from ontology or their context became
	 * not saturated.
	 * 
	 * @return collection that contains at least all individuals that are in
	 *         taxonomy, but either are removed from ontology or their context
	 *         became not saturated.
	 */
	Collection<IndexedIndividual> getToRemove() {
		if (taxonomy_ == null) {// TODO: Never set taxonomy_ to null !!!
			// no individuals are in taxonomy
			toRemove_.clear();
			return Collections.emptyList();
		}
		// else
		final int size = pruneToRemove();
		/*
		 * since getting the size of the queue is a linear operation, use the
		 * computed size
		 */
		return Operations.getCollection(toRemove_, size);
	}

	private void resetTaxonomy(
			final UpdateableTaxonomy<ElkClass> classTaxonomy) {
		LOGGER_.trace("Reset instance taxonomy");

		if (taxonomy_ != null) {
			taxonomy_.removeInstanceListener(nodeStoreListener_);
			taxonomy_.removeInstanceListener(taxonomyListener_);
		}
		taxonomy_ = new ConcurrentInstanceTaxonomy(classTaxonomy,
				ElkIndividualKeyProvider.INSTANCE);
		taxonomy_.addInstanceListener(nodeStoreListener_);
		taxonomy_.addInstanceListener(taxonomyListener_);

		// All individuals need to be added to the taxonomy
		toRemove_.clear();
		toAdd_.clear();
		toAdd_.addAll(ontologyIndex_.getIndividuals());

	}

	/**
	 * Notifies this {@link InstanceTaxonomyState} that the taxonomy has just
	 * been completely construction.
	 */
	void taxonomyComplete() {
		// Clear pending individuals.
		toRemove_.clear();
		toAdd_.clear();
	}

	private final ClassTaxonomyState.Listener CLASS_TAXONOMY_STATE_LISTENER = new ClassTaxonomyState.Listener() {

		@Override
		public void taxonomyReset(
				final UpdateableTaxonomy<ElkClass> oldTaxonomy,
				final UpdateableTaxonomy<ElkClass> newTaxonomy) {
			resetTaxonomy(newTaxonomy);
		}

	};

	public ClassTaxonomyState.Listener getClassTaxonomyStateListener() {
		return CLASS_TAXONOMY_STATE_LISTENER;
	}

}
