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

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.reasoner.ReasonerComputationWithInputs;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationFactory;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationInput;
import org.semanticweb.elk.util.concurrent.computation.ConcurrentExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ReasonerComputationWithInputs} that computes saturation for the
 * input collection of {@link IndexedContextRoot}s
 * 
 * @author Yevgeny Kazakov
 * @param <I>
 *                the types of {@link IndexedContextRoot}s managed by this
 *                computation
 * 
 */
public class ClassExpressionSaturation<I extends IndexedContextRoot>
		extends
		ReasonerComputationWithInputs<SaturationJob<I>, ClassExpressionSaturationFactory<SaturationJob<I>>> {

	// logger for this class
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ClassExpressionSaturation.class);

	/*
	 * Takes inputs and uses the default rule application factory and a dummy
	 * listener
	 */
	public ClassExpressionSaturation(Collection<? extends I> inputs,
			ConcurrentExecutor executor, int maxWorkers,
			ProgressMonitor progressMonitor,
			RuleApplicationFactory<?, RuleApplicationInput> ruleAppFactory) {
		this(inputs, executor, maxWorkers, progressMonitor, ruleAppFactory,
				new DummyClassExpressionSaturationListener<SaturationJob<I>>());
	}

	/*
	 * Takes inputs and uses the default rule application factory
	 */
	public ClassExpressionSaturation(Collection<? extends I> inputs,
			ConcurrentExecutor executor, int maxWorkers,
			ProgressMonitor progressMonitor,
			RuleApplicationFactory<?, RuleApplicationInput> ruleAppFactory,
			ClassExpressionSaturationListener<SaturationJob<I>> listener) {
		super(new TodoJobs<I>(inputs),
				new ClassExpressionSaturationFactory<SaturationJob<I>>(
						ruleAppFactory, maxWorkers, listener), executor,
				maxWorkers, progressMonitor);
	}

	/**
	 * Print statistics about the saturation computation
	 */
	public void printStatistics() {
		processorFactory.printStatistics();
	}

	public SaturationStatistics getRuleAndConclusionStatistics() {
		return processorFactory.getRuleAndConclusionStatistics();
	}

	/**
	 * Dynamic collection view for saturation checking jobs that correspond to
	 * the given input of {@link IndexedContextRoot}s.
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	private static class TodoJobs<I extends IndexedContextRoot> extends
			AbstractCollection<SaturationJob<I>> {

		private final Collection<? extends I> inputs;

		TodoJobs(Collection<? extends I> inputs) {
			this.inputs = inputs;
		}

		@Override
		public int size() {
			return inputs.size();
		}

		@Override
		public Iterator<SaturationJob<I>> iterator() {
			return new Iterator<SaturationJob<I>>() {

				final Iterator<? extends I> inputsIterator = inputs.iterator();

				@Override
				public boolean hasNext() {
					return inputsIterator.hasNext();
				}

				@Override
				public SaturationJob<I> next() {
					SaturationJob<I> job = new SaturationJob<I>(
							inputsIterator.next());
					LOGGER_.trace("{}: saturation submitted", job.getInput());
					return job;
				}

				@Override
				public void remove() {
					inputsIterator.remove();
				}
			};
		}
	}
}
