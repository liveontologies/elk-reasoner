package org.semanticweb.elk.reasoner.taxonomy.nodes;

import java.util.Map;

public class NonBottomInstanceNodeFactory<K, M, KI, I> implements
		NodeFactory<KI, I, NonBottomInstanceNode<K, M, KI, I>> {

	@Override
	public NonBottomInstanceNode<K, M, KI, I> createNode(Map<KI, I> instances) {
		return new NonBottomInstanceNode<K, M, KI, I>(instances);
	}

}
