package org.semanticweb.elk.reasoner.taxonomy;

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

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.reasoner.taxonomy.model.ComparatorKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.TypeNode;
import org.semanticweb.elk.util.collections.ArrayHashMap;

/**
 * An {@link InstanceTaxonomy} consisting of a single {@link TypeNode} = top
 * node = bottom node, which has a single {@link InstanceNode}. Typically, this
 * is used to represent an inconsistent {@link InstanceTaxonomy}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of objects stored in this taxonomy
 * @param <I>
 *            the type of instances of nodes of this taxonomy
 * @param <N>
 *            the type of the node of this taxonomy
 * 
 * @author "Yevgeny Kazakov"
 */
public class SingletoneInstanceTaxonomy<T extends ElkEntity, I extends ElkEntity, N extends OrphanTypeNode<T, I>>
		extends SingletoneTaxonomy<T, N> implements InstanceTaxonomy<T, I> {

	final Map<Object, InstanceNode<T, I>> instanceNodeLookup;
	/** provides keys that are used for hashing instead of the elkIndividuals */
	private final ComparatorKeyProvider<ElkEntity> individualKeyProvider_;

	public SingletoneInstanceTaxonomy(N node,
			final ComparatorKeyProvider<ElkEntity> individualKeyProvider) {
		super(node);
		this.individualKeyProvider_ = individualKeyProvider;
		this.instanceNodeLookup = new ArrayHashMap<Object, InstanceNode<T, I>>(node
				.getAllInstanceNodes().size());
		for (InstanceNode<T, I> instanceNode : node.getAllInstanceNodes()) {
			for (I instance : instanceNode) {
				instanceNodeLookup.put(individualKeyProvider_.getKey(instance), instanceNode);
			}
		}
	}

	@Override
	public ComparatorKeyProvider<ElkEntity> getInstanceKeyProvider() {
		return individualKeyProvider_;
	}
	
	@Override
	public TypeNode<T, I> getTopNode() {
		return node;
	}

	@Override
	public TypeNode<T, I> getBottomNode() {
		return node;
	}

	@Override
	public TypeNode<T, I> getTypeNode(T elkObject) {
		if (node.contains(elkObject))
			return node;
		// else
		return null;
	}

	@Override
	public Set<? extends TypeNode<T, I>> getTypeNodes() {
		return Collections.singleton(node);
	}

	@Override
	public InstanceNode<T, I> getInstanceNode(I elkObject) {
		return instanceNodeLookup.get(individualKeyProvider_.getKey(elkObject));
	}

	@Override
	public Set<? extends InstanceNode<T, I>> getInstanceNodes() {
		return node.instanceNodes;
	}

}
