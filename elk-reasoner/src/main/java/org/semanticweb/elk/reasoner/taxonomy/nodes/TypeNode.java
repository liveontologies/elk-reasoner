package org.semanticweb.elk.reasoner.taxonomy.nodes;

import java.util.Set;

/**
 * A {@link TaxonomyNode} that is assigned with a (possibly empty) set of
 * {@link InstanceNode}s storing instances of the node members.
 * 
 * @author Markus Kroetzsch
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
 */
public interface TypeNode<K, M, KI, I> extends TaxonomyNode<K, M> {

	@Override
	public Set<? extends TypeNode<K, M, KI, I>> getAllSuperNodes();

	@Override
	public Set<? extends TypeNode<K, M, KI, I>> getDirectSuperNodes();

	@Override
	public Set<? extends TypeNode<K, M, KI, I>> getAllSubNodes();

	@Override
	public Set<? extends TypeNode<K, M, KI, I>> getDirectSubNodes();

	/**
	 * @return the unmodifiable set of all {@link InstanceNode}s of this
	 *         {@link TypeNode}. For each element of this set, this
	 *         {@link TypeNode} should be among its type nodes.
	 * 
	 * @see InstanceNode#getAllTypeNodes()
	 */
	public Set<? extends InstanceNode<K, M, KI, I>> getAllInstanceNodes();

	/**
	 * @return the unmodifiable set of direct {@link InstanceNode}s of this
	 *         {@link TypeNode}, that is, the maximal subset of all
	 *         {@link InstanceNode}s that contains no {@link InstanceNode}s of
	 *         the sub-nodes of this {@link TypeNode}.
	 * 
	 * @see #getAllInstanceNodes()
	 * @see TaxonomyNode#getAllSubNodes()
	 */
	public Set<? extends InstanceNode<K, M, KI, I>> getDirectInstanceNodes();

}
