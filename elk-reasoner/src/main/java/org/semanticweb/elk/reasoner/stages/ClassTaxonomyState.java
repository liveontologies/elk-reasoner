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
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.taxonomy.DummyNodeStoreListener;
import org.semanticweb.elk.reasoner.taxonomy.DummyTaxonomyListener;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;
import org.semanticweb.elk.reasoner.taxonomy.model.NodeStore;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableTaxonomy;
import org.semanticweb.elk.util.collections.Operations;

/**
 * Stores information about the state of the class taxonomy
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author Peter Skocovsky
 */
public class ClassTaxonomyState {

	private UpdateableTaxonomy<ElkClass> taxonomy_ = null;

	/**
	 * Contains at least all classes that are in ontology, but either are
	 * removed from taxonomy or their super-nodes in taxonomy were removed.
	 */
	private final Queue<IndexedClass> modified_ = new ConcurrentLinkedQueue<IndexedClass>();

	/**
	 * Contains at least all classes that are in taxonomy, but either are
	 * removed from ontology or their context became not saturated.
	 */
	private final Queue<IndexedClass> removed_ = new ConcurrentLinkedQueue<IndexedClass>();

	private final OntologyIndex ontologyIndex_;

	private final ElkPolarityExpressionConverter converter_;

	public <C extends Context> ClassTaxonomyState(
			final SaturationState<C> saturationState,
			final DifferentialIndex ontologyIndex,
			final ElkObject.Factory elkFactory) {

		this.ontologyIndex_ = ontologyIndex;
		this.converter_ = new ElkPolarityExpressionConverterImpl(elkFactory,
				ontologyIndex);

		ontologyIndex.addListener(new OntologyIndexDummyChangeListener() {

			@Override
			public void classAddition(final IndexedClass cls) {
				modified_.add(cls);
			}

			@Override
			public void classRemoval(final IndexedClass cls) {
				removed_.add(cls);
			}

		});

		saturationState
				.addListener(new SaturationStateDummyChangeListener<C>() {

					@Override
					public void contextMarkNonSaturated(final C context) {
						final IndexedContextRoot root = context.getRoot();
						if (root instanceof IndexedClass) {
							final IndexedClass cls = (IndexedClass) root;
							removed_.add(cls);
						}
					}

				});

	}

	private final NodeStore.Listener<ElkClass> nodeStoreListener_ = new DummyNodeStoreListener<ElkClass>() {

		@Override
		public void memberForNodeDisappeared(final ElkClass member,
				final Node<ElkClass> node) {
			addModified(member);
		}

	};

	private final Taxonomy.Listener<ElkClass> taxonomyListener_ = new DummyTaxonomyListener<ElkClass>() {

		@Override
		public void directSupernodeRemoval(final TaxonomyNode<ElkClass> subNode,
				final Collection<? extends TaxonomyNode<ElkClass>> superNodes) {
			for (final ElkClass elkClass : subNode) {
				addModified(elkClass);
			}
		}

	};

	public UpdateableTaxonomy<ElkClass> getTaxonomy() {
		return taxonomy_;
	}

	private void addModified(final ElkClass elkClass) {
		final ModifiableIndexedClassExpression converted = elkClass
				.accept(converter_);
		if (converted != null && (converted instanceof IndexedClass)) {
			final IndexedClass cls = (IndexedClass) converted;
			modified_.add(cls);
		}
	}

	private int pruneModified() {
		final Iterator<IndexedClass> iter = modified_.iterator();
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
			if (taxonomy_ == null) {
				// it is not in taxonomy
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
			if (node.equals(taxonomy_.getTopNode())) {
				iter.remove();
				continue;
			}
			// else
			if (!node.getDirectSuperNodes().isEmpty()) {
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
	Collection<IndexedClass> getModified() {
		final int size = pruneModified();
		/*
		 * since getting the size of the queue is a linear operation, use the
		 * computed size
		 */
		return Operations.getCollection(modified_, size);
	}

	private int pruneRemoved() {
		final Iterator<IndexedClass> iter = removed_.iterator();
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
			if (taxonomy_ == null) {// TODO: Never set taxonomy_ to null !!!
				// it is not in taxonomy
				iter.remove();
				if (cls.occurs()) {
					modified_.add(cls);
				}
				continue;
			}
			// else
			final TaxonomyNode<ElkClass> node = taxonomy_
					.getNode(cls.getElkEntity());
			if (node == null) {
				iter.remove();
				if (cls.occurs()) {
					modified_.add(cls);
				}
				continue;
			}
			// else
			if (cls == ontologyIndex_.getOwlNothing()) {
				iter.remove();
				if (cls.occurs()) {
					modified_.add(cls);
				}
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
	Collection<IndexedClass> getRemoved() {
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

		void setTaxonomy(UpdateableTaxonomy<ElkClass> classTaxonomy) {
			if (taxonomy_ != null) {
				taxonomy_.removeListener(nodeStoreListener_);
				taxonomy_.removeListener(taxonomyListener_);
			}
			taxonomy_ = classTaxonomy;
			if (taxonomy_ != null) {
				taxonomy_.addListener(nodeStoreListener_);
				taxonomy_.addListener(taxonomyListener_);
			}
		}

		public void clearTaxonomy() {
			if (taxonomy_ != null) {
				taxonomy_.removeListener(nodeStoreListener_);
				taxonomy_.removeListener(taxonomyListener_);
			}
			taxonomy_ = null;
		}

	}

}
