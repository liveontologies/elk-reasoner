/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * Given an inference, returns the root of the context to which this inference
 * should be produced.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class GetInferenceTarget extends BaseTracedConclusionVisitor<IndexedClassExpression, Context> {

	@Override
	protected IndexedClassExpression defaultTracedVisit(TracedConclusion conclusion, Context premiseContext) {
		// by default produce to the context where the inference has been
		// made (where its premises are stored)
		return premiseContext.getRoot();
	}

	@Override
	public IndexedClassExpression visit(PropagatedSubsumer conclusion, Context premiseContext) {
		return conclusion.getBackwardLink().getSource().getRoot();
	}

	@Override
	public IndexedClassExpression visit(ComposedBackwardLink conclusion, Context premiseContext) {
		return conclusion.getForwardLink().getTarget().getRoot();
	}

	@Override
	public IndexedClassExpression visit(ReversedBackwardLink conclusion, Context premiseContext) {
		return conclusion.getSourceLink().getSource().getRoot();
	}

	@Override
	public IndexedClassExpression visit(DecomposedExistential conclusion, Context premiseContext) {
		// FIXME this is a dirty hack, parameterize Subsumer so we can get the
		// properly typed existential here 
		return ((IndexedObjectSomeValuesFrom)conclusion.getExistential().getExpression()).getFiller();
	}
}
