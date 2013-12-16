package org.semanticweb.elk.reasoner.taxonomy.nodes.wrap;

import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTaxonomyNode;

public class BottomTypeNodeWrap<K, M,
			WN extends GenericTaxonomyNode<K, M, WN>,
			W extends GenericBottomTaxonomyNode<K, M, WN>, 
			KI, I>				
	extends	
	AbstractGenericBottomTypeNodeWrap<K, M, 
				WN,
				W,
				KI, I,
				GenericTypeNodeWrap.Min<K, M, WN, ?, KI, I>,
				GenericInstanceNodeWrap.Min<K, M, WN, KI, I>,
				BottomTypeNodeWrapper.Min<K, M, WN, KI, I>>
	implements
	GenericBottomTypeNodeWrap.Min<K, M, WN, W, KI, I> {

	public BottomTypeNodeWrap(W node, BottomTypeNodeWrapper.Min<K, M, WN, KI, I> wrapper) {
		super(node, wrapper);
	}

}