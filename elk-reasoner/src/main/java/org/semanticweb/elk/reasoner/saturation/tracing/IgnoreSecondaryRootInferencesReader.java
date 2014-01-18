/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * Ignores all inferences for the root of a context (except of the
 * initialization) when reading the trace.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class IgnoreSecondaryRootInferencesReader implements TraceStore.Reader {

	private final TraceStore.Reader reader_;
	
	public IgnoreSecondaryRootInferencesReader(TraceStore.Reader r) {
		reader_ = r;
	}

	@Override
	public void accept(final Context context, final Conclusion conclusion, final TracedConclusionVisitor<?, ?> visitor) {
		reader_.accept(context, conclusion, new BaseTracedConclusionVisitor<Boolean, Context>() {

			@Override
			protected Boolean defaultTracedVisit(TracedConclusion conclusion, Context context) {
				conclusion.acceptTraced(visitor, null);
				
				return true;
			}
			
			@Override
			public Boolean visit(SubClassOfSubsumer conclusion, Context context) {
				if (conclusion.getExpression() != context.getRoot()) {
					defaultTracedVisit(conclusion, context);
				}
				
				return true;
			}

			@Override
			public Boolean visit(ComposedConjunction conclusion, Context context) {
				if (conclusion.getExpression() != context.getRoot()) {
					defaultTracedVisit(conclusion, context);
				}
				
				return true;
			}

			@Override
			public Boolean visit(DecomposedConjunction conclusion, Context context) {
				if (conclusion.getExpression() != context.getRoot()) {
					defaultTracedVisit(conclusion, context);
				}
				
				return true;
			}

			@Override
			public Boolean visit(PropagatedSubsumer conclusion, Context context) {
				if (conclusion.getExpression() != context.getRoot()) {
					defaultTracedVisit(conclusion, context);
				}
				
				return true;
			}
			
		});
	}

}
