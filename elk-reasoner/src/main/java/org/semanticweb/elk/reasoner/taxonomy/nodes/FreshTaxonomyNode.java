/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.taxonomy.nodes;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.taxonomy.GenericTaxonomy;

/**
 * A fresh {@link GenericTaxonomyNode} containing an assignment for one member
 * that do not occur in a taxonomy. Such nodes are returned to queries when
 * {@link Reasoner#getAllowFreshEntities()} is set to {@code true}.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 * @param <K>
 *            the type of the keys for the node members
 * @param <M>
 *            the type of node members
 * @param <N>
 *            the type of sub-nodes and super-nodes of this
 *            {@code FreshTaxonomyNode}
 * @param <T>
 *            the type of taxonomy to which this node is attached
 * 
 * @see Reasoner#getAllowFreshEntities()
 */
public class FreshTaxonomyNode<K, M,
			N extends GenericTaxonomyNode<K, M, N>, 
			NB extends GenericNonBottomTaxonomyNode<K, M, N, NB>,
			T extends GenericTaxonomy<K, M, N>>
		extends 
		FreshNode<K, M> 
		implements GenericNonBottomTaxonomyNode<K, M, N, NB> {

	private final T taxonomy_;

	public FreshTaxonomyNode(Map.Entry<K, M> member, T taxonomy) {
		super(member);
		this.taxonomy_ = taxonomy;
	}
	
	@Override
	public Set<? extends N> getDirectSuperNodes() {
		return Collections.singleton(taxonomy_.getTopNode());
	}

	@Override
	public Set<? extends N> getAllSuperNodes() {
		return Collections.singleton(taxonomy_.getTopNode());
	}

	@Override
	public Set<? extends N> getDirectSubNodes() {
		return Collections.singleton(taxonomy_.getBottomNode());
	}

	@Override
	public Set<? extends N> getAllSubNodes() {
		return Collections.singleton(taxonomy_.getBottomNode());
	}

	@Override
	public <O> O accept(GenericTaxonomyNodeVisitor<K, M, N, O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public boolean addDirectSuperNode(NB superNode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeDirectSuperNode(NB superNode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addDirectSubNode(NB subNode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeDirectSubNode(NB subNode) {
		// TODO Auto-generated method stub
		return false;
	}

}
