package org.semanticweb.elk.reasoner.taxonomy;

import java.util.Set;

import org.semanticweb.elk.reasoner.taxonomy.nodes.InstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.TypeNode;

/**
 * A {@link Taxonomy} that also stores a (possibly empty set) of
 * {@link InstanceNode}s. The types of each {@link InstanceNode} should be also
 * stored in this {@link InstanceTaxonomy}. The sets of members of the
 * {@link InstanceNode}s of this {@link Taxonomy} should be disjoint.
 * 
 * @author Markus Kroetzsch
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
public interface InstanceTaxonomy<K, M, KI, I> extends 
		Taxonomy<K, M> {
	
	@Override
	public Set<? extends TypeNode<K, M, KI, I>> getNodes();

	@Override
	public TypeNode<K, M, KI, I> getNode(K key);

	@Override
	public TypeNode<K, M, KI, I> getTopNode();
	
	@Override
	public TypeNode<K, M, KI, I> getBottomNode();

	/**
	 * @return an unmodifiable Set of all {@link InstanceNode}s stored in this
	 *         {@link InstanceTaxonomy}. This set can be empty. For
	 *         {@link InstanceNode} in this set, the {@link InstanceTaxonomy}
	 *         should contain all of its type nodes.
	 * 
	 * @see InstanceNode#getAllTypeNodes()
	 * @see #getNodes()
	 */
	public Set<? extends InstanceNode<K, M, KI, I>> getInstanceNodes();

	/**
	 * @param key
	 *            the member for which to return the {@link InstanceNode}
	 * @return the {@link InstanceNode} containing the given member or
	 *         {@code null} if the member does not occur in any
	 *         {@link InstanceNode} of this {@link Taxonomy}
	 */
	public InstanceNode<K, M, KI, I> getInstanceNode(KI key);

}
