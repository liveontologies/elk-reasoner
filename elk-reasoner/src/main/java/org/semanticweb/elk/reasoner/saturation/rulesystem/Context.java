package org.semanticweb.elk.reasoner.saturation.rulesystem;

import java.util.Queue;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;


public interface Context {

	
	/**
	 * Get the root expression of that context.
	 * 
	 * @return
	 */
	public IndexedClassExpression getRoot();
	
	/**
	 * Get the current queue of items that still need to be processed for this
	 * context.
	 * 
	 * @return queue
	 */
	public Queue<Queueable<?>> getQueue();
	
	/**
	 * Ensure that the context is active, and return true if the activation
	 * state has been changed from false to true. This method is thread safe:
	 * for two concurrent executions only one succeeds.
	 * 
	 * @return true if the context was not active; returns false otherwise
	 */
	public boolean tryActivate();
	
	/**
	 * Ensure that the context is not active, and return true if the activation
	 * state has been changed from true to false. This method is thread safe:
	 * for two concurrent executions only one succeeds.
	 * 
	 * @return true if the context was active; returns false otherwise
	 */
	public boolean tryDeactivate();
}
