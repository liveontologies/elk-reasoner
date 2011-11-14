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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.collections.Multimap;

/**
 * Objects of this class are used to manage subsumption relations between class
 * expressions that are derived during saturation. Besides storing consequences,
 * they also provide facilities for managing the processing of new derivations,
 * ensuring that only new derivations are used when searching for applicable
 * derivation rules.
 * 
 * @author Frantisek Simancik
 */
public class SaturatedClassExpression implements Linkable {

	protected final IndexedClassExpression root;

	protected final Queue<Queueable> queue;

	// TODO use Derivable instead of IndexedClassExpression here
	protected final Set<IndexedClassExpression> derived;

	protected Multimap<IndexedPropertyChain, Linkable> backwardLinksByObjectProperty;

	protected Multimap<IndexedPropertyChain, Linkable> forwardLinksByObjectProperty;

	protected Multimap<IndexedPropertyChain, Queueable> propagationsByObjectProperty;

	protected boolean satisfiable = true;

	protected boolean saturated = false;

	/**
	 * If set to true, then composition rules will be applied to derive all
	 * incoming links. This is usually needed only when at least one propagation
	 * has been derived at this object.
	 */
	protected boolean deriveBackwardLinks = false;

	/**
	 * A context is active iff its queue is not empty or it is being processed.
	 */
	private AtomicBoolean isActive;

	public SaturatedClassExpression(IndexedClassExpression root) {
		this.root = root;
		this.queue = new ConcurrentLinkedQueue<Queueable>();
		this.derived = new ArrayHashSet<IndexedClassExpression>(13);
		this.isActive = new AtomicBoolean(false);
	}

	public IndexedClassExpression getRoot() {
		return root;
	}

	public boolean isSatisfiable() {
		return satisfiable;
	}

	/**
	 * @return the set of derived indexed class expressions
	 */
	public Set<IndexedClassExpression> getSuperClassExpressions() {
		return derived;
	}

	/**
	 * Sets the context as active if it was false. This method is thread safe:
	 * for two concurrent executions only one succeeds.
	 * 
	 * @return true if the context was not active; returns false otherwise
	 */
	boolean tryActivate() {
		if (isActive.get())
			return false;
		return isActive.compareAndSet(false, true);
	}

	/**
	 * Sets the context as not active if it was active. This method is thread
	 * safe: for two concurrent executions only one succeeds.
	 * 
	 * @return true if the context was active; returns false otherwise
	 */
	boolean tryDeactivate() {
		if (!isActive.get())
			return false;
		return isActive.compareAndSet(true, false);
	}

	/**
	 * Marks this context as saturated. The derivable set should not change from
	 * this point.
	 */
	public void setSaturated() {
		this.saturated = true;
	}

	/**
	 * Tests if this context is saturated
	 * 
	 * @return <tt>true</tt> if this context is saturated and <tt>false</tt>
	 *         otherwise
	 */
	public boolean isSaturated() {
		return this.saturated;
	}

}
