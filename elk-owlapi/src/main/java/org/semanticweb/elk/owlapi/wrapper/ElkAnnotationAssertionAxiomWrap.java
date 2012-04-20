/**
 * 
 */
package org.semanticweb.elk.owlapi.wrapper;

import org.semanticweb.elk.owl.interfaces.ElkAnnotationAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationProperty;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationSubject;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationValue;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 *
 */
public class ElkAnnotationAssertionAxiomWrap<T extends OWLAnnotationAssertionAxiom> extends ElkAxiomWrap<T> implements ElkAnnotationAssertionAxiom {

	public ElkAnnotationAssertionAxiomWrap(T owlAxiom) {
		super(owlAxiom);
	}

	@Override
	public <O> O accept(ElkAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public ElkAnnotationSubject getSubject() {
		return converter.convert(owlObject.getSubject());
	}

	@Override
	public ElkAnnotationProperty getProperty() {
		return converter.convert(owlObject.getProperty());
	}

	@Override
	public ElkAnnotationValue getValue() {
		return converter.convert(owlObject.getValue());
	}
	

}
