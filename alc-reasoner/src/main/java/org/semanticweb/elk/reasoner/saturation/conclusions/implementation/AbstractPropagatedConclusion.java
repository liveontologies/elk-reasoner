package org.semanticweb.elk.reasoner.saturation.conclusions.implementation;

import org.semanticweb.elk.alc.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.alc.saturation.Root;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.PropagatedConclusion;

public abstract class AbstractPropagatedConclusion extends
		AbstractExternalDeterministicConclusion implements PropagatedConclusion {

	private final IndexedObjectProperty relation_;

	private final Root sourceRoot_;

	public AbstractPropagatedConclusion(IndexedObjectProperty relation,
			Root inconsistentRoot) {
		this.relation_ = relation;
		this.sourceRoot_ = inconsistentRoot;
	}

	@Override
	public IndexedObjectProperty getRelation() {
		return relation_;
	}

	@Override
	public Root getSourceRoot() {
		return sourceRoot_;
	}

}
