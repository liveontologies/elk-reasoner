/**
 * 
 */
package org.semanticweb.elk.owlapi.proofs;

import org.semanticweb.elk.owl.interfaces.ElkObjectInverseOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.visitors.ElkObjectPropertyExpressionVisitor;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
class ObjectPropertyExpressionConverter implements
		ElkObjectPropertyExpressionVisitor<OWLObjectPropertyExpression> {

	private final OWLDataFactory factory_;
	
	ObjectPropertyExpressionConverter(OWLDataFactory f) {
		factory_ = f;
	}
	
	@Override
	public OWLObjectPropertyExpression visit(ElkObjectInverseOf ope) {
		return factory_.getOWLObjectInverseOf(ope.getObjectProperty().accept(this));
	}

	@Override
	public OWLObjectPropertyExpression visit(ElkObjectProperty ope) {
		return factory_.getOWLObjectProperty(IRI.create(ope.getIri().getFullIriAsString()));
	}

}
