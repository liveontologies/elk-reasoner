package org.semanticweb.elk.reasoner.saturation.conclusions.visitors;

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

import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.DecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.SubContextInitialization;

/**
 * A {@link ConclusionVisitor} that composes several given
 * {@link ConclusionVisitor}s. The visit method of the composed visitor returns
 * calls the original {@link ConclusionVisitor}s in the specified order and
 * returns {@link true} for the {@link Conclusion} if and only all of the
 * {@link ConclusionVisitor}s return {@code true}. The result is evaluated
 * lazily, i.e., if some {@link ConclusionVisitor} returns {@code false}, the
 * subsequent {@link ConclusionVisitor}s are not called.
 * 
 * @author "Yevgeny Kazakov"
 */
public class ComposedConclusionVisitor<I> implements
		ConclusionVisitor<I, Boolean> {

	/**
	 * The original {@link ConclusionVisitor}s to be called in the specified
	 * order
	 */
	final private ConclusionVisitor<? super I, Boolean>[] visitors_;

	/**
	 * Creates a new {@link ConclusionVisitor} that combines several given
	 * {@link ConclusionVisitor}s. The visit method of the combined visitor
	 * returns calls the original {@link ConclusionVisitor}s in the specified
	 * order and returns {@link true} for the {@link Conclusion} if and only all
	 * of the {@link ConclusionVisitor}s return {@code true}. The result is
	 * evaluated lazily, i.e., if some {@link ConclusionVisitor} returns
	 * {@code false}, the subsequent {@link ConclusionVisitor}s are not called.
	 * 
	 * @param visitors
	 *            the {@link ConclusionVisitor} to be composed
	 */
	public ComposedConclusionVisitor(
			ConclusionVisitor<? super I, Boolean>... visitors) {
		this.visitors_ = visitors;
	}

	@Override
	public Boolean visit(BackwardLink subConclusion, I input) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(subConclusion, input))
				return false;
		}
		return true;
	}

	@Override
	public Boolean visit(ComposedSubsumer<?> conclusion, I input) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(conclusion, input))
				return false;
		}
		return true;
	}

	@Override
	public Boolean visit(ContextInitialization conclusion, I input) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(conclusion, input))
				return false;
		}
		return true;
	}

	@Override
	public Boolean visit(Contradiction conclusion, I input) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(conclusion, input))
				return false;
		}
		return true;
	}

	@Override
	public Boolean visit(DecomposedSubsumer<?> conclusion, I input) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(conclusion, input))
				return false;
		}
		return true;
	}

	@Override
	public Boolean visit(DisjointSubsumer conclusion, I input) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(conclusion, input))
				return false;
		}
		return true;
	}

	@Override
	public Boolean visit(ForwardLink conclusion, I input) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(conclusion, input))
				return false;
		}
		return true;
	}

	@Override
	public Boolean visit(Propagation subConclusion, I input) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(subConclusion, input))
				return false;
		}
		return true;
	}

	@Override
	public Boolean visit(SubContextInitialization subConclusion, I input) {
		for (int i = 0; i < visitors_.length; i++) {
			if (!visitors_[i].visit(subConclusion, input))
				return false;
		}
		return true;
	}

}
