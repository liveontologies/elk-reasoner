package org.semanticweb.elk.reasoner.saturation.tracing.factories;

import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;

public interface ProofUnwindingListener<C extends ClassConclusion, J extends ProofUnwindingJob<C>> {

	public void notifyFinished(J job);

	public static class Helper {

		public static <C extends ClassConclusion, J extends ProofUnwindingJob<C>> ProofUnwindingListener<C, J> dummyListener() {
			return new ProofUnwindingListener<C, J>() {

				@Override
				public void notifyFinished(J job) {
					// no-op
				}
			};
		}
	}

}
