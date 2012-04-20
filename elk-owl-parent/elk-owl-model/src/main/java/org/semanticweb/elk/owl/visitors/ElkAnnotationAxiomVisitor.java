/**
 * 
 */
package org.semanticweb.elk.owl.visitors;

import org.semanticweb.elk.owl.interfaces.ElkAnnotationAssertionAxiom;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 *
 */
public interface ElkAnnotationAxiomVisitor<O> {
	
	O visit(ElkAnnotationAssertionAxiom annAssertionAxiom);

}
