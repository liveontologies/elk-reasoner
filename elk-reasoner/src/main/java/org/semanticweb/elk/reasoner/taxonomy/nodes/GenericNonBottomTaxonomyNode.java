package org.semanticweb.elk.reasoner.taxonomy.nodes;

public interface GenericNonBottomTaxonomyNode<K, M,
			N extends GenericTaxonomyNode<K, M, N>, 
			NB extends GenericNonBottomTaxonomyNode<K, M, N, NB>>
		extends 
		GenericTaxonomyNode<K, M, N> {

	public boolean addDirectSuperNode(NB superNode);

	public boolean removeDirectSuperNode(NB superNode);

	public boolean addDirectSubNode(NB subNode);

	public boolean removeDirectSubNode(NB subNode);

	interface Min<K, M>
			extends
			GenericTaxonomyNode.Min<K, M>,
			GenericNonBottomTaxonomyNode<K, M, 
						GenericTaxonomyNode.Min<K, M>, 
						GenericNonBottomTaxonomyNode.Min<K, M>> {
	}

}
