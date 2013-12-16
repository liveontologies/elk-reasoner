package org.semanticweb.elk.reasoner.taxonomy;

import java.util.Set;

import org.semanticweb.elk.reasoner.taxonomy.nodes.TaxonomyNode;

/**
 * A representation for some collection of {@link TaxonomyNode}s. If a
 * {@link Taxonomy} contains a {@link TaxonomyNode}, it should also contain all
 * its sub-nodes and super-nodes. There should be exactly one
 * {@link TaxonomyNode} in this taxonomy that has no sub-nodes (the bottom node)
 * and exactly one {@link TaxonomyNode} that has not super-node (the top node).
 * These nodes could possibly be equal (in which case there cannot be any other
 * nodes in this {@link Taxonomy}. The sets of members of the
 * {@link TaxonomyNode}s stored in the {@link Taxonomy} should be disjoint.
 * 
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch
 * @author Frantisek Simancik
 * 
 * @param <K>
 *            the type of the keys for the node members
 * @param <M>
 *            the type of node members
 * 
 * @see GenericTaxonomy
 */
public interface Taxonomy<K, M> extends NodeStore<K, M> {
	
	/**
	 * @return an unmodifiable Set of all {@link TaxonomyNode}s stored in this
	 *         {@link Taxonomy}; this set should contain the top node and the
	 *         bottom node, and should contain all sub-node and super-nodes of
	 *         the nodes that it contains
	 * 
	 * @see #getTopNode()
	 * @see #getBottomNode()
	 * @see TaxonomyNode#getAllSubNodes()
	 * @see TaxonomyNode#getAllSuperNodes()
	 */
	@Override
	public Set<? extends TaxonomyNode<K, M>> getNodes();

	/**
	 * @param key
	 *            the key for which to return the {@link TaxonomyNode}
	 * 
	 * @return the {@link TaxonomyNode} containing a member with the given key
	 *         {@code null} if no such member occurs in any {@link TaxonomyNode}
	 *         of this {@link Taxonomy}
	 */
	@Override
	public TaxonomyNode<K, M> getNode(K key);

	/**
	 * @return the unique {@link TaxonomyNode} of this {@link Taxonomy} that has
	 *         no super-nodes
	 * @see TaxonomyNode#getAllSuperNodes()
	 */
	public TaxonomyNode<K, M> getTopNode();

	
	/**
	 * @return the unique {@link TaxonomyNode} of this {@link Taxonomy} that has
	 *         no sub-nodes
	 * @see TaxonomyNode#getAllSubNodes()
	 */
	public TaxonomyNode<K, M> getBottomNode();

}
