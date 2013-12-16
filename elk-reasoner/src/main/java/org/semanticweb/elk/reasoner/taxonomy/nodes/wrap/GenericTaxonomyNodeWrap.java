package org.semanticweb.elk.reasoner.taxonomy.nodes.wrap;

import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTaxonomyNode;

public interface GenericTaxonomyNodeWrap<K, M, 
			WN extends GenericTaxonomyNode<K, M, WN>,
			W extends GenericTaxonomyNode<K, M, WN>,
			N extends GenericTaxonomyNodeWrap<K, M, WN, ? extends GenericTaxonomyNode<K, M, WN>, N>>
		extends 
		NodeWrap<K, M, W>,
		GenericTaxonomyNode<K, M, N> {

	static interface Min<K, M,
		WN extends GenericTaxonomyNode<K, M, WN>,
		W extends GenericTaxonomyNode<K, M, WN>>				
		extends
		NodeWrap<K, M, W>, 
		GenericTaxonomyNodeWrap<K, M, 
		    WN,
		    W,
		    GenericTaxonomyNodeWrap.Min<K, M, WN, ?>> {
		
	}
	
}
