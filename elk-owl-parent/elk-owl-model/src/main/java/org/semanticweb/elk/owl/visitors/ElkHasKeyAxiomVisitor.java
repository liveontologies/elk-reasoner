package org.semanticweb.elk.owl.visitors;

import org.semanticweb.elk.owl.interfaces.ElkHasKeyAxiom;

/**
 * Visitor pattern interface for instances of {@link ElkHasKeyAxiom}.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 *
 */
public interface ElkHasKeyAxiomVisitor<O> {

	O visit(ElkHasKeyAxiom elkHasKey);
}
