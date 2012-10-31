/**
 * 
 */
package org.semanticweb.elk.reasoner.stages;

import java.util.Arrays;
import java.util.List;

import org.semanticweb.elk.reasoner.incremental.IncrementalStages;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturation;
import org.semanticweb.elk.reasoner.saturation.rules.ContextCleaningFactory;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationFactory;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class IncrementalContextCleaningStage extends AbstractReasonerStage {

	// logger for this class
	// private static final Logger LOGGER_ = Logger.getLogger(IncrementalDeSaturationStage.class);

	private ClassExpressionSaturation<IndexedClassExpression> cleaning_ = null;

	public IncrementalContextCleaningStage(AbstractReasonerState reasoner) {
		super(reasoner);
	}

	@Override
	public String getName() {
		return IncrementalStages.CONTEXT_CLEANING.toString();
	}

	@Override
	public boolean done() {
		return reasoner.incrementalState.getStageStatus(IncrementalStages.CONTEXT_CLEANING);
	}

	@Override
	public List<ReasonerStage> getDependencies() {
		return Arrays.asList((ReasonerStage) new IncrementalContextInitializationStage(reasoner, new IncrementalDeSaturationStage(reasoner)));
	}

	@Override
	public void execute() throws ElkInterruptedException {
		if (cleaning_ == null) {
			initComputation();
		}
		
		progressMonitor.start(getName());
		
		try {
			for (;;) {
				cleaning_.process();
				if (!interrupted())
					break;
			}
		} finally {
			progressMonitor.finish();
		}
		
		reasoner.incrementalState.setStageStatus(IncrementalStages.CONTEXT_CLEANING, true);
		// save cleaned contexts for future processing
		reasoner.incrementalState.classesToProcess = reasoner.saturationState.getModifiedContexts();
	}
	
	

	@Override
	void initComputation() {
		super.initComputation();

		RuleApplicationFactory cleaningFactory = new ContextCleaningFactory(reasoner.saturationState);
		
		reasoner.saturationState.reset();//reset the queue of modified contexts
		cleaning_ = new ClassExpressionSaturation<IndexedClassExpression>(
				reasoner.incrementalState.classesToProcess,
				reasoner.getProcessExecutor(),
				workerNo,
				reasoner.getProgressMonitor(),
				cleaningFactory);
	}

	@Override
	public void printInfo() {
		if (cleaning_ != null)
			cleaning_.printStatistics();
	}
}