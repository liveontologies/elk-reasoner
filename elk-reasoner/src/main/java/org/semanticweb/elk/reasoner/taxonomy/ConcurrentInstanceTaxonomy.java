/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Oxford University Computing Laboratory
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
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.predefined.PredefinedElkClassFactory;
import org.semanticweb.elk.reasoner.taxonomy.impl.AbstractInstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.impl.ConcurrentNodeStore;
import org.semanticweb.elk.reasoner.taxonomy.impl.IndividualNode;
import org.semanticweb.elk.reasoner.taxonomy.impl.UpdateableInstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.impl.UpdateableNodeStore;
import org.semanticweb.elk.reasoner.taxonomy.impl.UpdateableTypeNode;
import org.semanticweb.elk.reasoner.taxonomy.model.ComparatorKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.model.GenericInstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.GenericTypeNode;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.NodeFactory;
import org.semanticweb.elk.reasoner.taxonomy.model.NodeStore;
import org.semanticweb.elk.reasoner.taxonomy.model.NonBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.NonBottomTypeNode;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.TypeNode;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableInstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableTaxonomy;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.collections.Operations.FunctorEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Instance taxonomy that is suitable for concurrent processing. Taxonomy
 * objects are only constructed for consistent ontologies, and some consequences
 * of this are hardcoded here.
 * <p>
 * This class wraps an instance of {@link UpdateableTaxonomy} and lazily
 * generates wrappers for its nodes to store direct instances.
 * 
 * @author Yevgeny Kazakov
 * @author Frantisek Simancik
 * @author Markus Kroetzsch
 * @author Pavel Klinov
 * @author Peter Skocovsky
 */
public class ConcurrentInstanceTaxonomy
		extends AbstractInstanceTaxonomy<ElkClass, ElkNamedIndividual>
		implements UpdateableInstanceTaxonomy<ElkClass, ElkNamedIndividual> {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ConcurrentInstanceTaxonomy.class);

	/**
	 * The store for instance nodes of this taxonomy.
	 */
	private final UpdateableNodeStore<ElkNamedIndividual, IndividualNode.Projection<ElkClass, ElkNamedIndividual>> individualNodeStore_;

	/**
	 * The wrapped class taxonomy
	 */
	private final UpdateableTaxonomy<ElkClass> classTaxonomy_;

	/**
	 * Map from wrapped nodes to their wrappers.
	 */
	private final ConcurrentMap<TaxonomyNode<ElkClass>, UpdateableTypeNodeWrapper> wrapperMap_;

	/** The listeners notified about the changes to instance taxonomy. */
	protected final List<InstanceTaxonomy.Listener<ElkClass, ElkNamedIndividual>> instanceListeners_;

	public ConcurrentInstanceTaxonomy(
			PredefinedElkClassFactory elkFactory,
			final ComparatorKeyProvider<ElkEntity> classKeyProvider,
			final ComparatorKeyProvider<ElkEntity> individualKeyProvider) {
		this(new ConcurrentClassTaxonomy(elkFactory, classKeyProvider),
				individualKeyProvider);
	}

	public ConcurrentInstanceTaxonomy(
			final UpdateableTaxonomy<ElkClass> classTaxonomy,
			final ComparatorKeyProvider<ElkEntity> individualKeyProvider) {
		this.individualNodeStore_ = new ConcurrentNodeStore<ElkNamedIndividual, IndividualNode.Projection<ElkClass, ElkNamedIndividual>>(
				individualKeyProvider);
		this.classTaxonomy_ = classTaxonomy;
		this.wrapperMap_ = new ConcurrentHashMap<TaxonomyNode<ElkClass>, UpdateableTypeNodeWrapper>();
		this.instanceListeners_ = new ArrayList<InstanceTaxonomy.Listener<ElkClass, ElkNamedIndividual>>();
	}

	@Override
	public ComparatorKeyProvider<? super ElkClass> getKeyProvider() {
		return classTaxonomy_.getKeyProvider();
	}

	@Override
	public ComparatorKeyProvider<? super ElkNamedIndividual> getInstanceKeyProvider() {
		return individualNodeStore_.getKeyProvider();
	}

	@Override
	public TypeNode<ElkClass, ElkNamedIndividual> getNode(ElkClass elkClass) {
		return functor_.apply(classTaxonomy_.getNode(elkClass));
	}

	/**
	 * Obtain a {@link TypeNode} object for a given {@link ElkClass}, or
	 * {@code null} if none assigned.
	 * 
	 * @param individual
	 * @return instance node object for elkClass, possibly still incomplete
	 */
	@Override
	public IndividualNode.Projection<ElkClass, ElkNamedIndividual> getInstanceNode(
			final ElkNamedIndividual individual) {
		return individualNodeStore_.getNode(individual);
	}

	@Override
	public Set<? extends TypeNode<ElkClass, ElkNamedIndividual>> getNodes() {
		return Operations.map(classTaxonomy_.getNodes(), functor_);
	}

	@Override
	public Set<? extends InstanceNode<ElkClass, ElkNamedIndividual>> getInstanceNodes() {
		return individualNodeStore_.getNodes();
	}

	@Override
	public NonBottomTypeNode<ElkClass, ElkNamedIndividual> getNonBottomNode(
			final ElkClass elkEntity) {
		return getCreateUpdateableTypeNode(
				classTaxonomy_.getNonBottomNode(elkEntity));
	}

	@Override
	public Set<? extends NonBottomTypeNode<ElkClass, ElkNamedIndividual>> getNonBottomNodes() {
		return Operations.map(classTaxonomy_.getNonBottomNodes(),
				nonBottomFunctor_);
	}

	/**
	 * Node factory creating instance nodes of this taxonomy.
	 */
	private final NodeFactory<ElkNamedIndividual, IndividualNode.Projection<ElkClass, ElkNamedIndividual>> INSTANCE_NODE_FACTORY = new NodeFactory<ElkNamedIndividual, IndividualNode.Projection<ElkClass, ElkNamedIndividual>>() {

		@Override
		public IndividualNode.Projection<ElkClass, ElkNamedIndividual> createNode(
				final Iterable<? extends ElkNamedIndividual> members,
				final int size) {
			return new IndividualNode.Projection<ElkClass, ElkNamedIndividual>(
					ConcurrentInstanceTaxonomy.this, members, size);
		}

	};

	@Override
	public InstanceNode<ElkClass, ElkNamedIndividual> getCreateInstanceNode(
			final Collection<? extends ElkNamedIndividual> instances) {
		return individualNodeStore_.getCreateNode(instances, instances.size(),
				INSTANCE_NODE_FACTORY);
	};

	@Override
	public boolean setCreateDirectTypes(
			final InstanceNode<ElkClass, ElkNamedIndividual> instanceNode,
			final Iterable<? extends java.util.Collection<? extends ElkClass>> typeSets) {

		if (!(instanceNode instanceof IndividualNode)) {
			throw new IllegalArgumentException(
					"The sub-node must belong to this taxonomy: "
							+ instanceNode);
		}
		final IndividualNode.Projection<ElkClass, ElkNamedIndividual> node = (IndividualNode.Projection<ElkClass, ElkNamedIndividual>) instanceNode;
		if (node.getTaxonomy() != this) {
			throw new IllegalArgumentException(
					"The sub-node must belong to this taxonomy: "
							+ instanceNode);
		}

		// TODO: establish consistency by adding default type to the nodes.

		boolean isTypeSets = true;

		for (final Collection<? extends ElkClass> superMembers : typeSets) {
			final UpdateableTypeNode.Projection<ElkClass, ElkNamedIndividual> superNode = getCreateUpdateableTypeNode(
					classTaxonomy_.getCreateNode(superMembers));
			isTypeSets = false;
			addDirectType(superNode, node);
		}

		if (node.trySetAllParentsAssigned(true)) {
			if (!isTypeSets) {
				fireDirectTypeAssignment(instanceNode,
						instanceNode.getDirectTypeNodes());
			}
			return true;
		} else {
			return false;
		}
	};

	private static void addDirectType(
			final UpdateableTypeNode.Projection<ElkClass, ElkNamedIndividual> typeNode,
			final UpdateableInstanceNode.Projection<ElkClass, ElkNamedIndividual> instanceNode) {
		instanceNode.addDirectTypeNode(typeNode);
		typeNode.addDirectInstanceNode(instanceNode);
	}

	@Override
	public boolean removeDirectTypes(
			final InstanceNode<ElkClass, ElkNamedIndividual> instanceNode) {

		if (!(instanceNode instanceof IndividualNode)) {
			throw new IllegalArgumentException(
					"The sub-node must belong to this taxonomy: "
							+ instanceNode);
		}
		final IndividualNode.Projection<ElkClass, ElkNamedIndividual> node = (IndividualNode.Projection<ElkClass, ElkNamedIndividual>) instanceNode;
		if (node.getTaxonomy() != this) {
			throw new IllegalArgumentException(
					"The sub-node must belong to this taxonomy: "
							+ instanceNode);
		}

		if (!node.trySetAllParentsAssigned(false)) {
			return false;
		}

		final List<UpdateableTypeNode.Projection<ElkClass, ElkNamedIndividual>> directTypes = new ArrayList<UpdateableTypeNode.Projection<ElkClass, ElkNamedIndividual>>();

		synchronized (node) {
			directTypes.addAll(node.getDirectNonBottomTypeNodes());
			for (UpdateableTypeNode.Projection<ElkClass, ElkNamedIndividual> typeNode : directTypes) {
				node.removeDirectTypeNode(typeNode);
			}
		}
		// detaching the removed instance node from all its direct types
		for (UpdateableTypeNode.Projection<ElkClass, ElkNamedIndividual> typeNode : directTypes) {
			synchronized (typeNode) {
				typeNode.removeDirectInstanceNode(node);
			}
		}

		fireDirectTypeRemoval(instanceNode, directTypes);

		return true;
	}

	@Override
	public boolean removeInstanceNode(final ElkNamedIndividual instance) {
		if (individualNodeStore_.removeNode(instance)) {
			LOGGER_.trace("removed instance node with member: {}", instance);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public NonBottomTypeNode<ElkClass, ElkNamedIndividual> getCreateNode(
			final Collection<? extends ElkClass> members) {
		return getCreateUpdateableTypeNode(
				classTaxonomy_.getCreateNode(members));
	}

	@Override
	public boolean setCreateDirectSupernodes(
			final NonBottomTaxonomyNode<ElkClass> subNode,
			final Iterable<? extends Collection<? extends ElkClass>> superMemberSets) {
		if (!(subNode instanceof UpdateableTypeNodeWrapper)) {
			throw new IllegalArgumentException(
					"The sub-node must belong to this taxonomy: " + subNode);
		}
		final UpdateableTypeNodeWrapper node = (UpdateableTypeNodeWrapper) subNode;
		return classTaxonomy_.setCreateDirectSupernodes(node.getNode(),
				superMemberSets);
	}

	@Override
	public TypeNode<ElkClass, ElkNamedIndividual> getTopNode() {
		return functor_.apply(classTaxonomy_.getTopNode());
	}

	@Override
	public TypeNode<ElkClass, ElkNamedIndividual> getBottomNode() {
		return bottomNodeWrapper_;
	}

	@Override
	public boolean removeDirectSupernodes(
			final NonBottomTaxonomyNode<ElkClass> subNode) {
		if (!(subNode instanceof UpdateableTypeNodeWrapper)) {
			throw new IllegalArgumentException(
					"The sub-node must belong to this taxonomy: " + subNode);
		}
		final UpdateableTypeNodeWrapper node = (UpdateableTypeNodeWrapper) subNode;
		return classTaxonomy_.removeDirectSupernodes(node.getNode());
	}

	@Override
	public boolean removeNode(final ElkClass member) {
		final TaxonomyNode<ElkClass> node = classTaxonomy_.getNode(member);
		if (node == null) {
			return false;
		}
		UpdateableTypeNodeWrapper wrapper = wrapperMap_.get(node);

		if (wrapper != null && wrapperMap_.remove(node, wrapper)) {

			// TODO: maybe this can be removed
			for (UpdateableInstanceNode.Projection<ElkClass, ElkNamedIndividual> instanceNode : wrapper
					.getDirectInstanceNodes()) {
				synchronized (instanceNode) {
					instanceNode.removeDirectTypeNode(wrapper);
				}
			}
		}

		return classTaxonomy_.removeNode(member);
	}

	@Override
	public boolean addToBottomNode(final ElkClass member) {
		return classTaxonomy_.addToBottomNode(member);
	}

	@Override
	public boolean removeFromBottomNode(final ElkClass member) {
		return classTaxonomy_.removeFromBottomNode(member);
	}

	private UpdateableTypeNodeWrapper getCreateUpdateableTypeNode(
			NonBottomTaxonomyNode<ElkClass> taxNode) {
		if (taxNode == null) {
			return null;
		}

		synchronized (taxNode) {
			UpdateableTypeNodeWrapper wrapper = wrapperMap_.get(taxNode);

			if (wrapper == null) {
				wrapper = new UpdateableTypeNodeWrapper(taxNode);
				wrapperMap_.put(taxNode, wrapper);
			}

			return wrapper;
		}
	}

	/**
	 * Transforms updateable taxonomy nodes into updateable type nodes
	 */
	private final FunctorEx<NonBottomTaxonomyNode<ElkClass>, UpdateableTypeNodeWrapper> nonBottomFunctor_ = new FunctorEx<NonBottomTaxonomyNode<ElkClass>, UpdateableTypeNodeWrapper>() {

		@Override
		public UpdateableTypeNodeWrapper apply(
				final NonBottomTaxonomyNode<ElkClass> node) {
			return getCreateUpdateableTypeNode(node);
		}

		@Override
		public NonBottomTaxonomyNode<ElkClass> deapply(final Object element) {
			if (element instanceof UpdateableTypeNodeWrapper) {
				return ((UpdateableTypeNodeWrapper) element).getNode();
			} else {
				return null;
			}
		}

	};

	private final FunctorEx<TaxonomyNode<ElkClass>, TypeNodeWrapper> functor_ = new FunctorEx<TaxonomyNode<ElkClass>, TypeNodeWrapper>() {

		@Override
		public TypeNodeWrapper apply(final TaxonomyNode<ElkClass> node) {
			if (node == null) {
				return null;
			} else if (node instanceof NonBottomTaxonomyNode) {
				return nonBottomFunctor_
						.apply((NonBottomTaxonomyNode<ElkClass>) node);
			} else {
				return bottomNodeWrapper_;
			}
		}

		@Override
		public TaxonomyNode<ElkClass> deapply(Object element) {
			if (element instanceof TypeNodeWrapper) {
				return ((TypeNodeWrapper) element).getNode();
			} else {
				return null;
			}
		}

	};

	/**
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 * @author Peter Skocovsky
	 */
	private abstract class TypeNodeWrapper implements
			GenericTypeNode.Projection<ElkClass, ElkNamedIndividual> {

		public abstract TaxonomyNode<ElkClass> getNode();

		@Override
		public ComparatorKeyProvider<? super ElkClass> getKeyProvider() {
			return getNode().getKeyProvider();
		}

		@Override
		public boolean contains(ElkClass member) {
			return getNode().contains(member);
		}

		@Override
		public int size() {
			return getNode().size();
		}

		@Override
		public ElkClass getCanonicalMember() {
			return getNode().getCanonicalMember();
		}

		@Override
		public Iterator<ElkClass> iterator() {
			return getNode().iterator();
		}

		@Override
		public Taxonomy<ElkClass> getTaxonomy() {
			return getNode().getTaxonomy();
		}

		@Override
		public Set<TypeNodeWrapper> getDirectSuperNodes() {
			return Operations.map(getNode().getDirectSuperNodes(), functor_);
		}

		@Override
		public Set<TypeNodeWrapper> getAllSuperNodes() {
			return Operations.map(getNode().getAllSuperNodes(), functor_);
		}

		@Override
		public Set<TypeNodeWrapper> getDirectSubNodes() {
			return Operations.map(getNode().getDirectSubNodes(), functor_);
		}

		@Override
		public Set<TypeNodeWrapper> getAllSubNodes() {
			return Operations.map(getNode().getAllSubNodes(), functor_);
		}

	}

	private final TypeNodeWrapper bottomNodeWrapper_ = new TypeNodeWrapper() {

		@Override
		public TaxonomyNode<ElkClass> getNode() {
			return classTaxonomy_.getBottomNode();
		}

		@Override
		public Set<? extends GenericInstanceNode.Projection<ElkClass, ElkNamedIndividual>> getDirectInstanceNodes() {
			return Collections.emptySet();
		}

		@Override
		public Set<? extends GenericInstanceNode.Projection<ElkClass, ElkNamedIndividual>> getAllInstanceNodes() {
			return Collections.emptySet();
		}

	};

	/**
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 * @author Peter Skocovsky
	 */
	private class UpdateableTypeNodeWrapper extends TypeNodeWrapper implements
			UpdateableTypeNode.Projection<ElkClass, ElkNamedIndividual> {

		/**
		 * The wrapped node.
		 */
		protected final NonBottomTaxonomyNode<ElkClass> classNode_;

		/**
		 * ElkNamedIndividual nodes whose members are instances of the members
		 * of this node.
		 */
		private final Set<UpdateableInstanceNode.Projection<ElkClass, ElkNamedIndividual>> directInstanceNodes_;

		UpdateableTypeNodeWrapper(final NonBottomTaxonomyNode<ElkClass> node) {
			this.classNode_ = node;
			this.directInstanceNodes_ = Collections.newSetFromMap(
					new ConcurrentHashMap<UpdateableInstanceNode.Projection<ElkClass, ElkNamedIndividual>, Boolean>());
		}

		public NonBottomTaxonomyNode<ElkClass> getNode() {
			return classNode_;
		}

		@Override
		public Set<? extends UpdateableInstanceNode.Projection<ElkClass, ElkNamedIndividual>> getDirectInstanceNodes() {
			return Collections.unmodifiableSet(directInstanceNodes_);
		}

		@Override
		public Set<? extends GenericInstanceNode.Projection<ElkClass, ElkNamedIndividual>> getAllInstanceNodes() {
			return TaxonomyNodeUtils.getAllInstanceNodes(this);
		}

		@Override
		public Set<? extends UpdateableTypeNode.Projection<ElkClass, ElkNamedIndividual>> getDirectNonBottomSuperNodes() {
			return Operations.map(getNode().getDirectNonBottomSuperNodes(),
					nonBottomFunctor_);
		}

		@Override
		public Set<? extends UpdateableTypeNode.Projection<ElkClass, ElkNamedIndividual>> getDirectNonBottomSubNodes() {
			return Operations.map(getNode().getDirectNonBottomSubNodes(),
					nonBottomFunctor_);
		}

		@Override
		public void addDirectInstanceNode(
				UpdateableInstanceNode.Projection<ElkClass, ElkNamedIndividual> instanceNode) {
			LOGGER_.trace("{}: new direct instance-node {}", classNode_,
					instanceNode);

			directInstanceNodes_.add(instanceNode);
		}

		/*
		 * This method is not thread safe
		 */
		@Override
		public void removeDirectInstanceNode(
				UpdateableInstanceNode.Projection<ElkClass, ElkNamedIndividual> instanceNode) {
			LOGGER_.trace("{}: direct instance node removed {}", classNode_,
					instanceNode);

			directInstanceNodes_.remove(instanceNode);
		}

		@Override
		public String toString() {
			return classNode_.toString();
		}

		@Override
		public int hashCode() {
			return classNode_.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof UpdateableTypeNodeWrapper) {
				return classNode_ == ((UpdateableTypeNodeWrapper) obj).classNode_;
			}

			return false;
		}

	}

	@Override
	public boolean addListener(final Taxonomy.Listener<ElkClass> listener) {
		return classTaxonomy_.addListener(listener);
	}

	@Override
	public boolean removeListener(final Taxonomy.Listener<ElkClass> listener) {
		return classTaxonomy_.removeListener(listener);
	}

	@Override
	public boolean addListener(final NodeStore.Listener<ElkClass> listener) {
		return classTaxonomy_.addListener(listener);
	}

	@Override
	public boolean removeListener(final NodeStore.Listener<ElkClass> listener) {
		return classTaxonomy_.removeListener(listener);
	}

	@Override
	public boolean addInstanceListener(
			final NodeStore.Listener<ElkNamedIndividual> listener) {
		return individualNodeStore_.addListener(listener);
	}

	@Override
	public boolean removeInstanceListener(
			final NodeStore.Listener<ElkNamedIndividual> listener) {
		return individualNodeStore_.removeListener(listener);
	}

	@Override
	public boolean addInstanceListener(
			final InstanceTaxonomy.Listener<ElkClass, ElkNamedIndividual> listener) {
		return instanceListeners_.add(listener);
	}

	@Override
	public boolean removeInstanceListener(
			final InstanceTaxonomy.Listener<ElkClass, ElkNamedIndividual> listener) {
		return instanceListeners_.remove(listener);
	}

	protected void fireDirectTypeAssignment(
			final InstanceNode<ElkClass, ElkNamedIndividual> instanceNode,
			final Collection<? extends TypeNode<ElkClass, ElkNamedIndividual>> typeNodes) {
		for (final InstanceTaxonomy.Listener<ElkClass, ElkNamedIndividual> listener : instanceListeners_) {
			listener.directTypeAssignment(instanceNode, typeNodes);
		}
	}

	protected void fireDirectTypeRemoval(
			final InstanceNode<ElkClass, ElkNamedIndividual> instanceNode,
			final Collection<? extends TypeNode<ElkClass, ElkNamedIndividual>> typeNodes) {
		for (final InstanceTaxonomy.Listener<ElkClass, ElkNamedIndividual> listener : instanceListeners_) {
			listener.directTypeRemoval(instanceNode, typeNodes);
		}
	}

}
