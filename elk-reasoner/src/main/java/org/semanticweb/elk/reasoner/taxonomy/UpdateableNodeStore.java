package org.semanticweb.elk.reasoner.taxonomy;

import org.semanticweb.elk.reasoner.taxonomy.nodes.Node;

public interface UpdateableNodeStore<K, M> extends NodeStore<K, M> {

	/**
	 * Adds the given node to this this {@link NodeStore} if it does not already
	 * contain a node with the same members.
	 * 
	 * @param node
	 *            the node to be added
	 * @return the node with the same members contained in the {@link NodeStore}
	 *         or {@code null} if there was no such a node
	 */
	public Node<K, M> putIfAbsent(Node<K, M> node);

}
