package org.semanticweb.elk.reasoner.saturation.inferences;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.ComposedSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.inferences.visitors.ClassInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.inferences.visitors.SubsumerInferenceVisitor;

public abstract class AbstractComposedSubsumerInference<S extends IndexedClassExpression>
		extends ComposedSubsumerImpl<S> implements SubsumerInference<S> {

	public AbstractComposedSubsumerInference(IndexedContextRoot root, S subsumer) {
		super(root, subsumer);
	}

	@Override
	public <I, O> O accept(ClassInferenceVisitor<I, O> visitor, I parameter) {
		return accept((SubsumerInferenceVisitor<I, O>) visitor, parameter);
	}

}
