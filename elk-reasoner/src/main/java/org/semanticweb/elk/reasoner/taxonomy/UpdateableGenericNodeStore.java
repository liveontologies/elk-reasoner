package org.semanticweb.elk.reasoner.taxonomy;

import java.util.Map;

import org.semanticweb.elk.reasoner.taxonomy.nodes.Node;
import org.semanticweb.elk.reasoner.taxonomy.nodes.NodeFactory;

public interface UpdateableGenericNodeStore<K, M, N extends Node<K, M>> extends
		GenericNodeStore<K, M, N> {

	/**
	 * Adds the given node to this this {@link NodeStore} if it does not already
	 * contain a node with the same members.
	 * 
	 * @param node
	 *            the node to be added
	 * @return the node with the same members contained in the {@link NodeStore}
	 *         or {@code null} if there was no such a node
	 */
	public N putIfAbsent(N node);

	static class Helper {

		static <K, M, N extends Node<K, M>> N getCreateNode(
				UpdateableGenericNodeStore<K, M, N> nodeStore,
				NodeFactory<K, M, ? extends N> nodeFactory, Map<K, M> members) {
			K firstKey = members.keySet().iterator().next();
			N result = nodeStore.getNode(firstKey);
			if (result != null)
				return result;
			// else create a new node and try to insert to the node store
			N newNode = nodeFactory.createNode(members);
			result = nodeStore.putIfAbsent(newNode);
			if (result != null)
				return result;
			// else
			return newNode;
		}

	}

}
