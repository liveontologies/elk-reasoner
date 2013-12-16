package org.semanticweb.elk.reasoner.taxonomy.nodes;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.semanticweb.elk.util.collections.LazySetUnion;

abstract class AbstractGenericBottomInstanceNode<K, M, KI, I, 
			TN extends GenericTypeNode<K, M, KI, I, TN, IN>, 
			IN extends GenericInstanceNode<K, M, KI, I, TN, IN>>
		extends 
		SimpleNode<KI, I> 
		implements
		GenericBottomInstanceNode<K, M, KI, I, TN, IN> {

	private final TN bottomTypeNode_;

	public AbstractGenericBottomInstanceNode(TN bottomTypeNode, Map<KI, I> members) {
		super(members);
		this.bottomTypeNode_ = bottomTypeNode;
	}

	@Override
	public Set<? extends TN> getAllTypeNodes() {
		return new LazySetUnion<TN>(Collections.singleton(bottomTypeNode_),
				bottomTypeNode_.getAllSuperNodes());
	}

	@Override
	public Set<? extends TN> getDirectTypeNodes() {
		return Collections.singleton(bottomTypeNode_);
	}	

}
