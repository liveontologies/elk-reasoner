package org.semanticweb.elk.reasoner.taxonomy.nodes;

public interface GenericTaxonomyNodeVisitor<K, M, N extends GenericTaxonomyNode<K, M, N>, O> {

	public O visit(GenericBottomTaxonomyNode<K, M, N> node);

	public <NB extends GenericNonBottomTaxonomyNode<K, M, N, NB>> O visit(
			GenericNonBottomTaxonomyNode<K, M, N, NB> node);

}
