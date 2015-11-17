package org.semanticweb.elk.reasoner.saturation.conclusions.classes;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionDecomposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubContextInitialization;

/**
 * A {@link ClassConclusion.Visitor} that implements an if-then-else statement over
 * the three given {@link ClassConclusion.Visitor}s. If the visit method of the first
 * (Boolean) {@link ClassConclusion.Visitor} returns {@code true} for the given
 * {@link ClassConclusion}, the second {@link ClassConclusion.Visitor} is called on this
 * {@link ClassConclusion}, and otherwise the third {@link ClassConclusion.Visitor} is
 * called on this {@link ClassConclusion}.
 * 
 * @author "Yevgeny Kazakov"
 */
public class IfThenElseClassConclusionVisitor<O> implements
		ClassConclusion.Visitor<O> {

	final private ClassConclusion.Visitor<Boolean> check_;

	final private ClassConclusion.Visitor<O> doFalse_;

	final private ClassConclusion.Visitor<O> doTrue_;

	/**
	 * Creates a new {@link ClassConclusion.Visitor} that implements an if-then-else
	 * statement over the three given {@link ClassConclusion.Visitor}s. If the visit
	 * method of the first (Boolean) {@link ClassConclusion.Visitor} returns
	 * {@code true} for the given {@link ClassConclusion}, the second
	 * {@link ClassConclusion.Visitor} is called on this {@link ClassConclusion}, and
	 * otherwise the third {@link ClassConclusion.Visitor} is called on this
	 * {@link ClassConclusion}.
	 * 
	 * @param check
	 *            The {@link ClassConclusion.Visitor} that is used to evaluated a
	 *            condition
	 * @param doTrue
	 *            The {@link ClassConclusion.Visitor} that is called when the
	 *            condition is evaluated {@code true}
	 * @param doFalse
	 *            The {@link ClassConclusion.Visitor} that is called when the
	 *            condition is evaluated {@code false}
	 */
	public IfThenElseClassConclusionVisitor(
			ClassConclusion.Visitor<Boolean> check,
			ClassConclusion.Visitor<O> doTrue,
			ClassConclusion.Visitor<O> doFalse) {
		this.check_ = check;
		this.doTrue_ = doTrue;
		this.doFalse_ = doFalse;
	}

	@Override
	public O visit(BackwardLink subConclusion) {
		return check_.visit(subConclusion) ? doTrue_.visit(
				subConclusion) : doFalse_.visit(subConclusion);
	}

	@Override
	public O visit(SubClassInclusionComposed conclusion) {
		return check_.visit(conclusion)
				? doTrue_.visit(conclusion)
				: doFalse_.visit(conclusion);
	}

	@Override
	public O visit(ContextInitialization conclusion) {
		return check_.visit(conclusion)
				? doTrue_.visit(conclusion)
				: doFalse_.visit(conclusion);
	}

	@Override
	public O visit(Contradiction conclusion) {
		return check_.visit(conclusion)
				? doTrue_.visit(conclusion)
				: doFalse_.visit(conclusion);
	}

	@Override
	public O visit(SubClassInclusionDecomposed conclusion) {
		return check_.visit(conclusion)
				? doTrue_.visit(conclusion)
				: doFalse_.visit(conclusion);
	}

	@Override
	public O visit(DisjointSubsumer conclusion) {
		return check_.visit(conclusion)
				? doTrue_.visit(conclusion)
				: doFalse_.visit(conclusion);
	}

	@Override
	public O visit(ForwardLink conclusion) {
		return check_.visit(conclusion)
				? doTrue_.visit(conclusion)
				: doFalse_.visit(conclusion);
	}

	@Override
	public O visit(Propagation subConclusion) {
		return check_.visit(subConclusion)
				? doTrue_.visit(subConclusion)
				: doFalse_.visit(subConclusion);
	}

	@Override
	public O visit(SubContextInitialization subConclusion) {
		return check_.visit(subConclusion)
				? doTrue_.visit(subConclusion)
				: doFalse_.visit(subConclusion);
	}

}
