package org.semanticweb.elk.reasoner.indexing.collections;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassEntity;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDataHasValue;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitorEx;
import org.semanticweb.elk.util.collections.Operations;

public abstract class AbstractSplitIndexedClassExpressionSet extends
		AbstractSet<IndexedClassExpression> implements
		SplitIndexedClassExpressionSet {

	public abstract Set<IndexedClassEntity> getClassEntitiesSetReadOnly();

	public abstract Set<IndexedClassEntity> getClassEntitiesSetReadWrite();

	public abstract Set<IndexedObjectIntersectionOf> getObjectIntersectionsSetReadOnly();

	public abstract Set<IndexedObjectIntersectionOf> getObjectIntersectionsSetReadWrite();

	public abstract Set<IndexedObjectSomeValuesFrom> getObjectExistentialsSetReadOnly();

	public abstract Set<IndexedObjectSomeValuesFrom> getObjectExistentialsSetReadWrite();

	public abstract Set<IndexedDataHasValue> getDataExistentialsSetReadOnly();

	public abstract Set<IndexedDataHasValue> getDataExistentialsSetReadWrite();

	private static final StaticMemberChecker_ MEMBER_CHECKER_ = new StaticMemberChecker_();
	private static final StaticInserter_ INSERTER_ = new StaticInserter_();
	private static final StaticRemover_ REMOVER_ = new StaticRemover_();

	private static class StaticMemberChecker_
			implements
			IndexedClassExpressionVisitorEx<Boolean, AbstractSplitIndexedClassExpressionSet> {

		@Override
		public Boolean visit(IndexedClass element,
				AbstractSplitIndexedClassExpressionSet set) {
			return set.getClassEntitiesSetReadOnly().contains(element);
		}

		@Override
		public Boolean visit(IndexedIndividual element,
				AbstractSplitIndexedClassExpressionSet set) {
			return set.getClassEntitiesSetReadOnly().contains(element);
		}

		@Override
		public Boolean visit(IndexedObjectIntersectionOf element,
				AbstractSplitIndexedClassExpressionSet set) {
			return set.getObjectIntersectionsSetReadOnly().contains(element);
		}

		@Override
		public Boolean visit(IndexedObjectSomeValuesFrom element,
				AbstractSplitIndexedClassExpressionSet set) {
			return set.getObjectExistentialsSetReadOnly().contains(element);
		}

		@Override
		public Boolean visit(IndexedDataHasValue element,
				AbstractSplitIndexedClassExpressionSet set) {
			return set.getDataExistentialsSetReadOnly().contains(element);
		}

	}

	private static class StaticInserter_
			implements
			IndexedClassExpressionVisitorEx<Boolean, AbstractSplitIndexedClassExpressionSet> {

		@Override
		public Boolean visit(IndexedClass element,
				AbstractSplitIndexedClassExpressionSet set) {
			return set.getClassEntitiesSetReadWrite().add(element);
		}

		@Override
		public Boolean visit(IndexedIndividual element,
				AbstractSplitIndexedClassExpressionSet set) {
			return set.getClassEntitiesSetReadWrite().add(element);
		}

		@Override
		public Boolean visit(IndexedObjectIntersectionOf element,
				AbstractSplitIndexedClassExpressionSet set) {
			return set.getObjectIntersectionsSetReadWrite().add(element);
		}

		@Override
		public Boolean visit(IndexedObjectSomeValuesFrom element,
				AbstractSplitIndexedClassExpressionSet set) {
			return set.getObjectExistentialsSetReadWrite().add(element);
		}

		@Override
		public Boolean visit(IndexedDataHasValue element,
				AbstractSplitIndexedClassExpressionSet set) {
			return set.getDataExistentialsSetReadWrite().add(element);
		}

	}

	private static class StaticRemover_
			implements
			IndexedClassExpressionVisitorEx<Boolean, AbstractSplitIndexedClassExpressionSet> {

		@Override
		public Boolean visit(IndexedClass element,
				AbstractSplitIndexedClassExpressionSet set) {
			return set.getClassEntitiesSetReadWrite().remove(element);
		}

		@Override
		public Boolean visit(IndexedIndividual element,
				AbstractSplitIndexedClassExpressionSet set) {
			return set.getClassEntitiesSetReadWrite().remove(element);
		}

		@Override
		public Boolean visit(IndexedObjectIntersectionOf element,
				AbstractSplitIndexedClassExpressionSet set) {
			return set.getObjectIntersectionsSetReadWrite().remove(element);
		}

		@Override
		public Boolean visit(IndexedObjectSomeValuesFrom element,
				AbstractSplitIndexedClassExpressionSet set) {
			return set.getObjectExistentialsSetReadWrite().remove(element);
		}

		@Override
		public Boolean visit(IndexedDataHasValue element,
				AbstractSplitIndexedClassExpressionSet set) {
			return set.getDataExistentialsSetReadWrite().remove(element);
		}

	}

	@Override
	public int size() {
		return getClassEntitiesSetReadOnly().size()
				+ getObjectIntersectionsSetReadOnly().size()
				+ getObjectExistentialsSetReadOnly().size()
				+ getDataExistentialsSetReadOnly().size();
	}

	@Override
	public boolean contains(Object o) {
		if (o instanceof IndexedClassExpression)
			return ((IndexedClassExpression) o).accept(MEMBER_CHECKER_, this);
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<IndexedClassExpression> iterator() {
		return Operations.concat(
				Arrays.asList(getClassEntitiesSetReadOnly(),
						getObjectIntersectionsSetReadOnly(),
						getObjectExistentialsSetReadOnly(),
						getDataExistentialsSetReadOnly())).iterator();
	}

	@Override
	public boolean add(IndexedClassExpression e) {
		return e.accept(INSERTER_, this);
	}

	@Override
	public boolean remove(Object o) {
		if (o instanceof IndexedClassExpression)
			return ((IndexedClassExpression) o).accept(REMOVER_, this);
		return false;
	}

	@Override
	public void clear() {
		getClassEntitiesSetReadOnly().clear();
		getObjectIntersectionsSetReadOnly().clear();
		getObjectExistentialsSetReadOnly().clear();
		getDataExistentialsSetReadOnly().clear();
	}

}
