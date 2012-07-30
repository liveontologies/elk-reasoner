package org.semanticweb.elk.util.collections;

/**
 * A multimap whose keys are subsumable elements. The main operation is to find
 * all values associated with the keys subsumed by the given key.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <K>
 *            the type of the keys in this multimap
 * @param <V>
 *            the type of the values in this multimap
 */
public interface SubsumableMultimap<K extends Subsumable<K>, V> {

	/**
	 * Add a given value for a given key to this multimap. There can be several
	 * values added with the same key.
	 * 
	 * @param key
	 *            the key for which to add the value
	 * @param value
	 *            the value to be added for the given key
	 */
	public void add(K key, V value);

	public void remove(K key, V value);

	/**
	 * Return all values associated with the keys that subsume the given key
	 * 
	 * @param key
	 *            the key for which to find the values associated with the
	 *            subsumed keys
	 * 
	 * @return all values associated with the keys that subsume the given key
	 */
	public Iterable<V> getSubsumingValues(K key);

}
