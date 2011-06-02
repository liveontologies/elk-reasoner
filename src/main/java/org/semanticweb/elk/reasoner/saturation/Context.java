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
package org.semanticweb.elk.reasoner.saturation;

import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.semanticweb.elk.reasoner.indexing.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.util.ArrayHashSet;
import org.semanticweb.elk.util.HashGenerator;
import org.semanticweb.elk.util.HashListMultimap;
import org.semanticweb.elk.util.HashSetMultimap;
import org.semanticweb.elk.util.Multimap;
import org.semanticweb.elk.util.Pair;

/**
 * Objects of this class are used to manage subsumption relations between class
 * expressions that are derived during saturation. Besides storing consequences,
 * they also provide facilities for managing the processing of new derivations,
 * ensuring that only new derivations are used when searching for applicable
 * derivation rules.
 * 
 * @author Frantisek Simancik
 */
public class Context {
	final IndexedClassExpression root;
	final Queue<IndexedClassExpression> derivedQueue;
	final Queue<Pair<IndexedObjectProperty, Context>> linkQueue;
	final Queue<Pair<IndexedObjectProperty, IndexedObjectSomeValuesFrom>> propagationQueue;
	final Set<IndexedClassExpression> derived;
	final Multimap<IndexedObjectProperty, Context> linksByObjectProperty;
	final Multimap<IndexedObjectProperty, IndexedObjectSomeValuesFrom> propagationsByObjectProperty;
	/**
	 * A context is active iff one of its queues is not empty or it is being
	 * processed.
	 */
	private AtomicBoolean isActive;

	public Context(IndexedClassExpression root) {
		this.root = root;
		this.derivedQueue = new ConcurrentLinkedQueue<IndexedClassExpression>();
		this.linkQueue = new ConcurrentLinkedQueue<Pair<IndexedObjectProperty, Context>>();
		this.propagationQueue = new ConcurrentLinkedQueue<Pair<IndexedObjectProperty, IndexedObjectSomeValuesFrom>>();
		this.derived = new ArrayHashSet<IndexedClassExpression>(13);
		this.linksByObjectProperty = new HashListMultimap<IndexedObjectProperty, Context>(
				1);
		this.propagationsByObjectProperty = new HashSetMultimap<IndexedObjectProperty, IndexedObjectSomeValuesFrom>(
				1);
		this.isActive = new AtomicBoolean(false);
	}

	public IndexedClassExpression getRoot() {
		return root;
	}

	/**
	 * Sets the context as active if it was false. This method is thread safe:
	 * for two concurrent executions only one succeeds.
	 * 
	 * @return true if the context was not active; returns false otherwise
	 */
	protected boolean tryActivate() {
		return isActive.compareAndSet(false, true);
	}

	/**
	 * Sets the context as not active if it was active. This method is thread
	 * safe: for two concurrent executions only one succeeds.
	 * 
	 * @return true if the context was active; returns false otherwise
	 */
	protected boolean tryDeactivate() {
		return isActive.compareAndSet(true, false);
	}

	/**
	 * @return the set of derived indexed class expressions
	 */
	public Set<IndexedClassExpression> getDerived() {
		return derived;
	}

	private final int hashCode_ = HashGenerator.generateNextHashCode();

	/**
	 * Get an integer hash code to be used for this object.
	 * 
	 * @return integer hash code
	 */
	@Override
	public final int hashCode() {
		return hashCode_;
	}
}