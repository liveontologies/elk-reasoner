package org.semanticweb.elk.reasoner.taxonomy.nodes;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link GenericTaxonomyNode} that has at least one sub-node. It is not
 * possible to add new members to this
 * {@link AbstractGenericNonBottomTaxonomyNode}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <K>
 *            the type of the keys for the node members
 * @param <M>
 *            the type of node members
 * 
 */
abstract class AbstractGenericNonBottomTaxonomyNode<K, M, 
			N extends GenericTaxonomyNode<K, M, N>, 
			NB extends GenericNonBottomTaxonomyNode<K, M, N, NB>>
		extends 
		SimpleNode<K, M> 
		implements
		GenericNonBottomTaxonomyNode<K, M, N, NB> {

	
	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(AbstractGenericNonBottomTaxonomyNode.class);
	
	/**
	 * Direct super-nodes of this {@link AbstractGenericNonBottomTaxonomyNode}.
	 */
	private final Set<NB> directSuperNodes_;
	/**
	 * Direct sub-nodes of this {@link AbstractGenericNonBottomTaxonomyNode},
	 * except for the bottom node
	 */
	private final Set<NB> directSubNodes_;
	/**
	 * The bottom node used as a default sub node in case this
	 * {@link AbstractGenericNonBottomTaxonomyNode} does not have other
	 * sub-nodes
	 */
	private final N bottomNode_;
	
	public AbstractGenericNonBottomTaxonomyNode(Map<K, M> members,
			N botomNode) {
		super(members);
		this.bottomNode_ = botomNode;
		this.directSubNodes_ = new ArrayHashSet<NB>();
		this.directSuperNodes_ = new ArrayHashSet<NB>();
	}	

	abstract Set<? extends N> convertNodes(Set<? extends NB> set);

	@Override
	public Set<? extends N> getDirectSuperNodes() {
		return convertNodes(Collections.unmodifiableSet(directSuperNodes_));
	}

	@Override
	public Set<? extends N> getDirectSubNodes() {
		if (directSubNodes_.isEmpty())
			return Collections.singleton(bottomNode_);
		// else
		return convertNodes(Collections.unmodifiableSet(directSubNodes_));
	}

	@Override
	public Set<? extends N> getAllSuperNodes() {
		return Collections.unmodifiableSet(GenericTaxonomyNode.Helper
				.getAllSuperNodes(this));
	}

	@Override
	public Set<? extends N> getAllSubNodes() {
		return Collections.unmodifiableSet(GenericTaxonomyNode.Helper
				.getAllSubNodes(this));
	}
	
	@Override
	public synchronized boolean addDirectSuperNode(NB superNode) {
		if (!directSuperNodes_.add(superNode))
			return false;
		LOGGER_.trace("{}: new direct super-node {}", this, superNode);
		return true;
	}

	@Override
	public synchronized boolean removeDirectSuperNode(NB superNode) {
		if (!directSuperNodes_.remove(superNode))
			return false;
		LOGGER_.trace("{}: removed direct super-node {}", this, superNode);
		return true;
	}

	@Override
	public synchronized boolean addDirectSubNode(NB subNode) {
		if (!directSubNodes_.add(subNode))
			return false;
		LOGGER_.trace("{}: new direct sub-node {}", this, subNode);
		return true;
	}

	@Override
	public synchronized boolean removeDirectSubNode(NB subNode) {
		if (!directSubNodes_.remove(subNode))
			return false;
		LOGGER_.trace("{}: removed direct sub-node {}", this, subNode);
		return true;
	}
	
	@Override
	public <O> O accept(
			GenericTaxonomyNodeVisitor<K, M, N, O> visitor) {
		return visitor.visit(this);
	}

}
