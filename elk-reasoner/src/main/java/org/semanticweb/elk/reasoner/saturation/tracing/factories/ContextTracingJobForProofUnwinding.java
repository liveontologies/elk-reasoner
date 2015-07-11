package org.semanticweb.elk.reasoner.saturation.tracing.factories;

import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;

class ContextTracingJobForProofUnwinding<I extends Conclusion, J extends ProofUnwindingJob<I>>
		extends ContextTracingJob<IndexedContextRoot> {

	final Conclusion conclusionToDo;

	final ProofUnwindingState<I, J> unwindingState;

	ContextTracingJobForProofUnwinding(Conclusion conclusionToDo,
			ProofUnwindingState<I, J> unwindingState) {
		super(conclusionToDo.getOriginRoot());
		this.conclusionToDo = conclusionToDo;
		this.unwindingState = unwindingState;
	}

}
