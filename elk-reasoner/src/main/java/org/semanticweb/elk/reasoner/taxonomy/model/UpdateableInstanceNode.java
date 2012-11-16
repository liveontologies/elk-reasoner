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
public interface UpdateableInstanceNode<T extends ElkObject, I extends ElkObject> extends InstanceNode<T, I>, UpdateableNode<I> {

	public void addDirectTypeNode(UpdateableTypeNode<T, I> typeNode);
}
