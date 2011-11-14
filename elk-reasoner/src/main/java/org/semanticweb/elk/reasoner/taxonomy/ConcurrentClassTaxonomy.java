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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.util.collections.ArrayHashSet;
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
	/* thread safe list of all nodes */
	protected final Set<ClassNode> allNodes;
	/* counts the number of nodes which have non-bottom sub-classes */
	protected final AtomicInteger countNodesWithSubClasses;
	/* thread safe list of unsatisfiable classes */
	protected final Set<ElkClass> unsatisfiableClasses;

	ConcurrentClassTaxonomy() {
		this.nodeLookup = new ConcurrentHashMap<String, NonBottomNode>();
		this.allNodes = Collections
				.synchronizedSet(new ArrayHashSet<ClassNode>());
		// this.allNodes.add(this);
		this.countNodesWithSubClasses = new AtomicInteger(0);
		this.unsatisfiableClasses = Collections
				.synchronizedSet(new ArrayHashSet<ElkClass>());
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

	/**
	 * Inserts a given node into the taxonomy if there is no node associated
	 * with the same set of members.
	 * 
	 * @param node
	 *            the node to be inserted
	 * @return the created node or the previous node associated with the same
	 *         set of members as the given node
	 */
	public NonBottomNode putIfAbsent(NonBottomNode node) {
		ElkClass canonical = node.getCanonicalMember();
		NonBottomNode previous = nodeLookup
				.putIfAbsent(getKey(canonical), node);
		if (previous == null) {
			if (LOGGER_.isTraceEnabled())
				LOGGER_.trace(getKey(canonical) + ": new node assigned");
			/* a new node for the set of members */
			allNodes.add(node);
			/* insert the node for other member elements */
			for (ElkClass member : node.getMembers()) {
				if (member != canonical)
					nodeLookup.put(getKey(member), node);
			}
			return null;
		} else
			return previous;
	}

	/* {@link ClassNode} method implementing the bottom class */

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
			 * nodes that have no non-bottom sub-classes and the bottom node
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