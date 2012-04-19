/**
 * 
 */
package org.semanticweb.elk.owl.implementation;

import org.semanticweb.elk.owl.interfaces.ElkAnnotationAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationProperty;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationSubject;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationValue;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;

/**
 * Implementation of {@link ElkAnnotationAssertionAxiom}
 * 
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 *
 */
public class ElkAnnotationAssertionAxiomImpl implements ElkAnnotationAssertionAxiom {

	private final ElkAnnotationSubject annSubject;
	private final ElkAnnotationProperty annProperty;
	private final ElkAnnotationValue annValue;
	
	ElkAnnotationAssertionAxiomImpl(ElkAnnotationSubject annSubject, ElkAnnotationProperty annProperty, ElkAnnotationValue annValue) {
		this.annSubject = annSubject;
		this.annProperty = annProperty;
		this.annValue = annValue;
	}
	
	@Override
	public <O> O accept(ElkObjectVisitor<O> visitor) {
		// TODO extend the visitor
		return null;
	}

	@Override
	public ElkAnnotationProperty getProperty() {
		return annProperty;
	}

	@Override
	public ElkAnnotationValue getValue() {
		return annValue;
	}

	@Override
	public <O> O accept(ElkAxiomVisitor<O> visitor) {
		// TODO extend the visitor
		return null;
	}

	@Override
	public ElkAnnotationSubject getSubject() {
		return annSubject;
	}
}