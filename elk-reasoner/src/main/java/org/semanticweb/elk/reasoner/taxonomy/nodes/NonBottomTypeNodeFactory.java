package org.semanticweb.elk.reasoner.taxonomy.nodes;

import java.util.Map;

public class NonBottomTypeNodeFactory<K, M, KI, I> implements
		NodeFactory<K, M, NonBottomTypeNode<K, M, KI, I>> {

	private final GenericTypeNode.Min<K, M, KI, I> bottomNode_;

	public NonBottomTypeNodeFactory(GenericTypeNode.Min<K, M, KI, I> bottomNode) {
		this.bottomNode_ = bottomNode;
	}

	@Override
	public NonBottomTypeNode<K, M, KI, I> createNode(Map<K, M> membersLookup) {
		return new NonBottomTypeNode<K, M, KI, I>(membersLookup, bottomNode_);
	}

}
