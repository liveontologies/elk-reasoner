/**
 * @author Yevgeny Kazakov, Jul 4, 2011
 */
package org.semanticweb.elk.owlapi;

import org.semanticweb.elk.syntax.ElkEntity;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntityVisitorEx;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

/**
 * @author Yevgeny Kazakov
 *
 */
public class OwlEntityConverter implements OWLEntityVisitorEx<ElkEntity> {
	
	private static final OwlEntityConverter converter_ = new OwlEntityConverter();

	private OwlEntityConverter() {
	}

	static OwlEntityConverter getInstance() {
		return converter_;
	}
	
	public ElkEntity visit(OWLClass cls) {
		return OwlClassExpressionConverter.getInstance().visit(cls);
	}

	public ElkEntity visit(OWLObjectProperty property) {
		return OwlPropertyExpressionConverter.getInstance().visit(property);
	}

	public ElkEntity visit(OWLDataProperty property) {
		// TODO Support this constructor
		return null;
	}
	
	public ElkEntity visit(OWLNamedIndividual individual) {
		// TODO Support this constructor
		return null;
	}
	
	public ElkEntity visit(OWLDatatype datatype) {
		// TODO Support this constructor
		return null;
	}
	
	public ElkEntity visit(OWLAnnotationProperty property) {
		// TODO Support this constructor
		return null;
	}

}
