package org.semanticweb.elk.reasoner.taxonomy.nodes.wrap;

import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTypeNode;

public interface GenericTypeNodeWrap<K, M, 
			WN extends GenericTaxonomyNode<K, M, WN>,
			W extends GenericTaxonomyNode<K, M, WN>,
			KI, I,
			TN extends GenericTypeNodeWrap<K, M, WN, ?, KI, I, TN, IN>,
			IN extends GenericInstanceNodeWrap<K, M, WN, KI, I, TN, IN>>
		extends 
		GenericTaxonomyNodeWrap<K, M, WN, W, TN>,
		GenericTypeNode<K, M, KI, I, TN, IN> {	
	
	
	static interface Min<K, M,
				WN extends GenericTaxonomyNode<K, M, WN>,
				W extends GenericTaxonomyNode<K, M, WN>, 
				KI, I>				
	extends 
	GenericTypeNodeWrap<K, M, 
	    WN,
	    W,
	    KI, I,
	    GenericTypeNodeWrap.Min<K, M, WN, ?, KI, I>,
	    GenericInstanceNodeWrap.Min<K, M, WN, KI, I>> {
		
	}
	
}
