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
package org.semanticweb.elk.reasoner.saturation;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpressionList;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.BackwardLinkImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.ContextInitializationImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.DecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.SubContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Subsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.context.SubContext;
import org.semanticweb.elk.reasoner.saturation.context.SubContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.backwardlinks.BackwardLinkChainFromBackwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.backwardlinks.ContradictionOverBackwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.backwardlinks.LinkableBackwardLinkRule;
import org.semanticweb.elk.util.collections.ArrayHashMap;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.collections.chains.AbstractChain;
import org.semanticweb.elk.util.collections.chains.Chain;
import org.semanticweb.elk.util.concurrent.collections.ActivationStack;
import org.semanticweb.elk.util.concurrent.collections.SynchronizedArrayListActivationStack;

/**
 * Context implementation that is used for EL reasoning. It provides data
 * structures for storing and retrieving various types of derived expressions,
 * including computed subsumptions between class expressions.
 * 
 * @author Markus Kroetzsch
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 */
public class ContextImpl implements ExtendedContext {

	private static final ConclusionVisitor<ContextImpl, Boolean> CONCLUSION_INSERTER_ = new ConclusionInserter();
	private static final ConclusionVisitor<ContextImpl, Boolean> CONCLUSION_DELETER_ = new ConclusionDeleter();
	private static final ConclusionVisitor<ContextImpl, Boolean> CONCLUSION_OCCURRENCE_CHECKER_ = new ConclusionOccurrenceChecker();

	/**
	 * the rules that should be applied to each derived {@link BackwardLinkImpl}
	 * in this {@link Context}; can be {@code null}
	 */
	private LinkableBackwardLinkRule backwardLinkRules_ = null;

	/**
	 * the indexed representation of all derived reflexive
	 * {@link BackwardLinkImpl} s, i.e., the {@link BackwardLinkImpl}s whose
	 * source is this {@link Context}; can be {@code null}
	 * 
	 * @see BackwardLinkImpl#getOriginRoot()
	 */
	private Set<IndexedObjectProperty> reflexiveBackwardLinks_ = null;

	/**
	 * the {@link SubContext}s of this {@link Context} indexed by their root
	 * {@link IndexedObjectProperty}s
	 */
	private Map<IndexedObjectProperty, SubContext> subContextsByObjectProperty_ = null;

	/**
	 * the map from {@link IndexedClassExpressionList}s to the positions
	 * corresponding to the derived {@link IndexedClassExpression} subsumers
	 */
	private Map<IndexedClassExpressionList, Set<Integer>> disjointnessAxioms_;

	/**
	 * {@code true} if {@code owl:Nothing} is stored in
	 * {@link #composedSubsumers_}
	 */
	private volatile boolean isInconsistent_ = false;

	/**
	 * {@code true} if it is not initialized or otherwise all derived
	 * {@link Subsumer}s of {@link #root_} have been computed.
	 */
	private volatile boolean isSaturated_ = true;

	/**
	 * the {@link IndexedContextRoot} for which the {@link #composedSubsumers_}
	 * are computed
	 */
	private final IndexedContextRoot root_;

	/**
	 * the derived {@link IndexedClassExpression}s that subsume {@link #root_}
	 * obtained by composition rules
	 */
	private final Set<IndexedClassExpression> composedSubsumers_;

	/**
	 * the derived {@link IndexedClassExpression}s that subsume {@link #root_}
	 * obtained by decomposition rules
	 */
	private final Set<IndexedClassExpression> decomposedSubsumers_;

	/**
	 * the queue of unprocessed {@code Conclusion}s of this {@link Context}
	 */
	private final ActivationStack<Conclusion> toDo_;

	/**
	 * {@code true} if this {@link Context} is initialized, i.e., contains
	 * {@link ContextInitializationImpl}
	 */
	private volatile boolean isInitialized_ = false;

	/**
	 * the number of {@link Conclusion}s contained in this {@code ConclusionSet}
	 */
	private int size = 0;

	/**
	 * Construct a new {@link Context} for the given {@link IndexedContextRoot}.
	 * Initially, the context is not active.
	 * 
	 * @param root
	 */
	public ContextImpl(IndexedContextRoot root) {
		this.root_ = root;
		this.toDo_ = new SynchronizedArrayListActivationStack<Conclusion>();
		this.composedSubsumers_ = new ArrayHashSet<IndexedClassExpression>(16);
		this.decomposedSubsumers_ = new ArrayHashSet<IndexedClassExpression>(8);
	}

	@Override
	public boolean addConclusion(Conclusion conclusion) {
		boolean success = conclusion.accept(CONCLUSION_INSERTER_, this);
		if (success)
			size++;
		return success;
	}

	@Override
	public boolean removeConclusion(Conclusion conclusion) {
		boolean success = conclusion.accept(CONCLUSION_DELETER_, this);
		if (success)
			size--;
		return success;
	}

	@Override
	public boolean containsConclusion(Conclusion conclusion) {
		return conclusion.accept(CONCLUSION_OCCURRENCE_CHECKER_, this);
	}

	@Override
	public boolean isEmpty() {
		return size == 0;

	}

	@Override
	public boolean isEmpty(IndexedObjectProperty subRoot) {
		if (subContextsByObjectProperty_ == null)
			return true;
		// else
		SubContext subContext = subContextsByObjectProperty_.get(subRoot);
		return subContext == null || subContext.isEmpty();
	}

	@Override
	public boolean addToDo(Conclusion conclusion) {
		return toDo_.push(conclusion);
	}

	@Override
	public Map<IndexedObjectProperty, ? extends SubContextPremises> getSubContextPremisesByObjectProperty() {
		if (subContextsByObjectProperty_ == null)
			return Collections.emptyMap();
		// else
		return subContextsByObjectProperty_;
	}

	SubContext getCreateSubContext(IndexedObjectProperty subRoot) {
		if (subContextsByObjectProperty_ == null)
			subContextsByObjectProperty_ = new ArrayHashMap<IndexedObjectProperty, SubContext>(
					3);
		SubContext result = subContextsByObjectProperty_.get(subRoot);
		if (result == null) {
			result = new SubContextImpl();
			subContextsByObjectProperty_.put(subRoot, result);
		}
		return result;
	}

	public boolean removeSubContext(IndexedPropertyChain subRoot) {
		if (subContextsByObjectProperty_ == null)
			return false;
		boolean changed = subContextsByObjectProperty_.remove(subRoot) != null;
		if (changed && subContextsByObjectProperty_.isEmpty())
			subContextsByObjectProperty_ = null;
		return changed;
	}

	@Override
	public Chain<LinkableBackwardLinkRule> getBackwardLinkRuleChain() {
		return new AbstractChain<LinkableBackwardLinkRule>() {

			@Override
			public LinkableBackwardLinkRule next() {
				return backwardLinkRules_;
			}

			@Override
			public void setNext(LinkableBackwardLinkRule tail) {
				backwardLinkRules_ = tail;
			}
		};
	}

	@Override
	public LinkableBackwardLinkRule getBackwardLinkRuleHead() {
		return backwardLinkRules_;
	}

	@Override
	public Set<IndexedObjectProperty> getLocalReflexiveObjectProperties() {
		return reflexiveBackwardLinks_ == null ? Collections
				.<IndexedObjectProperty> emptySet() : reflexiveBackwardLinks_;
	}

	@Override
	public IndexedContextRoot getRoot() {
		return root_;
	}

	@Override
	public Set<IndexedClassExpression> getComposedSubsumers() {
		return composedSubsumers_;
	}

	@Override
	public Set<IndexedClassExpression> getDecomposedSubsumers() {
		return decomposedSubsumers_;
	}

	/*
	 * @Override public boolean isInconsistForDisjointnessAxiom(
	 * IndexedDisjointnessAxiom axiom) { if (disjointnessAxioms_ == null) return
	 * false; IndexedClassExpression[] positions = disjointnessAxioms_.get(axiom);
	 * if (positions == null) return false; // check if both positions are not null;
	 * this is always when the second // position is not null return (positions[1]
	 * != null); }
	 */

	@Override
	public Set<? extends Integer> getSubsumerPositions(
			IndexedClassExpressionList disjoint) {
		if (disjointnessAxioms_ == null) {
			return null;
		}

		return disjointnessAxioms_.get(disjoint);
	}

	@Override
	public boolean isSaturated() {
		return isSaturated_;
	}

	@Override
	public boolean isInitialized() {
		return isInitialized_;
	}

	@Override
	public Conclusion takeToDo() {
		return toDo_.pop();
	}

	@Override
	public String toString() {
		return root_.toString() + (this != root_.getContext() ? "[local]" : "");
	}

	@Override
	public synchronized boolean setSaturated(boolean saturated) {
		// synchronized to ensure consistency when updated from two workers
		boolean previous = isSaturated_;
		isSaturated_ = saturated;
		return previous;
	}

	@Override
	public Iterable<? extends IndexedObjectSomeValuesFrom> getPropagatedSubsumers(
			IndexedPropertyChain subRoot) {
		if (subContextsByObjectProperty_ == null) {
			return Collections.emptyList();
		}

		SubContext subContext = subContextsByObjectProperty_.get(subRoot);

		if (subContext == null) {
			return Collections.emptyList();
		}

		return subContext.getPropagatedSubsumers();
	}

	private static class ConclusionInserter implements
			ConclusionVisitor<ContextImpl, Boolean> {

		@Override
		public Boolean visit(BackwardLink subConclusion, ContextImpl input) {
			IndexedObjectProperty relation = subConclusion
					.getBackwardRelation();
			// make sure that relevant context always exists
			SubContext subContext = input.getCreateSubContext(relation);
			if (subConclusion.getOriginRoot() == input.root_) {
				// reflexive
				if (input.reflexiveBackwardLinks_ == null) {
					input.reflexiveBackwardLinks_ = new ArrayHashSet<IndexedObjectProperty>(
							3);
				}
				return input.reflexiveBackwardLinks_.add(relation);
			}
			// else non-reflexive
			return subContext.addSubConclusion(subConclusion);
		}

		@Override
		public Boolean visit(ComposedSubsumer conclusion, ContextImpl input) {
			return input.composedSubsumers_.add(conclusion.getExpression());
		}

		@Override
		public Boolean visit(DecomposedSubsumer conclusion, ContextImpl input) {
			return input.decomposedSubsumers_.add(conclusion.getExpression());
		}

		@Override
		public Boolean visit(ContextInitialization conclusion, ContextImpl input) {
			if (input.isInitialized_)
				// nothing changes
				return false;
			// else
			input.isInitialized_ = true;
			return true;
		}

		@Override
		public Boolean visit(Contradiction conclusion, ContextImpl input) {
			boolean before = input.isInconsistent_;
			input.isInconsistent_ = true;
			ContradictionOverBackwardLinkRule.addTo(input);
			return before != input.isInconsistent_;
		}

		@Override
		public Boolean visit(DisjointSubsumer conclusion, ContextImpl input) {
			if (input.disjointnessAxioms_ == null) {
				input.disjointnessAxioms_ = new ArrayHashMap<IndexedClassExpressionList, Set<Integer>>();
			}
			IndexedClassExpressionList disjoint = conclusion.getDisjointExpressions();
			int position = conclusion.getPosition();
			Set<Integer> positions = input.disjointnessAxioms_
					.get(disjoint);
			if (positions == null) {
				positions = new ArrayHashSet<Integer>(2);
				input.disjointnessAxioms_.put(disjoint, positions);
			}
			if (positions.contains(position)) {
				return false;
			}
			// else
			positions.add(position);
			return true;
		}

		@Override
		public Boolean visit(ForwardLink conclusion, ContextImpl input) {
			return BackwardLinkChainFromBackwardLinkRule.addRuleFor(conclusion,
					input);
		}

		@Override
		public Boolean visit(Propagation subConclusion, ContextImpl input) {
			return input.getCreateSubContext(subConclusion.getRelation())
					.addSubConclusion(subConclusion);
		}

		@Override
		public Boolean visit(SubContextInitialization subConclusion,
				ContextImpl input) {
			return input.getCreateSubContext(
					subConclusion.getConclusionSubRoot()).addSubConclusion(
					subConclusion);
		}

	}

	private static class ConclusionDeleter implements
			ConclusionVisitor<ContextImpl, Boolean> {

		@Override
		public Boolean visit(BackwardLink subConclusion, ContextImpl input) {
			boolean changed = false;
			IndexedObjectProperty relation = subConclusion
					.getBackwardRelation();
			SubContext subContext = input.getCreateSubContext(relation);
			if (subConclusion.getOriginRoot() == input.root_) {
				// link is reflexive
				if (input.reflexiveBackwardLinks_ != null) {
					changed = input.reflexiveBackwardLinks_.remove(relation);
					if (input.reflexiveBackwardLinks_.isEmpty()) {
						input.reflexiveBackwardLinks_ = null;
					}
				}
			} else {
				// link is not reflexive
				if (subContext == null)
					return false;
				// else
				changed = subContext.removeSubConclusion(subConclusion);
			}
			return changed;
		}

		@Override
		public Boolean visit(ComposedSubsumer conclusion, ContextImpl input) {
			return input.composedSubsumers_.remove(conclusion.getExpression());
		}

		@Override
		public Boolean visit(DecomposedSubsumer conclusion, ContextImpl input) {
			return input.decomposedSubsumers_
					.remove(conclusion.getExpression());
		}

		@Override
		public Boolean visit(ContextInitialization conclusion, ContextImpl input) {
			if (!input.isInitialized_)
				// nothing changes
				return false;
			// else
			input.isInitialized_ = false;
			return true;
		}

		@Override
		public Boolean visit(Contradiction conclusion, ContextImpl input) {
			boolean before = input.isInconsistent_;
			input.isInconsistent_ = false;
			ContradictionOverBackwardLinkRule.removeFrom(input);
			return before != input.isInconsistent_;
		}

		@Override
		public Boolean visit(DisjointSubsumer conclusion, ContextImpl input) {
			if (input.disjointnessAxioms_ == null) {
				return false;
			}
			IndexedClassExpressionList disjoint = conclusion.getDisjointExpressions();
			int position = conclusion.getPosition();
			Set<Integer> positions = input.disjointnessAxioms_.get(disjoint);
			if (positions == null) {
				return false;
			}	
			// else
			return (positions.remove(position));
		}

		@Override
		public Boolean visit(ForwardLink conclusion, ContextImpl input) {
			return BackwardLinkChainFromBackwardLinkRule.removeRuleFor(
					conclusion, input);
		}

		@Override
		public Boolean visit(Propagation subConclusion, ContextImpl input) {
			SubContext subContext = input.getCreateSubContext(subConclusion
					.getRelation());
			if (subContext == null)
				return false;
			// else
			return subContext.removeSubConclusion(subConclusion);
		}

		@Override
		public Boolean visit(SubContextInitialization subConclusion,
				ContextImpl input) {
			SubContext subContext = input.getCreateSubContext(subConclusion
					.getConclusionSubRoot());
			if (subContext == null)
				return false;
			// else
			return subContext.removeSubConclusion(subConclusion);
		}

	}

	private static class ConclusionOccurrenceChecker implements
			ConclusionVisitor<ContextImpl, Boolean> {

		@Override
		public Boolean visit(BackwardLink subConclusion, ContextImpl input) {
			if (subConclusion.getOriginRoot() == input.root_) {
				// reflexive
				return input.reflexiveBackwardLinks_ != null
						&& input.reflexiveBackwardLinks_.contains(subConclusion
								.getBackwardRelation());
			}
			// else non-reflexive
			SubContext subContext = input.getCreateSubContext(subConclusion
					.getBackwardRelation());
			return subContext != null
					&& subContext.containsSubConclusion(subConclusion);
		}

		@Override
		public Boolean visit(ComposedSubsumer conclusion, ContextImpl input) {
			return input.composedSubsumers_
					.contains(conclusion.getExpression());
		}

		@Override
		public Boolean visit(DecomposedSubsumer conclusion, ContextImpl input) {
			return input.decomposedSubsumers_.contains(conclusion
					.getExpression());
		}

		@Override
		public Boolean visit(ContextInitialization conclusion, ContextImpl input) {
			return input.isInitialized_;
		}

		@Override
		public Boolean visit(Contradiction conclusion, ContextImpl input) {
			return input.isInconsistent_;
		}

		@Override
		public Boolean visit(DisjointSubsumer conclusion, ContextImpl input) {
			if (input.disjointnessAxioms_ == null) {
				return false;
			}
			IndexedClassExpressionList disjoint = conclusion.getDisjointExpressions();
			int position = conclusion.getPosition();
			Set<Integer> positions = input.disjointnessAxioms_.get(disjoint);
			if (positions == null) {
				return false;
			}
			// else
			return positions.contains(position);
		}

		@Override
		public Boolean visit(ForwardLink conclusion, ContextImpl input) {
			return BackwardLinkChainFromBackwardLinkRule.containsRuleFor(
					conclusion, input);
		}

		@Override
		public Boolean visit(Propagation subConclusion, ContextImpl input) {
			SubContext subContext = input.getCreateSubContext(subConclusion
					.getRelation());
			if (subContext == null)
				return false;
			// else
			return subContext.containsSubConclusion(subConclusion);
		}

		@Override
		public Boolean visit(SubContextInitialization subConclusion,
				ContextImpl input) {
			SubContext subContext = input.getCreateSubContext(subConclusion
					.getConclusionSubRoot());
			if (subContext == null)
				return false;
			// else
			return subContext.containsSubConclusion(subConclusion);
		}

	}
}
