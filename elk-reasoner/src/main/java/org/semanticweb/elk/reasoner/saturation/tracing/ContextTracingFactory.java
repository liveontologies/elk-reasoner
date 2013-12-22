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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointnessAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.BasicSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.ContextCreationListener;
import org.semanticweb.elk.reasoner.saturation.ContextModificationListener;
import org.semanticweb.elk.reasoner.saturation.ExtendedSaturationState;
import org.semanticweb.elk.reasoner.saturation.ExtendedSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.CombinedConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionInsertionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.DisjointnessAxiom;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.DecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.BasicDecompositionRuleApplicationVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.CompositionRuleApplicationVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.ContextCompletionFactory;
import org.semanticweb.elk.reasoner.saturation.rules.DecompositionRuleApplicationVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.LinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.ModifiableLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationFactory;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceStore.Writer;
import org.semanticweb.elk.util.collections.Condition;
import org.semanticweb.elk.util.collections.Multimap;
import org.semanticweb.elk.util.collections.MultimapOperations;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.collections.chains.Chain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ContextTracingFactory extends RuleApplicationFactory {

	// logger for this class
	protected static final Logger LOGGER_ = LoggerFactory
			.getLogger(ContextCompletionFactory.class);
	/**
	 * Encapsulates the tracing saturation state (with local contexts) and a
	 * trace store which stores traced conclusions.
	 */
	private final TraceState traceState_;

	/**
	 * Holds iff the context is traced (passed as input into this factory).
	 */
	private final Condition<Context> tracingCondition_ = new Condition<Context>() {

		@Override
		public boolean holds(Context cxt) {
			return traceState_.getRootsSubmittedForTracing().contains(
					cxt.getRoot());
		}
	};

	public ContextTracingFactory(ExtendedSaturationState mainSaturationState,
			TraceState traceState) {
		super(mainSaturationState);
		traceState_ = traceState;
	}

	@Override
	public BaseEngine getDefaultEngine(ContextCreationListener listener,
			ContextModificationListener modListener) {
		return new TracingEngine();
	}

	@Override
	public TracingSaturationState getSaturationState() {
		return traceState_.getSaturationState();
	}

	/**
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	private class TracingEngine extends RuleApplicationFactory.BaseEngine {

		// processes conclusions taken from the ToDo queue
		private final ConclusionVisitor<Boolean, Context> conclusionProcessor_;

		protected TracingEngine() {
			super(new SaturationStatistics());

			ExtendedSaturationStateWriter tracingWriter = getSaturationStateWriter();
			// inserts to the local context and writes inferences
			// the inference writer should go first so we capture alternative
			// derivations.
			ConclusionVisitor<Boolean, Context> inserter = new CombinedConclusionVisitor<Context>(
					new InferenceInserter(traceState_.getTraceStore()
							.getWriter()), new ConclusionInsertionVisitor());
			// applies rules on the main contexts
			ConclusionVisitor<Boolean, Context> applicator = new ApplicationVisitor(
					tracingWriter,
					SaturationState.DEFAULT_INIT_RULE_APP_VISITOR);
			// combines the inserter and the applicator
			conclusionProcessor_ = new CombinedConclusionVisitor<Context>(
					inserter, applicator);
		}

		@Override
		public void submit(IndexedClassExpression root) {
			getSaturationStateWriter().getCreateContext(root);
		}

		@Override
		protected ConclusionVisitor<Boolean, Context> getBaseConclusionProcessor() {
			return conclusionProcessor_;
		}

		@Override
		protected ExtendedSaturationStateWriter getSaturationStateWriter() {
			return getSaturationState().getTracingWriter(
					ConclusionVisitor.DUMMY,
					SaturationState.DEFAULT_INIT_RULE_APP_VISITOR,
					tracingCondition_);
		}

	}

	/**
	 * Applies unoptimized rules on main contexts.
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	private class ApplicationVisitor implements
			ConclusionVisitor<Boolean, Context> {

		private final BasicSaturationStateWriter iterationWriter_;
		private final CompositionRuleApplicationVisitor ruleAppVisitor_;
		private final DecompositionRuleApplicationVisitor mainDecompRuleAppVisitor_;

		public ApplicationVisitor(BasicSaturationStateWriter iterationWriter,
				CompositionRuleApplicationVisitor ruleAppVisitor) {
			this.iterationWriter_ = iterationWriter;
			this.ruleAppVisitor_ = ruleAppVisitor;
			this.mainDecompRuleAppVisitor_ = new LocalDecompositionVisitor(
					saturationState);

		}

		Context getHybridContext(IndexedClassExpression root) {
			// return root.getContext();
			return new HybridContext(getSaturationState().getContext(root),
					root.getContext());
		}

		@Override
		public Boolean visit(ComposedSubsumer negSCE, Context context) {
			Context cxt = getHybridContext(context.getRoot());

			negSCE.apply(iterationWriter_, cxt, ruleAppVisitor_);
			negSCE.applyDecompositionRules(cxt, mainDecompRuleAppVisitor_);

			return true;
		}

		@Override
		public Boolean visit(DecomposedSubsumer posSCE, Context context) {
			posSCE.apply(iterationWriter_, getHybridContext(context.getRoot()),
					ruleAppVisitor_, mainDecompRuleAppVisitor_);
			return true;
		}

		@Override
		public Boolean visit(BackwardLink link, Context context) {
			link.apply(iterationWriter_, getHybridContext(context.getRoot()),
					ruleAppVisitor_);

			return true;
		}

		@Override
		public Boolean visit(ForwardLink link, Context context) {
			link.apply(iterationWriter_, getHybridContext(context.getRoot()));

			return true;
		}

		@Override
		public Boolean visit(Contradiction bot, Context context) {
			bot.deapply(iterationWriter_, getHybridContext(context.getRoot()));
			return true;
		}

		@Override
		public Boolean visit(Propagation propagation, Context context) {
			propagation.apply(iterationWriter_,
					getHybridContext(context.getRoot()));
			return true;
		}

		@Override
		public Boolean visit(DisjointnessAxiom disjointnessAxiom,
				Context context) {
			disjointnessAxiom.apply(iterationWriter_,
					getHybridContext(context.getRoot()));

			return true;
		}

		/**
		 * A decomposition visitor which look ups contexts in the main
		 * saturation state and doesn't create local contexts.
		 * 
		 * @author Pavel Klinov
		 * 
		 *         pavel.klinov@uni-ulm.de
		 */
		private class LocalDecompositionVisitor extends
				BasicDecompositionRuleApplicationVisitor {

			private final SaturationState mainSaturationState_;

			LocalDecompositionVisitor(SaturationState mainState) {
				mainSaturationState_ = mainState;
			}

			@Override
			public void visit(IndexedObjectSomeValuesFrom ice, Context context) {
				// never creates a new main context for the filler
				Context fillerContext = mainSaturationState_.getContext(ice
						.getFiller());

				if (fillerContext != null) {
					// the passed context is hybrid but we really need to point
					// the backward link to the main context.
					Context mainContext = context.getRoot().getContext();

					iterationWriter_.produce(fillerContext,
							iterationWriter_.getConclusionFactory()
									.createBackwardLink(ice, mainContext));
				}
			}

			@Override
			protected BasicSaturationStateWriter getSaturationStateWriter() {
				return iterationWriter_;
			}

		}

	}

	// TODO: why main contexts are modified at all??
	/**
	 * Makes sure that inferences are stored by main contexts.
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	private static class InferenceInserter extends
			TracingConclusionInsertionVisitor {

		public InferenceInserter(Writer traceWriter) {
			super(traceWriter);
		}

		@Override
		protected Boolean defaultVisit(Conclusion conclusion, Context cxt) {
			return super.defaultVisit(conclusion, cxt.getRoot().getContext());
		}

	}

	/**
	 * Hybrid context is a read-only view over the local and the main context
	 * for the same root expression. When the outer code requests a conclusion
	 * (subsumer, backward link, or a backward link rule), it checks if it
	 * logically belongs to a traced context. If yes, it is retrieved from the
	 * local copy. If not, from the main context.
	 * 
	 * The rationale is to avoid duplicate inferences when iterating over
	 * conclusions. The duplicates can occur as a result of applying a binary
	 * rule twice (once for each premise). Hybrid contexts solve this problem
	 * since when the rule is applied for the first time, the second premise is
	 * not yet in the local copy (otherwise the rule was already applied for
	 * it). As such, the rule produces each conclusion only when applied for the
	 * second premise (when the first premise is already in the local copy).
	 * 
	 * The tricky part of implementing hybrid contexts is filtering backward
	 * links. A context can store any number of backward links which belong to
	 * other contexts (some of which are traced, others aren't). Thus some
	 * backward links must be retrieved from the local copy and others from the
	 * main context. This class uses a generic multimap filtering and merging to
	 * implement this on the fly.
	 * 
	 */
	private class HybridContext implements Context {

		private final Context localContext_;

		private final Context mainContext_;

		private final Context selectedContext_;

		HybridContext(Context local, Context main) {
			localContext_ = local;
			mainContext_ = main;

			if (tracingCondition_.holds(localContext_)) {
				selectedContext_ = localContext_;
			} else {
				selectedContext_ = mainContext_;
			}
		}

		@Override
		public IndexedClassExpression getRoot() {
			return selectedContext_.getRoot();
		}

		@Override
		public Set<IndexedClassExpression> getSubsumers() {
			return selectedContext_.getSubsumers();
		}

		@Override
		public Multimap<IndexedPropertyChain, Context> getBackwardLinksByObjectProperty() {
			/*
			 * the rules with backward links *currently* produce only
			 * conclusions that belong to the same contexts as the backward
			 * links. Therefore, if such rules should be applied during tracing,
			 * then the contexts to which the backward links belong are traced
			 * and hence, they should be taken from localContext However, this
			 * solution is fragile as there can potentially be rules with
			 * backward links that produce conclusions in other contexts
			 */

			return localContext_.getBackwardLinksByObjectProperty();
		}

		@Override
		public LinkRule<BackwardLink, Context> getBackwardLinkRuleHead() {
			return selectedContext_.getBackwardLinkRuleHead();
		}

		@Override
		public Chain<ModifiableLinkRule<BackwardLink, Context>> getBackwardLinkRuleChain() {
			return selectedContext_.getBackwardLinkRuleChain();
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
			return mainContext_.isInconsistent();
		}

		@Override
		public boolean isSaturated() {
			return false;
		}

		@Override
		public boolean setInconsistent(boolean consistent) {
			// no-op
			return false;
		}

		@Override
		public boolean setSaturated(boolean saturated) {
			// no-op
			return false;
		}

		@Override
		public boolean isEmpty() {
			return localContext_.isEmpty() && mainContext_.isEmpty();
		}

		@Override
		public void removeLinks() {
			// no-op
		}

		@Override
		public String toString() {
			return getRoot() + "[hybrid]";
		}

	}
}
