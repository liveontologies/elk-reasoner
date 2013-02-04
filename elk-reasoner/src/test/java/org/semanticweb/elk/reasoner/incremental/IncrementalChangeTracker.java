/**
 * 
 */
package org.semanticweb.elk.reasoner.incremental;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Random;

import org.semanticweb.elk.RandomSeedProvider;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class IncrementalChangeTracker<T> {

	//protected static final Logger LOGGER_ = Logger.getLogger(IncrementalChangeTracker.class);
	
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
	
	public IncrementalChangeTracker(OnOffVector<T> changingAxioms, int changeSize) {
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
