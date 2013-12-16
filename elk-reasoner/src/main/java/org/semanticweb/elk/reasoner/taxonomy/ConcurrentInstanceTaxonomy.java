package org.semanticweb.elk.reasoner.taxonomy;

import java.util.Map;
import java.util.Set;

import org.semanticweb.elk.reasoner.taxonomy.nodes.InstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.TaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.TypeNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.wrap.BottomInstanceNodeWrap;
import org.semanticweb.elk.reasoner.taxonomy.nodes.wrap.GenericInstanceNodeWrap;
import org.semanticweb.elk.reasoner.taxonomy.nodes.wrap.NonBottomInstanceNodeWrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A thread-safe implementation of an {@link UpdateableInstanceTaxonomy}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <K>
 *            the type of the keys for the node members
 * @param <M>
 *            the type of node members
 * @param <KI>
 *            the type of the keys for the node instances
 * @param <I>
 *            the type of instances
 */
public class ConcurrentInstanceTaxonomy<K extends Comparable<K>, M, KI extends Comparable<KI>, I>
		implements UpdateableInstanceTaxonomy<K, M, KI, I> {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ConcurrentInstanceTaxonomyOld.class);

	/**
	 * The {@link Taxonomy} backed by this {@link ConcurrentInstanceTaxonomyOld};
	 * it is assumed to be computed already
	 */
	private Taxonomy<K, M> taxonomy_;

	final UpdateableGenericNodeStore<KI, I, NonBottomInstanceNodeWrap<K, M, KI, I>> instanceNodeStore_;

	final BottomInstanceNodeWrap<K, M, KI, I> bottomInstanceNode;
	/**
	 * thread safe assignment of {@link TaxonomyNode}s to
	 * {@link GenericInstanceNodeWrap}s
	 */
	private final SetMultimap<TaxonomyNode<K, M>, GenericInstanceNodeWrap<K, M, KI, I>> instancesByNodes_;

	public ConcurrentInstanceTaxonomy(Taxonomy<K, M> taxonomy) {
		this.taxonomy_ = taxonomy;
		this.instancesByNodes_ = new ConcurrentHashSetMultimap<TaxonomyNode<K, M>, GenericInstanceNodeWrap<K, M, KI, I>>();
		this.instanceNodeStore_ = new ConcurrentNodeStore<KI, I, NonBottomInstanceNodeWrap<K, M, KI, I>>(
				new NonBottomInstanceNodeWrapFactory<K, M, KI, I>(
						instancesByNodes_));
		this.bottomInstanceNode = new BottomInstanceNodeWrap<K, M, KI, I>(
				taxonomy_.getBottomNode(), instancesByNodes_);
	}

	@Override
	public Set<? extends TypeNode<K, M, KI, I>> getNodes() {
		return TypeNodeWrap.convert(taxonomy_.getNodes(), instancesByNodes_);
	}

	@Override
	public TypeNode<K, M, KI, I> getNode(K memberKey) {
		return new TypeNodeWrap<K, M, KI, I>(taxonomy_.getNode(memberKey),
				instancesByNodes_);
	}

	@Override
	public TypeNode<K, M, KI, I> getTopNode() {
		return new TypeNodeWrap<K, M, KI, I>(taxonomy_.getTopNode(),
				instancesByNodes_);
	}

	@Override
	public TypeNode<K, M, KI, I> getBottomNode() {
		return new TypeNodeWrap<K, M, KI, I>(taxonomy_.getBottomNode(),
				instancesByNodes_);
	}

	@Override
	public Set<? extends InstanceNode<K, M, KI, I>> getInstanceNodes() {
		return instanceNodeStore_.getNodes();
	}

	@Override
	public InstanceNode<K, M, KI, I> getInstanceNode(KI instanceKey) {
		return instanceNodeStore_.getNode(instanceKey);
	}

	@Override
	public void setDirectTypes(Map<KI, I> members,
			Iterable<? extends Map<K, M>> superMemberSets) {
		NonBottomInstanceNodeWrap<K, M, KI, I> node = instanceNodeStore_
				.getCreateNode(members);
		for (Map<K, M> superMembers : superMemberSets) {
			K member = superMembers.keySet().iterator().next();
			TaxonomyNode<K, M> superNode = taxonomy_.getNode(member);
			addInstance(superNode, node);
		}
		if (!superMemberSets.iterator().hasNext()) {
			// no told super-members, assign the top node
			addInstance(taxonomy_.getTopNode(), node);
		}
	}

	@Override
	public I addBottomInstance(KI key, I instance) {
		return bottomInstanceNode.addInstance(key, instance);
	}

	private void addInstance(TaxonomyNode<K, M> node,
			NonBottomInstanceNodeWrap<K, M, KI, I> instanceNode) {
		if (instancesByNodes_.add(node, instanceNode)
				&& LOGGER_.isTraceEnabled()) {
			LOGGER_.trace(node + ": new direct instance " + instanceNode);
		}
		instanceNode.addDirectTypeNode(node);
	}
}
