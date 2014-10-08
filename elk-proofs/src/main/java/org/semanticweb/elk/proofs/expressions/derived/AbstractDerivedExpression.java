/**
 * 
 */
package org.semanticweb.elk.proofs.expressions.derived;

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.elk.proofs.inferences.InferenceReader;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
abstract class AbstractDerivedExpression implements DerivedExpression {

	private final InferenceReader reader_;
	
	AbstractDerivedExpression(InferenceReader reader) {
		reader_ = reader;
	}

	@Override
	public Iterable<Inference> getInferences() throws ElkException {
		return reader_.getInferences(this);
	}

}
