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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.hierarchy.OntologyIndex;
import org.semanticweb.elk.reasoner.saturation.ContextFactory;
import org.semanticweb.elk.reasoner.saturation.ContextImpl;
import org.semanticweb.elk.reasoner.saturation.MapSaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.tracing.LocalTracingSaturationState.TracedContext;
import org.semanticweb.elk.util.collections.Condition;
import org.semanticweb.elk.util.collections.HashListMultimap;
import org.semanticweb.elk.util.collections.Multimap;
import org.semanticweb.elk.util.collections.Operations;

/**
 * A specialization of {@link MapSaturationState} for inference tracing.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class LocalTracingSaturationState extends
		MapSaturationState<TracedContext> {

	public LocalTracingSaturationState(OntologyIndex index) {
		// use a context factory which creates traced contexts
		super(index, new ContextFactory<TracedContext>() {

			@Override
			public TracedContext createContext(IndexedContextRoot root) {
				return new TracedContext(root);
			}

		});
	}

	@Override
	public TracedContext getContext(IndexedContextRoot root) {
		return super.getContext(root);
	}

	public Iterable<TracedContext> getTracedContexts() {
		return Operations.filter(getContexts(), new Condition<Context>() {

			@Override
			public boolean holds(Context cxt) {
				return cxt.isInitialized() && cxt.isSaturated();
			}
		});
	}

	/**
	 * Local contexts used for inference tracing.
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	public static class TracedContext extends ContextImpl {
		/**
		 * Set to {@code true} immediately before submitting for tracing (via
		 * saturation) and set to {@code false} once that is done. This flag
		 * ensures that only one worker traces the context and all other
		 * processing (including "reading" e.g. unwinding the traces) must wait
		 * until that is done.
		 */
		private final AtomicBoolean beingTraced_ = new AtomicBoolean(false);
		/**
		 * Stores the set of blocked inferences indexed by conclusions through
		 * which they are blocked. That is, if a conclusion C has only been
		 * derived through the inference I, then we store C -> I when we derive
		 * I from C.
		 */
		private Multimap<Conclusion, ClassInference> blockedInferences_;

		public TracedContext(IndexedContextRoot root) {
			super(root);
		}

		public Multimap<Conclusion, ClassInference> getBlockedInferences() {
			if (blockedInferences_ == null) {
				blockedInferences_ = new HashListMultimap<Conclusion, ClassInference>();
			}

			return blockedInferences_;
		}

		public void clearBlockedInferences() {
			blockedInferences_ = null;
		}

		public boolean beingTracedCompareAndSet(boolean expect, boolean update) {
			return beingTraced_.compareAndSet(expect, update);
		}

	}

}
