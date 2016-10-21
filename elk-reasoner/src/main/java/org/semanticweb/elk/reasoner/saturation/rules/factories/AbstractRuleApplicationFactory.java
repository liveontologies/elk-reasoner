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
package org.semanticweb.elk.reasoner.saturation.rules.factories;

import org.semanticweb.elk.ModifiableReference;
import org.semanticweb.elk.Reference;
import org.semanticweb.elk.ReferenceImpl;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.ContextCreationListener;
import org.semanticweb.elk.reasoner.saturation.ContextModificationListener;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.SaturationUtils;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.rules.RuleStatistics;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InterruptMonitor;
import org.semanticweb.elk.util.concurrent.computation.DelegateInterruptMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A skeleton for implementing {@link RuleApplicationFactory}
 * 
 * @author "Yevgeny Kazakov"
 *
 * @param <C>
 *            the type of the context used by this factory
 * @param <I>
 *            the type of the input processed by this factory
 */
public abstract class AbstractRuleApplicationFactory<C extends Context, I extends RuleApplicationInput>
		extends
			DelegateInterruptMonitor
		implements RuleApplicationFactory<C, I> {

	// logger for this class
	protected static final Logger LOGGER_ = LoggerFactory
			.getLogger(AbstractRuleApplicationFactory.class);

	/**
	 * The main {@link SaturationState} this factory works with
	 */
	private final SaturationState<? extends C> saturationState_;

	/**
	 * The {@link SaturationStatistics} aggregated for all workers
	 */
	private final SaturationStatistics aggregatedStats_;

	public AbstractRuleApplicationFactory(final InterruptMonitor interrupter,
			final SaturationState<? extends C> saturationState) {
		super(interrupter);
		this.saturationState_ = saturationState;
		this.aggregatedStats_ = new SaturationStatistics();
	}

	/**
	 * @param activeContext
	 * @param inferenceProcessor
	 * @param saturationStateWriter
	 * @param localStatistics
	 * @return an {@link InputProcessor} that processes {@link ClassInference}s
	 *         in {@link Context}s within an individual worker thread for the
	 *         input root {@link IndexedClassExpression} using the supplied
	 *         {@link SaturationStateWriter} and updates the supplied local
	 *         {@link SaturationStatistics} accordingly
	 */
	protected InputProcessor<I> getEngine(
			ModifiableReference<Context> activeContext,
			ClassInference.Visitor<Boolean> inferenceProcessor,
			SaturationStateWriter<? extends C> saturationStateWriter,
			WorkerLocalTodo localTodo, SaturationStatistics localStatistics) {
		return new BasicRuleEngine<I>(activeContext, inferenceProcessor,
				localTodo, this, saturationStateWriter, aggregatedStats_,
				localStatistics);
	}

	/**
	 * Creates a new primary {@link SaturationStateWriter} for the
	 * {@link SaturationState} to be used by an engine of this
	 * {@link RuleApplicationFactory}. This {@link SaturationStateWriter} can be
	 * further extended and optimized.
	 * 
	 * @param creationListener
	 * @param modificationListener
	 * @return a new writer for the main {@link SaturationState} to be used by
	 *         engine.
	 */
	SaturationStateWriter<? extends C> getBaseWriter(
			ContextCreationListener creationListener,
			ContextModificationListener modificationListener) {
		// by default the writer can create new contexts
		return saturationState_.getContextCreatingWriter(creationListener,
				modificationListener);
	}

	/**
	 * Produces the final {@link SaturationStateWriter} to be used by an engine
	 * of this {@link RuleApplicationFactory} using the given candidate.
	 * 
	 * @param writer
	 *            the {@link SaturationStateWriter} intended to be used by this
	 *            {@link RuleApplicationFactory}
	 * @return the actual {@link SaturationStateWriter} that will be used
	 */
	SaturationStateWriter<? extends C> getFinalWriter(
			SaturationStateWriter<? extends C> writer) {
		return writer;
	}

	/**
	 * An instance of {@link ClassInference.Visitor} that processes
	 * {@link ClassInference}s within {@link Context} by an individual worker.
	 * 
	 * @param activeContext
	 * @param ruleVisitor
	 * @param writer
	 * @param localStatistics
	 * @return
	 */
	protected abstract ClassInference.Visitor<Boolean> getInferenceProcessor(
			Reference<Context> activeContext, RuleVisitor<?> ruleVisitor,
			SaturationStateWriter<? extends C> writer,
			SaturationStatistics localStatistics);

	/**
	 * Creates a {@link RuleVisitor} that specifies how the rules are applied 
	 * 
	 * @param statistics
	 * @return the {@link RuleVisitor} used by this 
	 */
	@SuppressWarnings("static-method")
	protected RuleVisitor<?> getRuleVisitor(RuleStatistics statistics) {
		return SaturationUtils.getStatsAwareRuleVisitor(statistics);
	}
	
	@Override
	public final InputProcessor<I> getEngine(
			ContextCreationListener creationListener,
			ContextModificationListener modificationListener) {
		SaturationStatistics localStatistics = new SaturationStatistics();
		localStatistics.startMeasurements();
		creationListener = SaturationUtils.addStatsToContextCreationListener(
				creationListener, localStatistics.getContextStatistics());
		modificationListener = SaturationUtils
				.addStatsToContextModificationListener(modificationListener,
						localStatistics.getContextStatistics());
		SaturationStateWriter<? extends C> writer = getBaseWriter(
				creationListener, modificationListener);
		WorkerLocalTodo localTodo = new WorkerLocalTodoImpl();
		writer = new WorkerLocalizedSaturationStateWriter<C>(writer, localTodo);
		writer = getFinalWriter(writer);
		RuleVisitor<?> ruleVisitor = getRuleVisitor(localStatistics.getRuleStatistics());
		ModifiableReference<Context> activeContext = new ReferenceImpl<Context>();
		return getEngine(
				activeContext, getInferenceProcessor(activeContext, ruleVisitor,
						writer, localStatistics),
				writer, localTodo, localStatistics);
	}

	@Override
	public void dispose() {
		// aggregatedStats_.check(LOGGER_);
	}

	@Override
	public SaturationStatistics getSaturationStatistics() {
		return aggregatedStats_;
	}

	@Override
	public final SaturationState<? extends C> getSaturationState() {
		return saturationState_;
	}

}
