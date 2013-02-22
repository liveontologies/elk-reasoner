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
import org.semanticweb.elk.reasoner.taxonomy.model.TypeNode;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableTypeNode;
import org.semanticweb.elk.util.collections.LazySetUnion;

/**
 * Class taxonomy that is suitable for concurrent processing. Taxonomy objects
 * are only constructed for consistent ontologies, and some consequences of this
 * are hardcoded here.
 * 
 * @author Yevgeny Kazakov
 * @author Frantisek Simancik
 * @author Markus Kroetzsch
 * @author Pavel Klinov
 */
class ConcurrentInstanceTaxonomy implements IndividualClassTaxonomy {

	// logger for events
	private static final Logger LOGGER_ = Logger
			.getLogger(ConcurrentInstanceTaxonomy.class);

	/** thread safe map from class IRIs to individual nodes */
	private final ConcurrentMap<ElkIri, IndividualNode> individualNodeLookup_;
	/** thread safe set of all individual nodes */
	private final Set<InstanceNode<ElkClass, ElkNamedIndividual>> allIndividualNodes_;
	
	private final ConcurrentClassTaxonomy classTaxonomy_;
	
	//private final TypeNode<ElkClass, ElkNamedIndividual> bottomNodeWrapper_;

	ConcurrentInstanceTaxonomy() {
		this(new ConcurrentClassTaxonomy());
	}
	
	ConcurrentInstanceTaxonomy(UpdateableTaxonomy<ElkClass> classTaxonomy) {
		if (classTaxonomy instanceof ConcurrentClassTaxonomy) {
			this.classTaxonomy_ = (ConcurrentClassTaxonomy) classTaxonomy;
		}
		else {
			throw new IllegalArgumentException("Class taxonomy does not support instances, can't proceed");
		}
		
		this.individualNodeLookup_ = new ConcurrentHashMap<ElkIri, IndividualNode>();
		this.allIndividualNodes_ = Collections
				.newSetFromMap(new ConcurrentHashMap<InstanceNode<ElkClass, ElkNamedIndividual>, Boolean>());
		//this.bottomNodeWrapper_ = new BottomTypeNodeWrapper(classTaxonomy_.bottomClassNode);
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
		TypeNode<ElkClass, ElkNamedIndividual> result = classTaxonomy_.getNonBottomNode(elkClass);
		
		if (result == null && classTaxonomy_.unsatisfiableClasses.contains(elkClass)) {
			result = classTaxonomy_.bottomClassNode;
		}
		
		return result;
	}

	/**
	 * Obtain a {@link TypeNode} object for a given {@link ElkClass}, or
	 * {@code null} if none assigned.
	 * 
	 * @param individual
	 * @return instance node object for elkClass, possibly still incomplete
	 */
	@Override
	public InstanceNode<ElkClass, ElkNamedIndividual> getInstanceNode(
			ElkNamedIndividual individual) {
		return individualNodeLookup_.get(getKey(individual));
	}

	@Override
	public TaxonomyNode<ElkClass> getNode(ElkClass elkClass) {
		return getTypeNode(elkClass);
	}

	@Override
	public Set<? extends TypeNode<ElkClass, ElkNamedIndividual>> getTypeNodes() {
		//return Collections.unmodifiableSet(allClassNodes_);
		Set<? extends TypeNode<ElkClass, ElkNamedIndividual>> allNodes = classTaxonomy_.allSatisfiableClassNodes_;
		Set<TypeNode<ElkClass, ElkNamedIndividual>> bottom = Collections.<TypeNode<ElkClass, ElkNamedIndividual>>singleton(classTaxonomy_.bottomClassNode);
		
		return new LazySetUnion<TypeNode<ElkClass, ElkNamedIndividual>>(allNodes, bottom);
	}

	@Override
	public Set<? extends InstanceNode<ElkClass, ElkNamedIndividual>> getInstanceNodes() {
		return Collections.unmodifiableSet(allIndividualNodes_);
	}

	@Override
	public Set<? extends TaxonomyNode<ElkClass>> getNodes() {
		return getTypeNodes();
	}


	@Override
	public IndividualNode getCreateIndividualNode(
			Collection<ElkNamedIndividual> members) {
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
		
		if (previous != null)
			return previous;

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
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public UpdateableTypeNode<ElkClass, ElkNamedIndividual> getUpdateableTypeNode(
			ElkClass elkClass) {
		//return classNodeLookup_.get(getKey(elkObject));
		return classTaxonomy_.getNonBottomNode(elkClass);
	}

	@Override
	public UpdateableTypeNode<ElkClass, ElkNamedIndividual> getCreateTypeNode(
			Collection<ElkClass> members) {
		return classTaxonomy_.getCreateNonBottomClassNode(members);
	}

	@Override
	public TypeNode<ElkClass, ElkNamedIndividual> getTopNode() {
		return classTaxonomy_.getTopNode();
	}

	@Override
	public TypeNode<ElkClass, ElkNamedIndividual> getBottomNode() {
		return classTaxonomy_.bottomClassNode;
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
		return classTaxonomy_.removeNode(node);
	}

	@Override
	public UpdateableTaxonomyNode<ElkClass> getUpdateableNode(ElkClass elkObject) {
		return classTaxonomy_.getUpdateableNode(elkObject);
	}

	@Override
	public Iterable<? extends UpdateableTaxonomyNode<ElkClass>> getUpdateableNodes() {
		return classTaxonomy_.getUpdateableNodes();
	}


}