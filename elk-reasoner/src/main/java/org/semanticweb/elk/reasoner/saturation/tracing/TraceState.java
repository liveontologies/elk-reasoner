/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.ExtendedSaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.tracing.util.TracingUtils;
import org.semanticweb.elk.util.collections.HashListMultimap;
import org.semanticweb.elk.util.collections.Multimap;

/**
 * A collections of objects for tracing contexts and keeping the relevant
 * information about the state of tracing.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class TraceState {

	private final TraceStore traceStore_;

	private final LocalTracingSaturationState tracingSaturationState_;
	
	private final Multimap<IndexedClassExpression, Conclusion> toTraceQueue_;
	
	private final ContextTracingFactory tracingFactory_;
	
	public TraceState(TraceStore store, ExtendedSaturationState mainState, int maxWorkers) {
		traceStore_ = new SimpleCentralizedTraceStore();
		toTraceQueue_ = new HashListMultimap<IndexedClassExpression, Conclusion>();
		tracingSaturationState_ = new LocalTracingSaturationState(mainState.getOntologyIndex());
		tracingFactory_ = new NonRecursiveContextTracingFactory(mainState, tracingSaturationState_, traceStore_, maxWorkers);
	}

	public boolean submitForTracing(IndexedClassExpression root, IndexedClassExpression subsumer) {
		return toTraceQueue_.add(root, TracingUtils.getSubsumerWrapper(subsumer));
	}
	
	public Multimap<IndexedClassExpression, Conclusion> getTracingQueue() {
		return toTraceQueue_;
	}
	
	public void flushQueue() {
		toTraceQueue_.clear();
	}
	
	public TraceStore getTraceStore() {
		return traceStore_;
	}
	
	public LocalTracingSaturationState getSaturationState() {
		return tracingSaturationState_;
	}
	
	public ContextTracingFactory getContextTracingFactory() {
		return tracingFactory_;
	}
}
