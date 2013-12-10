/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.saturation.tracing.inferences.InferenceVisitor;

/**
 * Represents a trace (i.e., a "proof" in some sense) as an object which
 * provides access to its inference via a {@link InferenceVisitor}.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public interface Trace {

	public <R> R accept(InferenceVisitor<R> visitor);
}
