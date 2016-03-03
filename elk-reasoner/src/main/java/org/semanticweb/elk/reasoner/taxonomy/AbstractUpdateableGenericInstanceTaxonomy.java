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
package org.semanticweb.elk.reasoner.taxonomy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.reasoner.taxonomy.model.ComparatorKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.model.GenericInstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.GenericTypeNode;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.NodeFactory;
import org.semanticweb.elk.reasoner.taxonomy.model.TypeNode;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableGenericNodeStore;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableTaxonomyTypeNode;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableInstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableInstanceTaxonomy;
import org.semanticweb.elk.util.collections.LazySetUnion;

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
	
	private final NodeFactory<I, UIN> instanceNodeFactory_;
	
	/** The store containing instance nodes of this taxonomy. */
	protected final UpdateableGenericNodeStore<I, UIN> instanceNodeStore_;

	public AbstractUpdateableGenericInstanceTaxonomy(
			final UpdateableGenericNodeStore<T, UTN> typeNodeStore,
			final InternalNodeFactoryFactory<T, UTN, AbstractDistinctBottomTaxonomy<T, TN, UTN>> typeNodeFactoryFactory,
			final UpdateableGenericNodeStore<I, UIN> instanceNodeStore,
			final InternalNodeFactoryFactory<I, UIN, InstanceTaxonomy<T, I>> instanceNodeFactoryFactory,
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
	public abstract TN getBottomNode();

	@Override
	public InstanceNode<T, I> getCreateInstanceNode(
			final Collection<? extends I> instances) {
		return instanceNodeStore_.getCreateNode(instances, instances.size(), instanceNodeFactory_);
	}

	@Override
	public boolean setCreateDirectTypes(final InstanceNode<T, I> instanceNode,
			final Iterable<? extends Collection<? extends T>> typeSets) {
		
		final UIN node = toInternalInstanceNode(instanceNode);
		
		for (final Collection<? extends T> superMembers : typeSets) {
			final UTN superNode = getCreateNode(superMembers);
			addDirectType(superNode, node);
		}

		return node.trySetAllParentsAssigned(true);
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
	
}
