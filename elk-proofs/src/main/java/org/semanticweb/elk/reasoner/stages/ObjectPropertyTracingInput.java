/**
 * 
 */
package org.semanticweb.elk.reasoner.stages;

import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ObjectPropertyConclusion;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ObjectPropertyTracingInput implements TracingInput {

	ObjectPropertyConclusion conclusion;
	
	ObjectPropertyTracingInput(ObjectPropertyConclusion c) {
		conclusion = c;
	}
}
