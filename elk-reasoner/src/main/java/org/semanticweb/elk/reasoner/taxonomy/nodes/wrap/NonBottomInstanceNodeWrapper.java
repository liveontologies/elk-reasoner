package org.semanticweb.elk.reasoner.taxonomy.nodes.wrap;

import java.util.Set;

import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericNonBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.TypeNode;

public interface NonBottomInstanceNodeWrapper<K, M,
			WN extends GenericTaxonomyNode<K, M, WN>,			
			KI, I,
			TN extends TypeNode<K, M, KI, I>> {
	
	<WNB extends GenericNonBottomTaxonomyNode<K, M, WN, WNB>>
	Set<? extends TN> convertNonBottomNodes(Set<? extends WNB> taxonomyNodes);

	public interface Min<K, M,
		WN extends GenericTaxonomyNode<K, M, WN>,
		KI, I>
	extends 
	NonBottomInstanceNodeWrapper<K, M, WN, KI, I,
			GenericTypeNodeWrap.Min<K, M, WN, ?, KI, I>> {
				
	}	
	
	
}
