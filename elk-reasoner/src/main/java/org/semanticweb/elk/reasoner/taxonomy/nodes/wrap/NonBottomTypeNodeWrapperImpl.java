package org.semanticweb.elk.reasoner.taxonomy.nodes.wrap;

import java.util.Set;

import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericNonBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTaxonomyNode;
import org.semanticweb.elk.util.collections.ConcurrentHashSetMultimap;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.collections.Operations.FunctorEx;
import org.semanticweb.elk.util.collections.SetMultimap;

public class NonBottomTypeNodeWrapperImpl<K, M, 
			WN extends GenericTaxonomyNode<K, M, WN>,
			KI, I> 
	implements NonBottomTypeNodeWrapper.Min<K, M, WN, KI, I> {

	private final SetMultimap<GenericTaxonomyNode<K, M, WN>, GenericInstanceNodeWrap.Min<K, M, WN, KI, I>> directInstances_;
	
	private final FunctorEx<WN, GenericTypeNodeWrap.Min<K, M, WN, ?, KI, I>> nodeConverter_;
	
	public NonBottomTypeNodeWrapperImpl(
			FunctorEx<WN, GenericTypeNodeWrap.Min<K, M, WN, ?, KI, I>> nodeConverter) {		
		this.directInstances_ = new ConcurrentHashSetMultimap<GenericTaxonomyNode<K, M, WN>, GenericInstanceNodeWrap.Min<K, M, WN, KI, I>>(
				128);
		this.nodeConverter_ = nodeConverter;
	}
	
	@Override
	public Set<? extends GenericInstanceNodeWrap.Min<K, M, WN, KI, I>> getDirectInstanceNodes(
			GenericTaxonomyNode<K, M, WN> node) {
		return directInstances_.get(node);
	}

	@Override	
	public 
	<WNB extends GenericNonBottomTaxonomyNode<K, M, WN, WNB>>
	boolean addDirectInstanceNode(
			GenericTaxonomyNode<K, M, WN> node,
			GenericNonBottomInstanceNodeWrap.Min<K, M, WN, WNB, KI, I> instanceNode) {
		return directInstances_.add(node, instanceNode);
	}
	
	@Override	
	public 
	<WNB extends GenericNonBottomTaxonomyNode<K, M, WN, WNB>>
	boolean removeDirectInstanceNode(
			GenericTaxonomyNode<K, M, WN> node,
			GenericNonBottomInstanceNodeWrap.Min<K, M, WN, WNB, KI, I> instanceNode) {
		return directInstances_.remove(node, instanceNode);
	}
	
	@Override
	public Set<? extends GenericTypeNodeWrap.Min<K, M, WN, ?, KI, I>> convertNodes(
			Set<? extends WN> taxonomyNodes) {
		return Operations.map(taxonomyNodes, nodeConverter_);
	}
				
}


