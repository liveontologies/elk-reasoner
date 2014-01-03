package org.semanticweb.elk.reasoner.saturation.rules.subsumers;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectComplementOf;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;

/**
 * A {@link SubsumerDecompositionRule} producing {@link Contradiction} when
 * processing the {@link IndexedObjectComplementOf} which negation
 * {@link IndexedClassExpression} is contained in the {@code Context}
 * 
 * @see IndexedObjectComplementOf#getNegated()
 * @see ContradictionFromNegationRule
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class IndexedObjectComplementOfDecomposition extends
		AbstractSubsumerDecompositionRule<IndexedObjectComplementOf> {

	public static final String NAME_ = "IndexedObjectComplementOf Decomposition";

	private static IndexedObjectComplementOfDecomposition INSTANCE_ = new IndexedObjectComplementOfDecomposition();

	public static IndexedObjectComplementOfDecomposition getInstance() {
		return INSTANCE_;
	}

	@Override
	public String getName() {
		return NAME_;
	}

	@Override
	public void accept(SubsumerDecompositionRuleVisitor visitor,
			IndexedObjectComplementOf premise, Context context,
			ConclusionProducer producer) {
		visitor.visit(this, premise, context, producer);
	}

	@Override
	public void apply(IndexedObjectComplementOf premise, Context context,
			ConclusionProducer producer) {
		if (context.getSubsumers().contains(premise.getNegated()))
			producer.produce(context, Contradiction.getInstance());
	}
}
