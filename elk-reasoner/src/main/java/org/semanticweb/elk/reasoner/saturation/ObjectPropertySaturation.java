/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.saturation;

import java.util.ArrayDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import org.semanticweb.elk.reasoner.indexing.ComplexRoleInclusion;
import org.semanticweb.elk.reasoner.indexing.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.IndexedPropertyExpression;
import org.semanticweb.elk.reasoner.indexing.IndexedPropertyExpressionVisitor;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.util.AbstractConcurrentComputation;

/**
 * @author Frantisek Simancik
 *
 */
public class ObjectPropertySaturation { 

	protected final ExecutorService executor;
	protected final int maxWorkers;
	protected final OntologyIndex ontologyIndex;
	
	final AtomicInteger safeRiaNo = new AtomicInteger(0);
	final AtomicInteger unsafeRiaNo = new AtomicInteger(0);
	
	public ObjectPropertySaturation(ExecutorService executor, int maxWorkers, OntologyIndex ontologyIndex) {
		this.executor = executor;
		this.maxWorkers = maxWorkers;
		this.ontologyIndex = ontologyIndex;  
	}
	
	public void compute() {
		RoleHierarchyComputation roleHierarchyComputation = new RoleHierarchyComputation(executor, maxWorkers);
		
		for (IndexedObjectProperty iop : ontologyIndex.getIndexedObjectProperties())
			roleHierarchyComputation.submit(iop);

		for (IndexedPropertyChain ipc : ontologyIndex.getNamedIndexedPropertyChains())
			roleHierarchyComputation.submit(ipc);
		
		roleHierarchyComputation.waitCompletion();
		
		if (!ontologyIndex.getComplexRoleInclusions().isEmpty()) {
		
			for (ComplexRoleInclusion ria : ontologyIndex.getComplexRoleInclusions()) {
				for (IndexedPropertyExpression rightSubProperty : ria.getRightSubProperty().getSaturated().getSubObjectProperties())
					rightSubProperty.getSaturated().rightSubPropertyInComplexInclusions.add(ria);
				for (IndexedPropertyExpression superProperty : ria.getSuperProperty().getSaturated().getSuperObjectProperties())
					superProperty.getSaturated().superPropertyInComplexInclusions.add(ria);
			}
			
			ComplexRiaSafetyChecker complexRiaSafetyChecker = new ComplexRiaSafetyChecker(executor, maxWorkers);
			
			for (ComplexRoleInclusion ria : ontologyIndex.getComplexRoleInclusions())
				complexRiaSafetyChecker.submit(ria);
			
			complexRiaSafetyChecker.waitCompletion();
		}
		System.err.println("  safe RIAs: " + safeRiaNo);
		System.err.println("unsafe RIAs: " + unsafeRiaNo);
	}

	class RoleHierarchyComputation extends AbstractConcurrentComputation<IndexedPropertyExpression> {
		RoleHierarchyComputation(ExecutorService executor, int maxWorkers) { 
			super(executor, maxWorkers, 0, 128);
		}
		
		final IndexedPropertyExpressionVisitor<Void> processor =  new IndexedPropertyExpressionVisitor<Void>() {
			
			public Void visit(IndexedPropertyChain ipc) {
				SaturatedPropertyExpression saturated = new SaturatedPropertyExpression(ipc);
				saturated.derivedSubObjectProperties.add(ipc);
				saturated.derivedSuperObjectProperties.add(ipc);
				if (ipc.getRightComponent() instanceof IndexedPropertyChain)
					addJob(ipc.getRightComponent());
				return null;
			}
			
			public Void visit(IndexedObjectProperty iop) {
				SaturatedPropertyExpression saturated = new SaturatedPropertyExpression(iop);
				iop.setSaturated(saturated);

				//compute all subproperties
				ArrayDeque<IndexedObjectProperty> queue = new ArrayDeque<IndexedObjectProperty>();
				saturated.derivedSubObjectProperties.add(iop);
				queue.addLast(iop);
				while (!queue.isEmpty()) {
					IndexedObjectProperty r = queue.removeLast();
					if (r.getToldSubObjectProperties() != null)
						for (IndexedObjectProperty s : r.getToldSubObjectProperties())
							if (saturated.derivedSubObjectProperties.add(s))
								queue.addLast(s);
				}

				//compute all superproperties
				queue.clear();
				saturated.derivedSuperObjectProperties.add(iop);
				queue.addLast(iop);
				while (!queue.isEmpty()) {
					IndexedObjectProperty r = queue.removeLast();
					if (r.getToldSuperObjectProperties() != null)
						for (IndexedObjectProperty s : r.getToldSuperObjectProperties())
							if (saturated.derivedSuperObjectProperties.add(s))
								queue.addLast(s);
				}
				return null;
			}
		}; 

		@Override
		protected void process(IndexedPropertyExpression ipc) {
			ipc.accept(processor);
		}
	}
	
	class ComplexRiaSafetyChecker extends AbstractConcurrentComputation<ComplexRoleInclusion> {

		public ComplexRiaSafetyChecker(ExecutorService executor, int maxWorkers) {
			super(executor, maxWorkers, 0, 128);
		}

		@Override
		protected void process(ComplexRoleInclusion ria) {
			ria.setSafe(true);
			IndexedPropertyExpression r = ria.getSuperProperty();
			IndexedPropertyExpression r1 = ria.getLeftSubProperty();
			for (ComplexRoleInclusion subRia : ria.getRightSubProperty().getSaturated().getSuperPropertyInComplexInclusions()) {
				IndexedPropertyExpression r2 = subRia.getLeftSubProperty();
				IndexedPropertyExpression r3 = subRia.getRightSubProperty();
				
				// check if r1.r2.r3 -> r in a left-linear way
				boolean safe = false;
				check:
				for (ComplexRoleInclusion p : r.getSaturated().getSuperPropertyInComplexInclusions())
					if (p.getRightSubProperty().getSaturated().getSubObjectProperties().contains(r3))
						for (ComplexRoleInclusion q : p.getLeftSubProperty().getSaturated().getSuperPropertyInComplexInclusions())
							if (q.getLeftSubProperty().getSaturated().getSubObjectProperties().contains(r1) &&
								q.getRightSubProperty().getSaturated().getSubObjectProperties().contains(r2)) {
									safe = true;
									break check;
							}
				
				if (!safe) {
					ria.setSafe(false);
					unsafeRiaNo.incrementAndGet();
					return;
				}
			}
			safeRiaNo.incrementAndGet();
		}
	}
		
}
