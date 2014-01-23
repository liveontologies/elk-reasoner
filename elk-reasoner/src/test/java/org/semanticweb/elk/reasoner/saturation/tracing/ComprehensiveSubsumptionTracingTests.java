/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import java.util.Deque;
import java.util.LinkedList;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.predefined.PredefinedElkIri;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNode;

/**
 * Visits all atomic subsumptions in a class taxonomy using a {@link TracingTestVisitor}. Used for tracng tests and benchmarking.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ComprehensiveSubsumptionTracingTests implements TracingTests {

	//private static final Logger LOGGER_ = LoggerFactory.getLogger(ComprehensiveSubsumptionTracingTests.class);	
	
	private final Taxonomy<ElkClass> classTaxonomy_;
	
	public ComprehensiveSubsumptionTracingTests(Taxonomy<ElkClass> tax) {
		classTaxonomy_ = tax;
	}
	
	@Override
	public void accept(TracingTestVisitor visitor) throws Exception {
		Deque<TaxonomyNode<ElkClass>> toDo = new LinkedList<TaxonomyNode<ElkClass>>();
		
		toDo.add(classTaxonomy_.getBottomNode());
		
		for (;;) {
			TaxonomyNode<ElkClass> next = toDo.poll();
			
			if (next == null) {
				break;
			}
			
			visitEquivalentClassesTracingTasks(next, visitor);

			for (TaxonomyNode<ElkClass> superNode : next.getDirectSuperNodes()) {
				if (next != classTaxonomy_.getBottomNode()) {
					visitTracingTasksForDirectSuperClasses(next, superNode, visitor);
				}
				toDo.push(superNode);
			}
		}
	}
	
	private void visitTracingTasksForDirectSuperClasses(
			TaxonomyNode<ElkClass> node, TaxonomyNode<ElkClass> superNode,
			TracingTestVisitor visitor) throws Exception {
		for (ElkClass sub : node.getMembers()) {
			if (sub.getIri() == PredefinedElkIri.OWL_NOTHING.get()) {
				continue;
			}
			
			for (ElkClass sup : superNode.getMembers()) {
				if (sup.getIri() == PredefinedElkIri.OWL_THING.get()) {
					continue;
				}
				
				if (sub != sup) {
					//tasks.add(new TracingTask(reasoner_, sub, sup));
					//LOGGER_.trace("Tracing test created: {} => {}", sub, sup);
					
					visitor.visit(sub, sup);
				}
			}
		}
		
	}

	private void visitEquivalentClassesTracingTasks(TaxonomyNode<ElkClass> node, TracingTestVisitor visitor) throws Exception {
		visitTracingTasksForDirectSuperClasses(node, node, visitor);
	}	
}
