package org.semanticweb.elk.reasoner.saturation.rules.subsumers;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.saturation.conclusions.DecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.Subsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;

/**
 * A {@link SubsumerDecompositionRule} that processes an
 * {@link IndexedObjectIntersectionOf} and produces {@link Subsumer}s for its
 * conjuncts
 * 
 * @see IndexedObjectIntersectionOf#getFirstConjunct()
 * @see IndexedObjectIntersectionOf#getSecondConjunct()
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class IndexedObjectIntersectionOfDecomposition extends
		AbstractSubsumerDecompositionRule<IndexedObjectIntersectionOf> {

	public static final String NAME_ = "ObjectIntersectionOf Decomposition";

	private static IndexedObjectIntersectionOfDecomposition INSTANCE_ = new IndexedObjectIntersectionOfDecomposition();

	public static IndexedObjectIntersectionOfDecomposition getInstance() {
		return INSTANCE_;
	}

	@Override
	public String getName() {
		return NAME_;
	}

	@Override
	public void apply(IndexedObjectIntersectionOf premise, Context context,
			ConclusionProducer producer) {
		producer.produce(context,
				new DecomposedSubsumer(premise.getFirstConjunct()));
		producer.produce(context,
				new DecomposedSubsumer(premise.getSecondConjunct()));
	}

	@Override
	public void accept(SubsumerDecompositionRuleVisitor visitor,
			IndexedObjectIntersectionOf premise, Context context,
			ConclusionProducer producer) {
		visitor.visit(this, premise, context, producer);

	}

}
