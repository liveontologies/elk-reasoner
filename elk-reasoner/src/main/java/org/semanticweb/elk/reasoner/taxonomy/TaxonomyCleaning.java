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
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableInstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableInstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableTypeNode;
import org.semanticweb.elk.util.concurrent.computation.ComputationExecutor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorFactory;

/**
 * TODO docs
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
		super(inputs, new TaxonomyCleaningFactory(classTaxonomyState, instanceTaxonomyState),
				executor, maxWorkers, progressMonitor);
	}

}

/**
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
class TaxonomyCleaningFactory implements
		InputProcessorFactory<IndexedClassEntity, InputProcessor<IndexedClassEntity>> {

	private final ClassTaxonomyState classTaxonomyState_;
	private final InstanceTaxonomyState instanceTaxonomyState_;

	TaxonomyCleaningFactory(final ClassTaxonomyState classTaxonomyState, final InstanceTaxonomyState instanceTaxonomyState) {
		classTaxonomyState_ = classTaxonomyState;
		instanceTaxonomyState_ = instanceTaxonomyState;
	}

	@Override
	public InputProcessor<IndexedClassEntity> getEngine() {
		return new InputProcessor<IndexedClassEntity>() {
			
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

			private final ClassTaxonomyState.Writer classStateWriter_ = classTaxonomyState_.getWriter();
			
			private final InstanceTaxonomyState.Writer instanceStateWriter_ = instanceTaxonomyState_.getWriter();
			
			/**
			 * Temporary queue of nodes that should be removed from the taxonomy
			 */
			private final Queue<UpdateableTaxonomyNode<ElkClass>> classNodesToRemove = new ConcurrentLinkedQueue<UpdateableTaxonomyNode<ElkClass>>();

			@Override
			public void submit(IndexedClassEntity entity) {
				entity.accept(submissionVisitor_);
			}
			
			private void submitClass(IndexedClass indexedClass) {
				ElkClass elkClass = indexedClass.getElkClass();
				UpdateableTaxonomy<ElkClass> classTaxonomy = classTaxonomyState_.getTaxonomy();
				UpdateableInstanceTaxonomy<ElkClass, ElkNamedIndividual> instanceTaxonomy = instanceTaxonomyState_.getTaxonomy();
				
				if (elkClass == PredefinedElkClass.OWL_NOTHING){
					classStateWriter_.markClassesForModifiedNode(classTaxonomy.getBottomNode());
					return;
				}
				
				/*
				 * shouldn't modify the set of members and iterate over them (to
				 * mark as modified) at the same time
				 */
				synchronized (classTaxonomy.getBottomNode()) {
					if (classTaxonomy.getBottomNode().getMembers().remove(elkClass)) {
						classStateWriter_.markClassesForModifiedNode(classTaxonomy
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
					classNodesToRemove.add(node);
					classStateWriter_.markClassesForModifiedNode(node);
					
					if (instanceTaxonomy != null) {
						UpdateableTypeNode<ElkClass, ElkNamedIndividual> typeNode = instanceTaxonomy.getUpdateableTypeNode(elkClass);

						synchronized (typeNode) {
							for (UpdateableInstanceNode<ElkClass, ElkNamedIndividual> instanceNode : typeNode.getDirectInstanceNodes()) {
								if (instanceNode.trySetModified(true)) {
									instanceStateWriter_.markModifiedIndividuals(instanceNode);		
									//TODO move this to process
									instanceTaxonomy.removeInstanceNode(instanceNode.getCanonicalMember());
								}
							}
						}
					}
				}
				// add all its direct satisfiable sub-nodes to the queue
				synchronized (node) {
					for (UpdateableTaxonomyNode<ElkClass> subNode : node
							.getDirectUpdateableSubNodes()) {
						if (subNode.trySetModified(true)) {
							classNodesToRemove.add(subNode);
							classStateWriter_.markClassesForModifiedNode(subNode);
						}
					}
				}
				
				// remove node from the taxonomy
				/*classTaxonomy.removeNode(node);
				
				if (instanceTaxonomy != null) {
					instanceTaxonomy.removeNode(node);
				}*/
			}
			
			private void submitIndividual(IndexedIndividual indexedIndividual) {
				if (instanceTaxonomyState_ != null && instanceTaxonomyState_.getTaxonomy() != null) {
					UpdateableInstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy = instanceTaxonomyState_.getTaxonomy();
					ElkNamedIndividual individual = indexedIndividual.getElkNamedIndividual();
					UpdateableInstanceNode<ElkClass, ElkNamedIndividual> node = taxonomy.getInstanceNode(individual);
					
					if (node != null && node.trySetModified(true)) {
						instanceStateWriter_.markModifiedIndividuals(node);
						taxonomy.removeInstanceNode(individual);
					}
				}
				else {
					//TODO Log it, should never happen
				}
			}

			@Override
			public void process() throws InterruptedException {
				UpdateableTaxonomy<ElkClass> classTaxonomy = classTaxonomyState_.getTaxonomy();
				UpdateableInstanceTaxonomy<ElkClass, ElkNamedIndividual> instanceTaxonomy = instanceTaxonomyState_.getTaxonomy();
				
				for (;;) {
					UpdateableTaxonomyNode<ElkClass> node = classNodesToRemove.poll();

					if (node == null) {
						return;
					}

					// remove all super-class links
					synchronized (node) {

						List<UpdateableTaxonomyNode<ElkClass>> superNodes = new LinkedList<UpdateableTaxonomyNode<ElkClass>>(
								node.getDirectUpdateableSuperNodes());

						for (UpdateableTaxonomyNode<ElkClass> superNode : superNodes) {
							superNode.removeDirectSubNode(node);
							node.removeDirectSuperNode(superNode);											
						}
						
						// remove node from the taxonomy
						classTaxonomy.removeNode(node);
						
						if (instanceTaxonomy != null) {
							instanceTaxonomy.removeNode(node);
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
