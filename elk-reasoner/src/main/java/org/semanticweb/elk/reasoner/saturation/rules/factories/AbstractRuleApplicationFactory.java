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
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A skeleton for implementing {@link RuleApplicationFactory}
 * 
 * @author "Yevgeny Kazakov"
 */
public abstract class AbstractRuleApplicationFactory implements
		RuleApplicationFactory {

	// logger for this class
	protected static final Logger LOGGER_ = LoggerFactory
			.getLogger(AbstractRuleApplicationFactory.class);

	/**
	 * The main {@link SaturationState} this factory works with
	 */
	private final SaturationState saturationState_;

	/**
	 * The {@link SaturationStatistics} aggregated for all workers
	 */
	private final SaturationStatistics aggregatedStats_;

	public AbstractRuleApplicationFactory(final SaturationState saturationState) {
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
			ConclusionVisitor<Context, Boolean> conclusionProcessor,
			SaturationStateWriter saturationStateWriter,
			WorkerLocalTodo localTodo, SaturationStatistics localStatistics) {
		return new BasicRuleEngine(saturationState_.getOntologyIndex(),
				conclusionProcessor, localTodo, saturationStateWriter,
				aggregatedStats_, localStatistics);
	}

	/**
	 * Produces the {@link SaturationStateWriter} that produces
	 * {@link Conclusion}s that are processed within this
	 * {@link RuleApplicationFactory}. The given {@link WorkerLocalTodo} is used
	 * to optimize producing & processing of {@link Conclusion}s for the
	 * currently processed {@link Context}, and the provided local
	 * {@link SaturationStatistics} is used to accumulate the statistics about
	 * this {@link SaturationStateWriter}. The returned
	 * {@link SaturationStateWriter} should be the same as passed to
	 * {@link #getEngine(ContextCreationListener, ContextModificationListener)},
	 * but it can be used in other places, e.g., to define the
	 * {@link ConclusionVisitor}.
	 * 
	 * @param writer
	 * @param localTodo
	 * @return
	 */
	static SaturationStateWriter addLocalTodoAndStatistics(
			SaturationStateWriter writer, WorkerLocalTodo localTodo,
			SaturationStatistics localStatistics) {
		// optimizing the writer to deal more efficiently with conclusions to be
		// processed within the same context
		WorkerLocalizedSaturationStateWriter localizedWriter = new WorkerLocalizedSaturationStateWriter(
				writer, localTodo);
		// count all conclusions produced
		return SaturationUtils.getStatsAwareWriter(localizedWriter,
				localStatistics);

	}

	/**
	 * @param creationListener
	 * @param modificationListener
	 * @return a new writer for the main {@link SaturationState} to be used by
	 *         engine
	 */
	SaturationStateWriter getWriter(ContextCreationListener creationListener,
			ContextModificationListener modificationListener) {
		// by default the writer can create new contexts
		return saturationState_.getContextCreatingWriter(creationListener,
				modificationListener);
	}

	protected abstract InputProcessor<IndexedClassExpression> getEngine(
			RuleVisitor ruleVisitor, SaturationStateWriter writer,
			SaturationStatistics localStatistics);

	@Override
	public final InputProcessor<IndexedClassExpression> getEngine(
			ContextCreationListener creationListener,
			ContextModificationListener modificationListener) {
		SaturationStatistics localStatistics = new SaturationStatistics();
		creationListener = SaturationUtils.addStatsToContextCreationListener(
				creationListener, localStatistics.getContextStatistics());
		modificationListener = SaturationUtils
				.addStatsToContextModificationListener(modificationListener,
						localStatistics.getContextStatistics());
		SaturationStateWriter writer = getWriter(creationListener,
				modificationListener);
		RuleVisitor ruleVisitor = SaturationUtils
				.getStatsAwareRuleVisitor(localStatistics.getRuleStatistics());
		return getEngine(ruleVisitor, writer, localStatistics);
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
	public SaturationState getSaturationState() {
		return saturationState_;
	}

}
