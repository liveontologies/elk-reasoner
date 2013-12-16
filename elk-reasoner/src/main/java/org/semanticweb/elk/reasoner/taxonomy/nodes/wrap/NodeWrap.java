package org.semanticweb.elk.reasoner.taxonomy.nodes.wrap;

import org.semanticweb.elk.reasoner.taxonomy.nodes.Node;

public interface NodeWrap<K, M, W extends Node<K, M>> extends
		Node<K, M> {

	W getWrappedNode();

}
