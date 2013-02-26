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
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.stages.TaxonomyState;
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
		ReasonerComputation<IndexedClass, ClassTaxonomyCleaningFactory> {

	public ClassTaxonomyCleaning(Collection<IndexedClass> inputs,
			UpdateableTaxonomy<ElkClass> taxonomy,
			final TaxonomyState.Writer taxonomyWriter,
			ComputationExecutor executor, int maxWorkers,
			ProgressMonitor progressMonitor) {
		super(inputs,
				new ClassTaxonomyCleaningFactory(taxonomy, taxonomyWriter),
				executor, maxWorkers, progressMonitor);
	}

}

class ClassTaxonomyCleaningFactory implements
		InputProcessorFactory<IndexedClass, InputProcessor<IndexedClass>> {

	private final UpdateableTaxonomy<ElkClass> taxonomy_;
	private final TaxonomyState.Writer taxonomyWriter_;

	ClassTaxonomyCleaningFactory(final UpdateableTaxonomy<ElkClass> taxonomy,
			final TaxonomyState.Writer taxonomyWriter) {
		this.taxonomy_ = taxonomy;
		this.taxonomyWriter_ = taxonomyWriter;
	}

	@Override
	public InputProcessor<IndexedClass> getEngine() {
		return new InputProcessor<IndexedClass>() {

			/**
			 * Temporary queue of nodes that should be removed from the taxonomy
			 */
			private final Queue<UpdateableTaxonomyNode<ElkClass>> toRemove = new ConcurrentLinkedQueue<UpdateableTaxonomyNode<ElkClass>>();

			@Override
			public void submit(IndexedClass indexedClass) {
				ElkClass elkClass = indexedClass.getElkClass();

				if (elkClass == PredefinedElkClass.OWL_NOTHING) {
					taxonomyWriter_.markClassesForModifiedNode(taxonomy_
							.getBottomNode());
					return;
				}

				/*
				 * shouldn't modify the set of members and iterate over them (to
				 * mark as modified) at the same time
				 */
				synchronized (taxonomy_.getBottomNode()) {
					if (taxonomy_.getBottomNode().getMembers().remove(elkClass)) {
						taxonomyWriter_.markClassesForModifiedNode(taxonomy_
								.getBottomNode());
						taxonomyWriter_.markClassForModifiedNode(elkClass);
						return;
					}
				}

				UpdateableTaxonomyNode<ElkClass> node = taxonomy_
						.getUpdateableNode(elkClass);

				if (node == null) {
					taxonomyWriter_.markClassForModifiedNode(elkClass);
					return;
				}

				if (node.trySetModified(true)) {
					toRemove.add(node);
					taxonomyWriter_.markClassesForModifiedNode(node);
				}
				// add all its direct satisfiable sub-nodes to the queue
				synchronized (node) {
					for (UpdateableTaxonomyNode<ElkClass> subNode : node
							.getDirectUpdateableSubNodes()) {
						if (subNode.trySetModified(true)) {
							toRemove.add(subNode);
							taxonomyWriter_.markClassesForModifiedNode(subNode);
						}
					}
				}

				// remove node from the taxonomy
				taxonomy_.removeNode(node);
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
			}

		};
	}

	@Override
	public void finish() {
	}
}
