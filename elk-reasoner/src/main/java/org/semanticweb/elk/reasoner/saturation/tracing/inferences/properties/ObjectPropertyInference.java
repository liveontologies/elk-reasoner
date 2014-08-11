/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties;

import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ObjectPropertyInferenceVisitor;


/**
 * The root of all inferences on properties.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface ObjectPropertyInference/* extends ObjectPropertyConclusion*/ {

	public <I, O> O acceptTraced(ObjectPropertyInferenceVisitor<I, O> visitor, I input);
}
