/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class BaseBooleanTracedConclusionVisitor<C> extends BaseTracedConclusionVisitor<Boolean, C> {

	@Override
	protected Boolean defaultTracedVisit(TracedConclusion conclusion, C parameter) {
		return false;
	}

	
}
