package org.semanticweb.elk.reasoner.saturation.conclusions.classes;

import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.DecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubContextInitialization;

/**
 * A {@link ClassConclusion.Visitor} that increments the corresponding counters of the
 * given {@link ClassConclusionCounter} when visiting {@link ClassConclusion}s
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <I>
 */
public class CountingClassConclusionVisitor<I> implements
		ClassConclusion.Visitor<I, Boolean> {

	private final ClassConclusionCounter counter_;

	public CountingClassConclusionVisitor(ClassConclusionCounter counter) {
		this.counter_ = counter;
	}

	@Override
	public Boolean visit(BackwardLink subConclusion, I input) {
		counter_.countBackwardLinks++;
		return true;
	}

	@Override
	public Boolean visit(ComposedSubsumer conclusion, I input) {
		counter_.countComposedSubsumers++;
		return true;
	}

	@Override
	public Boolean visit(ContextInitialization conclusion, I input) {
		counter_.countContextInitializations++;
		return true;
	}

	@Override
	public Boolean visit(Contradiction conclusion, I input) {
		counter_.countContradictions++;
		return true;
	}

	@Override
	public Boolean visit(DecomposedSubsumer conclusion, I input) {
		counter_.countDecomposedSubsumers++;
		return true;
	}

	@Override
	public Boolean visit(DisjointSubsumer conclusion, I input) {
		counter_.countDisjointSubsumers++;
		return true;
	}

	@Override
	public Boolean visit(ForwardLink conclusion, I input) {
		counter_.countForwardLinks++;
		return true;
	}

	@Override
	public Boolean visit(Propagation subConclusion, I input) {
		counter_.countPropagations++;
		return true;
	}

	@Override
	public Boolean visit(SubContextInitialization subConclusion, I input) {
		counter_.countSubContextInitializations++;
		return true;
	}

}
