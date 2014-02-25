/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

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
		Set<TaxonomyNode<ElkClass>> visited = new HashSet<TaxonomyNode<ElkClass>>();
		Deque<TaxonomyNode<ElkClass>> toDo = new LinkedList<TaxonomyNode<ElkClass>>();
		
		toDo.add(classTaxonomy_.getTopNode());
		
		for (;;) {
			TaxonomyNode<ElkClass> next = toDo.poll();
			
			if (next == null) {
				break;
			}
			
			visitEquivalentClassesTracingTasks(next, visitor);
	
			for (TaxonomyNode<ElkClass> subNode : next.getDirectSubNodes()) {
				if (subNode != classTaxonomy_.getBottomNode() && next != classTaxonomy_.getTopNode()) {
					visitTracingTasksForDirectSubClasses(subNode, next, visitor);
				}
				
				if (visited.add(subNode)) {
					toDo.push(subNode);
				}
			}
		}
	}
	
	private void visitTracingTasksForDirectSubClasses(
			TaxonomyNode<ElkClass> node, TaxonomyNode<ElkClass> superNode,
			TracingTestVisitor visitor) throws Exception {
		
		for (ElkClass sub : node.getMembers()) {
			if (sub.getIri().equals(PredefinedElkIri.OWL_NOTHING.get())) {
				continue;
			}
			
			for (ElkClass sup : superNode.getMembers()) {
				if (sup.getIri().equals(PredefinedElkIri.OWL_THING.get())) {
					continue;
				}
				
				if (sub != sup) {
					visitor.visit(sub, sup);
				}
			}
		}
		
	}

	private void visitEquivalentClassesTracingTasks(TaxonomyNode<ElkClass> node, TracingTestVisitor visitor) throws Exception {
		visitTracingTasksForDirectSubClasses(node, node, visitor);
	}

	/*@Override
	public void accept(TracingTestVisitor visitor) throws Exception {
		Set<ElkClass> canonicalElkClasses = new HashSet<ElkClass>();
		
		for (TaxonomyNode<ElkClass> classNode : classTaxonomy_.getNodes()) {
			canonicalElkClasses.add(classNode.getCanonicalMember());
		}

		for (ElkClass elkClass : canonicalElkClasses) {
			TaxonomyNode<ElkClass> classNode = classTaxonomy_.getNode(elkClass);

			for (TaxonomyNode<ElkClass> childNode : classNode.getDirectSubNodes()) {
				if (childNode != classTaxonomy_.getBottomNode() && classNode != classTaxonomy_.getTopNode()) {
					visitor.visit(childNode.getCanonicalMember(), classNode.getCanonicalMember());
				}
			}

			for (ElkClass sub : classNode.getMembers()) {
				if (sub.getIri().equals(PredefinedElkIri.OWL_NOTHING.get())) {
					continue;
				}
				
				for (ElkClass sup : classNode.getMembers()) {
					if (sup.getIri().equals(PredefinedElkIri.OWL_THING.get())) {
						continue;
					}
					
					if (sub != sup) {
						visitor.visit(sub, sup);
					}
				}
			}
		}
	}*/	
}
