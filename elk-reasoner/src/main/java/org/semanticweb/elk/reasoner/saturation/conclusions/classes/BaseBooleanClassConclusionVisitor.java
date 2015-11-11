/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.conclusions.classes;

import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class BaseBooleanClassConclusionVisitor<C> extends
		AbstractClassConclusionVisitor<C, Boolean> {

	@Override
	protected Boolean defaultVisit(ClassConclusion conclusion, C cxt) {
		return Boolean.FALSE;
	}

}
