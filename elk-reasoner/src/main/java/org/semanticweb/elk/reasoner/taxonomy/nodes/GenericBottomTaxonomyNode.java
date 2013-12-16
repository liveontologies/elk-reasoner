package org.semanticweb.elk.reasoner.taxonomy.nodes;

public interface GenericBottomTaxonomyNode<K, M,
			N extends GenericTaxonomyNode<K, M, N>>
		extends GenericTaxonomyNode<K, M, N> {

	/**
	 * Adds a new member assignment to this {@link UpdateableBottomNode}. If the
	 * node already contained an assignment for the same key, it is replaced
	 * with the new member.
	 * 
	 * @param key
	 *            the key for the new member
	 * @param member
	 *            the member to be added to the node
	 * @return the previous member assigned for the given key or {@link null} if
	 *         there was no such a member
	 */
	public M addMember(K key, M member);

	interface Min<K, M>
			extends
			GenericTaxonomyNode.Min<K, M>,
			GenericBottomTaxonomyNode<K, M, GenericTaxonomyNode.Min<K, M>> {

	}

}
