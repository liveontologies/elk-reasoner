/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.saturation.rules;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.RuleStatistics;
import org.semanticweb.elk.reasoner.saturation.ContextCreationListener;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Bottom;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionsCounter;
import org.semanticweb.elk.reasoner.saturation.conclusions.DisjointnessAxiom;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.IndexChange;
import org.semanticweb.elk.reasoner.saturation.conclusions.NegativeSuperClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.PositiveSuperClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationFactory.Engine;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorFactory;
import org.semanticweb.elk.util.logging.CachedTimeThread;

/**
 * The factory for engines for concurrently computing the saturation of class
 * expressions. This is the class that implements the application of inference
 * rules.
 * 
 * @author Frantisek Simancik
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch
 * 
 */
public class RuleApplicationFactory implements
		InputProcessorFactory<IndexedClassExpression, Engine> {

	// logger for this class
	protected static final Logger LOGGER_ = Logger
			.getLogger(RuleApplicationFactory.class);

	
	protected final SaturationState saturationState_;
	/**
	 * The approximate number of contexts ever created by this engine. This
	 * number is used not only for statistical purposes, but also by saturation
	 * engine callers to control when to submit new saturation tasks: if there
	 * are too many unprocessed contexts, new saturation tasks are not
	 * submitted.
	 */
	protected final AtomicInteger approximateContextNumber_ = new AtomicInteger(
			0);

	/**
	 * To reduce thread congestion, {@link #approximateContextNumber_} is not
	 * updated immediately when contexts are created, but after a worker creates
	 * {@link #contextUpdateInterval_} new contexts.
	 */
	private final int contextUpdateInterval_ = 32;

	/**
	 * The {@link ConclusionsCounter} aggregated for all workers
	 */
	private final ConclusionsCounter aggregatedConclusionsCounter_;

	/**
	 * The {@link RuleStatistics} aggregated for all workers
	 */
	private final RuleStatistics aggregatedRuleStats_;

	/**
	 * The {@link ThisStatistics} aggregated for all workers
	 */
	private final ThisStatistics aggregatedFactoryStats_;
	
	private final boolean trackModifiedContexts_;
	
	public RuleApplicationFactory(final SaturationState saturationState) {
		this(saturationState, false);
	}
	
	public RuleApplicationFactory(final SaturationState saturationState, boolean trackModifiedContexts) {
		this.aggregatedConclusionsCounter_ = new ConclusionsCounter();
		this.aggregatedRuleStats_ = new RuleStatistics();
		this.aggregatedFactoryStats_ = new ThisStatistics();
		this.saturationState_ = saturationState;
		this.trackModifiedContexts_ = trackModifiedContexts;
	}	
	
	@Override
	public Engine getEngine() {
		return new Engine(new AddConclusionVisitor());
	}

	@Override
	public void finish() {
		checkStatistics();
	}

	/**
	 * Returns the approximate number of contexts created. The real number of
	 * contexts is larger, but to ensure good concurrency performance it is not
	 * updated often. It is exact when all engines created by this factory
	 * finish.
	 * 
	 * @return the approximate number of created contexts
	 */
	public int getApproximateContextNumber() {
		return approximateContextNumber_.get();
	}

	/**
	 * Prints statistic of rule applications
	 */
	public void printStatistics() {
		if (LOGGER_.isDebugEnabled()) {
			checkStatistics();
			// CONTEXT STATISTICS:
			if (approximateContextNumber_.get() > 0)
				LOGGER_.debug("Contexts created: " + approximateContextNumber_);
			if (approximateContextNumber_.get() > 0)
				LOGGER_.debug("Contexts processsing: "
						+ aggregatedFactoryStats_.contContextProcess + " ("
						+ aggregatedFactoryStats_.timeContextProcess + " ms)");
			// CONCLUSIONS STATISTICS:
			if (aggregatedConclusionsCounter_
					.getPositiveSuperClassExpressionInfNo()
					+ aggregatedConclusionsCounter_
							.getNegativeSuperClassExpressionInfNo() > 0)
				LOGGER_.debug("Super classes positive/negative/unique: "
						+ aggregatedConclusionsCounter_
								.getPositiveSuperClassExpressionInfNo()
						+ "/"
						+ aggregatedConclusionsCounter_
								.getNegativeSuperClassExpressionInfNo()
						+ "/"
						+ aggregatedConclusionsCounter_
								.getSuperClassExpressionNo()
						+ " ("
						+ aggregatedConclusionsCounter_
								.getSuperClassExpressionTime() + " ms)");
			if (aggregatedConclusionsCounter_.getBackLinkInfNo() > 0)
				LOGGER_.debug("Backward Links produced/unique: "
						+ aggregatedConclusionsCounter_.getBackLinkInfNo()
						+ "/" + aggregatedConclusionsCounter_.getBackLinkNo()
						+ " ("
						+ aggregatedConclusionsCounter_.getBackLinkTime()
						+ " ms)");
			if (aggregatedConclusionsCounter_.getForwLinkInfNo() > 0)
				LOGGER_.debug("Forward Links produced/unique: "
						+ aggregatedConclusionsCounter_.getForwLinkInfNo()
						+ "/" + aggregatedConclusionsCounter_.getForwLinkNo()
						+ " ("
						+ aggregatedConclusionsCounter_.getForwLinkTime()
						+ " ms)");
			LOGGER_.debug("Total conclusion processing time: "
					+ (aggregatedConclusionsCounter_
							.getSuperClassExpressionTime()
							+ aggregatedConclusionsCounter_.getBackLinkTime() + aggregatedConclusionsCounter_
								.getForwLinkTime()) + " ms"

			);

			// RULES STATISTICS:
			if (aggregatedRuleStats_
					.getObjectSomeValuesFromCompositionRuleCount() > 0)
				LOGGER_.debug("ObjectSomeValuesFrom composition rules: "
						+ aggregatedRuleStats_
								.getObjectSomeValuesFromCompositionRuleCount()
						+ " ("
						+ aggregatedRuleStats_
								.getObjectSomeValuesFromCompositionRuleTime()
						+ " ms)");
			if (aggregatedRuleStats_
					.getObjectSomeValuesFromDecompositionRuleCount() > 0)
				LOGGER_.debug("ObjectSomeValuesFrom decomposition rules: "
						+ aggregatedRuleStats_
								.getObjectSomeValuesFromDecompositionRuleCount()
						+ " ("
						+ aggregatedRuleStats_
								.getObjectSomeValuesFromDecompositionRuleTime()
						+ " ms)");
			if (aggregatedRuleStats_
					.getObjectSomeValuesFromBackwardLinkRuleCount() > 0)
				LOGGER_.debug("ObjectSomeValuesFrom backward link rules: "
						+ aggregatedRuleStats_
								.getObjectSomeValuesFromBackwardLinkRuleCount()
						+ " ("
						+ aggregatedRuleStats_
								.getObjectSomeValuesFromBackwardLinkRuleTime()
						+ " ms)");
			if (aggregatedRuleStats_
					.getObjectIntersectionOfCompositionRuleCount() > 0)
				LOGGER_.debug("ObjectIntersectionOf composition rules: "
						+ aggregatedRuleStats_
								.getObjectIntersectionOfCompositionRuleCount()
						+ " ("
						+ aggregatedRuleStats_
								.getObjectIntersectionOfCompositionRuleTime()
						+ " ms)");
			if (aggregatedRuleStats_
					.getObjectIntersectionOfDecompositionRuleCount() > 0)
				LOGGER_.debug("ObjectIntersectionOf decomposition rules: "
						+ aggregatedRuleStats_
								.getObjectIntersectionOfDecompositionRuleCount()
						+ " ("
						+ aggregatedRuleStats_
								.getObjectIntersectionOfDecompositionRuleTime()
						+ " ms)");
			if (aggregatedRuleStats_.getForwardLinkBackwardLinkRuleCount() > 0)
				LOGGER_.debug("ForwardLink backward link rules: "
						+ aggregatedRuleStats_
								.getForwardLinkBackwardLinkRuleCount()
						+ " ("
						+ aggregatedRuleStats_
								.getForwardLinkBackwardLinkRuleTime() + " ms)");
			if (aggregatedRuleStats_.getClassDecompositionRuleCount() > 0)
				LOGGER_.debug("Class decomposition rules: "
						+ aggregatedRuleStats_.getClassDecompositionRuleCount()
						+ " ("
						+ aggregatedRuleStats_.getClassDecompositionRuleTime()
						+ "ms)");
			if (aggregatedRuleStats_.getClassBottomBackwardLinkRuleCount() > 0)
				LOGGER_.debug("owl:Nothing backward link rules: "
						+ aggregatedRuleStats_
								.getClassBottomBackwardLinkRuleCount()
						+ " ("
						+ aggregatedRuleStats_
								.getClassBottomBackwardLinkRuleTime() + " ms)");
			if (aggregatedRuleStats_.getSubClassOfRuleCount() > 0)
				LOGGER_.debug("SubClassOf expansion rules: "
						+ aggregatedRuleStats_.getSubClassOfRuleCount() + " ("
						+ aggregatedRuleStats_.getSubClassOfRuleTime() + " ms)");
			LOGGER_.debug("Total rules time: "
					+ (aggregatedRuleStats_
							.getObjectSomeValuesFromCompositionRuleTime()
							+ aggregatedRuleStats_
									.getObjectSomeValuesFromDecompositionRuleTime()
							+ aggregatedRuleStats_
									.getObjectSomeValuesFromBackwardLinkRuleTime()
							+ aggregatedRuleStats_
									.getObjectIntersectionOfCompositionRuleTime()
							+ aggregatedRuleStats_
									.getObjectIntersectionOfDecompositionRuleTime()
							+ aggregatedRuleStats_
									.getForwardLinkBackwardLinkRuleTime()
							+ aggregatedRuleStats_
									.getClassDecompositionRuleTime()
							+ aggregatedRuleStats_
									.getClassBottomBackwardLinkRuleTime() + aggregatedRuleStats_
								.getSubClassOfRuleTime()) + " ms");
		}
	}

	private void checkStatistics() {
		if (aggregatedFactoryStats_.contContextProcess < approximateContextNumber_
				.get())
			LOGGER_.error("More contexts than context activations!");
		if (aggregatedConclusionsCounter_
				.getPositiveSuperClassExpressionInfNo()
				+ aggregatedConclusionsCounter_
						.getNegativeSuperClassExpressionInfNo() < aggregatedConclusionsCounter_
					.getSuperClassExpressionNo())
			LOGGER_.error("More unique derived superclasses than produced!");
		if (aggregatedConclusionsCounter_.getBackLinkInfNo() < aggregatedConclusionsCounter_
				.getBackLinkNo())
			LOGGER_.error("More unique backward links than produced!");
		if (aggregatedConclusionsCounter_.getForwLinkInfNo() < aggregatedConclusionsCounter_
				.getForwLinkNo())
			LOGGER_.error("More unique forward links than produced!");
	}

	/**
	 * 
	 */
	public class Engine implements InputProcessor<IndexedClassExpression>, RuleEngine, ContextCreationListener {

		protected final ConclusionVisitor<Boolean> conclusionVisitor_;
		
		/**
		 * Local {@link ConclusionsCounter} created for every worker
		 */
		protected final ConclusionsCounter conclusionsCounter_ = new ConclusionsCounter();
		/**
		 * Local {@link RuleStatistics} created for every worker
		 */
		protected final RuleStatistics ruleStats_ = new RuleStatistics();
		/**
		 * Local {@link ThisStatistics} created for every worker
		 */
		protected final ThisStatistics factoryStats_ = new ThisStatistics();
		/**
		 * Worker-local counter for the number of created contexts
		 */
		private int localContextNumber = 0;

		protected Engine(ConclusionVisitor<Boolean> visitor) {
			conclusionVisitor_ = visitor;
			saturationState_.registerContextCreationListener(this);
		}

		protected ConclusionVisitor<Boolean> getConclusionVisitor() {
			return conclusionVisitor_;
		}
		
		@Override
		public void submit(IndexedClassExpression job) {
			saturationState_.getCreateContext(job);
		}

		@Override
		public void process() {
			factoryStats_.timeContextProcess -= CachedTimeThread.currentTimeMillis;
			for (;;) {
				if (Thread.currentThread().isInterrupted())
					break;
				
				Context nextContext = saturationState_.pollForContext();
				
				if (nextContext == null) {
					break;
				}
				else {
					process(nextContext);
				}
			}
			factoryStats_.timeContextProcess += CachedTimeThread.currentTimeMillis;
		}

		@Override
		public void finish() {
			approximateContextNumber_.addAndGet(localContextNumber);
			localContextNumber = 0;
			aggregatedConclusionsCounter_.merge(conclusionsCounter_);
			aggregatedRuleStats_.merge(ruleStats_);
			aggregatedFactoryStats_.merge(factoryStats_);
			saturationState_.deregisterContextCreationListener(this);
		}

		/**
		 * @return the object collecting statistics of rule applications
		 */
		@Override
		public ConclusionsCounter getConclusionsCounter() {
			return this.conclusionsCounter_;
		}

		@Override
		public RuleStatistics getRulesTimer() {
			return this.ruleStats_;
		}

		/**
		 * Process all scheduled items in the given context
		 * 
		 * @param context
		 *            the context in which to process the scheduled items
		 */
		protected void process(Context context) {
			factoryStats_.contContextProcess++;
			for (;;) {
				Conclusion conclusion = context.takeToDo();
				if (conclusion == null) {
					if (context.deactivate()) {
						// context was re-activated
						continue;
					}
					else {
						break;
					}
				}
				
				if (preApply(conclusion, context)) {
					process(conclusion, context);
					postApply(conclusion, context);
				}
			}
		}

		protected void process(Conclusion conclusion, Context context) {
			conclusion.apply(saturationState_, context);
		}

		protected boolean preApply(Conclusion conclusion, Context context) {
			return conclusion.accept(conclusionVisitor_, context);
		}

		protected void postApply(Conclusion conclusion, Context context) {
			// nothing here
		}

		@Override
		public void notifyContextCreation(Context newContext) {
			if (++localContextNumber == contextUpdateInterval_) {
				approximateContextNumber_.addAndGet(localContextNumber);
				localContextNumber = 0;
			}
			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace(newContext.getRoot() + ": context created");
			}
		}		
	}
	
	protected abstract class BaseConclusionVisitor implements ConclusionVisitor<Boolean> {
		
		protected void markAsModified(Context context) {
			// re-use the saturation flag as a sign that the context is modified for the first time
			if (trackModifiedContexts_ && context.isSaturated()) {
				saturationState_.markAsModified(context);
				context.setSaturated(false);
			}
		}
	}
	
	/**
	 * Used to add different kinds of conclusions to the context
	 */
	protected class AddConclusionVisitor extends BaseConclusionVisitor {

		@Override
		public Boolean visit(NegativeSuperClassExpression negSCE,
				Context context) {
			
			if (context.addSuperClassExpression(negSCE.getExpression())) {
				//statistics_.superClassExpressionNo++;
				//statistics_.negSuperClassExpressionInfNo++;
				markAsModified(context);
				
				return true;
			}
			
			return false;
		}

		@Override
		public Boolean visit(PositiveSuperClassExpression posSCE,
				Context context) {
			
			if (context.addSuperClassExpression(posSCE.getExpression())) {
				//statistics_.superClassExpressionNo++;
				//statistics_.posSuperClassExpressionInfNo++;
				markAsModified(context);
				
				return true;
			}
			
			return false;
		}

		@Override
		public Boolean visit(BackwardLink link, Context context) {
			//statistics_.backLinkInfNo++;
			if (context.addBackwardLink(link)) {
				markAsModified(context);
				
				return true;
			}
			
			return false;
			//statistics_.backLinkNo++;
		}

		@Override
		public Boolean visit(ForwardLink link, Context context) {
			//statistics_.forwLinkInfNo++;
			if (link.addToContextBackwardLinkRule(context)) {
			
				return true;
			}
			
			return false;
			//statistics_.forwLinkNo++;
		}

		@Override
		public Boolean visit(IndexChange indexChange, Context context) {
			// need not add this element, just apply all its rules
			return true;
		}

		@Override
		public Boolean visit(Bottom bot, Context context) {
			return !context.isInconsistent();
		}

		@Override
		public Boolean visit(Propagation propagation, Context context) {
			if (propagation.addToContextBackwardLinkRule(context)) {
				
				return true;
			}
			
			return false;
		}

		@Override
		public Boolean visit(DisjointnessAxiom axiom, Context context) {
			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace("Trying to add disjointness axiom to " + context.getRoot());
			}
			return !context.addDisjointnessAxiom(axiom.getAxiom());
		}
	}
	
	/**
	 * Counters accumulating statistical information about this factory.
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	protected static class ThisStatistics {
		/**
		 * the number of times a context has been processed using
		 * {@link Engine#process(Context)}
		 */
		int contContextProcess;

		/**
		 * the time spent within {@link Engine#process()}
		 */
		long timeContextProcess;

		public synchronized void merge(ThisStatistics statistics) {
			this.contContextProcess += statistics.contContextProcess;
			this.timeContextProcess += statistics.timeContextProcess;
		}
	}

}
