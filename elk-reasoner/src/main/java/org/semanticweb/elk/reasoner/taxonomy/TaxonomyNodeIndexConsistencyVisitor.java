/**
 * 
 */
package org.semanticweb.elk.reasoner.taxonomy;

import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNode;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class TaxonomyNodeIndexConsistencyVisitor<T extends ElkObject> implements
		TaxonomyNodeVisitor<T> {

	@Override
	public void visit(TaxonomyNode<T> node, List<TaxonomyNode<T>> pathFromStart) {
		
		if (!node.getMembers().contains(node.getCanonicalMember())) {
			throw new InvalidTaxonomyException("Canonical member is not a member? " + node.getCanonicalMember() + ", members: " + node.getMembers());
		}
		
		for (T obj : node.getMembers()) {
			TaxonomyNode<T> n = node.getTaxonomy().getNode(obj);

			if (n != node) {
				throw new InvalidTaxonomyException(
						"Invalid taxonomy node index at object " + obj);
			}
		}
	}

}
