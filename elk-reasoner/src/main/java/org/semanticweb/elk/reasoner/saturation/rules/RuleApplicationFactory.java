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

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.RuleStatistics;
import org.semanticweb.elk.reasoner.saturation.ContextCreationListener;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.CombinedConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionApplicationVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionInsertionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionsCounter;
import org.semanticweb.elk.reasoner.saturation.conclusions.MarkingConclusionVisitor;
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

	final SaturationState saturationState;

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

	public RuleApplicationFactory(final SaturationState saturationState,
			boolean trackModifiedContexts) {
		this.aggregatedConclusionsCounter_ = new ConclusionsCounter();
		this.aggregatedRuleStats_ = new RuleStatistics();
		this.aggregatedFactoryStats_ = new ThisStatistics();
		this.saturationState = saturationState;
		this.trackModifiedContexts_ = trackModifiedContexts;
	}

	@Override
	public Engine getEngine() {
		return new Engine();
	}

	public Engine getEngine(ContextCreationListener listener) {
		return new Engine(listener);
	}

	@Override
	public void finish() {
		checkStatistics();
	}

	/**
	 * Prints statistic of rule applications
	 */
	public void printStatistics() {
		if (LOGGER_.isDebugEnabled()) {
			checkStatistics();
			// CONTEXT STATISTICS:
			if (aggregatedFactoryStats_.countCreatedContexts > 0)
				LOGGER_.debug("Contexts created: "
						+ aggregatedFactoryStats_.countCreatedContexts);
			if (aggregatedFactoryStats_.countCreatedContexts > 0)
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
		if (aggregatedFactoryStats_.contContextProcess < aggregatedFactoryStats_.countCreatedContexts)
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

	static ContextCreationListener getEngineListener(
			final ContextCreationListener listener,
			final ThisStatistics factoryStats) {
		return new ContextCreationListener() {
			@Override
			public void notifyContextCreation(Context newContext) {
				factoryStats.countCreatedContexts++;
				listener.notifyContextCreation(newContext);
			}
		};
	}

	/**
	 * 
	 */
	public class Engine implements InputProcessor<IndexedClassExpression>,
			RuleEngine {

		protected final SaturationState.Writer saturationStateWriter;

		protected final ConclusionVisitor<Boolean> conclusionProcessor;

		/**
		 * Local {@link ConclusionsCounter} created for every worker
		 */
		protected final ConclusionsCounter conclusionsCounter = new ConclusionsCounter();
		/**
		 * Local {@link RuleStatistics} created for every worker
		 */
		protected final RuleStatistics ruleStats = new RuleStatistics();
		/**
		 * Local {@link ThisStatistics} created for every worker
		 */
		protected final ThisStatistics factoryStats;

		protected Engine(SaturationState.Writer saturationStateWriter,
				ThisStatistics factoryStats) {
			this.conclusionProcessor = getConclusionProcessor(saturationStateWriter);
			this.saturationStateWriter = saturationStateWriter;
			this.factoryStats = factoryStats;
		}

		protected Engine(SaturationState.Writer saturationStateWriter) {
			this(saturationStateWriter, new ThisStatistics());
		}

		protected Engine() {
			this(saturationState.getWriter());
		}

		protected Engine(final ContextCreationListener listener,
				final ThisStatistics factoryStats) {
			this(saturationState.getWriter(getEngineListener(listener,
					factoryStats)), factoryStats);
		}

		protected Engine(final ContextCreationListener listener) {
			this(listener, new ThisStatistics());
		}

		protected ConclusionVisitor<Boolean> getConclusionVisitor() {
			return conclusionProcessor;
		}

		@Override
		public void submit(IndexedClassExpression job) {
			saturationStateWriter.getCreateContext(job);
		}

		@Override
		public void process() {
			factoryStats.timeContextProcess -= CachedTimeThread.currentTimeMillis;
			for (;;) {
				if (Thread.currentThread().isInterrupted())
					break;

				Context nextContext = saturationStateWriter.pollForContext();

				if (nextContext == null) {
					break;
				} else {
					process(nextContext);
				}
			}
			factoryStats.timeContextProcess += CachedTimeThread.currentTimeMillis;
		}

		@Override
		public void finish() {
			aggregatedConclusionsCounter_.merge(conclusionsCounter);
			aggregatedRuleStats_.merge(ruleStats);
			aggregatedFactoryStats_.merge(factoryStats);
		}

		/**
		 * @return the object collecting statistics of rule applications
		 */
		@Override
		public ConclusionsCounter getConclusionsCounter() {
			return this.conclusionsCounter;
		}

		@Override
		public RuleStatistics getRulesTimer() {
			return this.ruleStats;
		}

		/**
		 * Process all scheduled items in the given context
		 * 
		 * @param context
		 *            the context in which to process the scheduled items
		 */
		protected void process(Context context) {
			factoryStats.contContextProcess++;
			for (;;) {
				Conclusion conclusion = context.takeToDo();
				if (conclusion == null) {
					if (context.deactivate()) {
						// context was re-activated
						continue;
					} else {
						break;
					}
				}
				conclusion.accept(conclusionProcessor, context);
			}
		}

		protected ConclusionVisitor<Boolean> getBaseConclusionProcessor(
				SaturationState.Writer saturationStateWriter) {
			return new CombinedConclusionVisitor(
					new ConclusionInsertionVisitor(),
					new ConclusionApplicationVisitor(saturationStateWriter));
		}

		protected ConclusionVisitor<Boolean> getConclusionProcessor(
				SaturationState.Writer saturationStateWriter) {
			return trackModifiedContexts_ ? new CombinedConclusionVisitor(
					getBaseConclusionProcessor(saturationStateWriter),
					new MarkingConclusionVisitor(saturationStateWriter))
					: getBaseConclusionProcessor(saturationStateWriter);
		}
	}

	/**
	 * Counters accumulating statistical information about this factory.
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	static class ThisStatistics {

		/**
		 * The number of created contexts
		 */
		int countCreatedContexts;

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
