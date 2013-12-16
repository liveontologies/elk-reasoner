package org.semanticweb.elk.reasoner.taxonomy.nodes.wrap;

import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericInstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTaxonomyNode;

public interface GenericInstanceNodeWrap<K, M, 
			WN extends GenericTaxonomyNode<K, M, WN>,
			KI, I,
			TN extends GenericTypeNodeWrap<K, M, WN, ?, KI, I, TN, IN>,
			IN extends GenericInstanceNodeWrap<K, M, WN, KI, I, TN, IN>>
		extends
		GenericInstanceNode<K, M, KI, I, TN, IN> {
	
	
	static interface Min<K, M, 
				WN extends GenericTaxonomyNode<K, M, WN>,
				KI, I>				
	extends 
	GenericInstanceNodeWrap<K, M, 
	    WN,
	    KI, I,
	    GenericTypeNodeWrap.Min<K, M, WN, ?, KI, I>,
	    GenericInstanceNodeWrap.Min<K, M, WN, KI, I>> {
		
	}
	
}
