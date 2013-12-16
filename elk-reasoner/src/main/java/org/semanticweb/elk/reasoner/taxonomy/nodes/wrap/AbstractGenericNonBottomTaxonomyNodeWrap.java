package org.semanticweb.elk.reasoner.taxonomy.nodes.wrap;

import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericNonBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTaxonomyNodeVisitor;

abstract class AbstractGenericNonBottomTaxonomyNodeWrap<K, M, 
			WN extends GenericTaxonomyNode<K, M, WN>,
			WNB extends GenericNonBottomTaxonomyNode<K, M, WN, WNB>,
			W extends GenericNonBottomTaxonomyNode<K, M, WN, WNB>,
			N extends GenericTaxonomyNodeWrap<K, M, WN, ?, N>,
			NB extends GenericNonBottomTaxonomyNodeWrap<K, M, WN, WNB, ? extends WNB, N, NB>,
			WP extends TaxonomyNodeWrapper<K, M, WN, N>>
		extends AbstractGenericTaxonomyNodeWrap<K, M, WN, W, N, WP> implements
		GenericNonBottomTaxonomyNodeWrap<K, M, WN, WNB, W, N, NB> {
	
	AbstractGenericNonBottomTaxonomyNodeWrap(W node, WP wrapper) {
		super(node, wrapper);
	}
	
	@Override
	public boolean addDirectSuperNode(NB superNode) {
		return getWrappedNode().addDirectSuperNode(superNode.getWrappedNode());
	}

	@Override
	public boolean removeDirectSuperNode(NB superNode) {
		return getWrappedNode().removeDirectSuperNode(superNode.getWrappedNode());
	}

	@Override
	public boolean addDirectSubNode(NB subNode) {
		return getWrappedNode().addDirectSubNode(subNode.getWrappedNode());
	}

	@Override
	public boolean removeDirectSubNode(NB subNode) {
		return getWrappedNode().removeDirectSubNode(subNode.getWrappedNode());
	}
		
	@Override
	public <O> O accept(GenericTaxonomyNodeVisitor<K, M, N, O> visitor) {
		return visitor.visit(this);
	}	
		
}
