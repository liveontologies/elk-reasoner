package org.semanticweb.elk.reasoner.taxonomy.nodes.wrap;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericInstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.SimpleNode;

class AbstractGenericBottomInstanceNodeWrap<K, M, 
			WN extends GenericTaxonomyNode<K, M, WN>,
			KI, I,
			TN extends GenericTypeNodeWrap<K, M, WN, ?, KI, I, TN, IN>,
			IN extends GenericInstanceNodeWrap<K, M, WN, KI, I, TN, IN>>

	extends 
	SimpleNode<KI, I>
	implements 
	GenericBottomInstanceNodeWrap<K, M, WN, KI, I, TN, IN> {

	private final TN bottomTypeNode_; 
	
	public AbstractGenericBottomInstanceNodeWrap(Map<KI, I> members, TN bottomTypeNode) {
		super(members);
		this.bottomTypeNode_ = bottomTypeNode;
	}

	@Override
	public Set<? extends TN> getAllTypeNodes() {
		return GenericInstanceNode.Helper.getAllTypeNodes(this);
	}

	@Override
	public Set<? extends TN> getDirectTypeNodes() {
		return Collections.singleton(bottomTypeNode_);
	}

}
