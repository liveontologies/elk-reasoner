/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.saturation;

import java.util.Collections;

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.reasoner.ReasonerComputation;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationFactory;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationFactory.BaseEngine;
import org.semanticweb.elk.util.concurrent.computation.ComputationExecutor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorFactory;

// TODO: create a ReasonerComputation which requires no input
/**
 * A {@link ReasonerComputation} that continues saturation based on what's
 * currently initialized in the {@link RuleApplicationFactory}
 * 
 * @author Pavel Klinov
 * 
 */
public class ClassExpressionNoInputSaturation
		extends
		ReasonerComputation<IndexedClassExpression, ClassExpressionNoInputSaturationFactory> {

	public ClassExpressionNoInputSaturation(final ComputationExecutor executor,
			final int maxWorkers, final ProgressMonitor progressMonitor,
			final RuleApplicationFactory ruleAppFactory,
			final ContextModificationListener contextModificationListener) {

		super(Collections.<IndexedClassExpression> emptyList(),
				new ClassExpressionNoInputSaturationFactory(ruleAppFactory,
						contextModificationListener), executor, maxWorkers,
				progressMonitor);
	}

	/**
	 * Print statistics about the saturation computation
	 */

	public void printStatistics() {
		getInputProcessorFactory().printStatistics();
	}

	public SaturationStatistics getRuleAndConclusionStatistics() {
		return getInputProcessorFactory().getRuleAndConclusionStatistics();
	}

}

class ClassExpressionNoInputSaturationFactory
		implements
		InputProcessorFactory<IndexedClassExpression, ClassExpressionNoInputSaturationFactory.Engine> {

	private static final Logger LOGGER_ = Logger
			.getLogger(ClassExpressionNoInputSaturationFactory.class);

	private final RuleApplicationFactory ruleAppFactory_;

	private final ContextModificationListener contextModificationListener_;

	public ClassExpressionNoInputSaturationFactory(
			final RuleApplicationFactory ruleAppFactory,
			final ContextModificationListener contextModificationListener) {

		ruleAppFactory_ = ruleAppFactory;
		contextModificationListener_ = contextModificationListener;
	}

	@Override
	public Engine getEngine() {
		return new Engine();
	}

	@Override
	public void setInterrupt(boolean flag) {
		ruleAppFactory_.setInterrupt(flag);
	}

	@Override
	public boolean isInterrupted() {
		return ruleAppFactory_.isInterrupted();
	}

	@Override
	public void finish() {
		ruleAppFactory_.finish();
	}

	public SaturationStatistics getRuleAndConclusionStatistics() {
		return ruleAppFactory_.getSaturationStatistics();
	}

	/**
	 * Print statistics about the saturation
	 */
	public void printStatistics() {
		ruleAppFactory_.getSaturationStatistics().print(LOGGER_);
	}

	class Engine implements InputProcessor<IndexedClassExpression> {

		private final BaseEngine engine_;

		private Engine() {
			this.engine_ = ruleAppFactory_
					.getDefaultEngine(ContextCreationListener.DUMMY,
							contextModificationListener_);
		}

		@Override
		public void submit(IndexedClassExpression job) {
		}

		@Override
		public void process() {
			engine_.process();
		}

		@Override
		public void finish() {
			engine_.finish();
			// System.err.println(ruleAppFactory_.getStatistics().getRuleStatistics().getTotalRuleAppCount());
		}

	}

}
