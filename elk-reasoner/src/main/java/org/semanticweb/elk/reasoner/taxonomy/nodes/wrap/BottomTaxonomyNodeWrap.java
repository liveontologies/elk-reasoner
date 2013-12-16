package org.semanticweb.elk.reasoner.taxonomy.nodes.wrap;

import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTaxonomyNode;

public class BottomTaxonomyNodeWrap<K, M, WN extends GenericTaxonomyNode<K, M, WN>>				
	extends	
	AbstractGenericBottomTaxonomyNodeWrap<K, M, 
				WN,
				GenericBottomTaxonomyNode<K, M, WN>,		
				GenericTaxonomyNodeWrap.Min<K, M, WN, ?>,
				TaxonomyNodeWrapper.Min<K, M, WN>>
	implements
	GenericBottomTaxonomyNodeWrap.Min<K, M, WN, GenericBottomTaxonomyNode<K, M, WN>> {

	BottomTaxonomyNodeWrap(GenericBottomTaxonomyNode<K, M, WN> node,
			TaxonomyNodeWrapper.Min<K, M, WN> wrapper) {
		super(node, wrapper);
	}

}