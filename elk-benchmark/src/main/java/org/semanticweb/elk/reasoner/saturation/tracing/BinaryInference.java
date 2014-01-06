/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class BinaryInference extends AbstractInference {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8270599150383557136L;

	private final Conclusion premise_;
	
	/**
	 * 
	 */
	public BinaryInference(Context context, Conclusion conclusion, Conclusion premise) {
		super(context, conclusion);
		premise_ = premise;
	}

	public Conclusion getPremise() {
		return premise_;
	}

}
