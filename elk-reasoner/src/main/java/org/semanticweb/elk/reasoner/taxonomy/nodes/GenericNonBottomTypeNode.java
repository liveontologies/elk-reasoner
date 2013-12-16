package org.semanticweb.elk.reasoner.taxonomy.nodes;


public interface GenericNonBottomTypeNode<K, M, KI, I, 
			TN extends GenericTypeNode<K, M, KI, I, TN, IN>,
			TNB extends GenericNonBottomTypeNode<K, M, KI, I, TN, TNB, IN, INB>,
			IN extends GenericInstanceNode<K, M, KI, I, TN, IN>,
			INB extends GenericNonBottomInstanceNode<K, M, KI, I, TN, TNB, IN, INB>>
		extends 
		GenericTypeNode<K, M, KI, I, TN, IN>,
		GenericNonBottomTaxonomyNode<K, M, TN, TNB> {

	public boolean addDirectInstanceNode(INB instanceNode);

	public boolean removeDirectInstanceNode(INB instanceNode);

	
	interface Min<K, M, KI, I>
	extends
	GenericTypeNode.Min<K, M, KI, I>,
	GenericNonBottomTypeNode<K, M, KI, I, 
			GenericTypeNode.Min<K, M, KI, I>,
			GenericNonBottomTypeNode.Min<K, M, KI, I>,
			GenericInstanceNode.Min<K, M, KI, I>,
			GenericNonBottomInstanceNode.Min<K, M, KI, I>> {

	}
	
}
