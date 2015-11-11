/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.conclusions.classes;

import org.semanticweb.elk.reasoner.saturation.conclusions.model.ObjectPropertyConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubPropertyChain;

/**
 * @author Pavel Klinov
 *
 *         pavel.klinov@uni-ulm.de
 */
public abstract class AbstractObjectPropertyConclusionVIsitor<I, O> implements
		ObjectPropertyConclusion.Visitor<I, O> {

	protected abstract O defaultVisit(ObjectPropertyConclusion conclusion,
			I input);

	@Override
	public O visit(SubPropertyChain conclusion, I input) {
		return defaultVisit(conclusion, input);
	}

}
