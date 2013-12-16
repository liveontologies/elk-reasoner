package org.semanticweb.elk.reasoner.taxonomy.nodes.wrap;

import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericNonBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTaxonomyNode;
import org.semanticweb.elk.util.collections.Operations.FunctorEx;

public class NonBottomTypeNodeConverter<K, M, 
			WN extends GenericTaxonomyNode<K, M, WN>,
			KI, I>
	implements
	FunctorEx<GenericNonBottomTaxonomyNode<K, M, WN, ?>, GenericTypeNodeWrap.Min<K, M, WN, ?, KI, I>> {

	private final NonBottomTypeNodeWrapper.Min<K, M, WN, KI, I> wrapper_;
	
	public NonBottomTypeNodeConverter(NonBottomTypeNodeWrapper.Min<K, M, WN, KI, I> wrapper) {
		this.wrapper_ = wrapper;
	}
	
	<WNB extends GenericNonBottomTaxonomyNode<K, M, WN, WNB>> 
	GenericTypeNodeWrap.Min<K, M, WN, ?, KI, I>
	convert(GenericNonBottomTaxonomyNode<K, M, WN, WNB> element) {
		return new NonBottomTypeNodeWrap<K, M, WN, WNB, GenericNonBottomTaxonomyNode<K, M, WN, WNB>, KI, I>(element, wrapper_);
	}
	
	@Override					
	public 
	GenericTypeNodeWrap.Min<K, M, WN, ?, KI, I> apply(
			GenericNonBottomTaxonomyNode<K, M, WN, ?> element) {
		return convert(element);						
	}

	@Override
	public Object reverse(Object element) {
		if (element instanceof GenericTypeNodeWrap.Min<?, ?, ?, ?, ?, ?>) {
			return ((GenericTypeNodeWrap.Min<?, ?, ?, ?, ?, ?>) element)
					.getWrappedNode();
		}
		// else
		return null;
	}
}