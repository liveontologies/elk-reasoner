package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference;

/**
 * An object using which {@link ClassInference}s can be produced
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public interface ClassInferenceProducer {

	/**
	 * Tells that the given {@link ClassInference} is produced.
	 * 
	 * @param inference
	 */
	public void produce(ClassInference inference);

}
