package org.semanticweb.elk.reasoner.taxonomy;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.semanticweb.elk.reasoner.taxonomy.nodes.Node;
import org.semanticweb.elk.util.collections.ArrayHashMap;
import org.semanticweb.elk.util.collections.ArrayHashSet;

/**
 * A thread-safe implementation of {@link GenericNodeStore} in which all access
 * methods are synchronized
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <K>
 *            the type of the keys for the node members
 * @param <M>
 *            the type of node members
 * @param <N>
 *            the type of the {@link Node}s stored in this
 *            {@link AbstractNodeStore}
 */
public class SynchronizedNodeStore<K, T, N extends Node<K, T>> implements
		UpdateableGenericNodeStore<K, T, N> {

	/**
	 * stores nodes by elements
	 */
	private final Map<K, N> nodeLookup_;

	/**
	 * the set of all nodes
	 */
	private final Set<N> allNodes_;

	public SynchronizedNodeStore() {
		this.nodeLookup_ = new ArrayHashMap<K, N>(127);
		this.allNodes_ = new ArrayHashSet<N>(127);
	}

	@Override
	public synchronized Set<N> getNodes() {
		return Collections.unmodifiableSet(allNodes_);
	}

	@Override
	public synchronized N getNode(K key) {
		return nodeLookup_.get(key);
	}

	@Override
	public synchronized N putIfAbsent(N node) {
		Iterator<K> keyIterator = node.getMembersLookup().keySet().iterator();
		// compute the minimal key according to the comparator
		K firstKey = keyIterator.next();
		N result = nodeLookup_.get(firstKey);
		if (result != null)
			return result;
		// else, there is no node assignment
		allNodes_.add(node);
		for (K key : node.getMembersLookup().keySet()) {
			N previous = nodeLookup_.put(key, node);
			if (previous != null)
				throw new IllegalArgumentException(key
						+ ": is already assigned with node " + node);
		}
		return null;
	}

}
