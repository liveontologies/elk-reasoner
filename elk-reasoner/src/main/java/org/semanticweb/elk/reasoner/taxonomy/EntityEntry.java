package org.semanticweb.elk.reasoner.taxonomy;

import java.util.Map;

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.iris.ElkIri;

/**
 * A {@link Map.Entry} view of an {@link ElkEntity}; the {@link ElkIri} of this
 * {@link ElkEntity} is considered as a key and the {@link ElkEntity} itself as
 * a value
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <E>
 *            the type of the wrapped element
 * 
 * @see EntityArray
 */
public class EntityEntry<E extends ElkEntity> implements Map.Entry<ElkIri, E>,
		Comparable<EntityEntry<E>> {

	private final E entity_;

	public EntityEntry(E entity) {
		this.entity_ = entity;
	}

	@Override
	public ElkIri getKey() {
		return entity_.getIri();
	}

	@Override
	public E getValue() {
		return entity_;
	}

	@Override
	public E setValue(E value) {
		throw new UnsupportedOperationException("Entity cannot be modified");
	}

	@Override
	public int hashCode() {
		return entity_.getIri().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof EntityEntry<?>)
			return ((EntityEntry<?>) o).entity_.getIri().equals(
					entity_.getIri());
		return false;
	}

	@Override
	public int compareTo(EntityEntry<E> o) {
		return o.entity_.getIri().compareTo(entity_.getIri());
	}
}
