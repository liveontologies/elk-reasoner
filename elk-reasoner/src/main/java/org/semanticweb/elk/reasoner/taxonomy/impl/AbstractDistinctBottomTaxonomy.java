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

import java.util.Set;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.reasoner.taxonomy.model.GenericTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableTaxonomy;

public abstract class AbstractDistinctBottomTaxonomy<
				T extends ElkEntity,
				N extends GenericTaxonomyNode<T, N>,
				UN extends UpdateableTaxonomyNode<T, N, UN>
		> extends AbstractTaxonomy<T> implements UpdateableTaxonomy<T> {

	/** thread safe set of unsatisfiable classes */
	final ConcurrentMap<Object, T> unsatisfiableClasses_;
	
	/** counts the number of nodes which have non-bottom sub-classes */
	final AtomicInteger countNodesWithSubClasses;

	public AbstractDistinctBottomTaxonomy() {
		this.unsatisfiableClasses_ = new ConcurrentHashMap<Object, T>();
		this.countNodesWithSubClasses = new AtomicInteger(0);
	}
	
	@Override
	public abstract N getBottomNode();
	
	@Override
	public abstract Set<? extends UN> getNonBottomNodes();

	protected abstract Set<? extends N> toTaxonomyNodes(Set<? extends UN> nodes);
	
}
