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

import java.util.List;
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.reasoner.taxonomy.DepthFirstSearch.Direction;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNode;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.collections.Operations.Condition;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class TaxonomyAcyclicityAndReductionValidator<T extends ElkObject> implements TaxonomyValidator<T> {

	@Override
	public void validate(Taxonomy<T> taxonomy) {
		//we're not doing a depth-first search here because in the presence of cycles
		//we may miss some nodes (e.g. getDirectSuperNodes() may return the empty set for Bottom, etc.)
		for (TaxonomyNode<T> node : taxonomy.getNodes()) {
			check(node);
		}
	}


	private void check(TaxonomyNode<T> current) {
		// compute the sets of direct and indirect successors
		// TODO Reuse indirect successors from previously visited nodes
		final NodeData<T> data = new NodeData<T>(
				DepthFirstSearch.<T> getSuccessors(current, Direction.DOWN),
				getIndirectlyReachableSuccessors(current, Direction.DOWN));
		// main checks
		if (data.directSuccessors.contains(current)) {
			throw new InvalidTaxonomyException("Self loop at " + current);
		}

		if (data.indirectSuccessors.contains(current)) {
			//TODO restore the cycle
			throw new InvalidTaxonomyException("Cycle detected at " + current);
		}

		if (Operations
				.filter(data.directSuccessors,
						new Condition<TaxonomyNode<T>>() {
							@Override
							public boolean holds(TaxonomyNode<T> element) {
								return data.indirectSuccessors
										.contains(element);
							}
						}).iterator().hasNext()) {
			//TODO create a proper report
			throw new InvalidTaxonomyException(
					"Taxonomy not transitively reduced at " + current);
		}
	}

	private Set<TaxonomyNode<T>> getIndirectlyReachableSuccessors(
			final TaxonomyNode<T> current, final Direction reachabilityDir) {
		// run another DFS
		DepthFirstSearch<T> dfs = new DepthFirstSearch<T>();
		final Set<TaxonomyNode<T>> reachable = new ArrayHashSet<TaxonomyNode<T>>();

		dfs.run(current, reachabilityDir, new TaxonomyNodeVisitor<T>() {

			@Override
			public void visit(TaxonomyNode<T> node,
					List<TaxonomyNode<T>> pathFromStart) {
				if (pathFromStart.size() > 1) {
					reachable.add(node);
				}
			}
		});

		return reachable;
	}

}

class NodeData<T extends ElkObject> {

	final Set<? extends TaxonomyNode<T>> directSuccessors;
	final Set<TaxonomyNode<T>> indirectSuccessors;

	NodeData() {
		directSuccessors = new ArrayHashSet<TaxonomyNode<T>>();
		indirectSuccessors = new ArrayHashSet<TaxonomyNode<T>>();
	}

	NodeData(Set<? extends TaxonomyNode<T>> direct,
			Set<TaxonomyNode<T>> indirect) {
		directSuccessors = direct;
		indirectSuccessors = indirect;
	}
}