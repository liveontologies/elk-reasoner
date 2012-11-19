/**
 * 
 */
package org.semanticweb.elk.reasoner.taxonomy;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.semanticweb.elk.owl.interfaces.ElkClass;
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
 * pavel.klinov@uni-ulm.de
 */
public class ClassTaxonomyCleaning extends
		ReasonerComputation<ElkClass, ClassTaxonomyCleaningFactory> {

	public ClassTaxonomyCleaning(
			Collection<? extends ElkClass> inputs,
			UpdateableTaxonomy<ElkClass> classTaxonomy,
			ComputationExecutor executor,
			int maxWorkers,
			ProgressMonitor progressMonitor) {
		super(inputs, new ClassTaxonomyCleaningFactory(classTaxonomy), executor, maxWorkers, progressMonitor);
	}

}

class ClassTaxonomyCleaningFactory
		implements
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
				if (classTaxonomy_.getBottomNode().getMembers().remove(elkClass)) {
					return;
				}
				
				UpdateableTaxonomyNode<ElkClass> node = classTaxonomy_.getUpdateableNode(elkClass);
				
				if (node == null) {
					return;
				}
				if (node.trySetModified(true)) {
					toRemove.add(node);
				}
				// add all its direct satisfiable sub-nodes to the queue
				synchronized (node) {
					for (UpdateableTaxonomyNode<ElkClass> subNode : node.getDirectUpdateableSubNodes()) {
						if (!subNode.trySetModified(true))
							continue;
						toRemove.add(subNode);
					}
				}
				
				classTaxonomy_.removeNode(node);
			}

			@Override
			public void process() throws InterruptedException {
				
				for (;;) {
					UpdateableTaxonomyNode<ElkClass> node = toRemove.poll();
					
					if (node == null)
						return;
					
					List<UpdateableTaxonomyNode<ElkClass>> superNodes = new LinkedList<UpdateableTaxonomyNode<ElkClass>>();
					// remove all super-class links
					synchronized (node) {						
						for (Iterator<? extends UpdateableTaxonomyNode<ElkClass>> iter = node.getDirectSuperNodes().iterator(); iter.hasNext();) {
							superNodes.add(iter.next());
							iter.remove();
						}
					}
					
					for (UpdateableTaxonomyNode<ElkClass> superNode : superNodes) {
						superNode.removeDirectSubNode(node);
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
