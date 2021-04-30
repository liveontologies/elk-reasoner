package org.semanticweb.elk.reasoner.saturation.rules.subsumers;

import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDefinedClass;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectComplementOf;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectHasSelf;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedPredefinedClass;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ClassInferenceProducer;

/**
 * A visitor pattern for {@link SubsumerDecompositionRule}s
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <O>
 *            the type of output parameter with which this visitor works
 */
public interface SubsumerDecompositionRuleVisitor<O> {

	O visit(ComposedFromDecomposedSubsumerRule rule,
			IndexedClassExpression premise, ContextPremises premises,
			ClassInferenceProducer producer);
	
	O visit(IndexedClassDecompositionRule rule, IndexedDefinedClass premise,
			ContextPremises premises, ClassInferenceProducer producer);

	O visit(IndexedObjectComplementOfDecomposition rule,
			IndexedObjectComplementOf premise, ContextPremises premises,
			ClassInferenceProducer producer);

	O visit(IndexedObjectHasSelfDecomposition rule,
			IndexedObjectHasSelf premise, ContextPremises premises,
			ClassInferenceProducer producer);

	O visit(IndexedObjectIntersectionOfDecomposition rule,
			IndexedObjectIntersectionOf premise, ContextPremises premises,
			ClassInferenceProducer producer);

	O visit(IndexedObjectSomeValuesFromDecomposition rule,
			IndexedObjectSomeValuesFrom premise, ContextPremises premises,
			ClassInferenceProducer producer);

	O visit(OwlNothingDecompositionRule rule, IndexedPredefinedClass premise,
			ContextPremises premises, ClassInferenceProducer producer);
	
}
