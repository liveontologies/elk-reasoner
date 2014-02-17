/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
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
public class IgnoreSecondaryRootInferencesReader extends DelegatingTraceReader {

	public IgnoreSecondaryRootInferencesReader(TraceStore.Reader r) {
		super(r);
	}

	@Override
	public void accept(final IndexedClassExpression root, final Conclusion conclusion, final TracedConclusionVisitor<?, ?> visitor) {
		reader.accept(root, conclusion, new BaseTracedConclusionVisitor<Boolean, Context>() {

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
