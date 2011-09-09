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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import org.semanticweb.elk.reasoner.indexing.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.IndexedPropertyComposition;
import org.semanticweb.elk.reasoner.indexing.IndexedPropertyExpression;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.util.collections.HashListMultimap;
import org.semanticweb.elk.util.collections.Pair;
import org.semanticweb.elk.util.concurrent.AbstractConcurrentComputation;

/**
 * Computes the transitive closure of object property inclusions. Sets up
 * multimaps for fast retrieval of property compositions. 
 * 
 * @author Frantisek Simancik
 *
 */
/**
 * @author Frantisek Simancik
 *
 */
public class ObjectPropertySaturation { 

	protected final ExecutorService executor;
	protected final int maxWorkers;
	protected final OntologyIndex ontologyIndex;

	// Safety for left-linear composition is not used in the current implementation.
/*
	protected final Multimap<IndexedPropertyExpression, IndexedPropertyComposition> subChains;
	final AtomicInteger safeRiaNo = new AtomicInteger(0);
	final AtomicInteger unsafeRiaNo = new AtomicInteger(0);
*/
	
	public ObjectPropertySaturation(ExecutorService executor, int maxWorkers, OntologyIndex ontologyIndex) {
		this.executor = executor;
		this.maxWorkers = maxWorkers;
		this.ontologyIndex = ontologyIndex;  
		
//		this.subChains = new HashListMultimap<IndexedPropertyExpression, IndexedPropertyComposition> ();
	}
	
	/**
	 * 
	 */
	public void compute() {
		// set up property hierarchy
		RoleHierarchyComputation roleHierarchyComputation = 
			new RoleHierarchyComputation(executor, maxWorkers);
		
		for (IndexedObjectProperty iop : ontologyIndex.getIndexedObjectProperties()) {
			iop.resetSaturated();
			roleHierarchyComputation.submit(iop);
		}
		
		for (IndexedPropertyComposition ipc : ontologyIndex.getIndexedPropertyChains()) {		
			ipc.resetSaturated();
			if (ipc.isAuxiliary()) {
				SaturatedPropertyExpression saturated = new SaturatedPropertyExpression(ipc);
				ipc.setSaturated(saturated);
				saturated.derivedSubObjectProperties.add(ipc);
				saturated.derivedSuperObjectProperties.add(ipc);
			}
		}
		
		roleHierarchyComputation.waitCompletion();
		

		//set up property composition
		HashMap<Pair<IndexedPropertyExpression, IndexedPropertyExpression>, ArrayList<IndexedPropertyExpression>>
		m = new HashMap<Pair<IndexedPropertyExpression, IndexedPropertyExpression>, ArrayList<IndexedPropertyExpression>> ();
		
		for (IndexedPropertyComposition ria : ontologyIndex.getIndexedPropertyChains())
			for (IndexedPropertyExpression rightProperty : ria.getRightProperty().getSaturated().getSubObjectProperties())
				for (IndexedPropertyExpression leftProperty : ria.getLeftProperty().getSaturated().getSubObjectProperties()) {
	
					Pair<IndexedPropertyExpression, IndexedPropertyExpression> body = 
						new Pair<IndexedPropertyExpression, IndexedPropertyExpression> (leftProperty, rightProperty);
					ArrayList<IndexedPropertyExpression> list = m.get(body);
					
					if (list == null) {
						list = new ArrayList<IndexedPropertyExpression> ();
						m.put(body, list);
					}
					
					list.add(ria.getSuperProperty());
				}

		RedundantCompositionsElimination elimination =
			new RedundantCompositionsElimination(executor, maxWorkers);
		
		for (Map.Entry<Pair<IndexedPropertyExpression, IndexedPropertyExpression>, 
				ArrayList<IndexedPropertyExpression>> e : m.entrySet()) {
			
			SaturatedPropertyExpression firstSat = e.getKey().getFirst().getSaturated();
			if (firstSat.propertyCompositionsByRightSubProperty == null)
				firstSat.propertyCompositionsByRightSubProperty = 
					new HashListMultimap<IndexedPropertyExpression, IndexedPropertyExpression>();
			firstSat.propertyCompositionsByRightSubProperty.put(e.getKey().getSecond(), e.getValue());
		
			SaturatedPropertyExpression secondSat = e.getKey().getSecond().getSaturated(); 
			if (secondSat.propertyCompositionsByLeftSubProperty == null)
				secondSat.propertyCompositionsByLeftSubProperty = 
					new HashListMultimap<IndexedPropertyExpression, IndexedPropertyExpression>();
			secondSat.propertyCompositionsByLeftSubProperty.put(e.getKey().getFirst(), e.getValue());
			
			elimination.submit(e.getValue());
		}
		m = null;
		elimination.waitCompletion();
	}

	
	class RoleHierarchyComputation extends AbstractConcurrentComputation<IndexedObjectProperty> {
		RoleHierarchyComputation(ExecutorService executor, int maxWorkers) { 
			super(executor, maxWorkers, 0, 128);
		}
		
		@Override
		protected void process(IndexedObjectProperty iop) {
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
		}
	}

	/* if R1.R2 -> S1 and R1.R2 -> S2 with S1 -> S2,
	 * then the latter composition is redundant and is removed
	 */
	class RedundantCompositionsElimination extends AbstractConcurrentComputation<ArrayList<IndexedPropertyExpression>> {

		public RedundantCompositionsElimination(ExecutorService executor, int maxWorkers) {
			super(executor, maxWorkers, 0, 128);
		}

		@Override
		protected void process(ArrayList<IndexedPropertyExpression> list) {
			for (int i = 0; i < list.size(); i++)
				if (list.get(i) != null) {
					Set<IndexedPropertyExpression> superProperties =
						list.get(i).getSaturated().getSuperObjectProperties();
					
					for (int j = 0; j < list.size(); j++)
						if (j != i && list.get(j) != null && superProperties.contains(list.get(j)))
							list.set(j, null);
				}

			Iterator<IndexedPropertyExpression> iter = list.iterator();  
			while (iter.hasNext()) {  
				if (iter.next() == null) {  
					iter.remove();  
				}  
			}
			
			list.trimToSize();
		}
		
	}

}
