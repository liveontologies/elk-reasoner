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

import org.semanticweb.elk.reasoner.indexing.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.Quantifier;
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
			enqueueClass(context, ice);
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
			if (!context.linkQueue.isEmpty() || !context.classQueue.isEmpty())
				activateContext(context);
	}

	protected void enqueueClass(Context context, IndexedClassExpression ice) {
		if (!context.derived.contains(ice)) {
			context.classQueue.add(ice);
			activateContext(context);
		}
	}

	protected void enqueueLink(Context context, IndexedObjectProperty relation,
			Context linkTarget) {
		if (!context.linksByObjectProperty.contains(relation, linkTarget)) {
			context.linkQueue.add(new Pair<IndexedObjectProperty, Context>(
					relation, linkTarget));
			activateContext(context);
		}
	}
	
	protected void enqueuePropagation(Context context, IndexedObjectProperty relation,
			IndexedClassExpression propClass) {
		if (!context.propagationsByObjectProperty.contains(relation, propClass)) {
			context.propagationQueue.add(new Pair<IndexedObjectProperty, IndexedClassExpression>(
					relation, propClass));
			activateContext(context);
		}
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
				context.classQueue.poll();
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
			
			Pair<IndexedObjectProperty, IndexedClassExpression> propagation =
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

	protected void processClass(Context context, IndexedClassExpression ice) {
		if (context.derived.add(ice)) {
			processImplications(context, ice);
			processNegativeConjunctions(context, ice);
			processPositiveExistentials(context, ice);
			processNegativeExistentials(context, ice);
			
		}
	}
	
	protected void propagateOverLink(Context context, 
			IndexedObjectProperty linkRelation, Context linkTarget,
			IndexedObjectProperty propRelation, IndexedClassExpression propClass) {
		
		enqueueClass(linkTarget, propClass);
		
	// transitive object properties 	
//	/*	
		if (propRelation.isTransitive)
			enqueuePropagation(linkTarget, propRelation, propClass);
		else
			for (IndexedObjectProperty common : new LazySetIntersection<IndexedObjectProperty> (
					linkRelation.getSuperObjectProperties(), propRelation.getSubObjectProperties()))
				if (common.isTransitive)
					enqueuePropagation(linkTarget, common, propClass);
//	*/
	}

	protected void processLink(Context context, IndexedObjectProperty linkRelation,
			Context linkTarget) {
		if (context.linksByObjectProperty.add(linkRelation, linkTarget)) {
			for (IndexedObjectProperty propRelation : new LazySetIntersection<IndexedObjectProperty>(
					linkRelation.getSuperObjectProperties(), context.propagationsByObjectProperty.keySet()))
				for (IndexedClassExpression propClass : context.propagationsByObjectProperty.get(propRelation))
					propagateOverLink(context, linkRelation, linkTarget, propRelation, propClass);
		}
	}
	
	protected void processPropagation(Context context, IndexedObjectProperty propRelation, 
			IndexedClassExpression propClass) {
		if (context.propagationsByObjectProperty.add(propRelation, propClass))
			for (IndexedObjectProperty linkRelation : new LazySetIntersection<IndexedObjectProperty>(
					propRelation.getSubObjectProperties(), context.linksByObjectProperty.keySet()))
				for (Context linkTarget : context.linksByObjectProperty.get(linkRelation))
					propagateOverLink(context, linkRelation, linkTarget, propRelation, propClass);

	}

	protected void processImplications(Context context, IndexedClassExpression ice) {
		for (IndexedClassExpression implied : ice.superClassExpressions)
			enqueueClass(context, implied);
	}

	protected void processNegativeConjunctions(Context context, IndexedClassExpression ice) {
		for (IndexedClassExpression common : new LazySetIntersection<IndexedClassExpression>(
				ice.negConjunctionsByConjunct.keySet(), context.derived)) {
			for (IndexedClassExpression conclusion : ice.negConjunctionsByConjunct
					.get(common))
				enqueueClass(context, conclusion);
		}
	}

	protected void processPositiveExistentials(Context context, IndexedClassExpression ice) {
		for (Quantifier e : ice.posExistentials) {
			Context target = getContext(e.getElement());
			enqueueLink(target, e.getRelation(), context);
		}
	}

	protected void processNegativeExistentials(Context context, IndexedClassExpression ice) {
		for (Quantifier e : ice.negExistentials) {
			processPropagation(context, e.getRelation(), e.getElement());
		}
	}
}