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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.BasicSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.ContextCreationListener;
import org.semanticweb.elk.reasoner.saturation.ContextModificationListener;
import org.semanticweb.elk.reasoner.saturation.ExtendedSaturationState;
import org.semanticweb.elk.reasoner.saturation.ExtendedSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.CombinedConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionInsertionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.DecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.DisjointnessAxiom;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.BasicDecompositionRuleApplicationVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.CompositionRuleApplicationVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.ContextCompletionFactory;
import org.semanticweb.elk.reasoner.saturation.rules.DecompositionRuleApplicationVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationFactory;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceStore.Writer;
import org.semanticweb.elk.reasoner.saturation.tracing.TracingSaturationState.TracedContext;
import org.semanticweb.elk.reasoner.saturation.tracing.TracingSaturationState.TracingWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Non-recursive context tracing factory. Traces only conclusion which logically
 * belong to the context submitted for tracing.
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
	class TracingEngine extends RuleApplicationFactory.BaseEngine {

		// processes conclusions taken from the ToDo queue
		private final ConclusionVisitor<Boolean, Context> conclusionProcessor_;

		protected TracingEngine() {
			super(new SaturationStatistics());

			TraceStore.Writer traceWriter = traceState_.getTraceStore().getWriter();
			ExtendedSaturationStateWriter localContextWriter = getSaturationStateWriter();
			// inserts to the local context and writes inferences.
			// the inference writer should go first so we capture alternative
			// derivations.
			ConclusionVisitor<Boolean, Context> inserter = new CombinedConclusionVisitor<Context>(
					new TracingInserter(traceWriter), new ConclusionInsertionVisitor());
			// applies rules
			ConclusionVisitor<Boolean, Context> applicator = new ApplicationVisitor(
					localContextWriter,
					SaturationState.DEFAULT_INIT_RULE_APP_VISITOR);
			// combines the inserter and the applicator
			conclusionProcessor_ = new CombinedConclusionVisitor<Context>(
					inserter, getUsedConclusionsCountingVisitor(applicator));
		}

		@Override
		public void submit(IndexedClassExpression root) {
			TracedContext cxt = getSaturationStateWriter().getCreateContext(root);
			//trace if it's not been traced
			if (!cxt.isSaturated()) {
				localStatistics.getContextStatistics().countModifiedContexts++;
				
				getSaturationStateWriter().initContext(cxt);
			}
		}

		@Override
		protected void process(Context context) {
			super.process(context);
		}

		@Override
		protected ConclusionVisitor<Boolean, Context> getBaseConclusionProcessor() {
			return conclusionProcessor_;
		}

		@Override
		protected TracingWriter getSaturationStateWriter() {
			return getSaturationState().getTracingWriter(
					ConclusionVisitor.DUMMY,
					SaturationState.DEFAULT_INIT_RULE_APP_VISITOR);
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

		private final BasicSaturationStateWriter localWriter_;
		private final CompositionRuleApplicationVisitor ruleAppVisitor_;
		private final DecompositionRuleApplicationVisitor mainDecompRuleAppVisitor_;

		public ApplicationVisitor(BasicSaturationStateWriter iterationWriter,
				CompositionRuleApplicationVisitor ruleAppVisitor) {
			this.localWriter_ = iterationWriter;
			this.ruleAppVisitor_ = ruleAppVisitor;
			this.mainDecompRuleAppVisitor_ = new LocalDecompositionVisitor(saturationState);

		}

		Context getContext(Conclusion conclusion, Context context) {
			IndexedClassExpression root = context.getRoot();
			
			if (context == conclusion.getSourceContext(context)) {
				// this will be the hybrid context which will return all local
				// conclusions except of the backward links which belong to
				// other contexts. Those will be retrieved from the main
				// context.
				return getSaturationState().getContext(root);
			} else {
				return root.getContext();
			}
		}

		@Override
		public Boolean visit(ComposedSubsumer negSCE, Context context) {
			Context cxt = getContext(negSCE, context);

			negSCE.apply(localWriter_, cxt, ruleAppVisitor_);
			negSCE.applyDecompositionRules(cxt, mainDecompRuleAppVisitor_);

			return true;
		}

		@Override
		public Boolean visit(DecomposedSubsumer posSCE, Context context) {			
			posSCE.apply(localWriter_, getContext(posSCE, context),
					ruleAppVisitor_, mainDecompRuleAppVisitor_);
			return true;
		}

		@Override
		public Boolean visit(BackwardLink link, Context inferenceContext) {
			link.applyLocally(localWriter_, getContext(link, inferenceContext), ruleAppVisitor_);

			return true;
		}

		@Override
		public Boolean visit(ForwardLink link, Context inferenceContext) {
			link.applyLocally(localWriter_, getContext(link, inferenceContext));

			return true;
		}

		@Override
		public Boolean visit(Contradiction bot, Context context) {
			bot.deapply(localWriter_, getContext(bot, context));
			return true;
		}

		@Override
		public Boolean visit(Propagation propagation, final Context inferenceContext) {
			propagation.applyLocally(localWriter_,
					getContext(propagation, inferenceContext));
			return true;
		}

		@Override
		public Boolean visit(DisjointnessAxiom disjointnessAxiom,
				Context context) {
			disjointnessAxiom.apply(localWriter_,
					getContext(disjointnessAxiom, context));

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
				//this call won't ever create a context in the main saturation state
				Context fillerContext = mainSaturationState_.getContext(ice.getFiller());

				if (fillerContext != null) {
					// the passed context is hybrid but we really need to point
					// the backward link to the main context.
					Context mainContext = context.getRoot().getContext();

					localWriter_.produce(fillerContext,
							localWriter_.getConclusionFactory()
									.createBackwardLink(ice, mainContext));
				}
			}

			@Override
			protected BasicSaturationStateWriter getSaturationStateWriter() {
				return localWriter_;
			}

		}

	}

	/**
	 * Inserts traces into the trace store but passes the main context so that
	 * the traces can be retrieved by main contexts.
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	private static class TracingInserter extends TracingConclusionInsertionVisitor {

		public TracingInserter(Writer traceWriter) {
			super(traceWriter);
		}

		@Override
		protected Boolean defaultVisit(Conclusion conclusion, Context cxt) {
			return super.defaultVisit(conclusion, cxt.getRoot().getContext());
		}

	}
	
}
