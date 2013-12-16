package org.semanticweb.elk.reasoner.taxonomy;

import java.util.Map;
import java.util.Set;

import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericBottomTypeNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericInstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericNonBottomInstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericNonBottomTypeNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTypeNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.NodeFactory;

abstract class AbstractUpdateableGenericInstanceTaxonomy<K, M, KI, I,
			TN extends GenericTypeNode<K, M, KI, I, TN, IN>, 
			TNB extends GenericNonBottomTypeNode<K, M, KI, I, TN, TNB, IN, INB>,
			TBN extends GenericBottomTypeNode<K, M, KI, I, TN, IN>,
			IN extends GenericInstanceNode<K, M, KI, I, TN, IN>,
			INB extends GenericNonBottomInstanceNode<K, M, KI, I, TN, TNB, IN, INB>>
		extends
		AbstractUpdateableGenericInstanceTaxonomyWrap<K, M, KI, I, TN, TNB, TBN, TN, TNB, TBN, IN, INB> {

	private final WrappableGenericTaxonomy<K, M, TN, TNB, TBN> wrappedTaxonomy_;
	
	public AbstractUpdateableGenericInstanceTaxonomy(
			NodeFactory<K, M, ? extends TNB> nonBottomNodeFactory,
			UpdateableGenericNodeStore<K, M, TNB> nonBottomNodeStore,			
			NodeFactory<KI, I, ? extends INB> instanceNodeFactory,
			UpdateableGenericNodeStore<KI, I, INB> instanceNodeStore,
			Map<K, M> defaultTopMembers, TBN bottomNode) {
		super(instanceNodeFactory, instanceNodeStore);
		this.wrappedTaxonomy_ = new WrappedTaxonomy(nonBottomNodeFactory,
				nonBottomNodeStore, defaultTopMembers, bottomNode);
	}

	@Override
	WrappableGenericTaxonomy<K, M, TN, TNB, TBN> getWrappedTaxonomy() {
		return wrappedTaxonomy_;
	}
	
	@Override
	TNB getNonBottomTypeNode(TNB node) {		
		return node;
	}

	@Override
	TBN getBottomTypeNode(TBN node) {
		return node;
	}

	@Override
	TN getTypeNode(TN node) {
		return node;
	}

	@Override
	Set<? extends TN> getTypeNodes(Set<? extends TN> nodes) {
		return nodes;
	}
	
	abstract Set<? extends TN> convertNonBottomNodes(Set<? extends TNB> nodes);

	abstract TN convertNonBottomNode(TNB node);
	
	abstract TN convertBottomNode(TBN bottomNode);
		
	class WrappedTaxonomy
	extends AbstractWrappableGenericTaxonomy<K, M, TN, TNB, TBN>
	implements
	WrappableGenericTaxonomy<K, M, TN, TNB, TBN> {

		public WrappedTaxonomy(
				NodeFactory<K, M, ? extends TNB> nonBottomNodeFactory,
				UpdateableGenericNodeStore<K, M, TNB> nonBottomNodeStore,
				Map<K, M> defaultTopMembers, TBN bottomNode) {
			super(nonBottomNodeFactory, nonBottomNodeStore, defaultTopMembers, bottomNode);			
		}

		@Override
		Set<? extends TN> convertNonBottomNodes(Set<? extends TNB> nodes) {
			return AbstractUpdateableGenericInstanceTaxonomy.this.convertNonBottomNodes(nodes);
		}

		@Override
		TN convertNonBottomNode(TNB node) {
			return AbstractUpdateableGenericInstanceTaxonomy.this.convertNonBottomNode(node);
		}

		@Override
		TN convertBottomNode(TBN bottomNode) {
			return AbstractUpdateableGenericInstanceTaxonomy.this.convertBottomNode(bottomNode);
		}
		
	}

}
