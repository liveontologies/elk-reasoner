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

import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.DecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.reasoner.saturation.context.Context;

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
public class IfThenElseConclusionVisitor<R> implements ConclusionVisitor<R> {

	final private ConclusionVisitor<Boolean> check_;

	final private ConclusionVisitor<R> doTrue_;

	final private ConclusionVisitor<R> doFalse_;

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
	public IfThenElseConclusionVisitor(ConclusionVisitor<Boolean> check,
			ConclusionVisitor<R> doTrue, ConclusionVisitor<R> doFalse) {
		this.check_ = check;
		this.doTrue_ = doTrue;
		this.doFalse_ = doFalse;
	}

	@Override
	public R visit(BackwardLink conclusion, Context context) {
		return check_.visit(conclusion, context) ? doTrue_.visit(conclusion,
				context) : doFalse_.visit(conclusion, context);
	}

	@Override
	public R visit(ComposedSubsumer conclusion, Context context) {
		return check_.visit(conclusion, context) ? doTrue_.visit(conclusion,
				context) : doFalse_.visit(conclusion, context);
	}

	@Override
	public R visit(Contradiction conclusion, Context context) {
		return check_.visit(conclusion, context) ? doTrue_.visit(conclusion,
				context) : doFalse_.visit(conclusion, context);
	}

	@Override
	public R visit(DecomposedSubsumer conclusion, Context context) {
		return check_.visit(conclusion, context) ? doTrue_.visit(conclusion,
				context) : doFalse_.visit(conclusion, context);
	}

	@Override
	public R visit(DisjointSubsumer conclusion, Context context) {
		return check_.visit(conclusion, context) ? doTrue_.visit(conclusion,
				context) : doFalse_.visit(conclusion, context);
	}

	@Override
	public R visit(ForwardLink conclusion, Context context) {
		return check_.visit(conclusion, context) ? doTrue_.visit(conclusion,
				context) : doFalse_.visit(conclusion, context);
	}

	@Override
	public R visit(Propagation conclusion, Context context) {
		return check_.visit(conclusion, context) ? doTrue_.visit(conclusion,
				context) : doFalse_.visit(conclusion, context);
	}

}
