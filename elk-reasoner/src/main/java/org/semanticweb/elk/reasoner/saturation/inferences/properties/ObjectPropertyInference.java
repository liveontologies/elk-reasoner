/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.inferences.properties;

import org.semanticweb.elk.reasoner.saturation.conclusions.model.ObjectPropertyConclusion;

/**
 * The root of all inferences on properties.
 * 
 * @author Pavel Klinov
 *
 *         pavel.klinov@uni-ulm.de
 */
public interface ObjectPropertyInference extends ObjectPropertyConclusion {

	public <I, O> O accept(ObjectPropertyInferenceVisitor<I, O> visitor, I input);
}
