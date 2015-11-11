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
 * A {@link ClassConclusion.Visitor} that runs a special preprocessor
 * {@link ClassConclusion.Visitor} before every call of the provided
 * {@link ClassConclusion.Visitor}. The returned result is taken from the letter.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <I>
 *            the type of input parameter with which this visitor works
 * @param <O>
 *            the type of output parameter with which this visitor works
 */
public class PreprocessedConclusionVisitor<I, O> implements
		ClassConclusion.Visitor<I, O> {

	/**
	 * a {@link ClassConclusion.Visitor} that is called first
	 */
	final private ClassConclusion.Visitor<I, ?> preprocessor_;
	/**
	 * a {@link ClassConclusion.Visitor} that is called next and returns the output
	 */
	final private ClassConclusion.Visitor<? super I, O> visitor_;

	public PreprocessedConclusionVisitor(ClassConclusion.Visitor<I, ?> preprocessor,
			ClassConclusion.Visitor<? super I, O> visitor) {
		this.preprocessor_ = preprocessor;
		this.visitor_ = visitor;
	}

	@Override
	public O visit(BackwardLink subConclusion, I input) {
		preprocessor_.visit(subConclusion, input);
		return visitor_.visit(subConclusion, input);
	}

	@Override
	public O visit(ComposedSubsumer conclusion, I input) {
		preprocessor_.visit(conclusion, input);
		return visitor_.visit(conclusion, input);
	}

	@Override
	public O visit(ContextInitialization conclusion, I input) {
		preprocessor_.visit(conclusion, input);
		return visitor_.visit(conclusion, input);
	}

	@Override
	public O visit(Contradiction conclusion, I input) {
		preprocessor_.visit(conclusion, input);
		return visitor_.visit(conclusion, input);
	}

	@Override
	public O visit(DecomposedSubsumer conclusion, I input) {
		preprocessor_.visit(conclusion, input);
		return visitor_.visit(conclusion, input);
	}

	@Override
	public O visit(DisjointSubsumer conclusion, I input) {
		preprocessor_.visit(conclusion, input);
		return visitor_.visit(conclusion, input);
	}

	@Override
	public O visit(ForwardLink conclusion, I input) {
		preprocessor_.visit(conclusion, input);
		return visitor_.visit(conclusion, input);
	}

	@Override
	public O visit(Propagation subConclusion, I input) {
		preprocessor_.visit(subConclusion, input);
		return visitor_.visit(subConclusion, input);
	}

	@Override
	public O visit(SubContextInitialization subConclusion, I input) {
		preprocessor_.visit(subConclusion, input);
		return visitor_.visit(subConclusion, input);
	}

}
