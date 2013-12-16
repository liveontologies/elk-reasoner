package org.semanticweb.elk.reasoner.taxonomy;

import java.util.Set;

import org.semanticweb.elk.reasoner.taxonomy.nodes.Node;

public interface NodeStore<K, M> {

	/**
	 * @return the unmodifiable set of all {@link Node}s stored in this
	 *         {@link NodeStore}
	 * 
	 */
	public Set<? extends Node<K, M>> getNodes();

	/**
	 * Retrieves a {@link Node} in this {@link NodeStore} which has a given a
	 * key for some of its member
	 * 
	 * @param key
	 *            a member key
	 * @return the {@link Node} containing a member with the matching key or
	 *         {@code null} if such {@link Node} does not exist
	 */
	public Node<K, M> getNode(K key);

}
