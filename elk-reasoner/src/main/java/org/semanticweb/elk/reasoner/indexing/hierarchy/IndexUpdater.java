package org.semanticweb.elk.reasoner.indexing.hierarchy;

/**
 * Functions through which entries for indexed class expressions are updated.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
interface IndexUpdater {

	public boolean addToldSuperClassExpression(IndexedClassExpression target,
			IndexedClassExpression superClassExpression);

	public boolean removeToldSuperClassExpression(
			IndexedClassExpression target,
			IndexedClassExpression superClassExpression);

	public boolean addNegConjunctionByConjunct(IndexedClassExpression target,
			IndexedObjectIntersectionOf conjunction,
			IndexedClassExpression conjunct);

	public boolean removeNegConjunctionByConjunct(
			IndexedClassExpression target,
			IndexedObjectIntersectionOf conjunction,
			IndexedClassExpression conjunct);

	public boolean addNegExistential(IndexedClassExpression target,
			IndexedObjectSomeValuesFrom existential);

	public boolean removeNegExistential(IndexedClassExpression target,
			IndexedObjectSomeValuesFrom existential);

	public boolean addToldSubObjectProperty(IndexedObjectProperty target,
			IndexedPropertyChain subObjectProperty);

	public boolean removeToldSubObjectProperty(IndexedObjectProperty target,
			IndexedPropertyChain subObjectProperty);

	public boolean addToldSuperObjectProperty(IndexedPropertyChain target,
			IndexedObjectProperty superObjectProperty);

	public boolean removeToldSuperObjectProperty(IndexedPropertyChain target,
			IndexedObjectProperty superObjectProperty);

}
