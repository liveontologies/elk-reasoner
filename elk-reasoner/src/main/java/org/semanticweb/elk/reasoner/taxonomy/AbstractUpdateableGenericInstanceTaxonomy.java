package org.semanticweb.elk.reasoner.taxonomy;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.reasoner.taxonomy.model.BottomTypeNode;
import org.semanticweb.elk.reasoner.taxonomy.model.ComparatorKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.NodeFactory;
import org.semanticweb.elk.reasoner.taxonomy.model.TypeNode;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableGenericInstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableGenericNodeStore;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableGenericTaxonomyTypeNode;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableInstanceTaxonomy;
import org.semanticweb.elk.util.collections.LazySetUnion;

public abstract class AbstractUpdateableGenericInstanceTaxonomy<
				T extends ElkEntity,
				I extends ElkEntity,
				TN extends UpdateableGenericTaxonomyTypeNode<T, I, TN, IN>,
				IN extends UpdateableGenericInstanceNode<T, I, TN, IN>,
				BN extends BottomTypeNode<T, I>
		>
		extends AbstractUpdateableGenericTaxonomy<T, TN, BN>
		implements UpdateableInstanceTaxonomy<T, I> {
	
	private final NodeFactory<I, IN> instanceNodeFactory_;
	
	/** The store containing instance nodes of this taxonomy. */
	protected final UpdateableGenericNodeStore<I, IN> instanceNodeStore_;

	public AbstractUpdateableGenericInstanceTaxonomy(
			final UpdateableGenericNodeStore<T, TN> typeNodeStore,
			final InternalNodeFactoryFactory<T, TN, BN> typeNodeFactoryFactory,
			final UpdateableGenericNodeStore<I, IN> instanceNodeStore,
			final InternalNodeFactoryFactory<I, IN, InstanceTaxonomy<T, I>> instanceNodeFactoryFactory,
			final T topMember) {
		super(typeNodeStore, typeNodeFactoryFactory, topMember);
		this.instanceNodeStore_ = instanceNodeStore;
		this.instanceNodeFactory_ = instanceNodeFactoryFactory.createInternalNodeFactory(this);
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
	public InstanceNode<T, I> getCreateInstanceNode(
			final Collection<? extends I> instances) {
		return instanceNodeStore_.getCreateNode(instances, instances.size(), instanceNodeFactory_);
	}

	@Override
	public boolean setCreateDirectTypes(final InstanceNode<T, I> instanceNode,
			final Iterable<? extends Collection<? extends T>> typeSets) {
		
		final IN node = toInternalInstanceNode(instanceNode);
		
		for (final Collection<? extends T> superMembers : typeSets) {
			final TN superNode = getCreateNode(superMembers);
			addDirectType(superNode, node);
		}

		return node.trySetAllParentsAssigned(true);
	}

	private void addDirectType(
			final TN typeNode,
			final IN instanceNode) {
		instanceNode.addDirectTypeNode(typeNode);
		typeNode.addDirectInstanceNode(instanceNode);
	}

	@Override
	public boolean removeDirectTypes(final InstanceNode<T, I> instanceNode) {
		
		final IN node = toInternalInstanceNode(instanceNode);

		if (!node.trySetAllParentsAssigned(false)) {
			return false;
		}

		final List<TN> directTypes = new ArrayList<TN>();

		synchronized (node) {
			directTypes.addAll(node.getDirectNonBottomTypeNodes());
			for (final TN typeNode : directTypes) {
				node.removeDirectTypeNode(typeNode);
			}
		}
		// detaching the removed instance node from all its direct types
		for (final TN typeNode : directTypes) {
			synchronized (typeNode) {
				typeNode.removeDirectInstanceNode(node);
			}
		}

		return true;
	}

	@Override
	public boolean removeInstanceNode(final I instance) {
		return instanceNodeStore_.removeNode(instance);
	}

	@SuppressWarnings("unchecked")
	protected IN toInternalInstanceNode(final InstanceNode<T, I> node) {
		if (node.getTaxonomy() != this) {
			throw new IllegalArgumentException(
					"The sub-node must belong to this taxonomy: " + node);
		}
		// By construction, if the node is in this taxonomy, it is of type N.
		try {
			return (IN) node;
		} catch (final ClassCastException e) {
			throw new IllegalArgumentException(
					"The sub-node must belong to this taxonomy: " + node);
		}
	}
	
}
