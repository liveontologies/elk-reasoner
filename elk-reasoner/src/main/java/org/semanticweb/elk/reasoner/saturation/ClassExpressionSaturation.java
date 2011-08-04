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

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.indexing.ComplexRoleInclusion;
import org.semanticweb.elk.reasoner.indexing.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.indexing.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.IndexedPropertyExpression;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.syntax.ElkClass;
import org.semanticweb.elk.util.AbstractConcurrentComputation;
import org.semanticweb.elk.util.LazySetIntersection;

/**
 * Experimental version of Saturation Manager.
 * 
 * @author Frantisek Simancik
 * 
 */
public class ClassExpressionSaturation extends
		AbstractConcurrentComputation<SaturatedClassExpression> {

	protected final static Logger LOGGER_ = Logger
			.getLogger(ClassExpressionSaturation.class);

	protected final OntologyIndex ontologyIndex;

	public ClassExpressionSaturation(ExecutorService executor, int workerNo,
			OntologyIndex ontologyIndex) {
		super(executor, workerNo, 256, 512);
		this.ontologyIndex = ontologyIndex;
		for (IndexedClassExpression ice : ontologyIndex
				.getIndexedClassExpressions())
			ice.resetSaturated();
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
				IndexedClassExpression top = ontologyIndex
						.getIndexedClassExpression(ElkClass.ELK_OWL_THING);
				if (top != null && top.occursNegatively())
					enqueue(sce, top);
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
			if (context.derived.contains(item))
				return;
			
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
			if (ice.getNegExistentials() != null) {
				for (IndexedObjectSomeValuesFrom e : ice.getNegExistentials())
					enqueue(context, new Propagation(e.getRelation(), new DecomposedClassExpression(e)));
			}

		}
		
		public Void visit(BackwardLink backwardLink) {
			IndexedPropertyExpression linkRelation = backwardLink.getRelation();
			Linkable target = backwardLink.getTarget();
			if (context.linksByObjectProperty.add(linkRelation, target)) {
				for (IndexedPropertyExpression propRelation : new LazySetIntersection<IndexedPropertyExpression>(
						linkRelation.getSaturated().getSuperObjectProperties(),
						context.propagationsByObjectProperty.keySet()))
					for (Queueable carry : context.propagationsByObjectProperty
							.get(propRelation))
						enqueue(target, carry);

				for (ComplexRoleInclusion ria : linkRelation.getSaturated().getRightSubPropertyInComplexInclusions())
					for (IndexedPropertyExpression propRelation : new LazySetIntersection<IndexedPropertyExpression>(
							ria.getSuperProperty().getSaturated().getSuperObjectProperties(),
							context.propagationsByObjectProperty.keySet()))
						for (Queueable carry : context.propagationsByObjectProperty
								.get(propRelation))
							enqueue(target, new Propagation(ria.getLeftSubProperty(), carry));
			}

			return null;
		}
		
		public Void visit(Propagation propagation) {
			IndexedPropertyExpression propRelation = propagation.getRelation();
			Queueable carry = propagation.getCarry();
			if (context.propagationsByObjectProperty.add(propRelation, carry)) {
				for (IndexedPropertyExpression linkRelation : new LazySetIntersection<IndexedPropertyExpression>(
						propRelation.getSaturated().getSubObjectProperties(),
						context.linksByObjectProperty.keySet()))
					for (Linkable target : context.linksByObjectProperty
							.get(linkRelation))
						enqueue(target, carry);

				for (ComplexRoleInclusion ria : propRelation.getSaturated().getSuperPropertyInComplexInclusions()) {
					IndexedPropertyExpression rightSubProperty = ria.getRightSubProperty();
					if (!ria.isSafe())
						enqueue(context, new Propagation(rightSubProperty, new ForwardLink(rightSubProperty, context)));
					for (IndexedPropertyExpression linkRelation : new LazySetIntersection<IndexedPropertyExpression>(
							rightSubProperty.getSaturated().getSubObjectProperties(),
							context.linksByObjectProperty.keySet()))
						for (Linkable target : context.linksByObjectProperty
								.get(linkRelation))
							enqueue(target, new Propagation(ria.getLeftSubProperty(), carry));
				}
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
			enqueue(forwardLink.getTarget(), new BackwardLink(forwardLink.getRelation(), context));
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
}
