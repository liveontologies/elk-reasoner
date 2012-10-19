/**
 * 
 */
package org.semanticweb.elk.reasoner.stages;

import java.util.Collection;

import org.semanticweb.elk.reasoner.indexing.hierarchy.DifferentialIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectCache;

/**
 * Stores all data structures, e.g., the differential index, to
 * maintain the state of the reasoner in the incremental mode.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class IncrementalReasonerState {

	final DifferentialIndex diffIndex_;
	
	Collection<IndexedClassExpression> classesToProcess_; 
	
	IncrementalReasonerState(IndexedObjectCache objectCache, IndexedClass owlNothing) {
		diffIndex_ = new DifferentialIndex(objectCache, owlNothing);
	}
	
	
}
