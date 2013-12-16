package org.semanticweb.elk.reasoner.taxonomy.nodes;

import java.util.Map;

public class BottomInstanceNode<K, M, KI, I>
		extends
		AbstractGenericBottomInstanceNode<K, M, KI, I, 
					GenericTypeNode.Min<K, M, KI, I>, 
					GenericInstanceNode.Min<K, M, KI, I>>
		implements GenericInstanceNode.Min<K, M, KI, I> {

	public BottomInstanceNode(GenericTypeNode.Min<K, M, KI, I> bottomTypeNode,
			Map<KI, I> members) {
		super(bottomTypeNode, members);
	}

}
