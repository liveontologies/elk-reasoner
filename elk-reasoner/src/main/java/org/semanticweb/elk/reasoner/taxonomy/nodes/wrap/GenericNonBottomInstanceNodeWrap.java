package org.semanticweb.elk.reasoner.taxonomy.nodes.wrap;

import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericNonBottomInstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericNonBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTaxonomyNode;

public interface GenericNonBottomInstanceNodeWrap<K, M, 
			WN extends GenericTaxonomyNode<K, M, WN>,
			WNB extends GenericNonBottomTaxonomyNode<K, M, WN, WNB>,
			KI, I,
			TN extends GenericTypeNodeWrap<K, M, WN, ?, KI, I, TN, IN>,
			TNB extends GenericNonBottomTypeNodeWrap<K, M, WN, WNB, ? extends WNB, KI, I, TN, TNB, IN, INB>,
			IN extends GenericInstanceNodeWrap<K, M, WN, KI, I, TN, IN>,
			INB extends GenericNonBottomInstanceNodeWrap<K, M, WN, WNB, KI, I, TN, TNB, IN, INB>>
	extends 
	GenericNonBottomInstanceNode<K, M, KI, I, TN, TNB, IN, INB>,
	GenericInstanceNodeWrap<K, M, WN, KI, I, TN, IN> {
			
	
	static interface Min<K, M,
				WN extends GenericTaxonomyNode<K, M, WN>,
				WNB extends GenericNonBottomTaxonomyNode<K, M, WN, WNB>,
				KI, I>				
	extends 
	GenericInstanceNodeWrap.Min<K, M, WN, KI, I>,
	GenericNonBottomInstanceNodeWrap<K, M, 
	    WN,
	    WNB,
	    KI, I,
	    GenericTypeNodeWrap.Min<K, M, WN, ?, KI, I>,
	    GenericNonBottomTypeNodeWrap.Min<K, M, WN, WNB, ? extends WNB, KI, I>,
	    GenericInstanceNodeWrap.Min<K, M, WN, KI, I>,
	    GenericNonBottomInstanceNodeWrap.Min<K, M, WN, WNB, KI, I>> {
		
	}
}
