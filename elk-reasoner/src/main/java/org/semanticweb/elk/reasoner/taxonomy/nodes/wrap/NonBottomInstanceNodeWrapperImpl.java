package org.semanticweb.elk.reasoner.taxonomy.nodes.wrap;

import java.util.Set;

import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericNonBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.TypeNode;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.collections.Operations.FunctorEx;

public class NonBottomInstanceNodeWrapperImpl<K, M,
			WN extends GenericTaxonomyNode<K, M, WN>,			
			KI, I,
			TN extends TypeNode<K, M, KI, I>>
	implements
	NonBottomInstanceNodeWrapper.Min<K, M, WN, KI, I> {

	private final FunctorEx<GenericNonBottomTaxonomyNode<K, M, WN, ?>, 
				GenericTypeNodeWrap.Min<K, M, WN, ?, KI, I>> nonBottomConverter_;
	
	NonBottomInstanceNodeWrapperImpl(
			FunctorEx<GenericNonBottomTaxonomyNode<K, M, WN, ?>, GenericTypeNodeWrap.Min<K, M, WN, ?, KI, I>> nonBottomConverter) {
		this.nonBottomConverter_ = nonBottomConverter;
	}
	
	NonBottomInstanceNodeWrapperImpl(TypeNodeWrapperVisitor<K, M, WN, KI, I> wrapperVisitor) {
		this.nonBottomConverter_ = new NonBottomTypeNodeConverter<K, M, WN, KI, I>(wrapperVisitor.getNonBottomWrapper());		
	}
		
	@Override
	public <WNB extends GenericNonBottomTaxonomyNode<K, M, WN, WNB>> 
	Set<? extends GenericTypeNodeWrap.Min<K, M, WN, ?, KI, I>> convertNonBottomNodes(
			Set<? extends WNB> taxonomyNodes) {		
		return Operations.map(taxonomyNodes, nonBottomConverter_);
	}
		
}
