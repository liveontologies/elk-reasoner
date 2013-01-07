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
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.reasoner.ReasonerComputation;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableTaxonomyNode;
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
public class ClassTaxonomyCleaning extends
		ReasonerComputation<ElkClass, ClassTaxonomyCleaningFactory> {

	public ClassTaxonomyCleaning(Collection<? extends ElkClass> inputs,
			UpdateableTaxonomy<ElkClass> classTaxonomy,
			ComputationExecutor executor, int maxWorkers,
			ProgressMonitor progressMonitor) {
		super(inputs, new ClassTaxonomyCleaningFactory(classTaxonomy),
				executor, maxWorkers, progressMonitor);
	}

}

class ClassTaxonomyCleaningFactory implements
		InputProcessorFactory<ElkClass, InputProcessor<ElkClass>> {

	private final UpdateableTaxonomy<ElkClass> classTaxonomy_;

	ClassTaxonomyCleaningFactory(UpdateableTaxonomy<ElkClass> taxonomy) {
		classTaxonomy_ = taxonomy;
	}

	@Override
	public InputProcessor<ElkClass> getEngine() {
		return new InputProcessor<ElkClass>() {

			/**
			 * Temporary queue of nodes that should be removed from the taxonomy
			 */
			private final Queue<UpdateableTaxonomyNode<ElkClass>> toRemove = new ConcurrentLinkedQueue<UpdateableTaxonomyNode<ElkClass>>();

			@Override
			public void submit(ElkClass elkClass) {
				///TODO Decide if this is a normal situation
				if (elkClass == PredefinedElkClass.OWL_NOTHING){
					System.err.println("Removing owl:Nothing!!!");
					return;
				}
				
				if (classTaxonomy_.getBottomNode().getMembers()
						.remove(elkClass)) {
					return;
				}

				UpdateableTaxonomyNode<ElkClass> node = classTaxonomy_
						.getUpdateableNode(elkClass);

				if (node == null) {
					return;
				}
				
				if (node.trySetModified(true)) {
					toRemove.add(node);
				}
				// add all its direct satisfiable sub-nodes to the queue
				synchronized (node) {
					for (UpdateableTaxonomyNode<ElkClass> subNode : node
							.getDirectUpdateableSubNodes()) {
						if (subNode.trySetModified(true)) {
							toRemove.add(subNode);
						}
					}
				}
				
				// remove node from the taxonomy
				classTaxonomy_.removeNode(node);
			}

			@Override
			public void process() throws InterruptedException {

				for (;;) {
					UpdateableTaxonomyNode<ElkClass> node = toRemove.poll();

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
												
					}

				}
			}

			@Override
			public void finish() {
				// TODO Auto-generated method stub
			}

		};
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}
}
