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
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNode;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class TaxonomyNodeIndexConsistencyVisitor<T extends ElkObject> implements
		TaxonomyNodeVisitor<T> {

	private final Taxonomy<T> taxonomy_;
	
	public TaxonomyNodeIndexConsistencyVisitor(Taxonomy<T> t) {
		taxonomy_ = t;
	}
	
	@Override
	public void visit(TaxonomyNode<T> node, List<TaxonomyNode<T>> pathFromStart) {
		
		if (!node.getMembers().contains(node.getCanonicalMember())) {
			throw new InvalidTaxonomyException("Canonical member is not a member? " + node.getCanonicalMember() + ", members: " + node.getMembers());
		}
		
		for (T obj : node.getMembers()) {
			TaxonomyNode<T> n = taxonomy_.getNode(obj);

			if (n != node) {
				throw new InvalidTaxonomyException(
						"Invalid taxonomy node index at object " + obj);
			}
		}
	}

}
