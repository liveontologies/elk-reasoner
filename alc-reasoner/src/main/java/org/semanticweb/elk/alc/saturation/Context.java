package org.semanticweb.elk.alc.saturation;
/*
 * #%L
 * ALC Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Set;

import org.semanticweb.elk.alc.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.alc.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.DecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Subsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.collections.HashSetMultimap;
import org.semanticweb.elk.util.collections.Multimap;

/**
 * The elementary component of the saturation.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class Context {

	private final static ConclusionVisitor<Context, Boolean> CONCLUSION_INSERTER_ = new ConclusionInserter();

	/**
	 * the {@link Root} from which the information in this {@link Context} was
	 * derived
	 */
	private final Root root_;

	/**
	 * {@code true} if this {@link Context} is initialized
	 */
	private boolean isInitialized_ = false;

	/**
	 * the common super-classes of the {@link Root}
	 */
	private final Set<IndexedClassExpression> subsumers_;

	/**
	 * the existential relations from other {@link Root}s
	 */
	private final Multimap<IndexedObjectProperty, Root> backwardLinks_;

	/**
	 * propagations for the keys of {@link #backwardLinks_}
	 */
	private final Multimap<IndexedObjectProperty, IndexedClassExpression> propagations_;

	private Queue<Conclusion> toDo_;

	Context(Root root) {
		this.root_ = root;
		this.subsumers_ = new ArrayHashSet<IndexedClassExpression>(64);
		this.backwardLinks_ = new HashSetMultimap<IndexedObjectProperty, Root>(
				16);
		this.propagations_ = new HashSetMultimap<IndexedObjectProperty, IndexedClassExpression>(
				16);
	}

	public Root getRoot() {
		return root_;
	}

	public Set<IndexedClassExpression> getSubsumers() {
		return subsumers_;
	}

	public Multimap<IndexedObjectProperty, Root> getBackwardLinks() {
		return backwardLinks_;
	}

	public Multimap<IndexedObjectProperty, IndexedClassExpression> getPropagations() {
		return propagations_;
	}

	/**
	 * Adds the given {@link Conclusion} to this {@link Context}
	 * 
	 * @param conclusion
	 * @return {@code true} if this {@link Context} has changed as a result of
	 *         this operation
	 */
	public boolean addConclusion(Conclusion conclusion) {
		return conclusion.accept(CONCLUSION_INSERTER_, this);
	}

	/**
	 * Add the given {@link Conclusion} to be processed within this
	 * {@link Context}.
	 * 
	 * @param conclusion
	 * @return {@code true} if this {@link Context} did not have any unprocessed
	 *         {@link Conclusion}s and {@code false} otherwise
	 */
	public boolean addToDo(Conclusion conclusion) {
		boolean result = false;
		if (toDo_ == null) {
			toDo_ = new ArrayDeque<Conclusion>();
			result = true;
		}
		toDo_.add(conclusion);
		return result;
	}

	/**
	 * @return the next unprocessed {@link Conclusion} within this
	 *         {@link Context} or {@code null} if there is no such unprocessed
	 *         {@link Conclusion}.
	 */
	public Conclusion takeToDo() {
		if (toDo_ == null)
			return null;
		// else
		Conclusion result = toDo_.poll();
		if (result == null) {
			toDo_ = null;
		}
		return result;
	}

	static class ConclusionInserter implements
			ConclusionVisitor<Context, Boolean> {

		public static boolean visit(Subsumer conclusion, Context input) {
			return input.subsumers_.add(conclusion.getExpression());
		}

		@Override
		public Boolean visit(DecomposedSubsumer conclusion, Context input) {
			return visit((Subsumer) conclusion, input);
		}

		@Override
		public Boolean visit(ComposedSubsumer conclusion, Context input) {
			return visit((Subsumer) conclusion, input);
		}

		@Override
		public Boolean visit(BackwardLink conclusion, Context input) {
			return input.backwardLinks_.add(conclusion.getRelation(),
					conclusion.getSource());
		}

		@Override
		public Boolean visit(ContextInitialization conclusion, Context input) {
			if (input.isInitialized_)
				return false;
			// else
			input.isInitialized_ = true;
			return true;
		}

		@Override
		public Boolean visit(Propagation conclusion, Context input) {
			return input.propagations_.add(conclusion.getRelation(),
					conclusion.getCarry());
		}

	}
}
