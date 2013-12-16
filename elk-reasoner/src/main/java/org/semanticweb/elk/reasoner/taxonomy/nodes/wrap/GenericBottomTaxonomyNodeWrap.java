package org.semanticweb.elk.reasoner.taxonomy.nodes.wrap;

import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTaxonomyNode;

public interface GenericBottomTaxonomyNodeWrap<K, M, 
			WN extends GenericTaxonomyNode<K, M, WN>,
			W extends GenericBottomTaxonomyNode<K, M, WN>,
			N extends GenericTaxonomyNodeWrap<K, M, WN, ?, N>>
	extends
	GenericTaxonomyNodeWrap<K, M, WN, W, N>, 
	GenericBottomTaxonomyNode<K, M, N> {
		
	static interface Min<K, M, 
				WN extends GenericTaxonomyNode<K, M, WN>,
				W extends GenericBottomTaxonomyNode<K, M, WN>>				
	extends
	GenericTaxonomyNodeWrap.Min<K, M, WN, W>, 
	GenericBottomTaxonomyNodeWrap<K, M, 
	    WN,
	    W,
	    GenericTaxonomyNodeWrap.Min<K, M, WN, ?>> {
	
	}
	
}
