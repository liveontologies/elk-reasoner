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
import org.semanticweb.elk.util.Multimap;

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
			context = new Context(ice.canonicalIndexedClassExpression);
			Context previous = contextLookup.putIfAbsent(ice, context);
			if (previous != null)
				return (previous);
			enqueueConcept(context, ice);
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
			if (!context.linkQueue.isEmpty() || !context.conceptQueue.isEmpty())
				activateContext(context);
	}

	protected void enqueueConcept(Context context, IndexedClassExpression ice) {
		if (!context.derived.contains(ice)) {
			context.conceptQueue.add(ice);
			activateContext(context);
		}
	}

	protected void enqueueLink(Context context, IndexedObjectProperty relation,
			Context parent) {
		if (!context.linksToParents.contains(relation, parent)) {
			context.linkQueue.add(new Pair<IndexedObjectProperty, Context>(
					relation, parent));
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
			IndexedClassExpression indexedClassExpression = context.conceptQueue
					.poll();
			if (indexedClassExpression != null) {
				processConcept(context, indexedClassExpression);
				continue;
			}
			Pair<IndexedObjectProperty, Context> link = context.linkQueue
					.poll();
			if (link != null) {
				processLink(context, link);
				continue;
			}
			break;
		}
		deactivateContext(context);
		// }
	}

	protected void processConcept(Context context, IndexedClassExpression ice) {
		IndexedClassExpression canonical = ice.canonicalIndexedClassExpression;
		if (context.derived.add(canonical))
			for (IndexedClassExpression represented : canonical.representedIndexedClassExpressions) {
				processImplications(context, represented);
				processConjunctions(context, represented);
				processExistentials(context, represented);
				processUniversals(context, represented);
			}
	}

	protected void processLink(Context context,
			Pair<IndexedObjectProperty, Context> link) {
		IndexedObjectProperty relation = link.getFirst();
		Context target = link.getSecond();
		if (context.linksToParents.add(relation, target)) {
			for (IndexedClassExpression common : new LazySetIntersection<IndexedClassExpression>(
					relation.getPropagations().keySet(), context.derived))
				for (IndexedClassExpression conclusion : relation
						.getPropagations().get(common))
					enqueueConcept(target, conclusion);
		}
	}

	void processImplications(Context context, IndexedClassExpression ice) {
		for (IndexedClassExpression implied : ice.superClassExpressions)
			enqueueConcept(context, implied);
	}

	void processConjunctions(Context context, IndexedClassExpression ice) {
		for (IndexedClassExpression common : new LazySetIntersection<IndexedClassExpression>(
				ice.negConjunctionsByConjunct.keySet(), context.derived)) {
			for (IndexedClassExpression conclusion : ice.negConjunctionsByConjunct
					.get(common))
				enqueueConcept(context, conclusion);
		}
	}

	void processExistentials(Context context, IndexedClassExpression ice) {
		for (Quantifier e : ice.posExistentials) {
			Context target = getContext(e.getElement());
			enqueueLink(target, e.getRelation(), context);
		}
	}

	void processUniversals(Context context, IndexedClassExpression ice) {
		Multimap<IndexedObjectProperty, IndexedClassExpression> universals = ice.negExistentialsByObjectProperty;
		Multimap<IndexedObjectProperty, Context> linksToParents = context.linksToParents;
		for (IndexedObjectProperty parentProperty : new LazySetIntersection<IndexedObjectProperty>(
				universals.keySet(), linksToParents.keySet()))
			for (Context target : linksToParents.get(parentProperty))
				for (IndexedClassExpression conclusion : universals
						.get(parentProperty))
					enqueueConcept(target, conclusion);
	}
}
