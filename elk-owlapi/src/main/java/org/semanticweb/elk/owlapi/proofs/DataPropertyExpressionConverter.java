/**
 * 
 */
package org.semanticweb.elk.owlapi.proofs;

import org.semanticweb.elk.owl.interfaces.ElkDataProperty;
import org.semanticweb.elk.owl.visitors.ElkDataPropertyExpressionVisitor;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
class DataPropertyExpressionConverter implements
		ElkDataPropertyExpressionVisitor<OWLDataPropertyExpression> {

	private final OWLDataFactory factory_;
	
	
	DataPropertyExpressionConverter(OWLDataFactory f) {
		factory_ = f;
	}
	
	@Override
	public OWLDataPropertyExpression visit(ElkDataProperty elkDataProperty) {
		return factory_.getOWLDataProperty(IRI.create(elkDataProperty.getIri().getFullIriAsString()));
	}

}
