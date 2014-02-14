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

import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.reasoner.ReasonerComputation;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationAdditionFactory;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationFactory;
import org.semanticweb.elk.util.concurrent.computation.ComputationExecutor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: create a ReasonerComputation which requires no input
/**
 * A {@link ReasonerComputation} that continues saturation based on what's
 * currently initialized in the {@link RuleApplicationAdditionFactory}
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
		inputProcessorFactory.printStatistics();
	}

	public SaturationStatistics getRuleAndConclusionStatistics() {
		return inputProcessorFactory.getRuleAndConclusionStatistics();
	}

}

class ClassExpressionNoInputSaturationFactory
		implements
		InputProcessorFactory<IndexedClassExpression, ClassExpressionNoInputSaturationFactory.Engine> {

	private static final Logger LOGGER_ = LoggerFactory
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
	public void finish() {
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

		private Engine() {
		}

		@Override
		public void submit(IndexedClassExpression job) {
		}

		@Override
		public void process() throws InterruptedException {
			InputProcessor<IndexedClassExpression> engine = ruleAppFactory_
					.getEngine(ContextCreationListener.DUMMY,
							contextModificationListener_);

			try {
				engine.process();
			} finally {
				engine.finish();
			}
		}

		@Override
		public void finish() {
			ruleAppFactory_.dispose();
			// System.err.println(ruleAppFactory_.getStatistics().getRuleStatistics().getTotalRuleAppCount());
		}

	}
}
