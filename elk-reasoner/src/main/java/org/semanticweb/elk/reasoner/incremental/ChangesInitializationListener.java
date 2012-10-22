package org.semanticweb.elk.reasoner.incremental;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorListenerNotifyFinishedJob;

/**
 * Used to track modified contexts
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ChangesInitializationListener<J extends IndexedClassExpression>
		implements InputProcessorListenerNotifyFinishedJob<J> {

	private final Queue<J> modified_ = new ConcurrentLinkedQueue<J>();
	
	@Override
	public void notifyFinished(J ice) throws InterruptedException {
		modified_.add(ice);
	}
	
	public void reset() {
		modified_.clear();
	}
	
	public Collection<J> getModifiedClassExpressions() {
		return modified_;
	}
}
