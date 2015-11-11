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
 * A {@link ClassConclusion.Visitor} that composes several given
 * {@link ClassConclusion.Visitor}s. The visit method of the composed visitor returns
 * calls the original {@link ClassConclusion.Visitor}s in the specified order and
 * returns {@link true} for the {@link ClassConclusion} if and only all of the
 * {@link ClassConclusion.Visitor}s return {@code true}. The result is evaluated
 * lazily, i.e., if some {@link ClassConclusion.Visitor} returns {@code false}, the
 * subsequent {@link ClassConclusion.Visitor}s are not called.
 * 
 * @author "Yevgeny Kazakov"
 */
public class ComposedClassConclusionVisitor<I> implements
		ClassConclusion.Visitor<I, Boolean> {

	/**
	 * The original {@link ClassConclusion.Visitor}s to be called in the specified
	 * order
	 */
	final private ClassConclusion.Visitor<? super I, Boolean>[] visitors_;

	/**
	 * Creates a new {@link ClassConclusion.Visitor} that combines several given
	 * {@link ClassConclusion.Visitor}s. The visit method of the combined visitor
	 * returns calls the original {@link ClassConclusion.Visitor}s in the specified
	 * order and returns {@link true} for the {@link ClassConclusion} if and only all
	 * of the {@link ClassConclusion.Visitor}s return {@code true}. The result is
	 * evaluated lazily, i.e., if some {@link ClassConclusion.Visitor} returns
	 * {@code false}, the subsequent {@link ClassConclusion.Visitor}s are not called.
	 * 
	 * @param visitors
	 *            the {@link ClassConclusion.Visitor} to be composed
	 */
	public ComposedClassConclusionVisitor(
			ClassConclusion.Visitor<? super I, Boolean>... visitors) {
		this.visitors_ = visitors;
	}

	@Override
	public Boolean visit(BackwardLink subConclusion, I input) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(subConclusion, input))
				return false;
		}
		return true;
	}

	@Override
	public Boolean visit(ComposedSubsumer conclusion, I input) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(conclusion, input))
				return false;
		}
		return true;
	}

	@Override
	public Boolean visit(ContextInitialization conclusion, I input) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(conclusion, input))
				return false;
		}
		return true;
	}

	@Override
	public Boolean visit(Contradiction conclusion, I input) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(conclusion, input))
				return false;
		}
		return true;
	}

	@Override
	public Boolean visit(DecomposedSubsumer conclusion, I input) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(conclusion, input))
				return false;
		}
		return true;
	}

	@Override
	public Boolean visit(DisjointSubsumer conclusion, I input) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(conclusion, input))
				return false;
		}
		return true;
	}

	@Override
	public Boolean visit(ForwardLink conclusion, I input) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(conclusion, input))
				return false;
		}
		return true;
	}

	@Override
	public Boolean visit(Propagation subConclusion, I input) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(subConclusion, input))
				return false;
		}
		return true;
	}

	@Override
	public Boolean visit(SubContextInitialization subConclusion, I input) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(subConclusion, input))
				return false;
		}
		return true;
	}

}
