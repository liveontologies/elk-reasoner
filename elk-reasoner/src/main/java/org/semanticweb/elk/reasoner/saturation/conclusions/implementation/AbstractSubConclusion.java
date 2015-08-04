package org.semanticweb.elk.reasoner.saturation.conclusions.implementation;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.SubConclusion;

/**
 * A skeleton for implementation of {@link SubConclusion}s.
 * 
 * @author "Yevgeny Kazakov"
 *
 */
public abstract class AbstractSubConclusion extends AbstractConclusion
		implements SubConclusion {

	private final IndexedObjectProperty subRoot_;

	protected AbstractSubConclusion(IndexedContextRoot root,
			IndexedObjectProperty subRoot) {
		super(root);
		this.subRoot_ = subRoot;
	}

	@Override
	public IndexedObjectProperty getConclusionSubRoot() {
		return this.subRoot_;
	}

	@Override
	public IndexedObjectProperty getOriginSubRoot() {
		return this.subRoot_;
	}

}
