package org.semanticweb.elk.reasoner.saturation.rules;

import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;

/**
 * A {@link ClassConclusionProducer} that combines two given
 * {@link ClassConclusionProducer}: all methods are executed first for the first
 * {@link ClassConclusionProducer} and then for the second.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class CombinedConclusionProducer implements ClassConclusionProducer {

	private final ClassConclusionProducer firstProducer_;

	private final ClassConclusionProducer secondProducer_;

	public CombinedConclusionProducer(ClassConclusionProducer firstProducer,
			ClassConclusionProducer secondProducer) {
		this.firstProducer_ = firstProducer;
		this.secondProducer_ = secondProducer;
	}

	@Override
	public void produce(ClassConclusion conclusion) {
		firstProducer_.produce(conclusion);
		secondProducer_.produce(conclusion);
	}

}
