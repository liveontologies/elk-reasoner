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
 * A {@link ConclusionVisitor} that combines two given {@link ConclusionVisitor}
 * s. The visit method of the combined visitor returns {@link true} for the
 * {@link Conclusion} if and only if both of these {@link ConclusionVisitor}s
 * return {@code true}. The combined {@link ConclusionVisitor}s are evaluated
 * lazily, that is, if the fist {@link ConclusionVisitor} returns {@link false},
 * the visit method of the second {@link ConclusionVisitor} is not called.
 * 
 * @author "Yevgeny Kazakov"
 */
public class AndConclusionVisitor implements ConclusionVisitor<Boolean> {

	final private ConclusionVisitor<Boolean> first_;

	final private ConclusionVisitor<Boolean> second_;

	/**
	 * Creates a new {@link ConclusionVisitor} that combines two given
	 * {@link ConclusionVisitor}s. The visit method of the combined visitor
	 * returns {@link true} for the {@link Conclusion} if and only if both of
	 * these {@link ConclusionVisitor}s return {@code true}. The combined
	 * {@link ConclusionVisitor}s are evaluated lazily, that is, if the fist
	 * {@link ConclusionVisitor} returns {@link false}, the visit method of the
	 * second {@link ConclusionVisitor} is not called.
	 * 
	 * @param first
	 *            The {@link ConclusionVisitor} that should be used first
	 * @param second
	 *            The {@link ConclusionVisitor} that should be used second
	 */
	public AndConclusionVisitor(ConclusionVisitor<Boolean> first,
			ConclusionVisitor<Boolean> second) {
		this.first_ = first;
		this.second_ = second;
	}

	@Override
	public Boolean visit(BackwardLink conclusion, Context context) {
		return first_.visit(conclusion, context)
				&& second_.visit(conclusion, context);
	}

	@Override
	public Boolean visit(ComposedSubsumer conclusion, Context context) {
		return first_.visit(conclusion, context)
				&& second_.visit(conclusion, context);
	}

	@Override
	public Boolean visit(Contradiction conclusion, Context context) {
		return first_.visit(conclusion, context)
				&& second_.visit(conclusion, context);
	}

	@Override
	public Boolean visit(DecomposedSubsumer conclusion, Context context) {
		return first_.visit(conclusion, context)
				&& second_.visit(conclusion, context);
	}

	@Override
	public Boolean visit(DisjointSubsumer conclusion, Context context) {
		return first_.visit(conclusion, context)
				&& second_.visit(conclusion, context);
	}

	@Override
	public Boolean visit(ForwardLink conclusion, Context context) {
		return first_.visit(conclusion, context)
				&& second_.visit(conclusion, context);
	}

	@Override
	public Boolean visit(Propagation conclusion, Context context) {
		return first_.visit(conclusion, context)
				&& second_.visit(conclusion, context);
	}

}
