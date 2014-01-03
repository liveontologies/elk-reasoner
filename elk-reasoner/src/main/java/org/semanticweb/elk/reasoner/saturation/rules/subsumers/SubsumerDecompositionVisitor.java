package org.semanticweb.elk.reasoner.saturation.rules.subsumers;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDataHasValue;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectComplementOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectUnionOf;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;

/**
 * An {@link IndexedClassExpressionVisitor} applying decomposition rules using a
 * given {@link SubsumerDecompositionRuleVisitor} in a given {@link Context} and
 * producing conclusions using a given {@link ConclusionProducer}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class SubsumerDecompositionVisitor implements
		IndexedClassExpressionVisitor<Void> {

	/**
	 * the rule visitor used when applying decomposition rules
	 */
	private final SubsumerDecompositionRuleVisitor ruleVisitor_;

	/**
	 * the {@link Context} in which the rules are applied
	 */
	private final Context context_;

	/**
	 * the producer for conclusions
	 */
	private final ConclusionProducer producer_;

	public SubsumerDecompositionVisitor(
			SubsumerDecompositionRuleVisitor ruleVisitor, Context context,
			ConclusionProducer producer) {
		this.ruleVisitor_ = ruleVisitor;
		this.context_ = context;
		this.producer_ = producer;
	}

	@Override
	public Void visit(IndexedClass element) {
		// no rules are applicable
		return null;
	}

	@Override
	public Void visit(IndexedIndividual element) {
		// no rules are applicable
		return null;
	}

	@Override
	public Void visit(IndexedObjectComplementOf element) {
		IndexedObjectComplementOfDecomposition.getInstance().accept(
				ruleVisitor_, element, context_, producer_);
		return null;
	}

	@Override
	public Void visit(IndexedObjectIntersectionOf element) {
		IndexedObjectIntersectionOfDecomposition.getInstance().accept(
				ruleVisitor_, element, context_, producer_);
		return null;
	}

	@Override
	public Void visit(IndexedObjectSomeValuesFrom element) {
		IndexedObjectSomeValuesFromDecomposition.getInstance().accept(
				ruleVisitor_, element, context_, producer_);
		return null;
	}

	@Override
	public Void visit(IndexedObjectUnionOf element) {
		// not supported
		return null;
	}

	@Override
	public Void visit(IndexedDataHasValue element) {
		// TODO Auto-generated method stub
		return null;
	}

}
