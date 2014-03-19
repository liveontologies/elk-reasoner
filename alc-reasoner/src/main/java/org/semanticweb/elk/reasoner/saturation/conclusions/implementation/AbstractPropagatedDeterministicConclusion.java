package org.semanticweb.elk.reasoner.saturation.conclusions.implementation;

import org.semanticweb.elk.alc.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.alc.saturation.Root;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ExternalDeterministicConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ExternalConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ExternalDeterministicConclusionVisitor;

public abstract class AbstractPropagatedDeterministicConclusion extends
		AbstractPropagatedConclusion implements ExternalDeterministicConclusion {

	public AbstractPropagatedDeterministicConclusion(
			IndexedObjectProperty relation, Root sourceRoot) {
		super(relation, sourceRoot);
	}

	@Override
	public <I, O> O accept(ExternalConclusionVisitor<I, O> visitor, I input) {
		return accept((ExternalDeterministicConclusionVisitor<I, O>) visitor,
				input);
	}
}
