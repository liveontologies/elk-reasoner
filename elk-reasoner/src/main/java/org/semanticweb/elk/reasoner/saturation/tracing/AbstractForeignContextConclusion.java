/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * Represents an inference which took place in some foreign context and was
 * propagated to the current context.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
abstract class AbstractForeignContextConclusion implements Conclusion {

	protected final Context context;

	AbstractForeignContextConclusion(Context cxt) {
		context = cxt;
	}

	@Override
	public Context getSourceContext(Context defaultContext) {
		return context;
	}

}
