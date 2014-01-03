package org.semanticweb.elk.reasoner.saturation.conclusions.visitors;

import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.DecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * A skeleton for implementation of {@link ConclusionVisitor}s using a common
 * (default) methods
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <R>
 */
public abstract class AbstractConclusionVisitor<R> implements
		ConclusionVisitor<R> {

	abstract R defaultVisit(Conclusion conclusion, Context context);

	@Override
	public R visit(BackwardLink link, Context context) {
		return defaultVisit(link, context);
	}

	@Override
	public R visit(ComposedSubsumer cSub, Context context) {
		return defaultVisit(cSub, context);
	}

	@Override
	public R visit(Contradiction bot, Context context) {
		return defaultVisit(bot, context);
	}

	@Override
	public R visit(DecomposedSubsumer dSub, Context context) {
		return defaultVisit(dSub, context);
	}

	@Override
	public R visit(DisjointSubsumer disjoint, Context context) {
		return defaultVisit(disjoint, context);
	}

	@Override
	public R visit(ForwardLink link, Context context) {
		return defaultVisit(link, context);
	}

	@Override
	public R visit(Propagation propagation, Context context) {
		return defaultVisit(propagation, context);
	}

}
