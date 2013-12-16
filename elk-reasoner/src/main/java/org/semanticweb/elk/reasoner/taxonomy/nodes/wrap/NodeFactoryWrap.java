package org.semanticweb.elk.reasoner.taxonomy.nodes.wrap;

import java.util.Map;

import org.semanticweb.elk.reasoner.taxonomy.nodes.Node;
import org.semanticweb.elk.reasoner.taxonomy.nodes.NodeFactory;
import org.semanticweb.elk.util.collections.Operations.FunctorEx;

public class NodeFactoryWrap<K, M, WN extends Node<K, M>, N extends NodeWrap<K, M, WN>>
		implements NodeFactory<K, M, N> {

	private final NodeFactory<K, M, ? extends WN> wrappedNodeFactory_;

	private final FunctorEx<WN, N> nodeWrapper_;

	public NodeFactoryWrap(NodeFactory<K, M, ? extends WN> wrappedNodeFactory,
			FunctorEx<WN, N> nodeWrapper) {
		this.wrappedNodeFactory_ = wrappedNodeFactory;
		this.nodeWrapper_ = nodeWrapper;
	}

	@Override
	public N createNode(Map<K, M> members) {
		return nodeWrapper_.apply(wrappedNodeFactory_.createNode(members));
	}

}
