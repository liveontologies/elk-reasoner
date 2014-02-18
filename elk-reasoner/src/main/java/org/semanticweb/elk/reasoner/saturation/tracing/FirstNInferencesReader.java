/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.MutableInteger;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;

/**
 * Visits only the first N inferences provided by the underlying
 * {@link TraceStore.Reader}.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class FirstNInferencesReader extends DelegatingTraceReader {

	private final int numberOfInferencesToVisit_;
	
	public FirstNInferencesReader(TraceStore.Reader r, int n) {
		super(r);
		numberOfInferencesToVisit_ = n;
	}
	
	@Override
	public void accept(IndexedClassExpression root, Conclusion conclusion, final InferenceVisitor<?, ?> visitor) {
		final MutableInteger counter = new MutableInteger(0);
		
		reader.accept(root, conclusion, new BaseInferenceVisitor<Void, Void>() {

			@Override
			protected Void defaultTracedVisit(Inference inference,	Void ignored) {
				if (counter.get() < numberOfInferencesToVisit_) {
					counter.increment();
					inference.acceptTraced(visitor, null);
				}
				
				return null;
			}
			
		});
	}

}
