package org.semanticweb.elk.reasoner.taxonomy.nodes.wrap;

import java.util.Set;

import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTaxonomyNode;

abstract class AbstractGenericBottomTypeNodeWrap<K, M, 
			WN extends GenericTaxonomyNode<K, M, WN>,
			W extends GenericBottomTaxonomyNode<K, M, WN>,
			KI, I,
			TN extends GenericTypeNodeWrap<K, M, WN, ?, KI, I, TN, IN>,
			IN extends GenericInstanceNodeWrap<K, M, WN, KI, I, TN, IN>,
			WP extends BottomTypeNodeWrapper<K, M, WN, KI, I, TN, IN>>

	extends
	AbstractGenericBottomTaxonomyNodeWrap<K, M, WN, W, TN, WP>
	implements 
	GenericBottomTypeNodeWrap<K, M, WN, W, KI, I, TN, IN> {

	AbstractGenericBottomTypeNodeWrap(W node, WP wrapper) {
		super(node, wrapper);
	}

	@Override
	public I addBottomInstance(KI key, I instance) {
		return wrapper.addBottomInstance(key, instance);
	}

	@Override
	public Set<? extends IN> getAllInstanceNodes() {
		return getDirectInstanceNodes();
	}

	@Override
	public Set<? extends IN> getDirectInstanceNodes() {
		return wrapper.getDirectBottomInstanceNodes();
	}	
	
}
