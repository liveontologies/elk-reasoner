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

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.ContextFactory;
import org.semanticweb.elk.reasoner.saturation.ContextImpl;
import org.semanticweb.elk.reasoner.saturation.MapSaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.tracing.LocalTracingSaturationState.TracedContext;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.Inference;
import org.semanticweb.elk.util.collections.ArrayHashSet;
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
public class LocalTracingSaturationState extends
		MapSaturationState<TracedContext> {

	public LocalTracingSaturationState(OntologyIndex index) {
		// use a context factory which creates traced contexts
		super(index, new ContextFactory<TracedContext>() {

			@Override
			public TracedContext createContext(IndexedClassExpression root) {
				return new TracedContext(root);
			}

		});
	}

	@Override
	public TracedContext getContext(IndexedClassExpression ice) {
		return super.getContext(ice);
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
		private Multimap<Conclusion, Inference> blockedInferences_;
		/**
		 * The set of conclusions missing in the main saturation state at the
		 * time of tracing (if the closure was computed w.r.t. a subset of all
		 * rules, e.g. only non-redundant rules). Indexed by roots of contexts
		 * where these conclusions should be stored.
		 */
		private final Multimap<IndexedClassExpression, Conclusion> missingConclusions_ = new HashListMultimap<IndexedClassExpression, Conclusion>();
		/**
		 * The set of roots of contexts in the main saturation state whose
		 * saturation was triggered by tracing this context. We remember such
		 * contexts because we can't simply mark *specific* contexts in the main
		 * state as saturated during tracing. We can only mark *all* unsaturated
		 * contexts as saturated which is only possible when all concurrent
		 * tracing jobs finish.
		 * 
		 * TODO I'd happily get rid of this if I could
		 */
		private final Set<IndexedClassExpression> saturatedMainContexts_ = new ArrayHashSet<IndexedClassExpression>(4);

		public TracedContext(IndexedClassExpression root) {
			super(root);
		}

		public Multimap<IndexedClassExpression, Conclusion> getMissingConclusions() {
			return missingConclusions_;
		}
		
		public void addMissingConclusion(IndexedClassExpression root, Conclusion conclusion) {
			missingConclusions_.add(root, conclusion);
		}
		
		public void clearMissingConclusions() {
			missingConclusions_.clear();
		}
		
		public void addSaturatedMainContext(IndexedClassExpression root) {
			saturatedMainContexts_.add(root);
		}
		
		public void addSaturatedMainContexts(Set<IndexedClassExpression> roots) {
			saturatedMainContexts_.addAll(roots);
		}
		
		public boolean isMainContextSaturated(IndexedClassExpression root) {
			return saturatedMainContexts_.contains(root);
		}
		
		public void clearSaturatedMainContexts() {
			saturatedMainContexts_.clear();
		}
		
		public Multimap<Conclusion, Inference> getBlockedInferences() {
			if (blockedInferences_ == null) {
				blockedInferences_ = new HashListMultimap<Conclusion, Inference>();
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
