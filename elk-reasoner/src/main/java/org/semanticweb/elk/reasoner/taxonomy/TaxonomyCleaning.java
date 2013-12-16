/**
 * 
 */
package org.semanticweb.elk.reasoner.taxonomy;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.reasoner.ReasonerComputation;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassEntity;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassEntityVisitor;
import org.semanticweb.elk.reasoner.stages.ClassTaxonomyState;
import org.semanticweb.elk.reasoner.stages.InstanceTaxonomyState;
import org.semanticweb.elk.util.concurrent.computation.ComputationExecutor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorFactory;

/**
 * Cleans both class and instance taxonomies concurrently
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class TaxonomyCleaning extends
		ReasonerComputation<IndexedClassEntity, TaxonomyCleaningFactory> {

	public TaxonomyCleaning(Collection<IndexedClassEntity> inputs,
			ClassTaxonomyState classTaxonomyState,
			InstanceTaxonomyState instanceTaxonomyState,
			ComputationExecutor executor, int maxWorkers,
			ProgressMonitor progressMonitor) {
		super(inputs, new TaxonomyCleaningFactory(classTaxonomyState,
				instanceTaxonomyState), executor, maxWorkers, progressMonitor);
	}

}

/**
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
class TaxonomyCleaningFactory
		implements
		InputProcessorFactory<IndexedClassEntity, InputProcessor<IndexedClassEntity>> {

	/*
	 * private static final Logger LOGGER_ = Logger
	 * .getLogger(TaxonomyCleaningFactory.class);
	 */

	private final ClassTaxonomyState classTaxonomyState_;
	private final InstanceTaxonomyState instanceTaxonomyState_;

	TaxonomyCleaningFactory(final ClassTaxonomyState classTaxonomyState,
			final InstanceTaxonomyState instanceTaxonomyState) {
		classTaxonomyState_ = classTaxonomyState;
		instanceTaxonomyState_ = instanceTaxonomyState;
	}

	@Override
	public InputProcessor<IndexedClassEntity> getEngine() {
		return new InputProcessor<IndexedClassEntity>() {
			// a simple dispatching visitor
			private final IndexedClassEntityVisitor<?> submissionVisitor_ = new IndexedClassEntityVisitor<Object>() {

				@Override
				public Object visit(IndexedClass element) {
					submitClass(element);
					return null;
				}

				@Override
				public Object visit(IndexedIndividual element) {
					submitIndividual(element);
					return null;
				}
			};
			// writers have no state so can be safely reused
			private final ClassTaxonomyState.Writer classStateWriter_ = classTaxonomyState_
					.getWriter();

			private final InstanceTaxonomyState.Writer instanceStateWriter_ = instanceTaxonomyState_
					.getWriter();

			/**
			 * Temporary queue of nodes that should be removed from the
			 * taxonomies
			 */
			private final Queue<UpdateableTaxonomyNode<ElkClass>> toRemove_ = new ConcurrentLinkedQueue<UpdateableTaxonomyNode<ElkClass>>();

			@Override
			public void submit(IndexedClassEntity entity) {
				entity.accept(submissionVisitor_);
			}

			private void submitClass(IndexedClass indexedClass) {
				ElkClass elkClass = indexedClass.getElkClass();
				UpdateableTaxonomy<ElkClass> classTaxonomy = classTaxonomyState_
						.getTaxonomy();
				UpdateableInstanceTaxonomy<ElkClass, ElkNamedIndividual> instanceTaxonomy = instanceTaxonomyState_
						.getTaxonomy();

				if (elkClass == PredefinedElkClass.OWL_NOTHING) {
					// classStateWriter_.markClassesForModifiedNode(classTaxonomy.getBottomNode());
					return;
				}

				/*
				 * shouldn't modify the set of members and iterate over them (to
				 * mark as modified) at the same time
				 */
				synchronized (classTaxonomy.getBottomNode()) {
					if (classTaxonomy.getBottomNode().getMembersLookup()
							.remove(elkClass)) {
						classStateWriter_
								.markClassesForModifiedNode(classTaxonomy
										.getBottomNode());
						classStateWriter_.markClassForModifiedNode(elkClass);
						return;
					}
				}

				UpdateableTaxonomyNode<ElkClass> node = classTaxonomy
						.getUpdateableNode(elkClass);

				if (node == null) {
					classStateWriter_.markClassForModifiedNode(elkClass);
					return;
				}

				if (node.trySetModified(true)) {
					toRemove_.add(node);
					classStateWriter_.markClassesForModifiedNode(node);
				}

				// add all its direct satisfiable sub-nodes to the queue
				synchronized (node) {
					for (UpdateableGenericTaxonomyNode<ElkClass> subNode : bottomNode
							.getDirectUpdateableSubNodes()) {
						if (subNode.trySetModified(true)) {
							toRemove_.add(subNode);
							classStateWriter_
									.markClassesForModifiedNode(subNode);
						}
					}
				}

				// delete all direct instance nodes of the type node being
				// removed
				if (instanceTaxonomy != null) {
					UpdateableTypeNode<ElkClass, ElkNamedIndividual> typeNode = instanceTaxonomy
							.getUpdateableTypeNode(elkClass);

					if (typeNode == null) {
						// could be deleted meanwhile in another thread
						return;
					} else {
						List<UpdateableInstanceNode<ElkClass, ElkNamedIndividual>> directInstances = null;

						synchronized (typeNode) {
							directInstances = new LinkedList<UpdateableInstanceNode<ElkClass, ElkNamedIndividual>>(
									typeNode.getDirectInstanceNodes());
						}

						for (UpdateableInstanceNode<ElkClass, ElkNamedIndividual> instanceNode : directInstances) {
							if (instanceNode.trySetModified(true)) {
								instanceStateWriter_
										.markModifiedIndividuals(instanceNode
												.getMembersLookup());
								instanceTaxonomy
										.removeInstanceNode(instanceNode
												.getCanonicalMember());
							}
						}
					}
				}

				/*
				 * Remove node from both taxonomies. Reasonable implementations
				 * have only one copy of each class node but it's not guaranteed
				 * so we still remove it from both taxonomies.
				 */
				classTaxonomy.removeNode(node);

				if (instanceTaxonomy != null) {
					instanceTaxonomy.removeNode(node);
				}
			}

			private void submitIndividual(IndexedIndividual indexedIndividual) {
				if (instanceTaxonomyState_ != null
						&& instanceTaxonomyState_.getTaxonomy() != null) {
					UpdateableInstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy = instanceTaxonomyState_
							.getTaxonomy();
					ElkNamedIndividual individual = indexedIndividual
							.getElkNamedIndividual();
					UpdateableInstanceNode<ElkClass, ElkNamedIndividual> node = taxonomy
							.convertNonBottomInstanceNode(individual);

					if (node == null) {
						instanceStateWriter_
								.markModifiedIndividuals(Collections
										.singleton(individual));
						return;
					}

					if (node.trySetModified(true)) {
						instanceStateWriter_.markModifiedIndividuals(node
								.getMembersLookup());
						taxonomy.removeInstanceNode(individual);
					}
				} else {
					/*
					 * can happen if the ontology has individuals, the instance
					 * taxonomy was never constructed, but then some ABox axiom
					 * was added or deleted. since there's no instance taxonomy,
					 * we can safely ignore this.
					 */
				}
			}

			@Override
			public void process() throws InterruptedException {
				for (;;) {
					UpdateableTaxonomyNode<ElkClass> node = toRemove_.poll();

					if (node == null) {
						return;
					}

					List<UpdateableTaxonomyNode<ElkClass>> superNodes = null;

					// remove all super-class links
					synchronized (node) {
						superNodes = new LinkedList<UpdateableGenericTaxonomyNode<ElkClass>>(
								bottomNode.getDirectUpdateableSuperNodes());

						for (UpdateableGenericTaxonomyNode<ElkClass> superNode : superNodes) {
							bottomNode.removeDirectSuperNode(superNode);
						}
					}

					for (UpdateableTaxonomyNode<ElkClass> superNode : superNodes) {
						synchronized (superNode) {
							superNode.removeDirectSubNode(bottomNode);
						}
					}
				}
			}

			@Override
			public void finish() {
			}

		};
	}

	@Override
	public void finish() {
	}
}
