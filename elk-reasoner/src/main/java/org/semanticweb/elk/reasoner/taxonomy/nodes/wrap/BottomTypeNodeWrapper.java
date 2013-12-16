package org.semanticweb.elk.reasoner.taxonomy.nodes.wrap;

import java.util.Set;

import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.InstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.TaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.TypeNode;

public interface BottomTypeNodeWrapper<K, M, 
			WN extends TaxonomyNode<K, M>,
			KI, I,
			TN extends TypeNode<K, M, KI, I>,
			IN extends InstanceNode<K, M, KI, I>>

	extends 
	TaxonomyNodeWrapper<K, M, WN, TN> {
	
	I addBottomInstance(KI key, I instance);
	
	Set<? extends IN> getDirectBottomInstanceNodes();
	
	public interface Min<K, M,
				WN extends GenericTaxonomyNode<K, M, WN>,
				KI, I> 
	extends 
	BottomTypeNodeWrapper<K, M,
			WN,
			KI, I,
			GenericTypeNodeWrap.Min<K, M, WN, ?, KI, I>,
			GenericInstanceNodeWrap.Min<K, M, WN, KI, I>> {
		
	}
	
}
