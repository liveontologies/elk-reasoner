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

import java.util.Collections;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedIndividual;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionFactory;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionJob;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionListener;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionOutputEquivalent;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionOutputEquivalentDirect;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionOutputUnsatisfiable;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionOutputVisitor;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.taxonomy.InstanceTaxonomyComputationFactory.Engine;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableInstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableInstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableTypeNode;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorFactory;

/*
 * TODO: current implementation does not support equivalent individuals,
 * i.e. assumes that all individual nodes are singletons
 */

/**
 * The factory for engines that concurrently construct an
 * {@link InstanceTaxonomy}. The jobs are submitted using the method
 * {@link Engine#submit(IndexedIndividual)}, which require the computation of
 * the {@link Node} for the input {@link IndexedIndividual}.
 * 
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch
 */
public class InstanceTaxonomyComputationFactory implements
		InputProcessorFactory<IndexedIndividual, Engine> {

	/**
	 * The class taxonomy object into which we write the result
	 */
	private final UpdateableInstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy_;
	/**
	 * The transitive reduction shared structures used in the taxonomy
	 * construction
	 */
	private final TransitiveReductionFactory<IndexedIndividual, TransitiveReductionJob<IndexedIndividual>> transitiveReductionShared_;
	/**
	 * The objects creating or update the nodes from the result of the
	 * transitive reduction
	 */
	private final TransitiveReductionOutputProcessor outputProcessor_;

	/**
	 * Create a shared engine for the input ontology index and a partially
	 * pre-computed taxonomy object. The taxonomy is used to avoid computations
	 * that have been made before. For this to work, the taxonomy object must
	 * originate from an earlier run of this engine on the same ontology.
	 * 
	 * @param saturationState
	 *            the saturation state of the reasoner
	 * @param maxWorkers
	 *            the maximum number of workers that can use this factory
	 * @param partialTaxonomy
	 *            the (partially pre-computed) class taxonomy object to store
	 *            results in
	 */
	public InstanceTaxonomyComputationFactory(
			SaturationState saturationState,
			int maxWorkers,
			UpdateableInstanceTaxonomy<ElkClass, ElkNamedIndividual> partialTaxonomy) {
		this.taxonomy_ = partialTaxonomy;
		this.transitiveReductionShared_ = new TransitiveReductionFactory<IndexedIndividual, TransitiveReductionJob<IndexedIndividual>>(
				saturationState, maxWorkers,
				new ThisTransitiveReductionListener());
		this.outputProcessor_ = new TransitiveReductionOutputProcessor();
	}

	/**
	 * The listener class used for the transitive reduction engine, which is
	 * used within this class taxonomy computation engine
	 * 
	 * @author "Yevgeny Kazakov"
	 */
	private class ThisTransitiveReductionListener
			implements
			TransitiveReductionListener<TransitiveReductionJob<IndexedIndividual>> {

		@Override
		public void notifyFinished(TransitiveReductionJob<IndexedIndividual> job)
				throws InterruptedException {
			job.getOutput().accept(outputProcessor_);
		}

	}

	/**
	 * The class for processing the finished transitive reduction jobs. It
	 * implements the visitor pattern for
	 * {@link TransitiveReductionOutputVisitor}.
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	private class TransitiveReductionOutputProcessor implements
			TransitiveReductionOutputVisitor<IndexedIndividual> {

		@Override
		public void visit(
				TransitiveReductionOutputEquivalentDirect<IndexedIndividual> output) {

			// only supports singleton individuals
			UpdateableInstanceNode<ElkClass, ElkNamedIndividual> node = taxonomy_
					.getCreateInstanceNode(Collections.singleton(output
							.getRoot().getElkNamedIndividual()));

			for (TransitiveReductionOutputEquivalent<IndexedClass> directSuperEquivalent : output
					.getDirectSubsumers()) {
				UpdateableTypeNode<ElkClass, ElkNamedIndividual> superNode = taxonomy_
						.getCreateTypeNode(directSuperEquivalent
								.getEquivalent());
				assignDirectTypeNode(node, superNode);
			}

			node.trySetModified(false);
		}

		@Override
		public void visit(
				TransitiveReductionOutputUnsatisfiable<IndexedIndividual> output) {
			// the ontology is inconsistent, this should have been checked
			// earlier
			throw new IllegalArgumentException();
		}

		@Override
		public void visit(
				TransitiveReductionOutputEquivalent<IndexedIndividual> output) {
			// this should not happen: all transitive reduction results should
			// be computed with direct super classes
			throw new IllegalArgumentException();
		}

	}

	/**
	 * Connecting the given pair of nodes in instance/type-node relation. The
	 * method should not be called concurrently for the same first argument.
	 * 
	 * @param instanceNode
	 *            the node that should be the sub-node of the second node
	 * 
	 * @param typeNode
	 *            the node that should be the super-node of the first node
	 */
	private static void assignDirectTypeNode(
			UpdateableInstanceNode<ElkClass, ElkNamedIndividual> instanceNode,
			UpdateableTypeNode<ElkClass, ElkNamedIndividual> typeNode) {
		instanceNode.addDirectTypeNode(typeNode);
		/*
		 * since type-nodes can be added from different nodes, this call should
		 * be synchronized
		 */
		synchronized (typeNode) {
			typeNode.addDirectInstanceNode(instanceNode);
		}
	}

	/**
	 * Returns the taxonomy constructed by this engine
	 * 
	 * @return the taxonomy constructed by this engine
	 */
	public UpdateableInstanceTaxonomy<ElkClass, ElkNamedIndividual> getTaxonomy() {
		return this.taxonomy_;
	}

	@Override
	public Engine getEngine() {
		return new Engine();

	}

	@Override
	public void interrupt() {
		transitiveReductionShared_.interrupt();
	}

	@Override
	public void finish() {
		transitiveReductionShared_.finish();
	}

	/**
	 * Print statistics about taxonomy construction
	 */
	public void printStatistics() {
		transitiveReductionShared_.printStatistics();
	}

	public class Engine implements InputProcessor<IndexedIndividual> {

		/**
		 * The transitive reduction engine used in the taxonomy construction
		 */
		protected final TransitiveReductionFactory<IndexedIndividual, TransitiveReductionJob<IndexedIndividual>>.Engine transitiveReductionEngine = transitiveReductionShared_
				.getEngine();

		// don't allow creating of engines directly; only through the factory
		private Engine() {
		}

		@Override
		public final void submit(IndexedIndividual job) {
			transitiveReductionEngine
					.submit(new TransitiveReductionJob<IndexedIndividual>(job));
		}

		@Override
		public final void process() throws InterruptedException {
			transitiveReductionEngine.process();
		}

		@Override
		public void finish() {
			transitiveReductionEngine.finish();
		}

	}

}
