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

import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.reasoner.taxonomy.nodes.TaxonomyNode;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class TaxonomyLinkConsistencyVisitor<T extends ElkObject> implements TaxonomyNodeVisitor<T> {

	@Override
	public void visit(TaxonomyNode<T> node,
			List<TaxonomyNode<T>> pathFromStart) {
		// Check parent/child links are consistent
		for (TaxonomyNode<T> parent : node.getDirectSuperNodes()) {
			if (!parent.getDirectSubNodes().contains(node)) {
				String ln = System.getProperty("line.separator");

				throw new InvalidTaxonomyException(
						"Invalid taxonomy: the parent/child relationships between "
								+ ln + parent.toString() + ln + node.toString()
								+ ln + " are inconsistent");
			}
		}
	}
}