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
package org.semanticweb.elk.reasoner.taxonomy;

import java.util.ArrayList;
import java.util.Collection;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;

/**
 * A class to represent the top node of the class taxonomy, i.e., the class node
 * that contains <tt>owl:Thing</tt> as one of the members. There should be
 * exactly one top class node in every taxonomy.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class TopClassNode extends SatisfiableClassNode {

	private TopClassNode(ConcurrentClassTaxonomy taxonomy,
			Collection<ElkClass> members) {
		super(taxonomy, members);
	}

	protected TopClassNode(ConcurrentClassTaxonomy taxonomy) {
		super(taxonomy, new ArrayList<ElkClass>(1));
		this.members.add(PredefinedElkClass.OWL_THING);
	}

	/**
	 * Replacing the set of members of this node with the given set of members
	 * 
	 * @param newMembers
	 *            the new set of members of this node
	 */
	void setMembers(Collection<ElkClass> newMembers) {
		members.clear();
		members.addAll(newMembers);
	}

	/**
	 * Removes the given member unless it is <tt>owl:Thing</tt>
	 * 
	 * @param member
	 *            the member to be removed
	 * @return <tt>true</tt> if the members of this node have changed
	 */
	boolean removeMember(ElkClass member) {
		if (!member.equals(PredefinedElkClass.OWL_THING))
			return members.remove(member);
		else
			return false;
	}

	/**
	 * Deletes all members of this node except for <tt>owl:Thing</tt>
	 */
	void clearMembers() {
		members.clear();
		members.add(PredefinedElkClass.OWL_THING);
	}

	@Override
	void addDirectSuperNode(SatisfiableClassNode superNode) {
		throw new UnsupportedOperationException(
				"Top node cannot have super nodes");
	}

}
