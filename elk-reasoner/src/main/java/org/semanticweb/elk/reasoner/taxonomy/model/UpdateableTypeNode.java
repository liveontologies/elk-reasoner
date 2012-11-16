/**
 * 
 */
package org.semanticweb.elk.reasoner.taxonomy.model;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObject;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface UpdateableTypeNode<T extends ElkObject, I extends ElkObject> extends TypeNode<T, I>, UpdateableTaxonomyNode<T> {

	public void addDirectSuperNode(UpdateableTypeNode<T, I> superNode);

	public void addDirectSubNode(UpdateableTypeNode<T, I> subNode);
	
	public void addDirectInstanceNode(	UpdateableInstanceNode<ElkClass, ElkNamedIndividual> instanceNode);
	
}
