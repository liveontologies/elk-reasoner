package org.semanticweb.elk.reasoner.saturation.conclusions.visitors;

import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.DecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;

/**
 * A skeleton for implementation of {@link ConclusionVisitor}s using a common
 * (default) method
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <I>
 *            the type of input parameter with which this visitor works
 * @param <O>
 *            the type of output parameter with which this visitor works
 */
public abstract class AbstractConclusionVisitor<I, O> implements
		ConclusionVisitor<I, O> {

	abstract O defaultVisit(Conclusion conclusion, I input);

	@Override
	public O visit(BackwardLink conclusion, I input) {
		return defaultVisit(conclusion, input);
	}

	@Override
	public O visit(ComposedSubsumer conclusion, I input) {
		return defaultVisit(conclusion, input);
	}

	@Override
	public O visit(ContextInitialization conclusion, I input) {
		return defaultVisit(conclusion, input);
	}

	@Override
	public O visit(Contradiction conclusion, I input) {
		return defaultVisit(conclusion, input);
	}

	@Override
	public O visit(DecomposedSubsumer conclusion, I input) {
		return defaultVisit(conclusion, input);
	}

	@Override
	public O visit(DisjointSubsumer conclusion, I input) {
		return defaultVisit(conclusion, input);
	}

	@Override
	public O visit(ForwardLink conclusion, I input) {
		return defaultVisit(conclusion, input);
	}

	@Override
	public O visit(Propagation conclusion, I input) {
		return defaultVisit(conclusion, input);
	}

}
