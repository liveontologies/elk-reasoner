package org.semanticweb.elk.reasoner.taxonomy.nodes;

import java.util.Map;
import java.util.Set;

public class NonBottomTaxonomyNode<K, M>
		extends
		AbstractGenericNonBottomTaxonomyNode<K, M, GenericTaxonomyNode.Min<K, M>, GenericNonBottomTaxonomyNode.Min<K, M>>
		implements GenericNonBottomTaxonomyNode.Min<K, M> {

	public NonBottomTaxonomyNode(Map<K, M> members,
			GenericTaxonomyNode.Min<K, M> botomNode) {
		super(members, botomNode);
	}

	@Override
	Set<? extends GenericTaxonomyNode.Min<K, M>> convertNodes(
			Set<? extends GenericNonBottomTaxonomyNode.Min<K, M>> set) {
		return set;
	}

}
