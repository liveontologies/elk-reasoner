package org.semanticweb.elk.reasoner.saturation.conclusions.implementation;

import org.semanticweb.elk.alc.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.PossiblePropagatedExistential;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ExternalPossibleConclusionVisitor;

public class PossiblePropagatedExistentialImpl extends
		AbstractExternalPossibleConclusion implements
		PossiblePropagatedExistential {

	private final IndexedObjectSomeValuesFrom existential_;

	public PossiblePropagatedExistentialImpl(
			IndexedObjectSomeValuesFrom existential) {
		this.existential_ = existential;
	}

	@Override
	public IndexedObjectSomeValuesFrom getExpression() {
		return existential_;
	}

	@Override
	public <I, O> O accept(ExternalPossibleConclusionVisitor<I, O> visitor,
			I input) {
		return visitor.visit(this, input);
	}

}
