/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import java.util.Collection;

import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.reasoner.ReasonerComputation;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.ExtendedSaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.util.concurrent.computation.ComputationExecutor;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class RecursiveContextTracing extends ReasonerComputation<IndexedClassExpression, RecursiveContextTracingFactory> {

	/**
	 * 
	 */
	public RecursiveContextTracing(Collection<IndexedClassExpression> inputs,
			ComputationExecutor executor, int maxWorkers,
			ProgressMonitor progressMonitor, ExtendedSaturationState saturationState, TraceState traceState) {
		super(inputs,
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
	
}
