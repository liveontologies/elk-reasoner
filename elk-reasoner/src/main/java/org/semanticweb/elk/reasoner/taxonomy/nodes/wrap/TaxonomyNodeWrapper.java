package org.semanticweb.elk.reasoner.taxonomy.nodes.wrap;

import java.util.Set;

import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.TaxonomyNode;

public interface TaxonomyNodeWrapper<K, M, 
			WN extends TaxonomyNode<K, M>, 
			N extends TaxonomyNode<K, M>> {

	Set<? extends N> convertNodes(Set<? extends WN> taxonomyNodes);

	
	interface Min<K, M, 
				WN extends GenericTaxonomyNode<K, M, WN>> 
	extends
			TaxonomyNodeWrapper<K, M, 
						WN, 
						GenericTaxonomyNodeWrap.Min<K, M, WN, ?>> {

	}

}
