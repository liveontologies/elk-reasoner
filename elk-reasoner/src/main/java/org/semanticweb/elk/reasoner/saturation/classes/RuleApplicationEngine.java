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

import static org.semanticweb.elk.reasoner.saturation.markers.MarkerOperations.mark;
import static org.semanticweb.elk.reasoner.saturation.markers.MarkerOperations.markersDifference;
import static org.semanticweb.elk.reasoner.saturation.markers.MarkerOperations.markersIntersection;

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
import org.semanticweb.elk.reasoner.saturation.markers.HashSetMarkers;
import org.semanticweb.elk.reasoner.saturation.markers.Marked;
import org.semanticweb.elk.reasoner.saturation.markers.MarkedHashSet;
import org.semanticweb.elk.reasoner.saturation.markers.MarkedMultimap;
import org.semanticweb.elk.reasoner.saturation.markers.Marker;
import org.semanticweb.elk.reasoner.saturation.markers.Markers;
import org.semanticweb.elk.reasoner.saturation.markers.NonDefiniteMarkers;
import org.semanticweb.elk.reasoner.saturation.markers.QuestionMarker;
import org.semanticweb.elk.util.collections.ArraySet;
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
	public static AtomicInteger otherRules = new AtomicInteger(0);
	public static AtomicInteger otherNo = new AtomicInteger(0);

	public static AtomicInteger reachRules = new AtomicInteger(0);
	public static AtomicInteger reachNo = new AtomicInteger(0);
	
	public static AtomicInteger confirmedSubsumptions = new AtomicInteger(0);
	
	public static AtomicInteger newSuperClasses = new AtomicInteger(0);
	public static AtomicInteger newSubsumptions = new AtomicInteger(0);
	
	public static AtomicInteger atomicSubNominal = new AtomicInteger(0);
	public static AtomicInteger nominalSubNominal = new AtomicInteger(0);
	public static AtomicInteger complexSubNominal = new AtomicInteger(0);
	
	public static boolean secondPhase = false;
	
	public static void write() {
		System.err.printf("%d %d %s %s\n", otherRules.get()+reachRules.get(), otherNo.get()+reachNo.get(), reachRules, reachNo);
		System.err.printf("%s %s %s\n", atomicSubNominal, nominalSubNominal, complexSubNominal);
	}
	
	
	public static void reset() {
		otherRules.set(0);
		otherNo.set(0);
		reachRules.set(0);
		reachNo.set(0);
		atomicSubNominal.set(0);
		nominalSubNominal.set(0);
		complexSubNominal.set(0);
	}
	
	public static void setSecondPhase() {
		secondPhase = true;
		reset();
	}
	
	protected final static Logger LOGGER_ = Logger
			.getLogger(ClassExpressionSaturationEngine.class);

	/**
	 * Cached constants
	 */
	protected final IndexedClassExpression owlThing, owlNothing;

	/**
	 * The queue containing all activated contexts. Every activated context
	 * occurs exactly once.
	 */
	protected final Queue<SaturatedClassExpression> activeContexts;
	
	public final OntologyIndex ontologyIndex;
	
	public RuleApplicationEngine(OntologyIndex ontologyIndex) {
		this.activeContexts = new ConcurrentLinkedQueue<SaturatedClassExpression>();
		this.ontologyIndex = ontologyIndex;

		if (!secondPhase)
		for (IndexedClassExpression ice : ontologyIndex
				.getIndexedClassExpressions())
			ice.resetSaturated();

		owlThing = ontologyIndex.getIndexed(PredefinedElkClass.OWL_THING);
		owlNothing = ontologyIndex.getIndexed(PredefinedElkClass.OWL_NOTHING);
		
		int nominalNo = 0;
		for (IndexedNominal nominal : ontologyIndex.getIndexedNominals()) {
			SaturatedClassExpression context = getCreateContext(nominal);
//			if (secondPhase)
//				enqueue(context, new Reachability(DefiniteMarkers.INSTANCE));
			nominalNo++;
		}
		System.err.println("Indexed Nominals: " + nominalNo);
	}

	/**
	 * Return a context which has the input indexed class expression as a root.
	 * In case no such context exists, a new one is created with the given root
	 * and is returned. It is ensured that no two different contexts are created
	 * with the same root. In case a new context is created, it is scheduled to be
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
					LOGGER_.trace(root + ": context created");
				}
				
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

	public void enqueue(SaturatedClassExpression context, Derivable item) {
		context.queue.add(item);
		activateContext(context);
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
	
	private void link(IndexedPropertyChain relation,
			Marked<SaturatedClassExpression> source,
			Marked<SaturatedClassExpression> target) {
		
		Markers intersection = markersIntersection(target.getMarkers(), source.getMarkers());
		if (intersection != null)
			enqueue(target.getKey(), new BackwardLink(relation, mark(source.getKey(), intersection)));
	}

	private void propagate(Marked<SaturatedClassExpression> target,
			Marked<IndexedClassExpression> ice) {
		
		Markers intersection = markersIntersection(target.getMarkers(), ice.getMarkers());
		if (intersection != null)
			enqueue(target.getKey(), new SuperClassExpression(mark(ice.getKey(), intersection), false));
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
									mark((IndexedClassExpression) e, mce.getMarkers()));
			}

			otherRules.incrementAndGet();
			if (context.backwardLinksByObjectProperty.add(backwardRelation,
					backwardTarget)) {
				otherNo.incrementAndGet();

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
							backwardRelation, mark(context, backwardTarget.getMarkers())));

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

			otherRules.incrementAndGet();
			if (context.forwardLinksByObjectProperty.add(forwardRelation,
					forwardTarget)) {
				otherNo.incrementAndGet();

				/* compose the link with all backward links */
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

		
		public Void visit(SuperClassExpression superClassExpression) {

			Marked<IndexedClassExpression> mce = superClassExpression
					.getClassExpression();
			Markers markers = mce.getMarkers();
			IndexedClassExpression ice = mce.getKey();

			if (!markers.isDefinite() && markers.getMarkers().contains(context)) {
				mce = ice;
				markers = ice.getMarkers();
			}

			otherRules.incrementAndGet();
			if (context.superClassExpressions.add(mce)) {
				otherNo.incrementAndGet();
				
				processClassExpression(ice, markers);
				if (superClassExpression.needsDecomposition)
					ice.accept(new ClassExpressionDecomposer(markers));
			}
			return null;
		}

		private void processClassExpression(IndexedClassExpression ice, Markers markers) {

			if (secondPhase && markers.isDefinite() && context.root instanceof IndexedClass) {
				if (!context.newSubsumption && context.secondPhase) {
					context.newSubsumption = true;
					newSubsumptions.incrementAndGet();
				}
				if (ice instanceof IndexedClass) {
					if (!context.newSuperClass) {
						context.newSuperClass = true;
						newSuperClasses.incrementAndGet();
					}
					confirmedSubsumptions.incrementAndGet();
				}
			}
			


			/* process subsumptions */
			if (ice.getToldSuperClassExpressions() != null) {
				for (IndexedClassExpression implied : ice
						.getToldSuperClassExpressions())
					enqueue(context,
							new SuperClassExpression(mark(implied, markers)));
			}

			/* process negative conjunctions */
			if (ice.getNegConjunctionsByConjunct() != null) {
				for (IndexedClassExpression common : new LazySetIntersection<IndexedClassExpression>(
						ice.getNegConjunctionsByConjunct().keySet(),
						context.superClassExpressions.keySet())) {
					Markers intersection = markersIntersection(markers, context.superClassExpressions.get(common).getMarkers());
					if (intersection != null)
						enqueue(context, new SuperClassExpression(
								mark((IndexedClassExpression) ice.getNegConjunctionsByConjunct().get(common), intersection), false));
				}

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
							mark((IndexedClassExpression) e, markers));
			}
		}

		private class ClassExpressionDecomposer implements
				IndexedClassExpressionVisitor<Void> {

			private final Markers markers;

			public ClassExpressionDecomposer(Markers markers) {
				this.markers = markers;
			}

			public Void visit(IndexedClass ice) {
				// TODO propagate bottom backwards
				if (ice == owlNothing && markers.isDefinite()) {
					context.isSatisfiable = false;
				}
				
				// optimization for finding more non-empty classes in Stage 1
				
//				if (context.reachable.isDefinite() && markers.isDefinite() ) {
//					SaturatedClassExpression target = getCreateContext(ice);
//					enqueue(target, new Reachability(DefiniteMarkers.INSTANCE));
//				}
				
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
				enqueue(target, new BackwardLink(ice.getRelation(), mark(context, markers)));
				if (context.reachable != NonDefiniteMarkers.QUESTION_MARKERS) {
					Markers intersection = markersIntersection(markers, context.reachable);
					if (intersection != null) {
						// make sure markers are not mutable
						if (intersection instanceof HashSetMarkers)
							intersection = new NonDefiniteMarkers(new ArraySet<Marker> (intersection.getMarkers()));
						enqueue(target, new Reachability(intersection));
					}
						
				}
				return null;
			}

			public Void visit(IndexedDataHasValue element) {
				return null;
			}

			public Void visit(IndexedNominal element) {
				Markers intersection = markersIntersection(markers, context.reachable);
				if (intersection != null) {
					// make sure markers are not mutable
					if (intersection instanceof HashSetMarkers)
						intersection = new NonDefiniteMarkers(new ArraySet<Marker> (intersection.getMarkers()));
					enqueue(getCreateContext(element), new SubNominal(mark(context, intersection)));
				}
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
											context, backwardTarget.getMarkers())));
		}

		public Void visit(SubNominal subNominal) {
			if (context.subNominals == null) {
				context.subNominals = new MarkedHashSet<SaturatedClassExpression>();
			}

			Marked<SaturatedClassExpression> mce1 = subNominal
					.getClassExpression();
			otherRules.incrementAndGet();
			if (context.subNominals.add(mce1)) {
				otherNo.incrementAndGet();
				
				for (Marked<SaturatedClassExpression> mce2 : context.subNominals)
					if (mce1.getKey() != mce2.getKey()) {
						Markers intersection = markersIntersection(mce1.getMarkers(), mce2.getMarkers());
						if (intersection != null) {
							enqueue(mce1.getKey(), new SuperClassExpression(mark(mce2.getKey().getRoot(), intersection)));
							enqueue(mce2.getKey(), new SuperClassExpression(mark(mce1.getKey().getRoot(), intersection)));
						}
					}
				
//				if (mce1.getMarkers().isDefinite()) {

					if (mce1.getKey().getRoot() instanceof IndexedClass) {
						atomicSubNominal.incrementAndGet();
					}
					else if (mce1.getKey().getRoot() instanceof IndexedNominal) {
						nominalSubNominal.incrementAndGet();
					}
					else {
						complexSubNominal.incrementAndGet();
					}
//				}
			}

			return null;
		}

		public Void visit(Reachability reachability) {
			
			reachRules.incrementAndGet();
			
			Markers markers = reachability.getMarkers();
			Markers newMarkers = markersDifference(context.reachable, markers);
			if (newMarkers == null)
				return null;
				
			if (newMarkers.isDefinite()) {
				context.reachable = newMarkers;
				reachNo.incrementAndGet();
			}
			else {
				if (context.reachable == NonDefiniteMarkers.QUESTION_MARKERS) 
					context.reachable = new HashSetMarkers(QuestionMarker.INSTANCE);
				for (Marker m : newMarkers.getMarkers())
					context.reachable.getMarkers().add(m);
				reachNo.addAndGet(newMarkers.getMarkers().size());
			}
				
			/*
			 * The following iteration can be optimised by keeping all forward
			 * links and all super nominals.
			 */
			for (Marked<IndexedClassExpression> mce : context.superClassExpressions) {
				IndexedClassExpression ice = mce.getKey();

				// TODO use a visitor here
				if (ice instanceof IndexedObjectSomeValuesFrom) {
					Markers intersection = markersIntersection(
							newMarkers, mce.getMarkers());
					if (intersection != null)
						enqueue(getCreateContext(((IndexedObjectSomeValuesFrom) ice)
							.getFiller()), new Reachability(intersection));
				}
				else if (ice instanceof IndexedNominal) {
					Markers intersection = markersIntersection(
							newMarkers, mce.getMarkers());
					if (intersection != null)
					enqueue(getCreateContext(ice), new SubNominal(
							mark(context, intersection)));
				}
			}

			return null;
		}

	}
}
