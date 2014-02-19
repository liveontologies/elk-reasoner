/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.readers;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceStore;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ComposedConjunction;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.DecomposedConjunction;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.Inference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.PropagatedSubsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.SubClassOfSubsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.BaseInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.InferenceVisitor;

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
	public void accept(final IndexedClassExpression root, final Conclusion conclusion, final InferenceVisitor<?, ?> visitor) {
		reader.accept(root, conclusion, new BaseInferenceVisitor<IndexedClassExpression, Boolean>() {

			@Override
			protected Boolean defaultTracedVisit(Inference conclusion, IndexedClassExpression contextRoot) {
				conclusion.acceptTraced(visitor, null);
				
				return true;
			}
			
			@Override
			public Boolean visit(SubClassOfSubsumer conclusion, IndexedClassExpression contextRoot) {
				if (conclusion.getExpression() != contextRoot) {
					defaultTracedVisit(conclusion, contextRoot);
				}
				
				return true;
			}

			@Override
			public Boolean visit(ComposedConjunction conclusion, IndexedClassExpression contextRoot) {
				if (conclusion.getExpression() != contextRoot) {
					defaultTracedVisit(conclusion, contextRoot);
				}
				
				return true;
			}

			@Override
			public Boolean visit(DecomposedConjunction conclusion, IndexedClassExpression contextRoot) {
				if (conclusion.getExpression() != contextRoot) {
					defaultTracedVisit(conclusion, contextRoot);
				}
				
				return true;
			}

			@Override
			public Boolean visit(PropagatedSubsumer conclusion, IndexedClassExpression contextRoot) {
				if (conclusion.getExpression() != contextRoot) {
					defaultTracedVisit(conclusion, contextRoot);
				}
				
				return true;
			}
			
		});
	}

}
