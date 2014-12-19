package org.semanticweb.elk.owl.predefined;

import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.iris.ElkIri;
import org.semanticweb.elk.owl.visitors.ElkEntityVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectPropertyExpressionVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectPropertyVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;
import org.semanticweb.elk.owl.visitors.ElkSubObjectPropertyExpressionVisitor;

/**
 * Corresponds to <a href=
 * "http://www.w3.org/TR/owl2-syntax/#Object_Properties">built-in object
 * properties<a> in the OWL 2 specification, such as
 * {@code owl:topObjectProperty} and {@code owl:bottomObjectProperty} .
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public enum PredefinedElkObjectProperty implements ElkObjectProperty {

	OWL_TOP_OBJECT_PROPERTY(PredefinedElkIri.OWL_TOP_OBJECT_PROPERTY.get()),

	OWL_BOTTOM_OBJECT_PROPERTY(PredefinedElkIri.OWL_BOTTOM_OBJECT_PROPERTY
			.get())//
	;

	private final ElkIri iri_;

	private PredefinedElkObjectProperty(ElkIri iri) {
		this.iri_ = iri;
	}

	@Override
	public ElkIri getIri() {
		return iri_;
	}

	@Override
	public <O> O accept(ElkObjectPropertyExpressionVisitor<O> visitor) {
		return accept((ElkObjectPropertyVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkSubObjectPropertyExpressionVisitor<O> visitor) {
		return accept((ElkObjectPropertyVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return accept((ElkObjectPropertyVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkEntityVisitor<O> visitor) {
		return accept((ElkObjectPropertyVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkObjectPropertyVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
