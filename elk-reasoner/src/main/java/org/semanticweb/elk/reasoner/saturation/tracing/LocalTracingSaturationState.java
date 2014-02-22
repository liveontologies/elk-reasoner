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

import java.util.concurrent.atomic.AtomicBoolean;

import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.AbstractSaturationState;
import org.semanticweb.elk.reasoner.saturation.ContextCreationListener;
import org.semanticweb.elk.reasoner.saturation.ContextImpl;
import org.semanticweb.elk.reasoner.saturation.ContextModificationListener;
import org.semanticweb.elk.reasoner.saturation.MapSaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.Inference;
import org.semanticweb.elk.util.collections.HashListMultimap;
import org.semanticweb.elk.util.collections.Multimap;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.collections.Operations.Condition;

/**
 * A specialization of {@link MapSaturationState} for inference tracing.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class LocalTracingSaturationState extends MapSaturationState {

	public LocalTracingSaturationState(OntologyIndex index) {
		super(index);
	}

	@Override
	public TracedContext getContext(IndexedClassExpression ice) {
		return (TracedContext) super.getContext(ice);
	}
	
	public Iterable<? extends Context> getTracedContexts() {
		return Operations.filter(getContexts(), new Condition<Context>() {

			@Override
			public boolean holds(Context cxt) {
				return cxt.isInitialized() && cxt.isSaturated();
			}
		});
	}

	@Override
	public TracingWriter getContextCreatingWriter(
			ContextCreationListener contextCreationListener,
			ContextModificationListener contextModificationListener) {
		return new TracingWriter(contextCreationListener, contextModificationListener);
	}


	/**
	 * TODO
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	public class TracingWriter extends AbstractSaturationState.ContextCreatingWriter {

		public TracingWriter(ContextCreationListener contextCreationListener,
				ContextModificationListener contextModificationListener) {
			super(contextCreationListener, contextModificationListener);
		}

		@Override
		protected TracedContext newContext(IndexedClassExpression root) {
			return new TracedContext(root);
		}

		@Override
		public TracedContext getCreateContext(IndexedClassExpression root) {
			return (TracedContext) super.getCreateContext(root);
		}
		
	}
	
	/**
	 * TODO 
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	public static class TracedContext extends ContextImpl {
		/**
		 * Set to {@code true} immediately before submitting for tracing (via
		 * saturation) and set to {@code false} once that is done. This flag
		 * ensures that only one worker traces the context and all other
		 * processing (including "reading" e.g. unwinding the traces) must wait
		 * until that is done.
		 */
		private final AtomicBoolean beingTraced_;
		
		private HashListMultimap<Conclusion, Inference> blockedInferences_;
		
		public TracedContext(IndexedClassExpression root) {
			super(root);
			beingTraced_ = new AtomicBoolean(false);
		}
		
		public Multimap<Conclusion, Inference> getBlockedInferences() {
			if (blockedInferences_ == null) {
				blockedInferences_ = new HashListMultimap<Conclusion, Inference>();
			}
			
			return blockedInferences_;
		}
		
		public void cleanBlockedInferences() {
			blockedInferences_ = null;
		}
		
		public boolean beingTracedCompareAndSet(boolean expect, boolean update) {
			return beingTraced_.compareAndSet(expect, update);
		}

	}

}
