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

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.reasoner.ReasonerComputation;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationFactory;
import org.semanticweb.elk.util.concurrent.computation.ComputationExecutor;

/**
 * A {@link ReasonerComputation} that computes saturation for the input
 * collection of {@link IndexedClassExpression}s
 * 
 * @author Yevgeny Kazakov
 * 
 */
public class ClassExpressionSaturation<I extends IndexedClassExpression>
		extends
		ReasonerComputation<SaturationJob<I>, ClassExpressionSaturationFactory<SaturationJob<I>>> {

	// logger for this class
	private static final Logger LOGGER_ = Logger
			.getLogger(ClassExpressionSaturation.class);

	/*
	 * Takes inputs and uses the default rule application factory and a dummy listener
	 */
	public ClassExpressionSaturation(Collection<I> inputs,
			ComputationExecutor executor,
			int maxWorkers,
			ProgressMonitor progressMonitor,
			OntologyIndex ontIndex) {
		this(inputs, executor, maxWorkers, progressMonitor, ontIndex, new DummyClassExpressionSaturationListener<SaturationJob<I>>());
	}	
	
	/*
	 * Takes inputs and uses the default rule application factory
	 */
	public ClassExpressionSaturation(Collection<I> inputs,
			ComputationExecutor executor,
			int maxWorkers,
			ProgressMonitor progressMonitor,
			OntologyIndex ontIndex,
			ClassExpressionSaturationListener<SaturationJob<I>> listener
			) {
		super(
				new TodoJobs<I>(inputs),
				new ClassExpressionSaturationFactory<SaturationJob<I>>(
						new SaturationState(ontIndex),
						maxWorkers,
						listener),
				executor, maxWorkers, progressMonitor);
	}	
	
	/*
	 * Takes inputs but uses the given rule application factory
	 */
	public ClassExpressionSaturation(Collection<I> inputs,
			ComputationExecutor executor,
			int maxWorkers,
			ProgressMonitor progressMonitor,
			RuleApplicationFactory ruleAppFactory
			) {
		super(
				new TodoJobs<I>(inputs),
				new ClassExpressionSaturationFactory<SaturationJob<I>>(
						ruleAppFactory,
						maxWorkers,
						new DummyClassExpressionSaturationListener<SaturationJob<I>>()),
				executor, maxWorkers, progressMonitor);
	}	
	
	/**
	 * Print statistics about the saturation computation
	 */
	public void printStatistics() {
		inputProcessorFactory.printStatistics();
	}
	
	public RuleAndConclusionStatistics getRuleAndConclusionStatistics() {
		return inputProcessorFactory.getRuleAndConclusionStatistics();
	}	

	/**
	 * Dynamic collection view for saturation checking jobs that correspond to
	 * the given input of {@link IndexedClassExpression}s.
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	private static class TodoJobs<I extends IndexedClassExpression> extends
			AbstractCollection<SaturationJob<I>>  {

		private final Collection<I> inputs;

		TodoJobs(Collection<I> inputs) {
			this.inputs = inputs;
		}

		@Override
		public int size() {
			return inputs.size();
		}

		@Override
		public Iterator<SaturationJob<I>> iterator() {
			return new Iterator<SaturationJob<I>>() {

				final Iterator<I> inputsIterator = inputs.iterator();

				@Override
				public boolean hasNext() {
					return inputsIterator.hasNext();
				}

				@Override
				public SaturationJob<I> next() {
					SaturationJob<I> job = new SaturationJob<I>(
							inputsIterator.next());
					if (LOGGER_.isTraceEnabled())
						LOGGER_.trace(job.getInput() + ": saturation submitted");
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