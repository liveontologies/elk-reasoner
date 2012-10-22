/**
 * 
 */
package org.semanticweb.elk.reasoner.incremental;

import java.util.Collection;
import java.util.Map;

import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.reasoner.ReasonerComputation;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.IndexChange;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.util.collections.LazySetIntersection;
import org.semanticweb.elk.util.concurrent.computation.BaseInputProcessor;
import org.semanticweb.elk.util.concurrent.computation.ComputationExecutor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorFactory;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorListenerNotifyFinishedJob;

/**
 * Goes through the input class expressions and puts
 * each context's superclass for which there're changes into
 * the ToDo queue
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class IncrementalChangesInitialization
		extends
		ReasonerComputation<IndexedClassExpression, ContextInitializationFactory> {

	public IncrementalChangesInitialization(
			Collection<IndexedClassExpression> inputs,
			Map<IndexedClassExpression, IndexChange> changes,
			SaturationState state,
			ComputationExecutor executor,
			int maxWorkers,
			ProgressMonitor progressMonitor,
			InputProcessorListenerNotifyFinishedJob<IndexedClassExpression> listener) {
		super(null, new ContextInitializationFactory(state, changes, listener), executor, maxWorkers, progressMonitor);
	}
}


class ContextInitializationFactory implements InputProcessorFactory<IndexedClassExpression, InputProcessor<IndexedClassExpression>> {

	private final SaturationState saturationState_;
	private final Map<IndexedClassExpression, IndexChange> indexChanges_;
	private final InputProcessorListenerNotifyFinishedJob<IndexedClassExpression> listener_;
	
	public ContextInitializationFactory(SaturationState state,
			Map<IndexedClassExpression,
			IndexChange> indexChanges,
			InputProcessorListenerNotifyFinishedJob<IndexedClassExpression> listener) {
		saturationState_ = state;
		indexChanges_ = indexChanges;
		listener_ = listener;
	}

	@Override
	public InputProcessor<IndexedClassExpression> getEngine() {

		return new BaseInputProcessor<IndexedClassExpression>(listener_) {
			
			@Override
			protected void process(IndexedClassExpression ice) {
				Context context = ice.getContext();
				
				if (context != null) {

					for (IndexedClassExpression changedICE : new LazySetIntersection<IndexedClassExpression>(
							indexChanges_.keySet(),
							context.getSuperClassExpressions())) {
						IndexChange change = indexChanges_.get(changedICE);
						// place the accumulated changes into the queue
						saturationState_.produce(context, change);
					}
				}
			}
		};
	}

	@Override
	public void finish() {}
	
}