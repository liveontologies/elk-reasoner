/**
 * 
 */
package org.semanticweb.elk.reasoner.taxonomy;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.reasoner.ReasonerComputation;
import org.semanticweb.elk.util.concurrent.computation.ComputationExecutor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorFactory;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ClassTaxonomyCleaning extends
		ReasonerComputation<ElkClass, ClassTaxonomyCleaningFactory> {

	public ClassTaxonomyCleaning(
			Collection<? extends ElkClass> inputs,
			ConcurrentTaxonomy classTaxonomy,
			ComputationExecutor executor, int maxWorkers,
			ProgressMonitor progressMonitor) {
		super(inputs, new ClassTaxonomyCleaningFactory(classTaxonomy), executor, maxWorkers, progressMonitor);
	}

}

class ClassTaxonomyCleaningFactory
		implements
		InputProcessorFactory<ElkClass, InputProcessor<ElkClass>> {

	private final ConcurrentTaxonomy classTaxonomy_;
	
	ClassTaxonomyCleaningFactory(ConcurrentTaxonomy taxonomy) {
		classTaxonomy_ = taxonomy;
	}
	
	@Override
	public InputProcessor<ElkClass> getEngine() {
		return new InputProcessor<ElkClass>() {
			
			/**
			 * Temporary queue of nodes that should be removed from the taxonomy
			 */
			private final Queue<NonBottomClassNode> toRemove = new ConcurrentLinkedQueue<NonBottomClassNode>();			

			@Override
			public void submit(ElkClass elkClass) {
/*				if (classTaxonomy_.getBottomNode().getMembers().remove(elkClass)) {
					return;
				}
				
				NonBottomClassNode node = (NonBottomClassNode) classTaxonomy_.getTypeNode(elkClass);
				
				if (node == null) {
					return;
				}
				if (node.trySetModified())
					toRemove.add(node);
				synchronized (node) {
					for (NonBottomClassNode subNode : node
							.getDirectSatisfiableSubNodes()) {
						if (!subNode.trySetModified())
							continue;
						toRemove.add(subNode);
					}
				}
				if (node.equals(classTaxonomy_.getTopNode())) {
					// removing node assignment for members except owl:Thing
					for (ElkClass member : node.getMembers()) {
						if (!member.equals(PredefinedElkClass.OWL_THING))
							classTaxonomy_.removeSatisfiableClassNode(member);
					}
					classTaxonomy_.getTopNode().clearMembers();
				} else {
					classTaxonomy_.removeNode(node);
					// removing node assignment for members
					for (ElkClass member : node.getMembers()) {
						classTaxonomy_.removeSatisfiableClassNode(member);
					}
				}
				*/
			}

			@Override
			public void process() throws InterruptedException {
				/*
				for (;;) {
					NonBottomClassNode node = toRemove.poll();
					
					if (node == null)
						return;
					
					List<NonBottomClassNode> superNodes = new LinkedList<NonBottomClassNode>();
					
					synchronized (node) {
						for (NonBottomClassNode superNode : node
								.getDirectSuperNodes()) {
							superNodes.add(superNode);
						}
						
						node.clearSuperNodes();
					}
					for (NonBottomClassNode superNode : superNodes) {
						superNode.removeDirectSubNode(node);
					}
				}*/

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
