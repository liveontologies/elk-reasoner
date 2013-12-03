/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
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

	@Override
	public <O> O accept(ConclusionVisitor<O> conclusionVisitor, Context defaultContext) {
		return null;
	}

}
