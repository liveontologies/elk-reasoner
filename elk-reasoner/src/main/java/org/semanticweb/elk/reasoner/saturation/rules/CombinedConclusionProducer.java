package org.semanticweb.elk.reasoner.saturation.rules;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * A {@link ConclusionProducer} that combines two given
 * {@link ConclusionProducer}: all methods are executed first for the first
 * {@link ConclusionProducer} and then for the second.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class CombinedConclusionProducer implements ConclusionProducer {

	private final ConclusionProducer firstProducer_;

	private final ConclusionProducer secondProducer_;

	public CombinedConclusionProducer(ConclusionProducer firstProducer,
			ConclusionProducer secondProducer) {
		this.firstProducer_ = firstProducer;
		this.secondProducer_ = secondProducer;
	}

	@Override
	public void produce(Context context, Conclusion conclusion) {
		firstProducer_.produce(context, conclusion);
		secondProducer_.produce(context, conclusion);
	}

	@Override
	public void produce(IndexedClassExpression root, Conclusion conclusion) {
		firstProducer_.produce(root, conclusion);
		secondProducer_.produce(root, conclusion);
	}

}
