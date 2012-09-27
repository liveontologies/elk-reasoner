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

import org.semanticweb.elk.owl.interfaces.ElkClass;

/**
 * An index updater that saves the changes into the {@link DifferentialIndex} object,
 * instead of immediately applying them for the affected indexed objects. The
 * changes can be committed to the indexed object all at once by calling the
 * respective method.
 * 
 * @author "Yevgeny Kazakov"
 * @author Pavel Klinov
 * 
 */
public class IncrementalIndexUpdater implements IndexUpdater {

	private final DifferentialIndex differentialIndex_;

	public IncrementalIndexUpdater(DifferentialIndex indexChange_) {
		this.differentialIndex_ = indexChange_;
	}

	/*@Override
	public boolean addToldSuperClassExpression(IndexedClassExpression target,
			IndexedClassExpression superClassExpression) {
		IndexedClassExpressionChange change = indexChange_
				.getCreateAdditions(target);
		if (change.toldSuperClassExpressions == null)
			change.toldSuperClassExpressions = new ArrayList<IndexedClassExpression>(
					1);
		return change.toldSuperClassExpressions.add(superClassExpression);
	}

	@Override
	public boolean removeToldSuperClassExpression(
			IndexedClassExpression target,
			IndexedClassExpression superClassExpression) {
		IndexedClassExpressionChange change = indexChange_
				.getCreateDeletions(target);
		if (change.toldSuperClassExpressions == null)
			change.toldSuperClassExpressions = new ArrayList<IndexedClassExpression>(
					1);
		return change.toldSuperClassExpressions.add(superClassExpression);
	}

	@Override
	public boolean addNegConjunctionByConjunct(IndexedClassExpression target,
			IndexedObjectIntersectionOf conjunction,
			IndexedClassExpression conjunct) {
		IndexedClassExpressionChange change = indexChange_
				.getCreateAdditions(target);
		if (change.negConjunctionsByConjunct == null)
			change.negConjunctionsByConjunct = new ArrayHashMap<IndexedClassExpression, IndexedObjectIntersectionOf>(
					4);
		return change.negConjunctionsByConjunct.put(conjunct, conjunction) != null;
	}

	@Override
	public boolean removeNegConjunctionByConjunct(
			IndexedClassExpression target,
			IndexedObjectIntersectionOf conjunction,
			IndexedClassExpression conjunct) {
		IndexedClassExpressionChange change = indexChange_
				.getCreateDeletions(target);
		if (change.negConjunctionsByConjunct == null)
			change.negConjunctionsByConjunct = new ArrayHashMap<IndexedClassExpression, IndexedObjectIntersectionOf>(
					4);
		return change.negConjunctionsByConjunct.put(conjunct, conjunction) != null;
	}

	@Override
	public boolean addNegExistential(IndexedClassExpression target,
			IndexedObjectSomeValuesFrom existential) {
		IndexedClassExpressionChange change = indexChange_
				.getCreateAdditions(target);
		if (change.negExistentials == null)
			change.negExistentials = new ArrayList<IndexedObjectSomeValuesFrom>(
					1);
		return change.negExistentials.add(existential);
	}

	@Override
	public boolean removeNegExistential(IndexedClassExpression target,
			IndexedObjectSomeValuesFrom existential) {
		IndexedClassExpressionChange change = indexChange_
				.getCreateDeletions(target);
		if (change.negExistentials == null)
			change.negExistentials = new ArrayList<IndexedObjectSomeValuesFrom>(
					1);
		return change.negExistentials.add(existential);
	}

	@Override
	public boolean addToldSubObjectProperty(IndexedObjectProperty target,
			IndexedPropertyChain subObjectProperty) {
		return false;
	}

	@Override
	public boolean removeToldSubObjectProperty(IndexedObjectProperty target,
			IndexedPropertyChain subObjectProperty) {
		return false;
	}

	@Override
	public boolean addToldSuperObjectProperty(IndexedPropertyChain target,
			IndexedObjectProperty superObjectProperty) {
		return false;
	}

	@Override
	public boolean removeToldSuperObjectProperty(IndexedPropertyChain target,
			IndexedObjectProperty superObjectProperty) {
		return false;
	}*/

	@Override
	public void addClass(ElkClass newClass) {
		differentialIndex_.addedClasses.add(newClass);
	}

	@Override
	public void removeClass(ElkClass oldClass) {
		differentialIndex_.removedClasses.add(oldClass);
	}

	@Override
	public boolean add(IndexedClassExpression target, IndexChange change) {
		return differentialIndex_.getCreateAdditions(target).add(change);
	}

	@Override
	public boolean remove(IndexedClassExpression target, IndexChange change) {
		return differentialIndex_.getCreateDeletions(target).add(change);
	}
}