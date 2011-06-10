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
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import org.semanticweb.elk.reasoner.indexing.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.saturation.SaturatedClassExpression;
import org.semanticweb.elk.syntax.ElkClass;
import org.semanticweb.elk.util.ArraySet;
import org.semanticweb.elk.util.HashGenerator;
import org.semanticweb.elk.util.Pair;

/**
 * Class taxonomy that is suitable for concurrent processing.
 * 
 * @author Yevgeny Kazakov
 */
class ConcurrentClassTaxonomy implements ClassTaxonomy,
		ClassTaxonomyComputation {

	/**
	 * The Index that associates ElkClasses to IndexedClassExpression of a
	 * saturation.
	 */
	private OntologyIndex ontologyIndex;

	/**
	 * Lookup table for ClassNode objects.
	 */
	protected final ConcurrentMap<ElkClass, ClassNode> nodeLookup;

	/**
	 * Queue for assigning parents to ClassNode objects. The collection of
	 * contexts consists of the direct parent contexts of the ClassNode.
	 */
	protected final Queue<Pair<ClassNode, Collection<SaturatedClassExpression>>> assignParentsQueue;

	/**
	 * Queue for active ClassNode objects for which new children still have to
	 * be processed.
	 */
	protected final Queue<ClassNode> activeNodes;

	/**
	 * Constructor. Note that it is not checked that the given Saturation object
	 * is actually saturated.
	 * 
	 * @param ontologyIndex
	 *            that is used to associate ElkClasses to
	 *            IndexedClassExpressions
	 * @param saturation
	 *            based on which the hierarchy is computed
	 */
	ConcurrentClassTaxonomy(OntologyIndex ontologyIndex) {
		this.ontologyIndex = ontologyIndex;
		this.nodeLookup = new ConcurrentHashMap<ElkClass, ClassNode>();
		this.assignParentsQueue = new ConcurrentLinkedQueue<Pair<ClassNode, Collection<SaturatedClassExpression>>>();
		this.activeNodes = new ConcurrentLinkedQueue<ClassNode>();
	}

	public void addTarget(ElkClass target) {
		getNode(target);
	}

	public Set<ClassNode> getNodes() {
		return Collections.unmodifiableSet(new HashSet<ClassNode>(nodeLookup
				.values()));
	}

	/**
	 * Obtain a ClassNode object for a given ElkClass. If no object exists yet
	 * for the given class, then a new one is created based on the information
	 * that the Saturation holds for the indexed class expression that
	 * represents this ElkClass. The node that is returned may not contain all
	 * required information. Only after
	 * {@link org.semanticweb.elk.reasoner.classification.ConcurrentClassTaxonomy#compute
	 * compute()} has been called is it ensured that this method returns
	 * complete ClassNode objects for previously added ElkClasses.
	 * 
	 * @param elkClass
	 * @return ClassNode object for elkClass, possibly still incomplete
	 */
	public ClassNode getNode(ElkClass elkClass) {
		ClassNode node = nodeLookup.get(elkClass);
		if (node != null) {
			return node;
		}

		// Equivalent ElkClass with the smallest hash (may change below)
		ElkClass canonical = elkClass;
		IndexedClassExpression root = ontologyIndex
				.getIndexedClassExpression(elkClass);
		SaturatedClassExpression rootContext = root.getSaturated();

		// Classes equivalent to elkClass
		ArraySet<ElkClass> equivalent = new ArraySet<ElkClass>();
		// Contexts of direct superclasses of elkClass
		Collection<SaturatedClassExpression> directContexts = new LinkedList<SaturatedClassExpression>();

		for (IndexedClassExpression derived : rootContext
				.getSuperClassExpressions()) {
			if (derived.getClassExpression() instanceof ElkClass) {
				ElkClass derivedClass = (ElkClass) derived.getClassExpression();
				SaturatedClassExpression derivedContext = derived
						.getSaturated();

				Set<IndexedClassExpression> derivedDerived = derivedContext
						.getSuperClassExpressions();
				if (derivedDerived.contains(root)) {
					equivalent.add(derivedClass);
					// Uses that ElkClass is uniquely identified by its iri
					if (derivedClass.getIri().compareTo(canonical.getIri()) < 0) {
						canonical = derivedClass;
					}
				} else {
					boolean addThis = true;
					Iterator<SaturatedClassExpression> e = directContexts
							.iterator();
					while (e.hasNext()) {
						SaturatedClassExpression previousContext = e.next();
						if (previousContext.getSuperClassExpressions()
								.contains(derived)) {
							addThis = false;
							break;
						}
						if (derivedDerived.contains(previousContext.getRoot())) {
							e.remove();
						}
					}
					if (addThis) {
						directContexts.add(derivedContext);
					}
				}
			}
		}

		node = new ClassNode(equivalent);
		ClassNode previousNode = nodeLookup.putIfAbsent(canonical, node);
		if (previousNode != null) { // can happen in concurrent processing
			return previousNode;
		}

		for (ElkClass member : equivalent) {
			if (member != canonical) {
				nodeLookup.put(member, node);
			}
			assignParentsQueue
					.add(new Pair<ClassNode, Collection<SaturatedClassExpression>>(
							node, directContexts));
		}

		return node;
	}

	protected void assignParents(ClassNode node,
			Collection<SaturatedClassExpression> parents) {
		for (SaturatedClassExpression parentContext : parents) {
			ClassNode parentNode = getNode((ElkClass) parentContext.getRoot()
					.getClassExpression());
			node.addParent(parentNode);
			parentNode.enqueueChild(node);
			activateNode(parentNode);
		}
	}

	private void activateNode(ClassNode node) {
		if (node.tryActivate()) {
			activeNodes.add(node);
		}
	}

	private void deactivateNode(ClassNode node) {
		if (node.tryDeactivate()) {
			if (!node.childQueue.isEmpty()) {
				activateNode(node);
			}
		}
	}

	protected void processChildren(ClassNode node) {
		node.processQueuedChildren();
		deactivateNode(node);
	}

	/**
	 * Complete ClassNode data for all ElkClasses that have been used with this
	 * class so far.
	 */
	public void compute() {
		for (;;) {
			ClassNode activeNode = activeNodes.poll();
			if (activeNode != null) {
				processChildren(activeNode);
				continue;
			}
			Pair<ClassNode, Collection<SaturatedClassExpression>> parentAssignment = assignParentsQueue
					.poll();
			if (parentAssignment != null) {
				assignParents(parentAssignment.getFirst(), parentAssignment
						.getSecond());
				continue;
			}
			break;
		}
	}

	public int structuralHashCode() {
		return HashGenerator.combineMultisetHash(true, nodeLookup.values());
	}
}
