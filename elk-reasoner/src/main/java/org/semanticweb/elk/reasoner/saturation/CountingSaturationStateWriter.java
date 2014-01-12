package org.semanticweb.elk.reasoner.saturation;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionCounter;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.CountingConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;

/**
 * A {@link SaturationStateWriter} that mirrors all operations of the provided
 * internal {@link SaturationStateWriter} and additionally counts the number of
 * produced {@link Conclusion}s using a provided {@link ConclusionCounter}
 * 
 * @see ConclusionProducer#produce(Context, Conclusion)
 * @see ConclusionProducer#produce(IndexedClassExpression, Conclusion)
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <W>
 *            the type of the internal {@link SaturationStateWriter}
 */
public class CountingSaturationStateWriter<W extends SaturationStateWriter>
		extends SaturationStateWriterWrap<W> {

	private final ConclusionVisitor<Void, Integer> countingVisitor_;

	public CountingSaturationStateWriter(W writer, ConclusionCounter counter) {
		super(writer);
		this.countingVisitor_ = new CountingConclusionVisitor<Void>(counter);
	}

	@Override
	public void produce(Context context, Conclusion conclusion) {
		super.produce(context, conclusion);
		conclusion.accept(countingVisitor_, null);
	}

	@Override
	public void produce(IndexedClassExpression root, Conclusion conclusion) {
		mainWriter.produce(root, conclusion);
		conclusion.accept(countingVisitor_, null);
	}

}
