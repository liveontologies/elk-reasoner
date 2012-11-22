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
package org.semanticweb.elk.reasoner.saturation.context;

import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointnessAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.SuperClassExpression;
import org.semanticweb.elk.reasoner.saturation.rules.ModifiableLinkRule;
import org.semanticweb.elk.util.collections.ArrayHashMap;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.collections.HashSetMultimap;
import org.semanticweb.elk.util.collections.Multimap;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.collections.chains.AbstractChain;

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

	// logger for this class
	//private static final Logger LOGGER_ = Logger.getLogger(ContextImpl.class);

	/**
	 * the root {@link IndexedClassExpression} for which the
	 * {@link #superClassExpressions_} are computed
	 * 
	 */
	private final IndexedClassExpression root_;

	/**
	 * the representation of all derived {@link SuperClassExpression}s computed
	 * for this {@link Context}; these should be super-classes of {@link #root_}
	 */
	private final Set<IndexedClassExpression> superClassExpressions_;

	/**
	 * the indexed representation of all derived {@link BackwardLink}s computed
	 * for this {@link Context}; can be {@code null}
	 */
	private Multimap<IndexedPropertyChain, Context> backwardLinksByObjectProperty_ = null;

	private Map<IndexedDisjointnessAxiom, Integer> disjointnessAxioms_;

	/**
	 * the rules that should be applied to each derived {@link BackwardLink} in
	 * this {@link Context}; can be {@code null}
	 */
	private ModifiableLinkRule<BackwardLink> backwardLinkRules_ = null;

	/**
	 * the queue of unprocessed {@code Conclusion}s of this {@link Context}
	 */
	private final Queue<Conclusion> toDo_;

	/**
	 * the flag used to trigger activation and de-activation of contexts. If
	 * this value is {@code false} then {@link #toDo_} queue should be empty
	 */
	private final AtomicBoolean isActive_;

	/**
	 * {@code true} if all derived {@link SuperClassExpression} of
	 * {@link #root_} have been computed.
	 */
	protected volatile boolean isSaturated = false;

	/**
	 * {@code true} if {@code owl:Nothing} is stored in
	 * {@link #superClassExpressions_}
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
		this.toDo_ = new ConcurrentLinkedQueue<Conclusion>();
		this.isActive_ = new AtomicBoolean(false);
		this.superClassExpressions_ = new ArrayHashSet<IndexedClassExpression>(
				13);
	}

	@Override
	public IndexedClassExpression getRoot() {
		return root_;
	}

	@Override
	public Set<IndexedClassExpression> getSuperClassExpressions() {
		return superClassExpressions_;
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
	public int addDisjointnessAxiom(IndexedDisjointnessAxiom disjointnessAxiom) {
		if (disjointnessAxioms_ == null) {
			disjointnessAxioms_ = new ArrayHashMap<IndexedDisjointnessAxiom, Integer>();
		}
		Integer counter = disjointnessAxioms_.get(disjointnessAxiom);
		if (counter == null)
			counter = 0;
		disjointnessAxioms_.put(disjointnessAxiom, counter + 1);
		return counter;
	}

	@Override
	public int removeDisjointnessAxiom(IndexedDisjointnessAxiom axiom) {
		if (disjointnessAxioms_ == null) {
			return 0;
		}
		Integer counter = disjointnessAxioms_.get(axiom);

		if (counter > 0)
			disjointnessAxioms_.put(axiom, counter - 1);
		return counter;
	}

	@Override
	public int containsDisjointnessAxiom(IndexedDisjointnessAxiom axiom) {
		Integer counter = disjointnessAxioms_.get(axiom);

		return counter == null ? 0 : counter;
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
		
		/*if (changed && source.isSaturated())
			LOGGER_.error(getRoot()
					+ ": adding a backward link to a saturated context: "
					+ link);
		
		return changed;*/
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
	public boolean addSuperClassExpression(IndexedClassExpression expression) {
		return superClassExpressions_.add(expression);
		
		/*if (changed && isSaturated) 
			LOGGER_.error(getRoot()
					+ ": adding a superclass to a saturated context: "
					+ expression);
		
		return changed;*/
	}

	@Override
	public boolean removeSuperClassExpression(IndexedClassExpression expression) {
		return superClassExpressions_.remove(expression);
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
		toDo_.add(conclusion);
		if (isActive_.get()) {
			return false;
		}
		return isActive_.compareAndSet(false, true);
	}

	@Override
	public Conclusion takeToDo() {
		return toDo_.poll();
	}

	@Override
	public boolean deactivate() {
		if (!isActive_.get()) {
			return false;
		}
		if (isActive_.compareAndSet(true, false) && !toDo_.isEmpty())
			return isActive_.compareAndSet(false, true);
		return false;
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
	public boolean containsSuperClassExpression(
			IndexedClassExpression expression) {
		return superClassExpressions_.contains(expression);
	}

	@Override
	public String toString() {
		return root_.toString();
	}
}
