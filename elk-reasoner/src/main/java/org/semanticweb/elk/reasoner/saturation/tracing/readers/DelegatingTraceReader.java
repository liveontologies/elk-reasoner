/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.readers;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceStore;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceStore.Reader;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.InferenceVisitor;

/**
 * Delegates all calls to the underlying reader.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
abstract class DelegatingTraceReader implements Reader {

	protected final TraceStore.Reader reader;
	
	DelegatingTraceReader(TraceStore.Reader r) {
		reader = r;
	}
	
	@Override
	public void accept(IndexedClassExpression root, Conclusion conclusion,
			InferenceVisitor<?, ?> visitor) {
		reader.accept(root, conclusion, visitor);
	}

	@Override
	public Iterable<IndexedClassExpression> getContextRoots() {
		return reader.getContextRoots();
	}

	@Override
	public void visitInferences(IndexedClassExpression root, InferenceVisitor<?, ?> visitor) {
		reader.visitInferences(root, visitor);
	}

}
