/**
 * 
 */
package org.semanticweb.elk.reasoner.incremental;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import java.util.Deque;
import java.util.LinkedList;
import java.util.Random;

import org.semanticweb.elk.RandomSeedProvider;

/**
 * Generates random incremental "changes", that is, sets of added and deleted
 * axioms
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class IncrementalChangeTracker<T> {

	/**
	 * the axioms from which to generate the changes
	 */
	private final OnOffVector<T> changingAxioms_;
	/**
	 * the queue in which to append the generated changes
	 */
	private final Deque<IncrementalChange<T>> changes_ = new LinkedList<IncrementalChange<T>>();
	/**
	 * The number of added and removed axioms to generate
	 */
	private final int changeSize_;

	private final Random rnd_ = new Random(RandomSeedProvider.VALUE);

	public IncrementalChangeTracker(OnOffVector<T> changingAxioms,
			int changeSize) {
		changeSize_ = changeSize;
		changingAxioms_ = changingAxioms;
	}

	public IncrementalChange<T> generateNextChange() {
		IncrementalChange<T> change = new IncrementalChange<T>();

		for (int i = 0; i < changeSize_; i++) {
			int index = rnd_.nextInt(changingAxioms_.size());
			T axiom = changingAxioms_.get(index);
			// removing a random axiom if its status was "on", that is,
			// it was loaded to the reasoner; otherwise adding this
			// axiom; the status is flip
			if (changingAxioms_.flipOnOff(index)) {
				change.registerDeletion(axiom);
			} else {
				change.registerAddition(axiom);
			}
		}

		changes_.add(change);

		return change;
	}

	Deque<IncrementalChange<T>> getChangeHistory() {
		return changes_;
	}

	int getChangeSize() {
		return changeSize_;
	}

	void clearHistory() {
		changes_.clear();
	}
}
