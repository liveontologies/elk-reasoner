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

import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpressionList;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.BackwardLinkImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.ContextInitializationImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassInconsistency;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionDecomposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubContextInitialization;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.context.SubContext;
import org.semanticweb.elk.reasoner.saturation.context.SubContextPremises;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference;
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
	 * @see BackwardLinkImpl#getTraceRoot()
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
	 * {@link SubClassInclusion}s for {@link #root_} have been computed.
	 * @see SubClassInclusion#getDestination()
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
	 * the queue of unprocessed {@code ClassInference}s of this {@link Context}
	 */
	private final ActivationStack<ClassInference> toDo_;

	/**
	 * {@code true} if this {@link Context} is initialized, i.e., contains
	 * {@link ContextInitializationImpl}
	 */
	private volatile boolean isInitialized_ = false;

	/**
	 * the number of different {@link ClassConclusion}s contained in this {@code ConclusionSet}
	 */
	private int size = 0;

	/**
	 * Construct a new {@link Context} for the given {@link IndexedContextRoot}.
	 * Initially, the context is not active.
	 * 
	 * @param root
	 *            the {@link IndexedContextRoot} for which to construct the
	 *            {@link Context}
	 * @see Context#getRoot()
	 */
	public ContextImpl(IndexedContextRoot root) {
		this.root_ = root;
		this.toDo_ = new SynchronizedArrayListActivationStack<ClassInference>();
		this.composedSubsumers_ = new ArrayHashSet<IndexedClassExpression>(16);
		this.decomposedSubsumers_ = new ArrayHashSet<IndexedClassExpression>(8);
	}

	@Override
	public boolean addConclusion(ClassConclusion conclusion) {
		boolean success = conclusion.accept(new ConclusionInserter());
		if (success)
			size++;
		return success;
	}

	@Override
	public boolean removeConclusion(ClassConclusion conclusion) {
		boolean success = conclusion.accept(new ConclusionDeleter());
		if (success)
			size--;
		return success;
	}

	@Override
	public boolean containsConclusion(ClassConclusion conclusion) {
		return conclusion.accept(new ConclusionOccurrenceChecker());
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
	public boolean addToDo(ClassInference inference) {
		return toDo_.push(inference);
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
	public ClassInference takeToDo() {
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

	private class ConclusionInserter
			implements
				ClassConclusion.Visitor<Boolean> {

		@Override
		public Boolean visit(BackwardLink subConclusion) {
			IndexedObjectProperty relation = subConclusion
					.getRelation();
			// make sure that relevant context always exists
			SubContext subContext = getCreateSubContext(relation);
			if (subConclusion.getTraceRoot() == root_) {
				// reflexive
				if (reflexiveBackwardLinks_ == null) {
					reflexiveBackwardLinks_ = new ArrayHashSet<IndexedObjectProperty>(
							3);
				}
				return reflexiveBackwardLinks_.add(relation);
			}
			// else non-reflexive
			return subContext.addSubConclusion(subConclusion);
		}

		@Override
		public Boolean visit(SubClassInclusionComposed conclusion) {
			return composedSubsumers_.add(conclusion.getSubsumer());
		}

		@Override
		public Boolean visit(SubClassInclusionDecomposed conclusion) {
			return decomposedSubsumers_.add(conclusion.getSubsumer());
		}

		@Override
		public Boolean visit(ContextInitialization conclusion) {
			if (isInitialized_)
				// nothing changes
				return false;
			// else
			isInitialized_ = true;
			return true;
		}

		@Override
		public Boolean visit(ClassInconsistency conclusion) {
			boolean before = isInconsistent_;
			isInconsistent_ = true;
			ContradictionOverBackwardLinkRule.addTo(ContextImpl.this);
			return before != isInconsistent_;
		}

		@Override
		public Boolean visit(DisjointSubsumer conclusion) {
			if (disjointnessAxioms_ == null) {
				disjointnessAxioms_ = new ArrayHashMap<IndexedClassExpressionList, Set<Integer>>();
			}
			IndexedClassExpressionList disjoint = conclusion.getDisjointExpressions();
			int position = conclusion.getPosition();
			Set<Integer> positions = disjointnessAxioms_
					.get(disjoint);
			if (positions == null) {
				positions = new ArrayHashSet<Integer>(2);
				disjointnessAxioms_.put(disjoint, positions);
			}
			if (positions.contains(position)) {
				return false;
			}
			// else
			positions.add(position);
			return true;
		}

		@Override
		public Boolean visit(ForwardLink conclusion) {
			return BackwardLinkChainFromBackwardLinkRule.addRuleFor(conclusion,
					ContextImpl.this);
		}

		@Override
		public Boolean visit(Propagation subConclusion) {
			return getCreateSubContext(subConclusion.getSubDestination())
					.addSubConclusion(subConclusion);
		}

		@Override
		public Boolean visit(SubContextInitialization subConclusion) {
			return getCreateSubContext(
					subConclusion.getSubDestination()).addSubConclusion(
					subConclusion);
		}

	}

	private class ConclusionDeleter
			implements
				ClassConclusion.Visitor<Boolean> {

		@Override
		public Boolean visit(BackwardLink subConclusion) {
			boolean changed = false;
			IndexedObjectProperty relation = subConclusion
					.getRelation();
			SubContext subContext = getCreateSubContext(relation);
			if (subConclusion.getTraceRoot() == root_) {
				// link is reflexive
				if (reflexiveBackwardLinks_ != null) {
					changed = reflexiveBackwardLinks_.remove(relation);
					if (reflexiveBackwardLinks_.isEmpty()) {
						reflexiveBackwardLinks_ = null;
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
		public Boolean visit(SubClassInclusionComposed conclusion) {
			return composedSubsumers_.remove(conclusion.getSubsumer());
		}

		@Override
		public Boolean visit(SubClassInclusionDecomposed conclusion) {
			return decomposedSubsumers_
					.remove(conclusion.getSubsumer());
		}

		@Override
		public Boolean visit(ContextInitialization conclusion) {
			if (!isInitialized_)
				// nothing changes
				return false;
			// else
			isInitialized_ = false;
			return true;
		}

		@Override
		public Boolean visit(ClassInconsistency conclusion) {
			boolean before = isInconsistent_;
			isInconsistent_ = false;
			ContradictionOverBackwardLinkRule.removeFrom(ContextImpl.this);
			return before != isInconsistent_;
		}

		@Override
		public Boolean visit(DisjointSubsumer conclusion) {
			if (disjointnessAxioms_ == null) {
				return false;
			}
			IndexedClassExpressionList disjoint = conclusion.getDisjointExpressions();
			int position = conclusion.getPosition();
			Set<Integer> positions = disjointnessAxioms_.get(disjoint);
			if (positions == null) {
				return false;
			}	
			// else
			return (positions.remove(position));
		}

		@Override
		public Boolean visit(ForwardLink conclusion) {
			return BackwardLinkChainFromBackwardLinkRule.removeRuleFor(
					conclusion, ContextImpl.this);
		}

		@Override
		public Boolean visit(Propagation subConclusion) {
			SubContext subContext = getCreateSubContext(subConclusion
					.getSubDestination());
			if (subContext == null)
				return false;
			// else
			return subContext.removeSubConclusion(subConclusion);
		}

		@Override
		public Boolean visit(SubContextInitialization subConclusion) {
			SubContext subContext = getCreateSubContext(subConclusion
					.getSubDestination());
			if (subContext == null)
				return false;
			// else
			return subContext.removeSubConclusion(subConclusion);
		}

	}

	private class ConclusionOccurrenceChecker implements
			ClassConclusion.Visitor<Boolean> {

		@Override
		public Boolean visit(BackwardLink subConclusion) {
			if (subConclusion.getTraceRoot() == root_) {
				// reflexive
				return reflexiveBackwardLinks_ != null
						&& reflexiveBackwardLinks_.contains(subConclusion
								.getRelation());
			}
			// else non-reflexive
			SubContext subContext = getCreateSubContext(subConclusion
					.getRelation());
			return subContext != null
					&& subContext.containsSubConclusion(subConclusion);
		}

		@Override
		public Boolean visit(SubClassInclusionComposed conclusion) {
			return composedSubsumers_
					.contains(conclusion.getSubsumer());
		}

		@Override
		public Boolean visit(SubClassInclusionDecomposed conclusion) {
			return decomposedSubsumers_.contains(conclusion
					.getSubsumer());
		}

		@Override
		public Boolean visit(ContextInitialization conclusion) {
			return isInitialized_;
		}

		@Override
		public Boolean visit(ClassInconsistency conclusion) {
			return isInconsistent_;
		}

		@Override
		public Boolean visit(DisjointSubsumer conclusion) {
			if (disjointnessAxioms_ == null) {
				return false;
			}
			IndexedClassExpressionList disjoint = conclusion.getDisjointExpressions();
			int position = conclusion.getPosition();
			Set<Integer> positions = disjointnessAxioms_.get(disjoint);
			if (positions == null) {
				return false;
			}
			// else
			return positions.contains(position);
		}

		@Override
		public Boolean visit(ForwardLink conclusion) {
			return BackwardLinkChainFromBackwardLinkRule.containsRuleFor(
					conclusion, ContextImpl.this);
		}

		@Override
		public Boolean visit(Propagation subConclusion) {
			SubContext subContext = getCreateSubContext(subConclusion
					.getSubDestination());
			if (subContext == null)
				return false;
			// else
			return subContext.containsSubConclusion(subConclusion);
		}

		@Override
		public Boolean visit(SubContextInitialization subConclusion) {
			SubContext subContext = getCreateSubContext(subConclusion
					.getSubDestination());
			if (subContext == null)
				return false;
			// else
			return subContext.containsSubConclusion(subConclusion);
		}

	}
}
