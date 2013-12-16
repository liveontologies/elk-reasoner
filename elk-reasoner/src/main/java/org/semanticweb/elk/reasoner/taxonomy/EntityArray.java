package org.semanticweb.elk.reasoner.taxonomy;

import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.iris.ElkIri;

/**
 * An unmodifiable {@link Map} view of a given {@link ElkEntity} elements using
 * {@link ElkIri} as keys backed by a sorted {@link ElkEntity} array to optimize
 * memory consumption.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <E>
 *            the type of elements stored in this {@link EntityArray}
 * 
 * @see EntityEntry
 */
public class EntityArray<E extends ElkEntity> extends AbstractMap<ElkIri, E>
		implements Map<ElkIri, E> {

	/**
	 * the sorted set of elements backing this {@link EntityArray}
	 */
	private final E[] data_;

	@SuppressWarnings("unchecked")
	public EntityArray(Collection<E> elements) {
		this.data_ = (E[]) elements.toArray();
		Collections.sort(new EntryList());
	}

	@Override
	public E get(Object key) {
		return data_[getIndex(key)];
	}

	@Override
	public boolean containsKey(Object key) {
		return getIndex(key) >= 0;
	}

	@Override
	public Set<Map.Entry<ElkIri, E>> entrySet() {
		return new AbstractSet<Map.Entry<ElkIri, E>>() {

			@Override
			public boolean contains(Object o) {
				if (!(o instanceof Map.Entry<?, ?>))
					return false;
				Object key = ((Map.Entry<?, ?>) o).getKey();
				if (!(key instanceof ElkIri))
					return false;
				return getIndex(key) >= 0;
			}

			@Override
			public Iterator<Map.Entry<ElkIri, E>> iterator() {
				return new Iterator<Map.Entry<ElkIri, E>>() {
					// current cursor
					private int cursor_ = 0;

					@Override
					public boolean hasNext() {
						return cursor_ < data_.length;
					}

					@Override
					public Map.Entry<ElkIri, E> next() {
						return new EntityEntry<E>(data_[cursor_++]);
					}

					@Override
					public void remove() {
						throw new UnsupportedOperationException(
								"The set cannot be modified");
					}
				};
			}

			@Override
			public int size() {
				return data_.length;
			}

		};
	}

	/**
	 * a view of elements in this {@link EntityArray} as a list of
	 * {@link ElkIri}s
	 * 
	 * @author "Yevgeny Kazakov"
	 */
	private class KeyList extends AbstractList<ElkIri> implements List<ElkIri> {

		@Override
		public ElkIri get(int index) {
			return EntityArray.this.get(index).getIri();
		}

		@Override
		public int size() {
			return EntityArray.this.size();
		}

	}

	/**
	 * a view of elements in this {@link EntityArray} as a list of
	 * {@link EntityEntry}s
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	private class EntryList extends AbstractList<EntityEntry<E>> implements
			List<EntityEntry<E>> {

		@Override
		public EntityEntry<E> get(int index) {
			return new EntityEntry<E>(data_[index]);
		}

		@Override
		public int size() {
			return data_.length;
		}

		@Override
		public EntityEntry<E> set(int index, EntityEntry<E> element) {
			EntityEntry<E> previous = new EntityEntry<E>(data_[index]);
			data_[index] = element.getValue();
			return previous;
		}
	}

	private int getIndex(Object key) {
		if (key instanceof ElkIri)
			return Collections.binarySearch(new KeyList(), (ElkIri) key);
		return -1;
	}
}
