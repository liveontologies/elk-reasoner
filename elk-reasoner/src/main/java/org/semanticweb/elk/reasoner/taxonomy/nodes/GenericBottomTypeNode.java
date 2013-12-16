package org.semanticweb.elk.reasoner.taxonomy.nodes;

public interface GenericBottomTypeNode<K, M, KI, I, 
			TN extends GenericTypeNode<K, M, KI, I, TN, IN>, 
			IN extends GenericInstanceNode<K, M, KI, I, TN, IN>>
		extends 
		GenericTypeNode<K, M, KI, I, TN, IN>,
		GenericBottomTaxonomyNode<K, M, TN> {

	public I addBottomInstance(KI key, I instance);
	
	interface Min<K, M, KI, I> extends
	GenericTypeNode.Min<K,M,KI,I>,
	GenericBottomTypeNode<K, M, KI, I, 
				GenericTypeNode.Min<K, M, KI, I>,
				GenericInstanceNode.Min<K, M, KI, I>> {
		
	}

}
