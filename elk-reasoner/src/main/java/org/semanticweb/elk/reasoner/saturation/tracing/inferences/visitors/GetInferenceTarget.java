/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ComposedBackwardLink;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.DecomposedExistential;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.Inference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.PropagatedSubsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ReversedBackwardLink;

/**
 * Given an {@link Inference}, returns the root of the context to which this inference
 * should be produced.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class GetInferenceTarget extends BaseInferenceVisitor<Context, IndexedClassExpression> {

	@Override
	protected IndexedClassExpression defaultTracedVisit(Inference conclusion, Context premiseContext) {
		// by default produce to the context where the inference has been
		// made (where its premises are stored)
		return premiseContext.getRoot();
	}

	@Override
	public IndexedClassExpression visit(PropagatedSubsumer conclusion, Context premiseContext) {
		return conclusion.getBackwardLink().getSource();
	}

	@Override
	public IndexedClassExpression visit(ComposedBackwardLink conclusion, Context premiseContext) {
		return conclusion.getForwardLink().getTarget();
	}

	@Override
	public IndexedClassExpression visit(ReversedBackwardLink conclusion, Context premiseContext) {
		return conclusion.getSourceLink().getSource();
	}

	@Override
	public IndexedClassExpression visit(DecomposedExistential conclusion, Context premiseContext) {
		// FIXME this is a dirty hack, parameterize Subsumer so we can get the
		// properly typed existential here 
		return ((IndexedObjectSomeValuesFrom)conclusion.getExistential().getExpression()).getFiller();
	}
}
