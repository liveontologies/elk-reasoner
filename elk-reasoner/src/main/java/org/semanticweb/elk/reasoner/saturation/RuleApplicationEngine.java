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
package org.semanticweb.elk.reasoner.saturation;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
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
import org.semanticweb.elk.util.collections.HashSetMultimap;
import org.semanticweb.elk.util.collections.LazySetIntersection;

/**
 * The engine for computing the saturation of class expressions. This is the
 * class that implements the application of inference rules.
 * 
 * @author Frantisek Simancik
 * 
 */
public class RuleApplicationEngine {

	// Statistical information
	AtomicInteger derivedNo = new AtomicInteger(0);
	AtomicInteger backLinkNo = new AtomicInteger(0);
	AtomicInteger propNo = new AtomicInteger(0);
	AtomicInteger forwLinkNo = new AtomicInteger(0);

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
	protected final Queue<SaturatedClassExpression> activeContexts;

	public RuleApplicationEngine(OntologyIndex ontologyIndex) {
		this.ontologyIndex = ontologyIndex;
		this.activeContexts = new ConcurrentLinkedQueue<SaturatedClassExpression>();

		// reset saturation in case of re-saturation after changes
		for (IndexedClassExpression ice : ontologyIndex
				.getIndexedClassExpressions())
			ice.resetSaturated();

		owlThing = ontologyIndex.getIndexed(PredefinedElkClass.OWL_THING);
		owlNothing = ontologyIndex.getIndexed(PredefinedElkClass.OWL_NOTHING);

	}

	/**
	 * Return a context which has the input indexed class expression as a root.
	 * In case no such context exists, a new one is created with the given root
	 * and is returned. It is ensured that no two different contexts are created
	 * with the same root. In case a new context is created, it is scheduled to
	 * be processed.
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
				enqueue(sce, root);

				if (owlThing.occursNegatively())
					enqueue(sce, owlThing);
			}
		}
		return root.getSaturated();
	}

	protected void activateContext(SaturatedClassExpression context) {
		if (context.tryActivate()) {
			activeContexts.add(context);
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
			if (nextContext == null)
				break;
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

	private class QueueProcessor implements QueueableVisitor<Void> {
		final SaturatedClassExpression context;

		QueueProcessor(SaturatedClassExpression context) {
			this.context = context;
		}

		public Void visit(BackwardLink backwardLink) {
			IndexedPropertyChain linkRelation = backwardLink.getRelation();
			Linkable target = backwardLink.getTarget();

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
						Collection<Linkable> forwardTargets = context.forwardLinksByObjectProperty
								.get(forwardRelation);

						for (IndexedPropertyChain composition : compositions)
							for (Linkable forwardTarget : forwardTargets)
								enqueue(forwardTarget, new BackwardLink(
										composition, target));
					}
				}

			}

			return null;
		}

		public Void visit(ForwardLink forwardLink) {
			IndexedPropertyChain linkRelation = forwardLink.getRelation();
			Linkable target = forwardLink.getTarget();

			if (context.forwardLinksByObjectProperty == null) {
				context.forwardLinksByObjectProperty = new HashSetMultimap<IndexedPropertyChain, Linkable>();
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
						Collection<Linkable> backwardTargets = context.backwardLinksByObjectProperty
								.get(backwardRelation);

						for (IndexedPropertyChain composition : compositions)
							for (Linkable backwardTarget : backwardTargets)
								enqueue(target, new BackwardLink(composition,
										backwardTarget));
					}
				}

			}

			return null;
		}

		public Void visit(Propagation propagation) {
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

						for (Linkable target : context.backwardLinksByObjectProperty
								.get(linkRelation))

							enqueue(target, carry);
				}

			}

			return null;
		}

		public Void visit(DecomposedClassExpression compositeClassExpression) {
			if (context.isSaturated())
				LOGGER_.warn(context.root + ": adding "
						+ compositeClassExpression.getClassExpression()
						+ " to a saturated context!");
			if (context.derived.add(compositeClassExpression
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
			if (context.isSaturated())
				LOGGER_.warn(context.root + ": adding "
						+ indexedClassExpression + " to a saturated context!");
			if (context.derived.add(indexedClassExpression)) {
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
