/**
 * 
 */
package org.semanticweb.elk.reasoner.stages;

import java.util.LinkedList;
import java.util.List;

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.proofs.InferenceReader;
import org.semanticweb.elk.proofs.expressions.Expression;
import org.semanticweb.elk.proofs.inferences.AbstractInferenceVisitor;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.elk.proofs.inferences.InferenceVisitor;
import org.semanticweb.elk.proofs.inferences.mapping.ExpressionMapper;
import org.semanticweb.elk.proofs.inferences.mapping.InferenceMapper;
import org.semanticweb.elk.proofs.inferences.mapping.OneStepTraceUnwinder;
import org.semanticweb.elk.proofs.inferences.mapping.TracingInput;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceStore;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceUnwinder;


/**
 * Inference reader which works directly with the reasoner to request low-level
 * inferences for expressions and maps them to the user-level inferences showed
 * in proofs.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ReasonerInferenceReader implements InferenceReader {

	final AbstractReasonerState reasoner;
	
	public ReasonerInferenceReader(AbstractReasonerState r) {
		reasoner = r;
	}
	
	public void initialize(ElkSubClassOfAxiom subsumption) throws ElkException {
		reasoner.explainSubsumption(subsumption.getSubClassExpression(), subsumption.getSuperClassExpression());
	}
	
	protected TraceUnwinder getTraceUnwinder(TraceStore.Reader reader) {
		return new OneStepTraceUnwinder(reader);
	}
	
	@Override
	public Iterable<Inference> getInferences(Expression expression) throws ElkException {
		// first transform the expression into inputs for the trace reader
		Iterable<TracingInput> inputs = ExpressionMapper.convertExpressionToTracingInputs(expression, reasoner.getIndexObjectConverter());
		TraceStore.Reader traceReader = reasoner.getTraceState().getTraceStore().getReader();
		final InferenceMapper mapper = new InferenceMapper(getTraceUnwinder(traceReader));
		final List<Inference> userInferences = new LinkedList<Inference>();
		InferenceVisitor<Void, Void> collector = new AbstractInferenceVisitor<Void, Void>() {

			@Override
			protected Void defaultVisit(Inference inference, Void input) {
				userInferences.add(inference);
				return null;
			}
			
		};
		// transformation happens here, each user-level inference will be passed
		// to the collector
		mapper.map(inputs, collector);
		
		return userInferences;
	}

}
