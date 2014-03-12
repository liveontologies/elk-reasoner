/**
 * 
 */
package org.semanticweb.elk.util.collections;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Simple {@link MultimapQueue} backed by a {@link Multimap}.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class MultimapQueueImpl<Key, Value> implements MultimapQueue<Key, Value> {

	private final Multimap<Key, Value> multimap_;
	
	public MultimapQueueImpl(Multimap<Key, Value> multimap) {
		multimap_ = multimap;
	}
	
	@Override
	public boolean contains(Key key, Value value) {
		return multimap_.contains(key, value);
	}

	@Override
	public boolean add(Key key, Value value) {
		return multimap_.add(key, value);
	}

	@Override
	public Collection<Value> get(Key key) {
		return multimap_.get(key);
	}

	@Override
	public boolean remove(Object key, Object value) {
		return multimap_.remove(key, value);
	}

	@Override
	public Collection<Value> remove(Object key) {
		return multimap_.remove(key);
	}

	@Override
	public boolean isEmpty() {
		return multimap_.isEmpty();
	}

	@Override
	public Set<Key> keySet() {
		return multimap_.keySet();
	}

	@Override
	public void clear() {
		multimap_.clear();
	}

	@Override
	public Entry<Key, Collection<Value>> takeEntry() {
		if (isEmpty()) {
			return null;
		}
		
		Key firstKey = keySet().iterator().next();
		
		return new AbstractMap.SimpleImmutableEntry<Key, Collection<Value>>(firstKey, remove(firstKey));
	}

}
