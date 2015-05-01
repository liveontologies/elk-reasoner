package org.semanticweb.elk.reasoner.saturation.tracing.inferences;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ClassInferenceVisitor;

/**
 * A {@link BackwardLink} that is obtained by reversing a given
 * {@link ForwardLink} and taking its super-property.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class SuperReversedForwardLink extends ReversedForwardLink {

	/**
	 * The {@link ElkAxiom} responsible for relation to be a super-property of
	 * the role chain from the forward link
	 */
	private final ElkAxiom reason_;

	public SuperReversedForwardLink(IndexedClassExpression source,
			IndexedObjectProperty relation, ForwardLink forwardLink,
			ElkAxiom reason) {
		super(source, relation, forwardLink);
		this.reason_ = reason;
	}

	public ElkAxiom getReason() {
		return this.reason_;
	}

	@Override
	public <I, O> O acceptTraced(ClassInferenceVisitor<I, O> visitor,
			I parameter) {
		return visitor.visit(this, parameter);
	}

}
