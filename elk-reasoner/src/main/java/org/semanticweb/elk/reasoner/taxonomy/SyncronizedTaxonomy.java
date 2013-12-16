package org.semanticweb.elk.reasoner.taxonomy;

import java.util.Map;

import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericNonBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.NodeFactory;

public class SyncronizedTaxonomy<K, M> extends UpdateableTaxonomyImpl<K, M> {

	public SyncronizedTaxonomy(
			NodeFactory<K, M, ? extends GenericNonBottomTaxonomyNode.Min<K, M>> nonBottomNodeFactory,
			Map<K, M> defaultTopMembers,
			GenericBottomTaxonomyNode.Min<K, M> bottomNode) {
		super(
				nonBottomNodeFactory,
				new SynchronizedNodeStore<K, M, GenericNonBottomTaxonomyNode.Min<K, M>>(),
				defaultTopMembers, bottomNode);
	}

}
