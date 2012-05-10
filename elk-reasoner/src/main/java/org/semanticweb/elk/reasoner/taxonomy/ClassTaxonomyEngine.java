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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionEngine;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionJob;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionListener;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionOutputEquivalent;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionOutputEquivalentDirect;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionOutputUnsatisfiable;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionOutputVisitor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;

/**
 * The engine for constructing of the {@link ClassTaxonomy}. The jobs are
 * submitted using the method {@link #submit(IndexedClass)}, which require the
 * computation of the {@link ClassNode} for the input {@link IndexedClass}. 
 * 
 * @author "Yevgeny Kazakov"
 */
public class ClassTaxonomyEngine implements InputProcessor<IndexedClass> {
	/**
	 * The class taxonomy object into which we write the result
	 */
	protected final ConcurrentClassTaxonomy taxonomy;
	/**
	 * The transitive reduction engine used in the taxonomy construction
	 */
	protected final TransitiveReductionEngine<IndexedClass, TransitiveReductionJob<IndexedClass>> transitiveReductionEngine;
	/**
	 * The object creating or update the nodes from the result of the transitive
	 * reduction
	 */
	protected final TransitiveReductionOutputProcessor outputProcessor = new TransitiveReductionOutputProcessor();
	/**
	 * The reference to cache the value of the top node for frequent use
	 */
	protected final AtomicReference<NonBottomNode> topNodeRef = new AtomicReference<NonBottomNode>();

	/**
	 * Creates a new class taxonomy engine for the input ontology index and a
	 * listener for executing callback functions.
	 * 
	 * @param ontologyIndex
	 *            the ontology index for which the engine is created
	 * @param listener
	 *            the listener object implementing callback functions
	 */
	public ClassTaxonomyEngine(OntologyIndex ontologyIndex) {
		this.taxonomy = new ConcurrentClassTaxonomy();
		this.transitiveReductionEngine = new TransitiveReductionEngine<IndexedClass, TransitiveReductionJob<IndexedClass>>(
				ontologyIndex, new ThisTransitiveReductionListener());
	}


	@Override
	public final void submit(IndexedClass job) throws InterruptedException {
		transitiveReductionEngine
				.submit(new TransitiveReductionJob<IndexedClass>(job));
	}

	@Override
	public final void process() throws InterruptedException {
		transitiveReductionEngine.process();
	}

	@Override
	public boolean canProcess() {
		return transitiveReductionEngine.canProcess();
	}

	/**
	 * Print statistics about class taxonomy construction
	 */
	public void printStatistics() {
		transitiveReductionEngine.printStatistics();
	}

	/**
	 * Returns the class taxonomy constructed by this engine
	 * 
	 * @return the class taxonomy constructed by this engine
	 */
	public ClassTaxonomy getClassTaxonomy() {
		return this.taxonomy;
	}

	/**
	 * The listener class used for the transitive reduction engine, which is
	 * used within this class taxonomy computation engine
	 * 
	 * @author "Yevgeny Kazakov"
	 */
	class ThisTransitiveReductionListener
			implements
			TransitiveReductionListener<TransitiveReductionJob<IndexedClass>, TransitiveReductionEngine<IndexedClass, TransitiveReductionJob<IndexedClass>>> {

		@Override
		public void notifyCanProcess() {
		}

		@Override
		public void notifyFinished(TransitiveReductionJob<IndexedClass> job)
				throws InterruptedException {
			job.getOutput().accept(outputProcessor);
		}

	}

	/**
	 * The class for processing the finished transitive reduction jobs. It
	 * implements the visitor pattern for
	 * {@link TransitiveReductionOutputVisitor<IndexedClass>}.
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	class TransitiveReductionOutputProcessor implements
			TransitiveReductionOutputVisitor<IndexedClass> {
		@Override
		public void visit(
				TransitiveReductionOutputEquivalentDirect<IndexedClass> output) {
			NonBottomNode node = taxonomy.getCreate(output.getEquivalent());
			for (TransitiveReductionOutputEquivalent<IndexedClass> directSuperEquivalent : output
					.getDirectSuperClasses()) {
				NonBottomNode superNode = taxonomy
						.getCreate(directSuperEquivalent.getEquivalent());
				assignDirectSuperClassNode(node, superNode);
			}
			// if there are no direct super nodes, then the top node is the only
			// direct super node
			if (node.getDirectSuperNodes().isEmpty()) {
				NonBottomNode topNode = topNodeRef.get();
				if (topNode == null) {
					if (node.getMembers()
							.contains(PredefinedElkClass.OWL_THING))
						topNode = node;
					else {
						List<ElkClass> topMembers = new ArrayList<ElkClass>(1);
						topMembers.add(PredefinedElkClass.OWL_THING);
						topNode = taxonomy.getCreate(topMembers);
					}
					topNodeRef.set(topNode);
				}
				if (node != topNode)
					assignDirectSuperClassNode(node, topNode);
			}
		}

		@Override
		public void visit(
				TransitiveReductionOutputUnsatisfiable<IndexedClass> output) {
			taxonomy.unsatisfiableClasses.add(output.getRoot().getElkClass());
		}

		@Override
		public void visit(
				TransitiveReductionOutputEquivalent<IndexedClass> output) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Connecting the given pair of nodes in sub/super-node relation. The method
	 * should not be called concurrently for the same first argument.
	 * 
	 * @param subNode
	 *            the node that should be the sub-node of the second node
	 * 
	 * @param superNode
	 *            the node that should be the super-node of the first node
	 */
	static void assignDirectSuperClassNode(NonBottomNode subNode,
			NonBottomNode superNode) {
		subNode.addDirectSuperNode(superNode);
		/*
		 * since super-nodes can be added from different nodes, this call should
		 * be synchronized
		 */
		synchronized (superNode) {
			superNode.addDirectSubNode(subNode);
		}
	}

}
