package org.semanticweb.elk.alc.indexing.hierarchy;

/*
 * #%L
 * ALC Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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

		ipc.updateAndCheckOccurrenceNumbers(index_, increment);

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
	public IndexedObjectUnionOf visit(IndexedObjectUnionOf element) {
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