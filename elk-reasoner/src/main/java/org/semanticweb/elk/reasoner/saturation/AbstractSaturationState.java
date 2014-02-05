package org.semanticweb.elk.reasoner.saturation;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.contextinit.ContextInitRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A skeleton to simplify implementation of {@link SaturationState}s
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public abstract class AbstractSaturationState implements SaturationState {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(AbstractSaturationState.class);

	final OntologyIndex ontologyIndex;

	/**
	 * The queue containing all activated contexts. Every activated context
	 * occurs exactly once.
	 */
	private final Queue<Context> activeContexts_ = new ConcurrentLinkedQueue<Context>();

	/**
	 * The {@link Conclusion} used to initialize contexts using
	 * {@link ContextInitRule}s
	 */
	private final Conclusion ContextInitConclusion_;

	/**
	 * The queue of all contexts for which computation of the closure under
	 * inference rules has not yet been finished.
	 */
	private final Queue<IndexedClassExpression> notSaturatedContexts_ = new ConcurrentLinkedQueue<IndexedClassExpression>();

	public AbstractSaturationState(OntologyIndex index) {
		this.ontologyIndex = index;
		this.ContextInitConclusion_ = new ContextInitialization(index);
	}

	@Override
	public OntologyIndex getOntologyIndex() {
		return ontologyIndex;
	}

	@Override
	public Collection<IndexedClassExpression> getNotSaturatedContexts() {
		return notSaturatedContexts_;
	}

	@Override
	public ExtendedSaturationStateWriter getExtendedWriter(
			ContextCreationListener contextCreationListener,
			ContextModificationListener contextModificationListener,
			boolean trackNewContextsAsUnsaturated) {
		return new ExtendedWriter(contextCreationListener,
				contextModificationListener, trackNewContextsAsUnsaturated);
	}

	@Override
	public SaturationStateWriter getWriter(
			ContextModificationListener contextModificationListener) {
		return new BasicWriter(contextModificationListener);
	}

	@Override
	public SaturationStateWriter getWriter() {
		return getDefaultWriter();
	}

	@Override
	public ExtendedSaturationStateWriter getExtendedWriter() {
		return getDefaultWriter();
	}

	abstract void resetContexts();

	/**
	 * Adds the given {@link Context} to this {@link SaturationState} if no
	 * {@link Context} was assigned for its root
	 * 
	 * @param context
	 *            the {@link Context} to be added to this
	 *            {@link SaturationState}
	 * @return the {@link Context} in this {@link SaturationState} assigned to
	 *         the same root, or {@link null} if no such {@link Context} existed
	 *         before this method is called
	 * 
	 * @see Context#getRoot()
	 */
	abstract Context setIfAbsent(Context context);

	private ExtendedSaturationStateWriter getDefaultWriter() {
		return new ExtendedWriter();
	}

	/**
	 * 
	 * 
	 */
	class BasicWriter implements SaturationStateWriter {

		private final ContextModificationListener contextModificationListener_;

		private BasicWriter(
				ContextModificationListener contextSaturationListener) {
			this.contextModificationListener_ = contextSaturationListener;
		}

		@Override
		public Context pollForActiveContext() {
			Context result = activeContexts_.poll();
			return result;
		}

		public void produce(Context context, Conclusion conclusion) {
			LOGGER_.trace("{}: produced conclusion {}", context, conclusion);
			if (context.addToDo(conclusion)) {
				LOGGER_.trace("{}: activated", context);
				// context was activated
				activeContexts_.add(context);
			}
		}

		@Override
		public void produce(IndexedClassExpression root, Conclusion conclusion) {
			// TODO: what if NPE?
			produce(getContext(root), conclusion);
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
			}
			return false;
		}

		@Override
		public void clearNotSaturatedContexts() {
			LOGGER_.trace("Clear non-saturated contexts");
			notSaturatedContexts_.clear();
		}

		@Override
		public void resetContexts() {
			AbstractSaturationState.this.resetContexts();
		}

	}

	/**
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	protected class ExtendedWriter extends BasicWriter implements
			ExtendedSaturationStateWriter {

		/**
		 * If set to true, the writer will put all newly created contexts to
		 * {@link notSaturatedContexts_} This is important if saturation of a
		 * new context has been interrupted for some reason, e.g. inconsistency.
		 * Then we'll need to store all such contexts to be able to complete
		 * their saturation later.
		 */
		private final boolean trackNewContextsAsUnsaturated_;

		private final ContextCreationListener contextCreationListener_;

		protected ExtendedWriter(
				ContextCreationListener contextCreationListener,
				ContextModificationListener contextModificationListener,
				boolean trackNewContextsAsUnsaturated) {
			super(contextModificationListener);

			this.contextCreationListener_ = contextCreationListener;
			this.trackNewContextsAsUnsaturated_ = trackNewContextsAsUnsaturated;
		}

		protected ExtendedWriter() {
			super(ContextModificationListener.DUMMY);
			this.contextCreationListener_ = ContextCreationListener.DUMMY;
			this.trackNewContextsAsUnsaturated_ = true;
		}

		@Override
		public void produce(IndexedClassExpression root, Conclusion conclusion) {
			produce(getCreateContext(root), conclusion);
		}

		@Override
		public void initContext(Context context) {
			LOGGER_.trace("{}: initializing", context);
			produce(context, ContextInitConclusion_);
		}

		@Override
		public void removeContext(Context context) {
			// TODO Auto-generated method stub
		}

		@Override
		public Context getCreateContext(IndexedClassExpression root) {
			Context context = getContext(root);
			if (context != null)
				return context;
			// else try to assign a new context
			ContextImpl newContext = new ContextImpl(root);
			Context previous = setIfAbsent(newContext);
			if (previous != null)
				// the context is already assigned meanwhile
				return previous;

			// else the context is new; it should be initialized
			initContext(newContext);
			contextCreationListener_.notifyContextCreation(newContext);
			LOGGER_.trace("{}: context created", newContext);

			if (trackNewContextsAsUnsaturated_) {
				markAsNotSaturatedInternal(newContext);
			}
			return newContext;
		}

	}

}
