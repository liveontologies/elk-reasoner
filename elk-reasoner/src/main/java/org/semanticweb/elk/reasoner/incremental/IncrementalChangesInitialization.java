/**
 * 
 */
package org.semanticweb.elk.reasoner.incremental;

import java.util.Collection;
import java.util.Map;

import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.reasoner.ReasonerComputation;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.IndexChange;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationFactory;
import org.semanticweb.elk.reasoner.saturation.rules.RuleEngine;
import org.semanticweb.elk.util.collections.LazySetIntersection;
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
			RuleApplicationFactory ruleAppFactory,
			ComputationExecutor executor,
			int maxWorkers,
			ProgressMonitor progressMonitor,
			InputProcessorListenerNotifyFinishedJob<IndexedClassExpression> listener) {
		super(null, new ContextInitializationFactory(ruleAppFactory, changes, listener), executor, maxWorkers, progressMonitor);
	}
}


class ContextInitializationFactory implements InputProcessorFactory<IndexedClassExpression, InputProcessor<IndexedClassExpression>> {

	private final RuleApplicationFactory ruleAppFactory_;
	private final Map<IndexedClassExpression, IndexChange> indexChanges_;
	private final InputProcessorListenerNotifyFinishedJob<IndexedClassExpression> listener_;
	
	public ContextInitializationFactory(RuleApplicationFactory ruleAppFactory,
			Map<IndexedClassExpression,
			IndexChange> indexChanges,
			InputProcessorListenerNotifyFinishedJob<IndexedClassExpression> listener) {
		ruleAppFactory_ = ruleAppFactory;
		indexChanges_ = indexChanges;
		listener_ = listener;
	}

	@Override
	public InputProcessor<IndexedClassExpression> getEngine() {

		final RuleEngine ruleEngine = ruleAppFactory_.getEngine();
		
		return new InputProcessor<IndexedClassExpression>() {
			//TODO Create a base input processor
			//which would only leave to implement how to process a single queue element  
			@Override
			public void submit(IndexedClassExpression ice) {
				Context context = ice.getContext();
				
				if (context != null) {

					for (IndexedClassExpression changedICE : new LazySetIntersection<IndexedClassExpression>(
							indexChanges_.keySet(),
							context.getSuperClassExpressions())) {
						IndexChange change = indexChanges_.get(changedICE);
						// must place into the queue
						ruleEngine.produce(context, change);
						//listener_.notifyFinished(ice); // can throw InterruptedException
					}
				}
			}

			@Override
			public void process() throws InterruptedException {}

			@Override
			public void finish() {}
		};
	}

	@Override
	public void finish() {}
	
}