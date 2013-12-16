package org.semanticweb.elk.reasoner.taxonomy.nodes;

import java.util.Map;
import java.util.Set;

public class NonBottomInstanceNode<K, M, KI, I>
		extends
		AbstractGenericNonBottomInstanceNode<K, M, KI, I, 
					GenericTypeNode.Min<K, M, KI, I>, 
					GenericNonBottomTypeNode.Min<K, M, KI, I>, 
					GenericInstanceNode.Min<K, M, KI, I>, 
					GenericNonBottomInstanceNode.Min<K, M, KI, I>>
		implements 
		GenericInstanceNode.Min<K, M, KI, I> {

	public NonBottomInstanceNode(Map<KI, I> members) {
		super(members);
	}

	@Override
	Set<? extends GenericTypeNode.Min<K, M, KI, I>> convertTypeNodes(
			Set<? extends GenericNonBottomTypeNode.Min<K, M, KI, I>> typeNodes) {
		return typeNodes;
	}

}
