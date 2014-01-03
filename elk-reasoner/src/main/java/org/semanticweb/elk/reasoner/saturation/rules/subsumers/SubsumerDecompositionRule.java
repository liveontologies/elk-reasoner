package org.semanticweb.elk.reasoner.saturation.rules.subsumers;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.Subsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;

/**
 * A decomposition rules for {@link Subsumer}s. The rule typically does not
 * depend on the other {@link Conclusion}s stored in the {@link Context}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <P>
 */
public interface SubsumerDecompositionRule<P extends IndexedClassExpression>
		extends SubsumerRule<P> {

	public void accept(SubsumerDecompositionRuleVisitor visitor, P premise,
			Context context, ConclusionProducer producer);

}
