package org.semanticweb.elk.reasoner.taxonomy.nodes.wrap;

import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTaxonomyNodeVisitor;
import org.semanticweb.elk.util.collections.Operations.FunctorEx;

public class TypeNodeConverter<K, M, 
			WN extends GenericTaxonomyNode<K, M, WN>,
			KI, I> 
	implements
	FunctorEx<WN, GenericTypeNodeWrap.Min<K, M, WN, ?, KI, I>> {

	private final GenericTaxonomyNodeVisitor<K, M, WN, GenericTypeNodeWrap.Min<K, M, WN, ?, KI, I>> wrapperVisitor_;

	public TypeNodeConverter(GenericTaxonomyNodeVisitor<K, M, WN, GenericTypeNodeWrap.Min<K, M, WN, ?, KI, I>> wrapperVisitor) {
		this.wrapperVisitor_ = wrapperVisitor;
	}

	@Override
	public GenericTypeNodeWrap.Min<K, M, WN, ?, KI, I> apply(WN element) {
		return element.accept(wrapperVisitor_);
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