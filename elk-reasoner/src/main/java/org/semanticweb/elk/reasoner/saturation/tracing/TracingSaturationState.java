/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

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

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointnessAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.BasicSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.LocalSaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.CompositionRuleApplicationVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.LinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.ModifiableLinkRule;
import org.semanticweb.elk.util.collections.Condition;
import org.semanticweb.elk.util.collections.Multimap;
import org.semanticweb.elk.util.collections.MultimapOperations;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.collections.chains.Chain;
import org.semanticweb.elk.util.concurrent.collections.ActivationStack;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class TracingSaturationState extends LocalSaturationState {

	public TracingSaturationState(OntologyIndex index) {
		super(index);
	}

	@Override
	public BasicSaturationStateWriter getWriter(
			ConclusionVisitor<?, Context> conclusionVisitor) {
		return getExtendedWriter(conclusionVisitor,
				SaturationState.DEFAULT_INIT_RULE_APP_VISITOR);
	}

	public TracingWriter getTracingWriter(
			ConclusionVisitor<?, Context> conclusionVisitor,
			CompositionRuleApplicationVisitor initRuleAppVisitor) {
		return new TracingWriter(conclusionVisitor, initRuleAppVisitor);
	}
	
	TracingWriter getTracingWriter() {
		return getTracingWriter(ConclusionVisitor.DUMMY, SaturationState.DEFAULT_INIT_RULE_APP_VISITOR);
	}
	
	@Override
	public TracedContext getContext(IndexedClassExpression ice) {
		return (TracedContext) super.getContext(ice);
	}
	
	/**
	 * 
	 * @param context
	 * @return true if the context has been traced
	 */
	public boolean isTraced(IndexedClassExpression root) {
		TracedContext localContext = getContext(root);

		return localContext != null && localContext.isInitialized();
	}

	public Iterable<Context> getTracedContexts() {
		return Operations.filter(getContexts(), new Condition<Context>() {

			@Override
			public boolean holds(Context cxt) {
				return cxt.isSaturated();
			}
		});
	}

	/**
	 * The same as {@link LocalSaturationState.LocalWriter} but uses
	 * {@link TracingConclusionFactory}.
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	class TracingWriter extends LocalSaturationState.LocalWriter {

		public TracingWriter(ConclusionVisitor<?, Context> visitor,
				CompositionRuleApplicationVisitor ruleAppVisitor) {
			super(visitor, ruleAppVisitor, new TracingConclusionFactory());
		}

		@Override
		protected TracedContext newContext(IndexedClassExpression root) {
			return new TracedContext(super.newContext(root), root.getContext());
		}

		@Override
		public TracedContext getCreateContext(IndexedClassExpression root) {
			return (TracedContext) super.getCreateContext(root);
		}
		
	}
	
	/**
	 * A wrapper around local contexts which returns non-reflexive backward links from the main context. 
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	static class TracedContext implements Context {

		private final Context localContext_;

		private final Context mainContext_;
		/**
		 * set to {@code true} immediately before submitting for tracing (via
		 * saturation) and set to {@code false} once that is done. This flag
		 * ensures that only one worker traces the context and all other
		 * processing (including "reading" e.g. unwinding the traces) must wait
		 * until that is done.
		 */
		private final AtomicBoolean beingTraced_;
		/**
		 * TODO
		 */
		private final ActivationStack<Conclusion> traceExploreQueue_;
		
		TracedContext(Context local, Context main) {
			localContext_ = local;
			mainContext_ = main;
			beingTraced_ = new AtomicBoolean(false);
			traceExploreQueue_ = new ActivationStack<Conclusion>();
		}
		
		boolean addConclusionToTrace(Conclusion conclusion) {
			return traceExploreQueue_.push(conclusion);
		}
		
		Conclusion pollForConclusionToTrace() {
			return traceExploreQueue_.pop();
		}
		
		boolean beingTracedCompareAndSet(boolean expect, boolean update) {
			return beingTraced_.compareAndSet(expect, update);
		}
		
		boolean isInitialized() {
			// TODO it's safer to maintain a flag for this (the current check
			// relies on the fact that our initialization rules produce at least
			// one subsumer)
			return !localContext_.getSubsumers().isEmpty();
		}
		
		@Override
		public IndexedClassExpression getRoot() {
			return localContext_.getRoot();
		}

		@Override
		public Set<IndexedClassExpression> getSubsumers() {
			return localContext_.getSubsumers();
		}

		@Override
		public Multimap<IndexedPropertyChain, Context> getBackwardLinksByObjectProperty() {
			//TODO think if this can be simplified. 
			Multimap<IndexedPropertyChain, Context> allLocal = localContext_.getBackwardLinksByObjectProperty();
			Multimap<IndexedPropertyChain, Context> allMain = mainContext_.getBackwardLinksByObjectProperty();
			Condition<Context> isReflexive = new Condition<Context>() {

				@Override
				public boolean holds(Context element) {
					return element.getRoot() == getRoot();
				}
				
			};
			Condition<Context> isNotReflexive = new Condition<Context>() {

				@Override
				public boolean holds(Context element) {
					return element.getRoot() != getRoot();
				}
				
			};
			//reflexive links taken from the local context
			Multimap<IndexedPropertyChain, Context> local = MultimapOperations.valueFilter(allLocal, isReflexive);
			//non-reflexive links taken from the global context
			Multimap<IndexedPropertyChain, Context> main = MultimapOperations.valueFilter(allMain, isNotReflexive);

			return MultimapOperations.union(local, main);
		}

		@Override
		public LinkRule<BackwardLink, Context> getBackwardLinkRuleHead() {
			return localContext_.getBackwardLinkRuleHead();
		}

		@Override
		public Chain<ModifiableLinkRule<BackwardLink, Context>> getBackwardLinkRuleChain() {
			return localContext_.getBackwardLinkRuleChain();
		}

		@Override
		public boolean addBackwardLink(BackwardLink link) {
			return localContext_.addBackwardLink(link);
		}

		@Override
		public boolean removeBackwardLink(BackwardLink link) {
			return localContext_.removeBackwardLink(link);
		}

		@Override
		public boolean containsBackwardLink(BackwardLink link) {
			return localContext_.containsBackwardLink(link);
		}

		@Override
		public boolean addSubsumer(IndexedClassExpression expression) {
			return localContext_.addSubsumer(expression);
		}

		@Override
		public boolean removeSubsumer(IndexedClassExpression expression) {
			return localContext_.removeSubsumer(expression);
		}

		@Override
		public boolean containsSubsumer(IndexedClassExpression expression) {
			return localContext_.containsSubsumer(expression);
		}

		@Override
		public boolean addDisjointnessAxiom(IndexedDisjointnessAxiom axiom) {
			return localContext_.addDisjointnessAxiom(axiom);
		}

		@Override
		public boolean removeDisjointnessAxiom(IndexedDisjointnessAxiom axiom) {
			return localContext_.removeDisjointnessAxiom(axiom);
		}

		@Override
		public boolean containsDisjointnessAxiom(IndexedDisjointnessAxiom axiom) {
			return localContext_.containsDisjointnessAxiom(axiom);
		}

		@Override
		public boolean inconsistencyDisjointnessAxiom(
				IndexedDisjointnessAxiom axiom) {
			return localContext_.inconsistencyDisjointnessAxiom(axiom);
		}

		@Override
		public boolean addToDo(Conclusion conclusion) {
			return localContext_.addToDo(conclusion);
		}

		@Override
		public Conclusion takeToDo() {
			return localContext_.takeToDo();
		}

		@Override
		public boolean isInconsistent() {
			return localContext_.isInconsistent();
		}

		@Override
		public boolean isSaturated() {
			return localContext_.isSaturated();
		}

		@Override
		public boolean setInconsistent(boolean consistent) {
			return localContext_.setInconsistent(consistent);
		}

		@Override
		public boolean setSaturated(boolean saturated) {
			return localContext_.setSaturated(saturated);
		}

		@Override
		public boolean isEmpty() {
			return localContext_.isEmpty();
		}

		@Override
		public void removeLinks() {
			localContext_.removeLinks();
		}

		@Override
		public String toString() {
			return getRoot() + "[hybrid]";
		}

	}

}
