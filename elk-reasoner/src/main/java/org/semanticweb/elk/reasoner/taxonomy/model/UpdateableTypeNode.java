/**
 * 
 */
package org.semanticweb.elk.reasoner.taxonomy.model;

import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObject;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface UpdateableTypeNode<T extends ElkObject, I extends ElkObject> extends TypeNode<T, I>, UpdateableTaxonomyNode<T> {

	@Override
	public Set<UpdateableTypeNode<T, I>> getDirectUpdateableSubNodes();
	
	@Override
	public Set<UpdateableTypeNode<T, I>> getDirectSuperNodes();
	
	public void addDirectInstanceNode(	UpdateableInstanceNode<ElkClass, ElkNamedIndividual> instanceNode);
	
}
