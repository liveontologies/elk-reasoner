package org.semanticweb.elk.reasoner.taxonomy.nodes;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link GenericTypeNode} that has at least one sub-node. It is not possible
 * to add new members to this {@link GenericTypeNode}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <K>
 *            the type of the keys for the node members
 * @param <M>
 *            the type of node members
 * @param <KI>
 *            the type of the keys for the node instances
 * @param <I>
 *            the type of instances
 * @param <TN>
 *            the type of sub-nodes and super-nodes of this {@code TypeNode}
 * @param <IN>
 *            the type of instance nodes of this {@code TypeNode}
 */
abstract class AbstractGenericNonBottomTypeNode<K, M, KI, I,
			TN extends GenericTypeNode<K, M, KI, I, TN, IN>, 
			TNB extends GenericNonBottomTypeNode<K, M, KI, I, TN, TNB, IN, INB>, 
			IN extends GenericInstanceNode<K, M, KI, I, TN, IN>, 
			INB extends GenericNonBottomInstanceNode<K, M, KI, I, TN, TNB, IN, INB>>
		extends 
		AbstractGenericNonBottomTaxonomyNode<K, M, TN, TNB>
		implements 
		GenericNonBottomTypeNode<K, M, KI, I, TN, TNB, IN, INB> {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(AbstractGenericNonBottomTypeNode.class);

	/**
	 * Direct instance nodes of this
	 * {@link AbstractGenericNonBottomTypeNode}.
	 */
	final Set<INB> directInstanceNodes;

	public AbstractGenericNonBottomTypeNode(Map<K, M> members,
			TN bottomNode) {
		super(members, bottomNode);
		this.directInstanceNodes = new ArrayHashSet<INB>();
	}

	abstract Set<? extends IN> convertInstanceNodes(Set<? extends INB> instances);

	@Override
	public Set<? extends IN> getDirectInstanceNodes() {
		return convertInstanceNodes(Collections
				.unmodifiableSet(directInstanceNodes));
	}

	@Override
	public Set<? extends IN> getAllInstanceNodes() {
		return GenericTypeNode.Helper.getAllInstanceNodes(this);
	}

	@Override
	public synchronized boolean addDirectInstanceNode(INB instanceNode) {
		if (!directInstanceNodes.add(instanceNode))
			return false;
		LOGGER_.trace("{}: new direct instance node {}", this, instanceNode);
		return true;
	}

	@Override
	public synchronized boolean removeDirectInstanceNode(INB instanceNode) {
		if (!directInstanceNodes.remove(instanceNode))
			return false;
		LOGGER_.trace("{}: removed direct instance node {}", this, instanceNode);
		return true;
	}

}
