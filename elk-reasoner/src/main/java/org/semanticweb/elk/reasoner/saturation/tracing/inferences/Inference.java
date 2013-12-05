/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences;

import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * Inference represents a single application of a reasoning rule. It is always
 * done in some specific context and has some premises (which depend on the
 * exact inference rule). It also has a single conclusion which isn't stored
 * explicitly because inferences are expected to be indexed by conclusions.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public interface Inference {

	/**
	 * TODO
	 * @param defaultContext
	 * @return
	 */
	public Context getContext(Context defaultContext);

}
