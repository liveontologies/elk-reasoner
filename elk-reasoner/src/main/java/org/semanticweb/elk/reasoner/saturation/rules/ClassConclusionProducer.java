package org.semanticweb.elk.reasoner.saturation.rules;

import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;

/**
 * An object using which {@link ClassConclusion}s of inferences can be produced
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public interface ClassConclusionProducer {

	/**
	 * Tells that the given {@link ClassConclusion} is derived.
	 * 
	 * @param conclusion
	 */
	public void produce(ClassConclusion conclusion);

}
