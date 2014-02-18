/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class BaseBooleanInferenceVisitor<C> extends BaseInferenceVisitor<Boolean, C> {

	@Override
	protected Boolean defaultTracedVisit(Inference conclusion, C parameter) {
		return false;
	}

	
}
