package org.semanticweb.elk.reasoner.taxonomy.nodes.wrap;

import java.util.Set;

import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericNonBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTaxonomyNodeVisitor;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.collections.Operations.FunctorEx;

public class TaxonomyNodeWrapperImpl<K, M, 
			WN extends GenericTaxonomyNode<K, M, WN>>
		implements
		GenericTaxonomyNodeVisitor<K, M, WN, GenericTaxonomyNodeWrap.Min<K, M, WN, ?>>,
		TaxonomyNodeWrapper.Min<K, M, WN> {

	private final TaxonomyNodeConverter nodeConverter_ = new TaxonomyNodeConverter();
	
	@Override
	public Set<? extends GenericTaxonomyNodeWrap.Min<K, M, WN, ?>> convertNodes(
			Set<? extends WN> taxonomyNodes) {
		return Operations.map(taxonomyNodes, nodeConverter_);
	}

	@Override
	public <WNB extends GenericNonBottomTaxonomyNode<K, M, WN, WNB>> GenericTaxonomyNodeWrap.Min<K, M, WN, ?> visit(
			GenericNonBottomTaxonomyNode<K, M, WN, WNB> node) {
		return new NonBottomTaxonomyNodeWrap<K, M, WN, WNB>(node, this);
	}

	@Override
	public GenericTaxonomyNodeWrap.Min<K, M, WN, ?> visit(
			GenericBottomTaxonomyNode<K, M, WN> node) {
		return new BottomTaxonomyNodeWrap<K, M, WN>(node, this);
	}

	class TaxonomyNodeConverter
	implements
	FunctorEx<WN, GenericTaxonomyNodeWrap.Min<K, M, WN, ?>> {

		@Override
		public GenericTaxonomyNodeWrap.Min<K, M, WN, ?> apply(WN element) {
			GenericTaxonomyNodeWrap.Min<K, M, WN, ?> result = element
					.accept(TaxonomyNodeWrapperImpl.this);
			return result;
		}

		@Override
		public Object reverse(Object element) {
			if (element instanceof GenericTaxonomyNodeWrap.Min<?,?,?,?>) {
				return ((GenericTaxonomyNodeWrap.Min<?,?,?,?>) element).getWrappedNode();
			}
			// else
			return null;
		}

	} 
	
}
