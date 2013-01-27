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
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.text.AbstractWriter;

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.PositiveSubsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.BasicCompositionRuleApplicationVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.LinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationVisitor;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class SaturationState {

	// logger for this class
	private static final Logger LOGGER_ = Logger
			.getLogger(SaturationState.class);

	private final OntologyIndex ontologyIndex_;

	/**
	 * Cached constants
	 */
	private final IndexedClassExpression owlThing_, owlNothing_;

	/**
	 * the reference to the first context in the link list of contexts
	 */
	private final AtomicReference<ContextImpl> firstContext = new AtomicReference<ContextImpl>(
			null);

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

	/**
	 * Kept because still need to be cleaned to avoid broken backward links
	 */
	private final Queue<IndexedClassExpression> removedContexts_ = new ConcurrentLinkedQueue<IndexedClassExpression>();

	/**
	 * @return the {@link Collection} of {@link Context} stored in this
	 *         {@link SaturationState}
	 */
	public Collection<Context> getContexts() {
		return new AbstractCollection<Context>() {

			@Override
			public Iterator<Context> iterator() {
				return new Iterator<Context>() {

					ContextImpl next = firstContext.get();

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
			public int size() {
				// TODO Auto-generated method stub
				return 0;
			}

		};
	}

	public void resetFirstContext() {
		firstContext.set(null);
	}

	public Collection<IndexedClassExpression> getNotSaturatedContexts() {
		return notSaturatedContexts_;
	}

	public Collection<IndexedClassExpression> getContextsToBeRemoved() {
		return removedContexts_;
	}

	private static final RuleApplicationVisitor DEFAULT_INIT_RULE_APP_VISITOR = new BasicCompositionRuleApplicationVisitor();
	
	/**
	 * 
	 * @param index
	 */
	public SaturationState(OntologyIndex index) {
		ontologyIndex_ = index;
		owlThing_ = index.getIndexedOwlThing();
		owlNothing_ = index.getIndexedOwlNothing();
	}

	private ExtendedWriter getDefaultWriter(final ConclusionVisitor<?> conclusionVisitor) {
		return new ContextCreatingWriter(
				ContextCreationListener.DUMMY, ContextModificationListener.DUMMY,
				DEFAULT_INIT_RULE_APP_VISITOR, conclusionVisitor, false);
	}
	
	/**
	 * @param visitor a {@link ConclusionVisitor} which will be invoked for each produced {@link Conclusion}
	 * 
	 * @return an {@link Writer} for modifying this
	 *         {@link SaturationState}. The methods of this
	 *         {@link Writer} are thread safe
	 */
	public Writer getWriter(ConclusionVisitor<?> conclusionVisitor) {
		return getDefaultWriter(conclusionVisitor);
	}

	public ExtendedWriter getExtendedWriter(ConclusionVisitor<?> conclusionVisitor) {
		return getDefaultWriter(conclusionVisitor);
	}

	public Writer getSaturationCheckingWriter(ConclusionVisitor<?> conclusionVisitor) {
		return new SaturationCheckingWriter(conclusionVisitor);
	}

	/**
	 * Creates a new {@link ExtendedWriter} for modifying this
	 * {@link SaturationState} associated with the given
	 * {@link ContextCreationListener}. If {@link ContextCreationListener} is
	 * not thread safe, the calls of the methods for the same
	 * {@link AbstractWriter} should be synchronized
	 * 
	 */
	public ExtendedWriter getExtendedWriter(
			ContextCreationListener contextCreationListener,
			ContextModificationListener contextModificationListener,
			RuleApplicationVisitor ruleAppVisitor,
			ConclusionVisitor<?> conclusionVisitor,
			boolean trackNewContextsAsUnsaturated) {
		return new ContextCreatingWriter(contextCreationListener,
				contextModificationListener, ruleAppVisitor, conclusionVisitor,
				trackNewContextsAsUnsaturated);
	}

	public Writer getWriter(
			ContextModificationListener contextModificationListener, ConclusionVisitor<?> conclusionVisitor) {
		return new BasicWriter(contextModificationListener, conclusionVisitor);
	}

	/**
	 * Functions that can write the saturation state are grouped here. With
	 * every {@link Writer} one can register a {@link ContextCreationListener}
	 * that will be executed every time this {@link Writer} creates a new
	 * {@code Context}. Although all functions of this {@link Writer} are thread
	 * safe, the function of the {@link ContextCreationListener} might not be,
	 * in which the access of functions of {@link Writer} should be synchronized
	 * between threads.
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	public interface Writer {

		public IndexedClassExpression getOwlThing();

		public IndexedClassExpression getOwlNothing();

		public Context pollForContext();

		public void produce(Context context, Conclusion conclusion);

		public boolean markAsNotSaturated(Context context);

		public void markForRemoval(Context context);

		public void clearNotSaturatedContexts();

		public void clearContextsToBeRemoved();
	}

	/**
	 * The extended writer for situations when new contexts may need to be
	 * created/initialized
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	public interface ExtendedWriter extends Writer {

		public Context getCreateContext(IndexedClassExpression root);

		public void initContext(Context context);
	}

	/**
	 * 
	 * 
	 */
	class BasicWriter implements Writer {

		private final ConclusionVisitor<?> producedConclusionVisitor_;
		
		private final ContextModificationListener contextModificationListener_;

		private BasicWriter(
				ContextModificationListener contextSaturationListener,
				ConclusionVisitor<?> conclusionVisitor) {
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
		public Context pollForContext() {
			return activeContexts_.poll();
		}

		@Override
		public void produce(Context context, Conclusion conclusion) {
			if (LOGGER_.isTraceEnabled())
				LOGGER_.trace(context + ": new conclusion " + conclusion);
			// this may be necessary, e.g., for counting produced conclusions
			conclusion.accept(producedConclusionVisitor_, context);
			
			if (context.addToDo(conclusion)) {
				// context was activated
				activeContexts_.add(context);
			}
		}

		protected void markAsNotSaturatedInternal(Context context) {
			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace(context + ": marked as non-saturated");
			}

			notSaturatedContexts_.add(context.getRoot());
			contextModificationListener_.notifyContextModification(context);
		}

		@Override
		public boolean markAsNotSaturated(Context context) {
			if (context.setSaturated(false)) {
				markAsNotSaturatedInternal(context);

				return true;
			} else {
				// LOGGER_.trace(context + " is already saturated");
				return false;
			}
		}

		@Override
		public void markForRemoval(Context context) {
			if (context.setSaturated(false)) {
				removedContexts_.add(context.getRoot());
			}
		}

		@Override
		public void clearNotSaturatedContexts() {
			if (LOGGER_.isTraceEnabled())
				LOGGER_.trace("Clear non-saturated contexts");
			notSaturatedContexts_.clear();
		}

		@Override
		public void clearContextsToBeRemoved() {
			if (LOGGER_.isTraceEnabled())
				LOGGER_.trace("Clear contexts to be removed");
			removedContexts_.clear();
		}
	}

	/**
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	class ContextCreatingWriter extends BasicWriter implements ExtendedWriter {

		private final RuleApplicationVisitor initRuleAppVisitor_;

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
				RuleApplicationVisitor ruleAppVisitor,
				ConclusionVisitor<?> conclusionVisitor,
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

			/* linking contexts */
			for (;;) {
				ContextImpl oldHead = firstContext.get();
				if (firstContext.compareAndSet(oldHead, newContext)) {
					if (oldHead != null) {
						newContext.next = oldHead;
						oldHead.previous = newContext;
					}
					break;
				}
			}

			return newContext;
		}

		@Override
		public void initContext(Context context) {
			produce(context, new PositiveSubsumer(context.getRoot()));
			// apply all context initialization rules
			LinkRule<Context> initRule = ontologyIndex_
					.getContextInitRuleHead();
			while (initRule != null) {
				initRule.accept(initRuleAppVisitor_, this, context);
				initRule = initRule.next();
			}
		}
	}

	/**
	 * A {@link Writer} that does not produce conclusions if their source
	 * context is already saturated.
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	private class SaturationCheckingWriter extends BasicWriter {
		private SaturationCheckingWriter(ConclusionVisitor<?> conclusionVisitor) {
			super(ContextModificationListener.DUMMY, conclusionVisitor);
		}

		@Override
		public void produce(Context context, Conclusion conclusion) {
			Context sourceContext = conclusion.getSourceContext(context);

			if (sourceContext == null || !sourceContext.isSaturated()) {
				super.produce(context, conclusion);
			}
		}
	}
}
