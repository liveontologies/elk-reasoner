package org.semanticweb.elk.alc.indexing.hierarchy;

import org.semanticweb.elk.alc.indexing.visitors.IndexedObjectFilter;

/**
 * A {@link ObjectOccurrenceUpdateFilter}, which is responsible for updating the
 * occurrence counters of {@link IndexedObject}s, as well as for adding such
 * them to the {@link IndexedObjectCache} when its occurrences becomes non-zero,
 * and removing from the {@link IndexedObjectCache}, when its occurrences
 * becomes zero.
 */
class ObjectOccurrenceUpdateFilter implements IndexedObjectFilter {

	private final OntologyIndex index_;

	private final IndexedObjectCache cache_;

	protected final int increment, positiveIncrement, negativeIncrement;

	ObjectOccurrenceUpdateFilter(OntologyIndex index, int increment,
			int positiveIncrement, int negativeIncrement) {
		this.index_ = index;
		this.cache_ = index.getIndexedObjectCache();
		this.increment = increment;
		this.positiveIncrement = positiveIncrement;
		this.negativeIncrement = negativeIncrement;
	}

	public <T extends IndexedClassExpression> T update(T ice) {
		if (!ice.occurs() && increment > 0)
			index_.add(ice);

		ice.updateAndCheckOccurrenceNumbers(index_, increment,
				positiveIncrement, negativeIncrement);

		if (!ice.occurs() && increment < 0) {
			index_.remove(ice);
		}

		return ice;
	}

	public <T extends IndexedObjectProperty> T update(T ipc) {
		if (!ipc.occurs() && increment > 0)
			index_.add(ipc);

		ipc.updateAndCheckOccurrenceNumbers(increment);

		if (!ipc.occurs() && increment < 0)
			index_.remove(ipc);

		return ipc;
	}

	public <T extends IndexedAxiom> T update(T axiom) {
		if (!axiom.occurs() && increment > 0)
			index_.add(axiom);

		axiom.updateOccurrenceNumbers(index_, increment);

		if (!axiom.occurs() && increment < 0)
			index_.remove(axiom);

		return axiom;
	}

	@Override
	public IndexedClass visit(IndexedClass element) {
		return update(cache_.visit(element));
	}

	@Override
	public IndexedObjectIntersectionOf visit(IndexedObjectIntersectionOf element) {
		return update(cache_.visit(element));
	}

	@Override
	public IndexedObjectSomeValuesFrom visit(IndexedObjectSomeValuesFrom element) {
		return update(cache_.visit(element));
	}

	@Override
	public IndexedObjectProperty visit(IndexedObjectProperty element) {
		return update(cache_.visit(element));
	}

	@Override
	public IndexedSubClassOfAxiom visit(IndexedSubClassOfAxiom axiom) {
		return update(cache_.visit(axiom));
	}

}