package org.semanticweb.elk.reasoner.taxonomy.nodes;

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

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.semanticweb.elk.reasoner.taxonomy.GenericNodeStore;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.collections.Operations.Condition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link GenericTaxonomyNode} that does not have any sub-nodes. It is
 * possible to add new members to this {@link GenericTaxonomyNode}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <K>
 *            the type of the keys for the node members
 * @param <M>
 *            the type of node members
 * @param <N>
 *            the type of sub-nodes and super-nodes of this
 *            {@code BottomTaxonomyNode}
 * 
 * @see NonBottomTaxonomyNode
 */
abstract class AbstractGenericBottomTaxonomyNode<K, M, 
			N extends GenericTaxonomyNode<K, M, N>>
		extends 
		SimpleNode<K, M> 
		implements 
		GenericBottomTaxonomyNode<K, M, N> {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(AbstractGenericBottomTaxonomyNode.class);

	/**
	 * the set of all super-nodes
	 */
	private final GenericNodeStore<K, M, ? extends N> nonBottomNodes_;

	/**
	 * the number of direct super-nodes
	 */
	private final AtomicInteger countDirectSuperNodes_;

	public AbstractGenericBottomTaxonomyNode(Map<K, M> members,
			GenericNodeStore<K, M, ? extends N> upperTaxonomy) {
		super(members);
		this.nonBottomNodes_ = upperTaxonomy;
		this.countDirectSuperNodes_ = new AtomicInteger(0);
		for (TaxonomyNode<K, M> node : upperTaxonomy.getNodes())
			if (!node.getDirectSubNodes().isEmpty())
				countDirectSuperNodes_.incrementAndGet();
	}
	
	@Override
	public Set<? extends N> getAllSuperNodes() {
		return nonBottomNodes_.getNodes();
	}

	@Override
	public Set<? extends N> getDirectSuperNodes() {
		return Operations.filter(nonBottomNodes_.getNodes(),
				new Condition<TaxonomyNode<K, M>>() {
					@Override
					public boolean holds(TaxonomyNode<K, M> node) {
						return node.getDirectSubNodes().contains(this);
					}
				}, countDirectSuperNodes_.get());
	}

	@Override
	public Set<? extends N> getAllSubNodes() {
		return Collections.emptySet();
	}

	@Override
	public Set<? extends N> getDirectSubNodes() {
		return Collections.emptySet();
	}

	@Override
	public M addMember(K key, M member) {
		LOGGER_.trace("{}: new node member {}->{}", this, key, member);
		return membersLookup.put(key, member);
	}

	@Override
	public <O> O accept(GenericTaxonomyNodeVisitor<K, M, N, O> visitor) {
		return visitor.visit(this);
	}	

}
