package org.semanticweb.elk.reasoner.taxonomy.nodes.wrap;

import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericNonBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTaxonomyNode;

public interface GenericNonBottomTaxonomyNodeWrap<K, M, 
			WN extends GenericTaxonomyNode<K, M, WN>,
			WNB extends GenericNonBottomTaxonomyNode<K, M, WN, WNB>,
			W extends GenericNonBottomTaxonomyNode<K, M, WN, WNB>,
			N extends GenericTaxonomyNodeWrap<K, M, WN, ?, N>,
			NB extends GenericNonBottomTaxonomyNodeWrap<K, M, WN, WNB, ? extends WNB, N, NB>>
	extends 
	GenericNonBottomTaxonomyNode<K, M, N, NB>,
	GenericTaxonomyNodeWrap<K, M, WN, W, N> {
	
	
	static interface Min<K, M, 
				WN extends GenericTaxonomyNode<K, M, WN>,
				WNB extends GenericNonBottomTaxonomyNode<K, M, WN, WNB>,
				W extends GenericNonBottomTaxonomyNode<K, M, WN, WNB>>				
	extends 
	GenericTaxonomyNodeWrap.Min<K, M, WN, W>, 
	GenericNonBottomTaxonomyNodeWrap<K, M, 
		WN,
		WNB,
		W,
		GenericTaxonomyNodeWrap.Min<K, M, WN, ?>,
		GenericNonBottomTaxonomyNodeWrap.Min<K, M, WN, WNB, ? extends WNB>> {
	
	}
		
}
