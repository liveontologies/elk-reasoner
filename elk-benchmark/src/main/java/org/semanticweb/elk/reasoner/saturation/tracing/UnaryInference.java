/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class UnaryInference extends AbstractInference {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7073906225844947123L;

	/**
	 * 
	 */
	public UnaryInference(Context cxt, Conclusion c) {
		super(cxt, c);
	}

}
