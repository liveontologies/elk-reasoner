/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.reasoner.ReasonerComputation;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.ExtendedSaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.util.collections.Multimap;
import org.semanticweb.elk.util.concurrent.computation.ComputationExecutor;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class RecursiveContextTracing extends ReasonerComputation<TracingJob, RecursiveContextTracingFactory> {

	/**
	 * 
	 */
	public RecursiveContextTracing(Multimap<IndexedClassExpression, Conclusion> inputs,
			ComputationExecutor executor, int maxWorkers,
			ProgressMonitor progressMonitor, ExtendedSaturationState saturationState, TraceState traceState) {
		super(new TracingJobs(inputs),
				new RecursiveContextTracingFactory(saturationState, traceState, maxWorkers),
				executor, maxWorkers, progressMonitor);
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
	
	/**
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	private static class TracingJobs extends AbstractCollection<TracingJob> {

		private final Multimap<IndexedClassExpression, Conclusion> inputs_;
		
		private final Iterator<IndexedClassExpression> keyIterator_;
		
		private IndexedClassExpression currentKey_;
		
		private Iterator<Conclusion> valueIterator_;
		
		private TracingJob current_;
		
		TracingJobs(Multimap<IndexedClassExpression, Conclusion> inputs) {
			inputs_ = inputs;
			keyIterator_ = inputs.keySet().iterator();
		}
		
		@Override
		public Iterator<TracingJob> iterator() {
			return new Iterator<TracingJob>() {

				@Override
				public boolean hasNext() {
					if (current_ == null) {
						advance();	
					}
					
					return current_ != null;
				}

				private void advance() {
					for (;;) {
						if (currentKey_ == null) {
							if (keyIterator_.hasNext()) {
								currentKey_ = keyIterator_.next();
								valueIterator_ = inputs_.get(currentKey_).iterator();
							} else {
								//the end
								return;
							}
						}

						if (!valueIterator_.hasNext()) {
							//moving to the next key
							currentKey_ = null;
							valueIterator_ = null;
							continue;
						}
						
						Conclusion value = valueIterator_.next();
						
						current_ = new TracingJob(currentKey_, value);
						break;
					}
				}

				@Override
				public TracingJob next() {
					if (!hasNext()) {
						throw new NoSuchElementException();
					}
					
					TracingJob next = current_;
					
					current_ = null;
					
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
			//lower approximation
			return inputs_.keySet().size();
		}
		
	}
}
