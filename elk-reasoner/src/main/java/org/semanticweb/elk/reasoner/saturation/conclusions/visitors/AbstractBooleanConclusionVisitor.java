/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.conclusions.visitors;

import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class AbstractBooleanConclusionVisitor<C> extends AbstractConclusionVisitor<C,Boolean> {

	@Override
	protected Boolean defaultVisit(Conclusion conclusion, C cxt) {
		return Boolean.FALSE;
	}

}
