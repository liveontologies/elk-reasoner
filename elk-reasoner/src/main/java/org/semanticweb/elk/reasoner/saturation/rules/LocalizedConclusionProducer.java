package org.semanticweb.elk.reasoner.saturation.rules;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionLocalizer;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.LocalizedConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * An {@link ConclusionProducer} that first localizes the input {@link Context}
 * and {@link Conclusion} before producing them. Localization means that all
 * {@link Context}s (including those occurring in the {@link Conclusion}s) are
 * converted to the corresponding {@link Context}s of the given
 * {@link SaturationState} (i.e., with the same root).
 * 
 * @see LocalizedConclusionVisitor
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class LocalizedConclusionProducer implements ConclusionProducer {

	private static ConclusionVisitor<SaturationState, Conclusion> CONCLUSION_LOCALIZER_ = new ConclusionLocalizer();

	private final ConclusionProducer producer_;

	private final SaturationState state_;

	public LocalizedConclusionProducer(ConclusionProducer producer,
			SaturationState state) {
		this.producer_ = producer;
		this.state_ = state;
	}

	@Override
	public void produce(Context context, Conclusion conclusion) {
		produce(context.getRoot(), conclusion);
	}

	@Override
	public void produce(IndexedClassExpression root, Conclusion conclusion) {
		producer_.produce(root,
		// TODO: what if local the conclusion is null?
				conclusion.accept(CONCLUSION_LOCALIZER_, state_));
	}

}