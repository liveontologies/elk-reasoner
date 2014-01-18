/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.conclusions;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class BaseBooleanConclusionVisitor<C> extends BaseConclusionVisitor<Boolean, C> {

	@Override
	protected Boolean defaultVisit(Conclusion conclusion, C cxt) {
		return false;
	}

}
