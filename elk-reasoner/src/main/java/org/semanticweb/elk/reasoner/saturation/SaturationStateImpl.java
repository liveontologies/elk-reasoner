/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionFactory;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionInsertionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.SimpleConclusionFactory;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.CompositionRuleApplicationVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the main saturation state maintained by the reasoner.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
class SaturationStateImpl implements ExtendedSaturationState {

	// logger for this class
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(SaturationStateImpl.class);

	private final OntologyIndex ontologyIndex_;

	/**
	 * Cached constants
	 */
	private final IndexedClassExpression owlThing_, owlNothing_;

	/**
	 * The first context in the linked list of contexts
	 */
	private ContextImpl firstContext;

	/**
	 * The queue containing all activated contexts. Every activated context
	 * occurs exactly once.
	 */
	private final Queue<Context> activeContexts_ = new ConcurrentLinkedQueue<Context>();

	/**
	 * The queue of all contexts for which computation of the closure under
	 * inference rules has not yet been finished.
	 */
	private final Queue<IndexedClassExpression> notSaturatedContexts_ = new ConcurrentLinkedQueue<IndexedClassExpression>();
	
	private final ConclusionFactory conclusionFactory_ = new SimpleConclusionFactory();

	/**
	 * @return the {@link Collection} of {@link Context} stored in this
	 *         {@link SaturationStateImpl}
	 */
	@Override
	public Collection<Context> getContexts() {
		return new AbstractCollection<Context>() {

			@Override
			public Iterator<Context> iterator() {
				return new Iterator<Context>() {

					ContextImpl next = firstContext.next;

					@Override
					public boolean hasNext() {
						return next != null;
					}

					@Override
					public Context next() {
						if (next == null)
							throw new NoSuchElementException("No next context");
						Context result = next;
						next = next.next;
						return result;
					}

					@Override
					public void remove() {
						throw new UnsupportedOperationException(
								"Removal not supported");
					}

				};
			}
			
			@Override
			public boolean isEmpty() {
				return firstContext.next == null;
			}

			@Override
			public int size() {
				// TODO Auto-generated method stub
				return 0;
			}

		};
	}

	private void resetFirstContext() {
		firstContext = new ContextImpl(null);//a dummy first context
	}

	@Override
	public Collection<IndexedClassExpression> getNotSaturatedContexts() {
		return notSaturatedContexts_;
	}

	/**
	 * 
	 * @param index
	 */
	public SaturationStateImpl(OntologyIndex index) {
		ontologyIndex_ = index;
		owlThing_ = index.getIndexedOwlThing();
		owlNothing_ = index.getIndexedOwlNothing();
		resetFirstContext();
	}

	@Override
	public OntologyIndex getOntologyIndex() {
		return ontologyIndex_;
	}
	
	@Override
	public Context getContext(IndexedClassExpression ice) {
		return ice.getContext();
	}
	
	private ExtendedSaturationStateWriter getDefaultWriter(final ConclusionVisitor<?, Context> conclusionVisitor, CompositionRuleApplicationVisitor initRuleAppVisitor) {
		return new ContextCreatingWriter(
				ContextCreationListener.DUMMY, ContextModificationListener.DUMMY,
				initRuleAppVisitor, conclusionVisitor, true);
	}
	
	/**
	 * @param visitor a {@link ConclusionVisitor} which will be invoked for each produced {@link Conclusion}
	 * 
	 * @return an {@link BasicSaturationStateWriter} for modifying this
	 *         {@link SaturationState}. The methods of this
	 *         {@link BasicSaturationStateWriter} are thread safe
	 */
	@Override
	public BasicSaturationStateWriter getWriter(ConclusionVisitor<?, Context> conclusionVisitor) {
		return getDefaultWriter(conclusionVisitor, DEFAULT_INIT_RULE_APP_VISITOR);
	}

	@Override
	public ExtendedSaturationStateWriter getExtendedWriter(ConclusionVisitor<?, Context> conclusionVisitor, CompositionRuleApplicationVisitor initRuleAppVisitor) {
		return getDefaultWriter(conclusionVisitor, initRuleAppVisitor);
	}

	@Override
	public ExtendedSaturationStateWriter getExtendedWriter(
			ContextCreationListener contextCreationListener,
			ContextModificationListener contextModificationListener,
			CompositionRuleApplicationVisitor ruleAppVisitor,
			ConclusionVisitor<?, Context> conclusionVisitor,
			boolean trackNewContextsAsUnsaturated) {
		return new ContextCreatingWriter(contextCreationListener,
				contextModificationListener, ruleAppVisitor, conclusionVisitor,
				trackNewContextsAsUnsaturated);
	}

	@Override
	public BasicSaturationStateWriter getWriter(
			ContextModificationListener contextModificationListener, ConclusionVisitor<?, Context> conclusionVisitor) {
		return new BasicWriter(contextModificationListener, conclusionVisitor);
	}

	/**
	 * 
	 * 
	 */
	class BasicWriter implements BasicSaturationStateWriter {

		private final ConclusionVisitor<?, Context> producedConclusionVisitor_;
		
		private final ContextModificationListener contextModificationListener_;

		private BasicWriter(
				ContextModificationListener contextSaturationListener,
				ConclusionVisitor<?, Context> conclusionVisitor) {
			this.contextModificationListener_ = contextSaturationListener;
			this.producedConclusionVisitor_ = conclusionVisitor;
		}

		@Override
		public IndexedClassExpression getOwlThing() {
			return owlThing_;
		}

		@Override
		public IndexedClassExpression getOwlNothing() {
			return owlNothing_;
		}

		@Override
		public Context pollForActiveContext() {
			return activeContexts_.poll();
		}

		@Override
		public void produce(Context context, Conclusion conclusion) {
			LOGGER_.trace("{}: produced conclusion {}", context, conclusion);
			// this may be necessary, e.g., for counting produced conclusions
			conclusion.accept(producedConclusionVisitor_, context);
			
			if (context.addToDo(conclusion)) {
				// context was activated
				activeContexts_.add(context);
			}
		}

		protected void markAsNotSaturatedInternal(Context context) {
			LOGGER_.trace("{}: marked as non-saturated", context);

			notSaturatedContexts_.add(context.getRoot());
			contextModificationListener_.notifyContextModification(context);
		}

		@Override
		public boolean markAsNotSaturated(Context context) {
			if (context.setSaturated(false)) {
				markAsNotSaturatedInternal(context);

				return true;
			} else {
				return false;
			}
		}

		@Override
		public void clearNotSaturatedContexts() {
			LOGGER_.trace("Clear non-saturated contexts");
			notSaturatedContexts_.clear();
		}

		@Override
		public void resetContexts() {
			resetFirstContext();
		}

		@Override
		public ConclusionFactory getConclusionFactory() {
			return conclusionFactory_;
		}

		@Override
		public ConclusionVisitor<Boolean, Context> getConclusionInserter() {
			return new ConclusionInsertionVisitor();
		}

	}

	/**
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	class ContextCreatingWriter extends BasicWriter implements ExtendedSaturationStateWriter {
		
		private final CompositionRuleApplicationVisitor initRuleAppVisitor_;

		/**
		 * If set to true, the writer will put all newly created contexts to
		 * {@link notSaturatedContexts_} This is important if saturation of a
		 * new context has been interrupted for some reason, e.g. inconsistency.
		 * Then we'll need to store all such contexts to be able to complete
		 * their saturation later.
		 */
		private final boolean trackNewContextsAsUnsaturated_;

		private final ContextCreationListener contextCreationListener_;

		private ContextCreatingWriter(
				ContextCreationListener contextCreationListener,
				ContextModificationListener contextModificationListener,
				CompositionRuleApplicationVisitor ruleAppVisitor,
				ConclusionVisitor<?, Context> conclusionVisitor,
				boolean trackNewContextsAsUnsaturated) {
			super(contextModificationListener, conclusionVisitor);

			this.contextCreationListener_ = contextCreationListener;
			this.initRuleAppVisitor_ = ruleAppVisitor;
			this.trackNewContextsAsUnsaturated_ = trackNewContextsAsUnsaturated;
		}

		@Override
		public Context getCreateContext(IndexedClassExpression root) {
			Context context = root.getContext();
			
			if (context != null)
				return context;
			// else try to assign a new context
			ContextImpl newContext = new ContextImpl(root);
			if (!root.setContext(newContext))
				// the context is already assigned meanwhile
				return root.getContext();
			// else the context is new
			initContext(newContext);
			contextCreationListener_.notifyContextCreation(newContext);

			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace(newContext.getRoot() + ": context created");
			}

			if (trackNewContextsAsUnsaturated_) {
				markAsNotSaturatedInternal(newContext);
			}

			/* 
			 * linking contexts
			 * TODO how much contention are we going to get here? 
			*/
			synchronized(firstContext) {
				ContextImpl oldHead = firstContext.next;
				
				firstContext.next = newContext;
				newContext.previous = firstContext;
				
				if (oldHead != null) {
					newContext.next = oldHead;
					oldHead.previous = newContext;
				}
			}

			return newContext;
		}

		@Override
		public void initContext(Context context) {
			SaturationUtils.initContext(context, this, ontologyIndex_.getContextInitRuleHead(), initRuleAppVisitor_);
		}

	}
	
}
