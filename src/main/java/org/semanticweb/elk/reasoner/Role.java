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

import java.util.List;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Set;

import org.semanticweb.elk.syntax.ElkObjectPropertyExpression;
import org.semanticweb.elk.util.ArraySet;
import org.semanticweb.elk.util.Pair;

/**
 * Represents all occurrences of an ElkObjectPropertyExpression in an ontology  
 * 
 * @author Frantisek Simancik
 *
 */

class Role {
	protected final ElkObjectPropertyExpression objectPropertyExpression;
	protected final List<Role> toldSubRoles = new ArrayList<Role> ();
	protected final List<Role> toldSuperRoles = new ArrayList<Role> ();
	protected final List<Pair<Role, Role>> rightPropertyChains = new ArrayList<Pair<Role, Role>> ();
	protected final List<Pair<Role, Role>> leftPropertyChains = new ArrayList<Pair<Role, Role>> ();
	
	protected Set<Role> subRoles = null;
	
	
	/**
	 * Creates a Role representing objectPropertyExpression
	 * 
	 * @param objectPropertyExpression
	 */
	public Role(ElkObjectPropertyExpression objectPropertyExpression) {
		this.objectPropertyExpression  = objectPropertyExpression;
	}
	
	public ElkObjectPropertyExpression getObjectPropertyExpression() {
		return objectPropertyExpression;
	}

	public List<Role> getToldSubRoles() {
		return toldSubRoles;
	}
	
	public List<Role> getToldSuperRoles() {
		return toldSuperRoles;
	}

	public List<Pair<Role, Role>> getRightPropertyChains() {
		return rightPropertyChains;
	}

	public List<Pair<Role, Role>> getLeftPropertyChains() {
		return leftPropertyChains;
	}
	
	public Set<Role> getSubRoles() {
		if (subRoles == null)
			computeSubRoles();
		return subRoles;
	}
	
	protected void computeSubRoles() {
		subRoles = new ArraySet<Role> ();
		ArrayDeque<Role> queue = new ArrayDeque<Role> ();
		subRoles.add(this);
		queue.addLast(this);
		while (!queue.isEmpty()) {
			Role role = queue.removeLast();
			for (Role r : role.toldSubRoles)
				if (subRoles.add(r))
					queue.addLast(r);
		}
	}

	private static int nextHashCode_ = 0;
	private final int hash_ = nextHashCode_++;
	
	@Override
	public int hashCode() {
		return hash_;
	}
}