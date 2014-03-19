package org.semanticweb.elk.reasoner.saturation.conclusions.implementation;

import org.semanticweb.elk.alc.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.alc.saturation.Root;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ExternalPossibleConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ExternalConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ExternalPossibleConclusionVisitor;

public abstract class AbstractPropagatedPossibleConclusion extends
		AbstractPropagatedConclusion implements ExternalPossibleConclusion {

	public AbstractPropagatedPossibleConclusion(IndexedObjectProperty relation,
			Root sourceRoot) {
		super(relation, sourceRoot);
	}

	@Override
	public <I, O> O accept(ExternalConclusionVisitor<I, O> visitor, I input) {
		return accept((ExternalPossibleConclusionVisitor<I, O>) visitor, input);
	}
}
