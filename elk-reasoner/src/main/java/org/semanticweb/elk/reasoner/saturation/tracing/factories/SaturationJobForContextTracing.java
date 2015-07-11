package org.semanticweb.elk.reasoner.saturation.tracing.factories;

import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.SaturationJob;

class SaturationJobForContextTracing<I extends IndexedContextRoot, J extends ContextTracingJob<I>>
		extends SaturationJob<I> {

	private final J initiatorJob_;

	public SaturationJobForContextTracing(J initiatorJob) {
		super(initiatorJob.getInput());
		this.initiatorJob_ = initiatorJob;
	}

	J getInitiatorJob() {
		return this.initiatorJob_;
	}

}
