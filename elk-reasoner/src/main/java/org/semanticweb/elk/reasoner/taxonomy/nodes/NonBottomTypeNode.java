package org.semanticweb.elk.reasoner.taxonomy.nodes;

import java.util.Map;
import java.util.Set;

public class NonBottomTypeNode<K, M, KI, I>
		extends
		AbstractGenericNonBottomTypeNode<K, M, KI, I, 
					GenericTypeNode.Min<K, M, KI, I>, 
					GenericNonBottomTypeNode.Min<K, M, KI, I>, 
					GenericInstanceNode.Min<K, M, KI, I>, 
					GenericNonBottomInstanceNode.Min<K, M, KI, I>>
		implements GenericNonBottomTypeNode.Min<K, M, KI, I> {

	public NonBottomTypeNode(Map<K, M> members,
			GenericTypeNode.Min<K, M, KI, I> bottomNode) {
		super(members, bottomNode);
	}

	@Override
	public Set<? extends GenericTypeNode.Min<K, M, KI, I>> convertNodes(
			Set<? extends GenericNonBottomTypeNode.Min<K, M, KI, I>> set) {
		return set;
	}

	@Override
	Set<? extends GenericInstanceNode.Min<K, M, KI, I>> convertInstanceNodes(
			Set<? extends GenericNonBottomInstanceNode.Min<K, M, KI, I>> instances) {
		return instances;
	}	
	
}
