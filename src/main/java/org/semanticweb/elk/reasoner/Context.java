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
import org.semanticweb.elk.util.Pair;


/**
 * @author Frantisek Simancik
 *
 */
class Context implements DerivableVisitor<Boolean> {
	Set<Concept> derivedConcepts = new ArraySet<Concept> ();
	Index<Role, Context> forwardLinks = new HashIndex<Role, Context> ();
//	ArraySet<ForwardLink> forwardLinks=  new ArraySet<ForwardLink> ();
	Index<Role, Context> backwardLinks = new HashIndex<Role, Context> ();
	
	ArrayList<Derivable> queue = new ArrayList<Derivable> ();
	
	boolean saturated = true;

	public Boolean visit(Concept concept) {
		return derivedConcepts.add(concept);
	}

	public Boolean visit(ForwardLink forwardLink) {
		return forwardLinks.add(forwardLink);
	}

	public Boolean visit(BackwardLink backwardLink) {
		return backwardLinks.add(backwardLink);
	}
}

class Link extends Pair<Role, Context> {
	public Link(Role role, Context context) {
		super(role, context);
	}
	
	public Role getRole() {
		return first;
	}
	
	public Context getContext() {
		return second;
	}
}

class ForwardLink extends Link implements Derivable {
	public ForwardLink(Role role, Context context) {
		super(role, context);
	}

	public <O> O accept(DerivableVisitor<O> visitor) {
		return visitor.visit(this);
	}
}

class BackwardLink extends Link implements Derivable {
	public BackwardLink(Role role, Context context) {
		super(role, context);
	}

	public <O> O accept(DerivableVisitor<O> visitor) {
		return visitor.visit(this);
	}
}