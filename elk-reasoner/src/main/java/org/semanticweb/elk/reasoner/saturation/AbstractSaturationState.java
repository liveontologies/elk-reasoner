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

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.OntologyIndex;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A skeleton to simplify implementation of {@link SaturationState}s
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public abstract class AbstractSaturationState<EC extends ExtendedContext> implements SaturationState<EC> {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(AbstractSaturationState.class);

	final OntologyIndex ontologyIndex;

	/**
	 * The queue containing all activated contexts (whose todo queue is not
	 * empty). Every activated context occurs exactly once.
	 */
	private final Queue<Context> activeContexts_ = new ConcurrentLinkedQueue<Context>();

	/**
	 * The queue containing all {@link Context}s of this {@link SaturationState}
	 * that are not saturated, i.e., for which {@link Context#isSaturated()}
	 * returns {@code false}. Each non-saturated context occurs exactly once.
	 */
	private final Queue<EC> notSaturatedContexts_ = new ConcurrentLinkedQueue<EC>();

	/**
	 * increments every time a {@link Context} is marked as non-saturated
	 */
	private final AtomicInteger countextMarkNonSaturatedCount_ = new AtomicInteger(
			0);

	/**
	 * increments every time a {@link Context} is marked as saturated
	 */
	private final AtomicInteger contextSetSaturatedCount_ = new AtomicInteger(0);
	
	private final ContextFactory<EC> contextFactory;

	public AbstractSaturationState(OntologyIndex index, ContextFactory<EC> factory) {
		this.ontologyIndex = index;
		this.contextFactory = factory;
	}

	@Override
	public OntologyIndex getOntologyIndex() {
		return ontologyIndex;
	}

	@Override
	public Collection<EC> getNotSaturatedContexts() {
		return Collections
				.unmodifiableCollection(new AbstractCollection<EC>() {
					@Override
					public Iterator<EC> iterator() {
						return notSaturatedContexts_.iterator();
					}

					@Override
					public int size() {
						return countextMarkNonSaturatedCount_.get()
								- contextSetSaturatedCount_.get();
					}
				});
	}

	@Override
	public int getContextMarkNonSaturatedCount() {
		return countextMarkNonSaturatedCount_.get();
	}

	@Override
	public int getContextSetSaturatedCount() {
		return contextSetSaturatedCount_.get();
	}

	@Override
	public Context setNextContextSaturated() {
		EC next = notSaturatedContexts_.poll();
		
		if (next == null)
			return null;
		// else
		if (next.setSaturated(true))
			LOGGER_.error("{}: was marked as saturated already");
		LOGGER_.trace("{}: marked as saturated", next);
		contextSetSaturatedCount_.incrementAndGet();
		return next;
	}

	@Override
	public SaturationStateWriter<EC> getContextModifyingWriter(
			ContextModificationListener contextModificationListener) {
		return new ContextModifyingWriter(contextModificationListener);
	}

	@Override
	public SaturationStateWriter<EC> getContextModifyingWriter() {
		return getContextModifyingWriter(ContextModificationListener.DUMMY);
	}

	@Override
	public ContextCreatingSaturationStateWriter<EC> getContextCreatingWriter(
			ContextCreationListener contextCreationListener,
			ContextModificationListener contextModificationListener) {
		return new AbstractContextCreatingWriter(contextCreationListener,
				contextModificationListener);
	}

	@Override
	public ContextCreatingSaturationStateWriter<EC> getContextCreatingWriter() {
		return getContextCreatingWriter(ContextCreationListener.DUMMY,
				ContextModificationListener.DUMMY);
	}

	@Override
	abstract public EC getContext(IndexedClassExpression ice);

	abstract void resetContexts();

	/**
	 * Adds the given {@link ExtendedContext} to this {@link SaturationState} if
	 * no {@link ExtendedContext} was assigned for its root
	 * 
	 * @param context
	 *            the {@link ExtendedContext} to be added to this
	 *            {@link SaturationState}
	 * @return the {@link ExtendedContext} in this {@link SaturationState}
	 *         assigned to the same root, or {@link null} if no such
	 *         {@link Context} existed before this method is called
	 * 
	 * @see ExtendedContext#getRoot()
	 */
	abstract EC setIfAbsent(EC context);

	/**
	 * 
	 * 
	 */
	class ContextModifyingWriter implements SaturationStateWriter<EC> {

		private final ContextModificationListener contextModificationListener_;

		private ContextModifyingWriter(
				ContextModificationListener contextSaturationListener) {
			this.contextModificationListener_ = contextSaturationListener;
		}

		@Override
		public Context pollForActiveContext() {
			return activeContexts_.poll();
		}

		void produce(Context context, Conclusion conclusion) {
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

		void markAsNotSaturatedInternal(EC context) {
			LOGGER_.trace("{}: marked as non-saturated", context);
			notSaturatedContexts_.add(context);
			countextMarkNonSaturatedCount_.incrementAndGet();
			contextModificationListener_.notifyContextModification(context);
		}

		@Override
		public boolean markAsNotSaturated(IndexedClassExpression root) {
			EC context = getContext(root);
			
			if (context == null)
				return false;
			// else
			if (context.setSaturated(false)) {
				markAsNotSaturatedInternal(context);
				return true;
			}
			// else
			return false;
		}

		@Override
		public void resetContexts() {
			AbstractSaturationState.this.resetContexts();
		}

		@Override
		public SaturationState<EC> getSaturationState() {
			return AbstractSaturationState.this;
		}

	}

	/**
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 * @author "Yevgeny Kazakov"
	 */
	protected class AbstractContextCreatingWriter extends ContextModifyingWriter
			implements ContextCreatingSaturationStateWriter<EC> {

		private final ContextCreationListener contextCreationListener_;

		protected AbstractContextCreatingWriter(
				ContextCreationListener contextCreationListener,
				ContextModificationListener contextModificationListener) {
			super(contextModificationListener);

			this.contextCreationListener_ = contextCreationListener;
		}

		protected AbstractContextCreatingWriter() {
			super(ContextModificationListener.DUMMY);
			this.contextCreationListener_ = ContextCreationListener.DUMMY;
		}

		@Override
		public void produce(IndexedClassExpression root, Conclusion conclusion) {
			produce(getCreateContext(root), conclusion);
		}

		@Override
		public EC getCreateContext(IndexedClassExpression root) {
			EC previous = getContext(root);
			
			if (previous != null) {
				return previous;
			}
			// else try to assign a new context
			EC newContext = contextFactory.createContext(root);
			
			previous = setIfAbsent(newContext);
			
			if (previous != null) {
				// the context is already assigned meanwhile
				return previous;
			}

			// markAsNotSaturatedInternal(newContext);
			contextCreationListener_.notifyContextCreation(newContext);
			LOGGER_.trace("{}: context created", newContext);
			
			return newContext;
		}
		
	}

}
