/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.reasoner.ReasonerComputationWithInputs;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.tracing.factories.RecursiveContextTracingFactory;
import org.semanticweb.elk.reasoner.saturation.tracing.factories.RecursiveContextTracingJob;
import org.semanticweb.elk.util.collections.Multimap;
import org.semanticweb.elk.util.concurrent.computation.ComputationExecutor;

/**
 * A reasoner computation for recursive tracing. Uses
 * {@link RecursiveContextTracingFactory} to perform the computation.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class RecursiveTracingComputation extends
		ReasonerComputationWithInputs<RecursiveContextTracingJob, RecursiveContextTracingFactory> {

	public RecursiveTracingComputation(
			TraceState traceState,
			SaturationState<?> saturationState,
			ComputationExecutor executor, int maxWorkers,
			ProgressMonitor progressMonitor) {
		super(getInputJobs(traceState.getTracingMap()), new RecursiveContextTracingFactory(
				saturationState, traceState.getSaturationState(),
				traceState.getTraceStore(), maxWorkers), executor, maxWorkers,
				progressMonitor);
	}
	
	private static Collection<RecursiveContextTracingJob> getInputJobs(Multimap<IndexedClassExpression, Conclusion> toTrace) {
		Collection<RecursiveContextTracingJob> todo = new ToDoJobs(toTrace);
		
		/*for (RecursiveContextTracingJob job : todo) {
			System.err.println(job.getInput() + " : " + job.getTarget());
		}*/
		
		return todo;
	}

	public SaturationStatistics getStatistics() {
		return processorFactory.getStatistics();
	}

	/**
	 * Lazily transforms a mapping of roots to conclusions into a collection of
	 * tracing jobs.
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	private static class ToDoJobs extends AbstractCollection<RecursiveContextTracingJob> {

		private final Multimap<IndexedClassExpression, Conclusion> inputs_;

		ToDoJobs(Multimap<IndexedClassExpression, Conclusion> inputs) {
			inputs_ = inputs;
		}
		
		@Override
		public Iterator<RecursiveContextTracingJob> iterator() {
			return new Iterator<RecursiveContextTracingJob>() {
				
				private final Iterator<IndexedClassExpression> keyIterator_ = inputs_.keySet().iterator();
				
				private Iterator<Conclusion> currentValueIterator_;
				
				private IndexedClassExpression currentKey_;
				
				private RecursiveContextTracingJob nextJob_;

				@Override
				public boolean hasNext() {
					if (nextJob_ != null) {
						return true;
					}
					
					if (currentKey_ == null) {
						if (!nextKey()) {
							return false;
						}
					}
					
					for (;;) {
						if (currentValueIterator_ == null) {
							currentValueIterator_ = inputs_.get(currentKey_).iterator();
						}
						
						if (currentValueIterator_.hasNext()) {
							nextJob_ = new RecursiveContextTracingJob(currentKey_, currentValueIterator_.next());
							break;
						}
						
						currentValueIterator_ = null;
						
						if (!nextKey()) {
							break;
						}
					}
					
					return nextJob_ != null;
				}

				private boolean nextKey() {
					if (keyIterator_.hasNext()) {
						currentKey_ = keyIterator_.next();
						
						return true;
					}
					
					return false;
				}

				@Override
				public RecursiveContextTracingJob next() {
					if (!hasNext()) {
						throw new NoSuchElementException();
					}
					
					RecursiveContextTracingJob next = nextJob_;
					
					nextJob_ = null;
					
					return next;
				}

				@Override
				public void remove() {
					throw new UnsupportedOperationException();
				}
				
			};
		}

		@Override
		public int size() {
			return inputs_.keySet().size();
		}
		
	}
}
