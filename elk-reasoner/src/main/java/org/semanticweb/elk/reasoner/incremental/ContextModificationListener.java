/**
 * 
 */
package org.semanticweb.elk.reasoner.incremental;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturationListener;
import org.semanticweb.elk.reasoner.saturation.SaturationJob;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ContextModificationListener implements
		ClassExpressionSaturationListener<SaturationJob<IndexedClassExpression>> {

	private final Queue<IndexedClassExpression> modified_ = new ConcurrentLinkedQueue<IndexedClassExpression>();
	
	@Override
	public void notifyFinished(SaturationJob<IndexedClassExpression> job) throws InterruptedException {
		modified_.add(job.getInput());
	}
	
	public void reset() {
		modified_.clear();
	}
	
	public Collection<IndexedClassExpression> getModifiedClassExpressions() {
		return modified_;
	}
}
