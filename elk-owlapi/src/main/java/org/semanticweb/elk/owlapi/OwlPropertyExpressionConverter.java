/**
 * @author Yevgeny Kazakov, Jul 1, 2011
 */
package org.semanticweb.elk.owlapi;

import org.semanticweb.elk.syntax.ElkObjectProperty;
import org.semanticweb.elk.syntax.ElkObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpressionVisitorEx;

/**
 * Conversion of OWL object properties to Elk object properties.
 * 
 * @author Yevgeny Kazakov
 * 
 */
public class OwlPropertyExpressionConverter implements
		OWLPropertyExpressionVisitorEx<ElkObjectPropertyExpression> {

	private static final OwlPropertyExpressionConverter converter_ = new OwlPropertyExpressionConverter();

	private OwlPropertyExpressionConverter() {
	}

	static OwlPropertyExpressionConverter getInstance() {
		return converter_;
	}

	public ElkObjectProperty visit(OWLObjectProperty property) {
		if (property.isOWLTopObjectProperty())
			return ElkObjectProperty.ELK_OWL_TOP_OBJECT_PROPERTY;
		else if (property.isOWLBottomObjectProperty())
			return ElkObjectProperty.ELK_OWL_BOTTOM_OBJECT_PROPERTY;
		else
			return ElkObjectProperty.create(property.getIRI().toString());
	}

	public ElkObjectPropertyExpression visit(OWLObjectInverseOf property) {
		// TODO Support this constructor
		throw new ConverterException("OWLObjectInverseOf not supported");
	}

	public ElkObjectPropertyExpression visit(OWLDataProperty property) {
		// TODO Auto-generated method stub
		throw new ConverterException(property.getEntityType().getName()
				+ " not supported");
	}

}
