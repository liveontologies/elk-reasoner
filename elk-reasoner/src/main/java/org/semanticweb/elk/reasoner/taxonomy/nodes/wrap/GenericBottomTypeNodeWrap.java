package org.semanticweb.elk.reasoner.taxonomy.nodes.wrap;

import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericBottomTypeNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTaxonomyNode;

public interface GenericBottomTypeNodeWrap<K, M, 
			WN extends GenericTaxonomyNode<K, M, WN>,
			W extends GenericBottomTaxonomyNode<K, M, WN>,
			KI, I,
			TN extends GenericTypeNodeWrap<K, M, WN, ?, KI, I, TN, IN>,
			IN extends GenericInstanceNodeWrap<K, M, WN, KI, I, TN, IN>>
		extends 
		GenericBottomTaxonomyNodeWrap<K, M, WN, W, TN>,
		GenericBottomTypeNode<K, M, KI, I, TN, IN>,
		GenericTypeNodeWrap<K, M, WN, W, KI, I, TN, IN> {
	
	
	static interface Min<K, M,
				WN extends GenericTaxonomyNode<K, M, WN>,
				W extends GenericBottomTaxonomyNode<K, M, WN>,
				KI, I>				
	extends 
	GenericTypeNodeWrap.Min<K, M, WN, W, KI, I>,
	GenericBottomTypeNodeWrap<K, M, 
	    WN,
	    W,
	    KI, I,
	    GenericTypeNodeWrap.Min<K, M, WN, ?, KI, I>,
	    GenericInstanceNodeWrap.Min<K, M, WN, KI, I>> {
		
	}
	
}
