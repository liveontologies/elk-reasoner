package org.semanticweb.elk.reasoner.taxonomy.nodes;

import java.util.Map;

public interface NodeFactory<K, M, N extends Node<K, M>> {

	public N createNode(Map<K, M> members);

}
