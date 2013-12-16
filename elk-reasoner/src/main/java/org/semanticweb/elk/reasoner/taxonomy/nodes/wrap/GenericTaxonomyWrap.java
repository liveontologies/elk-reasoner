package org.semanticweb.elk.reasoner.taxonomy.nodes.wrap;

import java.util.Set;

import org.semanticweb.elk.reasoner.taxonomy.GenericTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTaxonomyNode;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.collections.Operations.FunctorEx;

public class GenericTaxonomyWrap<K, M, 
			WN extends GenericTaxonomyNode<K, M, WN>, 
			WT extends GenericTaxonomy<K, M, WN>, 
			N extends GenericTaxonomyNode<K, M, N>> 
		implements 
		GenericTaxonomy<K, M, N> {
	
	private final WT wrappedTaxonomy_;
	
	private final FunctorEx<WN, N> nodeWrapper_;
	
	public GenericTaxonomyWrap(WT wrappedTaxonomy, FunctorEx<WN, N> nodeWrapper) {
		this.wrappedTaxonomy_ = wrappedTaxonomy;
		this.nodeWrapper_ = nodeWrapper;		
	}

	@Override
	public Set<? extends N> getNodes() {
		return Operations.map(wrappedTaxonomy_.getNodes(), nodeWrapper_);
	}

	@Override
	public N getNode(K key) {
		WN wrappedNode = wrappedTaxonomy_.getNode(key);
		if (wrappedNode == null)
			return null;
		// else					
		return nodeWrapper_.apply(wrappedNode);
	}

	@Override
	public N getTopNode() {
		return nodeWrapper_.apply(wrappedTaxonomy_.getTopNode());
	}

	@Override
	public N getBottomNode() {
		return nodeWrapper_.apply(wrappedTaxonomy_.getBottomNode());
	}
	
	WT getWrappedTaxonomy() {
		return wrappedTaxonomy_;
	}

}
