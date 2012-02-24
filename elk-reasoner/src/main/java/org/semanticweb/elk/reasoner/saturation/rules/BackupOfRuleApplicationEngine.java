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
package org.semanticweb.elk.reasoner.saturation.rules;

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
import org.semanticweb.elk.reasoner.saturation.expressions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.expressions.DecomposedClassExpression;
import org.semanticweb.elk.reasoner.saturation.expressions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.expressions.Propagation;
import org.semanticweb.elk.reasoner.saturation.expressions.Queueable;
import org.semanticweb.elk.reasoner.saturation.expressions.QueueableVisitor;
import org.semanticweb.elk.util.collections.HashSetMultimap;
import org.semanticweb.elk.util.collections.LazySetIntersection;
import org.semanticweb.elk.util.concurrent.computation.AbstractJobManager;

/**
 * The engine for computing the saturation of class expressions. This is the
 * class that implements the application of inference rules.
 * 
 * @author Frantisek Simancik
 * @author Yevgeny Kazakov
 * 
 */
public class BackupOfRuleApplicationEngine extends
		AbstractJobManager<IndexedClassExpression> {

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
	protected final Queue<Context> activeContexts;
	/**
	 * The number of contexts ever created by this engine
	 */
	protected final AtomicInteger contextNo = new AtomicInteger(0);

	/**
	 * <tt>true</tt> if the {@link #activeContexts} queue is empty
	 */
	protected final AtomicBoolean activeContextsEmpty;

	public BackupOfRuleApplicationEngine(OntologyIndex ontologyIndex) {
		this.ontologyIndex = ontologyIndex;
		this.activeContexts = new ConcurrentLinkedQueue<Context>();
		this.activeContextsEmpty = new AtomicBoolean(true);

		// reset saturation in case of re-saturation after changes
		// TODO: introduce a separate method for this
		for (IndexedClassExpression ice : ontologyIndex
				.getIndexedClassExpressions())
			ice.resetContext();

		owlThing = ontologyIndex.getIndexed(PredefinedElkClass.OWL_THING);
		owlNothing = ontologyIndex.getIndexed(PredefinedElkClass.OWL_NOTHING);

	}

	public final void submit(IndexedClassExpression job) {
		getCreateContext(job);
	}

	public final void process() throws InterruptedException {
		processActiveContexts();
	}

	@Override
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
			notifyCanProcess();
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
	protected Context getCreateContext(
			IndexedClassExpression root) {
		if (root.getContext() == null) {
			Context sce = new Context(root);
			if (root.setContext(sce)) {
				if (LOGGER_.isTraceEnabled()) {
					LOGGER_.trace(root + ": context created");
				}
				contextNo.incrementAndGet();
				enqueue(sce, root);

				if (owlThing.occursNegatively())
					enqueue(sce, owlThing);
			}
		}
		return root.getContext();
	}

	protected void activateContext(Context context) {
		if (context.tryActivate()) {
			activeContexts.add(context);
			tryNotifyCanProcess();
		}
	}

	protected void deactivateContext(Context context) {
		if (context.tryDeactivate())
			if (!context.queue.isEmpty())
				activateContext(context);
	}

	protected void enqueue(Context target, Queueable item) {

		/*
		 * so far SaturatedClassExpression is the only implementation of
		 * Linkable
		 */
		if (target instanceof Context) {
			Context context = (Context) target;
			context.queue.add(item);
			activateContext(context);
		}
	}

	protected void processActiveContexts() throws InterruptedException {
		for (;;) {
			Context nextContext = activeContexts.poll();
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

	protected void process(Context context) {

		final QueueProcessor queueProcessor = new QueueProcessor(context);

		for (;;) {
			Queueable item = context.queue.poll();
			if (item == null)
				break;
			item.accept(queueProcessor);
		}

		deactivateContext(context);
	}

	private class QueueProcessor implements QueueableVisitor<Void> {
		final Context context;

		QueueProcessor(Context context) {
			this.context = context;
		}

		public Void visit(BackwardLink backwardLink) {
			backLinkInfNo.incrementAndGet();
			IndexedPropertyChain linkRelation = backwardLink.getRelation();
			Context target = backwardLink.getTarget();

			if (context.backwardLinksByObjectProperty == null) {
				context.backwardLinksByObjectProperty = new HashSetMultimap<IndexedPropertyChain, Context>();

				// start deriving propagations
				for (IndexedClassExpression ice : context.superClassExpressions)
					if (ice.getNegExistentials() != null)
						for (IndexedObjectSomeValuesFrom e : ice
								.getNegExistentials())
							new Propagation(e.getRelation(),
									new DecomposedClassExpression(e))
									.accept(this);
			}

			if (context.backwardLinksByObjectProperty.add(linkRelation, target)) {
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
				 * if deriveBackwardLinks, then add a forward copy of the link
				 * to consider the link in property compositions
				 */
				if (context.deriveBackwardLinks
						&& linkRelation.getSaturated().propertyCompositionsByLeftSubProperty != null)
					enqueue(target, new ForwardLink(linkRelation, context));

				/* compose the link with all forward links */
				if (linkRelation.getSaturated().propertyCompositionsByRightSubProperty != null
						&& context.forwardLinksByObjectProperty != null) {

					for (IndexedPropertyChain forwardRelation : new LazySetIntersection<IndexedPropertyChain>(
							linkRelation.getSaturated().propertyCompositionsByRightSubProperty
									.keySet(),
							context.forwardLinksByObjectProperty.keySet())) {

						Collection<IndexedBinaryPropertyChain> compositions = linkRelation
								.getSaturated().propertyCompositionsByRightSubProperty
								.get(forwardRelation);
						Collection<Context> forwardTargets = context.forwardLinksByObjectProperty
								.get(forwardRelation);

						for (IndexedPropertyChain composition : compositions)
							for (Context forwardTarget : forwardTargets)
								enqueue(forwardTarget, new BackwardLink(
										composition, target));
					}
				}

			}

			return null;
		}

		public Void visit(ForwardLink forwardLink) {
			forwLinkInfNo.incrementAndGet();
			IndexedPropertyChain linkRelation = forwardLink.getRelation();
			Context target = forwardLink.getTarget();

			if (context.forwardLinksByObjectProperty == null) {
				context.forwardLinksByObjectProperty = new HashSetMultimap<IndexedPropertyChain, Context>();
				initializeDerivationOfBackwardLinks();
			}

			if (context.forwardLinksByObjectProperty.add(linkRelation, target)) {
				forwLinkNo.incrementAndGet();

				/* compose the link with all backward links */
				// assert
				// linkRelation.getSaturated().propertyCompositionsByLeftSubProperty
				// != null
				if (context.backwardLinksByObjectProperty != null) {

					for (IndexedPropertyChain backwardRelation : new LazySetIntersection<IndexedPropertyChain>(
							linkRelation.getSaturated().propertyCompositionsByLeftSubProperty
									.keySet(),
							context.backwardLinksByObjectProperty.keySet())) {

						Collection<IndexedBinaryPropertyChain> compositions = linkRelation
								.getSaturated().propertyCompositionsByLeftSubProperty
								.get(backwardRelation);
						Collection<Context> backwardTargets = context.backwardLinksByObjectProperty
								.get(backwardRelation);

						for (IndexedPropertyChain composition : compositions)
							for (Context backwardTarget : backwardTargets)
								enqueue(target, new BackwardLink(composition,
										backwardTarget));
					}
				}

			}

			return null;
		}

		public Void visit(Propagation propagation) {
			propInfNo.incrementAndGet();
			IndexedPropertyChain propRelation = propagation.getRelation();
			Queueable carry = propagation.getCarry();

			if (context.propagationsByObjectProperty == null) {
				context.propagationsByObjectProperty = new HashSetMultimap<IndexedPropertyChain, Queueable>();
				initializeDerivationOfBackwardLinks();
			}

			if (context.propagationsByObjectProperty.add(propRelation, carry)) {
				propNo.incrementAndGet();

				/* propagate over all backward links */
				if (context.backwardLinksByObjectProperty != null) {

					for (IndexedPropertyChain linkRelation : new LazySetIntersection<IndexedPropertyChain>(
							propRelation.getSaturated().getSubProperties(),
							context.backwardLinksByObjectProperty.keySet()))

						for (Context target : context.backwardLinksByObjectProperty
								.get(linkRelation))

							enqueue(target, carry);
				}

			}

			return null;
		}

		public Void visit(DecomposedClassExpression compositeClassExpression) {
			derivedInfNo.incrementAndGet();
			if (context.isSaturated())
				LOGGER_.warn(context.root + ": adding "
						+ compositeClassExpression.getClassExpression()
						+ " to a saturated context!");
			if (context.superClassExpressions.add(compositeClassExpression
					.getClassExpression())) {
				if (context.isSaturated())
					LOGGER_.error(context.root + ": new "
							+ compositeClassExpression.getClassExpression()
							+ " in a saturated context!");
				derivedNo.incrementAndGet();
				processClass(compositeClassExpression.getClassExpression());
			}
			return null;
		}

		public Void visit(IndexedClassExpression indexedClassExpression) {
			derivedInfNo.incrementAndGet();
			if (context.isSaturated())
				LOGGER_.warn(context.root + ": adding "
						+ indexedClassExpression + " to a saturated context!");
			if (context.superClassExpressions.add(indexedClassExpression)) {
				if (context.isSaturated())
					LOGGER_.error(context.root + ": new "
							+ indexedClassExpression
							+ " in a saturated context!");
				derivedNo.incrementAndGet();
				processClass(indexedClassExpression);
				indexedClassExpression.accept(classExpressionDecomposer);
			}
			return null;
		}

		void processClass(IndexedClassExpression ice) {
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
						context.superClassExpressions))
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

						for (Context target : context.backwardLinksByObjectProperty
								.get(linkRelation))

							enqueue(target, new ForwardLink(linkRelation,
									context));
		}

	}

}
