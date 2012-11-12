/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.PositiveSuperClassExpression;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.context.ContextImpl;
import org.semanticweb.elk.reasoner.saturation.rules.ContextRules;
import org.semanticweb.elk.util.collections.chains.ChainImpl;
import org.semanticweb.elk.util.collections.chains.Matcher;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class SaturationState {

	// logger for this class
	private static final Logger LOGGER_ = Logger.getLogger(SaturationState.class);
	
	private final OntologyIndex ontologyIndex_;
	
	/**
	 * Cached constants
	 */
	private final IndexedClassExpression owlThing_, owlNothing_;

	/**
	 * The queue containing all activated contexts. Every activated context
	 * occurs exactly once.
	 */
	private final Queue<Context> activeContexts_ = new ConcurrentLinkedQueue<Context>();
	
	private ContextCreationListenerChain contextCreationListeners_ = null;//new ContextCreationListenerChain2();
	
	private Queue<IndexedClassExpression> modifiedContexts_ = new ConcurrentLinkedQueue<IndexedClassExpression>();
	
	
	public SaturationState(OntologyIndex index) {
		ontologyIndex_ = index;
		owlThing_ = index.getIndexedOwlThing();
		owlNothing_ = index.getIndexedOwlNothing();
	}	
	
	public void markAsModified(Context context) {
		modifiedContexts_.add(context.getRoot());
	}
	
	public Collection<IndexedClassExpression> getModifiedContexts() {
		return modifiedContexts_ == null ? Collections.<IndexedClassExpression>emptyList() : modifiedContexts_;
	}
	
	public void clearModifiedContexts() {
		modifiedContexts_.clear();
	}
	
	public Context pollForContext() {
		return activeContexts_.poll();
	}
	
	public IndexedClassExpression getOwlThing() {
		return owlThing_;
	}
	
	public IndexedClassExpression getOwlNothing() {
		return owlNothing_;
	}
	
	public void produce(Context context, Conclusion item) {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace(context.getRoot() + ": new conclusion " + item);
		if (context.addToDo(item)) {
			// context was activated
			activeContexts_.add(context);
			//LOGGER_.trace(context.getRoot() + " was activated!");	
		}
	}
	
	public Context getCreateContext(IndexedClassExpression root) {
		if (root.getContext() == null) {
			Context context = new ContextImpl(root);
			if (root.setContext(context)) {
				initContext(context);
				contextCreationListeners_.notifyAll(context);
			}
		}
		return root.getContext();
	}	
	
	public void initContext(Context context) {
		produce(context, new PositiveSuperClassExpression(context.getRoot()));
		//apply all context initialization rules
		ContextRules initRules = ontologyIndex_.getContextInitRules();
		
		while (initRules != null) {
			initRules.apply(this, context);
			initRules = initRules.next();
		}
	}
	
	public void registerContextCreationListener(ContextCreationListener listener) {
		if (contextCreationListeners_ == null) {
			contextCreationListeners_ = new ContextCreationListenerChain(listener, null);
		}
		else {
			contextCreationListeners_.register(listener);
		}
	}
	
	public void deregisterContextCreationListener(ContextCreationListener listener) {
		contextCreationListeners_.deregister(listener);
	}
	
	int size() {
		return activeContexts_.size();
	}
	
	Iterator<Context> getActiveContextIterator() {
		return activeContexts_.iterator();
	}
	
	/**
	 * A tiny chain of listeners supporting register/deregister operations
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	
	private static class ContextCreationListenerChain extends ChainImpl<ContextCreationListenerChain> {
		
		/*
		 * The matcher used to find existing listeners
		 */
		private static class ListenerMatcher implements Matcher<ContextCreationListenerChain, ContextCreationListenerChain> {

			private final ContextCreationListener toMatch_;
			
			ListenerMatcher(ContextCreationListener listener) {
				toMatch_ = listener;
			}
			
			@Override
			public ContextCreationListenerChain match(ContextCreationListenerChain candidate) {
				return (candidate.listener_ == toMatch_) ? candidate : null;
			}
			
		};
		
		private final ContextCreationListener listener_;
		
		ContextCreationListenerChain(final ContextCreationListener listener, final ContextCreationListenerChain tail) {
			super(tail);
			listener_ = listener;
		}
		
		void register(final ContextCreationListener listener) {
			if (find(new ListenerMatcher(listener)) == null) {
				setNext(new ContextCreationListenerChain(listener, next()));
			}
		}
		
		void deregister(final ContextCreationListener listener) {
			remove(new ListenerMatcher(listener));
		}
		
		/**
		 * Notify all registered listeners that a context has been created
		 */
		void notifyAll(final Context newContext) {
			ContextCreationListenerChain head = next();
			
			while (head != null) {
				head.listener_.notifyContextCreation(newContext);
				head = head.next();
			}
		}
	}
}