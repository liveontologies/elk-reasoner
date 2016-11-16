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
import org.semanticweb.elk.reasoner.taxonomy.DummyInstanceTaxonomyListener;
import org.semanticweb.elk.reasoner.taxonomy.DummyNodeStoreListener;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;
import org.semanticweb.elk.reasoner.taxonomy.model.NodeStore;
import org.semanticweb.elk.reasoner.taxonomy.model.TypeNode;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableInstanceTaxonomy;
import org.semanticweb.elk.util.collections.Operations;

/**
 * Stores information about the state of the instance taxonomy
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author Peter Skocovsky
 */
public class InstanceTaxonomyState {

	private UpdateableInstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy_ = null;

	/**
	 * Contains at least all individuals that are in ontology, but either are
	 * removed from taxonomy or their type nodes in taxonomy were removed.
	 */
	private final Queue<IndexedIndividual> modified_;

	/**
	 * Contains at least all individuals that are in taxonomy, but either are
	 * removed from ontology or their context became not saturated.
	 */
	private final Queue<IndexedIndividual> removed_;

	private final ElkPolarityExpressionConverter converter_;

	public static class Builder {

		private final Queue<IndexedIndividual> modified_ = new ConcurrentLinkedQueue<IndexedIndividual>();
		private final Queue<IndexedIndividual> removed_ = new ConcurrentLinkedQueue<IndexedIndividual>();
		private final OntologyIndex.ChangeListener ontologyListener_;

		private boolean isBuilt_ = false;

		public Builder() {
			this.ontologyListener_ = new OntologyIndexDummyChangeListener() {

				@Override
				public void individualAddition(final IndexedIndividual ind) {
					modified_.add(ind);
				}

				@Override
				public void individualRemoval(final IndexedIndividual ind) {
					removed_.add(ind);
				}

			};
		}

		public OntologyIndex.ChangeListener getOntologyListener() {
			return ontologyListener_;
		}

		public <C extends Context> InstanceTaxonomyState build(
				final SaturationState<C> saturationState,
				final DifferentialIndex ontologyIndex,
				final ElkObject.Factory elkFactory) {

			if (isBuilt_) {
				throw new IllegalStateException(
						InstanceTaxonomyState.class.getSimpleName()
								+ " can be built only once!");
			}
			isBuilt_ = true;

			return new InstanceTaxonomyState(modified_, removed_,
					saturationState, ontologyIndex, elkFactory);
		}

	}

	private <C extends Context> InstanceTaxonomyState(
			final Queue<IndexedIndividual> modified,
			final Queue<IndexedIndividual> removed,
			final SaturationState<C> saturationState,
			final DifferentialIndex ontologyIndex,
			final ElkObject.Factory elkFactory) {

		this.modified_ = modified;
		this.removed_ = removed;
		this.converter_ = new ElkPolarityExpressionConverterImpl(elkFactory,
				ontologyIndex);

		saturationState
				.addListener(new SaturationStateDummyChangeListener<C>() {

					@Override
					public void contextMarkNonSaturated(final C context) {
						final IndexedContextRoot root = context.getRoot();
						if (root instanceof IndexedIndividual) {
							final IndexedIndividual ind = (IndexedIndividual) root;
							removed_.add(ind);
						}
					}

				});

	}

	private final NodeStore.Listener<ElkNamedIndividual> nodeStoreListener_ = new DummyNodeStoreListener<ElkNamedIndividual>() {

		@Override
		public void memberForNodeDisappeared(final ElkNamedIndividual member,
				final Node<ElkNamedIndividual> node) {
			addModified(member);
		}

	};

	private final InstanceTaxonomy.Listener<ElkClass, ElkNamedIndividual> taxonomyListener_ = new DummyInstanceTaxonomyListener<ElkClass, ElkNamedIndividual>() {

		@Override
		public void directTypeRemoval(
				InstanceNode<ElkClass, ElkNamedIndividual> instanceNode,
				Collection<? extends TypeNode<ElkClass, ElkNamedIndividual>> typeNodes) {
			for (final ElkNamedIndividual elkIndividual : instanceNode) {
				addModified(elkIndividual);
			}
		};

	};

	public UpdateableInstanceTaxonomy<ElkClass, ElkNamedIndividual> getTaxonomy() {
		return taxonomy_;
	}

	private void addModified(final ElkNamedIndividual elkIndividual) {
		ModifiableIndexedClassExpression converted = elkIndividual
				.accept(converter_);
		if (converted != null && (converted instanceof IndexedIndividual)) {
			final IndexedIndividual ind = (IndexedIndividual) converted;
			modified_.add(ind);
		}
	}

	private int pruneModified() {
		final Iterator<IndexedIndividual> iter = modified_.iterator();
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
			if (taxonomy_ == null) {
				// it is not in taxonomy
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
	Collection<IndexedIndividual> getModified() {
		final int size = pruneModified();
		/*
		 * since getting the size of the queue is a linear operation, use the
		 * computed size
		 */
		return Operations.getCollection(modified_, size);
	}

	private int pruneRemoved() {
		final Iterator<IndexedIndividual> iter = removed_.iterator();
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
			if (taxonomy_ == null) {// TODO: Never set taxonomy_ to null !!!
				// it is not in taxonomy
				iter.remove();
				if (cls.occurs()) {
					modified_.add(cls);
				}
				continue;
			}
			// else
			final InstanceNode<ElkClass, ElkNamedIndividual> node = taxonomy_
					.getInstanceNode(cls.getElkEntity());
			if (node == null) {
				iter.remove();
				if (cls.occurs()) {
					modified_.add(cls);
				}
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
	Collection<IndexedIndividual> getRemoved() {
		final int size = pruneRemoved();
		/*
		 * since getting the size of the queue is a linear operation, use the
		 * computed size
		 */
		return Operations.getCollection(removed_, size);
	}

	public Writer getWriter() {
		return new Writer();
	}

	/**
	 * Groups all methods to change the state
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	public class Writer {

		void setTaxonomy(
				final UpdateableInstanceTaxonomy<ElkClass, ElkNamedIndividual> instanceTaxonomy) {
			if (taxonomy_ != null) {
				taxonomy_.removeInstanceListener(nodeStoreListener_);
				taxonomy_.removeInstanceListener(taxonomyListener_);
			}
			taxonomy_ = instanceTaxonomy;
			if (taxonomy_ != null) {
				taxonomy_.addInstanceListener(nodeStoreListener_);
				taxonomy_.addInstanceListener(taxonomyListener_);
			}
		}

		public void clearTaxonomy() {
			if (taxonomy_ != null) {
				taxonomy_.removeInstanceListener(nodeStoreListener_);
				taxonomy_.removeInstanceListener(taxonomyListener_);
			}
			taxonomy_ = null;
		}

	}

}
