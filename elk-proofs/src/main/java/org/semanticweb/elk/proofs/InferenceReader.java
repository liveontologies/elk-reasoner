/**
 * 
 */
package org.semanticweb.elk.proofs;

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.proofs.expressions.Expression;
import org.semanticweb.elk.proofs.inferences.Inference;


/**
 * The main interface for requesting {@link Inference}s for an
 * {@link Expression}.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public interface InferenceReader {

	public void initialize(ElkAxiom subsumption) throws ElkException;
	
	public Iterable<Inference> getInferences(Expression expression) throws ElkException;
}
