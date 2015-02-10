/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.predefined.PredefinedElkIri;
import org.semanticweb.elk.reasoner.ElkInconsistentOntologyException;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNode;

/**
 * Visits all atomic subsumptions in a class taxonomy using a {@link TracingTestVisitor}. Used for tracing tests and benchmarking.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ComprehensiveSubsumptionTracingTests implements TracingTests {
	
	private final Taxonomy<ElkClass> classTaxonomy_;
	
	public ComprehensiveSubsumptionTracingTests(Reasoner reasoner) throws ElkException {
		classTaxonomy_ = initTaxonomy(reasoner);
	}
	
	private static Taxonomy<ElkClass> initTaxonomy(Reasoner reasoner) throws ElkException {
		try {
			return reasoner.getTaxonomy();
		} catch (ElkInconsistentOntologyException e) {
			// no worries
			return null;
		} 
	}
	
	@Override
	public void accept(TracingTestVisitor visitor) throws Exception {
		if (classTaxonomy_ == null) {
			visitor.inconsistencyTest();
			return;
		}
		
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
					visitor.subsumptionTest(sub, sup);
				}
			}
		}
		
	}

	private void visitEquivalentClassesTracingTasks(TaxonomyNode<ElkClass> node, TracingTestVisitor visitor) throws Exception {
		visitTracingTasksForDirectSubClasses(node, node, visitor);
	}

}
