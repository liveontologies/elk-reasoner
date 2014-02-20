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
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointnessAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.DecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.SubContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.Subsumer;
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
	 * the rules that should be applied to each derived {@link BackwardLink} in
	 * this {@link Context}; can be {@code null}
	 */
	private LinkableBackwardLinkRule backwardLinkRules_ = null;

	/**
	 * the indexed representation of all derived reflexive {@link BackwardLink}
	 * s, i.e., the {@link BackwardLink}s whose source is this {@link Context};
	 * can be {@code null}
	 * 
	 * @see BackwardLink#getSource()
	 */
	private Set<IndexedPropertyChain> reflexiveBackwardLinks_ = null;

	/**
	 * the {@link SubContext}s of this {@link Context} indexed by their root
	 * {@link IndexedPropertyChain}s
	 */
	private Map<IndexedPropertyChain, SubContext> subContextsByObjectProperty_ = null;

	/**
	 * the derived {@link IndexedClassExpression} subsumers by
	 * {@link IndexedDisjointnessAxiom}s in which they occur as members
	 */
	private Map<IndexedDisjointnessAxiom, IndexedClassExpression[]> disjointnessAxioms_;

	/**
	 * {@code true} if {@code owl:Nothing} is stored in {@link #subsumers_}
	 */
	private volatile boolean isInconsistent_ = false;

	/**
	 * {@code true} if it is not initialized or otherwise all derived
	 * {@link Subsumer} of {@link #root_} have been computed.
	 */
	private volatile boolean isSaturated_ = true;

	/**
	 * the root {@link IndexedClassExpression} for which the {@link #subsumers_}
	 * are computed
	 * 
	 */
	private final IndexedClassExpression root_;

	/**
	 * the derived {@link IndexedClassExpression}s that are subsumers (i.e,
	 * super-classes) of {@link #root_}
	 */
	private final Set<IndexedClassExpression> subsumers_;

	/**
	 * the queue of unprocessed {@code Conclusion}s of this {@link Context}
	 */
	private final ActivationStack<Conclusion> toDo_;

	/**
	 * {@code true} if this {@link Context} is initialized, i.e., contains
	 * {@link ContextInitialization}
	 */
	private volatile boolean isInitialized_ = false;

	/**
	 * Construct a new {@link Context} for the given root
	 * {@link IndexedClassExpression}. Initially, the context is not active.
	 * 
	 * @param root
	 */
	public ContextImpl(IndexedClassExpression root) {
		this.root_ = root;
		this.toDo_ = new ActivationStack<Conclusion>();
		this.subsumers_ = new ArrayHashSet<IndexedClassExpression>(13);
	}

	@Override
	public boolean addConclusion(Conclusion conclusion) {
		return conclusion.accept(CONCLUSION_INSERTER_, this);
	}

	@Override
	public boolean removeConclusion(Conclusion conclusion) {
		return conclusion.accept(CONCLUSION_DELETER_, this);
	}

	@Override
	public boolean containsConclusion(Conclusion conclusion) {
		return conclusion.accept(CONCLUSION_OCCURRENCE_CHECKER_, this);
	}

	@Override
	public boolean addToDo(Conclusion conclusion) {
		return toDo_.push(conclusion);
	}

	@Override
	public Map<IndexedPropertyChain, ? extends SubContextPremises> getSubContextPremisesByObjectProperty() {
		if (subContextsByObjectProperty_ == null)
			return Collections.emptyMap();
		// else
		return subContextsByObjectProperty_;
	}

	SubContext getCreateSubContext(IndexedPropertyChain subRoot) {
		if (subContextsByObjectProperty_ == null)
			subContextsByObjectProperty_ = new ArrayHashMap<IndexedPropertyChain, SubContext>(
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
	public Set<IndexedPropertyChain> getLocalReflexiveObjectProperties() {
		return reflexiveBackwardLinks_ == null ? Collections
				.<IndexedPropertyChain> emptySet() : reflexiveBackwardLinks_;
	}

	@Override
	public IndexedClassExpression getRoot() {
		return root_;
	}

	@Override
	public Set<IndexedClassExpression> getSubsumers() {
		return subsumers_;
	}

	@Override
	public boolean isInconsistForDisjointnessAxiom(
			IndexedDisjointnessAxiom axiom) {
		if (disjointnessAxioms_ == null)
			return false;
		IndexedClassExpression[] members = disjointnessAxioms_.get(axiom);
		if (members == null)
			return false;
		// check if both members are not null; this is always when the second
		// member is not null
		return (members[1] != null);
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
		return root_.toString()
				+ (this != ((IndexedObjectWithContext) root_).getContext() ? "[local]"
						: "");
	}

	@Override
	public boolean setSaturated(boolean saturated) {
		boolean result = isSaturated_;
		isSaturated_ = saturated;
		return result;
	}

	private static class ConclusionInserter implements
			ConclusionVisitor<ContextImpl, Boolean> {

		static Boolean visit(Subsumer conclusion, ContextImpl input) {
			return input.subsumers_.add(conclusion.getExpression());
		}

		@Override
		public Boolean visit(BackwardLink subConclusion, ContextImpl input) {
			IndexedPropertyChain relation = subConclusion.getRelation();
			// make sure that relevant context always exists
			SubContext subContext = input.getCreateSubContext(relation);
			if (subConclusion.getSourceRoot(input.root_) == input.root_) {
				// reflexive
				if (input.reflexiveBackwardLinks_ == null) {
					input.reflexiveBackwardLinks_ = new ArrayHashSet<IndexedPropertyChain>(
							3);
				}
				return input.reflexiveBackwardLinks_.add(relation);
			}
			// else non-reflexive
			return subContext.addSubConclusion(subConclusion);
		}

		@Override
		public Boolean visit(ComposedSubsumer conclusion, ContextImpl input) {
			return visit((Subsumer) conclusion, input);
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
		public Boolean visit(DecomposedSubsumer conclusion, ContextImpl input) {
			return visit((Subsumer) conclusion, input);
		}

		@Override
		public Boolean visit(DisjointSubsumer conclusion, ContextImpl input) {
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
			return input.getCreateSubContext(subConclusion.getSubRoot())
					.addSubConclusion(subConclusion);
		}

	}

	private static class ConclusionDeleter implements
			ConclusionVisitor<ContextImpl, Boolean> {

		@Override
		public Boolean visit(BackwardLink subConclusion, ContextImpl input) {
			boolean changed = false;
			IndexedPropertyChain relation = subConclusion.getRelation();
			SubContext subContext = input.getCreateSubContext(relation);
			if (subConclusion.getSourceRoot(input.root_) == input.root_) {
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

		static boolean visit(Subsumer conclusion, ContextImpl input) {
			return input.subsumers_.remove(conclusion.getExpression());
		}

		@Override
		public Boolean visit(ComposedSubsumer conclusion, ContextImpl input) {
			return visit((Subsumer) conclusion, input);
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
		public Boolean visit(DecomposedSubsumer conclusion, ContextImpl input) {
			return visit((Subsumer) conclusion, input);
		}

		@Override
		public Boolean visit(DisjointSubsumer conclusion, ContextImpl input) {
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
					.getSubRoot());
			if (subContext == null)
				return false;
			// else
			return subContext.removeSubConclusion(subConclusion);
		}

	}

	private static class ConclusionOccurrenceChecker implements
			ConclusionVisitor<ContextImpl, Boolean> {

		static boolean visit(Subsumer conclusion, ContextImpl input) {
			return input.subsumers_.contains(conclusion.getExpression());
		}

		@Override
		public Boolean visit(BackwardLink subConclusion, ContextImpl input) {
			if (subConclusion.getSourceRoot(input.root_) == input.root_) {
				// reflexive
				return input.reflexiveBackwardLinks_ != null
						&& input.reflexiveBackwardLinks_.contains(subConclusion
								.getRelation());
			}
			// else non-reflexive
			SubContext subContext = input.getCreateSubContext(subConclusion
					.getRelation());
			return subContext != null
					&& subContext.containsSubConclusion(subConclusion);
		}

		@Override
		public Boolean visit(ComposedSubsumer conclusion, ContextImpl input) {
			return visit((Subsumer) conclusion, input);
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
		public Boolean visit(DecomposedSubsumer conclusion, ContextImpl input) {
			return visit((Subsumer) conclusion, input);
		}

		@Override
		public Boolean visit(DisjointSubsumer conclusion, ContextImpl input) {
			if (input.disjointnessAxioms_ == null) {
				return false;
			}
			IndexedDisjointnessAxiom axiom = conclusion.getAxiom();
			IndexedClassExpression member = conclusion.getMember();
			IndexedClassExpression[] members = input.disjointnessAxioms_
					.get(axiom);
			if (members == null)
				return false;
			return (members[0] == member || members[1] == member);
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
					.getSubRoot());
			if (subContext == null)
				return false;
			// else
			return subContext.containsSubConclusion(subConclusion);
		}

	}
}
