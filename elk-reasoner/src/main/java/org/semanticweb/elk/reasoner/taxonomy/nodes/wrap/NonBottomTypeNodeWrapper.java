package org.semanticweb.elk.reasoner.taxonomy.nodes.wrap;

import java.util.Set;

import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericNonBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.InstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.TypeNode;

public interface NonBottomTypeNodeWrapper<K, M, 
			WN extends GenericTaxonomyNode<K, M, WN>,
			KI, I,
			TN extends TypeNode<K, M, KI, I>,
			IN extends InstanceNode<K, M, KI, I>> 

	extends TaxonomyNodeWrapper<K, M, WN, TN> {

	Set<? extends IN> getDirectInstanceNodes(GenericTaxonomyNode<K, M, WN> node);	
	
	public interface Min<K, M, 
				WN extends GenericTaxonomyNode<K, M, WN>,
				KI, I>
	extends 
	NonBottomTypeNodeWrapper<K, M,
			WN,
			KI, I,
			GenericTypeNodeWrap.Min<K, M, WN, ?, KI, I>,
			GenericInstanceNodeWrap.Min<K, M, WN, KI, I>> {
		
		<WNB extends GenericNonBottomTaxonomyNode<K, M, WN, WNB>>		
		boolean addDirectInstanceNode(GenericTaxonomyNode<K, M, WN> node, 
				GenericNonBottomInstanceNodeWrap.Min<K, M, WN, WNB, KI, I> instanceNode);
		
		<WNB extends GenericNonBottomTaxonomyNode<K, M, WN, WNB>>
		boolean removeDirectInstanceNode(GenericTaxonomyNode<K, M, WN> node, 
				GenericNonBottomInstanceNodeWrap.Min<K, M, WN, WNB, KI, I> instanceNode);
		
	}	
	
}
