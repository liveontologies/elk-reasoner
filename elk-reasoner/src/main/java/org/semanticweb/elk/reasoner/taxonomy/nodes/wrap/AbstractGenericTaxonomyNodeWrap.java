package org.semanticweb.elk.reasoner.taxonomy.nodes.wrap;

import java.util.Map;
import java.util.Set;

import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTaxonomyNode;

abstract class AbstractGenericTaxonomyNodeWrap<K, M, 
			WN extends GenericTaxonomyNode<K, M, WN>,
			W extends GenericTaxonomyNode<K, M, WN>,
			N extends GenericTaxonomyNodeWrap<K, M, WN, ? extends GenericTaxonomyNode<K, M, WN>, N>,
			WP extends TaxonomyNodeWrapper<K, M, WN, N>>
		implements
		GenericTaxonomyNodeWrap<K, M, WN, W, N> {

	private final W node_;
	
	final WP wrapper;

	AbstractGenericTaxonomyNodeWrap(W node, WP wrapper) {
		this.wrapper = wrapper;
		this.node_ = node;
	}

	@Override
	public W getWrappedNode() {
		return this.node_;
	}
	
	@Override
	public Map<K, M> getMembersLookup() {
		return getWrappedNode().getMembersLookup();
	}

	@Override
	public Set<? extends N> getAllSuperNodes() {
		return wrapper.convertNodes(getWrappedNode().getAllSuperNodes());
	}

	@Override
	public Set<? extends N> getDirectSuperNodes() {
		return wrapper.convertNodes(getWrappedNode().getDirectSuperNodes());
	}

	@Override
	public Set<? extends N> getAllSubNodes() {
		return wrapper.convertNodes(getWrappedNode().getAllSubNodes());
	}

	@Override
	public Set<? extends N> getDirectSubNodes() {
		return wrapper.convertNodes(getWrappedNode().getDirectSubNodes());
	}

}
