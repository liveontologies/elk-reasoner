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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;

import org.semanticweb.elk.syntax.ElkObjectPropertyExpression;
import org.semanticweb.elk.util.ArraySet;

class Role {
	//fields
	protected final ElkObjectPropertyExpression objectPropertyExpression;
	protected final Set<Role> toldSubRoles;
	protected Set<Role> subRoles = null;

	//methods
	Role(ElkObjectPropertyExpression objectPropertyExpression) {
		this.objectPropertyExpression  = objectPropertyExpression;
		toldSubRoles = new ArraySet<Role> ();
	}
	
	ElkObjectPropertyExpression getObjectPropertyExpression() {
		return objectPropertyExpression;
	}
	
	Set<Role> getToldSubRoles() {
		return toldSubRoles;
	}
	
	Set<Role> getSubRoles() {
		if (subRoles == null)
			computeSubRoles();
		return subRoles;
	}
	
	void computeSubRoles() {
		subRoles = new ArraySet<Role> ();
		Deque<Role> queue = new ArrayDeque<Role> ();
		subRoles.add(this);
		queue.addLast(this);
		while (!queue.isEmpty()) {
			Role role = queue.removeLast();
			for (Role r : role.toldSubRoles)
				if (subRoles.add(r))
					queue.addLast(r);
		}
	}
}