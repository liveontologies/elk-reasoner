package org.semanticweb.elk.reasoner.taxonomy;

import java.util.Map;
import java.util.Set;

import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericBottomTypeNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericInstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericNonBottomInstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericNonBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericNonBottomTypeNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTypeNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.NodeFactory;

abstract class AbstractUpdateableGenericInstanceTaxonomyWrap<K, M, KI, I,
			WN extends GenericTaxonomyNode<K, M, WN>,
			WNB extends GenericNonBottomTaxonomyNode<K, M, WN, WNB>,
			WBN extends GenericBottomTaxonomyNode<K, M, WN>,
			TN extends GenericTypeNode<K, M, KI, I, TN, IN>, 
			TNB extends GenericNonBottomTypeNode<K, M, KI, I, TN, TNB, IN, INB>,
			TBN extends GenericBottomTypeNode<K, M, KI, I, TN, IN>,
			IN extends GenericInstanceNode<K, M, KI, I, TN, IN>,
			INB extends GenericNonBottomInstanceNode<K, M, KI, I, TN, TNB, IN, INB>> 		
		implements 
		UpdateableGenericInstanceTaxonomy<K, M, KI, I, TN, IN> {
	
	/**
	 * The {@link NodeFactory} used to create instance nodes
	 */
	private final NodeFactory<KI, I, ? extends INB> instanceNodeFactory_;

	/**
	 * An {@link UpdateableGenericNodeStore} used to store instance nodes
	 */
	private final UpdateableGenericNodeStore<KI, I, INB> instanceNodeStore_;

	public AbstractUpdateableGenericInstanceTaxonomyWrap(
			NodeFactory<KI, I, ? extends INB> instanceNodeFactory,
			UpdateableGenericNodeStore<KI, I, INB> instanceNodeStore) {
		this.instanceNodeFactory_ = instanceNodeFactory;
		this.instanceNodeStore_ = instanceNodeStore;
	}
		
	abstract WrappableGenericTaxonomy<K, M, WN, WNB, WBN> getWrappedTaxonomy();
	
	abstract TNB getNonBottomTypeNode(WNB node);
	
	abstract TBN getBottomTypeNode(WBN node);
	
	abstract TN getTypeNode(WN node);
	
	abstract Set<? extends TN> getTypeNodes(Set<? extends WN> nodes);
	
	abstract Set<? extends IN> convertNonBottomInstanceNodes(Set<? extends INB> nodes);
	
	abstract IN convertNonBottomInstanceNode(INB node);	
	
	@Override
	public TN getTopNode() {
		return getTypeNode(getWrappedTaxonomy().getTopNode());
	}

	@Override
	public TN getBottomNode() {
		return getTypeNode(getWrappedTaxonomy().getBottomNode());
	}

	@Override
	public Set<? extends TN> getNodes() {
		return getTypeNodes(getWrappedTaxonomy().getNodes());
	}

	@Override
	public TN getNode(K key) {
		WN wrappedNode = getWrappedTaxonomy().getNode(key);
		if (wrappedNode == null)
			return null;
		// else
		return getTypeNode(wrappedNode);
	}

	@Override
	public M addBottomMember(K key, M member) {
		return getWrappedTaxonomy().addBottomMember(key, member);
	}
	
	@Override
	public Set<? extends IN> getInstanceNodes() {
		return convertNonBottomInstanceNodes(instanceNodeStore_.getNodes());
	}

	@Override
	public IN getInstanceNode(KI key) {
		return convertNonBottomInstanceNode(instanceNodeStore_.getNode(key));
	}

	@Override
	public void setDirectTypes(Map<KI, I> members,
			Iterable<? extends Map<K, M>> superMemberSets) {
		INB instanceNode = getCreateInstanceNode(members);
		for (Map<K, M> superMembers : superMemberSets) {
			TNB typeNode = getNonBottomTypeNode(getWrappedTaxonomy()
					.getCreateNonBottomNode(superMembers));
			addDirectTypeNode(instanceNode, typeNode);
		}
		if (instanceNode.getDirectTypeNodes().isEmpty()) {
			// if there are no type nodes, the top node for default top members
			// is the only type node
			TNB topNode = getNonBottomTypeNode(getWrappedTaxonomy()
					.getCreateTopNode());
			addDirectTypeNode(instanceNode, topNode);
		}
	}

	@Override
	public void setDirectRelations(Map<K, M> members,
			Iterable<? extends Map<K, M>> superMemberSets) {

	}

	@Override
	public I addBottomInstance(KI key, I instance) {
		return getBottomTypeNode(getWrappedTaxonomy().getCreateBottomNode())
				.addBottomInstance(key, instance);
	}
	
	INB getCreateInstanceNode(Map<KI, I> members) {
		return UpdateableGenericNodeStore.Helper.getCreateNode(
				instanceNodeStore_, instanceNodeFactory_, members);
	}

	private void addDirectTypeNode(INB instanceNode, TNB typeNode) {
		instanceNode.addDirectTypeNode(typeNode);
		typeNode.addDirectInstanceNode(instanceNode);
	}	
	
}
