/**
 * 
 */
package org.semanticweb.elk.reasoner.stages;

import java.util.Arrays;
import java.util.List;

import org.semanticweb.elk.reasoner.incremental.ContextModificationListener;
import org.semanticweb.elk.reasoner.incremental.IncrementalStages;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturation;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationFactory;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class IncrementalReSaturationStage extends AbstractReasonerStage {

	// logger for this class
	// private static final Logger LOGGER_ = Logger.getLogger(IncrementalDeSaturationStage.class);

	private ClassExpressionSaturation<IndexedClassExpression> saturation_ = null;
	private final ContextModificationListener listener_ = new ContextModificationListener();

	public IncrementalReSaturationStage(AbstractReasonerState reasoner) {
		super(reasoner);
	}

	@Override
	public String getName() {
		return IncrementalStages.SATURATION.toString();
	}

	@Override
	public boolean done() {
		return reasoner.incrementalState.getStageStatus(IncrementalStages.SATURATION);
	}

	@Override
	public List<ReasonerStage> getDependencies() {
		// these two stages run in parallel and both modify the shares saturation state
		return Arrays.asList(
					(ReasonerStage) new IncrementalChangesInitializationStage(reasoner, false),
					(ReasonerStage) new IncrementalContextInitializationStage(reasoner));
	}

	@Override
	public void execute() throws ElkInterruptedException {
		if (saturation_ == null) {
			initComputation();
		}
		
		listener_.reset();
		progressMonitor.start(getName());
		
		try {
			for (;;) {
				saturation_.process();
				if (!interrupted())
					break;
			}
		} finally {
			progressMonitor.finish();
		}
		
		reasoner.incrementalState.setStageStatus(IncrementalStages.SATURATION, true);
	}
	
	

	@Override
	void initComputation() {
		super.initComputation();

		RuleApplicationFactory appFactory = new RuleApplicationFactory(reasoner.saturationState);
		
		saturation_ = new ClassExpressionSaturation<IndexedClassExpression>(
				reasoner.incrementalState.classesToProcess,
				reasoner.getProcessExecutor(),
				workerNo,
				reasoner.getProgressMonitor(),
				appFactory,
				listener_);
	}

	@Override
	public void printInfo() {
		if (saturation_ != null)
			saturation_.printStatistics();
	}
}