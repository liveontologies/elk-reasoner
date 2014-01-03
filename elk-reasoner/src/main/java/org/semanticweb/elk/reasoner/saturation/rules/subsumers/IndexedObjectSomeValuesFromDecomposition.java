package org.semanticweb.elk.reasoner.saturation.rules.subsumers;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;

/**
 * A {@link SubsumerDecompositionRule} that processes
 * {@link IndexedObjectSomeValuesFrom} and produces the corresponding
 * {@link BackwardLink}s in the context corresponding to its filler pointing to
 * the {@link Context} in which the {@link Conclusion} was processed using its
 * relation
 * 
 * @see IndexedObjectSomeValuesFrom#getFiller()
 * @see IndexedObjectSomeValuesFrom#getRelation()
 * 
 * @author "Yevgeny Kazakov"
 */
public class IndexedObjectSomeValuesFromDecomposition extends
		AbstractSubsumerDecompositionRule<IndexedObjectSomeValuesFrom> {

	public static final String NAME_ = "IndexedObjectSomeValuesFrom Decomposition";

	private static SubsumerDecompositionRule<IndexedObjectSomeValuesFrom> INSTANCE_ = new IndexedObjectSomeValuesFromDecomposition();

	public static SubsumerDecompositionRule<IndexedObjectSomeValuesFrom> getInstance() {
		return INSTANCE_;
	}

	@Override
	public String getName() {
		return NAME_;
	}

	@Override
	public void apply(IndexedObjectSomeValuesFrom premise, Context context,
			ConclusionProducer producer) {
		producer.produce(premise.getFiller(),
				new BackwardLink(context, premise.getRelation()));
	}

	@Override
	public void accept(SubsumerDecompositionRuleVisitor visitor,
			IndexedObjectSomeValuesFrom premise, Context context,
			ConclusionProducer producer) {
		visitor.visit(this, premise, context, producer);
	}

}
