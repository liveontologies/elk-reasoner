/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.rules;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedBinaryPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDataHasValue;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedNominal;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturationEngine;
import org.semanticweb.elk.util.collections.HashSetMultimap;
import org.semanticweb.elk.util.collections.LazySetIntersection;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;

/**
 * The engine for computing the saturation of class expressions. This is the
 * class that implements the application of inference rules.
 * 
 * @author Frantisek Simancik
 * @author Yevgeny Kazakov
 * 
 */
public class RuleApplicationEngine implements
		InputProcessor<IndexedClassExpression> {

	// Statistical information
	AtomicInteger derivedNo = new AtomicInteger(0);
	AtomicInteger derivedInfNo = new AtomicInteger(0);
	AtomicInteger backLinkNo = new AtomicInteger(0);
	AtomicInteger backLinkInfNo = new AtomicInteger(0);
	AtomicInteger propNo = new AtomicInteger(0);
	AtomicInteger propInfNo = new AtomicInteger(0);
	AtomicInteger forwLinkNo = new AtomicInteger(0);
	AtomicInteger forwLinkInfNo = new AtomicInteger(0);

	protected final static Logger LOGGER_ = Logger
			.getLogger(ClassExpressionSaturationEngine.class);

	/**
	 * The listener for rule application callbacks
	 */
	protected final RuleApplicationListener listener;

	// TODO: try to get rid of the ontology index, if possible
	/**
	 * The index used for executing the rules
	 */
	protected final OntologyIndex ontologyIndex;

	/**
	 * Cached constants
	 */
	protected final IndexedClassExpression owlThing, owlNothing;

	/**
	 * The queue containing all activated contexts. Every activated context
	 * occurs exactly once.
	 */
	protected final Queue<SaturatedClassExpression> activeContexts;
	/**
	 * The number of contexts ever created by this engine
	 */
	protected final AtomicInteger contextNo = new AtomicInteger(0);

	/**
	 * <tt>true</tt> if the {@link #activeContexts} queue is empty
	 */
	protected final AtomicBoolean activeContextsEmpty;

	/**
	 * if <tt>true</tt> the rules delete results instead of adding them
	 */
	protected final boolean deletionMode;

	/**
	 * if <tt>true</tt>, saturated contexts are marked as un-saturated
	 */
	protected final boolean unSaturationMode;

	/**
	 * In case of the {@link #unSaturationMode} is <tt>true</tt>, all contexts
	 * that are unsaturated are stored in this queue
	 */
	protected final Queue<SaturatedClassExpression> unSaturatedContexts;

	public RuleApplicationEngine(OntologyIndex ontologyIndex,
			RuleApplicationListener listener, boolean deletionMode,
			boolean unSaturationMode) {
		this.ontologyIndex = ontologyIndex;
		this.listener = listener;
		this.activeContexts = new ConcurrentLinkedQueue<SaturatedClassExpression>();
		this.activeContextsEmpty = new AtomicBoolean(true);
		this.deletionMode = deletionMode;
		this.unSaturationMode = unSaturationMode;
		this.unSaturatedContexts = new ConcurrentLinkedQueue<SaturatedClassExpression>();

		// reset saturation in case of re-saturation after changes
		// TODO: introduce a separate method for this
		// for (IndexedClassExpression ice : ontologyIndex
		// .getIndexedClassExpressions())
		// ice.resetSaturated();

		owlThing = ontologyIndex.getIndexed(PredefinedElkClass.OWL_THING);
		owlNothing = ontologyIndex.getIndexed(PredefinedElkClass.OWL_NOTHING);

	}

	public RuleApplicationEngine(OntologyIndex ontologyIndex,
			RuleApplicationListener listener) {
		this(ontologyIndex, listener, false, false);
	}

	public void submit(IndexedClassExpression job) {
		getCreateContext(job);
	}

	public void process() throws InterruptedException {
		processActiveContexts();
	}

	public boolean canProcess() {
		return !activeContextsEmpty.get();
	}

	/**
	 * Returns the total number of contexts created
	 * 
	 * @return number of created contexts
	 */
	public int getContextNo() {
		return contextNo.get();
	}

	/**
	 * Return the queue of contexts that have been un-saturated in case of the
	 * deletion phase
	 */
	public Queue<SaturatedClassExpression> getUnSaturatedContexts() {
		return this.unSaturatedContexts;
	}

	/**
	 * Prints statistic of rule applications
	 */
	public void printStatistics() {
		if (LOGGER_.isDebugEnabled()) {
			LOGGER_.debug("Contexts created:" + contextNo.get());
			LOGGER_.debug("Derived Produced/Unique:" + derivedInfNo.get() + "/"
					+ derivedNo.get());
			LOGGER_.debug("Backward Links Produced/Unique:"
					+ backLinkInfNo.get() + "/" + backLinkNo.get());
			LOGGER_.debug("Propagations Produced/Unique:" + propInfNo.get()
					+ "/" + propNo.get());
			LOGGER_.debug("Forward Links Produced/Unique:"
					+ forwLinkInfNo.get() + "/" + forwLinkNo.get());
		}
	}

	protected void tryNotifyCanProcess() {
		if (activeContextsEmpty.compareAndSet(true, false))
			listener.notifyCanProcess();
	}

	/**
	 * Return the context which has the input indexed class expression as a
	 * root. In case no such context exists, a new one is created with the given
	 * root and is returned. It is ensured that no two different contexts are
	 * created with the same root. In case a new context is created, it is
	 * scheduled to be processed.
	 * 
	 * @param root
	 *            the input indexed class expression for which to return the
	 *            context having it as a root
	 * @return context which root is the input indexed class expression.
	 * 
	 */
	protected SaturatedClassExpression getCreateContext(
			IndexedClassExpression root) {
		if (root.getSaturated() == null) {
			SaturatedClassExpression sce = new SaturatedClassExpression(root);
			if (root.setSaturated(sce)) {
				if (LOGGER_.isTraceEnabled()) {
					LOGGER_.trace(root + ": context created");
				}
				contextNo.incrementAndGet();
				initContext(sce);
			}
		}
		return root.getSaturated();
	}

	protected void initContext(SaturatedClassExpression sce) {
		enqueue(sce, sce.getRoot());

		if (owlThing.occursNegatively())
			enqueue(sce, owlThing);
	}

	protected void activateContext(SaturatedClassExpression context) {
		if (context.tryActivate()) {
			activeContexts.add(context);
			tryNotifyCanProcess();
		}
	}

	protected void deactivateContext(SaturatedClassExpression context) {
		if (context.tryDeactivate())
			if (!context.queue.isEmpty())
				activateContext(context);
	}

	protected void enqueue(Linkable target, Queueable item) {

		/*
		 * so far SaturatedClassExpression is the only implementation of
		 * Linkable
		 */
		if (target instanceof SaturatedClassExpression) {
			SaturatedClassExpression context = (SaturatedClassExpression) target;
			context.queue.add(item);
			activateContext(context);
		}
	}

	protected void processActiveContexts() throws InterruptedException {
		for (;;) {
			SaturatedClassExpression nextContext = activeContexts.poll();
			if (nextContext == null) {
				if (!activeContextsEmpty.compareAndSet(false, true))
					return;
				nextContext = activeContexts.poll();
				if (nextContext == null)
					return;
				tryNotifyCanProcess();
			}
			process(nextContext);
		}
	}

	protected void process(SaturatedClassExpression context) {

		final QueueProcessor queueProcessor = new QueueProcessor(context);

		for (;;) {
			Queueable item = context.queue.poll();
			if (item == null)
				break;
			item.accept(queueProcessor);
		}

		deactivateContext(context);
	}

	protected class QueueProcessor implements QueueableVisitor<Void> {
		final SaturatedClassExpression context;

		public QueueProcessor(SaturatedClassExpression context) {
			this.context = context;
		}

		public Void visit(BackwardLink backwardLink) {

			Linkable target = backwardLink.getTarget();

			// for cleaning up don't change unaffected contexts
			if (deletionMode && !unSaturationMode
					&& (target instanceof SaturatedClassExpression)
					&& ((SaturatedClassExpression) target).isSaturated()) {
				return null;
			}

			IndexedPropertyChain linkRelation = backwardLink.getRelation();
			backLinkInfNo.incrementAndGet();

			if (context.backwardLinksByObjectProperty == null) {
				context.backwardLinksByObjectProperty = new HashSetMultimap<IndexedPropertyChain, Linkable>();

				// start deriving propagations
				for (IndexedClassExpression ice : context.derived)
					if (ice.getNegExistentials() != null)
						for (IndexedObjectSomeValuesFrom e : ice
								.getNegExistentials())
							new Propagation(e.getRelation(),
									new DecomposedClassExpression(e))
									.accept(this);
			}

			boolean updated = deletionMode ? context.backwardLinksByObjectProperty
					.contains(linkRelation, target)
					: context.backwardLinksByObjectProperty.add(linkRelation,
							target);

			if (!updated)
				return null;

			backLinkNo.incrementAndGet();

			// apply all propagations over the link
			if (context.propagationsByObjectProperty != null) {

				for (IndexedPropertyChain propRelation : new LazySetIntersection<IndexedPropertyChain>(
						linkRelation.getSaturated().getSuperProperties(),
						context.propagationsByObjectProperty.keySet()))

					for (Queueable carry : context.propagationsByObjectProperty
							.get(propRelation))

						enqueue(target, carry);
			}

			/*
			 * if deriveBackwardLinks, then add a forward copy of the link to
			 * consider the link in property compositions
			 */
			if (context.deriveBackwardLinks
					&& linkRelation.getSaturated().propertyCompositionsByLeftSubProperty != null)
				enqueue(target, new ForwardLink(linkRelation, context));

			/* compose the link with all forward links */
			if (linkRelation.getSaturated().propertyCompositionsByRightSubProperty != null
					&& context.forwardLinksByObjectProperty != null) {

				for (IndexedPropertyChain forwardRelation : new LazySetIntersection<IndexedPropertyChain>(
						linkRelation.getSaturated().propertyCompositionsByRightSubProperty
								.keySet(), context.forwardLinksByObjectProperty
								.keySet())) {

					Collection<IndexedBinaryPropertyChain> compositions = linkRelation
							.getSaturated().propertyCompositionsByRightSubProperty
							.get(forwardRelation);
					Collection<Linkable> forwardTargets = context.forwardLinksByObjectProperty
							.get(forwardRelation);

					for (IndexedPropertyChain composition : compositions)
						for (Linkable forwardTarget : forwardTargets)
							enqueue(forwardTarget, new BackwardLink(
									composition, target));
				}
			}

			if (deletionMode)
				context.backwardLinksByObjectProperty.remove(linkRelation,
						target);

			return null;
		}

		public Void visit(ForwardLink forwardLink) {

			// for cleaning up don't change unaffected contexts
			if (deletionMode && !unSaturationMode && context.isSaturated()) {
				return null;
			}

			forwLinkInfNo.incrementAndGet();
			IndexedPropertyChain linkRelation = forwardLink.getRelation();
			Linkable target = forwardLink.getTarget();

			if (context.forwardLinksByObjectProperty == null) {
				context.forwardLinksByObjectProperty = new HashSetMultimap<IndexedPropertyChain, Linkable>();
				initializeDerivationOfBackwardLinks();
			}

			boolean updated = deletionMode ? context.forwardLinksByObjectProperty
					.contains(linkRelation, target)
					: context.forwardLinksByObjectProperty.add(linkRelation,
							target);

			if (!updated)
				return null;

			forwLinkNo.incrementAndGet();

			if (unSaturationMode && context.isSaturated()) {
				if (context.setNotSaturated())
					unSaturatedContexts.add(context);
			}

			/* compose the link with all backward links */
			assert linkRelation.getSaturated().propertyCompositionsByLeftSubProperty != null;
			if (context.backwardLinksByObjectProperty != null) {

				for (IndexedPropertyChain backwardRelation : new LazySetIntersection<IndexedPropertyChain>(
						linkRelation.getSaturated().propertyCompositionsByLeftSubProperty
								.keySet(),
						context.backwardLinksByObjectProperty.keySet())) {

					Collection<IndexedBinaryPropertyChain> compositions = linkRelation
							.getSaturated().propertyCompositionsByLeftSubProperty
							.get(backwardRelation);
					Collection<Linkable> backwardTargets = context.backwardLinksByObjectProperty
							.get(backwardRelation);

					for (IndexedPropertyChain composition : compositions)
						for (Linkable backwardTarget : backwardTargets)
							enqueue(target, new BackwardLink(composition,
									backwardTarget));
				}
			}

			if (deletionMode)
				context.forwardLinksByObjectProperty.remove(linkRelation,
						target);

			return null;
		}

		public Void visit(Propagation propagation) {

			// for cleaning up don't change unaffected contexts
			if (deletionMode && !unSaturationMode && context.isSaturated()) {
				return null;
			}

			propInfNo.incrementAndGet();
			IndexedPropertyChain propRelation = propagation.getRelation();
			Queueable carry = propagation.getCarry();

			if (context.propagationsByObjectProperty == null) {
				context.propagationsByObjectProperty = new HashSetMultimap<IndexedPropertyChain, Queueable>();
				initializeDerivationOfBackwardLinks();
			}

			boolean updated = deletionMode ? context.propagationsByObjectProperty
					.contains(propRelation, carry)
					: context.propagationsByObjectProperty.add(propRelation,
							carry);

			if (!updated)
				return null;

			propNo.incrementAndGet();

			if (unSaturationMode && context.isSaturated()) {
				if (context.setNotSaturated())
					unSaturatedContexts.add(context);
			}

			/* propagate over all backward links */
			if (context.backwardLinksByObjectProperty != null) {

				for (IndexedPropertyChain linkRelation : new LazySetIntersection<IndexedPropertyChain>(
						propRelation.getSaturated().getSubProperties(),
						context.backwardLinksByObjectProperty.keySet()))

					for (Linkable target : context.backwardLinksByObjectProperty
							.get(linkRelation))

						enqueue(target, carry);
			}

			if (deletionMode)
				context.propagationsByObjectProperty
						.remove(propRelation, carry);

			return null;
		}

		public Void visit(DecomposedClassExpression compositeClassExpression) {
			//
			// for cleaning up don't change unaffected contexts
			if (deletionMode && !unSaturationMode && context.isSaturated()) {
				return null;
			}

			derivedInfNo.incrementAndGet();

			IndexedClassExpression indexedClassExpression = compositeClassExpression
					.getClassExpression();

			if (!unSaturationMode && context.isSaturated())
				LOGGER_.warn(context.root + ": adding "
						+ indexedClassExpression + " to a saturated context!");

			boolean updated = deletionMode ? context.derived
					.contains(compositeClassExpression.getClassExpression())
					: context.derived.add(indexedClassExpression);

			if (!updated)
				return null;

			if (unSaturationMode && context.isSaturated()) {
				if (context.setNotSaturated())
					unSaturatedContexts.add(context);
			}

			if (!unSaturationMode && context.isSaturated())
				LOGGER_.error(context.root + ": new " + indexedClassExpression
						+ " in a saturated context!");
			derivedNo.incrementAndGet();
			processClass(indexedClassExpression);
			// if (deletionMode)
			indexedClassExpression.accept(classExpressionDecomposer);

			if (deletionMode)
				context.derived.remove(compositeClassExpression
						.getClassExpression());

			return null;
		}

		public Void visit(IndexedClassExpression indexedClassExpression) {

			// for cleaning up don't change unaffected contexts
			if (deletionMode && !unSaturationMode && context.isSaturated()) {
				return null;
			}

			derivedInfNo.incrementAndGet();

			if (!unSaturationMode && context.isSaturated())
				LOGGER_.warn(context.root + ": adding "
						+ indexedClassExpression + " to a saturated context!");

			boolean updated = deletionMode ? context.derived
					.contains(indexedClassExpression) : context.derived
					.add(indexedClassExpression);

			if (!updated)
				return null;

			if (unSaturationMode && context.isSaturated()) {
				if (context.setNotSaturated())
					unSaturatedContexts.add(context);
			}

			if (!unSaturationMode && context.isSaturated())
				LOGGER_.error(context.root + ": new " + indexedClassExpression
						+ " in a saturated context!");
			derivedNo.incrementAndGet();
			processClass(indexedClassExpression);
			indexedClassExpression.accept(classExpressionDecomposer);

			if (deletionMode)
				context.derived.remove(indexedClassExpression);

			return null;
		}

		public void processClass(IndexedClassExpression ice) {
			// TODO propagate bottom backwards
			if (ice == owlNothing) {
				context.isSatisfiable = false;
				return;
			}

			/* process subsumptions */
			if (ice.getToldSuperClassExpressions() != null) {
				for (IndexedClassExpression implied : ice
						.getToldSuperClassExpressions())
					enqueue(context, implied);
			}

			/* process negative conjunctions */
			if (ice.getNegConjunctionsByConjunct() != null) {
				for (IndexedClassExpression common : new LazySetIntersection<IndexedClassExpression>(
						ice.getNegConjunctionsByConjunct().keySet(),
						context.derived))
					enqueue(context, new DecomposedClassExpression(ice
							.getNegConjunctionsByConjunct().get(common)));
			}

			/*
			 * process negative existentials only needed when there is at least
			 * one backward link
			 */
			if (context.backwardLinksByObjectProperty != null
					&& ice.getNegExistentials() != null) {
				for (IndexedObjectSomeValuesFrom e : ice.getNegExistentials())
					new Propagation(e.getRelation(),
							new DecomposedClassExpression(e)).accept(this);
			}

		}

		private ClassExpressionDecomposer classExpressionDecomposer = new ClassExpressionDecomposer();

		private class ClassExpressionDecomposer implements
				IndexedClassExpressionVisitor<Void> {

			public Void visit(IndexedClass ice) {
				return null;
			}

			public Void visit(IndexedObjectIntersectionOf ice) {
				enqueue(context, ice.getFirstConjunct());
				enqueue(context, ice.getSecondConjunct());
				return null;
			}

			public Void visit(IndexedObjectSomeValuesFrom ice) {
				enqueue(getCreateContext(ice.getFiller()),
						new BackwardLink(ice.getRelation(), context));
				return null;
			}

			public Void visit(IndexedDataHasValue element) {
				return null;
			}

			public Void visit(IndexedNominal element) {
				return null;
			}
		}

		/*
		 * adds a forward copy of each backward link effectively allowing these
		 * links to occur as the right components in property compositions
		 */
		void initializeDerivationOfBackwardLinks() {
			if (context.deriveBackwardLinks)
				return;

			context.deriveBackwardLinks = true;

			if (context.backwardLinksByObjectProperty != null)

				for (IndexedPropertyChain linkRelation : context.backwardLinksByObjectProperty
						.keySet())

					if (linkRelation.getSaturated().propertyCompositionsByLeftSubProperty != null)

						for (Linkable target : context.backwardLinksByObjectProperty
								.get(linkRelation))

							enqueue(target, new ForwardLink(linkRelation,
									context));
		}

	}

}
