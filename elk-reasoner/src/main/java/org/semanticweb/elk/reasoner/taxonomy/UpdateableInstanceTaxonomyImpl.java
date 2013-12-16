package org.semanticweb.elk.reasoner.taxonomy;

import java.util.Map;
import java.util.Set;

import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericBottomTypeNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericInstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericNonBottomInstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericNonBottomTypeNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTypeNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.NodeFactory;

public class UpdateableInstanceTaxonomyImpl<K, M, KI, I>
extends AbstractUpdateableGenericInstanceTaxonomy<K, M, KI, I,
			GenericTypeNode.Min<K, M, KI, I>,
			GenericNonBottomTypeNode.Min<K, M, KI, I>,
			GenericBottomTypeNode.Min<K, M, KI, I>,			
			GenericInstanceNode.Min<K, M, KI, I>,
			GenericNonBottomInstanceNode.Min<K, M, KI, I>>
		implements
		UpdateableInstanceTaxonomy<K, M, KI, I> {

	
	public UpdateableInstanceTaxonomyImpl(
			NodeFactory<K, M, ? extends GenericNonBottomTypeNode.Min<K, M, KI, I>> nonBottomNodeFactory,
			UpdateableGenericNodeStore<K, M, GenericNonBottomTypeNode.Min<K, M, KI, I>> nonBottomNodeStore,
			NodeFactory<KI, I, ? extends GenericNonBottomInstanceNode.Min<K, M, KI, I>> instanceNodeFactory,
			UpdateableGenericNodeStore<KI, I, GenericNonBottomInstanceNode.Min<K, M, KI, I>> instanceNodeStore,
			Map<K, M> defaultTopMembers,
			GenericBottomTypeNode.Min<K, M, KI, I> bottomTypeNode) {
		super(nonBottomNodeFactory, nonBottomNodeStore, instanceNodeFactory,
				instanceNodeStore, defaultTopMembers, bottomTypeNode);
	}

	@Override
	Set<? extends GenericTypeNode.Min<K, M, KI, I>> convertNonBottomNodes(
			Set<? extends GenericNonBottomTypeNode.Min<K, M, KI, I>> nodes) {
		return nodes;
	}

	@Override
	GenericTypeNode.Min<K, M, KI, I> convertNonBottomNode(
			GenericNonBottomTypeNode.Min<K, M, KI, I> node) {
		return node;
	}

	@Override
	GenericTypeNode.Min<K, M, KI, I> convertBottomNode(
			GenericBottomTypeNode.Min<K, M, KI, I> bottomNode) {
		return bottomNode;
	}

	@Override
	Set<? extends GenericInstanceNode.Min<K, M, KI, I>> convertNonBottomInstanceNodes(
			Set<? extends GenericNonBottomInstanceNode.Min<K, M, KI, I>> nodes) {
		return nodes;
	}

	@Override
	GenericInstanceNode.Min<K, M, KI, I> convertNonBottomInstanceNode(
			GenericNonBottomInstanceNode.Min<K, M, KI, I> node) {
		return node;
	}
		
	
}
