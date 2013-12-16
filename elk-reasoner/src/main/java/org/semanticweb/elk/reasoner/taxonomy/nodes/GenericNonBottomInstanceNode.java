package org.semanticweb.elk.reasoner.taxonomy.nodes;


public interface GenericNonBottomInstanceNode<K, M, KI, I, 
			TN extends GenericTypeNode<K, M, KI, I, TN, IN>, 
			TNB extends GenericNonBottomTypeNode<K, M, KI, I, TN, TNB, IN, INB>, 
			IN extends GenericInstanceNode<K, M, KI, I, TN, IN>, 
			INB extends GenericNonBottomInstanceNode<K, M, KI, I, TN, TNB, IN, INB>>
		extends 
		GenericInstanceNode<K, M, KI, I, TN, IN> {

	public boolean addDirectTypeNode(TNB typeNode);

	public boolean removeDirectTypeNode(TNB typeNode);

	interface Min<K, M, KI, I>
	extends
	GenericInstanceNode.Min<K, M, KI, I>,
	GenericNonBottomInstanceNode<K, M, KI, I, 
				GenericTypeNode.Min<K, M, KI, I>,
				GenericNonBottomTypeNode.Min<K, M, KI, I>,
				GenericInstanceNode.Min<K, M, KI, I>,
				GenericNonBottomInstanceNode.Min<K, M, KI, I>> {
	}
	
}
