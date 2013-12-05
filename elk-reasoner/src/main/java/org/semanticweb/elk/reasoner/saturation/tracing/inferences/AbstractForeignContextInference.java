/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences;

import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * Represents an inference which took place in some foreign context and was
 * propagated to the current context.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class AbstractForeignContextInference extends AbstractInference {

	protected final Context context;

	AbstractForeignContextInference(Context cxt) {
		context = cxt;
	}

	@Override
	public Context getContext(Context defaultContext) {
		return context;
	}

}
