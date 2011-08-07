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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import org.semanticweb.elk.reasoner.indexing.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.IndexedPropertyComposition;
import org.semanticweb.elk.reasoner.indexing.IndexedPropertyExpression;
import org.semanticweb.elk.reasoner.indexing.IndexedPropertyExpressionVisitor;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.util.AbstractConcurrentComputation;
import org.semanticweb.elk.util.HashListMultimap;
import org.semanticweb.elk.util.Multimap;

/**
 * @author Frantisek Simancik
 *
 */
public class ObjectPropertySaturation { 

	protected final ExecutorService executor;
	protected final int maxWorkers;
	protected final OntologyIndex ontologyIndex;
	
	protected final List<IndexedPropertyComposition> allPropertyChains;
	protected final Multimap<IndexedPropertyExpression, IndexedPropertyComposition> subChains;
	
	final AtomicInteger safeRiaNo = new AtomicInteger(0);
	final AtomicInteger unsafeRiaNo = new AtomicInteger(0);
	
	public ObjectPropertySaturation(ExecutorService executor, int maxWorkers, OntologyIndex ontologyIndex) {
		this.executor = executor;
		this.maxWorkers = maxWorkers;
		this.ontologyIndex = ontologyIndex;  
		
		this.allPropertyChains = Collections.synchronizedList(new LinkedList<IndexedPropertyComposition> ());
		this.subChains = new HashListMultimap<IndexedPropertyExpression, IndexedPropertyComposition> ();
	}
	
	public void compute() {
		RoleHierarchyComputation roleHierarchyComputation = new RoleHierarchyComputation(executor, maxWorkers);
		
		for (IndexedObjectProperty iop : ontologyIndex.getIndexedObjectProperties())
			roleHierarchyComputation.submit(iop);

		for (IndexedPropertyComposition ipc : ontologyIndex.getIndexedPropertyChains())
			roleHierarchyComputation.submit(ipc);
		
		roleHierarchyComputation.waitCompletion();
		
		if (!allPropertyChains.isEmpty()) {
		
			for (IndexedPropertyComposition ria : allPropertyChains) {
				for (IndexedPropertyExpression rightProperty : ria.getRightProperty().getSaturated().getSubObjectProperties())
					for (IndexedPropertyExpression leftProperty : ria.getLeftProperty().getSaturated().getSubObjectProperties()) {
						rightProperty.getSaturated().addPropertyChainByLeftSubProperty(ria, leftProperty);
						leftProperty.getSaturated().addPropertyChainByRightSubProperty(ria, rightProperty);
					}
				for (IndexedPropertyExpression superProperty : ria.getSuperProperty().getSaturated().getSuperObjectProperties())
					subChains.add(superProperty, ria);
			}
			
			ComplexRiaSafetyChecker complexRiaSafetyChecker = new ComplexRiaSafetyChecker(executor, maxWorkers);
			
			for (IndexedPropertyComposition ria : allPropertyChains)
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
			
			public Void visit(IndexedPropertyComposition ipc) {
				allPropertyChains.add(ipc);
				
				ipc.resetSaturated();
				if (ipc.getSuperProperty() == ipc) {
					SaturatedPropertyExpression saturated = new SaturatedPropertyExpression(ipc);
					ipc.setSaturated(saturated);
					saturated.derivedSubObjectProperties.add(ipc);
					saturated.derivedSuperObjectProperties.add(ipc);
				}
				if (ipc.getRightProperty() instanceof IndexedPropertyComposition)
					addJob(ipc.getRightProperty());
				return null;
			}
			
			public Void visit(IndexedObjectProperty iop) {
				iop.resetSaturated();
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
	
	class ComplexRiaSafetyChecker extends AbstractConcurrentComputation<IndexedPropertyComposition> {

		public ComplexRiaSafetyChecker(ExecutorService executor, int maxWorkers) {
			super(executor, maxWorkers, 0, 128);
		}

		@Override
		protected void process(IndexedPropertyComposition ria) {
			ria.setSafe(true);
			IndexedPropertyExpression r = ria.getSuperProperty();
			IndexedPropertyExpression r1 = ria.getLeftProperty();
			if (subChains.get(ria.getRightProperty()) != null)
			for (IndexedPropertyComposition subRia : subChains.get(ria.getRightProperty())) {
				IndexedPropertyExpression r2 = subRia.getLeftProperty();
				IndexedPropertyExpression r3 = subRia.getRightProperty();
				
				// check if r1.r2.r3 -> r in a left-linear way
				boolean safe = false;
				check:
				if (subChains.get(r) != null)
				for (IndexedPropertyComposition p : subChains.get(r))
					if (p.getRightProperty().getSaturated().getSubObjectProperties().contains(r3))
						if (subChains.get(p.getLeftProperty()) != null)
						for (IndexedPropertyComposition q : subChains.get(p.getLeftProperty()))
							if (q.getLeftProperty().getSaturated().getSubObjectProperties().contains(r1) &&
								q.getRightProperty().getSaturated().getSubObjectProperties().contains(r2)) {
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
