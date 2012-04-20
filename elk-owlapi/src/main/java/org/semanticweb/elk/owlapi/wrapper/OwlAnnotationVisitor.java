/**
 * 
 */
package org.semanticweb.elk.owlapi.wrapper;

import org.semanticweb.elk.owl.interfaces.ElkAnnotationSubject;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationValue;
import org.semanticweb.elk.owl.interfaces.ElkAnonymousIndividual;
import org.semanticweb.elk.owl.interfaces.ElkLiteral;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.owl.iris.ElkIri;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAnnotationSubjectVisitorEx;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAnnotationValueVisitorEx;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;

/**
 * Converts OWL annotation subjects and values (IRIs, anonymous individuals, and literals)
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 *
 */
public class OwlAnnotationVisitor implements OWLAnnotationSubjectVisitorEx<ElkAnnotationSubject>, OWLAnnotationValueVisitorEx<ElkAnnotationValue> {

	private final static OwlAnnotationVisitor INSTANCE_ = new OwlAnnotationVisitor();
	protected static OwlConverter CONVERTER = OwlConverter.getInstance();
	
	static OwlAnnotationVisitor getInstance() {
		return INSTANCE_;
	}
	
	public ElkAnnotationSubject visit(OWLAnnotationSubject subject) {
		return subject.accept(this);
	}
	
	public ElkAnnotationValue visit(OWLAnnotationValue value) {
		return value.accept(this);
	}
	
	@Override
	public ElkIri visit(IRI iri) {
		return new ElkFullIri(iri.toString());
	}

	@Override
	public ElkAnonymousIndividual visit(OWLAnonymousIndividual anon) {
		return CONVERTER.convert(anon);
	}

	@Override
	public ElkLiteral visit(OWLLiteral literal) {
		return CONVERTER.convert(literal);
	}
}
