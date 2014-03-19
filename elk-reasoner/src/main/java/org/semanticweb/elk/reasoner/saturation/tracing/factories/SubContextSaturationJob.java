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
class SubContextSaturationJob extends SaturationJob<IndexedClassExpression> {

	private final IndexedObjectProperty subRoot_;
	
	SubContextSaturationJob(IndexedClassExpression root, IndexedObjectProperty subRoot) {
		super(root);
		subRoot_ = subRoot;
	}
	
	IndexedObjectProperty getSubRoot() {
		return subRoot_;
	}
}
