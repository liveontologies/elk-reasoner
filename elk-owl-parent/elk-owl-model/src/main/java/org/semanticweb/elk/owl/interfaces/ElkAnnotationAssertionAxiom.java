/**
 * 
 */
package org.semanticweb.elk.owl.interfaces;

/**
 * Annotation assertion axiom as defined in 
 * <a href="http://www.w3.org/TR/owl2-syntax/#Annotation_Assertion"> Section 10.2.1</a> of the specification
 * 
 * TODO It may be convenient to make this a subinterface of ElkAnnotation, even though it's not the case in the spec
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 *
 */
public interface ElkAnnotationAssertionAxiom extends ElkAnnotationAxiom {

	public ElkAnnotationSubject getSubject();
	public ElkAnnotationProperty getProperty();
	public ElkAnnotationValue getValue();
}
