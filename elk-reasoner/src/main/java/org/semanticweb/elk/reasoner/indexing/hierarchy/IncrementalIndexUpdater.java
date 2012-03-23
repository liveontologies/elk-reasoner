package org.semanticweb.elk.reasoner.indexing.hierarchy;

import java.util.ArrayList;

import org.semanticweb.elk.util.collections.ArrayHashMap;

/**
 * An index updater that saves the changes into the {@link IndexChange} object,
 * instead of immediately applying them for the affected indexed objects. The
 * changes can be committed to the indexed object all at once by calling the
 * respective method.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class IncrementalIndexUpdater implements IndexUpdater {

	protected final IndexChange indexChange;

	public IncrementalIndexUpdater(IndexChange indexChange) {
		this.indexChange = indexChange;
	}

	public boolean addToldSuperClassExpression(IndexedClassExpression target,
			IndexedClassExpression superClassExpression) {
		IndexedClassExpressionChange change = indexChange
				.getCreateAdditions(target);
		if (change.toldSuperClassExpressions == null)
			change.toldSuperClassExpressions = new ArrayList<IndexedClassExpression>(
					1);
		return change.toldSuperClassExpressions.add(superClassExpression);
	}

	public boolean removeToldSuperClassExpression(
			IndexedClassExpression target,
			IndexedClassExpression superClassExpression) {
		IndexedClassExpressionChange change = indexChange
				.getCreateDeletions(target);
		if (change.toldSuperClassExpressions == null)
			change.toldSuperClassExpressions = new ArrayList<IndexedClassExpression>(
					1);
		return change.toldSuperClassExpressions.add(superClassExpression);
	}

	public boolean addNegConjunctionByConjunct(IndexedClassExpression target,
			IndexedObjectIntersectionOf conjunction,
			IndexedClassExpression conjunct) {
		IndexedClassExpressionChange change = indexChange
				.getCreateAdditions(target);
		if (change.negConjunctionsByConjunct == null)
			change.negConjunctionsByConjunct = new ArrayHashMap<IndexedClassExpression, IndexedObjectIntersectionOf>(
					4);
		return change.negConjunctionsByConjunct.put(conjunct, conjunction) != null;
	}

	public boolean removeNegConjunctionByConjunct(
			IndexedClassExpression target,
			IndexedObjectIntersectionOf conjunction,
			IndexedClassExpression conjunct) {
		IndexedClassExpressionChange change = indexChange
				.getCreateDeletions(target);
		if (change.negConjunctionsByConjunct == null)
			// TODO possibly replace by ArrayHashMap when it supports removal
			change.negConjunctionsByConjunct = new ArrayHashMap<IndexedClassExpression, IndexedObjectIntersectionOf>(
					4);
		return change.negConjunctionsByConjunct.put(conjunct, conjunction) != null;
	}

	public boolean addNegExistential(IndexedClassExpression target,
			IndexedObjectSomeValuesFrom existential) {
		IndexedClassExpressionChange change = indexChange
				.getCreateAdditions(target);
		if (change.negExistentials == null)
			change.negExistentials = new ArrayList<IndexedObjectSomeValuesFrom>(
					1);
		return change.negExistentials.add(existential);
	}

	public boolean removeNegExistential(IndexedClassExpression target,
			IndexedObjectSomeValuesFrom existential) {
		IndexedClassExpressionChange change = indexChange
				.getCreateDeletions(target);
		if (change.negExistentials == null)
			change.negExistentials = new ArrayList<IndexedObjectSomeValuesFrom>(
					1);
		return change.negExistentials.add(existential);
	}

	public boolean addToldSubObjectProperty(IndexedObjectProperty target,
			IndexedPropertyChain subObjectProperty) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean removeToldSubObjectProperty(IndexedObjectProperty target,
			IndexedPropertyChain subObjectProperty) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean addToldSuperObjectProperty(IndexedPropertyChain target,
			IndexedObjectProperty superObjectProperty) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean removeToldSuperObjectProperty(IndexedPropertyChain target,
			IndexedObjectProperty superObjectProperty) {
		// TODO Auto-generated method stub
		return false;
	}

}
