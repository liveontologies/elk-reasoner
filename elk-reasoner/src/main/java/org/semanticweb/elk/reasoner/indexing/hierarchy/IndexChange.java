/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.indexing.hierarchy;

import java.util.Map;
import java.util.Map.Entry;

import org.semanticweb.elk.util.collections.ArrayHashMap;

/**
 * An object representing incremental changes in the index. The changes are
 * stored in two maps: additions and deletions. The map for additions assigns to
 * every indexed class expression for which some index entries have been added,
 * a dummy {@link IndexedClassExpressionChange} object, whose fields are exactly
 * the additions for the indexed class expressions. Likewise, the map for
 * deletions assigns to every indexed class expression for which some index
 * entries have been deleted, a dummy {@link IndexedClassExpressionChange}
 * object, whose fields are exactly the deletions for the indexed class
 * expressions.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class IndexChange {

	/**
	 * A map from indexed class expression to index class expression objects
	 * whose fields represent deleted entries for these class expressions
	 */
	final Map<IndexedClassExpression, IndexedClassExpressionChange> indexAdditions;

	/**
	 * The index updater which is used to commit all saved changes to the
	 * respective objects;
	 */
	final IndexUpdater directIndexUpdater;

	/**
	 * A map from indexed class expression to index class expressions whose
	 * fields represent added entries for these class expressions
	 */
	final Map<IndexedClassExpression, IndexedClassExpressionChange> indexDeletions;

	/**
	 * @return the map from indexed class expressions to the corresponding
	 *         objects containing index additions for these class expressions
	 * 
	 */
	public Map<IndexedClassExpression, IndexedClassExpressionChange> getIndexAdditions() {
		return this.indexAdditions;
	}

	/**
	 * @return the map from indexed class expressions to the corresponding
	 *         objects containing index deletions for these class expressions
	 */
	public Map<IndexedClassExpression, IndexedClassExpressionChange> getIndexDeletions() {
		return this.indexDeletions;
	}

	/**
	 * Get the object assigned to the given indexed class expression for storing
	 * index additions, or assign a new one if no such object has been assigned.
	 * 
	 * @param target
	 *            the indexed class expressions for which to find the changes
	 *            additions object
	 * @return the object which contains all index additions for the given
	 *         indexed class expression
	 */
	public IndexedClassExpressionChange getCreateAdditions(
			IndexedClassExpression target) {
		IndexedClassExpressionChange result = indexAdditions.get(target);
		if (result == null) {
			result = new IndexedClassExpressionChange();
			indexAdditions.put(target, result);
		}
		return result;
	}

	/**
	 * Get the object assigned to the given indexed class expression for storing
	 * index deletions, or assign a new one if no such object has been assigned.
	 * 
	 * @param target
	 *            the indexed class expressions for which to find the changes
	 *            deletions object
	 * @return the object which contains all index deletions for the given
	 *         indexed class expression
	 */
	public IndexedClassExpressionChange getCreateDeletions(
			IndexedClassExpression target) {
		IndexedClassExpressionChange result = indexDeletions.get(target);
		if (result == null) {
			result = new IndexedClassExpressionChange();
			indexDeletions.put(target, result);
		}
		return result;
	}

	public IndexChange() {
		this.indexAdditions = new ArrayHashMap<IndexedClassExpression, IndexedClassExpressionChange>(
				127);
		this.indexDeletions = new ArrayHashMap<IndexedClassExpression, IndexedClassExpressionChange>(
				127);
		this.directIndexUpdater = new DirectIndexUpdater();
	}

	/**
	 * Commits the changes to the indexed objects and clears all changes.
	 */
	public void commit() {
		// TODO: make this code less error-prone

		// commit deletions
		for (IndexedClassExpression target : indexDeletions.keySet()) {
			IndexedClassExpressionChange change = indexDeletions.get(target);
			if (change.toldSuperClassExpressions != null)
				for (IndexedClassExpression superClassExpression : change.toldSuperClassExpressions) {
					directIndexUpdater.removeToldSuperClassExpression(target,
							superClassExpression);
				}
			if (change.negConjunctionsByConjunct != null)
				for (Entry<IndexedClassExpression, IndexedObjectIntersectionOf> entry : change.negConjunctionsByConjunct
						.entrySet()) {
					directIndexUpdater.removeNegConjunctionByConjunct(target,
							entry.getValue(), entry.getKey());
				}
			if (change.negExistentials != null)
				for (IndexedObjectSomeValuesFrom existential : change.negExistentials) {
					directIndexUpdater
							.removeNegExistential(target, existential);
				}
		}
		indexDeletions.clear();
		// commit additions
		for (IndexedClassExpression target : indexAdditions.keySet()) {
			IndexedClassExpressionChange change = indexAdditions.get(target);
			if (change.toldSuperClassExpressions != null)
				for (IndexedClassExpression superClassExpression : change.toldSuperClassExpressions) {
					directIndexUpdater.addToldSuperClassExpression(target,
							superClassExpression);
				}
			if (change.negConjunctionsByConjunct != null)
				for (Entry<IndexedClassExpression, IndexedObjectIntersectionOf> entry : change.negConjunctionsByConjunct
						.entrySet()) {
					directIndexUpdater.addNegConjunctionByConjunct(target,
							entry.getValue(), entry.getKey());
				}
			if (change.negExistentials != null)
				for (IndexedObjectSomeValuesFrom existential : change.negExistentials) {
					directIndexUpdater.addNegExistential(target, existential);
				}
		}
		indexAdditions.clear();

	}
}
