/*
 * #%L
 * elk-reasoner
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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.indexing.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.indexing.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.IndexedPropertyComposition;
import org.semanticweb.elk.reasoner.indexing.IndexedPropertyExpression;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.syntax.ElkClass;
import org.semanticweb.elk.util.AbstractConcurrentComputation;
import org.semanticweb.elk.util.HashSetMultimap;
import org.semanticweb.elk.util.LazySetIntersection;

/**
 * Experimental version of Saturation Manager.
 * 
 * @author Frantisek Simancik
 * 
 */
public class ClassExpressionSaturation extends
		AbstractConcurrentComputation<SaturatedClassExpression> {
	
	AtomicInteger derivedNo = new AtomicInteger(0);
	AtomicInteger backLinkNo = new AtomicInteger(0);
	AtomicInteger propNo = new AtomicInteger(0);
	AtomicInteger forwLinkNo = new AtomicInteger(0);
	
	protected final static Logger LOGGER_ = Logger
			.getLogger(ClassExpressionSaturation.class);

	protected final OntologyIndex ontologyIndex;
	
	protected final IndexedClassExpression owlThing;

	public ClassExpressionSaturation(ExecutorService executor, int workerNo,
			OntologyIndex ontologyIndex) {
		super(executor, workerNo, 256, 512);
		this.ontologyIndex = ontologyIndex;
		for (IndexedClassExpression ice : ontologyIndex
				.getIndexedClassExpressions())
			ice.resetSaturated();
		
		owlThing = ontologyIndex
		.getIndexedClassExpression(ElkClass.ELK_OWL_THING);
	}

	public void submit(IndexedClassExpression root) {
		waitCapacity();
		getCreateContext(root);
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
					LOGGER_.trace("Created context for " + root);
				}
				enqueue(sce, root);
				
				if (owlThing != null && owlThing.occursNegatively())
					enqueue(sce, owlThing);
			}
		}
		return root.getSaturated();
	}

	protected void activateContext(SaturatedClassExpression context) {
		if (context.tryActivate()) {
			addJob(context);
		}
	}

	protected void deactivateContext(SaturatedClassExpression context) {
		if (context.tryDeactivate())
			if (!context.queue.isEmpty())
				activateContext(context);
	}

	protected void enqueue(Linkable target,
			Queueable item) {

		// so far SaturatedClassExpression is the only implementation of Linkable
		if (target instanceof SaturatedClassExpression) {
			SaturatedClassExpression context = (SaturatedClassExpression) target;
			
			// this check is not necessary
//			if (context.derived.contains(item))
//				return;

			context.queue.add(item);
			activateContext(context);
		}
	}
	
	@Override
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
	
	class QueueProcessor implements QueueableVisitor<Void> {
		private final SaturatedClassExpression context;
		
		QueueProcessor(SaturatedClassExpression context) {
			this.context = context;
		}
		
		protected void processClass(IndexedClassExpression ice) {
			derivedNo.incrementAndGet();
			
			// process subsumptions
			if (ice.getToldSuperClassExpressions() != null) {
				for (IndexedClassExpression implied : ice
						.getToldSuperClassExpressions())
					enqueue(context, implied);
			}

			// process negative conjunctions
			if (ice.getNegConjunctionsByConjunct() != null) {
				for (IndexedClassExpression common : new LazySetIntersection<IndexedClassExpression>(
						ice.getNegConjunctionsByConjunct().keySet(),
						context.derived))
					enqueue(context, new DecomposedClassExpression(ice.getNegConjunctionsByConjunct()
							.get(common)));
			}

			// process negative existentials
			if (context.backwardLinksByObjectProperty != null && ice.getNegExistentials() != null) {
				for (IndexedObjectSomeValuesFrom e : ice.getNegExistentials())
					new Propagation(e.getRelation(), new DecomposedClassExpression(e)).accept(this);
			}

		}
		
		protected void materializeIncomingLinks() {
			if (context.materializeIncomingLinks)
				return;
			
			context.materializeIncomingLinks = true;
			if (context.backwardLinksByObjectProperty != null)
			for (IndexedPropertyExpression linkRelation : context.backwardLinksByObjectProperty.keySet())
				if (linkRelation.getSaturated().propertyChainsByLeftSubProperty != null)
					for (Linkable target : context.backwardLinksByObjectProperty.get(linkRelation))
						enqueue(target, new ForwardLink(linkRelation, context));
		}
		
		public Void visit(BackwardLink backwardLink) {
			IndexedPropertyExpression linkRelation = backwardLink.getRelation();
			Linkable target = backwardLink.getTarget();
			
			if (context.backwardLinksByObjectProperty == null) {
				context.backwardLinksByObjectProperty = new HashSetMultimap<IndexedPropertyExpression, Linkable> ();
	
				//start deriving propagations
				for (IndexedClassExpression ice : context.derived)
					if (ice.getNegExistentials() != null)
						for (IndexedObjectSomeValuesFrom e : ice.getNegExistentials())
							new Propagation(e.getRelation(), new DecomposedClassExpression(e)).accept(this);
			}
			
			if (context.backwardLinksByObjectProperty.add(linkRelation, target)) {
				backLinkNo.incrementAndGet();
				if (context.propagationsByObjectProperty != null)
				for (IndexedPropertyExpression propRelation : new LazySetIntersection<IndexedPropertyExpression>(
						linkRelation.getSaturated().getSuperObjectProperties(),
						context.propagationsByObjectProperty.keySet()))
					for (Queueable carry : context.propagationsByObjectProperty
							.get(propRelation))
						enqueue(target, carry);
			
				if (context.materializeIncomingLinks && linkRelation.getSaturated().propertyChainsByLeftSubProperty != null)
					enqueue(target, new ForwardLink(linkRelation, context));

				if (linkRelation.getSaturated().propertyChainsByRightSubProperty != null && context.forwardLinksByObjectProperty != null)
					for (IndexedPropertyExpression forwardRelation : new LazySetIntersection<IndexedPropertyExpression>(
							linkRelation.getSaturated().propertyChainsByRightSubProperty.keySet(),
							context.forwardLinksByObjectProperty.keySet()))
						for (IndexedPropertyComposition ria : linkRelation.getSaturated().propertyChainsByRightSubProperty.get(forwardRelation))
							for (Linkable forwardTarget : context.forwardLinksByObjectProperty.get(forwardRelation))
								enqueue(forwardTarget, new BackwardLink(ria.getSuperProperty(), target));
			}

			return null;
		}
		
		public Void visit(Propagation propagation) {
			IndexedPropertyExpression propRelation = propagation.getRelation();
			Queueable carry = propagation.getCarry();
			
			if (context.propagationsByObjectProperty == null) {
				context.propagationsByObjectProperty = new HashSetMultimap<IndexedPropertyExpression, Queueable>();
				materializeIncomingLinks();
			}
			
			if (context.propagationsByObjectProperty.add(propRelation, carry)) {
				propNo.incrementAndGet();
				if (context.backwardLinksByObjectProperty != null)
				for (IndexedPropertyExpression linkRelation : new LazySetIntersection<IndexedPropertyExpression>(
						propRelation.getSaturated().getSubObjectProperties(),
						context.backwardLinksByObjectProperty.keySet()))
					for (Linkable target : context.backwardLinksByObjectProperty
							.get(linkRelation))
						enqueue(target, carry);
			}

			return null;
		}


		public Void visit(DecomposedClassExpression compositeClassExpression) {
			if (context.derived.add(compositeClassExpression.getClassExpression()))
				processClass(compositeClassExpression.getClassExpression());
			return null;
		}
		
		public Void visit(IndexedClassExpression indexedClassExpression) {
			if (context.derived.add(indexedClassExpression)) {
				processClass(indexedClassExpression);
				indexedClassExpression.accept(classExpressionDecomposer);
			}
			return null;
		}

		public Void visit(ForwardLink forwardLink) {
			IndexedPropertyExpression linkRelation = forwardLink.getRelation();
			Linkable target = forwardLink.getTarget();
			
			if (context.forwardLinksByObjectProperty == null) {
				context.forwardLinksByObjectProperty = new HashSetMultimap<IndexedPropertyExpression, Linkable>();
				materializeIncomingLinks();
			}
			
			if (context.forwardLinksByObjectProperty.add(linkRelation, target)) {
				forwLinkNo.incrementAndGet();
/*
				if (context.backwardLinksByObjectProperty != null)
				for (IndexedPropertyExpression backwardRelation : new LazySetIntersection<IndexedPropertyExpression>(
						linkRelation.getSaturated().propertyChainsByLeftSubProperty.keySet(),
						context.backwardLinksByObjectProperty.keySet()))
					for (IndexedPropertyChain ria : linkRelation.getSaturated().propertyChainsByLeftSubProperty.get(backwardRelation))
						for (Linkable backwardTarget : context.backwardLinksByObjectProperty.get(backwardRelation))
							enqueue(target, new BackwardLink(ria.getSuperProperty(), backwardTarget));
*/
			}

			return null;
		}

		private ClassExpressionDecomposer classExpressionDecomposer = new ClassExpressionDecomposer();
		
		class ClassExpressionDecomposer implements
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
				enqueue(getCreateContext(ice.getFiller()), new BackwardLink(ice.getRelation(),
						context));
				return null;
			}
		}
	}
	
	@Override
	public void waitCompletion() {
		super.waitCompletion();
		System.err.println("derived: " + derivedNo);
		System.err.println("backLnk: " + backLinkNo);
		System.err.println("  props: " + propNo);
		System.err.println("forwLnk: " + forwLinkNo);
		
	}
}
