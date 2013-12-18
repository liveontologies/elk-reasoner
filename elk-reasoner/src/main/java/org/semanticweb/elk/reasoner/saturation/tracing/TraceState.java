/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.SaturationState;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class TraceState {

	private final TraceStore traceStore_;

	private final TracingSaturationState tracingSaturationState_;
	
	private final Set<IndexedClassExpression> toTraceQueue_;
	
	public TraceState(TraceStore store, SaturationState mainState) {
		traceStore_ = new SimpleCentralizedTraceStore();
		toTraceQueue_ = new HashSet<IndexedClassExpression>();
		tracingSaturationState_ = new TracingSaturationState(mainState.getOntologyIndex());
	}

	public boolean submitForTracing(IndexedClassExpression root) {
		return toTraceQueue_.add(root);
	}
	
	public Set<IndexedClassExpression> getRootsSubmittedForTracing() {
		return Collections.unmodifiableSet(toTraceQueue_);
	}
	
	public void flushQueue() {
		toTraceQueue_.clear();
	}
	
	public TraceStore getTraceStore() {
		return traceStore_;
	}
	
	public TracingSaturationState getSaturationState() {
		return tracingSaturationState_;
	}
}
