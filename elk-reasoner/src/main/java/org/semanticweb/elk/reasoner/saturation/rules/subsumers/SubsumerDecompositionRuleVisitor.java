package org.semanticweb.elk.reasoner.saturation.rules.subsumers;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectComplementOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;

/**
 * A visitor pattern for {@link SubsumerDecompositionRule}s
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public interface SubsumerDecompositionRuleVisitor {

	void visit(IndexedObjectComplementOfDecomposition rule,
			IndexedObjectComplementOf premise, Context context,
			ConclusionProducer producer);

	void visit(IndexedObjectIntersectionOfDecomposition rule,
			IndexedObjectIntersectionOf premise, Context context,
			ConclusionProducer producer);

	void visit(IndexedObjectSomeValuesFromDecomposition rule,
			IndexedObjectSomeValuesFrom premise, Context context,
			ConclusionProducer producer);

}
