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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.model.OntologyIndex;
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.DummyClassConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.SaturationClassConclusionChecker;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A skeleton to simplify implementation of {@link SaturationState}s
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <EC>
 *            the type of contexts maintained by this {@link SaturationState}
 */
public abstract class AbstractSaturationState<EC extends ExtendedContext>
		implements SaturationState<EC> {

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
	 * increments after a {@link Context} is marked as non-saturated
	 */
	private final AtomicInteger contextMarkNonSaturatedLower_ = new AtomicInteger(
			0);

	/**
	 * increments before a {@link Context} is marked as saturated
	 */
	private final AtomicInteger contextSetSaturatedUpper_ = new AtomicInteger(
			0);

	/**
	 * increments after a {@link Context} is marked as saturated
	 */
	private final AtomicInteger contextSetSaturatedLower_ = new AtomicInteger(
			0);

	private final ContextFactory<EC> contextFactory;

	private final List<SaturationState.ChangeListener<EC>> listeners_ = new ArrayList<>();

	public AbstractSaturationState(OntologyIndex index,
			ContextFactory<EC> factory) {
		this.ontologyIndex = index;
		this.contextFactory = factory;
	}

	@Override
	public OntologyIndex getOntologyIndex() {
		return ontologyIndex;
	}

	@Override
	public Collection<EC> getNotSaturatedContexts() {
		return Collections.unmodifiableCollection(new AbstractCollection<EC>() {
			@Override
			public Iterator<EC> iterator() {
				return notSaturatedContexts_.iterator();
			}

			@Override
			public int size() {
				return contextMarkNonSaturatedLower_.get()
						- contextSetSaturatedLower_.get();
			}
		});
	}

	@Override
	public int getContextMarkNonSaturatedCount() {
		return contextMarkNonSaturatedLower_.get();
	}

	@Override
	public int getContextSetSaturatedCount() {
		return contextSetSaturatedLower_.get();
	}

	@Override
	public void setContextsSaturated(int saturatedContextLimit) {
		for (;;) {
			int contextSetSaturatedUpperSnapshot = contextSetSaturatedUpper_
					.get();
			if (contextSetSaturatedUpperSnapshot >= saturatedContextLimit) {
				return;
			}
			if (contextSetSaturatedUpperSnapshot >= contextMarkNonSaturatedLower_
					.get()) {
				EC next = notSaturatedContexts_.peek();
				if (next != null) {
					LOGGER_.error("{}: was not marked as saturated", next);
				}
				return;
			}
			if (!contextSetSaturatedUpper_.compareAndSet(
					contextSetSaturatedUpperSnapshot,
					contextSetSaturatedUpperSnapshot + 1)) {
				continue;
			}
			EC next = notSaturatedContexts_.poll();
			if (next.setSaturated(true)) {
				LOGGER_.error("{}: was marked as saturated already", next);
			}
			LOGGER_.trace("{}: marked as saturated", next);
			contextSetSaturatedLower_.incrementAndGet();
			notifyContextMarkedSaturated(next);
		}
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
		return new ContextCreatingWriter(contextCreationListener,
				contextModificationListener);
	}

	@Override
	public ContextCreatingSaturationStateWriter<EC> getContextCreatingWriter() {
		return getContextCreatingWriter(ContextCreationListener.DUMMY,
				ContextModificationListener.DUMMY);
	}

	@Override
	public boolean addListener(SaturationState.ChangeListener<EC> listener) {
		return listeners_.add(listener);
	}

	@Override
	public boolean removeListener(SaturationState.ChangeListener<EC> listener) {
		return listeners_.remove(listener);
	}

	@Override
	abstract public EC getContext(IndexedContextRoot root);
	
	void notifyContextsClear() {
		for (int i = 0; i < listeners_.size(); i++) {
			listeners_.get(i).contextsClear();
		}
	}
	
	void notifyContextAddition(EC context) {
		for (int i = 0; i < listeners_.size(); i++) {
			listeners_.get(i).contextAddition(context);
		}
	}	
	
	void notifyContextMarkedSaturated(EC context) {
		for (int i = 0; i < listeners_.size(); i++) {
			listeners_.get(i).contextMarkedSaturated(context);
		}
	}	
	
	void notifyContextMarkedNonSaturated(EC context) {
		for (int i = 0; i < listeners_.size(); i++) {
			listeners_.get(i).contextMarkedNonSaturated(context);
		}
	}
	
	void notifySaturatedContextModified(EC context) {
		for (int i = 0; i < listeners_.size(); i++) {
			listeners_.get(i).saturatedContextModified(context);
		}
	}	

	abstract void resetContexts();

	private void reset() {
		resetContexts();
		activeContexts_.clear();
		notSaturatedContexts_.clear();
		contextMarkNonSaturatedLower_.set(0);
		contextSetSaturatedUpper_.set(0);
		contextSetSaturatedLower_.set(0);
	}

	/**
	 * Adds the given {@link ExtendedContext} to this {@link SaturationState} if
	 * no {@link ExtendedContext} was assigned for its root
	 * 
	 * @param context
	 *            the {@link ExtendedContext} to be added to this
	 *            {@link SaturationState}
	 * @return the {@link ExtendedContext} in this {@link SaturationState} that
	 *         was assigned to the same root, or {@link null} if no such
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

		void produce(Context context, ClassInference inference) {
			LOGGER_.trace("{}: new inference {}", context, inference);
			if (context.addToDo(inference)) {
				LOGGER_.trace("{}: activated", context);
				// context was activated
				activeContexts_.add(context);
			}
		}

		@Override
		public void produce(ClassInference inference) {
			// TODO: what if NPE?
			produce(getContext(inference.getDestination()), inference);
		}

		void addNotSaturated(EC context) {
			LOGGER_.trace("{}: marked as non-saturated", context);
			notSaturatedContexts_.add(context);
			contextMarkNonSaturatedLower_.incrementAndGet();
			contextModificationListener_.notifyContextModification(context);
			notifyContextMarkedNonSaturated(context);
		}

		boolean markAsNotSaturated(EC context) {
			if (context.setSaturated(false)) {
				addNotSaturated(context);
				return true;
			}
			// else
			return false;
		}
		
		boolean markAsNotSaturated(IndexedContextRoot root) {
			EC context = getContext(root);
			return context == null ? false : markAsNotSaturated(context);
		}

		@Override
		public void resetContexts() {
			AbstractSaturationState.this.reset();
		}

		@Override
		public SaturationState<EC> getSaturationState() {
			return AbstractSaturationState.this;
		}
		
		@Override
		public boolean addConclusion(ClassConclusion conclusion) {
			EC context = getContext(conclusion.getDestination());
			return conclusion
					.accept(new DummyClassConclusionVisitor<Boolean>() {
						@Override
						protected Boolean defaultVisit(ClassConclusion c) {
							return addConclusion(context, c);
						}

						@Override
						public Boolean visit(ContextInitialization c) {
							
							if (context.containsConclusion(c)) {
								return false;
							}
							// Mark context as non-saturated before we
							// insert, otherwise the context could be found
							// initialized and non-saturated
							markAsNotSaturated(context);
							return addConclusion(context, c);
						}
					});
		}

		boolean addConclusion(EC context, ClassConclusion conclusion) {			
			if (!context.addConclusion(conclusion)) {
				LOGGER_.trace("{}: inserting {}: already present", context,
						conclusion);
				return false;
			}
			// else
			LOGGER_.trace("{}: inserting {}: success", context, conclusion);
			checkSaturationFor(conclusion);
			return true;
		}
		
		@Override
		public boolean removeConclusion(ClassConclusion conclusion) {
			EC context = getContext(conclusion.getDestination());
			if (!context.removeConclusion(conclusion)) {
				LOGGER_.trace("{}: deleting {}: already absent", context,
						conclusion);
				return false;
			}
			// else
			LOGGER_.trace("{}: deleting {}: success", context, conclusion);
			checkSaturationFor(conclusion);
			return true;
		}
		
		/**
		 * Checks if adding or removing the given conclusion can make some
		 * context non-saturated.
		 * 
		 * @param conclusion
		 */
		void checkSaturationFor(ClassConclusion conclusion) {
			EC origin = getContext(conclusion.getTraceRoot());
			if (!origin.isSaturated()) {
				return;
			}
			if (SaturationClassConclusionChecker.check(conclusion)) {
				markAsNotSaturated(origin);
			} else {
				notifySaturatedContextModified(origin);
			}
		}

	}

	/**
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 * @author "Yevgeny Kazakov"
	 */
	protected class ContextCreatingWriter extends ContextModifyingWriter
			implements ContextCreatingSaturationStateWriter<EC> {

		private final ContextCreationListener contextCreationListener_;

		protected ContextCreatingWriter(
				ContextCreationListener contextCreationListener,
				ContextModificationListener contextModificationListener) {
			super(contextModificationListener);

			this.contextCreationListener_ = contextCreationListener;
		}

		protected ContextCreatingWriter() {
			super(ContextModificationListener.DUMMY);
			this.contextCreationListener_ = ContextCreationListener.DUMMY;
		}

		@Override
		public void produce(ClassInference inference) {
			produce(getCreateContext(inference.getDestination()), inference);
		}

		@Override
		public EC getCreateContext(IndexedContextRoot root) {
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
