package org.semanticweb.elk.reasoner.saturation.conclusions.classes;

import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubContextInitialization;

/**
 * A skeleton for implementation of {@link SubClassConclusion.Visitor}s using a common
 * (default) method
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <I>
 *            the type of input parameter with which this visitor works
 * @param <O>
 *            the type of output parameter with which this visitor works
 */
public abstract class AbstractSubClassConclusionVisitor<I, O> implements
		SubClassConclusion.Visitor<I, O> {

	abstract O defaultVisit(SubClassConclusion subConclusion, I input);

	@Override
	public O visit(BackwardLink subConclusion, I input) {
		return defaultVisit(subConclusion, input);
	}

	@Override
	public O visit(Propagation subConclusion, I input) {
		return defaultVisit(subConclusion, input);
	}

	@Override
	public O visit(SubContextInitialization subConclusion, I input) {
		return defaultVisit(subConclusion, input);
	}

}
