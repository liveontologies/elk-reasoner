package org.semanticweb.elk.reasoner.taxonomy.nodes;

import java.util.Set;

import org.semanticweb.elk.reasoner.taxonomy.InstanceTaxonomy;

/**
 * A {@link Node} that stores instance assignment. Every {@link InstanceNode} is
 * assigned to a collection of {@link TypeNode}s that have this node as one of
 * the instances.
 * 
 * @author Frantisek Simancik
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
 * 
 * @see TypeNode
 * @see InstanceTaxonomy
 */
public interface InstanceNode<K, M, KI, I> extends Node<KI, I> {

	/**
	 * @return the unmodifiable set of all {@link TypeNode}s of this
	 *         {@link InstanceNode}. For each element of this set, this
	 *         {@link InstanceNode} should be among its instances.
	 * 
	 * @see TypeNode#getAllInstanceNodes()
	 */
	public Set<? extends TypeNode<K, M, KI, I>> getAllTypeNodes();

	/**
	 * @return the unmodifiable set of direct {@link TypeNode}s of this
	 *         {@link InstanceNode}, that is, the maximal subset of all
	 *         {@link TypeNode}s of this {@link InstanceNode} that does not
	 *         contain their super-{@link Node}s.
	 * 
	 * @see #getAllTypeNodes()
	 * @see TaxonomyNode#getAllSuperNodes()
	 */
	public Set<? extends TypeNode<K, M, KI, I>> getDirectTypeNodes();

}
