package org.semanticweb.elk.reasoner.taxonomy;

import java.util.Set;

import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericNonBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.NodeFactory;
import org.semanticweb.elk.reasoner.taxonomy.nodes.wrap.BottomTypeNodeWrap;
import org.semanticweb.elk.reasoner.taxonomy.nodes.wrap.GenericBottomTypeNodeWrap;
import org.semanticweb.elk.reasoner.taxonomy.nodes.wrap.GenericInstanceNodeWrap;
import org.semanticweb.elk.reasoner.taxonomy.nodes.wrap.GenericNonBottomInstanceNodeWrap;
import org.semanticweb.elk.reasoner.taxonomy.nodes.wrap.GenericNonBottomTypeNodeWrap;
import org.semanticweb.elk.reasoner.taxonomy.nodes.wrap.GenericTypeNodeWrap;
import org.semanticweb.elk.reasoner.taxonomy.nodes.wrap.NonBottomTypeNodeWrap;
import org.semanticweb.elk.reasoner.taxonomy.nodes.wrap.TypeNodeConverter;
import org.semanticweb.elk.reasoner.taxonomy.nodes.wrap.TypeNodeWrapperVisitor;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.collections.Operations.FunctorEx;

public class UpdateableInstanceTaxonomyWrap<K, M, KI, I,
			WN extends GenericTaxonomyNode<K, M, WN>,
			WNB extends GenericNonBottomTaxonomyNode<K, M, WN, WNB>,
			WBN extends GenericBottomTaxonomyNode<K, M, WN>> 
		extends
		AbstractUpdateableGenericInstanceTaxonomyWrap<K, M, KI, I, WN, WNB, WBN,
				GenericTypeNodeWrap.Min<K, M, WN, ?, KI, I>,
				GenericNonBottomTypeNodeWrap.Min<K, M, WN, WNB, ? extends WNB, KI, I>,
				GenericBottomTypeNodeWrap.Min<K, M, WN, ?, KI, I>,
				GenericInstanceNodeWrap.Min<K, M, WN, KI, I>,
				GenericNonBottomInstanceNodeWrap.Min<K, M, WN, WNB, KI, I>>

{
	
	private final WrappableGenericTaxonomy<K, M, WN, WNB, WBN> wrappedTaxonomy_;
	
	private final TypeNodeWrapperVisitor<K, M, WN, KI, I> nodeWrapper_;
	
	private final FunctorEx<WN, GenericTypeNodeWrap.Min<K, M, WN, ?, KI, I>> typeNodeConverter_;
	
	public UpdateableInstanceTaxonomyWrap(
			WrappableGenericTaxonomy<K, M, WN, WNB, WBN> wrappedTaxonomy,
			NodeFactory<KI, I, ? extends GenericNonBottomInstanceNodeWrap.Min<K, M, WN, WNB, KI, I>> instanceNodeFactory,
			UpdateableGenericNodeStore<KI, I, GenericNonBottomInstanceNodeWrap.Min<K, M, WN, WNB, KI, I>> instanceNodeStore) {
		super(instanceNodeFactory, instanceNodeStore);
		this.wrappedTaxonomy_ = wrappedTaxonomy;
		this.nodeWrapper_ = new TypeNodeWrapperVisitor<K, M, WN, KI, I>(wrappedTaxonomy.getCreateBottomNode());
		this.typeNodeConverter_ = new TypeNodeConverter<K, M, WN, KI, I>(nodeWrapper_);
	}
		
	@Override
	WrappableGenericTaxonomy<K, M, WN, WNB, WBN> getWrappedTaxonomy() {
		return wrappedTaxonomy_;
	}
	
	@Override
	GenericNonBottomTypeNodeWrap.Min<K, M, WN, WNB, WNB, KI, I> getNonBottomTypeNode(
			WNB node) {
		return new NonBottomTypeNodeWrap<K, M, WN, WNB, WNB, KI, I>(node,
				nodeWrapper_.getNonBottomWrapper());
	}

	@Override
	GenericBottomTypeNodeWrap.Min<K, M, WN, WBN, KI, I> getBottomTypeNode(
			WBN node) {
		return new BottomTypeNodeWrap<K, M, WN, WBN, KI, I>(node,
				nodeWrapper_.getBottomWrapper());
	}

	@Override
	GenericTypeNodeWrap.Min<K, M, WN, ?, KI, I> getTypeNode(
			WN node) {
		return node.accept(nodeWrapper_);
	}

	@Override
	Set<? extends GenericTypeNodeWrap.Min<K, M, WN, ?, KI, I>> getTypeNodes(
			Set<? extends WN> nodes) {
		return Operations.map(nodes, typeNodeConverter_);
	}

	@Override
	Set<? extends GenericInstanceNodeWrap.Min<K, M, WN, KI, I>> convertNonBottomInstanceNodes(
			Set<? extends GenericNonBottomInstanceNodeWrap.Min<K, M, WN, WNB, KI, I>> nodes) {
		return nodes;
	}

	@Override
	GenericInstanceNodeWrap.Min<K, M, WN, KI, I> convertNonBottomInstanceNode(
			GenericNonBottomInstanceNodeWrap.Min<K, M, WN, WNB, KI, I> node) {
		return node;
	}

}
