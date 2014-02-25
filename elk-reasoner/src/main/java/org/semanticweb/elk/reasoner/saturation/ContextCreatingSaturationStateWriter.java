/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface ContextCreatingSaturationStateWriter<C extends Context> extends SaturationStateWriter<C> {

	/**
	 * 
	 * @param root
	 * @return
	 */
	public C getCreateContext(IndexedClassExpression root);
}
