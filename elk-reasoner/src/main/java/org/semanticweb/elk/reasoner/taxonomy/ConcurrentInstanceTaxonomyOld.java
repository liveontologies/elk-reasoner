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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.iris.ElkIri;
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericInstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTypeNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.IndividualNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.TaxonomyNodeUtils;
import org.semanticweb.elk.reasoner.taxonomy.nodes.UpdateableBottomNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.UpdateableGenericTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.UpdateableInstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.UpdateableTypeNode;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.collections.LazySetUnion;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.collections.Operations.FunctorEx;

/**
 * Class taxonomy that is suitable for concurrent processing. Taxonomy objects
 * are only constructed for consistent ontologies, and some consequences of this
 * are hardcoded here.
 * 
 * This class wraps an instance of {@link UpdateableTaxonomy} and lazily
 * generates wrappers for its nodes to store direct instances.
 * 
 * @author Yevgeny Kazakov
 * @author Frantisek Simancik
 * @author Markus Kroetzsch
 * @author Pavel Klinov
 */
public class ConcurrentInstanceTaxonomyOld implements IndividualClassTaxonomy {

	// logger for events
	private static final Logger LOGGER_ = Logger
			.getLogger(ConcurrentInstanceTaxonomyOld.class);

	/** thread safe map from class IRIs to individual nodes */
	private final ConcurrentMap<ElkIri, IndividualNode> individualNodeLookup_;
	/** thread safe set of all individual nodes */
	private final Set<GenericInstanceNode<ElkClass, ElkNamedIndividual>> allIndividualNodes_;

	private final UpdateableTaxonomy<ElkClass> classTaxonomy_;

	private final ConcurrentMap<GenericTaxonomyNode<ElkClass>, UpdateableTypeNodeWrapper> wrapperMap_;

	private final TypeNodeWrapper bottom_;

	public ConcurrentInstanceTaxonomyOld() {
		this(new ConcurrentClassTaxonomy());
	}

	public ConcurrentInstanceTaxonomyOld(UpdateableTaxonomy<ElkClass> classTaxonomy) {
		this.classTaxonomy_ = classTaxonomy;
		this.individualNodeLookup_ = new ConcurrentHashMap<ElkIri, IndividualNode>();
		this.allIndividualNodes_ = Collections
				.newSetFromMap(new ConcurrentHashMap<GenericInstanceNode<ElkClass, ElkNamedIndividual>, Boolean>());
		this.wrapperMap_ = new ConcurrentHashMap<GenericTaxonomyNode<ElkClass>, UpdateableTypeNodeWrapper>();
		this.bottom_ = new BottomTypeNodeWrapper(
				classTaxonomy_.getCreateBottomNode());
	}

	/**
	 * Returns the IRI of the given ELK entity.
	 * 
	 * @return the IRI of the given ELK entity
	 */
	static ElkIri getKey(ElkEntity elkEntity) {
		return elkEntity.getIri();
	}

	@Override
	public GenericTypeNode<ElkClass, ElkNamedIndividual> getNode(ElkClass elkClass) {
		GenericTaxonomyNode<ElkClass> node = classTaxonomy_.getNode(elkClass);

		if (node == classTaxonomy_.getBottomNode()) {
			return bottom_;
		} else {
			UpdateableGenericTaxonomyNode<ElkClass> taxNode = classTaxonomy_
					.getUpdateableNode(elkClass);

			return getCreateUpdateableTypeNode(taxNode);
		}
	}

	@Override
	public UpdateableInstanceNode<ElkClass, ElkNamedIndividual> getInstanceNode(
			ElkNamedIndividual individual) {
		return individualNodeLookup_.get(getKey(individual));
	}

	@Override
	public Set<? extends GenericTypeNode<ElkClass, ElkNamedIndividual>> getTypeNodes() {
		Set<? extends GenericTypeNode<ElkClass, ElkNamedIndividual>> updateableNodes = Operations
				.map(classTaxonomy_.getUpdateableNodes(), functor_);

		return new LazySetUnion<GenericTypeNode<ElkClass, ElkNamedIndividual>>(
				updateableNodes, Collections.singleton(bottom_));
	}

	@Override
	public Set<? extends GenericInstanceNode<ElkClass, ElkNamedIndividual>> getInstanceNodes() {
		return Collections.unmodifiableSet(allIndividualNodes_);
	}

	@Override
	public Set<? extends GenericTaxonomyNode<ElkClass>> getNodes() {
		return classTaxonomy_.getNodes();
	}

	@Override
	public IndividualNode getCreateInstanceNode(
			Collection<ElkNamedIndividual> members) {
		// search if some node is already assigned to some member, and if so
		// use this node and update its members if necessary
		IndividualNode previous = null;

		for (ElkNamedIndividual member : members) {
			previous = individualNodeLookup_.get(getKey(member));
			if (previous == null)
				continue;
			synchronized (previous) {
				if (previous.getMembersLookup().size() < members.size())
					previous.setMembers(members);
				else
					return previous;
			}
			// updating the index
			for (ElkNamedIndividual newMember : members) {
				individualNodeLookup_.put(getKey(newMember), previous);
			}

			return previous;
		}

		// TODO: avoid code duplication, the same technique is used for creating
		// non-bottom class nodes!

		IndividualNode node = new IndividualNode(members);
		// we first assign the node to the canonical member to avoid
		// concurrency problems
		ElkNamedIndividual canonical = node.getCanonicalMember();

		previous = individualNodeLookup_.putIfAbsent(getKey(canonical), node);

		if (previous != null) {
			return previous;
		}

		allIndividualNodes_.add(node);

		if (LOGGER_.isTraceEnabled()) {
			LOGGER_.trace(OwlFunctionalStylePrinter.toString(canonical)
					+ ": node created");
		}

		for (ElkNamedIndividual member : members) {
			if (member != canonical)
				individualNodeLookup_.put(getKey(member), node);
		}

		return node;
	}

	@Override
	public boolean removeInstanceNode(ElkNamedIndividual instance) {
		IndividualNode node = individualNodeLookup_.get(getKey(instance));

		if (node != null) {
			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace("Removing the instance node " + node);
			}

			List<UpdateableTypeNode<ElkClass, ElkNamedIndividual>> directTypes = new LinkedList<UpdateableTypeNode<ElkClass, ElkNamedIndividual>>();

			synchronized (node) {
				for (ElkNamedIndividual individual : node.getMembersLookup()) {
					individualNodeLookup_.remove(getKey(individual));
				}

				allIndividualNodes_.remove(node);
				directTypes.addAll(node.getDirectTypeNodes());
			}
			// detaching the removed instance node from all its direct types
			for (UpdateableTypeNode<ElkClass, ElkNamedIndividual> typeNode : directTypes) {
				synchronized (typeNode) {
					typeNode.removeDirectInstanceNode(bottomNode);
				}
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public UpdateableTypeNode<ElkClass, ElkNamedIndividual> getUpdateableTypeNode(
			ElkClass elkClass) {
		return getCreateUpdateableTypeNode(classTaxonomy_
				.getUpdateableNode(elkClass));
	}

	@Override
	public UpdateableTypeNode<ElkClass, ElkNamedIndividual> getCreateTypeNode(
			Collection<ElkClass> members) {
		UpdateableGenericTaxonomyNode<ElkClass> taxNode = classTaxonomy_
				.getCreateNode(members);

		return getCreateUpdateableTypeNode(taxNode);
	}

	@Override
	public UpdateableTypeNode<ElkClass, ElkNamedIndividual> getTopNode() {
		return getUpdateableTopNode();
	}

	@Override
	public UpdateableTypeNode<ElkClass, ElkNamedIndividual> getUpdateableTopNode() {
		return getCreateUpdateableTypeNode(classTaxonomy_
				.getUpdateableTopNode());
	}

	@Override
	public GenericTypeNode<ElkClass, ElkNamedIndividual> getBottomNode() {
		return bottom_;
	}

	@Override
	public UpdateableBottomNode<ElkClass> getUpdateableBottomNode() {
		return classTaxonomy_.getCreateBottomNode();
	}

	@Override
	public UpdateableGenericTaxonomyNode<ElkClass> getCreateNode(
			Collection<ElkClass> members) {
		return classTaxonomy_.getCreateNode(members);
	}

	@Override
	public boolean addBottomMember(ElkClass member) {
		return classTaxonomy_.addBottomMember(member);
	}

	@Override
	public boolean removeNode(UpdateableGenericTaxonomyNode<ElkClass> node) {
		UpdateableTypeNodeWrapper wrapper = wrapperMap_.get(node);

		if (wrapper != null && wrapperMap_.remove(node, wrapper)) {

			for (UpdateableInstanceNode<ElkClass, ElkNamedIndividual> instanceNode : wrapper
					.getDirectInstanceNodes()) {
				synchronized (instanceNode) {
					instanceNode.removeDirectTypeNode(wrapper);
				}
			}
		}

		return classTaxonomy_.removeNode(node);
	}

	@Override
	public UpdateableGenericTaxonomyNode<ElkClass> getUpdateableNode(ElkClass elkObject) {
		return classTaxonomy_.getUpdateableNode(elkObject);
	}

	@Override
	public Set<? extends UpdateableGenericTaxonomyNode<ElkClass>> getUpdateableNodes() {
		return classTaxonomy_.getUpdateableNodes();
	}

	private UpdateableTypeNodeWrapper getCreateUpdateableTypeNode(
			UpdateableGenericTaxonomyNode<ElkClass> taxNode) {
		if (taxNode == null) {
			return null;
		}

		synchronized (taxNode) {
			NonBottomTypeNodeWrapper wrapper = wrapperMap_.get(taxNode);

			if (wrapper == null) {
				wrapper = new NonBottomTypeNodeWrapper(taxNode);
				wrapperMap_.put(taxNode, wrapper);
			}

			return wrapper;
		}
	}

	/**
	 * Transforms updateable taxonomy nodes into updateable type nodes
	 */
	private final FunctorEx<UpdateableGenericTaxonomyNode<ElkClass>, UpdateableTypeNodeWrapper> functor_ = new FunctorEx<UpdateableGenericTaxonomyNode<ElkClass>, UpdateableTypeNodeWrapper>() {

		@Override
		public UpdateableTypeNodeWrapper apply(
				UpdateableGenericTaxonomyNode<ElkClass> node) {
			return getCreateUpdateableTypeNode(bottomNode);
		}

		@Override
		public UpdateableGenericTaxonomyNode<ElkClass> reverse(Object element) {
			if (element instanceof UpdateableTypeNodeWrapper) {
				return ((UpdateableTypeNodeWrapper) element).getNode();
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
	 */
	private abstract class TypeNodeWrapper implements
			GenericTypeNode<ElkClass, ElkNamedIndividual> {

		protected final GenericTaxonomyNode<ElkClass> classNode_;

		TypeNodeWrapper(GenericTaxonomyNode<ElkClass> node) {
			classNode_ = node;
		}

		@Override
		public Set<ElkClass> getMembersLookup() {
			return classNode_.getMembersLookup();
		}

		@Override
		public ElkClass getCanonicalMember() {
			return classNode_.getCanonicalMember();
		}

		@Override
		public Set<? extends GenericInstanceNode<ElkClass, ElkNamedIndividual>> getDirectInstanceNodes() {
			return Collections.emptySet();
		}

		@Override
		public Set<? extends GenericInstanceNode<ElkClass, ElkNamedIndividual>> getAllInstanceNodes() {
			Set<GenericInstanceNode<ElkClass, ElkNamedIndividual>> result;

			if (!classNode_.getDirectSubNodes().isEmpty()) {
				result = new ArrayHashSet<GenericInstanceNode<ElkClass, ElkNamedIndividual>>();
				Queue<GenericTypeNode<ElkClass, ElkNamedIndividual>> todo = new LinkedList<GenericTypeNode<ElkClass, ElkNamedIndividual>>();

				todo.add(this);

				while (!todo.isEmpty()) {
					GenericTypeNode<ElkClass, ElkNamedIndividual> next = todo.poll();
					result.addAll(next.getDirectInstanceNodes());

					for (GenericTypeNode<ElkClass, ElkNamedIndividual> nextSubNode : next
							.getDirectSubNodes()) {
						todo.add(nextSubNode);
					}
				}

				return Collections.unmodifiableSet(result);

			} else {
				return Collections.unmodifiableSet(getDirectInstanceNodes());
			}
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
			if (obj instanceof TypeNodeWrapper) {
				return classNode_ == ((TypeNodeWrapper) obj).classNode_;
			}

			return false;
		}

	}

	/**
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	private class UpdateableTypeNodeWrapper extends TypeNodeWrapper implements
			UpdateableTypeNode<ElkClass, ElkNamedIndividual> {

		/**
		 * ElkNamedIndividual nodes whose members are instances of the members
		 * of this node.
		 */
		private final Set<UpdateableInstanceNode<ElkClass, ElkNamedIndividual>> directInstanceNodes_;

		UpdateableTypeNodeWrapper(UpdateableGenericTaxonomyNode<ElkClass> node) {
			super(node);
			this.directInstanceNodes_ = Collections
					.newSetFromMap(new ConcurrentHashMap<UpdateableInstanceNode<ElkClass, ElkNamedIndividual>, Boolean>());
		}

		private UpdateableGenericTaxonomyNode<ElkClass> getNode() {
			return (UpdateableGenericTaxonomyNode<ElkClass>) classNode_;
		}

		@Override
		public Set<? extends UpdateableInstanceNode<ElkClass, ElkNamedIndividual>> getDirectInstanceNodes() {
			return Collections.unmodifiableSet(directInstanceNodes_);
		}

		@Override
		public void addDirectSuperNode(
				UpdateableGenericTaxonomyNode<ElkClass> superNode) {
			getNode().addDirectSuperNode(superNode);
		}

		@Override
		public void addDirectSubNode(UpdateableGenericTaxonomyNode<ElkClass> subNode) {
			getNode().addDirectSubNode(subNode);
		}

		@Override
		public boolean removeDirectSubNode(
				UpdateableGenericTaxonomyNode<ElkClass> subNode) {
			return getNode().removeDirectSubNode(subNode);
		}

		@Override
		public boolean removeDirectSuperNode(
				UpdateableGenericTaxonomyNode<ElkClass> superNode) {
			return getNode().removeDirectSuperNode(superNode);
		}

		@Override
		public void clearMembers() {
			getNode().clearMembers();
		}

		@Override
		public boolean trySetModified(boolean modified) {
			return getNode().trySetModified(modified);
		}

		@Override
		public boolean isModified() {
			return getNode().isModified();
		}

		@Override
		public Set<UpdateableTypeNodeWrapper> getDirectUpdateableSubNodes() {
			return Operations.map(getNode().getDirectUpdateableSubNodes(),
					functor_);
		}

		@Override
		public Set<UpdateableTypeNodeWrapper> getDirectUpdateableSuperNodes() {
			return Operations.map(getNode().getDirectUpdateableSuperNodes(),
					functor_);
		}

		@Override
		public Set<? extends TypeNodeWrapper> getDirectSuperNodes() {
			return getDirectUpdateableSuperNodes();
		}

		@Override
		public Set<? extends GenericTypeNode<ElkClass, ElkNamedIndividual>> getAllSuperNodes() {
			return getDirectUpdateableSuperNodes();

		}

		@Override
		public Set<? extends GenericTypeNode<ElkClass, ElkNamedIndividual>> getDirectSubNodes() {
			Set<? extends GenericTypeNode<ElkClass, ElkNamedIndividual>> directSubNodes = getDirectUpdateableSubNodes();

			return directSubNodes.isEmpty() ? Collections
					.singleton(getBottomNode()) : directSubNodes;
		}

		@Override
		public Set<? extends GenericTypeNode<ElkClass, ElkNamedIndividual>> getAllSubNodes() {
			Set<? extends UpdateableGenericTaxonomyNode<ElkClass>> subNodes = TaxonomyNodeUtils
					.getAllUpdateableSubNodes(getNode());
			// this node is not the bottom one, so the bottom must be in the set
			return new LazySetUnion<GenericTypeNode<ElkClass, ElkNamedIndividual>>(
					Operations.map(subNodes, functor_),
					Collections.singleton(getBottomNode()));
		}

		@Override
		public void addDirectInstanceNode(
				UpdateableInstanceNode<ElkClass, ElkNamedIndividual> instanceNode) {
			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace(getNode() + ": new direct instance-node "
						+ instanceNode);
			}

			directInstanceNodes_.add(instanceNode);
		}

		/*
		 * This method is not thread safe
		 */
		@Override
		public void removeDirectInstanceNode(
				UpdateableInstanceNode<ElkClass, ElkNamedIndividual> instanceNode) {
			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace(getNode() + ": direct instance node removed "
						+ instanceNode);
			}

			directInstanceNodes_.remove(instanceNode);
		}

	}

	/**
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	private class BottomTypeNodeWrapper extends TypeNodeWrapper {

		BottomTypeNodeWrapper(UpdateableBottomNode<ElkClass> node) {
			super(node);
		}

		private UpdateableBottomNode<ElkClass> getNode() {
			return (UpdateableBottomNode<ElkClass>) classNode_;
		}

		@Override
		public Set<? extends GenericTypeNode<ElkClass, ElkNamedIndividual>> getDirectSuperNodes() {
			return Operations.map(getNode().getDirectUpdateableSuperNodes(),
					functor_);
		}

		@Override
		public Set<? extends GenericTypeNode<ElkClass, ElkNamedIndividual>> getAllSuperNodes() {
			return Operations.map(
					TaxonomyNodeUtils.getAllUpdateableSuperNodes(getNode()),
					functor_);
		}

		@Override
		public Set<? extends GenericTypeNode<ElkClass, ElkNamedIndividual>> getDirectSubNodes() {
			return Collections.emptySet();
		}

		@Override
		public Set<? extends GenericTypeNode<ElkClass, ElkNamedIndividual>> getAllSubNodes() {
			return Collections.emptySet();
		}

	}

}
