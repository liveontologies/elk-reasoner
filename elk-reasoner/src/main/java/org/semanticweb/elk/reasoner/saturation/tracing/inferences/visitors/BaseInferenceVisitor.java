/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors;

import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ComposedBackwardLink;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ComposedConjunction;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.DecomposedConjunction;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.DecomposedExistential;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.Inference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.InitializationSubsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.PropagatedSubsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ReflexiveSubsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ReversedBackwardLink;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.SubClassOfSubsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.TracedPropagation;



/**
 * TODO
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class BaseInferenceVisitor<I, O> implements InferenceVisitor<I, O> {

	protected O defaultTracedVisit(Inference conclusion, I input) {
		return null;
	}

	@Override
	public O visit(InitializationSubsumer conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(SubClassOfSubsumer conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(ComposedConjunction conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(DecomposedConjunction conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(PropagatedSubsumer conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(ReflexiveSubsumer conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(ComposedBackwardLink conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(ReversedBackwardLink conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(DecomposedExistential conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(TracedPropagation conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}
}
