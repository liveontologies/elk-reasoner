package org.semanticweb.elk.reasoner.taxonomy.nodes.wrap;

import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericNonBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTaxonomyNodeVisitor;
import org.semanticweb.elk.util.collections.Operations.FunctorEx;

public class TypeNodeWrapperVisitor<K, M, WN extends GenericTaxonomyNode<K, M, WN>, KI, I>
		implements
		GenericTaxonomyNodeVisitor<K, M, WN, GenericTypeNodeWrap.Min<K, M, WN, ?, KI, I>> {

	private final NonBottomTypeNodeWrapper.Min<K, M, WN, KI, I> nonBottomWrapper_;

	private final BottomTypeNodeWrapper.Min<K, M, WN, KI, I> bottomWrapper_;

	public TypeNodeWrapperVisitor(
			NonBottomTypeNodeWrapper.Min<K, M, WN, KI, I> nonBottomWrapper,
			BottomTypeNodeWrapper.Min<K, M, WN, KI, I> bottomWrapper) {
		this.nonBottomWrapper_ = nonBottomWrapper;
		this.bottomWrapper_ = bottomWrapper;				
	}
	
	public TypeNodeWrapperVisitor(GenericBottomTaxonomyNode<K, M, WN> bottomNode) {
		FunctorEx<WN, GenericTypeNodeWrap.Min<K, M, WN, ?, KI, I>> nodeConverter = 
				new TypeNodeConverter<K, M, WN, KI, I>(this);
		this.nonBottomWrapper_ = new NonBottomTypeNodeWrapperImpl<K, M, WN, KI, I>(nodeConverter);
		this.bottomWrapper_ = new BottomTypeNodeWrapperImpl<K, M, WN, KI, I>(bottomNode, nodeConverter);
	}	
	
	@Override
	public <WNB extends GenericNonBottomTaxonomyNode<K, M, WN, WNB>> GenericTypeNodeWrap.Min<K, M, WN, ?, KI, I> visit(
			GenericNonBottomTaxonomyNode<K, M, WN, WNB> node) {
		return new NonBottomTypeNodeWrap<K, M, WN, WNB, GenericNonBottomTaxonomyNode<K, M, WN, WNB>, KI, I>(node,
				nonBottomWrapper_);
	}

	@Override
	public GenericTypeNodeWrap.Min<K, M, WN, ?, KI, I> visit(
			GenericBottomTaxonomyNode<K, M, WN> node) {
		return new BottomTypeNodeWrap<K, M, WN, GenericBottomTaxonomyNode<K, M, WN>, KI, I>(
				node, bottomWrapper_);
	}
	
	public NonBottomTypeNodeWrapper.Min<K, M, WN, KI, I> getNonBottomWrapper() {
		return nonBottomWrapper_;		
	}
	
	public BottomTypeNodeWrapper.Min<K, M, WN, KI, I> getBottomWrapper() {
		return bottomWrapper_;
	}

}
