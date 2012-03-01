/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.saturation.elkrulesystem;

import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.rulesystem.Context;
import org.semanticweb.elk.reasoner.saturation.rulesystem.Queueable;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.collections.HashSetMultimap;
import org.semanticweb.elk.util.collections.Multimap;

public class ContextEl extends Context {
	
	public final Set<IndexedClassExpression> superClassExpressions;
	
	public Multimap<IndexedPropertyChain, ContextEl> backwardLinksByObjectProperty;
	
	public Multimap<IndexedPropertyChain, ContextEl> forwardLinksByObjectProperty;
	
	protected Multimap<IndexedPropertyChain, Queueable<? extends ContextEl>> propagationsByObjectProperty;
	
	/**
	 * If set to true, then composition rules will be applied to derive all
	 * incoming links. This is usually needed only when at least one propagation
	 * has been derived at this object.
	 */
	protected boolean deriveBackwardLinks = false;

	/**
	 * If set to true, then propagations will be derived in this context. This
	 * is needed only when there is at least one backward link.
	 */
	protected boolean derivePropagations = false;
	
	protected volatile boolean isSatisfiable = true;

	public ContextEl(IndexedClassExpression root) {
		super(root);
		this.superClassExpressions = new ArrayHashSet<IndexedClassExpression>(
				13);
	}
	
	/**
	 * @return the set of derived indexed class expressions
	 */
	public Set<IndexedClassExpression> getSuperClassExpressions() {
		return superClassExpressions;
	}

	public boolean isSatisfiable() {
		return isSatisfiable;
	}

	public void setSatisfiable(boolean satisfiable) {
		isSatisfiable = satisfiable;
	}
	
    /**
     * If set to true, then composition rules will be applied to derive all
     * incoming links. This is usually needed only when at least one propagation
     * has been derived at this object.
     */
	public boolean getDeriveBackwardLinks() {
		return deriveBackwardLinks;
	}

	public void setDeriveBackwardLinks(boolean deriveBackwardLinks) {
		this.deriveBackwardLinks = deriveBackwardLinks;
	}

	public Multimap<IndexedPropertyChain, ContextEl> getBackwardLinksByObjectProperty() {
		return backwardLinksByObjectProperty;
	}
	
	/**
     * Initialize the set of propagations with the empty set
     */
    public void initBackwardLinksByProperty() {
    	backwardLinksByObjectProperty = new HashSetMultimap<IndexedPropertyChain, ContextEl>();
    }
    
	public Multimap<IndexedPropertyChain, ContextEl> getForwardLinksByObjectProperty() {
		return forwardLinksByObjectProperty;
	}
	
    public void initForwardLinksByProperty() {
    	forwardLinksByObjectProperty = new HashSetMultimap<IndexedPropertyChain, ContextEl>();
    }
    
	/**
	 * If set to true, then propagations will be derived in this context. This
	 * is needed only when there is at least one backward link.
	 */
	public boolean getDerivePropagations() {
		return derivePropagations;
	}

	public void setDerivePropagations(boolean derivePropagations) {
		this.derivePropagations = derivePropagations;
	}
    
	public Multimap<IndexedPropertyChain, Queueable<? extends ContextEl>> getPropagationsByObjectProperty() {
		return propagationsByObjectProperty;
	}
	
	/**
     * Initialize the set of propagations with the empty set
     */
    public void initPropagationsByProperty() {
    	propagationsByObjectProperty = new HashSetMultimap<IndexedPropertyChain, Queueable<? extends ContextEl>>();
    }

}
