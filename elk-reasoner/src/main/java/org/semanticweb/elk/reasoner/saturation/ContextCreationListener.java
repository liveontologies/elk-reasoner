/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation;

import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface ContextCreationListener {

	public void notifyContextCreation(Context newContext);
}
