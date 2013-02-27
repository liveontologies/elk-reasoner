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
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNodeUtils;
import org.semanticweb.elk.reasoner.taxonomy.model.TypeNode;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableBottomNode;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableInstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableTypeNode;
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
public class ConcurrentInstanceTaxonomy implements IndividualClassTaxonomy {

	// logger for events
	private static final Logger LOGGER_ = Logger
			.getLogger(ConcurrentInstanceTaxonomy.class);

	/** thread safe map from class IRIs to individual nodes */
	private final ConcurrentMap<ElkIri, IndividualNode> individualNodeLookup_;
	/** thread safe set of all individual nodes */
	private final Set<InstanceNode<ElkClass, ElkNamedIndividual>> allIndividualNodes_;
	
	private final UpdateableTaxonomy<ElkClass> classTaxonomy_;
	
	private final ConcurrentMap<TaxonomyNode<ElkClass>, UpdateableTypeNodeWrapper> wrapperMap_;
	
	private final TypeNodeWrapper bottom_;

	public ConcurrentInstanceTaxonomy() {
		this(new ConcurrentClassTaxonomy());
	}
	
	public ConcurrentInstanceTaxonomy(UpdateableTaxonomy<ElkClass> classTaxonomy) {
		this.classTaxonomy_ = classTaxonomy;
		this.individualNodeLookup_ = new ConcurrentHashMap<ElkIri, IndividualNode>();
		this.allIndividualNodes_ = Collections
				.newSetFromMap(new ConcurrentHashMap<InstanceNode<ElkClass, ElkNamedIndividual>, Boolean>());
		this.wrapperMap_ = new ConcurrentHashMap<TaxonomyNode<ElkClass>, UpdateableTypeNodeWrapper>();
		this.bottom_ = new BottomTypeNodeWrapper(classTaxonomy_.getUpdateableBottomNode());
	}

	/**
	 * Returns the IRI of the given ELK entity.
	 * 
	 * @return the IRI of the given ELK entity
	 */
	static ElkIri getKey(ElkEntity elkEntity) {
		return elkEntity.getIri();
	}

	/**
	 * Obtain a {@link TypeNode} object for a given {@link ElkClass}, or
	 * {@code null} if none assigned.
	 * 
	 * @param elkClass
	 * @return type node object for elkClass, possibly still incomplete
	 */
	@Override
	public TypeNode<ElkClass, ElkNamedIndividual> getTypeNode(ElkClass elkClass) {
		TaxonomyNode<ElkClass> node = classTaxonomy_.getNode(elkClass);
		
		if (node == classTaxonomy_.getBottomNode()) {
			return bottom_;
		}
		else {
			UpdateableTaxonomyNode<ElkClass> taxNode = classTaxonomy_.getUpdateableNode(elkClass);
		
			return getCreateUpdateableTypeNode(taxNode);
		}
	}

	/**
	 * Obtain a {@link TypeNode} object for a given {@link ElkClass}, or
	 * {@code null} if none assigned.
	 * 
	 * @param individual
	 * @return instance node object for elkClass, possibly still incomplete
	 */
	@Override
	public UpdateableInstanceNode<ElkClass, ElkNamedIndividual> getInstanceNode(
			ElkNamedIndividual individual) {
		return individualNodeLookup_.get(getKey(individual));
	}

	@Override
	public TaxonomyNode<ElkClass> getNode(ElkClass elkClass) {
		return getTypeNode(elkClass);
	}

	@Override
	public Set<? extends TypeNode<ElkClass, ElkNamedIndividual>> getTypeNodes() {
		Set<? extends TypeNode<ElkClass, ElkNamedIndividual>> updateableNodes = Operations.map(classTaxonomy_.getUpdateableNodes(), functor_);
		
		return new LazySetUnion<TypeNode<ElkClass,ElkNamedIndividual>>(updateableNodes, Collections.singleton(bottom_));
	}

	@Override
	public Set<? extends InstanceNode<ElkClass, ElkNamedIndividual>> getInstanceNodes() {
		return Collections.unmodifiableSet(allIndividualNodes_);
	}

	@Override
	public Set<? extends TaxonomyNode<ElkClass>> getNodes() {
		return classTaxonomy_.getNodes();
	}


	@Override
	public IndividualNode getCreateInstanceNode(Collection<ElkNamedIndividual> members) {
		// search if some node is already assigned to some member, and if so
		// use this node and update its members if necessary
		IndividualNode previous = null;
		
		for (ElkNamedIndividual member : members) {
			previous = individualNodeLookup_.get(getKey(member));
			if (previous == null)
				continue;
			synchronized (previous) {
				if (previous.getMembers().size() < members.size())
					previous.setMembers(members);
				else
					return previous;
			}
			//updating the index
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
			LOGGER_.trace(OwlFunctionalStylePrinter.toString(canonical)	+ ": node created");
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
				for (ElkNamedIndividual individual : node.getMembers()) {
					individualNodeLookup_.remove(getKey(individual));
				}

				allIndividualNodes_.remove(node);
				directTypes.addAll(node.getDirectTypeNodes());
			}
			// detaching the removed instance node from all its direct types
			for (UpdateableTypeNode<ElkClass, ElkNamedIndividual> typeNode : directTypes) {
				synchronized (typeNode) {
					typeNode.removeDirectInstanceNode(node);
				}
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public UpdateableTypeNode<ElkClass, ElkNamedIndividual> getUpdateableTypeNode(ElkClass elkClass) {
		return getCreateUpdateableTypeNode(classTaxonomy_.getUpdateableNode(elkClass));
	}

	@Override
	public UpdateableTypeNode<ElkClass, ElkNamedIndividual> getCreateTypeNode(Collection<ElkClass> members) {
		UpdateableTaxonomyNode<ElkClass> taxNode = classTaxonomy_.getCreateNode(members);
		
		return getCreateUpdateableTypeNode(taxNode);
	}

	@Override
	public UpdateableTypeNode<ElkClass, ElkNamedIndividual> getTopNode() {
		return getUpdateableTopNode();
	}
	
	@Override
	public UpdateableTypeNode<ElkClass, ElkNamedIndividual> getUpdateableTopNode() {
		return getCreateUpdateableTypeNode(classTaxonomy_.getUpdateableTopNode());
	}

	@Override
	public TypeNode<ElkClass, ElkNamedIndividual> getBottomNode() {
		return bottom_;
	}
	
	@Override
	public UpdateableBottomNode<ElkClass> getUpdateableBottomNode() {
		return classTaxonomy_.getUpdateableBottomNode();
	}


	@Override
	public UpdateableTaxonomyNode<ElkClass> getCreateNode(
			Collection<ElkClass> members) {
		return classTaxonomy_.getCreateNode(members);
	}

	@Override
	public boolean addToBottomNode(ElkClass member) {
		return classTaxonomy_.addToBottomNode(member);
	}

	@Override
	public boolean removeNode(UpdateableTaxonomyNode<ElkClass> node) {
		UpdateableTypeNodeWrapper wrapper = wrapperMap_.get(node);
		
		if (wrapper != null) {
			wrapperMap_.remove(node);
			
			for (UpdateableInstanceNode<ElkClass, ElkNamedIndividual> instanceNode : wrapper.getDirectInstanceNodes()) {
				synchronized(instanceNode) {
					instanceNode.removeDirectTypeNode(wrapper);
				}
			}
		}
		
		return classTaxonomy_.removeNode(node);
	}

	@Override
	public UpdateableTaxonomyNode<ElkClass> getUpdateableNode(ElkClass elkObject) {
		return classTaxonomy_.getUpdateableNode(elkObject);
	}
	
	@Override
	public Set<? extends UpdateableTaxonomyNode<ElkClass>> getUpdateableNodes() {
		return classTaxonomy_.getUpdateableNodes();
	}

	private UpdateableTypeNodeWrapper getCreateUpdateableTypeNode(UpdateableTaxonomyNode<ElkClass> taxNode) {
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
	private final FunctorEx<UpdateableTaxonomyNode<ElkClass>, UpdateableTypeNodeWrapper> functor_ = new FunctorEx<UpdateableTaxonomyNode<ElkClass>, UpdateableTypeNodeWrapper>(){

		@Override
		public UpdateableTypeNodeWrapper apply(UpdateableTaxonomyNode<ElkClass> node) {
			return getCreateUpdateableTypeNode(node);
		}

		@Override
		public UpdateableTaxonomyNode<ElkClass> deapply(Object element) {
			if (element instanceof UpdateableTypeNodeWrapper) {
				return ((UpdateableTypeNodeWrapper) element).getNode();
			}
			else {
				return null;
			}
		}
		
	};	
	
	/**
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	private abstract class TypeNodeWrapper implements TypeNode<ElkClass, ElkNamedIndividual> {
		
		protected final TaxonomyNode<ElkClass> classNode_;
		
		TypeNodeWrapper(TaxonomyNode<ElkClass> node) {
			classNode_ = node;
		}

		@Override
		public Set<ElkClass> getMembers() {
			return classNode_.getMembers();
		}

		@Override
		public ElkClass getCanonicalMember() {
			return classNode_.getCanonicalMember();
		}

		@Override
		public Set<? extends InstanceNode<ElkClass, ElkNamedIndividual>> getDirectInstanceNodes() {
			return Collections.emptySet();
		}

		@Override
		public Set<? extends InstanceNode<ElkClass, ElkNamedIndividual>> getAllInstanceNodes() {
			Set<InstanceNode<ElkClass, ElkNamedIndividual>> result;
			
			if (!classNode_.getDirectSubNodes().isEmpty()) {
				result = new ArrayHashSet<InstanceNode<ElkClass, ElkNamedIndividual>>();
				Queue<TypeNode<ElkClass, ElkNamedIndividual>> todo = new LinkedList<TypeNode<ElkClass, ElkNamedIndividual>>();
				
				todo.add(this);
				
				while (!todo.isEmpty()) {
					TypeNode<ElkClass, ElkNamedIndividual> next = todo.poll();
					result.addAll(next.getDirectInstanceNodes());
					
					for (TypeNode<ElkClass, ElkNamedIndividual> nextSubNode : next
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
			if (obj instanceof TypeNodeWrapper){
				return classNode_ == ((TypeNodeWrapper) obj).classNode_;
			}
			
			return false;
		}
		
	}	
	
	/**
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	private class UpdateableTypeNodeWrapper extends TypeNodeWrapper implements UpdateableTypeNode<ElkClass, ElkNamedIndividual> {
		
		/**
		 * ElkNamedIndividual nodes whose members are instances of the members of
		 * this node.
		 */
		private final Set<UpdateableInstanceNode<ElkClass, ElkNamedIndividual>> directInstanceNodes_;
		
		UpdateableTypeNodeWrapper(UpdateableTaxonomyNode<ElkClass> node) {
			super(node);
			this.directInstanceNodes_ = new ArrayHashSet<UpdateableInstanceNode<ElkClass, ElkNamedIndividual>>();
		}
		
		private UpdateableTaxonomyNode<ElkClass> getNode() {
			return (UpdateableTaxonomyNode<ElkClass>) classNode_;
		}
		
		@Override
		public Set<? extends UpdateableInstanceNode<ElkClass, ElkNamedIndividual>> getDirectInstanceNodes() {
			return Collections.unmodifiableSet(directInstanceNodes_);
		}
		
		@Override
		public void addDirectSuperNode(
				UpdateableTaxonomyNode<ElkClass> superNode) {
			getNode().addDirectSuperNode(superNode);
		}

		@Override
		public void addDirectSubNode(UpdateableTaxonomyNode<ElkClass> subNode) {
			getNode().addDirectSubNode(subNode);			
		}

		@Override
		public boolean removeDirectSubNode(
				UpdateableTaxonomyNode<ElkClass> subNode) {
			return getNode().removeDirectSubNode(subNode);
		}

		@Override
		public boolean removeDirectSuperNode(
				UpdateableTaxonomyNode<ElkClass> superNode) {
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
			return Operations.map(getNode().getDirectUpdateableSubNodes(), functor_);
		}

		@Override
		public Set<UpdateableTypeNodeWrapper> getDirectUpdateableSuperNodes() {
			return Operations.map(getNode().getDirectUpdateableSuperNodes(), functor_);
		}
		
		@Override
		public Set<? extends TypeNodeWrapper> getDirectSuperNodes() {
			return getDirectUpdateableSuperNodes();
		}

		@Override
		public Set<? extends TypeNode<ElkClass, ElkNamedIndividual>> getAllSuperNodes() {
			return getDirectUpdateableSuperNodes();

		}

		@Override
		public Set<? extends TypeNode<ElkClass, ElkNamedIndividual>> getDirectSubNodes() {
			Set<? extends TypeNode<ElkClass, ElkNamedIndividual>> directSubNodes = getDirectUpdateableSubNodes();
			
			return directSubNodes.isEmpty() ? Collections.singleton(getBottomNode()) : directSubNodes;
		}

		@Override
		public Set<? extends TypeNode<ElkClass, ElkNamedIndividual>> getAllSubNodes() {
			Set<? extends UpdateableTaxonomyNode<ElkClass>> subNodes = TaxonomyNodeUtils.getAllUpdateableSubNodes(getNode());
			//this node is not the bottom one, so the bottom must be in the set
			return new LazySetUnion<TypeNode<ElkClass,ElkNamedIndividual>>(Operations.map(subNodes, functor_), Collections.singleton(getBottomNode()));
		}
		

		@Override
		public void addDirectInstanceNode(	UpdateableInstanceNode<ElkClass, ElkNamedIndividual> instanceNode) {
			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace(getNode() + ": new direct instance-node " + instanceNode);
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
				LOGGER_.trace(getNode() + ": direct instance node removed " + instanceNode);
			}
			
			directInstanceNodes_.remove(instanceNode);
		}
		
	}
	
	/**
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	private class BottomTypeNodeWrapper extends TypeNodeWrapper {

		BottomTypeNodeWrapper(UpdateableBottomNode<ElkClass> node) {
			super(node);
		}

		private UpdateableBottomNode<ElkClass> getNode() {
			return (UpdateableBottomNode<ElkClass>) classNode_;
		}
		
		@Override
		public Set<? extends TypeNode<ElkClass, ElkNamedIndividual>> getDirectSuperNodes() {
			return Operations.map(getNode().getDirectUpdateableSuperNodes(), functor_);
		}

		@Override
		public Set<? extends TypeNode<ElkClass, ElkNamedIndividual>> getAllSuperNodes() {
			return Operations.map(TaxonomyNodeUtils.getAllUpdateableSuperNodes(getNode()), functor_);
		}

		@Override
		public Set<? extends TypeNode<ElkClass, ElkNamedIndividual>> getDirectSubNodes() {
			return Collections.emptySet();
		}

		@Override
		public Set<? extends TypeNode<ElkClass, ElkNamedIndividual>> getAllSubNodes() {
			return Collections.emptySet();
		}
		
	}
	
}


