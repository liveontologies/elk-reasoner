package org.semanticweb.elk.reasoner.taxonomy.nodes.wrap;

import java.util.Map;
import java.util.Set;

import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericInstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericNonBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.InstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.SimpleNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.TaxonomyNode;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractGenericNonBottomInstanceNodeWrap<K, M, 
			WN extends GenericTaxonomyNode<K, M, WN>,
			WNB extends GenericNonBottomTaxonomyNode<K, M, WN, WNB>,
			KI, I,
			TN extends GenericTypeNodeWrap<K, M, WN, ?, KI, I, TN, IN>,
			TNB extends GenericNonBottomTypeNodeWrap<K, M, WN, WNB, ? extends WNB, KI, I, TN, TNB, IN, INB>,
			IN extends GenericInstanceNodeWrap<K, M, WN, KI, I, TN, IN>,
			INB extends GenericNonBottomInstanceNodeWrap<K, M, WN, WNB, KI, I, TN, TNB, IN, INB>,
			WP extends TaxonomyNodeWrapper<K, M, WN, TN>>

	extends
	SimpleNode<KI, I>
	implements 
		GenericNonBottomInstanceNodeWrap<K, M, WN, WNB, KI, I, TN, TNB, IN, INB> {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(AbstractGenericNonBottomInstanceNodeWrap.class);

	/**
	 * {@link TaxonomyNode}s that are direct types of this
	 * {@link GenericNonBottomInstanceNodeWrap}. To save memory, we do not keep
	 * the {@link GenericNonBottomTypeNodeWrap} themselves.
	 */
	private final Set<WNB> directTypeNodes_;

	/**
	 * a {@link TaxonomyNodeWrapper} representing an assignment of
	 * {@link InstanceNode}s to {@link TaxonomyNode}s
	 */
	private final NonBottomInstanceNodeWrapper<K, M, WN, KI, I, TN> wrapper_;

	public AbstractGenericNonBottomInstanceNodeWrap(Map<KI, I> members, NonBottomInstanceNodeWrapper<K, M, WN, KI, I, TN> wrapper) {
		super(members);
		this.directTypeNodes_ = new ArrayHashSet<WNB>();
		this.wrapper_ = wrapper;
	}

	@Override
	public synchronized boolean addDirectTypeNode(TNB typeNode) {
		if (!directTypeNodes_.add(typeNode.getWrappedNode()))
			return false;
		LOGGER_.trace("{}: new direct type node {}", this, typeNode);
		return true;
	}

	@Override
	public synchronized boolean removeDirectTypeNode(TNB typeNode) {
		if (!directTypeNodes_.remove(typeNode.getWrappedNode()))
			return false;
		LOGGER_.trace("{}: removed direct type node {}", this, typeNode);
		return true;
	}

	@Override
	public Set<? extends TN> getAllTypeNodes() {
		return GenericInstanceNode.Helper.getAllTypeNodes(this);
	}

	@Override
	public Set<? extends TN> getDirectTypeNodes() {
		return wrapper_.convertNonBottomNodes(directTypeNodes_);
	}
		
}
