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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
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
	private final Set<IndexedIndividual> modified_ = Collections
			.newSetFromMap(new ConcurrentHashMap<IndexedIndividual, Boolean>());

	/**
	 * Contains at least all individuals that are in taxonomy, but either are
	 * removed from ontology or their context became not saturated.
	 */
	private final Queue<IndexedIndividual> removed_ = new ConcurrentLinkedQueue<IndexedIndividual>();

	private final ElkPolarityExpressionConverter converter_;

	public <C extends Context> InstanceTaxonomyState(
			final SaturationState<C> saturationState,
			final DifferentialIndex ontologyIndex,
			final ElkObject.Factory elkFactory) {

		this.converter_ = new ElkPolarityExpressionConverterImpl(elkFactory,
				ontologyIndex);

		ontologyIndex.addListener(new OntologyIndexDummyChangeListener() {

			@Override
			public void individualAddition(final IndexedIndividual ind) {
				modified_.add(ind);
			}

			@Override
			public void individualRemoval(final IndexedIndividual ind) {
				removed_.add(ind);
			}

		});

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

	final NodeStore.Listener<ElkNamedIndividual> nodeStoreListener_ = new DummyNodeStoreListener<ElkNamedIndividual>() {

		@Override
		public void memberForNodeDisappeared(final ElkNamedIndividual member,
				final Node<ElkNamedIndividual> node) {
			addModified(member);
		}

	};

	final InstanceTaxonomy.Listener<ElkClass, ElkNamedIndividual> taxonomyListener_ = new DummyInstanceTaxonomyListener<ElkClass, ElkNamedIndividual>() {

		@Override
		public void directTypeRemoval(
				InstanceNode<ElkClass, ElkNamedIndividual> instanceNode,
				Collection<? extends TypeNode<ElkClass, ElkNamedIndividual>> typeNodes) {
			for (final ElkNamedIndividual elkClass : instanceNode) {
				addModified(elkClass);
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

	private void pruneModified() {
		final Iterator<IndexedIndividual> iter = modified_.iterator();
		while (iter.hasNext()) {
			final IndexedIndividual cls = iter.next();
			if (!cls.occurs()) {
				iter.remove();
			}
		}
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
	Set<IndexedIndividual> getModified() {
		pruneModified();
		return modified_;
	}

	private void pruneRemoved() {
		final Iterator<IndexedIndividual> iter = removed_.iterator();
		while (iter.hasNext()) {
			final IndexedIndividual cls = iter.next();
			if (taxonomy_ == null// TODO: Never set taxonomy_ to null !!!
					|| taxonomy_.getInstanceNode(cls.getElkEntity()) == null) {
				iter.remove();
				if (cls.occurs()) {
					modified_.add(cls);
				}
			}
		}
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
		pruneRemoved();
		return removed_;
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
