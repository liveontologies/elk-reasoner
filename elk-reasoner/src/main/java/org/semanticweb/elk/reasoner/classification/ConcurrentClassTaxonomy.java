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
package org.semanticweb.elk.reasoner.classification;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import org.semanticweb.elk.reasoner.indexing.Index;
import org.semanticweb.elk.reasoner.indexing.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.Context;
import org.semanticweb.elk.reasoner.saturation.Saturation;
import org.semanticweb.elk.syntax.ElkClass;
import org.semanticweb.elk.util.ArraySet;
import org.semanticweb.elk.util.Pair;

/**
 * @author Yevgeny Kazakov
 * 
 */
public class ConcurrentClassTaxonomy implements ClassTaxonomy {

	private Index index;
	private Saturation saturation;
	// lookup table for nodes
	protected final ConcurrentMap<ElkClass, ClassNode> nodeLookup;
	// queue for assigning nodes with parents
	protected final Queue<Pair<ClassNode, Collection<Context>>> assignParentsQueue;
	// queue for active nodes (with new children)
	protected final Queue<ClassNode> activeNodes;

	public ConcurrentClassTaxonomy(Index index, Saturation saturation) {
		this.index = index;
		this.saturation = saturation;
		this.nodeLookup = new ConcurrentHashMap<ElkClass, ClassNode>();
		this.assignParentsQueue = new ConcurrentLinkedQueue<Pair<ClassNode, Collection<Context>>>();
		this.activeNodes = new ConcurrentLinkedQueue<ClassNode>();
	}

	public ClassNode getNode(ElkClass elkClass) {
		ClassNode node = nodeLookup.get(elkClass);
		if (node != null)
			return node;
		// equivalent ElkClass with the smallest hash
		ElkClass canonical = elkClass;
		IndexedClassExpression root = index.getIndexed(elkClass);
		Context rootContext = saturation.getContext(root);
		ArraySet<ElkClass> equivalent = new ArraySet<ElkClass>();
		Collection<Context> directContexts = new LinkedList<Context>();
		for (IndexedClassExpression derived : rootContext.getDerived()) {
			if (derived.classExpression instanceof ElkClass) {
				ElkClass derivedClass = (ElkClass) derived.classExpression;
				Context derivedContext = saturation.getContext(derived);
				Set<IndexedClassExpression> derivedDerived = derivedContext
				.getDerived();
				if (derivedDerived.contains(root)) {
					equivalent.add(derivedClass);
					// uses that hash codes for members of ElkClass are unique!
					if (derivedClass.hashCode() < canonical.hashCode())
						canonical = derivedClass;
				} else {
					boolean addThis = true;
					Iterator<Context> e = directContexts.iterator();
					while (e.hasNext()) {
						Context previousContext = e.next();
						if (previousContext.getDerived().contains(derived)) {
							addThis = false;
							break;
						}
						if (derivedDerived.contains(previousContext.getRoot()))
							e.remove();
					}
					if (addThis)
						directContexts.add(derivedContext);
				}
			}
		}

		node = new ClassNode(equivalent);
		ClassNode previousNode = nodeLookup.putIfAbsent(canonical, node);
		if (previousNode != null)
			return previousNode;
		for (ElkClass member : equivalent)
			if (member != canonical)
				nodeLookup.put(member, node);
		assignParentsQueue.add(new Pair<ClassNode, Collection<Context>>(node,
				directContexts));
		return node;
	}

	protected void assignParents(ClassNode node, Collection<Context> parents) {
		for (Context parentContext : parents) {
			ClassNode parentNode = getNode((ElkClass) parentContext.getRoot().classExpression);
			node.addParent(parentNode);
			parentNode.enqueueChild(node);
			activateNode(parentNode);
		}
	}

	private void activateNode(ClassNode node) {
		if (node.tryActivate())
			activeNodes.add(node);
	}

	private void deactivateNode(ClassNode node) {
		if (node.tryDeactivate())
			if (!node.childQueue.isEmpty())
				activateNode(node);
	}

	protected void processChildren(ClassNode node) {
		for (;;) {
			ClassNode child = node.childQueue.poll();
			if (child == null)
				break;
			node.addChild(child);
		}
		deactivateNode(node);
	}

	public void compute() {
		for (;;) {
			ClassNode activeNode = activeNodes.poll();
			if (activeNode != null) {
				processChildren(activeNode);
				continue;
			}
			Pair<ClassNode, Collection<Context>> parentAssignment = assignParentsQueue
					.poll();
			if (parentAssignment != null) {
				assignParents(parentAssignment.getFirst(), parentAssignment
						.getSecond());
				continue;
			}

			break;
		}
	}
}
