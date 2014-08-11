/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.conclusions.interfaces;

import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ObjectPropertyConclusionVisitor;


/**
 * The interface for objects representing object property inferences. Used
 * primarily for tracing.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public interface ObjectPropertyConclusion {

	public <I, O> O accept(ObjectPropertyConclusionVisitor<I, O> visitor, I input);

}
