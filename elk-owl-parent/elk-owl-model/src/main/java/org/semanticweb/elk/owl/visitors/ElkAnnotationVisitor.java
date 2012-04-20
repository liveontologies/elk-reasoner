/**
 * 
 */
package org.semanticweb.elk.owl.visitors;

import org.semanticweb.elk.owl.interfaces.ElkAnonymousIndividual;
import org.semanticweb.elk.owl.interfaces.ElkLiteral;
import org.semanticweb.elk.owl.iris.ElkIri;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 *
 */
public interface ElkAnnotationVisitor<O> {

	O visit(ElkIri iri);
	O visit(ElkLiteral literal);
	O visit(ElkAnonymousIndividual anon);
}
