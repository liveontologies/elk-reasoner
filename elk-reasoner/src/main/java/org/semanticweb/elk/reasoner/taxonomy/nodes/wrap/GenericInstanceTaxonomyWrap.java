package org.semanticweb.elk.reasoner.taxonomy.nodes.wrap;

import java.util.Set;

import org.semanticweb.elk.reasoner.taxonomy.GenericInstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.GenericNodeStore;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericInstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTypeNode;
import org.semanticweb.elk.util.collections.Operations.FunctorEx;

public class GenericInstanceTaxonomyWrap<K, M, KI, I,
			WTN extends GenericTypeNode<K, M, KI, I, WTN, WIN>, 
			WIN extends GenericInstanceNode<K, M, KI, I, WTN, WIN>,
			WT extends GenericInstanceTaxonomy<K, M, KI, I, WTN, WIN>,
			TN extends GenericTypeNode<K, M, KI, I, TN, IN>, 
			IN extends GenericInstanceNode<K, M, KI, I, TN, IN>>
	extends
	GenericTaxonomyWrap<K, M, WTN, WT, TN>
	implements
	GenericInstanceTaxonomy<K, M, KI, I, TN, IN>{

	private final GenericNodeStore<KI, I, IN> instanceNodeStore_;
	
	public GenericInstanceTaxonomyWrap(WT wrappedTaxonomy,
			FunctorEx<WTN, TN> nodeWrapper,
			GenericNodeStore<KI, I, IN> instanceNodeStore) {
		super(wrappedTaxonomy, nodeWrapper);
		this.instanceNodeStore_ = instanceNodeStore;
	}

	@Override
	public Set<? extends IN> getInstanceNodes() {	
		return instanceNodeStore_.getNodes();
	}

	@Override
	public IN getInstanceNode(KI key) {
		return instanceNodeStore_.getNode(key);
	}

}
