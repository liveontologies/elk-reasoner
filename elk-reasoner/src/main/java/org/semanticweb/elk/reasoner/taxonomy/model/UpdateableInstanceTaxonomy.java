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
public interface UpdateableInstanceTaxonomy<T extends ElkObject, I extends ElkObject> extends InstanceTaxonomy<T, I>, UpdateableTaxonomy<T> {

	public UpdateableTypeNode<T, I> getCreateTypeNode(Collection<T> member);
	
	public UpdateableInstanceNode<T, I> getCreateIndividualNode(Collection<I> member);
	
	public UpdateableTypeNode<T, I> getUpdateableTypeNode(T elkObject);

	public boolean removeInstanceNode(I instance); 
}
