/**
 * 
 */
package org.semanticweb.elk.reasoner.stages;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
class ClassTracingInput implements TracingInput {

	final IndexedClassExpression root;
	
	final Conclusion conclusion;
	
	ClassTracingInput(IndexedClassExpression r, Conclusion c) {
		root = r;
		conclusion = c;
	}
}
