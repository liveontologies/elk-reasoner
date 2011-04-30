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
package org.semanticweb.elk.reasoner;

import java.util.ArrayList;
import java.util.Set;

import org.semanticweb.elk.util.ArraySet;
import org.semanticweb.elk.util.Index;
import org.semanticweb.elk.util.HashIndex;
import org.semanticweb.elk.util.Triple;


/**
 * @author Frantisek Simancik
 *
 */

class Context implements Queueable {
	Set<Concept> derivedConcepts = new ArraySet<Concept> ();
	Index<Role, Link> forwardLinks = new HashIndex<Role, Link> ();
	Index<Role, Link> backwardLinks = new HashIndex<Role, Link> ();
	
	ArrayList<Concept> localQueue = new ArrayList<Concept> ();
	
	boolean saturated = true;
	
	public void accept(QueueableVisitor visitor) {
		visitor.visit(this);
	}
	
	private static int nextHashCode_ = 0;
	private final int hash_ = nextHashCode_++;
	
	@Override
	public int hashCode() {
		return hash_;
	}
}

class Link extends Triple<Context, Context, Role> implements Queueable {
	int length;
	
	public Link(Context source, Context target, Role role) {
		this(source, target, role, 1);
	}
	
	public Link(Context source, Context target, Role role, int length) {
		super(source, target, role);
		this.length = length;
	}
	
	public Context getSource() {
		return first;
	}
	
	public Context getTarget() {
		return second;
	}
	
	public Role getRole() {
		return third;
	}

	public void accept(QueueableVisitor visitor) {
		visitor.visit(this);
	}
}