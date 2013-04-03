/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.rules;

import java.util.Collection;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.BasicSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.ContextCreationListener;
import org.semanticweb.elk.reasoner.saturation.ContextImpl;
import org.semanticweb.elk.reasoner.saturation.ContextModificationListener;
import org.semanticweb.elk.reasoner.saturation.ExtendedSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.CombinedConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionDeapplicationVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionInsertionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionOccurranceCheckingVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.DisjointnessAxiom;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.NegativeSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.PositiveSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.util.collections.ArrayHashMap;

/**
 * Applies rules to all conclusions of partially completed contexts. Uses a
 * local saturation state to iterate over all conclusions, adds previously
 * non-existent conclusions to the ToDo queues in the main saturation state.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ContextCompletionFactory extends RuleApplicationFactory {

	// logger for this class
	protected static final Logger LOGGER_ = Logger
				.getLogger(ContextCompletionFactory.class);
	
	private final LocalSaturationState localState_;

	public ContextCompletionFactory(SaturationState saturationState) {
		super(saturationState);
		localState_ = new LocalSaturationState(saturationState.getOntologyIndex());
	}

	@Override
	public BaseEngine getDefaultEngine(ContextCreationListener listener,
			ContextModificationListener modListener) {
		return new ContextCompletionEngine();
	}
	
	@Override
	public SaturationState getSaturationState() {
		return localState_;
	}

	/**
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	private class ContextCompletionEngine extends
			RuleApplicationFactory.BaseEngine {

		private final ExtendedSaturationStateWriter writer_;

		protected ContextCompletionEngine() {
			super(new SaturationStatistics());
			writer_ = localState_.getDefaultWriter();
		}

		@Override
		public void submit(IndexedClassExpression root) {
			// create a local context for this ICE
			writer_.getCreateContext(root);
		}

		@Override
		protected BasicSaturationStateWriter getSaturationStateWriter() {
			return writer_;
		}

		@Override
		protected DecompositionRuleApplicationVisitor getDecompositionRuleApplicationVisitor() {
			return new ForwardDecompositionRuleApplicationVisitor(writer_);
		}

		@Override
		protected ConclusionVisitor<Boolean> getBaseConclusionProcessor(
				BasicSaturationStateWriter saturationStateWriter,
				SaturationStatistics localStatistics) {

			return new CombinedConclusionVisitor(
			// this checks for existence in the main context and inserts the
			// conclusion either into the main context's ToDo or into the local
			// context
					new ConditionalInsertionVisitor(saturationState.getWriter(ConclusionVisitor.DUMMY)),
					// this applies rules in the latter case
					filterRuleConclusionProcessor(
							new ConclusionDeapplicationVisitor(
									saturationStateWriter,
									getEngineCompositionRuleApplicationVisitor(localStatistics
											.getRuleStatistics()),
									getEngineDecompositionRuleApplicationVisitor(
											getDecompositionRuleApplicationVisitor(),
											localStatistics.getRuleStatistics())),
							localStatistics));
		}

	}

	/**
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	private static class LocalSaturationState implements SaturationState {

		private static final RuleApplicationVisitor DEFAULT_INIT_RULE_APP_VISITOR_ = new BasicCompositionRuleApplicationVisitor();
		
		private final Map<IndexedClassExpression, Context> contextMap_;
		private final OntologyIndex ontologyIndex_;
		private final Queue<Context> activeContexts_ = new ConcurrentLinkedQueue<Context>();

		LocalSaturationState(OntologyIndex index) {
			contextMap_ = new ArrayHashMap<IndexedClassExpression, Context>();
			ontologyIndex_ = index;
		}

		@Override
		public OntologyIndex getOntologyIndex() {
			return ontologyIndex_;
		}
		
		@Override
		public Context getContext(IndexedClassExpression ice) {
			return contextMap_.get(ice);
		}
		
		@Override
		public Collection<Context> getContexts() {
			return contextMap_.values();
		}

		@Override
		public Collection<IndexedClassExpression> getNotSaturatedContexts() {
			return contextMap_.keySet();
		}

		@Override
		public ExtendedSaturationStateWriter getExtendedWriter(
				ContextCreationListener contextCreationListener,
				ContextModificationListener contextModificationListener,
				RuleApplicationVisitor ruleAppVisitor,
				ConclusionVisitor<?> conclusionVisitor,
				boolean trackNewContextsAsUnsaturated) {
			return getDefaultWriter();
		}

		@Override
		public BasicSaturationStateWriter getWriter(
				ContextModificationListener contextModificationListener,
				ConclusionVisitor<?> conclusionVisitor) {
			return getDefaultWriter();
		}

		@Override
		public BasicSaturationStateWriter getWriter(
				ConclusionVisitor<?> conclusionVisitor) {
			return getDefaultWriter();
		}

		@Override
		public ExtendedSaturationStateWriter getExtendedWriter(
				ConclusionVisitor<?> conclusionVisitor) {
			return getDefaultWriter();
		}

		private LocalSaturationStateWriter getDefaultWriter() {
			return new LocalSaturationStateWriter(ontologyIndex_);
		}

		/**
		 * 
		 * @author Pavel Klinov
		 * 
		 *         pavel.klinov@uni-ulm.de
		 */
		private class LocalSaturationStateWriter implements
				ExtendedSaturationStateWriter {

			private final OntologyIndex ontologyIndex_;
			// needed for statistics
			//private final ConclusionVisitor<?> conclusionVisitor_;

			LocalSaturationStateWriter(OntologyIndex index) {
				ontologyIndex_ = index;
				//conclusionVisitor_ = RuleApplicationFactory.getEngineConclusionVisitor(localStatistics);
			}

			@Override
			public IndexedClassExpression getOwlThing() {
				return ontologyIndex_.getIndexedOwlThing();
			}

			@Override
			public IndexedClassExpression getOwlNothing() {
				return ontologyIndex_.getIndexedOwlNothing();
			}

			@Override
			public Context pollForActiveContext() {
				return activeContexts_.poll();
			}

			@Override
			public void produce(Context context, Conclusion conclusion) {
				if (LOGGER_.isTraceEnabled()) {
					LOGGER_.trace(context + ": produced local conclusion "
							+ conclusion);
				}

				//conclusion.accept(conclusionVisitor_, context);

				if (context.addToDo(conclusion)) {
					// context was activated
					activeContexts_.add(context);
				}
			}

			@Override
			public boolean markAsNotSaturated(Context context) {
				return false;
			}

			@Override
			public void clearNotSaturatedContexts() {
				// this state doesn't maintain unsaturated contexts
			}

			@Override
			public void resetContexts() {
				contextMap_.clear();
			}

			@Override
			public Context getCreateContext(IndexedClassExpression root) {
				Context context = null;

				synchronized (root) {
					context = contextMap_.get(root);

					if (context != null)
						return context;

					context = new ContextImpl(root);
					contextMap_.put(root, context);
				}

				initContext(context);

				if (LOGGER_.isTraceEnabled()) {
					LOGGER_.trace(context.getRoot() + ": local context created");
				}

				return context;
			}

			@Override
			public void initContext(Context context) {
				produce(context, new PositiveSubsumer(context.getRoot()));
				// apply all context initialization rules			
				LinkRule<Context> initRule = ontologyIndex_
						.getContextInitRuleHead();
				
				while (initRule != null) {
					initRule.accept(DEFAULT_INIT_RULE_APP_VISITOR_, this, context);
					initRule = initRule.next();
				}
			}

			@Override
			public void removeContext(Context context) {
				contextMap_.remove(context.getRoot());
			}
		}

	}

	/**
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	private class ConditionalInsertionVisitor implements
			ConclusionVisitor<Boolean> {

		private final ConclusionVisitor<Boolean> checker_;

		private final ConclusionVisitor<Boolean> inserter_;
		// this transforming visitor makes sure that all conclusions stored in
		// the ToDo queues of the main contexts do not reference local contexts.
		private final ConclusionVisitor<Conclusion> transformer_;
		
		private final BasicSaturationStateWriter mainStateWriter_;

		ConditionalInsertionVisitor(BasicSaturationStateWriter writer) {
			checker_ = new ConclusionOccurranceCheckingVisitor();
			inserter_ = new ConclusionInsertionVisitor();
			transformer_ = new ConclusionTransformingVisitor();
			mainStateWriter_ = writer;
		}

		private Boolean defaultVisit(Conclusion conclusion, Context context) {
			Context mainContext = context.getRoot().getContext();
			Conclusion transformed = conclusion.accept(transformer_, null);
			
			if (transformed.accept(checker_, mainContext)) {
				// insert locally
				if (LOGGER_.isTraceEnabled()) {
					LOGGER_.trace(context + ": conclusion " + conclusion + " exists in the main context, process it locally");
				}
				
				return conclusion.accept(inserter_, context);
			} else {
				// insert to the main context's ToDo
				if (LOGGER_.isTraceEnabled()) {
					LOGGER_.trace(context + ": conclusion " + conclusion + " does not exist in the main context, insert into TODO");
				}
				
				mainStateWriter_.produce(mainContext, transformed);

				return false;
			}
		}

		@Override
		public Boolean visit(NegativeSubsumer negSCE, Context context) {
			return defaultVisit(negSCE, context);
		}

		@Override
		public Boolean visit(PositiveSubsumer posSCE, Context context) {
			return defaultVisit(posSCE, context);
		}

		@Override
		public Boolean visit(BackwardLink link, Context context) {
			return defaultVisit(link, context);
		}

		@Override
		public Boolean visit(ForwardLink link, Context context) {
			return defaultVisit(link, context);
		}

		@Override
		public Boolean visit(Contradiction bot, Context context) {
			return defaultVisit(bot, context);
		}

		@Override
		public Boolean visit(Propagation propagation, Context context) {
			return defaultVisit(propagation, context);
		}

		@Override
		public Boolean visit(DisjointnessAxiom disjointnessAxiom,
				Context context) {
			return defaultVisit(disjointnessAxiom, context);
		}

	}
	
	
	/**
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	private static class ConclusionTransformingVisitor implements ConclusionVisitor<Conclusion> {

		private Context getMainContext(Context context) {
			return context.getRoot().getContext();
		}
		
		@Override
		public Conclusion visit(NegativeSubsumer negSCE, Context context) {
			return negSCE;
		}

		@Override
		public Conclusion visit(PositiveSubsumer posSCE, Context context) {
			return posSCE;
		}

		@Override
		public Conclusion visit(BackwardLink link, Context context) {
			return new BackwardLink(getMainContext(link.getSource()), link.getRelation());
		}

		@Override
		public Conclusion visit(ForwardLink link, Context context) {
			return new ForwardLink(link.getRelation(), getMainContext(link.getTarget()));
		}

		@Override
		public Conclusion visit(Contradiction bot, Context context) {
			return bot;
		}

		@Override
		public Conclusion visit(Propagation propagation, Context context) {
			return propagation;
		}

		@Override
		public Conclusion visit(DisjointnessAxiom disjointnessAxiom,
				Context context) {
			return disjointnessAxiom;
		}
		
	}

}
