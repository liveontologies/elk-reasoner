package org.semanticweb.elk.reasoner.taxonomy.nodes;

public interface GenericBottomInstanceNode<K, M, KI, I, 
			TN extends GenericTypeNode<K, M, KI, I, TN, IN>, 
			IN extends GenericInstanceNode<K, M, KI, I, TN, IN>>
	extends
	GenericInstanceNode<K, M, KI, I, TN, IN> {
	
	// no additional methods; bottom instances are added through the bottom type node	
	
	interface Min<K, M, KI, I> 
	extends
	GenericInstanceNode.Min<K, M, KI, I>,
	GenericBottomInstanceNode<K, M, KI, I, 
				GenericTypeNode.Min<K, M, KI, I>,
				GenericInstanceNode.Min<K, M, KI, I>> {
		
	}

}
