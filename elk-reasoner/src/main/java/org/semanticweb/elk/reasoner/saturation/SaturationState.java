/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.Collection;
import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.PositiveSubsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.context.ContextImpl;
import org.semanticweb.elk.reasoner.saturation.rules.BasicCompositionRuleApplicationVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.LinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationVisitor;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class SaturationState {

	// logger for this class
	private static final Logger LOGGER_ = Logger
			.getLogger(SaturationState.class);

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

	/**
	 * The queue of all contexts for which computation of the closure under
	 * inference rules has not yet been finished.
	 */
	private Queue<IndexedClassExpression> notSaturatedContexts_ = new ConcurrentLinkedQueue<IndexedClassExpression>();

	public Collection<IndexedClassExpression> getNotSaturatedContexts() {
		return notSaturatedContexts_ == null ? Collections
				.<IndexedClassExpression> emptyList() : notSaturatedContexts_;
	}

	private static final ContextCreationListener DUMMY_LISTENER = new ContextCreationListener() {
		@Override
		public void notifyContextCreation(Context newContext) {
			// Dummy listener, which does not do anything
		}
	};

	private static final RuleApplicationVisitor DEFAULT_INIT_RULE_APP_VISITOR = new BasicCompositionRuleApplicationVisitor();
	
	private final Writer defaultWriter_ = new Writer(DUMMY_LISTENER, DEFAULT_INIT_RULE_APP_VISITOR);

	private final Writer defaultSaturationCheckingWriter_ = new SaturationCheckingWriter(
			DUMMY_LISTENER, DEFAULT_INIT_RULE_APP_VISITOR);

	public SaturationState(OntologyIndex index) {
		ontologyIndex_ = index;
		owlThing_ = index.getIndexedOwlThing();
		owlNothing_ = index.getIndexedOwlNothing();
	}

	/**
	 * @return an {@link Writer} for modifying this {@link SaturationState}. The
	 *         methods of this {@link Writer} are thread safe
	 */
	public Writer getWriter() {
		return defaultWriter_;
	}

	public Writer getSaturationCheckingWriter() {
		return defaultSaturationCheckingWriter_;
	}

	/**
	 * Creates a new {@link Writer} for modifying this {@link SaturationState}
	 * associated with the given {@link ContextCreationListener}. If
	 * {@link ContextCreationListener} is not thread safe, the calls of the
	 * methods for the same {@link Writer} should be synchronized
	 * 
	 * @param contextCreationListener
	 *            {@link ContextCreationListener} to be used for this
	 *            {@link Writer}
	 * @return a new {@link Writer} associated with the given
	 *         {@link ContextCreationListener}
	 */
	public Writer getWriter(ContextCreationListener contextCreationListener, RuleApplicationVisitor ruleAppVisitor) {
		return new Writer(contextCreationListener, ruleAppVisitor);
	}

	public Writer getSaturationCheckingWriter(
			ContextCreationListener contextCreationListener, RuleApplicationVisitor ruleAppVisitor) {
		return new SaturationCheckingWriter(contextCreationListener, ruleAppVisitor);
	}

	/**
	 * Functions that can write the saturation state are grouped here. With
	 * every {@link Writer} one can register a {@link ContextCreationListener}
	 * that will be executed every time this {@link Writer} creates a new
	 * {@code Context}. Although all functions of this {@link Writer} are thread
	 * safe, the function of the {@link ContextCreationListener} might not be,
	 * in which the access of functions of {@link Writer} should be synchronized
	 * between threads.
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	public class Writer {

		private final ContextCreationListener contextCreationListener_;
		
		private final RuleApplicationVisitor initRuleAppVisitor_;

		private Writer(ContextCreationListener contextCreationListener, RuleApplicationVisitor ruleAppVisitor) {
			this.contextCreationListener_ = contextCreationListener;
			this.initRuleAppVisitor_ = ruleAppVisitor;
		}

		public IndexedClassExpression getOwlThing() {
			return owlThing_;
		}

		public IndexedClassExpression getOwlNothing() {
			return owlNothing_;
		}

		public Context pollForContext() {
			return activeContexts_.poll();
		}

		public void produce(Context context, Conclusion conclusion) {
			if (LOGGER_.isTraceEnabled())
				LOGGER_.trace(context + ": new conclusion " + conclusion);
			if (context.addToDo(conclusion)) {

				// debugCheck(context, item);

				// context was activated
				activeContexts_.add(context);
				// LOGGER_.trace(context.getRoot() + " was activated!");
			}
		}

		public Context getCreateContext(IndexedClassExpression root) {
			if (root.getContext() == null) {
				Context context = new ContextImpl(root);
				if (root.setContext(context)) {
					initContext(context);
					contextCreationListener_.notifyContextCreation(context);
				}
			}
			return root.getContext();
		}

		public void initContext(Context context) {
			produce(context,
					new PositiveSubsumer(context.getRoot()));
			// apply all context initialization rules
			LinkRule<Context> initRule = ontologyIndex_
					.getContextInitRuleHead();
			while (initRule != null) {
				initRule.accept(initRuleAppVisitor_, this, context);
				//initRule.apply(this, context);
				initRule = initRule.next();
			}
		}

		public void markAsNotSaturated(Context context) {
			if (context.setSaturated(false))
				notSaturatedContexts_.add(context.getRoot());
		}

		public void clearNotSaturatedContexts() {
			notSaturatedContexts_.clear();
		}

		/*
		 * private boolean debugCheck(Context context, Conclusion conclusion) {
		 * if (context.getRoot() instanceof IndexedClass) { ElkClass cl =
		 * ((IndexedClass) context.getRoot()).getElkClass();
		 * 
		 * if (cl.getIri().toString().equals(
		 * "<http://www.co-ode.org/ontologies/galen#CoracohumeralLigament>")) {
		 * 
		 * System.out.println("!!!PRODUCED!!! " + conclusion + " saturated? " +
		 * context.isSaturated());
		 * 
		 * return true; } }
		 * 
		 * return false; }
		 */
	}

	/**
	 * A {@link Writer} that does not produce conclusions if their source
	 * context is already saturated.
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	private class SaturationCheckingWriter extends Writer {
		private SaturationCheckingWriter(
				ContextCreationListener contextCreationListener, RuleApplicationVisitor ruleAppVisitor) {
			super(contextCreationListener, ruleAppVisitor);
		}

		@Override
		public void produce(Context context, Conclusion conclusion) {
			if (conclusion.getSourceContext(context).isSaturated())
				return;
			super.produce(context, conclusion);
		}
	}

}
