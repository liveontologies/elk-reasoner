package org.semanticweb.elk.reasoner.taxonomy.nodes.wrap;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTaxonomyNode;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.collections.Operations.FunctorEx;

public class BottomTypeNodeWrapperImpl<K, M, 
			WN extends GenericTaxonomyNode<K, M, WN>,
			KI, I>
	implements
	BottomTypeNodeWrapper.Min<K, M, WN, KI, I> {

	private final GenericBottomTypeNodeWrap.Min<K, M, WN, GenericBottomTaxonomyNode<K, M, WN>, KI, I> bottomTypeNode_;
	
	private final Map<KI, I> bottomInstances_;
	
	private final FunctorEx<WN, GenericTypeNodeWrap.Min<K, M, WN, ?, KI, I>> nodeConverter_;
	
	public BottomTypeNodeWrapperImpl(
			GenericBottomTaxonomyNode<K, M, WN> bottomNode,
			FunctorEx<WN, GenericTypeNodeWrap.Min<K, M, WN, ?, KI, I>> nodeConverter) {
		this.bottomTypeNode_ = new BottomTypeNodeWrap<K, M, WN, GenericBottomTaxonomyNode<K, M, WN>, KI, I>(
				bottomNode, this);
		this.bottomInstances_ = new ConcurrentHashMap<KI, I>(64);
		this.nodeConverter_ = nodeConverter;
	}
	
	@Override
	public I addBottomInstance(KI key, I instance) {
		return bottomTypeNode_.addBottomInstance(key, instance);
	}

	@Override
	public Set<? extends GenericInstanceNodeWrap.Min<K, M, WN, KI, I>> getDirectBottomInstanceNodes() {
		if (bottomInstances_.isEmpty())
			return Collections.emptySet();
		// else
		return Collections
				.singleton(new BottomInstanceNodeWrap<K, M, WN, KI, I>(
						bottomInstances_, bottomTypeNode_));
	}

	@Override
	public Set<? extends GenericTypeNodeWrap.Min<K, M, WN, ?, KI, I>> convertNodes(
			Set<? extends WN> taxonomyNodes) {
		return Operations.map(taxonomyNodes, nodeConverter_);
	}


}
