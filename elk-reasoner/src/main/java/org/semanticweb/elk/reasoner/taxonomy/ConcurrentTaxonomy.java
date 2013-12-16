package org.semanticweb.elk.reasoner.taxonomy;

import java.util.Map;

import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericNonBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.NodeFactory;

public class ConcurrentTaxonomy<K extends Comparable<K>, M> extends
		UpdateableTaxonomyImpl<K, M> {

	public ConcurrentTaxonomy(
			NodeFactory<K, M, ? extends GenericNonBottomTaxonomyNode.Min<K, M>> nonBottomNodeFactory,
			Map<K, M> defaultTopMembers,
			GenericBottomTaxonomyNode.Min<K, M> bottomNode) {
		super(
				nonBottomNodeFactory,
				new ConcurrentNodeStore<K, M, GenericNonBottomTaxonomyNode.Min<K, M>>(),
				defaultTopMembers, bottomNode);			
	}

}
