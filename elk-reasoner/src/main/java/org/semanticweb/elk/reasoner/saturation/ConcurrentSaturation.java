/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Oxford University Computing Laboratory
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
/**
 * @author Yevgeny Kazakov, May 13, 2011
 */
package org.semanticweb.elk.reasoner.saturation;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.indexing.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.indexing.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.util.LazySetIntersection;
import org.semanticweb.elk.util.Pair;

/**
 * @author Yevgeny Kazakov
 * 
 */
class ConcurrentSaturation implements SaturationComputation {

	// lookup table for contexts
	protected final ConcurrentMap<IndexedClassExpression, Context> contextLookup;
	// queue for active contexts
	protected final Queue<Context> activeContexts;
	// the size of the queue
	protected final AtomicInteger activeContextCount;
	// logger for events
	protected final static Logger logger = Logger
			.getLogger(ConcurrentSaturation.class);

	ConcurrentSaturation() {
		this.contextLookup = new ConcurrentHashMap<IndexedClassExpression, Context>();
		this.activeContexts = new ConcurrentLinkedQueue<Context>();
		this.activeContextCount = new AtomicInteger(0);
	}

	public void addTarget(IndexedClassExpression root) {
		getCreateContext(root);
	}

	public Context getContext(IndexedClassExpression ice) {
		return contextLookup.get(ice);
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
	Context getCreateContext(IndexedClassExpression root) {
		Context context = contextLookup.get(root);
		if (context == null) {
			context = new Context(root);
			Context previous = contextLookup.putIfAbsent(root, context);
			if (previous != null)
				return (previous);
			if (logger.isTraceEnabled()) {
				logger.trace("Created context for " + root);
			}
			enqueueDerived(context, root, true);
		}
		return context;
	}

	private void activateContext(Context context) {
		if (context.tryActivate()) {
			activeContexts.add(context);
			activeContextCount.incrementAndGet();
		}
	}

	private void deactivateContext(Context context) {
		if (context.tryDeactivate())
			if (!context.linkQueue.isEmpty()
					|| !context.propagationQueue.isEmpty()
					|| !context.positivelyDerivedQueue.isEmpty()
					|| !context.negativelyDerivedQueue.isEmpty())
				activateContext(context);
	}

	protected void enqueueDerived(Context context, IndexedClassExpression ice,
			boolean polarity) {
		if (!context.derived.contains(ice)) {
			if (polarity)
				context.positivelyDerivedQueue.add(ice);
			else
				context.negativelyDerivedQueue.add(ice);
			activateContext(context);
		}
	}

	protected void enqueueLink(Context context, IndexedObjectProperty relation,
			Context linkTarget) {
		// if (!context.linksByObjectProperty.contains(relation, linkTarget)) {
		context.linkQueue.add(new Pair<IndexedObjectProperty, Context>(
				relation, linkTarget));
		activateContext(context);
		// }
	}

	protected void enqueuePropagation(Context context,
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

	public void compute() {
		for (;;) {
			Context context = activeContexts.poll();
			if (context != null) {
				process(context);
				continue;
			}
			break;
		}
	}

	protected void process(Context context) {
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

			Pair<IndexedObjectProperty, Context> link = context.linkQueue
					.poll();
			if (link != null) {
				processLink(context, link.getFirst(), link.getSecond());
				continue;
			}

			Pair<IndexedObjectProperty, IndexedObjectSomeValuesFrom> propagation = context.propagationQueue
					.poll();
			if (propagation != null) {
				processPropagation(context, propagation.getFirst(),
						propagation.getSecond());
				continue;
			}

			break;
		}
		deactivateContext(context);
		// }
	}

	protected void propagateOverLink(Context context,
			IndexedObjectProperty linkRelation, Context linkTarget,
			IndexedObjectProperty propRelation,
			IndexedObjectSomeValuesFrom propClass) {

		enqueueDerived(linkTarget, propClass, false);

		// transitive object properties

		if (linkRelation.transitiveSuperObjectProperties != null
				&& propRelation.transitiveSubObjectProperties != null) {
			if (propRelation.isTransitive)
				enqueuePropagation(linkTarget, propRelation, propClass);
			else
				for (IndexedObjectProperty common : new LazySetIntersection<IndexedObjectProperty>(
						linkRelation.transitiveSuperObjectProperties,
						propRelation.transitiveSubObjectProperties))
					enqueuePropagation(linkTarget, common, propClass);
		}
	}

	public static int derivedLinks = 0;

	protected void processLink(Context context,
			IndexedObjectProperty linkRelation, Context linkTarget) {
		if (context.linksByObjectProperty.add(linkRelation, linkTarget)) {
			derivedLinks++;
			for (IndexedObjectProperty propRelation : new LazySetIntersection<IndexedObjectProperty>(
					linkRelation.inferredSuperObjectProperties,
					context.propagationsByObjectProperty.keySet()))
				for (IndexedObjectSomeValuesFrom propClass : context.propagationsByObjectProperty
						.get(propRelation))
					propagateOverLink(context, linkRelation, linkTarget,
							propRelation, propClass);
		}
	}

	protected void processPropagation(Context context,
			IndexedObjectProperty propRelation,
			IndexedObjectSomeValuesFrom propClass) {
		if (context.propagationsByObjectProperty.add(propRelation, propClass)) {
			for (IndexedObjectProperty linkRelation : new LazySetIntersection<IndexedObjectProperty>(
					propRelation.inferredSubObjectProperties,
					context.linksByObjectProperty.keySet()))
				for (Context linkTarget : context.linksByObjectProperty
						.get(linkRelation))
					propagateOverLink(context, linkRelation, linkTarget,
							propRelation, propClass);
		}
	}

	protected void processClass(Context context, IndexedClassExpression ice,
			boolean polarity) {
		if (context.derived.add(ice)) {

			if (polarity)
				ice.accept(new PositivelyDerivedProcessor(context));

			// process subsumptions
			if (ice.superClassExpressions != null) {
				for (IndexedClassExpression implied : ice.superClassExpressions)
					enqueueDerived(context, implied, true);
			}

			// process negative conjunctions
			if (ice.negConjunctionsByConjunct != null) {
				for (IndexedClassExpression common : new LazySetIntersection<IndexedClassExpression>(
						ice.negConjunctionsByConjunct.keySet(), context.derived))
					for (IndexedClassExpression conclusion : ice.negConjunctionsByConjunct
							.get(common))
						enqueueDerived(context, conclusion, false);
			}

			// process negative existentials
			if (ice.negExistentialsWithRelation != null) {
				for (Pair<IndexedObjectSomeValuesFrom, IndexedObjectProperty> e : ice.negExistentialsWithRelation)
					processPropagation(context, e.getSecond(), e.getFirst());
			}
		}
	}

	protected class PositivelyDerivedProcessor implements
			IndexedClassExpressionVisitor<Void> {
		final Context context;

		PositivelyDerivedProcessor(Context context) {
			this.context = context;
		}

		public Void visit(IndexedClass ice) {
			return null;
		}

		public Void visit(IndexedObjectIntersectionOf ice) {
			for (IndexedClassExpression conjunct : ice.conjuncts)
				enqueueDerived(context, conjunct, true);

			return null;
		}

		public Void visit(IndexedObjectSomeValuesFrom ice) {
			enqueueLink(getCreateContext(ice.filler), ice.relation, context);

			return null;
		}
	}
}
