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
public class AbstractInference implements Inference {

	protected final Conclusion conclusion;
	
	protected final Context context;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5925129695725933271L;
	
	public AbstractInference(Context cxt, Conclusion c) {
		context = cxt;
		conclusion = c;
	}

	@Override
	public Conclusion getConclusion() {
		return conclusion;
	}

	@Override
	public Context getContext() {
		return context;
	}

}
