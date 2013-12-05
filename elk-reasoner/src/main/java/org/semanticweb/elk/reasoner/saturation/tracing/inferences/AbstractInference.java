/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences;

import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
abstract class AbstractInference implements Inference {

	@Override
	public Context getContext(Context defaultContext) {
		return defaultContext;
	}


}
