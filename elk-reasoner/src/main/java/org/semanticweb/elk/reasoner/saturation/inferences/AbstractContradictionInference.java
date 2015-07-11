package org.semanticweb.elk.reasoner.saturation.inferences;

import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.ContradictionImpl;
import org.semanticweb.elk.reasoner.saturation.inferences.visitors.ClassInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.inferences.visitors.ContradictionInferenceVisitor;

public abstract class AbstractContradictionInference extends ContradictionImpl
		implements ContradictionInference {

	protected AbstractContradictionInference(IndexedContextRoot root) {
		super(root);
	}

	@Override
	public <I, O> O accept(ClassInferenceVisitor<I, O> visitor, I parameter) {
		return accept((ContradictionInferenceVisitor<I, O>) visitor, parameter);
	}

}
