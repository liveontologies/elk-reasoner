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

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.owl.util.Comparators;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.collections.Operations.Condition;

/**
 * Class taxonomy that is suitable for concurrent processing. It also represents
 * the bottom class node of this taxonomy, i.e., the class node containing all
 * unsatisfiable classes of the taxonomy as the members (including
 * <tt>owl:NoThing</tt>).
 * 
 * @author Yevgeny Kazakov
 * @author Frantisek Simancik
 * 
 */
class ConcurrentClassTaxonomy implements ClassTaxonomy, ClassNode {

	// logger for events
	private static final Logger LOGGER_ = Logger
			.getLogger(ConcurrentClassTaxonomy.class);

	/** thread safe map from class IRIs to class nodes */
	private final ConcurrentMap<String, SatisfiableClassNode> nodeLookup;
	/** thread safe set of all nodes */
	private final Set<ClassNode> allNodes;
	/** counts the number of nodes which have non-bottom sub-classes */
	private final AtomicInteger countNodesWithSubClasses;
	/** thread safe set of unsatisfiable classes */
	private final Set<ElkClass> unsatisfiableClasses;
	/**
	 * the reference to the top node of this taxonomy
	 */
	private final TopClassNode topClassNode;

	// TODO: how to represent an inconsistent ontology?

	ConcurrentClassTaxonomy() {
		this.nodeLookup = new ConcurrentHashMap<String, SatisfiableClassNode>();
		this.allNodes = Collections
				.newSetFromMap(new ConcurrentHashMap<ClassNode, Boolean>());
		allNodes.add(this);
		this.countNodesWithSubClasses = new AtomicInteger(0);
		this.unsatisfiableClasses = Collections
				.synchronizedSet(new TreeSet<ElkClass>(
						Comparators.ELK_CLASS_COMPARATOR));
		this.unsatisfiableClasses.add(PredefinedElkClass.OWL_NOTHING);
		this.topClassNode = new TopClassNode(this);
		nodeLookup.put(getKey(topClassNode.getCanonicalMember()), topClassNode);
		allNodes.add(topClassNode);
	}

	public void clear() {
		this.nodeLookup.clear();
		this.allNodes.clear();
		allNodes.add(this);
		this.countNodesWithSubClasses.set(0);
		this.unsatisfiableClasses.clear();
		this.unsatisfiableClasses.add(PredefinedElkClass.OWL_NOTHING);
		this.topClassNode.clearMembers();
		this.topClassNode.clearSatisfiableSubNodes();
		nodeLookup.put(getKey(topClassNode.getCanonicalMember()), topClassNode);
		allNodes.add(topClassNode);
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
	protected SatisfiableClassNode getSatisfiableClassNode(ElkClass elkClass) {
		return nodeLookup.get(getKey(elkClass));
	}

	protected void removeSatisfiableClassNode(ElkClass elkClass) {
		nodeLookup.remove(getKey(elkClass));
	}

	protected boolean removeNode(SatisfiableClassNode node) {
		if (!node.equals(topClassNode))
			return allNodes.remove(node);
		return false;
	}

	final ElkObjectFactory objectFactory = new ElkObjectFactoryImpl();

	/**
	 * Obtain a ClassNode object for a given {@link ElkClass}, <tt>null</tt> if
	 * none assigned
	 * 
	 * @param elkClass
	 * @return ClassNode object for elkClass, possibly still incomplete
	 */
	public ClassNode getNode(ElkClass elkClass) {
		ClassNode result = getSatisfiableClassNode(elkClass);
		if (result != null)
			return result;
		if (unsatisfiableClasses.contains(elkClass))
			return this;
		// LOGGER_.error("No taxonomy node for class "
		// + elkClass.getIri().asString());
		return null;
	}

	public TopClassNode getTopNode() {
		return topClassNode;
	}

	public ClassNode getBottomNode() {
		return this;
	}

	SatisfiableClassNode getCreate(Collection<ElkClass> members) {
		SatisfiableClassNode result = new SatisfiableClassNode(this, members);
		ElkClass canonical = result.getCanonicalMember();

		// check if it is a top node
		if (canonical.equals(PredefinedElkClass.OWL_THING)) {
			if (members.size() > topClassNode.getMembers().size())
				topClassNode.setMembers(members);
			result = topClassNode;
		} else {
			// we assign first for the node to the canonical member to avoid
			// concurrency problems
			SatisfiableClassNode previous = nodeLookup.putIfAbsent(
					getKey(canonical), result);
			if (previous != null)
				return previous;
			allNodes.add(result);
			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace(canonical + ": node created");
			}
		}
		for (ElkClass member : members) {
			if (member != canonical)
				nodeLookup.put(getKey(member), result);
		}
		return result;
	}

	/**
	 * @return the set of unsatisfiable classes of this taxonomy
	 */
	Set<ElkClass> getUnsatisfiableClasses() {
		return this.unsatisfiableClasses;
	}

	void incrementCountNodesWithSubClasses() {
		countNodesWithSubClasses.incrementAndGet();
	}

	void decrementCountNodesWithSubClasses() {
		countNodesWithSubClasses.decrementAndGet();
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
					return (node.getDirectSubNodes()
							.contains(ConcurrentClassTaxonomy.this));
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

	public boolean isModified() {
		// always returns true since there is no way to know if all nodes in the
		// taxonomy are constructed
		return true;
	}

}