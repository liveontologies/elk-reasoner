/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.conclusions.visitors;

import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ObjectPropertyConclusion;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class DummyObjectPropertyConclusionVisitor<I, O> extends
		AbstractObjectPropertyConclusionVIsitor<I, O> {

	@Override
	protected O defaultVisit(ObjectPropertyConclusion conclusion, I input) {
		return null;
	}

}
