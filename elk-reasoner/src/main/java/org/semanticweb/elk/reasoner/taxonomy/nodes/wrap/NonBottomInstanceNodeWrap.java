package org.semanticweb.elk.reasoner.taxonomy.nodes.wrap;

import java.util.Map;

import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericNonBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTaxonomyNode;

public class NonBottomInstanceNodeWrap<K, M,
			WN extends GenericTaxonomyNode<K, M, WN>,
			WNB extends GenericNonBottomTaxonomyNode<K, M, WN, WNB>,
			KI, I>				
	extends	
	AbstractGenericNonBottomInstanceNodeWrap<K, M, 
				WN,
				WNB,
				KI, I,
				GenericTypeNodeWrap.Min<K, M, WN, ?, KI, I>,
				GenericNonBottomTypeNodeWrap.Min<K, M, WN, WNB, ? extends WNB, KI, I>,
				GenericInstanceNodeWrap.Min<K, M, WN, KI, I>,
				GenericNonBottomInstanceNodeWrap.Min<K, M, WN, WNB, KI, I>,
				NonBottomTypeNodeWrapper.Min<K, M, WN, KI, I>>
	implements
	GenericNonBottomInstanceNodeWrap.Min<K, M, WN, WNB, KI, I> {

public NonBottomInstanceNodeWrap(
				Map<KI, I> members,
				NonBottomInstanceNodeWrapper<K, M, 
				WN, 
				KI, I, 
				GenericTypeNodeWrap.Min<K, M, WN, ?, KI, I>> wrapper) {
	super(members, wrapper);
	}		

}
