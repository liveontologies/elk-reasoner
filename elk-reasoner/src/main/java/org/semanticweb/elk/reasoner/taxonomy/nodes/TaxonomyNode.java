package org.semanticweb.elk.reasoner.taxonomy.nodes;

import java.util.Set;

/**
 * A {@code Node} which is assigned with (possibly empty) sets of sub-nodes and
 * super-nodes.
 * 
 * @author Markus Kroetzsch
 * @author "Yevgeny Kazakov"
 * 
 * @param <K>
 *            the type of the keys for the node members
 * @param <M>
 *            the type of node members
 */
public interface TaxonomyNode<K, M> extends Node<K, M> {

	/**
	 * @return the unmodifiable set of all super-{@link Node}s of this
	 *         {@link Node}. This set should not contain this {@link Node}. For
	 *         each element of this set, this {@link Node} should be among its
	 *         sub-{@link Node}s.
	 * 
	 * @see #getAllSubNodes()
	 */
	public Set<? extends TaxonomyNode<K, M>> getAllSuperNodes();

	/**
	 * @return the unmodifiable set of direct super-{@link Node}s of this
	 *         {@link Node}, that is, the maximal subset of all super-
	 *         {@link Node}s of this {@link Node} that does not contain their
	 *         super-{@link Node}s.
	 * 
	 * @see #getAllSuperNodes()
	 */
	public Set<? extends TaxonomyNode<K, M>> getDirectSuperNodes();

	/**
	 * @return the unmodifiable set of all sub-{@link Node}s of this
	 *         {@link Node}. This set should not contain this {@link Node}. For
	 *         each element of this set, this {@link Node} should be among its
	 *         super-{@link Node}s.
	 * 
	 * @see #getAllSuperNodes()
	 */
	public Set<? extends TaxonomyNode<K, M>> getAllSubNodes();

	/**
	 * @return the unmodifiable set of direct sub-{@link Node}s of this
	 *         {@link Node}, that is, the maximal subset of all sub-{@link Node}
	 *         s of this {@link Node} that does not contain their sub-
	 *         {@link Node}s.
	 * 
	 * @see #getAllSubNodes()
	 */
	public Set<? extends TaxonomyNode<K, M>> getDirectSubNodes();

}
