package org.semanticweb.elk.reasoner.taxonomy.nodes.wrap;

import java.util.Set;

import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericNonBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTypeNode;

abstract class AbstractGenericNonBottomTypeNodeWrap<K, M, 
			WN extends GenericTaxonomyNode<K, M, WN>,
			WNB extends GenericNonBottomTaxonomyNode<K, M, WN, WNB>,
			W extends GenericNonBottomTaxonomyNode<K, M, WN, WNB>,
			KI, I,
			TN extends GenericTypeNodeWrap<K, M, WN, ?, KI, I, TN, IN>,
			TNB extends GenericNonBottomTypeNodeWrap<K, M, WN, WNB, ? extends WNB, KI, I, TN, TNB, IN, INB>,
			IN extends GenericInstanceNodeWrap<K, M, WN, KI, I, TN, IN>,
			INB extends GenericNonBottomInstanceNodeWrap<K, M, WN, WNB, KI, I, TN, TNB, IN, INB>,
			WP extends NonBottomTypeNodeWrapper<K, M, WN, KI, I, TN, IN>>
	extends 
	AbstractGenericNonBottomTaxonomyNodeWrap<K, M, WN, WNB, W, TN, TNB, WP>
	implements 
	GenericNonBottomTypeNodeWrap<K, M, WN, WNB, W, KI, I, TN, TNB, IN, INB> {

	AbstractGenericNonBottomTypeNodeWrap(W node, WP wrapper) {
		super(node, wrapper);
	}
	
	@Override
	public Set<? extends IN> getAllInstanceNodes() {
		return GenericTypeNode.Helper.getAllInstanceNodes(this);
	}

	@Override
	public Set<? extends IN> getDirectInstanceNodes() {
		return wrapper.getDirectInstanceNodes(getWrappedNode());
	}
	
}
