package org.semanticweb.elk.reasoner.indexing.collections;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassEntity;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDataHasValue;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.util.collections.Operations;

public abstract class AbstractSplitIndexedClassExpressionMap<V> extends
		AbstractMap<IndexedClassExpression, V> implements
		SplitIndexedClassExpressionMap<V> {

	public abstract Map<IndexedClassEntity, V> getClassEntitiesMapReadOnly();

	public abstract Map<IndexedClassEntity, V> getClassEntitiesMapReadWrite();

	public abstract Map<IndexedObjectIntersectionOf, V> getObjectIntersectionsMapReadOnly();

	public abstract Map<IndexedObjectIntersectionOf, V> getObjectIntersectionsMapReadWrite();

	public abstract Map<IndexedObjectSomeValuesFrom, V> getObjectExistentialsMapReadOnly();

	public abstract Map<IndexedObjectSomeValuesFrom, V> getObjectExistentialsMapReadWrite();

	public abstract Map<IndexedDataHasValue, V> getDataExistentialsMapReadOnly();

	public abstract Map<IndexedDataHasValue, V> getDataExistentialsMapReadWrite();

	private class KeyChecker_ implements IndexedClassExpressionVisitor<Boolean> {

		@Override
		public Boolean visit(IndexedClass key) {
			return getClassEntitiesMapReadOnly().containsKey(key);
		}

		@Override
		public Boolean visit(IndexedIndividual key) {
			return getClassEntitiesMapReadOnly().containsKey(key);
		}

		@Override
		public Boolean visit(IndexedObjectIntersectionOf key) {
			return getObjectIntersectionsMapReadOnly().containsKey(key);
		}

		@Override
		public Boolean visit(IndexedObjectSomeValuesFrom key) {
			return getObjectExistentialsMapReadOnly().containsKey(key);
		}

		@Override
		public Boolean visit(IndexedDataHasValue key) {
			return getDataExistentialsMapReadOnly().containsKey(key);
		}

	}

	private class ValueGetter_ implements IndexedClassExpressionVisitor<V> {

		@Override
		public V visit(IndexedClass element) {
			return getClassEntitiesMapReadOnly().get(element);
		}

		@Override
		public V visit(IndexedIndividual element) {
			return getClassEntitiesMapReadOnly().get(element);
		}

		@Override
		public V visit(IndexedObjectIntersectionOf element) {
			return getObjectIntersectionsMapReadOnly().get(element);
		}

		@Override
		public V visit(IndexedObjectSomeValuesFrom element) {
			return getObjectExistentialsMapReadOnly().get(element);
		}

		@Override
		public V visit(IndexedDataHasValue element) {
			return getDataExistentialsMapReadOnly().get(element);
		}

	}

	private class ValueInserter_ implements IndexedClassExpressionVisitor<V> {
		final V value_;

		private ValueInserter_(V value) {
			value_ = value;
		}

		@Override
		public V visit(IndexedClass element) {
			return getClassEntitiesMapReadWrite().put(element, value_);
		}

		@Override
		public V visit(IndexedIndividual element) {
			return getClassEntitiesMapReadWrite().put(element, value_);
		}

		@Override
		public V visit(IndexedObjectIntersectionOf element) {
			return getObjectIntersectionsMapReadWrite().put(element, value_);
		}

		@Override
		public V visit(IndexedObjectSomeValuesFrom element) {
			return getObjectExistentialsMapReadWrite().put(element, value_);
		}

		@Override
		public V visit(IndexedDataHasValue element) {
			return getDataExistentialsMapReadWrite().put(element, value_);
		}

	}

	private class EntryRemover_ implements IndexedClassExpressionVisitor<V> {

		@Override
		public V visit(IndexedClass element) {
			return getClassEntitiesMapReadWrite().remove(element);
		}

		@Override
		public V visit(IndexedIndividual element) {
			return getClassEntitiesMapReadWrite().remove(element);
		}

		@Override
		public V visit(IndexedObjectIntersectionOf element) {
			return getObjectIntersectionsMapReadWrite().remove(element);
		}

		@Override
		public V visit(IndexedObjectSomeValuesFrom element) {
			return getObjectExistentialsMapReadWrite().remove(element);
		}

		@Override
		public V visit(IndexedDataHasValue element) {
			return getDataExistentialsMapReadWrite().remove(element);
		}

	}

	@Override
	public int size() {
		return getClassEntitiesMapReadOnly().size()
				+ getObjectIntersectionsMapReadOnly().size()
				+ getObjectExistentialsMapReadOnly().size()
				+ getDataExistentialsMapReadOnly().size();
	}

	@Override
	public boolean isEmpty() {
		return getClassEntitiesMapReadOnly().isEmpty()
				& getObjectIntersectionsMapReadOnly().isEmpty()
				& getObjectExistentialsMapReadOnly().isEmpty()
				& getDataExistentialsMapReadOnly().isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		if (key instanceof IndexedClassExpression)
			return ((IndexedClassExpression) key).accept(new KeyChecker_());
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		return getClassEntitiesMapReadOnly().containsValue(value)
				|| getObjectIntersectionsMapReadOnly().containsValue(value)
				|| getObjectExistentialsMapReadOnly().containsValue(value)
				|| getDataExistentialsMapReadOnly().containsValue(value);
	}

	@Override
	public V get(Object key) {
		if (key instanceof IndexedClassExpression)
			return ((IndexedClassExpression) key).accept(new ValueGetter_());
		return null;
	}

	@Override
	public V put(IndexedClassExpression key, V value) {
		return ((IndexedClassExpression) key).accept(new ValueInserter_(value));
	}

	@Override
	public V remove(Object key) {
		if (key instanceof IndexedClassExpression)
			return ((IndexedClassExpression) key).accept(new EntryRemover_());
		return null;
	}

	@Override
	public void clear() {
		getClassEntitiesMapReadOnly().clear();
		getObjectIntersectionsMapReadOnly().clear();
		getObjectExistentialsMapReadOnly().clear();
		getDataExistentialsMapReadOnly().clear();
	}

	@Override
	public Set<IndexedClassExpression> keySet() {
		return new AbstractSplitIndexedClassExpressionSet() {

			@Override
			public Set<IndexedObjectIntersectionOf> getObjectIntersectionsSetReadWrite() {
				return getObjectIntersectionsMapReadWrite().keySet();
			}

			@Override
			public Set<IndexedObjectIntersectionOf> getObjectIntersectionsSetReadOnly() {
				return getObjectIntersectionsMapReadOnly().keySet();
			}

			@Override
			public Set<IndexedObjectSomeValuesFrom> getObjectExistentialsSetReadWrite() {
				return getObjectExistentialsMapReadWrite().keySet();
			}

			@Override
			public Set<IndexedObjectSomeValuesFrom> getObjectExistentialsSetReadOnly() {
				return getObjectExistentialsMapReadOnly().keySet();
			}

			@Override
			public Set<IndexedDataHasValue> getDataExistentialsSetReadWrite() {
				return getDataExistentialsMapReadWrite().keySet();
			}

			@Override
			public Set<IndexedDataHasValue> getDataExistentialsSetReadOnly() {
				return getDataExistentialsMapReadOnly().keySet();
			}

			@Override
			public Set<IndexedClassEntity> getClassEntitiesSetReadWrite() {
				return getClassEntitiesMapReadWrite().keySet();
			}

			@Override
			public Set<IndexedClassEntity> getClassEntitiesSetReadOnly() {
				return getClassEntitiesMapReadOnly().keySet();
			}
		};

	}

	@Override
	public Set<java.util.Map.Entry<IndexedClassExpression, V>> entrySet() {
		return new AbstractSet<Entry<IndexedClassExpression, V>>() {

			@SuppressWarnings("unchecked")
			@Override
			public Iterator<Entry<IndexedClassExpression, V>> iterator() {
				Map<IndexedClassExpression, V> classEntitiesMapReadOnly = Collections
						.<IndexedClassExpression, V> unmodifiableMap(getClassEntitiesMapReadOnly());
				Map<IndexedClassExpression, V> objectIntersectionsMapReadOnly = Collections
						.<IndexedClassExpression, V> unmodifiableMap(getObjectIntersectionsMapReadOnly());
				Map<IndexedClassExpression, V> objectExistentialsMapReadOnly = Collections
						.<IndexedClassExpression, V> unmodifiableMap(getObjectExistentialsMapReadOnly());
				Map<IndexedClassExpression, V> dataExistentialsMapReadOnly = Collections
						.<IndexedClassExpression, V> unmodifiableMap(getDataExistentialsMapReadOnly());

				return Operations.concat(
						Arrays.asList(classEntitiesMapReadOnly.entrySet(),
								objectIntersectionsMapReadOnly.entrySet(),
								objectExistentialsMapReadOnly.entrySet(),
								dataExistentialsMapReadOnly.entrySet()))
						.iterator();
			}

			@Override
			public boolean contains(Object o) {
				if (!(o instanceof Map.Entry<?, ?>))
					return false;
				Object k = ((Map.Entry<?, ?>) o).getKey();
				return AbstractSplitIndexedClassExpressionMap.this
						.containsKey(k);
			}

			@Override
			public boolean remove(Object o) {
				if (!(o instanceof Map.Entry<?, ?>))
					return false;
				Object k = ((Map.Entry<?, ?>) o).getKey();
				return AbstractSplitIndexedClassExpressionMap.this.remove(k) != null;
			}

			@Override
			public int size() {
				return AbstractSplitIndexedClassExpressionMap.this.size();
			}

		};
	}
}
