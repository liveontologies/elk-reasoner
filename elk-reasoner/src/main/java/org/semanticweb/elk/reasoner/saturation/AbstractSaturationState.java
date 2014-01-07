package org.semanticweb.elk.reasoner.saturation;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.BasicRuleVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A skeleton to simplify implementation of {@link SaturationState}s
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public abstract class AbstractSaturationState implements SaturationState {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(AbstractSaturationState.class);

	final OntologyIndex ontologyIndex;

	private static final RuleVisitor DEFAULT_INIT_RULE_APP_VISITOR = new BasicRuleVisitor();

	/**
	 * Cached constants
	 */
	// private final IndexedClassExpression owlThing_, owlNothing_;

	/**
	 * The queue containing all activated contexts. Every activated context
	 * occurs exactly once.
	 */
	private final Queue<Context> activeContexts_ = new ConcurrentLinkedQueue<Context>();

	/**
	 * The queue of all contexts for which computation of the closure under
	 * inference rules has not yet been finished.
	 */
	private final Queue<IndexedClassExpression> notSaturatedContexts_ = new ConcurrentLinkedQueue<IndexedClassExpression>();

	public AbstractSaturationState(OntologyIndex index) {
		ontologyIndex = index;
		// owlThing_ = index.getIndexedOwlThing();
		// owlNothing_ = index.getIndexedOwlNothing();
	}

	@Override
	public OntologyIndex getOntologyIndex() {
		return ontologyIndex;
	}

	@Override
	public Collection<IndexedClassExpression> getNotSaturatedContexts() {
		return notSaturatedContexts_;
	}

	@Override
	public ExtendedSaturationStateWriter getExtendedWriter(
			ContextCreationListener contextCreationListener,
			ContextModificationListener contextModificationListener,
			RuleVisitor ruleAppVisitor, ConclusionVisitor<?> conclusionVisitor,
			boolean trackNewContextsAsUnsaturated) {
		return new ExtendedWriter(contextCreationListener,
				contextModificationListener, ruleAppVisitor, conclusionVisitor,
				trackNewContextsAsUnsaturated);
	}

	@Override
	public SaturationStateWriter getWriter(
			ContextModificationListener contextModificationListener,
			ConclusionVisitor<?> conclusionVisitor) {
		return new BasicWriter(contextModificationListener, conclusionVisitor);
	}

	@Override
	public SaturationStateWriter getWriter(
			ConclusionVisitor<?> conclusionVisitor) {
		return getDefaultWriter(conclusionVisitor);
	}

	@Override
	public ExtendedSaturationStateWriter getExtendedWriter(
			ConclusionVisitor<?> conclusionVisitor) {
		return getDefaultWriter(conclusionVisitor);
	}

	abstract void resetContexts();

	/**
	 * Adds the given {@link Context} to this {@link SaturationState} if no
	 * {@link Context} was assigned for its root
	 * 
	 * @param context
	 *            the {@link Context} to be added to this
	 *            {@link SaturationState}
	 * @return the {@link Context} in this {@link SaturationState} assigned to
	 *         the same root, or {@link null} if no such {@link Context} existed
	 *         before this method is called
	 * 
	 * @see Context#getRoot()
	 */
	abstract Context setIfAbsent(Context context);

	private ExtendedSaturationStateWriter getDefaultWriter(
			final ConclusionVisitor<?> conclusionVisitor) {
		return new ExtendedWriter(ContextCreationListener.DUMMY,
				ContextModificationListener.DUMMY,
				DEFAULT_INIT_RULE_APP_VISITOR, conclusionVisitor, true);
	}

	/**
	 * 
	 * 
	 */
	class BasicWriter implements SaturationStateWriter {

		private final ConclusionVisitor<?> producedConclusionVisitor_;

		private final ContextModificationListener contextModificationListener_;

		private BasicWriter(
				ContextModificationListener contextSaturationListener,
				ConclusionVisitor<?> conclusionVisitor) {
			this.contextModificationListener_ = contextSaturationListener;
			this.producedConclusionVisitor_ = conclusionVisitor;
		}

		// @Override
		// public IndexedClassExpression getOwlThing() {
		// return owlThing_;
		// }
		//
		// @Override
		// public IndexedClassExpression getOwlNothing() {
		// return owlNothing_;
		// }

		@Override
		public Context pollForActiveContext() {
			Context result = activeContexts_.poll();
			LOGGER_.trace("pollForActiveContext() : {}", result);
			return result;
		}

		@Override
		public void produce(Context context, Conclusion conclusion) {
			LOGGER_.trace("{}: produced conclusion {}", context, conclusion);
			// this may be necessary, e.g., for counting produced conclusions
			conclusion.accept(producedConclusionVisitor_, context);

			if (context.addToDo(conclusion)) {
				LOGGER_.trace("{}: activated", context);
				// context was activated
				activeContexts_.add(context);
			}
		}

		@Override
		public void produce(IndexedClassExpression root, Conclusion conclusion) {
			// TODO: what if NPE?
			produce(getContext(root), conclusion);
		}

		protected void markAsNotSaturatedInternal(Context context) {
			LOGGER_.trace("{}: marked as non-saturated", context);
			notSaturatedContexts_.add(context.getRoot());
			contextModificationListener_.notifyContextModification(context);
		}

		@Override
		public boolean markAsNotSaturated(Context context) {
			if (context.setSaturated(false)) {
				markAsNotSaturatedInternal(context);

				return true;
			}
			return false;
		}

		@Override
		public void clearNotSaturatedContexts() {
			LOGGER_.trace("Clear non-saturated contexts");
			notSaturatedContexts_.clear();
		}

		@Override
		public void resetContexts() {
			AbstractSaturationState.this.resetContexts();
		}
	}

	/**
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	protected class ExtendedWriter extends BasicWriter implements
			ExtendedSaturationStateWriter {

		private final RuleVisitor initRuleAppVisitor_;

		/**
		 * If set to true, the writer will put all newly created contexts to
		 * {@link notSaturatedContexts_} This is important if saturation of a
		 * new context has been interrupted for some reason, e.g. inconsistency.
		 * Then we'll need to store all such contexts to be able to complete
		 * their saturation later.
		 */
		private final boolean trackNewContextsAsUnsaturated_;

		private final ContextCreationListener contextCreationListener_;

		protected ExtendedWriter(
				ContextCreationListener contextCreationListener,
				ContextModificationListener contextModificationListener,
				RuleVisitor ruleAppVisitor,
				ConclusionVisitor<?> conclusionVisitor,
				boolean trackNewContextsAsUnsaturated) {
			super(contextModificationListener, conclusionVisitor);

			this.contextCreationListener_ = contextCreationListener;
			this.initRuleAppVisitor_ = ruleAppVisitor;
			this.trackNewContextsAsUnsaturated_ = trackNewContextsAsUnsaturated;
		}

		@Override
		public void produce(IndexedClassExpression root, Conclusion conclusion) {
			produce(getCreateContext(root), conclusion);
		}

		@Override
		public void initContext(Context context) {
			SaturationUtils.initContext(ontologyIndex, initRuleAppVisitor_,
					context, this);
		}

		@Override
		public void removeContext(Context context) {
			// TODO Auto-generated method stub
		}

		@Override
		public Context getCreateContext(IndexedClassExpression root) {
			Context context = root.getContext();
			if (context != null)
				return context;
			// else try to assign a new context
			ContextImpl newContext = new ContextImpl(root);
			Context previous = setIfAbsent(newContext);
			if (previous != null)
				// the context is already assigned meanwhile
				return previous;
			// else the context is new
			initContext(newContext);
			contextCreationListener_.notifyContextCreation(newContext);

			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace(newContext.getRoot() + ": context created");
			}

			if (trackNewContextsAsUnsaturated_) {
				markAsNotSaturatedInternal(newContext);
			}
			return newContext;
		}

	}

}
