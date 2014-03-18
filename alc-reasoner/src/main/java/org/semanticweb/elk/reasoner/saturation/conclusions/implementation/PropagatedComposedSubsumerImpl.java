package org.semanticweb.elk.reasoner.saturation.conclusions.implementation;

import org.semanticweb.elk.alc.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.alc.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.alc.saturation.Root;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.PropagatedComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ExternalDeterministicConclusionVisitor;

public class PropagatedComposedSubsumerImpl extends
		AbstractPropagatedConclusion implements PropagatedComposedSubsumer {

	private final IndexedClassExpression expression_;

	public PropagatedComposedSubsumerImpl(IndexedObjectProperty relation,
			Root sourceRoot, IndexedClassExpression subsumer) {
		super(relation, sourceRoot);
		this.expression_ = subsumer;
	}

	@Override
	public <I, O> O accept(
			ExternalDeterministicConclusionVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

	@Override
	public IndexedClassExpression getExpression() {
		return expression_;
	}

	@Override
	public String toString() {
		return PropagatedComposedSubsumer.NAME + "(" + getRelation() + ": "
				+ getSourceRoot() + ": " + getExpression() + ")";
	}

}
