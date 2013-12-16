package org.semanticweb.elk.reasoner.taxonomy.nodes;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractGenericNonBottomInstanceNode<K, M, KI, I, 
			TN extends GenericTypeNode<K, M, KI, I, TN, IN>, 
			TNB extends GenericNonBottomTypeNode<K, M, KI, I, TN, TNB, IN, INB>, 
			IN extends GenericInstanceNode<K, M, KI, I, TN, IN>, 
			INB extends GenericNonBottomInstanceNode<K, M, KI, I, TN, TNB, IN, INB>>
		extends 
		SimpleNode<KI, I> 
		implements
		GenericNonBottomInstanceNode<K, M, KI, I, TN, TNB, IN, INB> {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(AbstractGenericNonBottomInstanceNode.class);

	/**
	 * Direct type nodes of this {@link AbstractGenericNonBottomInstanceNode}.
	 */
	final Set<TNB> directTypeNodes;

	abstract Set<? extends TN> convertTypeNodes(Set<? extends TNB> typeNodes);

	public AbstractGenericNonBottomInstanceNode(Map<KI, I> members) {
		super(members);
		this.directTypeNodes = new ArrayHashSet<TNB>();
	}

	@Override
	public Set<? extends TN> getAllTypeNodes() {
		return GenericInstanceNode.Helper.getAllTypeNodes(this);
	}

	@Override
	public Set<? extends TN> getDirectTypeNodes() {
		return convertTypeNodes(Collections.unmodifiableSet(directTypeNodes));
	}

	@Override
	public boolean addDirectTypeNode(TNB typeNode) {
		if (!directTypeNodes.add(typeNode))
			return false;
		LOGGER_.trace("{}: new direct type node {}", this, typeNode);
		return true;
	}

	@Override
	public boolean removeDirectTypeNode(TNB typeNode) {
		if (!directTypeNodes.remove(typeNode))
			return false;
		LOGGER_.trace("{}: removed direct type node {}", this, typeNode);
		return true;
	}

}
