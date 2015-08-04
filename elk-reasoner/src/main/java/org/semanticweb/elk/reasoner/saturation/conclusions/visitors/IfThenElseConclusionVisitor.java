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
 * A {@link ConclusionVisitor} that implements an if-then-else statement over
 * the three given {@link ConclusionVisitor}s. If the visit method of the first
 * (Boolean) {@link ConclusionVisitor} returns {@code true} for the given
 * {@link Conclusion}, the second {@link ConclusionVisitor} is called on this
 * {@link Conclusion}, and otherwise the third {@link ConclusionVisitor} is
 * called on this {@link Conclusion}.
 * 
 * @author "Yevgeny Kazakov"
 */
public class IfThenElseConclusionVisitor<I, O> implements
		ConclusionVisitor<I, O> {

	final private ConclusionVisitor<? super I, Boolean> check_;

	final private ConclusionVisitor<? super I, O> doFalse_;

	final private ConclusionVisitor<? super I, O> doTrue_;

	/**
	 * Creates a new {@link ConclusionVisitor} that implements an if-then-else
	 * statement over the three given {@link ConclusionVisitor}s. If the visit
	 * method of the first (Boolean) {@link ConclusionVisitor} returns
	 * {@code true} for the given {@link Conclusion}, the second
	 * {@link ConclusionVisitor} is called on this {@link Conclusion}, and
	 * otherwise the third {@link ConclusionVisitor} is called on this
	 * {@link Conclusion}.
	 * 
	 * @param check
	 *            The {@link ConclusionVisitor} that is used to evaluated a
	 *            condition
	 * @param doTrue
	 *            The {@link ConclusionVisitor} that is called when the
	 *            condition is evaluated {@code true}
	 * @param doFalse
	 *            The {@link ConclusionVisitor} that is called when the
	 *            condition is evaluated {@code false}
	 */
	public IfThenElseConclusionVisitor(
			ConclusionVisitor<? super I, Boolean> check,
			ConclusionVisitor<? super I, O> doTrue,
			ConclusionVisitor<? super I, O> doFalse) {
		this.check_ = check;
		this.doTrue_ = doTrue;
		this.doFalse_ = doFalse;
	}

	@Override
	public O visit(BackwardLink subConclusion, I input) {
		return check_.visit(subConclusion, input) ? doTrue_.visit(
				subConclusion, input) : doFalse_.visit(subConclusion, input);
	}

	@Override
	public O visit(ComposedSubsumer conclusion, I input) {
		return check_.visit(conclusion, input) ? doTrue_.visit(conclusion,
				input) : doFalse_.visit(conclusion, input);
	}

	@Override
	public O visit(ContextInitialization conclusion, I input) {
		return check_.visit(conclusion, input) ? doTrue_.visit(conclusion,
				input) : doFalse_.visit(conclusion, input);
	}

	@Override
	public O visit(Contradiction conclusion, I input) {
		return check_.visit(conclusion, input) ? doTrue_.visit(conclusion,
				input) : doFalse_.visit(conclusion, input);
	}

	@Override
	public O visit(DecomposedSubsumer conclusion, I input) {
		return check_.visit(conclusion, input) ? doTrue_.visit(conclusion,
				input) : doFalse_.visit(conclusion, input);
	}

	@Override
	public O visit(DisjointSubsumer conclusion, I input) {
		return check_.visit(conclusion, input) ? doTrue_.visit(conclusion,
				input) : doFalse_.visit(conclusion, input);
	}

	@Override
	public O visit(ForwardLink conclusion, I input) {
		return check_.visit(conclusion, input) ? doTrue_.visit(conclusion,
				input) : doFalse_.visit(conclusion, input);
	}

	@Override
	public O visit(Propagation subConclusion, I input) {
		return check_.visit(subConclusion, input) ? doTrue_.visit(
				subConclusion, input) : doFalse_.visit(subConclusion, input);
	}

	@Override
	public O visit(SubContextInitialization subConclusion, I input) {
		return check_.visit(subConclusion, input) ? doTrue_.visit(
				subConclusion, input) : doFalse_.visit(subConclusion, input);
	}

}
