package org.semanticweb.elk.reasoner.saturation.context;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassEntity;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDataHasValue;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.util.collections.ArrayHashMultiSet;
import org.semanticweb.elk.util.collections.Operations;

public class Subsumers extends AbstractSet<IndexedClassExpression> implements
		Set<IndexedClassExpression> {

	private ArrayHashMultiSet<IndexedClassEntity> classEntities_;
	private ArrayHashMultiSet<IndexedObjectIntersectionOf> objectIntersections_;
	private ArrayHashMultiSet<IndexedObjectSomeValuesFrom> objectExistentials_;
	private ArrayHashMultiSet<IndexedDataHasValue> dataExistentials_;

	public Subsumers() {
	}

	private class MemberChecker_ implements
			IndexedClassExpressionVisitor<Boolean> {

		@Override
		public Boolean visit(IndexedClass element) {
			if (classEntities_ == null)
				return false;
			return classEntities_.contains(element);
		}

		@Override
		public Boolean visit(IndexedIndividual element) {
			if (classEntities_ == null)
				return false;
			return classEntities_.contains(element);
		}

		@Override
		public Boolean visit(IndexedObjectIntersectionOf element) {
			if (objectIntersections_ == null)
				return false;
			return objectIntersections_.contains(element);
		}

		@Override
		public Boolean visit(IndexedObjectSomeValuesFrom element) {
			if (objectExistentials_ == null)
				return false;
			return objectExistentials_.contains(element);
		}

		@Override
		public Boolean visit(IndexedDataHasValue element) {
			if (dataExistentials_ == null)
				return false;
			return dataExistentials_.contains(element);
		}

	}

	private class Inserter_ implements IndexedClassExpressionVisitor<Boolean> {

		@Override
		public Boolean visit(IndexedClass element) {
			if (classEntities_ == null)
				classEntities_ = new ArrayHashMultiSet<IndexedClassEntity>(3);
			return classEntities_.add(element);
		}

		@Override
		public Boolean visit(IndexedIndividual element) {
			if (classEntities_ == null)
				classEntities_ = new ArrayHashMultiSet<IndexedClassEntity>(3);
			return classEntities_.add(element);
		}

		@Override
		public Boolean visit(IndexedObjectIntersectionOf element) {
			if (objectIntersections_ == null)
				objectIntersections_ = new ArrayHashMultiSet<IndexedObjectIntersectionOf>(
						3);
			return objectIntersections_.add(element);
		}

		@Override
		public Boolean visit(IndexedObjectSomeValuesFrom element) {
			if (objectExistentials_ == null)
				objectExistentials_ = new ArrayHashMultiSet<IndexedObjectSomeValuesFrom>(
						3);
			return objectExistentials_.add(element);
		}

		@Override
		public Boolean visit(IndexedDataHasValue element) {
			if (dataExistentials_ == null)
				dataExistentials_ = new ArrayHashMultiSet<IndexedDataHasValue>(
						3);
			return dataExistentials_.add(element);
		}

	}

	private class Remover_ implements IndexedClassExpressionVisitor<Boolean> {

		@Override
		public Boolean visit(IndexedClass element) {
			boolean result = classEntities_.remove(element);
			if (classEntities_.isEmpty())
				classEntities_ = null;
			return result;
		}

		@Override
		public Boolean visit(IndexedIndividual element) {
			boolean result = classEntities_.remove(element);
			if (classEntities_.isEmpty())
				classEntities_ = null;
			return result;
		}

		@Override
		public Boolean visit(IndexedObjectIntersectionOf element) {
			boolean result = objectIntersections_.remove(element);
			if (objectIntersections_.isEmpty())
				objectIntersections_ = null;
			return result;
		}

		@Override
		public Boolean visit(IndexedObjectSomeValuesFrom element) {
			boolean result = objectExistentials_.remove(element);
			if (objectExistentials_.isEmpty())
				objectExistentials_ = null;
			return result;
		}

		@Override
		public Boolean visit(IndexedDataHasValue element) {
			boolean result = dataExistentials_.remove(element);
			if (dataExistentials_.isEmpty())
				dataExistentials_ = null;
			return result;
		}

	}

	public static final <T> Set<T> emptyIfNull(Set<T> set) {
		if (set == null)
			return Collections.emptySet();
		return set;
	}

	@Override
	public int size() {
		return (emptyIfNull(classEntities_).size()
				+ emptyIfNull(objectIntersections_).size()
				+ emptyIfNull(objectExistentials_).size() + emptyIfNull(
					dataExistentials_).size());
	}

	@Override
	public boolean contains(Object o) {
		if (o instanceof IndexedClassExpression)
			return ((IndexedClassExpression) o).accept(new MemberChecker_());
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<IndexedClassExpression> iterator() {
		return Operations.concat(
				Arrays.asList(emptyIfNull(classEntities_),
						emptyIfNull(objectIntersections_),
						emptyIfNull(objectExistentials_),
						emptyIfNull(dataExistentials_))).iterator();
	}

	@Override
	public boolean add(IndexedClassExpression e) {
		return e.accept(new Inserter_());
	}

	@Override
	public boolean remove(Object o) {
		if (o instanceof IndexedClassExpression)
			return ((IndexedClassExpression) o).accept(new Remover_());
		return false;
	}

	@Override
	public void clear() {
		classEntities_ = null;
		objectIntersections_ = null;
		objectExistentials_ = null;
		dataExistentials_ = null;
	}

}
