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

import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionEngine;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionJob;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionOutputEquivalent;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionOutputEquivalentDirect;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionOutputUnsatisfiable;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionOutputVisitor;

public class TransitiveReductionEngineForClassTaxonomy
		extends
		TransitiveReductionEngine<IndexedClass, TransitiveReductionJob<IndexedClass>> {

	protected final OntologyIndex ontologyIndex;

	protected final ConcurrentClassTaxonomy taxonomy;

	protected final TransitiveReductionOutputProcessor outputProcessor;

	public TransitiveReductionEngineForClassTaxonomy(
			OntologyIndex ontologyIndex, ConcurrentClassTaxonomy taxonomy) {
		super(ontologyIndex);
		this.ontologyIndex = ontologyIndex;
		this.taxonomy = taxonomy;
		this.outputProcessor = new TransitiveReductionOutputProcessor();
	}

	@Override
	public void notifyProcessed(TransitiveReductionJob<IndexedClass> job)
			throws InterruptedException {
		super.notifyProcessed(job);
		job.getOutput().accept(outputProcessor);
	}

	class TransitiveReductionOutputProcessor implements
			TransitiveReductionOutputVisitor<IndexedClass> {
		public void visit(
				TransitiveReductionOutputEquivalentDirect<IndexedClass> output) {
			NonBottomNode node = taxonomy.getCreate(output.getEquivalent());
			processDirectSuperClasses(node, output.getDirectSuperClasses());
		}

		public void visit(
				TransitiveReductionOutputUnsatisfiable<IndexedClass> output) {
			taxonomy.unsatisfiableClasses.add(output.getRoot().getElkClass());
		}

		public void visit(
				TransitiveReductionOutputEquivalent<IndexedClass> output) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * We cache top node
	 */
	final AtomicReference<NonBottomNode> topNodeRef = new AtomicReference<NonBottomNode>();

	void processDirectSuperClasses(
			NonBottomNode node,
			Iterable<TransitiveReductionOutputEquivalent<IndexedClass>> directSuperClasses) {
		for (TransitiveReductionOutputEquivalent<IndexedClass> directSuperEquivalent : directSuperClasses) {
			NonBottomNode superNode = taxonomy.getCreate(directSuperEquivalent
					.getEquivalent());
			assignDirectSuperClassNode(node, superNode);
		}
		if (node.getDirectSuperNodes().isEmpty()) {
			NonBottomNode topNode = topNodeRef.get();
			if (topNode == null) {
				// TODO: make sure the membership checking works for any
				// ElkClass implementation!
				if (node.getMembers().contains(PredefinedElkClass.OWL_THING))
					topNode = node;
				else
					topNode = taxonomy
							.getCreate(Collections
									.singleton((ElkClass) PredefinedElkClass.OWL_THING));
				topNodeRef.set(topNode);
			}
			if (node != topNode)
				assignDirectSuperClassNode(node, topNode);
		}
	}

	void assignDirectSuperClassNode(NonBottomNode rootNode,
			NonBottomNode superClassNode) {
		rootNode.addDirectSuperNode(superClassNode);
		/* be careful: sub-nodes can be added from different threads */
		synchronized (superClassNode) {
			superClassNode.addDirectSubNode(rootNode);
		}
	}
}
