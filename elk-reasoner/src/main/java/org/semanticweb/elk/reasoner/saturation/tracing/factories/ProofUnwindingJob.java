package org.semanticweb.elk.reasoner.saturation.tracing.factories;

import org.semanticweb.elk.reasoner.ReasonerJob;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;

public class ProofUnwindingJob<I extends Conclusion> extends
		ReasonerJob<I, Void> {

	public ProofUnwindingJob(I input) {
		super(input);
	}

	@Override
	public String toString() {
		return getInput().toString() + " [proof unwinding]";

	}

}
