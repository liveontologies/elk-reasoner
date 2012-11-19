/**
 * 
 */
package org.semanticweb.elk.reasoner.taxonomy.model;

import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkObject;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface UpdateableTaxonomyNode<T extends ElkObject> extends UpdateableNode<T>, TaxonomyNode<T> {

	public void addDirectSuperNode(UpdateableTaxonomyNode<T> superNode);

	public void addDirectSubNode(UpdateableTaxonomyNode<T> subNode);
	
	public Set<? extends UpdateableTaxonomyNode<T>> getDirectUpdateableSubNodes();
	
	@Override
	public Set<? extends UpdateableTaxonomyNode<T>> getDirectSuperNodes();
	
	public boolean removeDirectSubNode(UpdateableTaxonomyNode<T> subNode);
	
	public boolean removeDirectSuperNode(UpdateableTaxonomyNode<T> superNode);
	
	public boolean trySetModified(boolean modified);
	
	public boolean isModified();
}
