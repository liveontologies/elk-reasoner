/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.consistency;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.semanticweb.elk.reasoner.indexing.classes.OntologyIndexDummyChangeListener;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassEntity;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.model.IndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.model.OntologyIndex;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStateDummyChangeListener;
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.SaturationConclusionBaseFactory;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassInconsistency;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.stages.PropertyHierarchyCompositionState;
import org.semanticweb.elk.util.collections.Operations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stores information about the state of consistency checking computation
 * 
 * @author Yevgeny Kazakov
 * @author Peter Skocovsky
 */
public class ConsistencyCheckingState {

	// logger for this class
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ConsistencyCheckingState.class);

	/**
	 * the saturation state of the reasoner
	 */
	private final SaturationState<?> saturationState_;

	/**
	 * {@code true} if the ontology is consistent due to some syntactic
	 * sufficient conditions
	 */
	private boolean isTriviallyConsistent_ = false;

	/**
	 * {@code true} if inconsistency is derived for {@code owl:Thing}
	 */
	private volatile boolean isOwlThingInconsistent_ = false;
	/**
	 * {@code true} if inconsistency is derived for
	 * {@code owl:bottomObjectProperty}
	 */
	private volatile boolean isTopObjectPropertyInBottom_ = false;
	/**
	 * the set of individuals for which inconsistency is derived
	 */
	private final Set<IndexedIndividual> inconsistentIndividuals_ = Collections
			.synchronizedSet(new HashSet<IndexedIndividual>());
	/**
	 * the entities for which consistency needs to be checked
	 */
	private final Queue<IndexedClassEntity> toDoEntities_;

	private <C extends Context> ConsistencyCheckingState(
			SaturationState<C> saturationState,
			final PropertyHierarchyCompositionState propertHierarchyState) {
		this.saturationState_ = saturationState;
		final OntologyIndex index = saturationState.getOntologyIndex();
		toDoEntities_ = new ConcurrentLinkedQueue<IndexedClassEntity>(
				index.getIndividuals());
		toDoEntities_.add(index.getOwlThing());
		isTriviallyConsistent_ = !index.hasPositiveOwlNothing();
		// listening to changes in the ontology
		index.addListener(new OntologyIndexDummyChangeListener() {

			@Override
			public void individualAddition(IndexedIndividual ind) {
				toDoEntities_.add(ind);
			}

			@Override
			public void individualRemoval(IndexedIndividual ind) {
				inconsistentIndividuals_.remove(ind);
			}

			@Override
			public void positiveOwlNothingAppeared() {
				isTriviallyConsistent_ = false;
			}

			@Override
			public void positiveOwlNothingDisappeared() {
				isTriviallyConsistent_ = true;
			}

		});
		// listening to changes in the saturation state
		saturationState
				.addListener(new SaturationStateDummyChangeListener<C>() {

					private final IndexedClass owlThing_ = index.getOwlThing();

					private final ClassInconsistency.Factory factory_ = new SaturationConclusionBaseFactory();

					@Override
					public void contextsClear() {						
						toDoEntities_.addAll(index.getIndividuals());
						toDoEntities_.add(owlThing_);
						inconsistentIndividuals_.clear();
						isOwlThingInconsistent_ = false;
					}

					@Override
					public void contextMarkNonSaturated(C context) {
						IndexedContextRoot root = context.getRoot();
						if (root instanceof IndexedIndividual) {
							IndexedIndividual ind = (IndexedIndividual) root;
							inconsistentIndividuals_.remove(ind);
							toDoEntities_.add(ind);
						} else if (root == owlThing_) {
							isOwlThingInconsistent_ = false;
							toDoEntities_.add(owlThing_);
						}
					}

					@Override
					public void contextMarkSaturated(C context) {

						IndexedContextRoot root = context.getRoot();
						if (!context.containsConclusion(
								factory_.getContradiction(root))) {
							return;
						}
						// else
						if (root instanceof IndexedIndividual) {
							tellInconsistentIndividual(
									(IndexedIndividual) root);
						} else if (root == owlThing_) {
							tellInconsistentOwlThing();
						}

					}

				});
		// listening to changes in the property hierarchy state
		propertHierarchyState
				.addListener(new PropertyHierarchyCompositionState.Listener() {

					private final IndexedObjectProperty bottomProperty_ = index
							.getOwlBottomObjectProperty();

					private final IndexedObjectProperty topProperty_ = index
							.getOwlTopObjectProperty();

					@Override
					public void propertyBecameSaturated(
							final IndexedPropertyChain chain) {
						if (bottomProperty_ == chain && chain.getSaturated()
								.getSubProperties().contains(topProperty_)) {
							tellTopObjectPropertyInBottom();
						}
					}

					@Override
					public void propertyBecameNotSaturated(
							final IndexedPropertyChain chain) {
						if (bottomProperty_ == chain) {
							isTopObjectPropertyInBottom_ = false;
						}
					}

				});
	}

	/**
	 * @param saturationState
	 * @return a new {@link ConsistencyCheckingState} associated with the given
	 *         {@link SaturationState}
	 */
	public static ConsistencyCheckingState create(
			SaturationState<?> saturationState,
			final PropertyHierarchyCompositionState propertHierarchyState) {
		return new ConsistencyCheckingState(saturationState,
				propertHierarchyState);
	}

	/**
	 * Removes from {@link #toDoEntities_} the entities which no longer occur in
	 * the ontology or for the context is already saturated (thus consistency is
	 * already checked)
	 * 
	 * @return the number of the remaining entries in {@link #toDoEntities_}
	 */
	private int pruneToDo() {
		int size = 0;
		Iterator<IndexedClassEntity> itr = toDoEntities_.iterator();
		while (itr.hasNext()) {
			IndexedClassEntity next = itr.next();
			if (!next.occurs()) {
				itr.remove();
				continue;
			}
			// else
			Context context = saturationState_.getContext(next);
			if (context != null && context.isSaturated()) {
				itr.remove();
			} else {
				size++;
			}
		}
		return size;
	}

	public Collection<? extends IndexedClassEntity> getTestEntitites() {
		if (isTriviallyConsistent_) {
			return Collections.emptyList();
		}
		int size = pruneToDo();
		// since getting the size of the queue is a linear operation,
		// use the computed size
		return Operations.getCollection(toDoEntities_, size);
	}

	/**
	 * tells that {@code owl:Thing} is inconsistent in the
	 * {@link SaturationState}, i.e., its context is saturated and contains
	 * {@link ClassInconsistency}
	 */
	private void tellInconsistentOwlThing() {
		isOwlThingInconsistent_ = true;
		LOGGER_.trace("owl:Thing inconsistent");
	}

	/**
	 * tells that derived sub-properties of {@code owl:bottomObjectProperty}
	 * contain {@code owl:topObjectProperty}
	 */
	private void tellTopObjectPropertyInBottom() {
		isTopObjectPropertyInBottom_ = true;
		LOGGER_.trace(
				"owl:topObjectProperty is a sub-property of owl:bottomObjectProperty");
	}

	/**
	 * tells that the given {@link IndexedIndividual} is inconsistent in the
	 * {@link SaturationState},
	 * 
	 * @param ind
	 */
	private void tellInconsistentIndividual(IndexedIndividual ind) {
		inconsistentIndividuals_.add(ind);
		LOGGER_.trace("{} inconsistent", ind);
	}

	/**
	 * @return {@code true} if the ontology is currently in the inconsistent
	 *         state
	 */
	public boolean isInconsistent() {
		return isOwlThingInconsistent_ || isTopObjectPropertyInBottom_
				|| !inconsistentIndividuals_.isEmpty();
	}

	/**
	 * @return {@code true} if inconsistency for {@code owl:Thing} is derived
	 */
	public boolean isOwlThingInconsistent() {
		return isOwlThingInconsistent_;
	}

	/**
	 * @return {@code true} if inconsistency is derived from object property
	 *         hierarchy
	 */
	public boolean isTopObjectPropertyInBottom() {
		return isTopObjectPropertyInBottom_;
	}

	/**
	 * @return all individuals for which inconsistency is derived
	 */
	public Collection<? extends IndexedIndividual> getInconsistentIndividuals() {
		return inconsistentIndividuals_;
	}

}
