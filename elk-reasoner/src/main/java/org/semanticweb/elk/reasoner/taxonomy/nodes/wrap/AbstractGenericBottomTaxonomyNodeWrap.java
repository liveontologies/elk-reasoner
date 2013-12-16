package org.semanticweb.elk.reasoner.taxonomy.nodes.wrap;

import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTaxonomyNodeVisitor;

abstract class AbstractGenericBottomTaxonomyNodeWrap<K, M, 
			WN extends GenericTaxonomyNode<K, M, WN>,
			W extends GenericBottomTaxonomyNode<K, M, WN>,
			N extends GenericTaxonomyNodeWrap<K, M, WN, ?, N>,
			WP extends TaxonomyNodeWrapper<K, M, WN, N>>
	extends AbstractGenericTaxonomyNodeWrap<K, M, WN, W, N, WP> 
	implements GenericBottomTaxonomyNodeWrap<K, M, WN, W, N> {	
	
	AbstractGenericBottomTaxonomyNodeWrap(W node, WP wrapper) {
		super(node, wrapper);
	}
	
	@Override
	public M addMember(K key, M member) {
		return getWrappedNode().addMember(key, member);
	}
				
	@Override
	public <O> O accept(GenericTaxonomyNodeVisitor<K, M, N, O> visitor) {
		return visitor.visit(this);
	}
}
