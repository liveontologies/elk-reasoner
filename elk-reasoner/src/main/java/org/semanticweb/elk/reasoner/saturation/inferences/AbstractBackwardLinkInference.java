package org.semanticweb.elk.reasoner.saturation.inferences;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.BackwardLinkImpl;
import org.semanticweb.elk.reasoner.saturation.inferences.visitors.BackwardLinkInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.inferences.visitors.ClassInferenceVisitor;

public abstract class AbstractBackwardLinkInference extends BackwardLinkImpl
		implements BackwardLinkInference {

	public AbstractBackwardLinkInference(IndexedContextRoot root,
			IndexedObjectProperty relation, IndexedContextRoot source) {
		super(root, relation, source);
	}

	@Override
	public <I, O> O accept(ClassInferenceVisitor<I, O> visitor, I parameter) {
		return accept((BackwardLinkInferenceVisitor<I, O>) visitor, parameter);
	}

}
