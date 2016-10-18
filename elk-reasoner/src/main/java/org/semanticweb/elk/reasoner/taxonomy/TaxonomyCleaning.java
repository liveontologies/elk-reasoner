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
package org.semanticweb.elk.reasoner.taxonomy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.reasoner.ReasonerComputationWithInputs;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassEntity;
import org.semanticweb.elk.reasoner.indexing.model.IndexedIndividual;
import org.semanticweb.elk.reasoner.stages.ClassTaxonomyState;
import org.semanticweb.elk.reasoner.stages.InstanceTaxonomyState;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.NonBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.TypeNode;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableInstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableTaxonomy;
import org.semanticweb.elk.util.concurrent.computation.ComputationExecutor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorFactory;
import org.semanticweb.elk.util.concurrent.computation.InterruptMonitor;
import org.semanticweb.elk.util.concurrent.computation.DelegateInterruptMonitor;

/**
 * Cleans both class and instance taxonomies concurrently
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class TaxonomyCleaning
		extends
		ReasonerComputationWithInputs<IndexedClassEntity, TaxonomyCleaningFactory> {

	public TaxonomyCleaning(Collection<IndexedClassEntity> inputs,
			final InterruptMonitor interrupter,
			ClassTaxonomyState classTaxonomyState,
			InstanceTaxonomyState instanceTaxonomyState,
			ComputationExecutor executor, int maxWorkers,
			ProgressMonitor progressMonitor) {
		super(inputs,
				new TaxonomyCleaningFactory(interrupter, classTaxonomyState,
						instanceTaxonomyState),
				executor, maxWorkers, progressMonitor);
	}

}

/**
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author Peter Skocovsky
 */
class TaxonomyCleaningFactory extends DelegateInterruptMonitor
		implements
		InputProcessorFactory<IndexedClassEntity, InputProcessor<IndexedClassEntity>> {

	/*
	 * private static final Logger LOGGER_ = Logger
	 * .getLogger(TaxonomyCleaningFactory.class);
	 */

	private final ClassTaxonomyState classTaxonomyState_;
	private final InstanceTaxonomyState instanceTaxonomyState_;

	TaxonomyCleaningFactory(final InterruptMonitor interrupter,
			final ClassTaxonomyState classTaxonomyState,
			final InstanceTaxonomyState instanceTaxonomyState) {
		super(interrupter);
		classTaxonomyState_ = classTaxonomyState;
		instanceTaxonomyState_ = instanceTaxonomyState;
	}

	@Override
	public InputProcessor<IndexedClassEntity> getEngine() {
		return new InputProcessor<IndexedClassEntity>() {
			// a simple dispatching visitor
			private final IndexedClassEntity.Visitor<?> submissionVisitor_ = new IndexedClassEntity.Visitor<Object>() {

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

			@Override
			public void submit(IndexedClassEntity entity) {
				entity.accept(submissionVisitor_);
			}

			private void submitClass(IndexedClass indexedClass) {
				ElkClass elkClass = indexedClass.getElkEntity();
				UpdateableTaxonomy<ElkClass> classTaxonomy = classTaxonomyState_
						.getTaxonomy();
				UpdateableInstanceTaxonomy<ElkClass, ElkNamedIndividual> instanceTaxonomy = instanceTaxonomyState_
						.getTaxonomy();

				if (elkClass == classTaxonomy.getBottomNode().getCanonicalMember()) {
					// classStateWriter_.markClassesForModifiedNode(classTaxonomy.getBottomNode());
					return;
				}

				/*
				 * shouldn't modify the set of members and iterate over them (to
				 * mark as modified) at the same time
				 */
				synchronized (classTaxonomy.getBottomNode()) {
					if (classTaxonomy.removeFromBottomNode(elkClass)) {
						return;
					}
				}

				final NonBottomTaxonomyNode<ElkClass> node = classTaxonomy
						.getNonBottomNode(elkClass);

				if (node == null) {
					return;
				}

				classTaxonomy.removeDirectSupernodes(node);

				// add all its direct satisfiable sub-nodes to the queue
				final List<NonBottomTaxonomyNode<ElkClass>> subNodes;
				synchronized (node) {
					subNodes = new ArrayList<NonBottomTaxonomyNode<ElkClass>>(
							node.getDirectNonBottomSubNodes());
				}
				for (NonBottomTaxonomyNode<ElkClass> subNode : subNodes) {
					classTaxonomy.removeDirectSupernodes(subNode);
				}

				// delete all direct instance nodes of the type node being
				// removed
				if (instanceTaxonomy != null) {
					TypeNode<ElkClass, ElkNamedIndividual> typeNode = instanceTaxonomy
							.getNode(elkClass);

					if (typeNode == null) {
						// could be deleted meanwhile in another thread
						return;
					}
					// else
					List<InstanceNode<ElkClass, ElkNamedIndividual>> directInstances = null;

					synchronized (typeNode) {
						directInstances = new LinkedList<InstanceNode<ElkClass, ElkNamedIndividual>>(
								typeNode.getDirectInstanceNodes());
					}

					for (InstanceNode<ElkClass, ElkNamedIndividual> instanceNode : directInstances) {
						if (instanceTaxonomy.removeDirectTypes(instanceNode)) {
							instanceTaxonomy.removeInstanceNode(instanceNode
									.getCanonicalMember());
						}
					}
				}

				/*
				 * Remove node from both taxonomies. Reasonable implementations
				 * have only one copy of each class node but it's not guaranteed
				 * so we still remove it from both taxonomies.
				 */
				classTaxonomy.removeNode(node.getCanonicalMember());

				if (instanceTaxonomy != null) {
					instanceTaxonomy.removeNode(node.getCanonicalMember());
				}
			}

			private void submitIndividual(IndexedIndividual indexedIndividual) {
				if (instanceTaxonomyState_ != null
						&& instanceTaxonomyState_.getTaxonomy() != null) {
					UpdateableInstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy = instanceTaxonomyState_
							.getTaxonomy();
					ElkNamedIndividual individual = indexedIndividual
							.getElkEntity();
					InstanceNode<ElkClass, ElkNamedIndividual> node = taxonomy
							.getInstanceNode(individual);

					if (node != null && taxonomy.removeDirectTypes(node)) {
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
			public void process() {
				// Currently does nothing.
				// TODO: The work done in submit should be moved here.
			}

			@Override
			public void finish() {
				// nothing to do
			}

		};
	}

	@Override
	public void finish() {
		// nothing to do
	}

}
