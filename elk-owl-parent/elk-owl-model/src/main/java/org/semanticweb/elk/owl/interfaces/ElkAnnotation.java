/**
 * 
 */
package org.semanticweb.elk.owl.interfaces;


/**
 * The basic interface for annotation objects in OWL 2
 * as defined in <a href="http://www.w3.org/TR/owl2-syntax/#Annotations">Section 10</a> of the specification
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 *
 */
public interface ElkAnnotation extends ElkObject {

	public ElkAnnotationProperty getProperty();
	public ElkAnnotationValue getValue();
}
