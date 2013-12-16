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

import org.semanticweb.elk.reasoner.taxonomy.GenericInstanceTaxonomy;

/**
 * A fresh {@link GenericInstanceNode} containing an assignment for one instance
 * that does not occur in a taxonomy. Such nodes are returned to queries when
 * {@link Reasoner#getAllowFreshEntities()} is set to {@code true}.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 * @param <K>
 *            the type of the keys for the node members
 * @param <M>
 *            the type of node members
 * @param <K>
 *            the type of the keys for the node instances
 * @param <I>
 *            the type of instances
 * @param <TN>
 *            the type of type-nodes of this {@code FreshInstanceNode}
 * @param <IN>
 *            the type of instance nodes of type nodes of this
 *            {@code FreshInstanceNode}
 * @param <T>
 *            the type of taxonomy to which this node is attached
 * 
 * @see Reasoner#getAllowFreshEntities()
 */
public class FreshInstanceNode<K, M, KI, I, TN extends GenericTypeNode<K, M, KI, I, TN, IN>, IN extends GenericInstanceNode<K, M, KI, I, TN, IN>, T extends GenericInstanceTaxonomy<K, M, KI, I, TN, IN>>
		extends FreshNode<KI, I> implements
		GenericInstanceNode<K, M, KI, I, TN, IN> {

	private final T taxonomy_;

	public FreshInstanceNode(Map.Entry<KI, I> instance, T taxonomy) {
		super(instance);
		this.taxonomy_ = taxonomy;
	}

	@Override
	public Set<? extends TN> getDirectTypeNodes() {
		return Collections.singleton(taxonomy_.getTopNode());
	}

	@Override
	public Set<? extends TN> getAllTypeNodes() {
		return Collections.singleton(taxonomy_.getTopNode());
	}

}
