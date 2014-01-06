/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import java.io.Serializable;

import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface Inference extends Serializable {

	public Conclusion getConclusion();
	
	public Context getContext();
}
