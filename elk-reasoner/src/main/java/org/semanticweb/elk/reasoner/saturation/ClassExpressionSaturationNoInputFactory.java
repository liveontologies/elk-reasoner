package org.semanticweb.elk.reasoner.saturation;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationFactory;
import org.semanticweb.elk.util.concurrent.computation.Processor;
import org.semanticweb.elk.util.concurrent.computation.ProcessorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A factory of engines that use a provided {@link RuleApplicationFactory} to
 * concurrently compute the closure of the current {@link SaturationState} under
 * the rules (based on which {@link ClassConclusion}s are currently unprocessed).
 * This factory cannot supply any further input jobs. Unlike
 * {@link ClassExpressionSaturationFactory}, it does not mark any contexts as
 * saturated.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class ClassExpressionSaturationNoInputFactory implements
		ProcessorFactory<ClassExpressionSaturationNoInputFactory.Engine> {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ClassExpressionSaturationNoInputFactory.class);

	private final RuleApplicationFactory<?, ?> ruleAppFactory_;

	private final ContextModificationListener contextModificationListener_;

	public ClassExpressionSaturationNoInputFactory(
			final RuleApplicationFactory<?, ?> ruleAppFactory,
			final ContextModificationListener contextModificationListener) {
		ruleAppFactory_ = ruleAppFactory;
		contextModificationListener_ = contextModificationListener;
	}
	
	public ClassExpressionSaturationNoInputFactory(
			final RuleApplicationFactory<?, ?> ruleAppFactory) {
		this(ruleAppFactory, ContextModificationListener.DUMMY);
	}

	@Override
	public Engine getEngine() {
		return new Engine();
	}
	
	public Engine getEngine(ContextModificationListener listener) {
		return new Engine(listener);
	}

	@Override
	public boolean isInterrupted() {
		return ruleAppFactory_.isInterrupted();
	}

	@Override
	public void finish() {
		ruleAppFactory_.dispose();
	}
	
	protected RuleApplicationFactory<?, ?> getRuleApplicationFactory() {
		return ruleAppFactory_;
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

	public class Engine implements Processor {

		private final Processor engine_;

		private Engine() {
			this(contextModificationListener_);
		}
		
		private Engine(ContextModificationListener listener) {
			engine_ =  ruleAppFactory_.getEngine(
					ContextCreationListener.DUMMY, listener);
		}

		@Override
		public void process() throws InterruptedException {
			engine_.process();
		}

		@Override
		public void finish() {
			engine_.finish();
			// System.err.println(ruleAppFactory_.getStatistics().getRuleStatistics().getTotalRuleAppCount());
		}

	}
}
