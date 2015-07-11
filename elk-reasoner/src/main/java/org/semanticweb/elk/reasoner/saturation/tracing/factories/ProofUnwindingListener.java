package org.semanticweb.elk.reasoner.saturation.tracing.factories;

import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;

public interface ProofUnwindingListener<C extends Conclusion, J extends ProofUnwindingJob<C>> {

	public void notifyFinished(J job);

	public static class Helper {

		public static <C extends Conclusion, J extends ProofUnwindingJob<C>> ProofUnwindingListener<C, J> dummyListener() {
			return new ProofUnwindingListener<C, J>() {

				@Override
				public void notifyFinished(J job) {
					// no-op
				}
			};
		}
	}

}
