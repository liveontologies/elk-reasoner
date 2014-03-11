package org.semanticweb.elk.reasoner.saturation.conclusions.implementation;

import org.semanticweb.elk.alc.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.alc.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.alc.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;

public class ForwardLinkImpl extends AbstractConclusion implements ForwardLink {

	private final IndexedObjectSomeValuesFrom existential_;

	public ForwardLinkImpl(IndexedObjectSomeValuesFrom existential) {
		this.existential_ = existential;
	}

	@Override
	public IndexedObjectProperty getRelation() {
		return existential_.getRelation();
	}

	@Override
	public IndexedClassExpression getTarget() {
		return existential_.getFiller();
	}

	@Override
	public <I, O> O accept(ConclusionVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

}
