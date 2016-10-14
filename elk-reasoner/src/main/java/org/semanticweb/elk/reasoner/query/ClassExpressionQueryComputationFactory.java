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
package org.semanticweb.elk.reasoner.query;

import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionFactory;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionJob;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionListener;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionOutputVisitor;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorFactory;
import org.semanticweb.elk.util.concurrent.computation.SimpleInterrupter;

/**
 * The factory for engines that concurrently saturate and transitively reduce
 * the class expressions supplied as input.
 * 
 * @author Peter Skocovsky
 */
public class ClassExpressionQueryComputationFactory extends SimpleInterrupter
		implements
		InputProcessorFactory<IndexedClassExpression, ClassExpressionQueryComputationFactory.Engine> {

	/**
	 * The transitive reduction shared structures used in the query computation.
	 */
	private final TransitiveReductionFactory<IndexedClassExpression, TransitiveReductionJob<IndexedClassExpression>> transitiveReductionShared_;
	/**
	 * This processor is notified about the output of transitive reduction.
	 */
	private final TransitiveReductionOutputVisitor<IndexedClassExpression> outputProcessor_;

	public ClassExpressionQueryComputationFactory(
			final SaturationState<?> saturationState, final int maxWorkers,
			final TransitiveReductionOutputVisitor<IndexedClassExpression> outputProcessor) {
		this.transitiveReductionShared_ = new TransitiveReductionFactory<IndexedClassExpression, TransitiveReductionJob<IndexedClassExpression>>(
				saturationState, maxWorkers,
				new ThisTransitiveReductionListener());
		this.outputProcessor_ = outputProcessor;
	}

	/**
	 * Listener notified of finished transitive reduction jobs that forwards the
	 * output to the output processor.
	 * 
	 * @author Peter Skocovsky
	 */
	private class ThisTransitiveReductionListener implements
			TransitiveReductionListener<TransitiveReductionJob<IndexedClassExpression>> {

		@Override
		public void notifyFinished(
				final TransitiveReductionJob<IndexedClassExpression> job)
						throws InterruptedException {
			job.getOutput().accept(outputProcessor_);
		}

	}

	@Override
	public Engine getEngine() {
		return new Engine();
	}

	@Override
	public synchronized void setInterrupt(boolean flag) {
		super.setInterrupt(flag);
		transitiveReductionShared_.setInterrupt(flag);
	}

	@Override
	public void finish() {
		transitiveReductionShared_.finish();
	}

	/**
	 * Print statistics about taxonomy construction
	 */
	public void printStatistics() {
		transitiveReductionShared_.printStatistics();
	}

	public SaturationStatistics getRuleAndConclusionStatistics() {
		return transitiveReductionShared_.getRuleAndConclusionStatistics();
	}

	public class Engine implements InputProcessor<IndexedClassExpression> {

		protected final TransitiveReductionFactory<IndexedClassExpression, TransitiveReductionJob<IndexedClassExpression>>.Engine transitiveReductionEngine = transitiveReductionShared_
				.getEngine();

		// don't allow creating of engines directly; only through the factory
		private Engine() {
		}

		@Override
		public final void submit(final IndexedClassExpression input) {
			transitiveReductionEngine.submit(
					new TransitiveReductionJob<IndexedClassExpression>(input));
		}

		@Override
		public final void process() throws InterruptedException {
			transitiveReductionEngine.process();
		}

		@Override
		public void finish() {
			transitiveReductionEngine.finish();
		}

	}

}
