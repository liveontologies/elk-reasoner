/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

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
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionInsertionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.DisjointnessAxiom;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.NegativeSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.PositiveSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.BasicDecompositionRuleApplicationVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.CompositionRuleApplicationVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.ContextCompletionFactory;
import org.semanticweb.elk.reasoner.saturation.rules.DecompositionRuleApplicationVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationFactory;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceStore.Writer;
import org.semanticweb.elk.util.collections.Operations.Condition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ContextTracingFactory extends RuleApplicationFactory {

	// logger for this class
	protected static final Logger LOGGER_ = LoggerFactory
			.getLogger(ContextCompletionFactory.class);
	/**
	 * encapsulates the tracing saturation state (with local contexts) and a
	 * trace store which stores inference.
	 */
	private final TraceState traceState_;
	
	private final Condition<Context> tracingCondition_ = new Condition<Context>() {

		@Override
		public boolean holds(Context cxt) {
			return traceState_.getRootsSubmittedForTracing().contains(cxt.getRoot());
		}
	};

	public ContextTracingFactory(ExtendedSaturationState mainSaturationState, TraceState traceState) {
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
			//inserts to the local context and writes inferences
			ConclusionVisitor<Boolean, Context> inserter = new CombinedConclusionVisitor<Context>(
					new ConclusionInsertionVisitor(),
					new InferenceInserter(traceState_.getTraceStore().getWriter()/*traceWriter_*/));
			//applies rules on the main contexts
			ConclusionVisitor<Boolean, Context> applicator = new ApplicationVisitor(tracingWriter, SaturationState.DEFAULT_INIT_RULE_APP_VISITOR);
			//combines the inserter and the applicator
			conclusionProcessor_ = new CombinedConclusionVisitor<Context>(inserter, applicator);
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
			return getSaturationState()/*localState_*/.getTracingWriter(ConclusionVisitor.DUMMY, SaturationState.DEFAULT_INIT_RULE_APP_VISITOR, tracingCondition_);
		}
		
	}

	
	/**
	 * Applies unoptimized rules on main contexts. Depending on whether the
	 * current context is being traced or not, it selects the proper context
	 * writer.
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	private class ApplicationVisitor implements ConclusionVisitor<Boolean, Context> {

		private final BasicSaturationStateWriter iterationWriter_;
		private final CompositionRuleApplicationVisitor ruleAppVisitor_;
		private final DecompositionRuleApplicationVisitor mainDecompRuleAppVisitor_;

		public ApplicationVisitor(
				BasicSaturationStateWriter iterationWriter,
				CompositionRuleApplicationVisitor ruleAppVisitor) {
			this.iterationWriter_ = iterationWriter;
			this.ruleAppVisitor_ = ruleAppVisitor;
			this.mainDecompRuleAppVisitor_ = new LocalDecompositionVisitor(saturationState);

		}

		@Override
		public Boolean visit(NegativeSubsumer negSCE, Context context) {
			
			negSCE.apply(iterationWriter_, context.getRoot().getContext(), ruleAppVisitor_);
			negSCE.applyDecompositionRules(context.getRoot().getContext(), mainDecompRuleAppVisitor_);
			
			return true;
		}

		@Override
		public Boolean visit(PositiveSubsumer posSCE, Context context) {
			
			posSCE.apply(iterationWriter_, context.getRoot().getContext(),
					ruleAppVisitor_, mainDecompRuleAppVisitor_);
			return true;
		}

		@Override
		public Boolean visit(BackwardLink link, Context context) {
			
			link.apply(iterationWriter_, context.getRoot().getContext(), ruleAppVisitor_);
			
			return true;
		}

		@Override
		public Boolean visit(ForwardLink link, Context context) {
			
			link.apply(iterationWriter_, context.getRoot().getContext());
			return true;
		}

		@Override
		public Boolean visit(Contradiction bot, Context context) {
			
			bot.deapply(iterationWriter_, context.getRoot().getContext());
			return true;
		}

		@Override
		public Boolean visit(Propagation propagation, Context context) {
			
			propagation.apply(iterationWriter_, context.getRoot().getContext());
			return true;
		}

		@Override
		public Boolean visit(DisjointnessAxiom disjointnessAxiom,
				Context context) {
			
			disjointnessAxiom.apply(iterationWriter_, context.getRoot().getContext());
			
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
		private class LocalDecompositionVisitor extends	BasicDecompositionRuleApplicationVisitor {

			private final SaturationState mainSaturationState_;

			LocalDecompositionVisitor(SaturationState mainState) {
				mainSaturationState_ = mainState;
			}

			@Override
			public void visit(IndexedObjectSomeValuesFrom ice, Context context) {
				// never creates a new main context for the filler
				Context fillerContext = mainSaturationState_.getContext(ice.getFiller());

				if (fillerContext != null) {
					iterationWriter_.produce(fillerContext, iterationWriter_.getConclusionFactory().createBackwardLink(ice, context));
				}
			}

			@Override
			protected BasicSaturationStateWriter getSaturationStateWriter() {
				return iterationWriter_;
			}

		}

	}
	
	/**
	 * Makes sure that inferences are stored by main contexts.
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	private static class InferenceInserter extends TracingConclusionInsertionVisitor {

		public InferenceInserter(Writer traceWriter) {
			super(traceWriter);
		}

		@Override
		protected boolean addInference(Conclusion conclusion, Context context) {
			return super.addInference(conclusion, context.getRoot().getContext());
		}		
	}
}
