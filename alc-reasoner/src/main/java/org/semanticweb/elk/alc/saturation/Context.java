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

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.Queue;
import java.util.Set;

import org.semanticweb.elk.alc.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.alc.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.alc.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Clash;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.DecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Disjunction;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ExternalConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ExternalDeterministicConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.NegatedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.NegativePropagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.PossibleSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.PropagatedClash;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Subsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
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
	 * {@code true} if this {@link Context} is initialized
	 */
	private boolean isInitialized_ = false;

	/**
	 * {@code true} if some inconsistency has been derived in this
	 * {@link Context}
	 */
	private boolean hasClash_ = false;

	/**
	 * the common super-classes of the {@link Root}
	 */
	private Set<IndexedClassExpression> subsumers_;

	/**
	 * the negated super-classes of the {@link Root}
	 */
	private Set<IndexedClassExpression> negativeSubsumers_;

	/**
	 * deterministically derived composed subsumers that should be guessed
	 */
	private Set<IndexedClassExpression> maskedPossibleSubsumers_;

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
	 * forward propagations of negations
	 */
	private Multimap<IndexedObjectProperty, IndexedClassExpression> negativePropagations_;

	/**
	 * disjunctions indexed by disjuncts
	 */
	private Multimap<IndexedClassExpression, IndexedClassExpression> disjunctions_;

	/**
	 * inconsistent {@link Root}s that are reachable by forward links
	 */
	private Multimap<IndexedObjectProperty, Root> inconsistentSuccessors_;

	/**
	 * {@link ExternalConclusion}s to which the rules are yet to be applied
	 */
	private Deque<ExternalDeterministicConclusion> toDo_;

	/**
	 * subsumers which need to be guessed within this context
	 */
	private Queue<PossibleSubsumer> toGuess_;

	/**
	 * the {@link Conclusion}s that have been processed after the first
	 * non-deterministic choice point
	 */
	private Deque<Conclusion> history_ = null;

	Context(Root root) {
		this.root_ = root;
	}

	public Root getRoot() {
		return root_;
	}

	public Set<IndexedClassExpression> getSubsumers() {
		if (subsumers_ == null)
			return Collections.emptySet();
		// else
		return subsumers_;
	}

	public Set<IndexedClassExpression> getNegativeSubsumers() {
		if (negativeSubsumers_ == null)
			return Collections.emptySet();
		// else
		return negativeSubsumers_;
	}

	public Multimap<IndexedClassExpression, IndexedClassExpression> getDisjunctions() {
		if (disjunctions_ == null)
			return Operations.emptyMultimap();
		// else
		return disjunctions_;
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

	public Set<IndexedClassExpression> getMaskedPossibleSubsumers() {
		if (maskedPossibleSubsumers_ == null)
			return Collections.emptySet();
		// else
		return maskedPossibleSubsumers_;
	}

	public Multimap<IndexedObjectProperty, Root> getInconsistentSuccessors() {
		if (inconsistentSuccessors_ == null)
			return Operations.emptyMultimap();
		// else
		return inconsistentSuccessors_;
	}

	public Set<IndexedClassExpression> getPossibleSubsumers() {
		if (history_ == null)
			return Collections.emptySet();
		Set<IndexedClassExpression> result = new ArrayHashSet<IndexedClassExpression>(
				32);
		for (Conclusion nonDeterministic : history_) {
			if (nonDeterministic instanceof Subsumer) {
				result.add(((Subsumer) nonDeterministic).getExpression());
			}
		}
		return result;
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

	boolean addToGuess(PossibleSubsumer possibleConclusion) {
		boolean result = false;
		if (toGuess_ == null) {
			toGuess_ = new ArrayDeque<PossibleSubsumer>();
			result = true;
		}
		toGuess_.add(possibleConclusion);
		return result;
	}

	PossibleSubsumer takeToGuess() {
		if (toGuess_ == null) {
			return null;
		}
		// else
		PossibleSubsumer result = toGuess_.poll();
		if (result == null) {
			toGuess_ = null;
		}
		return result;
	}

	public void removePropagatedConclusions(IndexedObjectProperty property) {
		if (inconsistentSuccessors_ == null)
			return;
		// else
		inconsistentSuccessors_.remove(property);
	}

	public void removePropagatedConclusions(IndexedObjectProperty property,
			Root root) {
		if (inconsistentSuccessors_ == null)
			return;
		// else
		inconsistentSuccessors_.remove(property, root);
	}

	public boolean hasClash() {
		return hasClash_;
	}

	public boolean isInconsistent() {
		return hasClash() && isDeterministic();
	}

	void pushToHistory(Conclusion conclusion) {
		if (history_ == null)
			history_ = new ArrayDeque<Conclusion>(16);
		LOGGER_.trace("{}: to history: {}", this, conclusion);
		history_.addLast(conclusion);
	}

	Conclusion popHistory() {
		if (history_ == null)
			return null;
		// else
		Conclusion result = this.history_.pollLast();
		if (result == null)
			history_ = null;
		else
			LOGGER_.trace("{}: taken from history: {}", this, result);
		return result;
	}

	@Override
	public String toString() {
		return getRoot().toString();
	}

	boolean isDeterministic() {
		return (history_ == null || history_.isEmpty());
	}

	static class ConclusionInserter implements
			ConclusionVisitor<Context, Boolean> {

		public static boolean visit(Subsumer conclusion, Context input) {
			if (input.subsumers_ == null)
				input.subsumers_ = new ArrayHashSet<IndexedClassExpression>(64);
			return input.subsumers_.add(conclusion.getExpression());
		}

		@Override
		public Boolean visit(DecomposedSubsumer conclusion, Context input) {
			return visit((Subsumer) conclusion, input);
		}

		@Override
		public Boolean visit(ComposedSubsumer conclusion, Context input) {
			return visit((Subsumer) conclusion, input);
		}

		@Override
		public Boolean visit(PossibleSubsumer conclusion, Context input) {
			boolean inserted = visit((Subsumer) conclusion, input);
			if (!inserted) {
				if (input.maskedPossibleSubsumers_ == null)
					input.maskedPossibleSubsumers_ = new ArrayHashSet<IndexedClassExpression>(
							8);
				input.maskedPossibleSubsumers_.add(conclusion.getExpression());
			}
			return inserted;
		}

		@Override
		public Boolean visit(NegatedSubsumer conclusion, Context input) {
			if (input.negativeSubsumers_ == null)
				input.negativeSubsumers_ = new ArrayHashSet<IndexedClassExpression>(
						32);
			return input.negativeSubsumers_.add(conclusion
					.getNegatedExpression());
		}

		@Override
		public Boolean visit(Disjunction conclusion, Context input) {
			if (input.disjunctions_ == null)
				input.disjunctions_ = new HashSetMultimap<IndexedClassExpression, IndexedClassExpression>(
						16);
			return input.disjunctions_.add(conclusion.getWatchedDisjunct(),
					conclusion.getPropagatedDisjunct());
		}

		@Override
		public Boolean visit(ForwardLink conclusion, Context input) {
			if (input.forwardLinks_ == null)
				input.forwardLinks_ = new HashSetMultimap<IndexedObjectProperty, IndexedClassExpression>(
						16);
			return input.forwardLinks_.add(conclusion.getRelation(),
					conclusion.getTarget());
		}

		@Override
		public Boolean visit(BackwardLink conclusion, Context input) {
			if (input.backwardLinks_ == null)
				input.backwardLinks_ = new HashSetMultimap<IndexedObjectProperty, Root>(
						16);
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
						16);
			return input.propagations_.add(conclusion.getRelation(),
					conclusion.getCarry());
		}

		@Override
		public Boolean visit(NegativePropagation conclusion, Context input) {
			if (input.negativePropagations_ == null)
				input.negativePropagations_ = new HashSetMultimap<IndexedObjectProperty, IndexedClassExpression>(
						16);
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
				input.inconsistentSuccessors_ = new HashSetMultimap<IndexedObjectProperty, Root>(
						4);
			return input.inconsistentSuccessors_.add(conclusion.getRelation(),
					conclusion.getSourceRoot());
		}

	}

	static class ConclusionDeleter implements
			ConclusionVisitor<Context, Boolean> {

		public static boolean visit(Subsumer conclusion, Context input) {
			if (input.subsumers_ == null)
				return false;
			if (input.subsumers_.remove(conclusion.getExpression())) {
				if (input.subsumers_.isEmpty())
					input.subsumers_ = null;
				if (input.maskedPossibleSubsumers_ != null) {
					input.maskedPossibleSubsumers_.remove(conclusion
							.getExpression());
				}
				return true;
			}
			// else
			return false;
		}

		@Override
		public Boolean visit(DecomposedSubsumer conclusion, Context input) {
			return visit((Subsumer) conclusion, input);
		}

		@Override
		public Boolean visit(ComposedSubsumer conclusion, Context input) {
			return visit((Subsumer) conclusion, input);
		}

		@Override
		public Boolean visit(PossibleSubsumer conclusion, Context input) {
			return visit((Subsumer) conclusion, input);
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
			if (input.disjunctions_ == null)
				return false;
			if (input.disjunctions_.remove(conclusion.getWatchedDisjunct(),
					conclusion.getPropagatedDisjunct())) {
				if (input.disjunctions_.isEmpty())
					input.disjunctions_ = null;
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
			if (input.inconsistentSuccessors_.remove(conclusion.getRelation(),
					conclusion.getSourceRoot())) {
				if (input.inconsistentSuccessors_.isEmpty())
					input.inconsistentSuccessors_ = null;
				return true;
			}
			// else
			return false;
		}

	}
}
