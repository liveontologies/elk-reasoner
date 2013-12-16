package org.semanticweb.elk.reasoner.taxonomy;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericNonBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.NodeFactory;
import org.semanticweb.elk.util.collections.LazySetUnion;

abstract class AbstractWrappableGenericTaxonomy<K, M, 
			N extends GenericTaxonomyNode<K, M, N>,
			NB extends GenericNonBottomTaxonomyNode<K, M, N, NB>,
			BN extends GenericBottomTaxonomyNode<K, M, N>>
	implements
	WrappableGenericTaxonomy<K, M, N, NB, BN> {

	/**
	 * The {@link NodeFactory} used to create nodes
	 */
	private final NodeFactory<K, M, ? extends NB> nonBottomNodeFactory_;

	/**
	 * An {@link UpdateableGenericNodeStore} used to store nodes
	 */
	private final UpdateableGenericNodeStore<K, M, NB> nonBottomNodeStore_;

	/**
	 * The default members of the top node; will be used to create the top node
	 * in case no other top members are found
	 */
	private final Map<K, M> defaultTopMembers_;
	
	private final BN bottomNode_;

	/**
	 * some key of the member that must be in the top node
	 */
	private final K topKey_;

	public AbstractWrappableGenericTaxonomy(
			NodeFactory<K, M, ? extends NB> nonBottomNodeFactory,
			UpdateableGenericNodeStore<K, M, NB> nonBottomNodeStore,
			Map<K, M> defaultTopMembers,
			BN bottomNode) {
		this.nonBottomNodeFactory_ = nonBottomNodeFactory;
		this.nonBottomNodeStore_ = nonBottomNodeStore;
		this.defaultTopMembers_ = defaultTopMembers;
		this.topKey_ = defaultTopMembers.entrySet().iterator().next().getKey();
		this.bottomNode_ = bottomNode;
	}
	
abstract Set<? extends N> convertNonBottomNodes(Set<? extends NB> nodes);
	
abstract N convertNonBottomNode(NB node);

abstract N convertBottomNode(BN bottomNode);
	
	@Override
	public Set<? extends N> getNodes() {
		return new LazySetUnion<N>(convertNonBottomNodes(nonBottomNodeStore_.getNodes()),
				Collections.singleton(convertBottomNode(bottomNode_)));
	}

	@Override
	public N getNode(K key) {
		N result = convertNonBottomNode(nonBottomNodeStore_.getNode(key));
		if (result != null)
			return result;
		if (bottomNode_.getMembersLookup().get(key) != null)
			return convertBottomNode(bottomNode_);
		// otherwise, the node is not found
		return null;		
	}

	@Override
	public void setDirectRelations(Map<K, M> members,
			Iterable<? extends Map<K, M>> superMemberSets) {

		NB node = getCreateNonBottomNode(members);
		for (Map<K, M> superMembers : superMemberSets) {
			NB superNode = getCreateNonBottomNode(superMembers);
			addDirectSuperNode(node, superNode);
		}
		if (node.getDirectSuperNodes().isEmpty()) {
			if (node.getMembersLookup().get(topKey_) != null) {
				// this is a top node
				return;
			}
			// else use the default top member to create the top node
			// and set relationship with that
			NB topNode = getCreateTopNode();
			addDirectSuperNode(node, topNode);
		}
	}

	@Override
	public N getTopNode() {
		return getNode(topKey_);
	}
	
	@Override
	public N getBottomNode() {
		return convertBottomNode(bottomNode_);
	}

	@Override
	public M addBottomMember(K key, M member) {
		return bottomNode_.addMember(key, member);
	}

	@Override
	public NB getCreateNonBottomNode(Map<K, M> members) {
		return UpdateableGenericNodeStore.Helper.getCreateNode(nonBottomNodeStore_,
				nonBottomNodeFactory_, members);
	}
	
	@Override
	public NB getCreateTopNode() {
		return getCreateNonBottomNode(defaultTopMembers_);
	}

	@Override
	public BN getCreateBottomNode() {
		return bottomNode_;
	}
	
	private void addDirectSuperNode(NB subNode, NB superNode) {
		subNode.addDirectSuperNode(superNode);
		superNode.addDirectSubNode(subNode);
	}

}
