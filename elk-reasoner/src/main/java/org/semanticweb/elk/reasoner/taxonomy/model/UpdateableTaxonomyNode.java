/**
 * 
 */
package org.semanticweb.elk.reasoner.taxonomy.model;

import org.semanticweb.elk.owl.interfaces.ElkObject;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface UpdateableTaxonomyNode<T extends ElkObject> extends UpdateableNode<T>, TaxonomyNode<T> {

	public void addDirectSuperNode(UpdateableTaxonomyNode<T> superNode);

	public void addDirectSubNode(UpdateableTaxonomyNode<T> subNode);
}
