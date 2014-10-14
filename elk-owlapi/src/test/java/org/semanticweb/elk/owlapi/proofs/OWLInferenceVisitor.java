/**
 * 
 */
package org.semanticweb.elk.owlapi.proofs;

import org.semanticweb.owlapitools.proofs.OWLInference;

/**
 * Used for testing only
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface OWLInferenceVisitor {

	public void visit(OWLInference inference);
}
