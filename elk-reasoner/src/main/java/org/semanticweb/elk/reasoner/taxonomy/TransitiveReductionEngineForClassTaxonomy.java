/*
 * #%L
 * ELK Reasoner
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
package org.semanticweb.elk.reasoner.taxonomy;

import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionEngine;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionOutputSatisfiable;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionOutputUnsatisfiable;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionOutputVisitor;

public class TransitiveReductionEngineForClassTaxonomy
		extends
		TransitiveReductionEngine<IndexedClass, TransitiveReductionJobClassTaxonomy> {

	protected final OntologyIndex ontologyIndex;

	protected final ConcurrentClassTaxonomy taxonomy;

	protected final TransitiveReductionJobOutputProcessor outputJobProcessor;

	protected final TransitiveReductionOutputProcessor outputProcessor;

	protected final Queue<TransitiveReductionJobClassTaxonomy> jobQueue;

	public TransitiveReductionEngineForClassTaxonomy(
			OntologyIndex ontologyIndex, ConcurrentClassTaxonomy taxonomy) {
		super(ontologyIndex);
		this.ontologyIndex = ontologyIndex;
		this.taxonomy = taxonomy;
		this.outputJobProcessor = new TransitiveReductionJobOutputProcessor();
		this.outputProcessor = new TransitiveReductionOutputProcessor();
		jobQueue = new ConcurrentLinkedQueue<TransitiveReductionJobClassTaxonomy>();
	}

	@Override
	public void process(TransitiveReductionJobClassTaxonomy job)
			throws InterruptedException {
		super.process(job);
		for (;;) {
			TransitiveReductionJobClassTaxonomy subJob = jobQueue.poll();
			if (subJob == null)
				break;
			super.process(subJob);
		}
	}

	@Override
	public void processOutput(TransitiveReductionJobClassTaxonomy job)
			throws InterruptedException {
		super.processOutput(job);
		job.accept(outputJobProcessor);
	}

	class TransitiveReductionJobOutputProcessor implements
			TransitiveReductionJobVisitor {

		public void visit(TransitiveReductionJobRoot job)
				throws InterruptedException {
			job.getOutput().accept(outputProcessor);
		}

		public void visit(TransitiveReductionJobDirectSuperClass job)
				throws InterruptedException {
			ClassTaxonomyState state = job.getClassTaxonomyState();
			NonBottomNode superClassNode = job.getOutput().accept(
					outputProcessor);
			assignDirectSuperClassNode(state.getRootNode(), superClassNode);
			processState(state);
		}

		public void visit(TransitiveReductionJobTopSuperClass job)
				throws InterruptedException {
			NonBottomNode topNode = job.getOutput().accept(outputProcessor);
			assignDirectSuperClassNode(job.getRootNode(), topNode);
		}
	}

	class TransitiveReductionOutputProcessor implements
			TransitiveReductionOutputVisitor<IndexedClass, NonBottomNode> {
		public NonBottomNode visit(
				TransitiveReductionOutputSatisfiable<IndexedClass> output) {
			return getCreateClassNode(output);
		}

		public NonBottomNode visit(
				TransitiveReductionOutputUnsatisfiable<IndexedClass> output) {
			taxonomy.unsatisfiableClasses.add(output.getRoot().getElkClass());
			return null;
		}
	}

	NonBottomNode getCreateClassNode(
			TransitiveReductionOutputSatisfiable<IndexedClass> transitiveReductionOutputSatisfiable) {
		Set<ElkClass> equivalentClasses = transitiveReductionOutputSatisfiable
				.getEquivalent();
		NonBottomNode node = new NonBottomNode(taxonomy, equivalentClasses);
		NonBottomNode previous = taxonomy.putIfAbsent(node);
		if (previous == null) {
			/* processing the new node */
			Iterator<IndexedClass> directSuperClassesIterator = transitiveReductionOutputSatisfiable
					.getDirectSuperClasses().iterator();
			ClassTaxonomyState state = new ClassTaxonomyState(node,
					directSuperClassesIterator);
			processState(state);
			return node;
		} else {
			return previous;
		}
	}

	void processState(ClassTaxonomyState state) {
		NonBottomNode rootNode = state.getRootNode();
		Iterator<IndexedClass> iteratorDirectSuperClasses = state
				.getIteratorDirectSuperClasses();
		while (iteratorDirectSuperClasses.hasNext()) {
			IndexedClass superClass = iteratorDirectSuperClasses.next();
			NonBottomNode superClassNode = taxonomy.getNonBottomNode(superClass
					.getElkClass());
			if (superClassNode == null) {
				jobQueue.add(new TransitiveReductionJobDirectSuperClass(
						superClass, state));
				return;
			}
			assignDirectSuperClassNode(rootNode, superClassNode);
		}
		if (rootNode.getDirectSuperNodes().isEmpty()) {
			NonBottomNode topNode = taxonomy
					.getNonBottomNode(PredefinedElkClass.OWL_THING);
			if (topNode == null) {
				/* in particular, rootNode != topNode */
				jobQueue.add(new TransitiveReductionJobTopSuperClass(
						this.ontologyIndex, rootNode));
				return;
			}
			if (rootNode != topNode) {
				assignDirectSuperClassNode(rootNode, topNode);
			}
		}
	}

	void assignDirectSuperClassNode(NonBottomNode rootNode,
			NonBottomNode superClassNode) {
		rootNode.addDirectSuperNode(superClassNode);
		/* be careful: sub nodes can be added from different threads */
		synchronized (superClassNode) {
			superClassNode.addDirectSubNode(rootNode);
		}
	}
}
