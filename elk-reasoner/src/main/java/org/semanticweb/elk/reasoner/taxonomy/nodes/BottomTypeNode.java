package org.semanticweb.elk.reasoner.taxonomy.nodes;

import java.util.Map;

import org.semanticweb.elk.reasoner.taxonomy.GenericNodeStore;

public class BottomTypeNode<K, M, KI, I>
		extends
		AbstractGenericBottomTypeNode<K, M, KI, I, 
					GenericTypeNode.Min<K, M, KI, I>, 
					GenericInstanceNode.Min<K, M, KI, I>>
		implements 
		GenericBottomTypeNode.Min<K, M, KI, I> {

	public BottomTypeNode(
			Map<K, M> members,
			Map<KI, I> instances,
			GenericNodeStore<K, M, ? extends GenericTypeNode.Min<K, M, KI, I>> upperTaxonomy) {
		super(members, instances, upperTaxonomy);
	}

	@Override
	GenericInstanceNode.Min<K, M, KI, I> createBottomInstanceNode(
			Map<KI, I> instances) {
		return new BottomInstanceNode<K, M, KI, I>(this, instances);
	}

}
