package org.semanticweb.elk.reasoner.taxonomy;

import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTaxonomyNode;

public interface UpdateableGenericTaxonomy<K, M, N extends GenericTaxonomyNode<K, M, N>>
		extends 
		GenericTaxonomy<K, M, N>,
		UpdateableTaxonomy<K, M> {

	
	interface Min<K, M> 
		extends
		GenericTaxonomy.Min<K, M>,
		UpdateableGenericTaxonomy<K, M, GenericTaxonomyNode.Min<K, M>> {
		
	}
	
}
