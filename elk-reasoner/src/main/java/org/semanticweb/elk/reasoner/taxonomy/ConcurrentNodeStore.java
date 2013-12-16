package org.semanticweb.elk.reasoner.taxonomy;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.semanticweb.elk.reasoner.taxonomy.nodes.Node;
import org.semanticweb.elk.reasoner.taxonomy.nodes.NodeFactory;

/**
 * A thread-safe implementation of {@link GenericNodeStore}
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
public class ConcurrentNodeStore<K extends Comparable<K>, M, N extends Node<K, M>>
		implements UpdateableGenericNodeStore<K, M, N> {

	/**
	 * stores nodes by elements
	 */
	private final ConcurrentMap<K, N> nodeLookup_;

	/**
	 * the set of all nodes
	 */
	private final Set<N> allNodes_;

	/**
	 * Create a new {@link ConcurrentNodeStore} that uses a given
	 * {@link NodeFactory} for creating the {@link Node}s. The
	 * {@link NodeFactory} must be thread safe.
	 * 
	 * @param nodeFactory
	 *            a thread safe {@link NodeFactory} for creating new
	 *            {@link Node}s
	 */
	public ConcurrentNodeStore() {
		this.nodeLookup_ = new ConcurrentHashMap<K, N>();
		this.allNodes_ = Collections
				.newSetFromMap(new ConcurrentHashMap<N, Boolean>());
	}

	@Override
	public Set<N> getNodes() {
		return Collections.unmodifiableSet(allNodes_);
	}

	@Override
	public N getNode(K key) {
		return nodeLookup_.get(key);
	}

	@Override
	public N putIfAbsent(N node) {
		Iterator<K> keyIterator = node.getMembersLookup().keySet().iterator();
		// compute the minimal key according to the comparator
		K minimalKey = keyIterator.next();
		while (keyIterator.hasNext()) {
			K nextKey = keyIterator.next();
			if (nextKey.compareTo(minimalKey) < 0)
				minimalKey = nextKey;
		}
		// first we put the assignment for the minimal key to avoid concurrency
		// problems
		N result = nodeLookup_.putIfAbsent(minimalKey, node);
		if (result != null)
			return result;
		allNodes_.add(node);
		for (K key : node.getMembersLookup().keySet()) {
			if (key != minimalKey) {
				N previous = nodeLookup_.put(key, node);
				if (previous != null)
					throw new IllegalArgumentException(key
							+ ": is already assigned with node " + node);
			}
		}
		return null;
	}

}
