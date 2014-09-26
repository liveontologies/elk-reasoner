/**
 * 
 */
package org.semanticweb.elk.reasoner.stages;

import java.util.LinkedList;
import java.util.List;

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.proofs.expressions.Expression;
import org.semanticweb.elk.proofs.inferences.AbstractInferenceVisitor;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.elk.proofs.inferences.InferenceVisitor;
import org.semanticweb.elk.proofs.inferences.mapping.InferenceMapper;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceStore;


/**
 * Similar to {@link ReasonerInferenceReader} but recursively requests all used inferences.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class RecursiveReasonerInferenceReader extends ReasonerInferenceReader {

	public RecursiveReasonerInferenceReader(AbstractReasonerState r) {
		super(r);
	}
	
	@Override
	public Iterable<Inference> getInferences(Expression expression) throws ElkException {
		// first transform the expression into an input for trace reader
		TracingInput input = expression.accept(new ExpressionTracingInputVisitor(reasoner.getIndexObjectConverter()), null);
		TraceStore.Reader traceReader = reasoner.getTraceState().getTraceStore().getReader();
		final InferenceMapper mapper = new InferenceMapper(traceReader);
		final List<Inference> userInferences = new LinkedList<Inference>();
		InferenceVisitor<Void, Void> collector = new AbstractInferenceVisitor<Void, Void>() {

			@Override
			protected Void defaultVisit(Inference inference, Void input) {
				userInferences.add(inference);
				return null;
			}
			
		};

		if (input instanceof ClassTracingInput) {
			ClassTracingInput inp = (ClassTracingInput) input;
			
			mapper.map(inp.root, inp.conclusion, collector);
			
			return userInferences;
					
		}
		else if (input instanceof ObjectPropertyTracingInput) {
			ObjectPropertyTracingInput inp = (ObjectPropertyTracingInput) input;
			
			mapper.map(inp.conclusion, collector);
			
			return userInferences;
		}
		
		return null;
	}

}
