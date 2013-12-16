package org.semanticweb.elk.reasoner.taxonomy;

import java.util.Map;
import java.util.Set;

import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericNonBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.NodeFactory;

public class UpdateableTaxonomyImpl<K, M>
  	extends
  	AbstractWrappableGenericTaxonomy<K, M, 
  			GenericTaxonomyNode.Min<K, M>,
			GenericNonBottomTaxonomyNode.Min<K,M>,
			GenericBottomTaxonomyNode.Min<K,M>>
	implements
	UpdateableGenericTaxonomy.Min<K, M> {

	public UpdateableTaxonomyImpl(
			NodeFactory<K, M, ? extends GenericNonBottomTaxonomyNode.Min<K, M>> nonBottomNodeFactory,
			UpdateableGenericNodeStore<K, M, GenericNonBottomTaxonomyNode.Min<K, M>> nonBottomNodeStore,
			Map<K, M> defaultTopMembers,
			GenericBottomTaxonomyNode.Min<K, M> bottomNode) {
		super(nonBottomNodeFactory, nonBottomNodeStore, defaultTopMembers, bottomNode);
	}

	@Override
	Set<? extends GenericTaxonomyNode.Min<K, M>> convertNonBottomNodes(
			Set<? extends GenericNonBottomTaxonomyNode.Min<K, M>> nodes) {
		return nodes;
	}

	@Override
	GenericTaxonomyNode.Min<K, M> convertNonBottomNode(
			GenericNonBottomTaxonomyNode.Min<K, M> node) {
		return node;
	}

	@Override
	GenericTaxonomyNode.Min<K, M> convertBottomNode(
			GenericBottomTaxonomyNode.Min<K, M> bottomNode) {
		return bottomNode;
	}

}
