/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.taxonomy.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.reasoner.taxonomy.hashing.InstanceTaxonomyEqualator;
import org.semanticweb.elk.reasoner.taxonomy.hashing.InstanceTaxonomyHasher;
import org.semanticweb.elk.reasoner.taxonomy.model.ComparatorKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.model.GenericInstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.GenericTypeNode;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.NodeFactory;
import org.semanticweb.elk.reasoner.taxonomy.model.NodeStore;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNodeFactory;
import org.semanticweb.elk.reasoner.taxonomy.model.TypeNode;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableInstanceTaxonomy;
import org.semanticweb.elk.util.collections.LazySetUnion;

/**
 * A generic implementation of instance taxonomy that extends an implementation
 * of class taxonomy.
 * 
 * @author Peter Skocovsky
 *
 * @param <T>
 *            The type of members of the type nodes in this taxonomy.
 * @param <I>
 *            The type of members of the instance nodes in this taxonomy.
 * @param <TN>
 *            The immutable type of type nodes in this taxonomy.
 * @param <IN>
 *            The immutable type of instance nodes in this taxonomy.
 * @param <UTN>
 *            The mutable type of type nodes in this taxonomy.
 * @param <UIN>
 *            The mutable type of instance nodes in this taxonomy.
 */
public abstract class AbstractUpdateableGenericInstanceTaxonomy<
				T extends ElkEntity,
				I extends ElkEntity,
				TN extends GenericTypeNode<T, I, TN, IN>,
				IN extends GenericInstanceNode<T, I, TN, IN>,
				UTN extends UpdateableTaxonomyTypeNode<T, I, TN, IN, UTN, UIN>,
				UIN extends UpdateableInstanceNode<T, I, TN, IN, UTN, UIN>
		>
		extends AbstractUpdateableGenericTaxonomy<T, TN, UTN>
		implements UpdateableInstanceTaxonomy<T, I> {
	
	/** Factory that creates instance nodes. */
	private final NodeFactory<I, UIN> instanceNodeFactory_;
	
	/** The store containing instance nodes of this taxonomy. */
	protected final UpdateableNodeStore<I, UIN> instanceNodeStore_;
	
	/** The listeners notified about the changes to instance taxonomy. */
	protected final List<InstanceTaxonomy.Listener<T, I>> instanceListeners_;

	/**
	 * Constructor.
	 * 
	 * @param typeNodeStore
	 *            Node store for the type nodes.
	 * @param typeNodeFactory
	 *            Factory that creates type nodes.
	 * @param instanceNodeStore
	 *            Node store for the instance nodes.
	 * @param instanceNodeFactory
	 *            Factory that creates instance nodes.
	 * @param topMember
	 *            The canonical member of the top node.
	 */
	public AbstractUpdateableGenericInstanceTaxonomy(
			final UpdateableNodeStore<T, UTN> typeNodeStore,
			final TaxonomyNodeFactory<T, UTN, AbstractDistinctBottomTaxonomy<T, TN, UTN>> typeNodeFactory,
			final UpdateableNodeStore<I, UIN> instanceNodeStore,
			final TaxonomyNodeFactory<I, UIN, InstanceTaxonomy<T, I>> instanceNodeFactory,
			final T topMember) {
		super(typeNodeStore, typeNodeFactory, topMember);
		this.instanceNodeStore_ = instanceNodeStore;
		this.instanceNodeFactory_ = new NodeFactory<I, UIN>() {
			@Override
			public UIN createNode(final Iterable<? extends I> members,
					final int size) {
				return instanceNodeFactory.createNode(members, size,
						AbstractUpdateableGenericInstanceTaxonomy.this);
			}
		};
		this.instanceListeners_ = new ArrayList<InstanceTaxonomy.Listener<T, I>>();
	}

	@Override
	public ComparatorKeyProvider<? super I> getInstanceKeyProvider() {
		return instanceNodeStore_.getKeyProvider();
	}

	@Override
	public InstanceNode<T, I> getInstanceNode(final I elkEntity) {
		return instanceNodeStore_.getNode(elkEntity);
	}

	@Override
	public Set<? extends InstanceNode<T, I>> getInstanceNodes() {
		return instanceNodeStore_.getNodes();
	}

	@Override
	public TypeNode<T, I> getNode(final T elkEntity) {
		TypeNode<T, I> result = nodeStore_.getNode(elkEntity);
		if (result == null && getBottomNode().contains(elkEntity)) {
			result = getBottomNode();
		}
		return result;
	}

	@Override
	public Set<? extends TypeNode<T, I>> getNodes() {
		return new LazySetUnion<TypeNode<T, I>>(nodeStore_.getNodes(),
				Collections.singleton(getBottomNode()));
	}

	@Override
	public abstract TN getBottomNode();

	@Override
	public InstanceNode<T, I> getCreateInstanceNode(
			final Collection<? extends I> instances) {
		return instanceNodeStore_.getCreateNode(instances, instances.size(),
				instanceNodeFactory_);
	}

	@Override
	public boolean setCreateDirectTypes(final InstanceNode<T, I> instanceNode,
			final Iterable<? extends Collection<? extends T>> typeSets) {

		final UIN node = toInternalInstanceNode(instanceNode);

		boolean isTypeSetsEmpty = true;

		for (final Collection<? extends T> superMembers : typeSets) {
			final UTN superNode = getCreateNode(superMembers);
			isTypeSetsEmpty = false;
			addDirectType(superNode, node);
		}

		if (node.trySetAllParentsAssigned(true)) {
			if (!isTypeSetsEmpty) {
				fireDirectTypeAssignment(instanceNode,
						instanceNode.getDirectTypeNodes());
			}
			return true;
		} else {
			return false;
		}
	}

	private void addDirectType(
			final UTN typeNode,
			final UIN instanceNode) {
		instanceNode.addDirectTypeNode(typeNode);
		typeNode.addDirectInstanceNode(instanceNode);
	}

	@Override
	public boolean removeDirectTypes(final InstanceNode<T, I> instanceNode) {

		final UIN node = toInternalInstanceNode(instanceNode);

		if (!node.trySetAllParentsAssigned(false)) {
			return false;
		}

		final List<UTN> directTypes = new ArrayList<UTN>();

		synchronized (node) {
			directTypes.addAll(node.getDirectNonBottomTypeNodes());
			for (final UTN typeNode : directTypes) {
				node.removeDirectTypeNode(typeNode);
			}
		}
		// detaching the removed instance node from all its direct types
		for (final UTN typeNode : directTypes) {
			synchronized (typeNode) {
				typeNode.removeDirectInstanceNode(node);
			}
		}

		fireDirectTypeRemoval(instanceNode, directTypes);

		return true;
	}

	@Override
	public boolean removeInstanceNode(final I instance) {
		return instanceNodeStore_.removeNode(instance);
	}

	@SuppressWarnings("unchecked")
	protected UIN toInternalInstanceNode(final InstanceNode<T, I> node) {
		if (node.getTaxonomy() != this) {
			throw new IllegalArgumentException(
					"The sub-node must belong to this taxonomy: " + node);
		}
		// By construction, if the node is in this taxonomy, it is of type N.
		try {
			return (UIN) node;
		} catch (final ClassCastException e) {
			throw new IllegalArgumentException(
					"The sub-node must belong to this taxonomy: " + node);
		}
	}

	@Override
	public int hashCode() {
		return InstanceTaxonomyHasher.hash(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(final Object obj) {

		if (!(obj instanceof Taxonomy<?>)) {
			return false;
		}

		try {
			return InstanceTaxonomyEqualator.equals(this, (Taxonomy<T>) obj);
		} catch (ClassCastException e) {
			return false;
		}
	}

	@Override
	public boolean addInstanceListener(final NodeStore.Listener<I> listener) {
		return instanceNodeStore_.addListener(listener);
	}

	@Override
	public boolean removeInstanceListener(
			final NodeStore.Listener<I> listener) {
		return instanceNodeStore_.removeListener(listener);
	}
	
	@Override
	public boolean addInstanceListener(
			final InstanceTaxonomy.Listener<T, I> listener) {
		return instanceListeners_.add(listener);
	}
	
	@Override
	public boolean removeInstanceListener(
			final InstanceTaxonomy.Listener<T, I> listener) {
		return instanceListeners_.remove(listener);
	}

	protected void fireDirectTypeAssignment(
			final InstanceNode<T, I> instanceNode,
			final Collection<? extends TypeNode<T, I>> typeNodes) {
		for (final InstanceTaxonomy.Listener<T, I> listener : instanceListeners_) {
			listener.directTypeAssignment(instanceNode, typeNodes);
		}
	}

	protected void fireDirectTypeRemoval(final InstanceNode<T, I> instanceNode,
			final Collection<? extends TypeNode<T, I>> typeNodes) {
		for (final InstanceTaxonomy.Listener<T, I> listener : instanceListeners_) {
			listener.directTypeRemoval(instanceNode, typeNodes);
		}
	}

}
