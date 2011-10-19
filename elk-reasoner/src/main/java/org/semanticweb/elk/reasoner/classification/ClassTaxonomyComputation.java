/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.classification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.owl.predefined.PredefinedElkIri;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.util.collections.Pair;
import org.semanticweb.elk.util.concurrent.computation.AbstractConcurrentComputation;

public class ClassTaxonomyComputation extends
		AbstractConcurrentComputation<IndexedClass> {

	protected final ConcurrentClassTaxonomy classTaxonomy;

	// logger for events
	protected final static Logger LOGGER_ = Logger
			.getLogger(ClassTaxonomyComputation.class);

	protected ClassNode topNode, bottomNode;

	/**
	 * Queue for nodes with assigned parents
	 */
	protected final Queue<Pair<ClassNode, List<IndexedClass>>> nodesWithParents;
	
	/**
	 * Synchronized list that collects all unsatisfiable classes 
	 */
	protected final List<ElkClass> unsatClasses;

	public ClassTaxonomyComputation(ExecutorService executor, int maxWorkers) {
		super(executor, maxWorkers, 2 * maxWorkers, 1024);
		this.classTaxonomy = new ConcurrentClassTaxonomy();
		this.nodesWithParents = new ConcurrentLinkedQueue<
			Pair<ClassNode, List<IndexedClass>>>();
		this.unsatClasses = Collections.synchronizedList(new ArrayList<ElkClass>());
	}

	public ClassTaxonomy computeTaxonomy() throws InterruptedException {
		waitCompletion();
		
		bottomNode = new ClassNode(unsatClasses);
		classTaxonomy.allNodes.add(bottomNode);
		for (ElkClass unsatClass : unsatClasses)
			classTaxonomy.nodeLookup.put(unsatClass.getIri(), bottomNode);
		
		topNode = classTaxonomy.getNode(PredefinedElkClass.OWL_THING);
		
		// processing the nodes with assigned parents
		for (;;) {
			Pair<ClassNode, List<IndexedClass>> p = nodesWithParents.poll();
			if (p == null) {
				break;
			}
			
			ClassNode node = p.getFirst();
			classTaxonomy.allNodes.add(node);
			for (IndexedClass ic : p.getSecond()) {
				ClassNode parent = classTaxonomy.getNode(ic.getElkClass());
				node.addDirectSuperNode(parent);
				parent.addDirectSubNode(node);
			}
		}

		// connect to topNode and bottomNode
		for (ClassNode node : classTaxonomy.getNodes()) {
			if (node.getDirectSuperNodes().isEmpty() && node != topNode) {
				node.addDirectSuperNode(topNode);
				topNode.addDirectSubNode(node);
			}
			if (node.getDirectSubNodes().isEmpty() && node != bottomNode) {
				node.addDirectSubNode(bottomNode);
				bottomNode.addDirectSuperNode(node);
			}
		}

		return classTaxonomy;
	}

	protected void process(Iterable<IndexedClass> rootBatch) {
		for (IndexedClass root : rootBatch) {
			process(root);
		}
	}

	protected void process(IndexedClass root) {
		
		if (!root.getSaturated().isSatisfiable()) {
			unsatClasses.add(root.getElkClass());
			return;
		}
		
		List<ElkClass> equivalent = new ArrayList<ElkClass>(1);
		List<IndexedClass> parents = new LinkedList<IndexedClass>();

		equivalent.add(root.getElkClass());
		
		for (IndexedClassExpression superClassExpression : root.getSaturated()
				.getSuperClassExpressions())
			if (superClassExpression != root &&
					superClassExpression instanceof IndexedClass) {
				IndexedClass superClass = (IndexedClass) superClassExpression;

				if (superClass.getSaturated().getSuperClassExpressions()
						.contains(root)) {
					if (PredefinedElkIri.compare(root.getElkClass().getIri(), 
							superClass.getElkClass().getIri()) > 0)
						return;
					equivalent.add(superClass.getElkClass());
				}
				else {
					boolean addThis = true;
					Iterator<IndexedClass> i = parents.iterator();
					while (i.hasNext()) {
						IndexedClass last = i.next();
						if (last.getSaturated().getSuperClassExpressions()
								.contains(superClass)) {
							addThis = false;
							break;
						}
						if (superClass.getSaturated()
								.getSuperClassExpressions().contains(last)) {
							i.remove();
						}
					}
					if (addThis) {
						parents.add(superClass);
					}
				}
			}

		ClassNode node = new ClassNode(equivalent);
		for (ElkClass ec : equivalent)
			classTaxonomy.nodeLookup.put(ec.getIri(), node);
		
		nodesWithParents.add(new Pair<ClassNode, List<IndexedClass>> (node, parents));
	}
}