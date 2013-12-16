package org.semanticweb.elk.reasoner.taxonomy.nodes.wrap;

import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericNonBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTaxonomyNode;

public class NonBottomTaxonomyNodeWrap<K, M,
			WN extends GenericTaxonomyNode<K, M, WN>,
			WNB extends GenericNonBottomTaxonomyNode<K, M, WN, WNB>>				
	extends	
	AbstractGenericNonBottomTaxonomyNodeWrap<K, M, 
				WN,
				WNB,
				GenericNonBottomTaxonomyNode<K, M, WN, WNB>,
				GenericTaxonomyNodeWrap.Min<K, M, WN, ?>,
				GenericNonBottomTaxonomyNodeWrap.Min<K, M, WN, WNB, ? extends WNB>,
				TaxonomyNodeWrapper.Min<K, M, WN>>
	implements
	GenericNonBottomTaxonomyNodeWrap.Min<K, M, WN, WNB, GenericNonBottomTaxonomyNode<K, M, WN, WNB>> {

	NonBottomTaxonomyNodeWrap(GenericNonBottomTaxonomyNode<K, M, WN, WNB> node, TaxonomyNodeWrapper.Min<K, M, WN> wrapper) {
		super(node, wrapper);
	}

}
