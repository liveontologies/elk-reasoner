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

import org.liveontologies.puli.Proof;
import org.semanticweb.elk.reasoner.entailments.impl.IndividualInconsistencyEntailsOntologyInconsistencyImpl;
import org.semanticweb.elk.reasoner.entailments.impl.OntologyInconsistencyImpl;
import org.semanticweb.elk.reasoner.entailments.impl.OwlThingInconsistencyEntailsOntologyInconsistencyImpl;
import org.semanticweb.elk.reasoner.entailments.impl.TopObjectPropertyInBottomEntailsOntologyInconsistencyImpl;
import org.semanticweb.elk.reasoner.entailments.model.EntailmentInference;
import org.semanticweb.elk.reasoner.entailments.model.OntologyInconsistencyEntailmentInference;
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
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SaturationConclusion;
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

	private final SaturationConclusion.Factory conclusionFactory_ = new SaturationConclusionBaseFactory();

	private final IndexedClass owlThing_;

	private final IndexedObjectProperty bottomProperty_;

	private final IndexedObjectProperty topProperty_;

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
		this.owlThing_ = index.getOwlThing();
		this.bottomProperty_ = index.getOwlBottomObjectProperty();
		this.topProperty_ = index.getOwlTopObjectProperty();
		toDoEntities_ = new ConcurrentLinkedQueue<IndexedClassEntity>(
				index.getIndividuals());
		toDoEntities_.add(index.getOwlThing());		
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

		});
		// listening to changes in the saturation state
		saturationState
				.addListener(new SaturationStateDummyChangeListener<C>() {

					@Override
					public void contextsClear() {
						toDoEntities_.addAll(index.getIndividuals());
						toDoEntities_.add(owlThing_);
						inconsistentIndividuals_.clear();
						isOwlThingInconsistent_ = false;
					}

					@Override
					public void contextMarkedNonSaturated(C context) {
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
					public void contextMarkedSaturated(C context) {

						IndexedContextRoot root = context.getRoot();
						if (!context.containsConclusion(
								conclusionFactory_.getContradiction(root))) {
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
	 *            a {@link SaturationState} containing information about derived
	 *            class axioms
	 * @param propertHierarchyState
	 *            a {@link PropertyHierarchyCompositionState} containing
	 *            information about derived property axioms
	 * 
	 * @return a new {@link ConsistencyCheckingState} associated with the given
	 *         {@link SaturationState} and {@link PropertyHierarchyCompositionState}
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
				continue;
			}
			size++;
		}
		return size;
	}
	
	public Collection<? extends IndexedClassEntity> getTestEntitites() {
		int size = pruneToDo();
		// since getting the size of the queue is not a linear operation,
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

	/**
	 * Explains why an ontology inconsistency is entailed. If it is not
	 * entailed, the returned proof is empty.
	 * 
	 * @param atMostOne
	 *            Whether at most one explanation should be returned.
	 * @return An evidence of entailment of ontology inconsistency.
	 */
	public Proof<? extends EntailmentInference> getEvidence(
			final boolean atMostOne) {

		return new Proof<EntailmentInference>() {

			@Override
			public Collection<OntologyInconsistencyEntailmentInference> getInferences(
					final Object conclusion) {

				if (!OntologyInconsistencyImpl.INSTANCE.equals(conclusion)) {
					return Collections.emptyList();
				}
				// else

				final Collection<? extends IndexedIndividual> inconsistentIndividuals = getInconsistentIndividuals();
				Iterable<OntologyInconsistencyEntailmentInference> result = Operations
						.map(inconsistentIndividuals,
								INDIVIDUAL_TO_ENTAILMENT_INFERENCE);
				int size = inconsistentIndividuals.size();

				if (isTopObjectPropertyInBottom_) {
					result = Operations.concat(Operations
							.<OntologyInconsistencyEntailmentInference> singleton(
									new TopObjectPropertyInBottomEntailsOntologyInconsistencyImpl(
											conclusionFactory_
													.getSubPropertyChain(
															topProperty_,
															bottomProperty_))),
							result);
					size++;
				}

				if (isOwlThingInconsistent_) {
					result = Operations.concat(Operations
							.<OntologyInconsistencyEntailmentInference> singleton(
									new OwlThingInconsistencyEntailsOntologyInconsistencyImpl(
											conclusionFactory_.getContradiction(
													owlThing_))),
							result);
					size++;
				}

				if (atMostOne) {
					final Iterator<OntologyInconsistencyEntailmentInference> iter = result
							.iterator();
					if (!iter.hasNext()) {
						return Collections.emptyList();
					}
					// else
					return Collections.singleton(iter.next());
				}
				// else

				return Operations.getCollection(result, size);
			}

		};

	}

	private final Operations.Transformation<IndexedIndividual, OntologyInconsistencyEntailmentInference> INDIVIDUAL_TO_ENTAILMENT_INFERENCE = new Operations.Transformation<IndexedIndividual, OntologyInconsistencyEntailmentInference>() {

		@Override
		public OntologyInconsistencyEntailmentInference transform(
				final IndexedIndividual ind) {
			return new IndividualInconsistencyEntailsOntologyInconsistencyImpl(
					conclusionFactory_.getContradiction(ind),
					ind.getElkEntity());
		}

	};

}
