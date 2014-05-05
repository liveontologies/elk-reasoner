/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.factories;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.saturation.SaturationJob;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
class SubContextSaturationJob<J> extends SaturationJob<IndexedClassExpression> {

	private final IndexedObjectProperty subRoot_;
	
	private final J initiatorJob_;
	
	SubContextSaturationJob(IndexedClassExpression root, IndexedObjectProperty subRoot, J initiatorJob) {
		super(root);
		subRoot_ = subRoot;
		initiatorJob_ = initiatorJob;
	}
	
	IndexedObjectProperty getSubRoot() {
		return subRoot_;
	}
	
	J getInitiatorJob() {
		return initiatorJob_;
	}
}
