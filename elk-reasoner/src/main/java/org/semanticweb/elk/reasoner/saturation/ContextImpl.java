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

import java.util.Map;
import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointnessAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.Subsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ModifiableLinkRule;
import org.semanticweb.elk.util.collections.ArrayHashMap;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.collections.HashSetMultimap;
import org.semanticweb.elk.util.collections.Multimap;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.collections.chains.AbstractChain;
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
public class ContextImpl implements Context {

	/**
	 * the root {@link IndexedClassExpression} for which the {@link #subsumers_}
	 * are computed
	 * 
	 */
	private final IndexedClassExpression root_;

	/**
	 * references to the next and previous contexts so that the contexts can be
	 * chained
	 */
	volatile ContextImpl next, previous;

	/**
	 * the derived {@link IndexedClassExpression}s that are subsumers (i.e,
	 * super-classes) of {@link #root_}
	 */
	private final Set<IndexedClassExpression> subsumers_;

	/**
	 * the indexed representation of all derived {@link BackwardLink}s computed
	 * for this {@link Context}; can be {@code null}
	 */
	private Multimap<IndexedPropertyChain, Context> backwardLinksByObjectProperty_ = null;

	/**
	 * 
	 */
	private Map<IndexedDisjointnessAxiom, Boolean> disjointnessAxioms_;

	/**
	 * the rules that should be applied to each derived {@link BackwardLink} in
	 * this {@link Context}; can be {@code null}
	 */
	private ModifiableLinkRule<BackwardLink> backwardLinkRules_ = null;

	/**
	 * the queue of unprocessed {@code Conclusion}s of this {@link Context}
	 */
	private final ActivationStack<Conclusion> toDo_;

	/**
	 * {@code true} if all derived {@link Subsumer} of {@link #root_} have been
	 * computed.
	 */
	protected volatile boolean isSaturated = false;

	/**
	 * {@code true} if {@code owl:Nothing} is stored in {@link #subsumers_}
	 */
	protected volatile boolean isInconsistent = false;

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
	public IndexedClassExpression getRoot() {
		return root_;
	}
	
	@Override
	public void removeLinks() {
		if (previous != null && next != null) {
			previous.next = next;
			next.previous = previous;
		}
	}

	@Override
	public Set<IndexedClassExpression> getSubsumers() {
		return subsumers_;
	}

	@Override
	public boolean addSubsumer(IndexedClassExpression expression) {
		return subsumers_.add(expression);

		/*
		 * if (changed && isSaturated) LOGGER_.error(getRoot() +
		 * ": adding a superclass to a saturated context: " + expression);
		 * 
		 * return changed;
		 */
	}

	@Override
	public boolean containsSubsumer(IndexedClassExpression expression) {
		return subsumers_.contains(expression);
	}

	@Override
	public boolean removeSubsumer(IndexedClassExpression expression) {
		return subsumers_.remove(expression);
	}

	@Override
	public boolean isInconsistent() {
		return isInconsistent;
	}

	@Override
	public boolean setInconsistent(boolean inconsistent) {
		boolean result = isInconsistent;
		isInconsistent = inconsistent;
		return result;
	}

	@Override
	public Multimap<IndexedPropertyChain, Context> getBackwardLinksByObjectProperty() {
		if (backwardLinksByObjectProperty_ == null)
			return Operations.emptyMultimap();
		return backwardLinksByObjectProperty_;
	}

	@Override
	public boolean containsBackwardLink(BackwardLink link) {
		if (backwardLinksByObjectProperty_ != null) {
			return backwardLinksByObjectProperty_.contains(link.getRelation(),
					link.getSource());
		}

		return false;
	}

	@Override
	public boolean addDisjointnessAxiom(
			IndexedDisjointnessAxiom disjointnessAxiom) {
		if (disjointnessAxioms_ == null) {
			disjointnessAxioms_ = new ArrayHashMap<IndexedDisjointnessAxiom, Boolean>();
		}
		Boolean inconsistency = disjointnessAxioms_.get(disjointnessAxiom);
		if (inconsistency == null)
			inconsistency = false;
		else if (inconsistency == true)
			// nothing changes
			return false;
		else
			// inconsistency == false;
			inconsistency = true;
		disjointnessAxioms_.put(disjointnessAxiom, inconsistency);
		return true;
	}

	@Override
	public boolean removeDisjointnessAxiom(IndexedDisjointnessAxiom axiom) {
		if (disjointnessAxioms_ == null) {
			return false;
		}
		Boolean inconcistency = disjointnessAxioms_.get(axiom);
		if (inconcistency == null)
			return false;
		if (inconcistency)
			disjointnessAxioms_.put(axiom, false);
		else {
			// inconcistency = false
			disjointnessAxioms_.remove(axiom);
			if (disjointnessAxioms_.isEmpty())
				disjointnessAxioms_ = null;
		}
		return true;
	}

	@Override
	public boolean containsDisjointnessAxiom(IndexedDisjointnessAxiom axiom) {
		if (disjointnessAxioms_ == null)
			return false;
		return disjointnessAxioms_.containsKey(axiom);
	}

	@Override
	public boolean inconsistencyDisjointnessAxiom(IndexedDisjointnessAxiom axiom) {
		Boolean inconsistency = disjointnessAxioms_.get(axiom);
		if (inconsistency == null)
			return false;
		return inconsistency;
	}

	@Override
	public boolean setSaturated(boolean saturated) {
		boolean result = isSaturated;
		isSaturated = saturated;
		return result;
	}

	@Override
	public boolean isSaturated() {
		return isSaturated;
	}

	@Override
	public boolean addBackwardLink(BackwardLink link) {
		Context source = link.getSource();
		IndexedPropertyChain relation = link.getRelation();

		if (backwardLinksByObjectProperty_ == null)
			backwardLinksByObjectProperty_ = new HashSetMultimap<IndexedPropertyChain, Context>();

		return backwardLinksByObjectProperty_.add(relation, source);

		/*
		 * if (changed && source.isSaturated()) LOGGER_.error(getRoot() +
		 * ": adding a backward link to a saturated context: " + link);
		 * 
		 * return changed;
		 */
	}

	@Override
	public boolean removeBackwardLink(BackwardLink link) {
		boolean changed = false;

		if (backwardLinksByObjectProperty_ != null) {
			changed = backwardLinksByObjectProperty_.remove(link.getRelation(),
					link.getSource());

			if (backwardLinksByObjectProperty_.isEmpty()) {
				backwardLinksByObjectProperty_ = null;
			}
		}

		return changed;
	}

	@Override
	public AbstractChain<ModifiableLinkRule<BackwardLink>> getBackwardLinkRuleChain() {
		return new AbstractChain<ModifiableLinkRule<BackwardLink>>() {

			@Override
			public ModifiableLinkRule<BackwardLink> next() {
				return backwardLinkRules_;
			}

			@Override
			public void setNext(ModifiableLinkRule<BackwardLink> tail) {
				backwardLinkRules_ = tail;
			}
		};
	}

	@Override
	public ModifiableLinkRule<BackwardLink> getBackwardLinkRuleHead() {
		return backwardLinkRules_;
	}

	@Override
	public boolean addToDo(Conclusion conclusion) {
		return toDo_.push(conclusion);
	}

	@Override
	public Conclusion takeToDo() {
		return toDo_.pop();
	}

	@Override
	public String toString() {
		return root_.toString();
	}

	@Override
	public boolean isEmpty() {
		return (subsumers_ == null || subsumers_.isEmpty())
				&& (backwardLinksByObjectProperty_ == null || backwardLinksByObjectProperty_
						.isEmpty()) && getBackwardLinkRuleHead() == null;
	}

}
