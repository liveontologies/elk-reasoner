package org.semanticweb.elk.reasoner.taxonomy.nodes.wrap;

import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericNonBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericNonBottomTypeNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTaxonomyNode;

public interface GenericNonBottomTypeNodeWrap<K, M, 
			WN extends GenericTaxonomyNode<K, M, WN>,
			WNB extends GenericNonBottomTaxonomyNode<K, M, WN, WNB>,
			W extends GenericNonBottomTaxonomyNode<K, M, WN, WNB>,
			KI, I,
			TN extends GenericTypeNodeWrap<K, M, WN, ?, KI, I, TN, IN>,
			TNB extends GenericNonBottomTypeNodeWrap<K, M, WN, WNB, ? extends WNB, KI, I, TN, TNB, IN, INB>,
			IN extends GenericInstanceNodeWrap<K, M, WN, KI, I, TN, IN>,
			INB extends GenericNonBottomInstanceNodeWrap<K, M, WN, ?, KI, I, TN, TNB, IN, INB>>
	extends 
	GenericNonBottomTaxonomyNodeWrap<K, M, WN, WNB, W, TN, TNB>,
	GenericNonBottomTypeNode<K, M, KI, I, TN, TNB, IN, INB>,
	GenericTypeNodeWrap<K, M, WN, W, KI, I, TN, IN> {
	
	
	static interface Min<K, M,
			WN extends GenericTaxonomyNode<K, M, WN>,
			WNB extends GenericNonBottomTaxonomyNode<K, M, WN, WNB>,
			W extends GenericNonBottomTaxonomyNode<K, M, WN, WNB>, 
			KI, I>				
	extends
	GenericTypeNodeWrap.Min<K, M, WN, W, KI, I>,
	GenericNonBottomTypeNodeWrap<K, M, 
	    WN,
	    WNB,
	    W,
	    KI, I,
	    GenericTypeNodeWrap.Min<K, M, WN, ?, KI, I>,
	    GenericNonBottomTypeNodeWrap.Min<K, M, WN, WNB, ? extends WNB, KI, I>,
	    GenericInstanceNodeWrap.Min<K, M, WN, KI, I>,
	    GenericNonBottomInstanceNodeWrap.Min<K, M, WN, WNB, KI, I>> {
		
	}
			
}
