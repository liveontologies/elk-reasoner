/**
 * 
 */
package org.semanticweb.elk.reasoner.stages;

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.reasoner.saturation.tracing.RecursiveTracingComputation;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceState;

/**
 * Executes {@link RecursiveTracingComputation} to trace inferences queued in {@link TraceState} 
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class InferenceTracingStage extends AbstractReasonerStage {

	private RecursiveTracingComputation tracing_;
	
	public InferenceTracingStage(AbstractReasonerState reasoner,
			AbstractReasonerStage... preStages) {
		super(reasoner, preStages);
	}

	@Override
	public String getName() {
		return "Inference tracing";
	}

	@Override
	public void printInfo() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public boolean preExecute() {
		if (!super.preExecute()) {
			return false;
		}
		
		tracing_ = new RecursiveTracingComputation(reasoner.traceState,
				reasoner.saturationState, reasoner.getProcessExecutor(),
				reasoner.getNumberOfWorkers(), reasoner.getProgressMonitor());
		
		return true;
	}


	@Override
	void executeStage() throws ElkException {
		for (;;) {
			tracing_.process();
			
			if (!spuriousInterrupt()) {
				break;
			}
		}
	}

}
