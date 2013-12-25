package org.semanticweb.elk.reasoner.saturation.conclusions;

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

import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * A {@link ConclusionVisitor} that marks the source context of the conclusion
 * as not saturated
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class ConclusionSourceUnsaturationVisitor implements
		ConclusionVisitor<Boolean> {

	private final SaturationStateWriter writer_;

	public ConclusionSourceUnsaturationVisitor(SaturationStateWriter writer) {
		this.writer_ = writer;
	}

	Boolean mark(Conclusion conclusion, Context context) {
		Context sourceContext = conclusion.getSourceContext(context);

		return sourceContext != null ? writer_
				.markAsNotSaturated(sourceContext) : false;
	}

	@Override
	public Boolean visit(ComposedSubsumer negSCE, Context context) {
		if (negSCE.getExpression().occurs()) {
			return mark(negSCE, context);
		}
		else {
			return false;
		}
	}

	@Override
	public Boolean visit(DecomposedSubsumer posSCE, Context context) {
		if (posSCE.getExpression().occurs()) {
			return mark(posSCE, context);
		}
		else {
			return false;
		}
	}

	@Override
	public Boolean visit(BackwardLink link, Context context) {
		return mark(link, context);
	}

	@Override
	public Boolean visit(ForwardLink link, Context context) {
		return mark(link, context);
	}

	@Override
	public Boolean visit(Contradiction bot, Context context) {
		return mark(bot, context);
	}

	@Override
	public Boolean visit(Propagation propagation, Context context) {
		return mark(propagation, context);
	}

	@Override
	public Boolean visit(DisjointSubsumer disjointnessAxiom, Context context) {
		return mark(disjointnessAxiom, context);
	}

}
