package org.semanticweb.elk.reasoner.taxonomy.nodes;

import java.util.Map;

public class NonBottomTaxonomyNodeFactory<K, M> implements
		NodeFactory<K, M, NonBottomTaxonomyNode<K, M>> {

	private final GenericTaxonomyNode.Min<K, M> bottomNode_;

	public NonBottomTaxonomyNodeFactory(GenericTaxonomyNode.Min<K, M> bottomNode) {
		this.bottomNode_ = bottomNode;
	}

	@Override
	public NonBottomTaxonomyNode<K, M> createNode(Map<K, M> membersLookup) {
		return new NonBottomTaxonomyNode<K, M>(membersLookup, bottomNode_);
	}
}
