package org.semanticweb.elk.reasoner.taxonomy.nodes;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.semanticweb.elk.reasoner.taxonomy.GenericNodeStore;

abstract class AbstractGenericBottomTypeNode<K, M, KI, I, 
			TN extends GenericTypeNode<K, M, KI, I, TN, IN>, 
			IN extends GenericInstanceNode<K, M, KI, I, TN, IN>>
		extends 
		AbstractGenericBottomTaxonomyNode<K, M, TN> 
		implements
		GenericBottomTypeNode<K, M, KI, I, TN, IN> {

	/**
	 * the instance members of this {@link BottomTypeNode}
	 */
	private final Map<KI, I> instanceLookup_;

	public AbstractGenericBottomTypeNode(Map<K, M> members,
			Map<KI, I> instances,
			GenericNodeStore<K, M, ? extends TN> upperTaxonomy) {
		super(members, upperTaxonomy);
		this.instanceLookup_ = instances;
	}

	@Override
	public I addBottomInstance(KI key, I instance) {
		return instanceLookup_.put(key, instance);
	}

	@Override
	public Set<? extends IN> getAllInstanceNodes() {
		return getDirectInstanceNodes();
	}

	@Override
	public Set<? extends IN> getDirectInstanceNodes() {
		if (instanceLookup_.isEmpty())
			return Collections.emptySet();
		// else
		return Collections.singleton(createBottomInstanceNode(instanceLookup_));
	}

	/**
	 * Creates a {@link GenericInstanceNode} with the given instances that have
	 * this {@link GenericTypeNode} as the only type node
	 * 
	 * @param instanceLookup
	 * @return
	 */
	abstract IN createBottomInstanceNode(Map<KI, I> instances);

}
