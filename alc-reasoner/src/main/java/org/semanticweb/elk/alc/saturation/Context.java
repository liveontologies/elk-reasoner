package org.semanticweb.elk.alc.saturation;

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

import java.util.AbstractCollection;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;

import org.semanticweb.elk.alc.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.alc.indexing.hierarchy.IndexedDisjointnessAxiom;
import org.semanticweb.elk.alc.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.alc.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.BackwardLink;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.Clash;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.ComposedSubsumer;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.ConjectureNonSubsumer;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.ContextInitialization;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.DecomposedSubsumer;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.DisjointSubsumer;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.Disjunction;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.ExternalConclusion;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.ExternalDeterministicConclusion;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.ForwardLink;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.LocalConclusion;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.NegatedSubsumer;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.NegativePropagation;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.PossibleComposedSubsumer;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.PossibleConclusion;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.PossibleDecomposedSubsumer;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.PossiblePropagatedExistential;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.PossiblePropagation;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.PropagatedClash;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.PropagatedComposedSubsumer;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.Propagation;
import org.semanticweb.elk.alc.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.util.collections.ArrayHashMap;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.collections.HashSetMultimap;
import org.semanticweb.elk.util.collections.Multimap;
import org.semanticweb.elk.util.collections.Operations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The elementary component of the saturation.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class Context {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(Context.class);

	private final static ConclusionVisitor<Context, Boolean> CONCLUSION_INSERTER_ = new ConclusionInserter();

	private final static ConclusionVisitor<Context, Boolean> CONCLUSION_DELETER_ = new ConclusionDeleter();

	/**
	 * the {@link Root} from which the information in this {@link Context} was
	 * derived
	 */
	private final Root root_;

	/**
	 * {@code true} if all rules for this {@link Context} are applied, i.e., all
	 * {@link Conclusion}s witnessing a model for this {@link Context} have been
	 * computed
	 */
	private boolean isSaturated_ = false;

	/**
	 * {@code true} if this {@link Context} is initialized
	 */
	private boolean isInitialized_ = false;
	
	/**
	 * if {@code false}, this context is in the process of exploring alternative
	 * branches and its local history will be maintained if it currently doesn't
	 * contain non-deterministic conclusions.
	 */
	private boolean historyExplored_ = true;

	/**
	 * {@code true} if some inconsistency has been derived in this
	 * {@link Context}
	 */
	private boolean hasClash_ = false;

	/**
	 * the common (possible) super-classes of the {@link Root} members
	 */
	private Set<IndexedClassExpression> subsumers_;

	/**
	 * subsumers to which composition rules were applied after non-deterministic
	 * choices
	 */
	private Set<IndexedClassExpression> composedSubsumers_;

	/**
	 * subsumers to which composition rules were applied after non-deterministic
	 * choices
	 */
	private Set<IndexedClassExpression> decomposedSubsumers_;

	/**
	 * the negated super-classes of the {@link Root}
	 */
	private Set<IndexedClassExpression> negativeSubsumers_;

	/**
	 * non-deterministically derived composed subsumers that should be guessed
	 */
	private Set<IndexedClassExpression> possibleComposedSubsumers_;

	/**
	 * non-deterministically derived decomposed subsumers that should be guessed
	 */
	private Set<IndexedClassExpression> possibleDecomposedSubsumers_;

	/**
	 * the entailed existential relations
	 */
	private Multimap<IndexedObjectProperty, IndexedClassExpression> forwardLinks_;

	/**
	 * the existential relations from other {@link Root}s
	 */
	private Multimap<IndexedObjectProperty, Root> backwardLinks_;

	/**
	 * backward propagations for the keys of {@link #backwardLinks_}
	 */
	private Multimap<IndexedObjectProperty, IndexedObjectSomeValuesFrom> propagations_;

	/**
	 * all values of negative propagations should be present negatively in the
	 * roots of contexts reachable using the given links
	 */
	private Multimap<IndexedObjectProperty, IndexedClassExpression> negativePropagations_;

	/**
	 * disjunctions indexed by watch disjuncts
	 */
	private Multimap<IndexedClassExpression, IndexedClassExpression> disjunctionsPropagatedByWatched_;

	/**
	 * possible existentials that are propagated from successor {@link Root}s;
	 * they should be guessed
	 */
	//private Multimap<IndexedObjectSomeValuesFrom, Root> possibleExistentials_;
	private Multimap<IndexedObjectSomeValuesFrom, IndexedObjectProperty> possibleExistentials_;

	/**
	 * inconsistent {@link Root}s that are reachable by forward links (together
	 * with negative propagations)
	 */
	private Set<Root> inconsistentSuccessors_;

	/**
	 * propagated {@link IndexedClassExpression}s indexed by {@link Root}s for
	 * which they were generated
	 */
	private Multimap<Root, IndexedClassExpression> propagatedComposedSubsumers_;
	
	/**
	 * the derived {@link IndexedClassExpression} subsumers by
	 * {@link IndexedDisjointnessAxiom}s in which they occur as members
	 */
	private Map<IndexedDisjointnessAxiom, IndexedClassExpression[]> disjointnessAxioms_;

	/**
	 * {@link ExternalConclusion}s to which the rules are yet to be applied
	 */
	private Deque<ExternalDeterministicConclusion> toDo_;

	/**
	 * conclusions which need to be non-deterministically guessed within this
	 * context
	 */
	private Queue<PossibleConclusion> toGuess_;

	/**
	 * the {@link LocalConclusion}s that have been processed after the first
	 * non-deterministic choice point
	 */
	private Deque<LocalConclusion> localHistory_ = null;
	
	/**
	 * Stores an object with additional information, e.g. all subsumers, when computed. 
	 */
	private SaturatedContext saturated_ = null;

	Context(Root root) {
		this.root_ = root;
	}

	public Root getRoot() {
		return root_;
	}

	public boolean isSaturated() {
		return isSaturated_;
	}

	/**
	 * @return all subsumers (deterministic and non-deterministic) derived in
	 *         this {@link Context}
	 */
	public Set<IndexedClassExpression> getSubsumers() {
		if (subsumers_ == null)
			return Collections.emptySet();
		// else
		return subsumers_;
	}

	/**
	 * @return the subsumers in this {@link Context} derived after a
	 *         non-deterministic choice to which composition rules were applied
	 */
	public Set<IndexedClassExpression> getComposedSubsumers() {
		if (composedSubsumers_ == null)
			return Collections.emptySet();
		// else
		return composedSubsumers_;
	}

	/**
	 * @return the subsumers in this {@link Context} derived after a
	 *         non-deterministic choice to which decomposition rules were
	 *         applied
	 */
	public Set<IndexedClassExpression> getDecomposedSubsumers() {
		if (decomposedSubsumers_ == null)
			return Collections.emptySet();
		// else
		return decomposedSubsumers_;
	}

	/**
	 * @return the set of {@link IndexedClassExpression} which negations where
	 *         derived (possibly after a non-deterministic choice) in this
	 *         {@link Context}
	 */
	public Set<IndexedClassExpression> getNegativeSubsumers() {
		if (negativeSubsumers_ == null)
			return Collections.emptySet();
		// else
		return negativeSubsumers_;
	}

	public Multimap<IndexedClassExpression, IndexedClassExpression> getPropagatedDisjunctionsByWatched() {
		if (disjunctionsPropagatedByWatched_ == null)
			return Operations.emptyMultimap();
		// else
		return disjunctionsPropagatedByWatched_;
	}

	public Multimap<IndexedObjectProperty, IndexedClassExpression> getForwardLinks() {
		if (forwardLinks_ == null)
			return Operations.emptyMultimap();
		// else
		return forwardLinks_;
	}

	public Multimap<IndexedObjectProperty, Root> getBackwardLinks() {
		if (backwardLinks_ == null)
			return Operations.emptyMultimap();
		// else
		return backwardLinks_;
	}

	public Multimap<IndexedObjectProperty, IndexedObjectSomeValuesFrom> getPropagations() {
		if (propagations_ == null)
			return Operations.emptyMultimap();
		// else
		return propagations_;
	}

	public Multimap<IndexedObjectProperty, IndexedClassExpression> getNegativePropagations() {
		if (negativePropagations_ == null)
			return Operations.emptyMultimap();
		// else
		return negativePropagations_;
	}

	public Set<IndexedClassExpression> getPossibleComposedSubsumers() {
		if (possibleComposedSubsumers_ == null)
			return Collections.emptySet();
		// else
		return possibleComposedSubsumers_;
	}

	public Set<IndexedClassExpression> getPossibleDecomposedSubsumers() {
		if (possibleDecomposedSubsumers_ == null)
			return Collections.emptySet();
		// else
		return possibleDecomposedSubsumers_;
	}

	public Set<IndexedObjectSomeValuesFrom> getPossibleExistentials() {
		if (possibleExistentials_ == null)
			return Collections.emptySet();
		// else
		return possibleExistentials_.keySet();
	}
	
	public Collection<IndexedObjectProperty> getRelationsForPossibleExistential(IndexedObjectSomeValuesFrom possibleExistential) {
		if (possibleExistentials_ == null)
			return Collections.emptySet();
		// else
		return possibleExistentials_.get(possibleExistential);
	}

	public Set<Root> getInconsistentSuccessors() {
		if (inconsistentSuccessors_ == null)
			return Collections.emptySet();
		// else
		return inconsistentSuccessors_;
	}

	public Multimap<Root, IndexedClassExpression> getPropagatedComposedSubsumers() {
		if (propagatedComposedSubsumers_ == null)
			return Operations.emptyMultimap();
		// else
		return propagatedComposedSubsumers_;
	}

	public int getToDoSize() {
		if (toDo_ == null)
			return 0;
		// else
		return toDo_.size();
	}

	public int getToGuessSize() {
		if (toGuess_ == null)
			return 0;
		// else
		return toGuess_.size();
	}

	/**
	 * Adds the given {@link Conclusion} to this {@link Context}
	 * 
	 * @param conclusion
	 * @return {@code true} if this {@link Context} has changed as a result of
	 *         this operation
	 */
	boolean addConclusion(Conclusion conclusion) {
		boolean result = conclusion.accept(CONCLUSION_INSERTER_, this);
		LOGGER_.trace("{}: adding {}: {}", this, conclusion, result ? "success"
				: "failure");
		return result;
	}

	/**
	 * Deletes the given {@link Conclusion} from this {@link Context}
	 * 
	 * @param conclusion
	 * @return {@code true} if this {@link Context} has changed as a result of
	 *         this operation
	 */
	boolean removeConclusion(Conclusion conclusion) {
		boolean result = conclusion.accept(CONCLUSION_DELETER_, this);
		LOGGER_.trace("{}: removing {}: {}", this, conclusion,
				result ? "success" : "failure");
		return result;
	}

	/**
	 * Add the given {@link Conclusion} to be processed within this
	 * {@link Context}.
	 * 
	 * @param conclusion
	 * @return {@code true} if this {@link Context} did not have any unprocessed
	 *         {@link ExternalDeterministicConclusion}s and {@code false}
	 *         otherwise
	 */
	boolean addToDo(ExternalDeterministicConclusion conclusion) {
		boolean result = false;
		if (toDo_ == null) {
			toDo_ = new ArrayDeque<ExternalDeterministicConclusion>();
			result = true;
		}
		toDo_.add(conclusion);
		return result;
	}

	/**
	 * @return the next unprocessed {@link ExternalDeterministicConclusion}
	 *         within this {@link Context} or {@code null} if there is no such
	 *         unprocessed {@link ExternalDeterministicConclusion}.
	 */
	ExternalDeterministicConclusion takeToDo() {
		if (toDo_ == null)
			return null;
		// else
		ExternalDeterministicConclusion result = toDo_.poll();
		if (result == null) {
			toDo_ = null;
		}
		return result;
	}

	boolean addToGuess(PossibleConclusion possibleConclusion) {
		boolean result = false;
		if (toGuess_ == null) {
			toGuess_ = new ArrayDeque<PossibleConclusion>();
			result = true;
		}
		toGuess_.add(possibleConclusion);
		return result;
	}

	PossibleConclusion takeToGuess() {
		if (toGuess_ == null) {
			return null;
		}
		// else
		PossibleConclusion result = toGuess_.poll();
		if (result == null) {
			toGuess_ = null;
		}
		return result;
	}

	boolean setSaturated() {
		if (isSaturated_)
			return false;
		// else
		isSaturated_ = true;
		LOGGER_.trace("{}: set saturated", this);
		return true;
	}

	boolean setNotSaturated() {
		if (!isSaturated_)
			return false;
		// else
		isSaturated_ = false;
		LOGGER_.trace("{}: set not saturated", this);
		return true;
	}

	public void removePropagatedConclusions(Root root) {
		if (inconsistentSuccessors_ != null) {
			inconsistentSuccessors_.remove(root);
			if (inconsistentSuccessors_.isEmpty()) {
				inconsistentSuccessors_ = null;
			}
		}
		if (propagatedComposedSubsumers_ != null) {
			propagatedComposedSubsumers_.remove(root);
			if (propagatedComposedSubsumers_.isEmpty()) {
				propagatedComposedSubsumers_ = null;
			}
		}
	}
	
	public IndexedClassExpression[] getDisjointSubsumers(
			IndexedDisjointnessAxiom axiom) {
		if (disjointnessAxioms_ == null) {
			return null;
		}
		
		return disjointnessAxioms_.get(axiom);
	}

	public boolean hasClash() {
		return hasClash_;
	}

	public boolean isInconsistent() {
		return hasClash() && isDeterministic();
	}

	void pushToHistory(LocalConclusion conclusion) {
		if (localHistory_ == null)
			localHistory_ = new ArrayDeque<LocalConclusion>(16);
		LOGGER_.trace("{}: to history: {}", this, conclusion);
		localHistory_.addLast(conclusion);
	}

	LocalConclusion popHistory() {
		if (localHistory_ == null)
			return null;
		// else
		LocalConclusion result = this.localHistory_.pollLast();
		if (result == null)
			localHistory_ = null;
		return result;
	}

	@Override
	public String toString() {
		return getRoot().toString();
	}

	boolean isDeterministic() {
		return (localHistory_ == null || localHistory_.isEmpty());
	}
	
	//TODO make package protected
	public void setSaturatedContext(SaturatedContext saturated) {
		saturated_ = saturated;
	}
	
	public SaturatedContext getSaturatedContext() {
		return saturated_;
	}
	
	boolean setHistoryExplored() {
		if (historyExplored_)
			return false;
		// else
		historyExplored_ = true;
		LOGGER_.trace("{}: marked as history explored", this);
		return true;
	}
	
	boolean setHistoryNotExplored() {
		if (!historyExplored_)
			return false;
		// else
		historyExplored_ = false;
		LOGGER_.trace("{}: marked as history not explored", this);
		return true;
	}
	
	boolean isHistoryExplored() {
		return historyExplored_;
	}
	
	// returns the collection of fillers of all negative propagations for
	// super-roles of the given relation
	Collection<IndexedClassExpression> getFillersInNegativePropagations(IndexedObjectProperty relation) {
		final Set<IndexedObjectProperty> superProperties = relation.getSaturatedProperty().getSuperProperties();
		//TODO implement contains()?
		return new AbstractCollection<IndexedClassExpression>() {
			
			@Override
			public Iterator<IndexedClassExpression> iterator() {
				return new Iterator<IndexedClassExpression>() {

					private final Iterator<IndexedObjectProperty> superPropertiesIterator_ = superProperties.iterator();
					private IndexedObjectProperty currentProperty_ = null;
					private Iterator<IndexedClassExpression> currentNegativeMembers_ = null;
					private IndexedClassExpression current_ = null;

					@Override
					public boolean hasNext() {
						for (;;) {
							if (current_ != null) {
								return true;
							}

							if (currentNegativeMembers_ != null && currentNegativeMembers_.hasNext()) {
								current_ = currentNegativeMembers_.next();
								return true;
							}
							// move on the properties iterator
							if (superPropertiesIterator_.hasNext()) {
								currentProperty_ = superPropertiesIterator_.next();
								currentNegativeMembers_ = getNegativePropagations().get(currentProperty_).iterator();
							} else {
								return false;
							}
						}
					}

					@Override
					public IndexedClassExpression next() {
						if (!hasNext()) {
							throw new NoSuchElementException();
						}

						IndexedClassExpression toReturn = current_;

						current_ = null;

						return toReturn;
					}

					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}

			@Override
			public int size() {
				// lower approximation
				return Math.min(superProperties.size(), getNegativePropagations().keySet().size());
			}

		};
	}	

	static class ConclusionInserter implements
			ConclusionVisitor<Context, Boolean> {

		private boolean visitSubsumer(IndexedClassExpression expression,
				Context input) {
			if (input.subsumers_ == null)
				input.subsumers_ = new ArrayHashSet<IndexedClassExpression>(64);
			return input.subsumers_.add(expression);
		}

		@Override
		public Boolean visit(DecomposedSubsumer conclusion, Context input) {
			IndexedClassExpression expression = conclusion.getExpression();
			if (input.isDeterministic()) {
				return visitSubsumer(expression, input);
			}
			// else non-deterministic
			if (input.getSubsumers().contains(expression)
					&& !input.getComposedSubsumers().contains(expression))
				// a subsumer was derived deterministically => already
				// decomposed
				return false;
			// else
			if (input.decomposedSubsumers_ == null)
				input.decomposedSubsumers_ = new ArrayHashSet<IndexedClassExpression>(
						16);
			return input.decomposedSubsumers_.add(conclusion.getExpression());
		}

		@Override
		public Boolean visit(ComposedSubsumer conclusion, Context input) {
			IndexedClassExpression expression = conclusion.getExpression();
			if (visitSubsumer(expression, input)) {
				if (input.isDeterministic())
					return true;
				// else non-deterministic
				if (input.composedSubsumers_ == null)
					input.composedSubsumers_ = new ArrayHashSet<IndexedClassExpression>(
							16);
				return input.composedSubsumers_.add(expression);
			}
			return false;
		}

		@Override
		public Boolean visit(PossibleComposedSubsumer conclusion, Context input) {
			IndexedClassExpression expression = conclusion.getExpression();
			if (input.possibleComposedSubsumers_ == null) {
				input.possibleComposedSubsumers_ = new ArrayHashSet<IndexedClassExpression>(
						16);
			}
			return input.possibleComposedSubsumers_.add(expression);
		}

		@Override
		public Boolean visit(PossibleDecomposedSubsumer conclusion,
				Context input) {
			IndexedClassExpression expression = conclusion.getExpression();
			if (input.possibleDecomposedSubsumers_ == null) {
				input.possibleDecomposedSubsumers_ = new ArrayHashSet<IndexedClassExpression>(
						16);
			}
			return input.possibleDecomposedSubsumers_.add(expression);
		}

		@Override
		public Boolean visit(NegatedSubsumer conclusion, Context input) {
			if (input.negativeSubsumers_ == null)
				input.negativeSubsumers_ = new ArrayHashSet<IndexedClassExpression>(
						16);
			return input.negativeSubsumers_.add(conclusion
					.getNegatedExpression());
		}

		@Override
		public Boolean visit(Disjunction conclusion, Context input) {
			IndexedClassExpression watchedDisjunct = conclusion
					.getWatchedDisjunct();
			IndexedClassExpression propagatedDisjunct = conclusion
					.getPropagatedDisjunct();
			if (input.getSubsumers().contains(watchedDisjunct)
					|| input.getSubsumers().contains(propagatedDisjunct))
				// disjunction is already subsumed
				return false;
			// else
			if (input.disjunctionsPropagatedByWatched_ == null) {
				input.disjunctionsPropagatedByWatched_ = new HashSetMultimap<IndexedClassExpression, IndexedClassExpression>(
						8);
			}
			return input.disjunctionsPropagatedByWatched_.add(watchedDisjunct,
					propagatedDisjunct);
		}

		@Override
		public Boolean visit(PossiblePropagatedExistential conclusion,
				Context input) {
			if (input.possibleExistentials_ == null) {
				input.possibleExistentials_ = new HashSetMultimap<IndexedObjectSomeValuesFrom, IndexedObjectProperty>(
						8);
			}
			IndexedObjectSomeValuesFrom expression = conclusion.getExpression();
			//Root root = conclusion.getSourceRoot();
			
			//if (input.possibleExistentials_.add(expression, root)) {
			if (input.possibleExistentials_.add(expression, conclusion.getRelation())) {
				return true;
			}
			// else
			return false;
		}

		@Override
		public Boolean visit(ForwardLink conclusion, Context input) {
			if (input.forwardLinks_ == null)
				input.forwardLinks_ = new HashSetMultimap<IndexedObjectProperty, IndexedClassExpression>(
						8);
			return input.forwardLinks_.add(conclusion.getRelation(),
					conclusion.getTarget());
		}

		@Override
		public Boolean visit(BackwardLink conclusion, Context input) {
			if (input.backwardLinks_ == null)
				input.backwardLinks_ = new HashSetMultimap<IndexedObjectProperty, Root>(
						8);
			return input.backwardLinks_.add(conclusion.getRelation(),
					conclusion.getSource());
		}

		@Override
		public Boolean visit(ContextInitialization conclusion, Context input) {
			if (input.isInitialized_)
				return false;
			// else
			input.isInitialized_ = true;
			return true;
		}

		@Override
		public Boolean visit(Propagation conclusion, Context input) {
			if (input.propagations_ == null)
				input.propagations_ = new HashSetMultimap<IndexedObjectProperty, IndexedObjectSomeValuesFrom>(
						8);
			return input.propagations_.add(conclusion.getRelation(),
					conclusion.getCarry());
		}

		@Override
		public Boolean visit(NegativePropagation conclusion, Context input) {
			if (input.negativePropagations_ == null)
				input.negativePropagations_ = new HashSetMultimap<IndexedObjectProperty, IndexedClassExpression>(
						8);
			return input.negativePropagations_.add(conclusion.getRelation(),
					conclusion.getNegatedCarry());
		}

		@Override
		public Boolean visit(Clash conclusion, Context input) {
			if (input.hasClash_)
				return false;
			// else
			input.hasClash_ = true;
			return true;
		}

		@Override
		public Boolean visit(PropagatedClash conclusion, Context input) {
			if (input.inconsistentSuccessors_ == null)
				input.inconsistentSuccessors_ = new ArrayHashSet<Root>(4);
			return input.inconsistentSuccessors_
					.add(conclusion.getSourceRoot());
		}

		@Override
		public Boolean visit(PropagatedComposedSubsumer conclusion,
				Context input) {
			if (input.propagatedComposedSubsumers_ == null)
				input.propagatedComposedSubsumers_ = new HashSetMultimap<Root, IndexedClassExpression>(
						8);
			return input.propagatedComposedSubsumers_.add(
					conclusion.getSourceRoot(), conclusion.getExpression());
		}

		@Override
		public Boolean visit(ConjectureNonSubsumer conclusion, Context input) {
			if (input.negativeSubsumers_ == null)
				input.negativeSubsumers_ = new ArrayHashSet<IndexedClassExpression>(
						16);
			return input.negativeSubsumers_.add(conclusion.getExpression());
		}

		@Override
		public Boolean visit(DisjointSubsumer conclusion, Context input) {
			if (input.disjointnessAxioms_ == null) {
				input.disjointnessAxioms_ = new ArrayHashMap<IndexedDisjointnessAxiom, IndexedClassExpression[]>();
			}
			IndexedDisjointnessAxiom axiom = conclusion.getAxiom();
			IndexedClassExpression member = conclusion.getMember();
			IndexedClassExpression[] members = input.disjointnessAxioms_
					.get(axiom);
			if (members == null) {
				// at most two members are stored; it is sufficient to detect
				// inconsistency
				members = new IndexedClassExpression[2];
				input.disjointnessAxioms_.put(axiom, members);
			}
			if (members[0] == null) {
				members[0] = member;
				return true;
			}
			if (members[0] == member) {
				return false;
			}
			if (members[1] == null) {
				members[1] = member;
				return true;
			}
			// else
			return false;
		}

		@Override
		public Boolean visit(PossiblePropagation conclusion, Context input) {
			return visit((Propagation)conclusion, input);
		}

	}

	static class ConclusionDeleter implements
			ConclusionVisitor<Context, Boolean> {

		private boolean visitSubsumer(IndexedClassExpression expression,
				Context input) {
			if (input.subsumers_ == null)
				return false;
			if (input.subsumers_.remove(expression)) {
				if (input.subsumers_.isEmpty())
					input.subsumers_ = null;
				return true;
			}
			// else
			return false;
		}

		@Override
		public Boolean visit(DecomposedSubsumer conclusion, Context input) {
			IndexedClassExpression expression = conclusion.getExpression();
			if (input.isDeterministic())
				return visitSubsumer(expression, input);
			// else it was derived non-deterministically
			if (input.decomposedSubsumers_ == null)
				return false;
			if (input.decomposedSubsumers_.remove(expression)) {
				if (input.decomposedSubsumers_.isEmpty())
					input.decomposedSubsumers_ = null;
				return true;
			}
			// else
			return false;
		}

		@Override
		public Boolean visit(ComposedSubsumer conclusion, Context input) {
			IndexedClassExpression expression = conclusion.getExpression();
			if (input.composedSubsumers_ != null)
				input.composedSubsumers_.remove(expression);
			return visitSubsumer(expression, input);
		}

		@Override
		public Boolean visit(PossibleDecomposedSubsumer conclusion,
				Context input) {
			if (input.possibleDecomposedSubsumers_ == null)
				return false;
			// else
			if (input.possibleDecomposedSubsumers_.remove(conclusion
					.getExpression())) {
				if (input.possibleDecomposedSubsumers_.isEmpty())
					input.possibleDecomposedSubsumers_ = null;
				return true;
			}
			// else
			return false;
		}

		@Override
		public Boolean visit(PossibleComposedSubsumer conclusion, Context input) {
			if (input.possibleComposedSubsumers_ == null)
				return false;
			// else
			if (input.possibleComposedSubsumers_.remove(conclusion
					.getExpression())) {
				if (input.possibleComposedSubsumers_.isEmpty())
					input.possibleComposedSubsumers_ = null;
				return true;
			}
			// else
			return false;
		}

		@Override
		public Boolean visit(NegatedSubsumer conclusion, Context input) {
			if (input.negativeSubsumers_ == null)
				return false;
			if (input.negativeSubsumers_.remove(conclusion
					.getNegatedExpression())) {
				if (input.negativeSubsumers_.isEmpty())
					input.negativeSubsumers_ = null;
				return true;
			}
			// else
			return false;
		}

		@Override
		public Boolean visit(Disjunction conclusion, Context input) {
			if (input.disjunctionsPropagatedByWatched_ == null)
				return false;
			IndexedClassExpression watchedDisjunct = conclusion
					.getWatchedDisjunct();
			IndexedClassExpression propagatedDisjunct = conclusion
					.getPropagatedDisjunct();
			if (input.disjunctionsPropagatedByWatched_.remove(watchedDisjunct,
					propagatedDisjunct)) {
				if (input.disjunctionsPropagatedByWatched_.isEmpty()) {
					input.disjunctionsPropagatedByWatched_ = null;
				}
				return true;
			}
			// else
			return false;
		}

		@Override
		public Boolean visit(PossiblePropagatedExistential conclusion,
				Context input) {
			if (input.possibleExistentials_ == null)
				return false;
			IndexedObjectSomeValuesFrom expression = conclusion.getExpression();
			Root root = conclusion.getSourceRoot();
			if (input.possibleExistentials_.remove(expression, root)) {
				if (input.possibleExistentials_.isEmpty()) {
					input.possibleExistentials_ = null;
				}
				return true;
			}
			// else
			return false;
		}

		@Override
		public Boolean visit(ForwardLink conclusion, Context input) {
			if (input.forwardLinks_ == null)
				return false;
			if (input.forwardLinks_.remove(conclusion.getRelation(),
					conclusion.getTarget())) {
				if (input.forwardLinks_.isEmpty())
					input.forwardLinks_ = null;
				return true;
			}
			// else
			return false;
		}

		@Override
		public Boolean visit(BackwardLink conclusion, Context input) {
			if (input.backwardLinks_ == null)
				return false;
			if (input.backwardLinks_.remove(conclusion.getRelation(),
					conclusion.getSource())) {
				if (input.backwardLinks_.isEmpty())
					input.backwardLinks_ = null;
				return true;
			}
			// else
			return false;
		}

		@Override
		public Boolean visit(ContextInitialization conclusion, Context input) {
			if (input.isInitialized_) {
				input.isInitialized_ = false;
				return true;
			}
			// else
			return false;
		}

		@Override
		public Boolean visit(Propagation conclusion, Context input) {
			if (input.propagations_ == null)
				return false;
			if (input.propagations_.remove(conclusion.getRelation(),
					conclusion.getCarry())) {
				if (input.propagations_.isEmpty())
					input.propagations_ = null;
				return true;
			}
			// else
			return false;
		}

		@Override
		public Boolean visit(NegativePropagation conclusion, Context input) {
			if (input.negativePropagations_ == null)
				return false;
			if (input.negativePropagations_.remove(conclusion.getRelation(),
					conclusion.getNegatedCarry())) {
				if (input.negativePropagations_.isEmpty())
					input.negativePropagations_ = null;
				return true;
			}
			// else
			return false;
		}

		@Override
		public Boolean visit(Clash conclusion, Context input) {
			if (input.hasClash_) {
				input.hasClash_ = false;
				return true;
			}
			// else
			return false;
		}

		@Override
		public Boolean visit(PropagatedClash conclusion, Context input) {
			if (input.inconsistentSuccessors_ == null)
				return false;
			if (input.inconsistentSuccessors_
					.remove(conclusion.getSourceRoot())) {
				if (input.inconsistentSuccessors_.isEmpty())
					input.inconsistentSuccessors_ = null;
				return true;
			}
			// else
			return false;
		}

		@Override
		public Boolean visit(PropagatedComposedSubsumer conclusion,
				Context input) {
			if (input.propagatedComposedSubsumers_ == null)
				return false;
			if (input.propagatedComposedSubsumers_.remove(
					conclusion.getSourceRoot(), conclusion.getExpression())) {
				if (input.propagatedComposedSubsumers_.isEmpty())
					input.propagatedComposedSubsumers_ = null;
				return true;
			}
			// else
			return false;
		}

		@Override
		public Boolean visit(ConjectureNonSubsumer conclusion, Context input) {
			if (input.negativeSubsumers_ == null)
				return false;
			if (input.negativeSubsumers_.remove(conclusion.getExpression())) {
				if (input.negativeSubsumers_.isEmpty())
					input.negativeSubsumers_ = null;
				return true;
			}
			// else
			return false;
		}

		@Override
		public Boolean visit(DisjointSubsumer conclusion, Context input) {
			if (input.disjointnessAxioms_ == null) {
				return false;
			}
			IndexedDisjointnessAxiom axiom = conclusion.getAxiom();
			IndexedClassExpression member = conclusion.getMember();
			IndexedClassExpression[] members = input.disjointnessAxioms_
					.get(axiom);
			if (members == null)
				return false;
			if (members[0] == null)
				return false;
			if (members[0] == member) {
				if (members[1] == null) {
					// delete the record
					input.disjointnessAxioms_.remove(axiom);
					if (input.disjointnessAxioms_.isEmpty())
						input.disjointnessAxioms_ = null;
				} else {
					// shift
					members[0] = members[1];
					members[1] = null;
				}
				return true;
			}
			if (members[1] == null)
				return false;
			if (members[1] == member) {
				members[1] = null;
				return true;
			}
			// else
			return false;
		}

		@Override
		public Boolean visit(PossiblePropagation conclusion, Context input) {
			return visit((Propagation)conclusion, input);
		}
	}
}
