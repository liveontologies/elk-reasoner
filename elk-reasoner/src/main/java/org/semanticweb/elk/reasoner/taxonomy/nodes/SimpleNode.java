package org.semanticweb.elk.reasoner.taxonomy.nodes;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

/**
 * A simple {@link Node} constructed from an assignment of members by keys
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <K>
 *            the type of the keys for the node members
 * @param <M>
 *            the type of node members
 */
public class SimpleNode<K, M> implements Node<K, M> {

	/**
	 * the map that backs the member assignment of this {@link Node}
	 */
	final Map<K, M> membersLookup;

	/**
	 * Constructs a new {@link SimpleNode} with the given assignment of members
	 * by keys. This set must not be modified after the construction.
	 * 
	 * @param members
	 *            the assignment of members by keys to store in this
	 *            {@link Node}
	 */
	public SimpleNode(Map<K, M> members) {
		this.membersLookup = members;
	}

	@Override
	public Map<K, M> getMembersLookup() {
		return Collections.unmodifiableMap(membersLookup);
	}

	@Override
	public String toString() {
		return Arrays.toString(membersLookup.values().toArray());
	}

}
