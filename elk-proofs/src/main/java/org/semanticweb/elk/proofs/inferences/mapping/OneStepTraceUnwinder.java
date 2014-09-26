package org.semanticweb.elk.proofs.inferences.mapping;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ObjectPropertyConclusion;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceStore;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceUnwinder;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ClassInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ObjectPropertyInferenceVisitor;

/**
 * Unwinds only one step back, i.e. just calls the underlying trace reader once.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class OneStepTraceUnwinder implements TraceUnwinder {

	private final TraceStore.Reader traceReader_;

	public OneStepTraceUnwinder(TraceStore.Reader reader) {
		traceReader_ = reader;
	}

	@Override
	public void accept(IndexedClassExpression context, Conclusion conclusion,
			ClassInferenceVisitor<IndexedClassExpression, ?> inferenceVisitor,
			ObjectPropertyInferenceVisitor<?, ?> propertyInferenceVisitor) {
		traceReader_.accept(context, conclusion, inferenceVisitor);
	}

	@Override
	public void accept(ObjectPropertyConclusion conclusion,
			ObjectPropertyInferenceVisitor<?, ?> inferenceVisitor) {
		traceReader_.accept(conclusion, inferenceVisitor);
	}

}
