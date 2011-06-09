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
import org.semanticweb.elk.reasoner.indexing.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.indexing.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.util.AbstractConcurrentComputation;
import org.semanticweb.elk.util.LazySetIntersection;
import org.semanticweb.elk.util.Pair;

/**
 * Experimental version of Saturation Manager.
 * 
 * @author Frantisek Simancik
 *
 */
public class ClassExpressionSaturation extends AbstractConcurrentComputation<SaturatedClassExpression> {

	protected final static Logger logger = Logger
			.getLogger(ClassExpressionSaturation.class);

	public ClassExpressionSaturation(ExecutorService executor, int workerNo) {
		super(executor, workerNo, 0, 512);
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
	protected SaturatedClassExpression getCreateContext(IndexedClassExpression root) {
		if (root.getSaturated() == null) {
			SaturatedClassExpression sce = new SaturatedClassExpression(root);
			if (root.setSaturated(sce)) {
				if (logger.isTraceEnabled()) {
					logger.trace("Created context for " + root);
				}
				enqueueDerived(sce, root, true);
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
			if (!context.linkQueue.isEmpty()
					|| !context.propagationQueue.isEmpty()
					|| !context.positivelyDerivedQueue.isEmpty()
					|| !context.negativelyDerivedQueue.isEmpty())
				activateContext(context);
	}

	protected void enqueueDerived(SaturatedClassExpression context,
			IndexedClassExpression ice, boolean polarity) {
		if (!context.derived.contains(ice)) {
			if (polarity)
				context.positivelyDerivedQueue.add(ice);
			else
				context.negativelyDerivedQueue.add(ice);
			activateContext(context);
		}
	}

	protected void enqueueLink(SaturatedClassExpression context,
			IndexedObjectProperty relation, SaturatedClassExpression linkTarget) {
		// if (!context.linksByObjectProperty.contains(relation, linkTarget)) {
		context.linkQueue
				.add(new Pair<IndexedObjectProperty, SaturatedClassExpression>(
						relation, linkTarget));
		activateContext(context);
		// }
	}

	protected void enqueuePropagation(SaturatedClassExpression context,
			IndexedObjectProperty relation,
			IndexedObjectSomeValuesFrom propClass) {
		// if (!context.propagationsByObjectProperty.contains(relation,
		// propClass)) {
		context.propagationQueue
				.add(new Pair<IndexedObjectProperty, IndexedObjectSomeValuesFrom>(
						relation, propClass));
		activateContext(context);
		// }
	}

	@Override
	protected void process(SaturatedClassExpression context) {
		// synchronized (context) {
		for (;;) {
			IndexedClassExpression ice = context.positivelyDerivedQueue.poll();
			if (ice != null) {
				processClass(context, ice, true);
				continue;
			}

			ice = context.negativelyDerivedQueue.poll();
			if (ice != null) {
				processClass(context, ice, false);
				continue;
			}

			Pair<IndexedObjectProperty, SaturatedClassExpression> link = context.linkQueue
					.poll();
			if (link != null) {
				processLink(context, link.getFirst(), link.getSecond());
				continue;
			}

			Pair<IndexedObjectProperty, IndexedObjectSomeValuesFrom> propagation = context.propagationQueue
					.poll();
			if (propagation != null) {
				processPropagation(context, propagation.getFirst(), propagation
						.getSecond());
				continue;
			}

			break;
		}
		deactivateContext(context);
		// }
	}

	protected void propagateOverLink(SaturatedClassExpression context,
			IndexedObjectProperty linkRelation,
			SaturatedClassExpression linkTarget,
			IndexedObjectProperty propRelation,
			IndexedObjectSomeValuesFrom propClass) {

		enqueueDerived(linkTarget, propClass, false);

		// transitive object properties

		SaturatedObjectProperty satLinkRelation = linkRelation.getSaturated();
		SaturatedObjectProperty satPropRelation = propRelation.getSaturated();

		if (satLinkRelation.getTransitiveSuperObjectProperties() != null
				&& satPropRelation.getTransitiveSubObjectProperties() != null) {
			if (propRelation.isTransitive())
				enqueuePropagation(linkTarget, propRelation, propClass);
			else
				for (IndexedObjectProperty common : new LazySetIntersection<IndexedObjectProperty>(
						satLinkRelation.getTransitiveSuperObjectProperties(),
						satPropRelation.getTransitiveSubObjectProperties()))
					enqueuePropagation(linkTarget, common, propClass);
		}
	}

	protected void processLink(SaturatedClassExpression context,
			IndexedObjectProperty linkRelation,
			SaturatedClassExpression linkTarget) {
		if (context.linksByObjectProperty.add(linkRelation, linkTarget)) {
			for (IndexedObjectProperty propRelation : new LazySetIntersection<IndexedObjectProperty>(
					linkRelation.getSaturated().getSuperObjectProperties(),
					context.propagationsByObjectProperty.keySet()))
				for (IndexedObjectSomeValuesFrom propClass : context.propagationsByObjectProperty
						.get(propRelation))
					propagateOverLink(context, linkRelation, linkTarget,
							propRelation, propClass);
		}
	}

	protected void processPropagation(SaturatedClassExpression context,
			IndexedObjectProperty propRelation,
			IndexedObjectSomeValuesFrom propClass) {
		if (context.propagationsByObjectProperty.add(propRelation, propClass)) {
			for (IndexedObjectProperty linkRelation : new LazySetIntersection<IndexedObjectProperty>(
					propRelation.getSaturated().getSubObjectProperties(),
					context.linksByObjectProperty.keySet()))
				for (SaturatedClassExpression linkTarget : context.linksByObjectProperty
						.get(linkRelation))
					propagateOverLink(context, linkRelation, linkTarget,
							propRelation, propClass);
		}
	}

	protected void processClass(SaturatedClassExpression context,
			IndexedClassExpression ice, boolean polarity) {
		if (context.derived.add(ice)) {

			if (polarity)
				ice.accept(new PositivelyDerivedProcessor(context));

			// process subsumptions
			if (ice.getToldSuperClassExpressions() != null) {
				for (IndexedClassExpression implied : ice
						.getToldSuperClassExpressions())
					enqueueDerived(context, implied, true);
			}

			// process negative conjunctions
			if (ice.getNegConjunctionsByConjunct() != null) {
				for (IndexedClassExpression common : new LazySetIntersection<IndexedClassExpression>(
						ice.getNegConjunctionsByConjunct().keySet(),
						context.derived))
					for (IndexedClassExpression conclusion : ice
							.getNegConjunctionsByConjunct().get(common))
						enqueueDerived(context, conclusion, false);
			}

			// process negative existentials
			if (ice.getNegExistentials() != null) {
				for (IndexedObjectSomeValuesFrom e : ice.getNegExistentials())
					processPropagation(context, e.getRelation(), e);
			}
		}
	}

	protected class PositivelyDerivedProcessor implements
			IndexedClassExpressionVisitor<Void> {
		final SaturatedClassExpression context;

		PositivelyDerivedProcessor(SaturatedClassExpression context) {
			this.context = context;
		}

		public Void visit(IndexedClass ice) {
			return null;
		}

		public Void visit(IndexedObjectIntersectionOf ice) {
			for (IndexedClassExpression conjunct : ice.getConjuncts())
				enqueueDerived(context, conjunct, true);

			return null;
		}

		public Void visit(IndexedObjectSomeValuesFrom ice) {
			enqueueLink(getCreateContext(ice.getFiller()), ice.getRelation(), context);

			return null;
		}
	}
	
	
}
