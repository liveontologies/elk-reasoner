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

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.reasoner.taxonomy.DepthFirstSearch.Direction;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNode;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 *         
 * @param <T> 
 */
public class TaxonomyAcyclicityAndReductionValidator<T extends ElkEntity>
		implements TaxonomyValidator<T> {

	@Override
	public void validate(Taxonomy<T> taxonomy) {
		for (TaxonomyNode<T> node : taxonomy.getNodes()) {
			if (node == taxonomy.getTopNode()) {
				continue;
			}
			
			check(node);
		}
	}

	private void check(final TaxonomyNode<T> current) {
		// compute the sets of direct and indirect successors
		// TODO Reuse indirect successors from previously visited nodes
		final Set<? extends TaxonomyNode<T>> directSuccessors = DepthFirstSearch
				.<T> getSuccessors(current, Direction.DOWN);
		// main checks
		if (directSuccessors.contains(current)) {
			throw new InvalidTaxonomyException("Self loop at " + current);
		}

		TaxonomyNodeVisitor<T> checker = new TaxonomyNodeVisitor<T>() {

			@Override
			public void visit(TaxonomyNode<T> node,
					List<TaxonomyNode<T>> pathFromStart) {

				if (pathFromStart.size() > 1) {
					if (node == current) {
						// TODO restore the cycle
						throw new InvalidTaxonomyException("Cycle detected at "
								+ current);
					}

					if (directSuccessors.contains(node)) {
						throw new InvalidTaxonomyException(
								"Taxonomy not transitively reduced at "
										+ current);
					}
				}
			}

		};

		new DepthFirstSearch<T>().run(current, Direction.DOWN, checker);
	}

}