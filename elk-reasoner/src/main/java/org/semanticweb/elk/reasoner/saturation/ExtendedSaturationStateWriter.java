/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * The extended writer for situations when new contexts may need to be
 * created/initialized or removed
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public interface ExtendedSaturationStateWriter extends
		BasicSaturationStateWriter {

	public Context getCreateContext(IndexedClassExpression root);

	public void initContext(Context context);

	public void removeContext(Context context);
}
