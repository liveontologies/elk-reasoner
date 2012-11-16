/**
 * 
 */
package org.semanticweb.elk.reasoner.taxonomy.model;

import java.util.Collection;

import org.semanticweb.elk.owl.interfaces.ElkObject;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface UpdateableTaxonomy<T extends ElkObject> extends Taxonomy<T> {

	public UpdateableTaxonomyNode<T> getCreateNode(Collection<T> members); 
	
	public boolean addToBottomNode(T member);
	
	public boolean removeNode(T member);
	
	public UpdateableTaxonomyNode<T> getUpdateableNode(T elkObject);

	public Iterable<? extends UpdateableTaxonomyNode<T>> getUpdateableNodes();
	
}
