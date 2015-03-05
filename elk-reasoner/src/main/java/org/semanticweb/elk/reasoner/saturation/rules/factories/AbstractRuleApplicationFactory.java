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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.ContextCreationListener;
import org.semanticweb.elk.reasoner.saturation.ContextModificationListener;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.SaturationUtils;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.SimpleInterrupter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A skeleton for implementing {@link RuleApplicationFactory}
 * 
 * @author "Yevgeny Kazakov"
 */
public abstract class AbstractRuleApplicationFactory<C extends Context> extends
		SimpleInterrupter implements RuleApplicationFactory<C> {

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

	public AbstractRuleApplicationFactory(
			final SaturationState<? extends C> saturationState) {
		this.saturationState_ = saturationState;
		this.aggregatedStats_ = new SaturationStatistics();
	}

	/**
	 * @param conclusionProcessor
	 * @param saturationStateWriter
	 * @param localStatistics
	 * @return an {@link InputProcessor} that processes {@link Conclusion}s in
	 *         {@link Context}s within an individual worker thread for the input
	 *         root {@link IndexedClassExpression} using the supplied
	 *         {@link SaturationStateWriter} and updates the supplied local
	 *         {@link SaturationStatistics} accordingly
	 */
	protected InputProcessor<IndexedClassExpression> getEngine(
			ConclusionVisitor<? super Context, Boolean> conclusionProcessor,
			SaturationStateWriter<? extends C> saturationStateWriter,
			WorkerLocalTodo localTodo, SaturationStatistics localStatistics) {
		conclusionProcessor = SaturationUtils.getTimedConclusionVisitor(
				conclusionProcessor, localStatistics);
		return new BasicRuleEngine(saturationState_.getOntologyIndex(),
				conclusionProcessor, localTodo, this, saturationStateWriter,
				aggregatedStats_, localStatistics);
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
	 * An instance of {@link ConclusionVisitor} that processes
	 * {@link Conclusion}s within {@link Context} by an individual worker.
	 * 
	 * @param ruleVisitor
	 * @param writer
	 * @param localStatistics
	 * @return
	 */
	protected abstract ConclusionVisitor<? super Context, Boolean> getConclusionProcessor(
			RuleVisitor ruleVisitor, SaturationStateWriter<? extends C> writer,
			SaturationStatistics localStatistics);

	@Override
	public final InputProcessor<IndexedClassExpression> getEngine(
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
		WorkerLocalizedSaturationStateWriter<C> optimizedWriter = new WorkerLocalizedSaturationStateWriter<C>(
				writer, localTodo);
		writer = SaturationUtils.<C> getStatsAwareWriter(optimizedWriter,
				localStatistics);
		writer = getFinalWriter(writer);
		RuleVisitor ruleVisitor = SaturationUtils
				.getStatsAwareRuleVisitor(localStatistics.getRuleStatistics());
		return getEngine(
				getConclusionProcessor(ruleVisitor, writer, localStatistics),
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
