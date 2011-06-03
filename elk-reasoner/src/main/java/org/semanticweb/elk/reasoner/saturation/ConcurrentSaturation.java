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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

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
public class ConcurrentSaturation implements Saturation {

	// lookup table for contexts
	protected final ConcurrentMap<IndexedClassExpression, Context> contextLookup;
	// queue for active contexts
	protected final Queue<Context> activeContexts;
	// the size of the queue
	protected final AtomicInteger activeContextCount;
	// bounded buffer for concepts added for saturation
	protected final BlockingQueue<IndexedClassExpression> conceptBuffer;

	
	public ConcurrentSaturation(int bufferSize) {
		this.contextLookup = new ConcurrentHashMap<IndexedClassExpression, Context>();
		this.activeContexts = new ConcurrentLinkedQueue<Context>();
		this.activeContextCount = new AtomicInteger(0);
		conceptBuffer = new ArrayBlockingQueue<IndexedClassExpression>(
				bufferSize);
	}

	public void addTarget(IndexedClassExpression root) {
		try {
			conceptBuffer.put(root);
		} catch (InterruptedException e) {
		}
	}

	public Context getContext(IndexedClassExpression ice) {
		Context context = contextLookup.get(ice);
		if (context == null) {
			context = new Context(ice);
			Context previous = contextLookup.putIfAbsent(ice, context);
			if (previous != null)
				return (previous);
			enqueueDerived(context, ice);
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
			if (!context.linkQueue.isEmpty() || !context.derivedQueue.isEmpty())
				activateContext(context);
	}

	protected void enqueueDerived(Context context, IndexedClassExpression ice) {
		if (!context.derived.contains(ice)) {
			context.derivedQueue.add(ice);
			activateContext(context);
		}
	}

	protected void enqueueLink(Context context, IndexedObjectProperty relation,
			Context linkTarget) {
//		if (!context.linksByObjectProperty.contains(relation, linkTarget)) {
			context.linkQueue.add(new Pair<IndexedObjectProperty, Context>(
					relation, linkTarget));
			activateContext(context);
//		}
	}
	
	protected void enqueuePropagation(Context context, IndexedObjectProperty relation,
			IndexedObjectSomeValuesFrom propClass) {
//		if (!context.propagationsByObjectProperty.contains(relation, propClass)) {
			context.propagationQueue.add(new Pair<IndexedObjectProperty, IndexedObjectSomeValuesFrom>(
					relation, propClass));
			activateContext(context);
//		}
	}

	public void compute() {
		for (;;) {
			Context context = activeContexts.poll();
			if (context != null) {
				process(context);
				continue;
			}
			IndexedClassExpression nextTarget = conceptBuffer.poll();
			if (nextTarget != null) {
				getContext(nextTarget);
				continue;
			}
			break;
		}
	}

	protected void process(Context context) {
		// synchronized (context) {
		for (;;) {
			IndexedClassExpression indexedClassExpression = 
				context.derivedQueue.poll();
			if (indexedClassExpression != null) {
				processClass(context, indexedClassExpression);
				continue;
			}

			Pair<IndexedObjectProperty, Context> link =
				context.linkQueue.poll();
			if (link != null) {
				processLink(context, link.getFirst(), link.getSecond());
				continue;
			}
			
			Pair<IndexedObjectProperty, IndexedObjectSomeValuesFrom> propagation =
				context.propagationQueue.poll();
			if (propagation != null) {
				processPropagation(context, propagation.getFirst(), propagation.getSecond());
				continue;
			}
			
			break;
		}
		deactivateContext(context);
		// }
	}

	protected void propagateOverLink(Context context, 
			IndexedObjectProperty linkRelation, Context linkTarget,
			IndexedObjectProperty propRelation, IndexedObjectSomeValuesFrom propClass) {
		
		enqueueDerived(linkTarget, propClass);
		
	// transitive object properties 	
		if (propRelation.isTransitive)
			enqueuePropagation(linkTarget, propRelation, propClass);
		
		else if (linkRelation.transitiveSuperObjectProperties != null && 
				propRelation.transitiveSubObjectProperties != null)
			for (IndexedObjectProperty common : new LazySetIntersection<IndexedObjectProperty> (
					linkRelation.transitiveSuperObjectProperties, 
					propRelation.transitiveSubObjectProperties))
				enqueuePropagation(linkTarget, common, propClass);
	}

	protected void processLink(Context context, IndexedObjectProperty linkRelation,
			Context linkTarget) {
		if (context.linksByObjectProperty.add(linkRelation, linkTarget)) {
			for (IndexedObjectProperty propRelation : new LazySetIntersection<IndexedObjectProperty>(
					linkRelation.inferredSuperObjectProperties, context.propagationsByObjectProperty.keySet()))
				for (IndexedObjectSomeValuesFrom propClass : context.propagationsByObjectProperty.get(propRelation))
					propagateOverLink(context, linkRelation, linkTarget, propRelation, propClass);
		}
	}
	
	protected void processPropagation(Context context, IndexedObjectProperty propRelation, 
			IndexedObjectSomeValuesFrom propClass) {
		if (context.propagationsByObjectProperty.add(propRelation, propClass)) {
			for (IndexedObjectProperty linkRelation : new LazySetIntersection<IndexedObjectProperty>(
					propRelation.inferredSubObjectProperties, context.linksByObjectProperty.keySet()))
				for (Context linkTarget : context.linksByObjectProperty.get(linkRelation))
					propagateOverLink(context, linkRelation, linkTarget, propRelation, propClass);
		}
	}
	
	protected void processClass(Context context, IndexedClassExpression ice) {
		if (context.derived.add(ice)) {
			ice.accept(new ClassProcessor(context));
		}
	}
	
	protected class ClassProcessor implements IndexedClassExpressionVisitor<Void> {
		final Context context;
		ClassProcessor(Context context) {
			this.context = context;
		}
		
		public Void visit(IndexedClass ice) {
			doAlways(ice);
			return null;
		}
		public Void visit(IndexedObjectIntersectionOf ice) {
			doAlways(ice);
			
			if (ice.occursPositively())
				for (IndexedClassExpression conjunct : ice.conjuncts)
					enqueueDerived(context, conjunct);
			
			return null;
		}
		public Void visit(IndexedObjectSomeValuesFrom ice) {
			doAlways(ice);
			
			if (ice.occursPositively())
				enqueueLink(getContext(ice.filler), ice.relation, context);
				
			return null;
		}
		
		protected void doAlways(IndexedClassExpression ice) {
			//process subsumptions
			if (ice.superClassExpressions != null) {
				for (IndexedClassExpression implied : ice.superClassExpressions)
					enqueueDerived(context, implied);
			}
			
			//process negative conjunctions 
			if (ice.negConjunctionsByConjunct != null) {
				for (IndexedClassExpression common : new LazySetIntersection<IndexedClassExpression>(
						ice.negConjunctionsByConjunct.keySet(), context.derived)) {
					for (IndexedClassExpression conclusion : ice.negConjunctionsByConjunct
							.get(common))
						enqueueDerived(context, conclusion);
				}
			}
			
			//process negative existentials
			if (ice.negExistentialsWithRelation != null) {
				for (Pair<IndexedObjectSomeValuesFrom, IndexedObjectProperty> e : ice.negExistentialsWithRelation) {
					processPropagation(context, e.getSecond(), e.getFirst());
				}
			}
		}
	}
}