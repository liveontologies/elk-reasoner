package org.semanticweb.elk.reasoner.saturation.inferences;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.PropagationImpl;
import org.semanticweb.elk.reasoner.saturation.inferences.visitors.ClassInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.inferences.visitors.PropagationInferenceVisitor;

public abstract class AbstractPropagationInference extends PropagationImpl
		implements PropagationInference {

	public AbstractPropagationInference(IndexedContextRoot root,
			IndexedObjectProperty relation, IndexedObjectSomeValuesFrom carry) {
		super(root, relation, carry);
	}

	@Override
	public <I, O> O accept(ClassInferenceVisitor<I, O> visitor, I parameter) {
		return accept((PropagationInferenceVisitor<I, O>) visitor, parameter);
	}

}
