/**
 * 
 */
package org.semanticweb.elk.reasoner.stages;

import java.util.LinkedList;
import java.util.List;

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.proofs.expressions.Expression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpressionFactory;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpressionFactoryWithCaching;
import org.semanticweb.elk.proofs.expressions.derived.entries.DummyExpressionfactory;
import org.semanticweb.elk.proofs.inferences.AbstractInferenceVisitor;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.elk.proofs.inferences.InferenceReader;
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
	
	private DerivedExpressionFactory expressionFactory_ = new DummyExpressionfactory();
	
	public ReasonerInferenceReader(AbstractReasonerState r) {
		reasoner = r;
	}
	
	public DerivedExpression initialize(ElkClass sub, ElkClass sup) throws ElkException {
		// trace it
		reasoner.explainSubsumption(sub, sup);
		// this expression factory will guarantee pointer equality for structurally equivalent expressions 
		expressionFactory_ = new DerivedExpressionFactoryWithCaching(this);
		// create and return the first derived expression which corresponds to the initial subsumption
		return expressionFactory_.create(new ElkObjectFactoryImpl().getSubClassOfAxiom(sub, sup));
	}
	
	protected TraceUnwinder getTraceUnwinder(TraceStore.Reader reader) {
		return new OneStepTraceUnwinder(reader);
	}
	
	@Override
	public Iterable<Inference> getInferences(Expression expression) throws ElkException {
		// first transform the expression into inputs for the trace reader
		Iterable<TracingInput> inputs = ExpressionMapper.convertExpressionToTracingInputs(expression, reasoner.getIndexObjectConverter());
		TraceStore.Reader traceReader = reasoner.getTraceState().getTraceStore().getReader();
		final InferenceMapper mapper = new InferenceMapper(getTraceUnwinder(traceReader), expressionFactory_);
		final List<Inference> userInferences = new LinkedList<Inference>();
		InferenceVisitor<Void, Void> collector = new AbstractInferenceVisitor<Void, Void>() {

			@Override
			protected Void defaultVisit(Inference inference, Void input) {
				userInferences.add(inference);
				return null;
			}
			
		};
		// transformation happens here, each user-level inference will be passed to the collector
		mapper.map(inputs, collector);
		
		return userInferences;
	}

	@Override
	public DerivedExpressionFactory getExpressionFactory() {
		return expressionFactory_;
	}

}
