package org.semanticweb.elk.reasoner.taxonomy.nodes.wrap;

import java.util.Map;

import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTaxonomyNode;

public class BottomInstanceNodeWrap<K, M, 
			WN extends GenericTaxonomyNode<K, M, WN>,
			KI, I>				
	extends	
	AbstractGenericBottomInstanceNodeWrap<K, M, 
				WN,
				KI, I,
				GenericTypeNodeWrap.Min<K, M, WN, ?, KI, I>,
				GenericInstanceNodeWrap.Min<K, M, WN, KI, I>>
	implements
	GenericBottomInstanceNodeWrap.Min<K, M, WN, KI, I> {

	public BottomInstanceNodeWrap(Map<KI, I> members,
			GenericTypeNodeWrap.Min<K, M, WN, ?, KI, I> bottomTypeNode) {
		super(members, bottomTypeNode);
	}

}