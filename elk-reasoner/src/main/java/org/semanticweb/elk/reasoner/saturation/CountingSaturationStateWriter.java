package org.semanticweb.elk.reasoner.saturation;

import org.semanticweb.elk.reasoner.saturation.conclusions.classes.ClassConclusionCounter;
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.CountingClassConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ClassConclusionProducer;

/**
 * A {@link SaturationStateWriter} that mirrors all operations of the provided
 * internal {@link SaturationStateWriter} and additionally counts the number of
 * produced {@link ClassConclusion}s using a provided {@link ClassConclusionCounter}
 * 
 * @see ClassConclusionProducer#produce(ClassConclusion)
 * 
 * @author "Yevgeny Kazakov"
 */
public class CountingSaturationStateWriter<C extends Context> extends
		SaturationStateWriterWrap<C> {

	private final ClassConclusion.Visitor<Void, ?> countingVisitor_;

	public CountingSaturationStateWriter(SaturationStateWriter<C> writer,
			ClassConclusionCounter counter) {
		super(writer);
		this.countingVisitor_ = new CountingClassConclusionVisitor<Void>(counter);
	}

	@Override
	public void produce(ClassConclusion conclusion) {
		mainWriter.produce(conclusion);
		conclusion.accept(countingVisitor_, null);
	}

}
