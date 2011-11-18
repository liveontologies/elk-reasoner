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
package org.semanticweb.elk.reasoner.saturation.classes;

import static org.semanticweb.elk.reasoner.saturation.markers.ExplicitlyMarked.mark;
import static org.semanticweb.elk.reasoner.saturation.markers.ExplicitlyMarked.markIntersection;
import static org.semanticweb.elk.reasoner.saturation.markers.ExplicitlyMarked.markUnion;

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
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturationEngine;
import org.semanticweb.elk.reasoner.saturation.markers.Marked;
import org.semanticweb.elk.reasoner.saturation.markers.MarkedHashSet;
import org.semanticweb.elk.reasoner.saturation.markers.MarkedMultimap;
import org.semanticweb.elk.reasoner.saturation.markers.NonEmpty;
import org.semanticweb.elk.util.collections.LazySetIntersection;

/**
 * The engine for computing the saturation of class expressions. This is the
 * class that implements the application of inference rules.
 * 
 * @author Frantisek Simancik
 * 
 */
public class RuleApplicationEngine {

	// TODO catch nulls produced by the method ExplicitlyMarked.mark() !

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
	 * In case no such context exists, a new one is markd with the given root
	 * and is returned. It is ensured that no two different contexts are markd
	 * with the same root. In case a new context is markd, it is scheduled to be
	 * processed.
	 * 
	 * @param root
	 *            the input indexed class expression for which to return the
	 *            context having it as a root
	 * @return context which root is the input indexed class expression.
	 * 
	 */
	public SaturatedClassExpression getCreateContext(IndexedClassExpression root) {
		if (root.getSaturated() == null) {
			SaturatedClassExpression sce = new SaturatedClassExpression(root);
			if (root.setSaturated(sce)) {
				if (LOGGER_.isTraceEnabled()) {
					LOGGER_.trace(root + ": context markd");
				}

				if (root instanceof IndexedNominal)
					sce.nonEmpty = NonEmpty.INSTANCE;
				else
					sce.nonEmpty = NonEmpty.POSSIBLE_INSTANCE;

				enqueue(sce, new SuperClassExpression(root));

				if (owlThing.occursNegatively())
					enqueue(sce, new SuperClassExpression(owlThing));
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

	protected void enqueue(SaturatedClassExpression context, Derivable item) {
		context.queue.add(item);
		activateContext(context);
	}

	protected void link(IndexedPropertyChain relation,
			Marked<SaturatedClassExpression> source,
			Marked<SaturatedClassExpression> target) {
		enqueue(target.getKey(),
				new BackwardLink(relation, markIntersection(source.getKey(),
						target, source)));
	}

	protected void propagate(Marked<SaturatedClassExpression> target,
			Marked<IndexedClassExpression> mice) {
		enqueue(target.getKey(), new ComposedSuperClassExpression(
				markIntersection(mice.getKey(), target, mice)));
	}

	public void processActiveContexts() throws InterruptedException {
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
			Derivable item = context.queue.poll();
			if (item == null)
				break;
			item.accept(queueProcessor);
		}

		deactivateContext(context);
	}

	private class QueueProcessor implements DerivableVisitor<Void> {
		final SaturatedClassExpression context;

		QueueProcessor(SaturatedClassExpression context) {
			this.context = context;
		}

		public Void visit(BackwardLink backwardLink) {
			IndexedPropertyChain backwardRelation = backwardLink.getRelation();
			Marked<SaturatedClassExpression> backwardTarget = backwardLink
					.getTarget();

			if (context.backwardLinksByObjectProperty == null) {
				context.backwardLinksByObjectProperty = new MarkedMultimap<IndexedPropertyChain, SaturatedClassExpression>();

				// start deriving propagations
				for (Marked<IndexedClassExpression> mce : context.superClassExpressions)
					if (mce.getKey().getNegExistentials() != null)
						for (IndexedObjectSomeValuesFrom e : mce.getKey()
								.getNegExistentials())
							addPropagation(e.getRelation(),
									mark((IndexedClassExpression) e, mce));
			}

			if (context.backwardLinksByObjectProperty.add(backwardRelation,
					backwardTarget)) {
				backLinkNo.incrementAndGet();

				// apply all propagations over the link
				if (context.propagationsByObjectProperty != null) {

					for (IndexedPropertyChain propRelation : new LazySetIntersection<IndexedPropertyChain>(
							backwardRelation.getSaturated()
									.getSuperProperties(),
							context.propagationsByObjectProperty.keySet()))

						for (Marked<IndexedClassExpression> carry : context.propagationsByObjectProperty
								.get(propRelation))

							propagate(backwardTarget, carry);
				}

				/*
				 * if deriveBackwardLinks, then add a forward copy of the link
				 * to consider the link in property compositions
				 */
				if (context.deriveBackwardLinks
						&& backwardRelation.getSaturated()
								.getPropertyCompositionsByLeftSubProperty() != null)
					enqueue(backwardTarget.getKey(), new ForwardLink(
							backwardRelation, mark(context, backwardTarget)));

				/* compose the link with all forward links */
				if (backwardRelation.getSaturated()
						.getPropertyCompositionsByRightSubProperty() != null
						&& context.forwardLinksByObjectProperty != null) {

					for (IndexedPropertyChain forwardRelation : new LazySetIntersection<IndexedPropertyChain>(
							backwardRelation
									.getSaturated()
									.getPropertyCompositionsByRightSubProperty()
									.keySet(),
							context.forwardLinksByObjectProperty.keySet())) {

						Collection<IndexedBinaryPropertyChain> compositions = backwardRelation
								.getSaturated()
								.getPropertyCompositionsByRightSubProperty()
								.get(forwardRelation);
						Collection<Marked<SaturatedClassExpression>> forwardTargets = context.forwardLinksByObjectProperty
								.get(forwardRelation);

						for (IndexedPropertyChain composition : compositions)
							for (Marked<SaturatedClassExpression> forwardTarget : forwardTargets)
								link(composition, backwardTarget, forwardTarget);
					}
				}

			}

			return null;
		}

		public Void visit(ForwardLink forwardLink) {
			IndexedPropertyChain forwardRelation = forwardLink.getRelation();
			Marked<SaturatedClassExpression> forwardTarget = forwardLink
					.getTarget();

			if (context.forwardLinksByObjectProperty == null) {
				context.forwardLinksByObjectProperty = new MarkedMultimap<IndexedPropertyChain, SaturatedClassExpression>();
				initializeDerivationOfBackwardLinks();
			}

			if (context.forwardLinksByObjectProperty.add(forwardRelation,
					forwardTarget)) {
				forwLinkNo.incrementAndGet();

				/* compose the link with all backward links */
				// assert
				// linkRelation.getSaturated().propertyCompositionsByLeftSubProperty
				// != null
				if (context.backwardLinksByObjectProperty != null) {

					for (IndexedPropertyChain backwardRelation : new LazySetIntersection<IndexedPropertyChain>(
							forwardRelation.getSaturated()
									.getPropertyCompositionsByLeftSubProperty()
									.keySet(),
							context.backwardLinksByObjectProperty.keySet())) {

						Collection<IndexedBinaryPropertyChain> compositions = forwardRelation
								.getSaturated()
								.getPropertyCompositionsByLeftSubProperty()
								.get(backwardRelation);
						Collection<Marked<SaturatedClassExpression>> backwardTargets = context.backwardLinksByObjectProperty
								.get(backwardRelation);

						for (IndexedPropertyChain composition : compositions)
							for (Marked<SaturatedClassExpression> backwardTarget : backwardTargets)
								link(composition, backwardTarget, forwardTarget);
					}
				}

			}

			return null;
		}

		private Void addPropagation(IndexedPropertyChain propRelation,
				Marked<IndexedClassExpression> carry) {

			if (context.propagationsByObjectProperty == null) {
				context.propagationsByObjectProperty = new MarkedMultimap<IndexedPropertyChain, IndexedClassExpression>();
				initializeDerivationOfBackwardLinks();
			}

			// addPropagations is never called twice with the same argument
			if (context.propagationsByObjectProperty.add(propRelation, carry)) {
				propNo.incrementAndGet();

				/* propagate over all backward links */
				if (context.backwardLinksByObjectProperty != null) {

					for (IndexedPropertyChain backwardRelation : new LazySetIntersection<IndexedPropertyChain>(
							propRelation.getSaturated().getSubProperties(),
							context.backwardLinksByObjectProperty.keySet()))

						for (Marked<SaturatedClassExpression> backwardTarget : context.backwardLinksByObjectProperty
								.get(backwardRelation))

							propagate(backwardTarget, carry);
				}
			}

			return null;
		}

		public Void visit(
				ComposedSuperClassExpression composedSuperClassExpression) {
			processClassExpression(composedSuperClassExpression
					.getClassExpression());
			return null;
		}

		public Void visit(SuperClassExpression superClassExpression) {
			Marked<IndexedClassExpression> mce = superClassExpression
					.getClassExpression();
			if (processClassExpression(mce)) {
				mce.getKey().accept(new ClassExpressionDecomposer(mce));
			}
			return null;
		}

		// returns whether this was newly derived
		boolean processClassExpression(Marked<IndexedClassExpression> mce) {

			if (context.isSaturated())
				LOGGER_.warn(context.root + ": adding " + mce.getKey()
						+ " to a saturated context!");

			if (context.superClassExpressions.add(mce)) {
				if (context.isSaturated())
					LOGGER_.error(context.root + ": new " + mce.getKey()
							+ " in a saturated context!");

				IndexedClassExpression ice = mce.getKey();

				// TODO propagate bottom backwards
				if (ice == owlNothing && mce.isDefinite()) {
					context.isSatisfiable = false;
					return true;
				}

				/* process subsumptions */
				if (ice.getToldSuperClassExpressions() != null) {
					for (IndexedClassExpression implied : ice
							.getToldSuperClassExpressions())
						enqueue(context,
								new SuperClassExpression(mark(implied, mce)));
				}

				/* process negative conjunctions */
				if (ice.getNegConjunctionsByConjunct() != null) {
					for (IndexedClassExpression common : new LazySetIntersection<IndexedClassExpression>(
							ice.getNegConjunctionsByConjunct().keySet(),
							context.superClassExpressions.keySet()))
						enqueue(context,
								new ComposedSuperClassExpression(
										markIntersection(
												(IndexedClassExpression) ice
														.getNegConjunctionsByConjunct()
														.get(common), mce,
												context.superClassExpressions
														.get(common))));
				}

				/*
				 * process negative existentials only needed when there is at
				 * least one backward link
				 */
				if (context.backwardLinksByObjectProperty != null
						&& ice.getNegExistentials() != null) {
					for (IndexedObjectSomeValuesFrom e : ice
							.getNegExistentials())
						addPropagation(e.getRelation(),
								mark((IndexedClassExpression) e, mce));
				}

				return true;
			}
			return false;
		}

		private class ClassExpressionDecomposer implements
				IndexedClassExpressionVisitor<Void> {

			private final Marked<?> markers;

			public ClassExpressionDecomposer(Marked<?> markers) {
				this.markers = markers;
			}

			public Void visit(IndexedClass ice) {
				return null;
			}

			public Void visit(IndexedObjectIntersectionOf ice) {
				enqueue(context,
						new SuperClassExpression(mark(ice.getFirstConjunct(),
								markers)));
				enqueue(context,
						new SuperClassExpression(mark(ice.getSecondConjunct(),
								markers)));
				return null;
			}

			public Void visit(IndexedObjectSomeValuesFrom ice) {
				SaturatedClassExpression target = getCreateContext(ice
						.getFiller());
				enqueue(target,
						new BackwardLink(ice.getRelation(), mark(context,
								markers)));
				if (context.nonEmpty != NonEmpty.POSSIBLE_INSTANCE)
					enqueue(target,
							new NonEmptyAxiom(markIntersection(
									NonEmpty.INSTANCE, context.nonEmpty,
									markers)));
				return null;
			}

			public Void visit(IndexedDataHasValue element) {
				return null;
			}

			public Void visit(IndexedNominal element) {
				enqueue(getCreateContext(element), new SubNominal(
						markIntersection(context, context.nonEmpty, markers)));
				return null;
			}
		}

		/*
		 * adds a forward copy of each backward link effectively allowing these
		 * links to occur as the right components in property compositions
		 */
		private void initializeDerivationOfBackwardLinks() {
			if (context.deriveBackwardLinks)
				return;

			context.deriveBackwardLinks = true;

			if (context.backwardLinksByObjectProperty != null)

				for (IndexedPropertyChain backwardRelation : context.backwardLinksByObjectProperty
						.keySet())

					if (backwardRelation.getSaturated()
							.getPropertyCompositionsByLeftSubProperty() != null)

						for (Marked<SaturatedClassExpression> backwardTarget : context.backwardLinksByObjectProperty
								.get(backwardRelation))

							enqueue(backwardTarget.getKey(),
									new ForwardLink(backwardRelation, mark(
											context, backwardTarget)));
		}

		public Void visit(SubNominal subNominal) {
			if (context.subNominals == null) {
				context.subNominals = new MarkedHashSet<SaturatedClassExpression>();
			}

			Marked<SaturatedClassExpression> mce1 = subNominal
					.getClassExpression();
			if (context.subNominals.add(mce1)) {
				for (Marked<SaturatedClassExpression> mce2 : context.subNominals)
					if (mce1.getKey() != mce2.getKey()) {
						enqueue(mce1.getKey(),
								new SuperClassExpression(markIntersection(
										mce2.getKey().root, mce1, mce2)));
						enqueue(mce2.getKey(),
								new SuperClassExpression(markIntersection(
										mce1.getKey().root, mce1, mce2)));
					}
			}

			return null;
		}

		public Void visit(NonEmptyAxiom nonEmptyAxiom) {
			Marked<NonEmpty> newNonEmpty = nonEmptyAxiom.getNonEmpty();

			Marked<NonEmpty> union = markUnion(context.nonEmpty, newNonEmpty);
			if (union == null)
				return null;
			
			context.nonEmpty = union;

			/*
			 * The following iteration can be optimised by keeping all forward
			 * links and all super nominals.
			 */
			for (Marked<IndexedClassExpression> mce : context.superClassExpressions) {
				IndexedClassExpression ice = mce.getKey();

				// TODO use a visitor here
				if (ice instanceof IndexedObjectSomeValuesFrom) {
					enqueue(getCreateContext(((IndexedObjectSomeValuesFrom) ice)
							.getFiller()),
							new NonEmptyAxiom(markIntersection(
									NonEmpty.INSTANCE, newNonEmpty, mce)));
				}
				if (ice instanceof IndexedNominal) {
					enqueue(getCreateContext(ice), new SubNominal(
							markIntersection(context, newNonEmpty, mce)));
				}
			}

			return null;
		}

	}
}
