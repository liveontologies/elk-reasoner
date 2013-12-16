package org.semanticweb.elk.reasoner.taxonomy.nodes;

import java.util.Map;

import org.semanticweb.elk.reasoner.taxonomy.GenericNodeStore;

public class BottomTaxonomyNode<K, M> extends
		AbstractGenericBottomTaxonomyNode<K, M, GenericTaxonomyNode.Min<K, M>>
		implements GenericBottomTaxonomyNode.Min<K, M> {

	public BottomTaxonomyNode(
			Map<K, M> members,
			GenericNodeStore<K, M, ? extends GenericTaxonomyNode.Min<K, M>> upperTaxonomy) {
		super(members, upperTaxonomy);
	}

}
