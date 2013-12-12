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
	 * Returns the contexts where the inference was actually made. This may be
	 * different from the context where the inference's conclusion was produced.
	 * Subclasses whose conclusions are always produced in the same context
	 * where the inference happened may not store the latter explicitly. In that
	 * case they will simply return the context passed into this method.
	 * 
	 * @param defaultContext
	 * @return
	 */
	public Context getContext(Context defaultContext);

	public <R> R accept(InferenceVisitor<R> visitor);
}
