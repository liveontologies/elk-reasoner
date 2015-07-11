package org.semanticweb.elk.reasoner.saturation.inferences;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.ForwardLinkImpl;
import org.semanticweb.elk.reasoner.saturation.inferences.visitors.ClassInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.inferences.visitors.ForwardLinkInferenceVisitor;

public abstract class AbstractForwardLinkInference<R extends IndexedPropertyChain>
		extends ForwardLinkImpl<R> implements ForwardLinkInference {

	public AbstractForwardLinkInference(IndexedContextRoot root, R relation,
			IndexedContextRoot target) {
		super(root, relation, target);
	}

	@Override
	public <I, O> O accept(ClassInferenceVisitor<I, O> visitor, I parameter) {
		return accept((ForwardLinkInferenceVisitor<I, O>) visitor, parameter);
	}

}
