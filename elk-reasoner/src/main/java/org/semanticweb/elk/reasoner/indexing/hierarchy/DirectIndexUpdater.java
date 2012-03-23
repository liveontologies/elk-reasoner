package org.semanticweb.elk.reasoner.indexing.hierarchy;

import java.util.ArrayList;

import org.semanticweb.elk.util.collections.ArrayHashMap;

/**
 * An index updater through which the fields of expressions are modified
 * directly.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
class DirectIndexUpdater implements IndexUpdater {

	public boolean addToldSuperClassExpression(IndexedClassExpression target,
			IndexedClassExpression superClassExpression) {
		if (target.toldSuperClassExpressions == null)
			target.toldSuperClassExpressions = new ArrayList<IndexedClassExpression>(
					1);
		return target.toldSuperClassExpressions.add(superClassExpression);
	}

	public boolean removeToldSuperClassExpression(
			IndexedClassExpression target,
			IndexedClassExpression superClassExpression) {
		boolean success = false;
		if (target.toldSuperClassExpressions != null) {
			success = target.toldSuperClassExpressions
					.remove(superClassExpression);
			if (target.toldSuperClassExpressions.isEmpty())
				target.toldSuperClassExpressions = null;
		}
		return success;
	}

	public boolean addNegConjunctionByConjunct(IndexedClassExpression target,
			IndexedObjectIntersectionOf conjunction,
			IndexedClassExpression conjunct) {
		if (target.negConjunctionsByConjunct == null)
			target.negConjunctionsByConjunct = new ArrayHashMap<IndexedClassExpression, IndexedObjectIntersectionOf>(
					4);
		return target.negConjunctionsByConjunct.put(conjunct, conjunction) != null;
	}

	public boolean removeNegConjunctionByConjunct(
			IndexedClassExpression target,
			IndexedObjectIntersectionOf conjunction,
			IndexedClassExpression conjunct) {
		boolean success = false;
		if (target.negConjunctionsByConjunct != null) {
			success = (target.negConjunctionsByConjunct.remove(conjunct) != null);
			if (target.negConjunctionsByConjunct.isEmpty())
				target.negConjunctionsByConjunct = null;
		}
		return success;
	}

	public boolean addNegExistential(IndexedClassExpression target,
			IndexedObjectSomeValuesFrom existential) {
		if (target.negExistentials == null)
			target.negExistentials = new ArrayList<IndexedObjectSomeValuesFrom>(
					1);
		return target.negExistentials.add(existential);
	}

	public boolean removeNegExistential(IndexedClassExpression target,
			IndexedObjectSomeValuesFrom existential) {
		boolean success = false;
		if (target.negExistentials != null) {
			success = target.negExistentials.remove(existential);
			if (target.negExistentials.isEmpty())
				target.negExistentials = null;
		}
		return success;
	}

	public boolean addToldSubObjectProperty(IndexedObjectProperty target,
			IndexedPropertyChain subObjectProperty) {
		if (target.toldSubProperties == null)
			target.toldSubProperties = new ArrayList<IndexedPropertyChain>(1);
		return target.toldSubProperties.add(subObjectProperty);
	}

	public boolean removeToldSubObjectProperty(IndexedObjectProperty target,
			IndexedPropertyChain subObjectProperty) {
		boolean success = false;
		if (target.toldSubProperties != null) {
			success = target.toldSubProperties.remove(subObjectProperty);
			if (target.toldSubProperties.isEmpty())
				target.toldSubProperties = null;
		}
		return success;
	}

	public boolean addToldSuperObjectProperty(IndexedPropertyChain target,
			IndexedObjectProperty superObjectProperty) {
		if (target.toldSuperProperties == null)
			target.toldSuperProperties = new ArrayList<IndexedObjectProperty>(1);
		return target.toldSuperProperties.add(superObjectProperty);
	}

	public boolean removeToldSuperObjectProperty(IndexedPropertyChain target,
			IndexedObjectProperty superObjectProperty) {
		boolean success = false;
		if (target.toldSuperProperties != null) {
			success = target.toldSuperProperties.remove(superObjectProperty);
			if (target.toldSuperProperties.isEmpty())
				target.toldSuperProperties = null;
		}
		return success;
	}

}
