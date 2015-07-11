package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.saturation.inferences.properties.ObjectPropertyInference;

/**
 * An object using which {@link ObjectPropertyInference}s can be produced
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public interface ObjectPropertyInferenceProducer {

	/**
	 * Tells that the given {@link ObjectPropertyInference} is produced.
	 * 
	 * @param inference
	 */
	public void produce(ObjectPropertyInference inference);

	public static ObjectPropertyInferenceProducer DUMMY = new ObjectPropertyInferenceProducer() {
		@Override
		public void produce(ObjectPropertyInference inference) {
			// no-op
		}
	};

}
