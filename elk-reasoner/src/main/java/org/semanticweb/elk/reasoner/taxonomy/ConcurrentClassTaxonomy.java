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
 * @author Yevgeny Kazakov, May 15, 2011
 */
package org.semanticweb.elk.reasoner.taxonomy;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.owl.util.Comparators;
import org.semanticweb.elk.util.collections.ArraySet;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.collections.Operations.Condition;

/**
 * Class taxonomy that is suitable for concurrent processing. It also implements
 * method for the bottom node.
 * 
 * @author Yevgeny Kazakov
 * @author Frantisek Simancik
 * 
 */
class ConcurrentClassTaxonomy implements ClassTaxonomy, ClassNode {

	// logger for events
	private static final Logger LOGGER_ = Logger
			.getLogger(ConcurrentClassTaxonomy.class);

	/* thread safe map from class IRIs to class nodes */
	protected final ConcurrentMap<String, NonBottomNode> nodeLookup;
	/* thread safe set of all nodes */
	protected final Set<ClassNode> allNodes;
	/* boolean to guard access to the set of all nodes */
	protected final AtomicBoolean processingNewNodes;
	/* counts the number of nodes which have non-bottom sub-classes */
	protected final AtomicInteger countNodesWithSubClasses;
	/* thread safe set of unsatisfiable classes */
	protected final Set<ElkClass> unsatisfiableClasses;

	ConcurrentClassTaxonomy() {
		this.nodeLookup = new ConcurrentHashMap<String, NonBottomNode>();
		this.allNodes = Collections
				.newSetFromMap(new ConcurrentHashMap<ClassNode, Boolean>());
		this.processingNewNodes = new AtomicBoolean(false);
		this.countNodesWithSubClasses = new AtomicInteger(0);
		this.unsatisfiableClasses = Collections
				.newSetFromMap(new ConcurrentHashMap<ElkClass, Boolean>());
		this.unsatisfiableClasses.add(PredefinedElkClass.OWL_NOTHING);
	}

	public Set<ClassNode> getNodes() {
		return Collections.unmodifiableSet(allNodes);
	}

	/**
	 * Returns a unique string representation of the given ELK class.
	 * 
	 * @return a unique string representation of the given ELK class
	 */
	static String getKey(ElkClass elkClass) {
		return elkClass.getIri().asString();
	}

	/**
	 * Get non-bottom node assigned to the given {@link ElkClass}, or
	 * <tt>null</tt> if none is assigned.
	 * 
	 * @param elkClass
	 *            the class for which to find the node non-bottom
	 * @return the non-bottom node for the given {@link ElkClass}
	 */
	NonBottomNode getNonBottomNode(ElkClass elkClass) {
		return nodeLookup.get(getKey(elkClass));
	}

	/**
	 * Obtain a ClassNode object for a given {@link ElkClass}, <tt>null</tt> if
	 * none assigned
	 * 
	 * @param elkClass
	 * @return ClassNode object for elkClass, possibly still incomplete
	 */
	public ClassNode getNode(ElkClass elkClass) {
		ClassNode result = getNonBottomNode(elkClass);
		if (result != null)
			return result;
		if (unsatisfiableClasses.contains(elkClass))
			return this;
		LOGGER_.error("No taxonomy node for class "
				+ elkClass.getIri().asString());
		return null;
	}

	public NonBottomNode getCreate(List<ElkClass> elkClasses) {

		ArraySet<ElkClass> members = new ArraySet<ElkClass>(elkClasses.size());
		for (ElkClass elkClass : elkClasses) {
			members.add(elkClass);
		}
		Collections.sort(members, Comparators.ELK_CLASS_COMPARATOR);
		NonBottomNode node = new NonBottomNode(this, members);
		ElkClass canonical = node.getCanonicalMember();
		NonBottomNode previous = nodeLookup
				.putIfAbsent(getKey(canonical), node);
		if (previous != null) {
			node = previous;
			if (!node.membersSet()) {
				node.setMembers(members);
			} else
				return previous;
		} else {
			allNodes.add(node);
		}
		for (ElkClass member : members) {
			if (member != canonical)
				nodeLookup.put(getKey(member), node);
		}
		return node;
	}

	protected NonBottomNode getCreate(ElkClass elkClass) {
		NonBottomNode node = new NonBottomNode(this);
		NonBottomNode previous = nodeLookup.putIfAbsent(getKey(elkClass), node);
		if (previous != null) {
			node = previous;
		} else {
			allNodes.add(node);
		}
		return node;
	}

	/* functions required by the {@link ClassNode} representing the bottom node */

	public Set<ElkClass> getMembers() {
		return unsatisfiableClasses;
	}

	public ElkClass getCanonicalMember() {
		return PredefinedElkClass.OWL_NOTHING;
	}

	public Set<ClassNode> getDirectSuperNodes() {
		return Operations.filter(allNodes, new Condition() {
			public boolean holds(Object element) {
				if (element instanceof ClassNode) {
					ClassNode node = (ClassNode) element;
					return (node.getDirectSubNodes().contains(this));
				}
				return false;
			}
			/*
			 * the direct super nodes of the bottom node are all nodes except
			 * the nodes that have no non-bottom sub-classes and the bottom node
			 */
		}, allNodes.size() - countNodesWithSubClasses.get() - 1);
	}

	public Set<ClassNode> getAllSuperNodes() {
		/* all nodes except this one */
		return Operations.filter(allNodes, new Condition() {
			public boolean holds(Object element) {
				return element != this;
			}
		}, allNodes.size() - 1);
	}

	public Set<ClassNode> getDirectSubNodes() {
		return Collections.emptySet();
	}

	public Set<ClassNode> getAllSubNodes() {
		return Collections.emptySet();
	}

	public ClassTaxonomy getTaxonomy() {
		return this;
	}

}