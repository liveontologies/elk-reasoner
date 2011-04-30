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

import java.util.LinkedList;
import java.util.List;

class RoleBox {
	
	List<Role> transitiveRoles =
		new LinkedList<Role> ();
	
	List<RoleChain> roleChains = 
		new LinkedList<RoleChain> ();
	
	void preprocess(boolean detectLeftLinear) {
		
		for (Role r : transitiveRoles) {
			r.transitive = true;
			roleChains.add(new RoleChain(r, r, r));
		}
				
		for (RoleChain r : roleChains) {
			r.getLeftSubRole().getLeftPropertyChains().add(r);
			r.getRightSubRole().getRightPropertyChains().add(r);
			r.getSuperRole().getSuperPropertyChains().add(r);
		}
		
		if (detectLeftLinear)
			for (RoleChain r : roleChains) 
				r.isLeftLinear = checkLeftLinear(r);
		
//		/*
		int leftNo = 0;
		int nonLeftNo = 0;
		
		for (RoleChain r : roleChains) {
			if (r.isLeftLinear)
				leftNo++;
			else 
				nonLeftNo++;
		}
		
		System.out.println("Left linear role chains: " + leftNo);
		System.out.println("Non left linear role chains: " + nonLeftNo);
//		*/
	}
	
	boolean checkLeftLinear(RoleChain chain) {
		/* Generating all triples is far too slow
		 * 
		Set<Triple<Role, Role, Role>> leftLinear = new HashSet<Triple<Role, Role, Role>> ();
		for (Role subRole : chain.getSuperRole().getAllSubRoles())
		for (RoleChain p : subRole.getSuperPropertyChains())
			for (Role s1 : p.getLeftSubRole().getAllSubRoles())
			for (Role s2 : p.getRightSubRole().getAllSubRoles()) {

				for (RoleChain q : s1.getSuperPropertyChains())
					for (Role r1 : q.getLeftSubRole().getAllSubRoles())
					for (Role r2 : q.getRightSubRole().getAllSubRoles()) 
						leftLinear.add(new Triple<Role, Role, Role> (r1, r2, s2));
						
				if (!p.isLeftLinear)
				for (RoleChain q : s2.getSuperPropertyChains())
					for (Role r1 : q.getLeftSubRole().getAllSubRoles())
					for (Role r2 : q.getRightSubRole().getAllSubRoles()) 
						leftLinear.add(new Triple<Role, Role, Role> (s1, r1, r2));
			}
		*/
		
		for (Role rightSubRole : chain.getRightSubRole().getAllSubRoles())
			for (RoleChain rightSubChain : rightSubRole.getSuperPropertyChains()) {

				Role r1 = chain.getLeftSubRole();
				Role r2 = rightSubChain.getLeftSubRole();
				Role r3 = rightSubChain.getRightSubRole();
				
				boolean found = false;
				search:
				for (Role subRole : chain.getSuperRole().getAllSubRoles())
					for (RoleChain p : subRole.getSuperPropertyChains()) { 
						if (p.getRightSubRole().getAllSubRoles().contains(r3))
							for (Role s : p.getLeftSubRole().getAllSubRoles())
								for (RoleChain q : s.getSuperPropertyChains())
									if (q.getLeftSubRole().getAllSubRoles().contains(r1) &&
										q.getRightSubRole().getAllSubRoles().contains(r2)) {
										found = true;
										break search;
									}
						/*
						if (!p.isLeftLinear && p.getLeftSubRole().getAllSubRoles().contains(r1))
							for (Role s : p.getRightSubRole().getAllSubRoles())
								for (RoleChain q : s.getSuperPropertyChains())
									if (q.getLeftSubRole().getAllSubRoles().contains(r2) &&
										q.getRightSubRole().getAllSubRoles().contains(r3)) {
										found = true;
										break search;
									}
						*/
					}
				
				if (!found)
					return false;
			}
		return true;
	}
}