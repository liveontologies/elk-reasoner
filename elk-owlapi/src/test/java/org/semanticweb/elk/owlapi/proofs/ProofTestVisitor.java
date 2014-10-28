/**
 * 
 */
package org.semanticweb.elk.owlapi.proofs;

import org.semanticweb.owlapi.model.OWLClassExpression;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface ProofTestVisitor<E extends Exception> {

	public void visit(OWLClassExpression subsumee, OWLClassExpression subsumer) throws E;
}
