package org.semanticweb.elk.reasoner.taxonomy;

import java.util.Set;

import org.semanticweb.elk.reasoner.taxonomy.nodes.Node;

/**
 * A on object storing a collection of {@link Node}s. It is possible to retrieve
 * such nodes by keys of its members. In order to work correctly, different
 * nodes should not have members with equal keys.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <K>
 *            the type of the keys for the node members
 * @param <M>
 *            the type of node members
 * @param <N>
 *            the type of the {@link Node}s stored in this
 *            {@link GenericNodeStore}
 */
public interface GenericNodeStore<K, M, N extends Node<K, M>> extends
		NodeStore<K, M> {

	@Override
	public Set<? extends N> getNodes();

	@Override
	public N getNode(K key);

}
