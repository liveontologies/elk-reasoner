package org.semanticweb.elk.reasoner.taxonomy.nodes.wrap;

import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericNonBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTaxonomyNode;

public class NonBottomTypeNodeWrap<K, M,
			WN extends GenericTaxonomyNode<K, M, WN>,
			WNB extends GenericNonBottomTaxonomyNode<K, M, WN, WNB>,
			W extends GenericNonBottomTaxonomyNode<K, M, WN, WNB>,
			KI, I>				
	extends	
	AbstractGenericNonBottomTypeNodeWrap<K, M, 
				WN,
				WNB,
				W,
				KI, I,
				GenericTypeNodeWrap.Min<K, M, WN, ?, KI, I>,
				GenericNonBottomTypeNodeWrap.Min<K, M, WN, WNB, ? extends WNB, KI, I>,
				GenericInstanceNodeWrap.Min<K, M, WN, KI, I>,
				GenericNonBottomInstanceNodeWrap.Min<K, M, WN, WNB, KI, I>,
				NonBottomTypeNodeWrapper.Min<K, M, WN, KI, I>>	
	implements
	GenericNonBottomTypeNodeWrap.Min<K, M, WN, WNB, W, KI, I> {

	public NonBottomTypeNodeWrap(W node, 
						NonBottomTypeNodeWrapper.Min<K, M, WN, KI, I> wrapper) {
		super(node, wrapper);
	}

	@Override
	public boolean addDirectInstanceNode(
			GenericNonBottomInstanceNodeWrap.Min<K, M, WN, WNB, KI, I> instanceNode) {
		return wrapper.addDirectInstanceNode(getWrappedNode(), instanceNode);
	}

	@Override
	public boolean removeDirectInstanceNode(
			GenericNonBottomInstanceNodeWrap.Min<K, M, WN, WNB, KI, I> instanceNode) {
		return wrapper.removeDirectInstanceNode(getWrappedNode(), instanceNode);
	}

}
