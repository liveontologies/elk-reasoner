/**
 * 
 */
package org.semanticweb.elk.proofs.inferences;

import java.util.Collections;

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.proofs.expressions.Expression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpressionFactory;
import org.semanticweb.elk.proofs.expressions.derived.entries.DummyExpressionfactory;


/**
 * The main interface for requesting {@link Inference}s for an
 * {@link Expression}.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public interface InferenceReader {

	public Iterable<Inference> getInferences(Expression expression) throws ElkException;
	
	public DerivedExpressionFactory getExpressionFactory();
	
	// no-op reader
	public static InferenceReader DUMMY = new InferenceReader() {

		@Override
		public Iterable<Inference> getInferences(Expression expression) throws ElkException {
			return Collections.emptyList();
		}

		@Override
		public DerivedExpressionFactory getExpressionFactory() {
			return new DummyExpressionfactory();
		}
		
	};
}
