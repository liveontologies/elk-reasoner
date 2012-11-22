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

import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.context.Context;

public class MarkingConclusionVisitor implements ConclusionVisitor<Boolean> {

	private final SaturationState.Writer engine_;

	public MarkingConclusionVisitor(SaturationState.Writer engine) {
		this.engine_ = engine;
	}

	// TODO: move the contents of Conclusion#apply method here

	@Override
	public Boolean visit(NegativeSuperClassExpression negSCE, Context context) {
		engine_.markAsNotSaturated(context);
		return true;
	}

	@Override
	public Boolean visit(PositiveSuperClassExpression posSCE, Context context) {
		engine_.markAsNotSaturated(context);
		return true;
	}

	@Override
	public Boolean visit(BackwardLink link, Context context) {
		engine_.markAsNotSaturated(link.getSource());
		return true;
	}

	@Override
	public Boolean visit(ForwardLink link, Context context) {
		engine_.markAsNotSaturated(context);
		return true;
	}

	@Override
	public Boolean visit(Bottom bot, Context context) {
		engine_.markAsNotSaturated(context);
		return true;
	}

	@Override
	public Boolean visit(Propagation propagation, Context context) {
		engine_.markAsNotSaturated(context);
		return true;
	}

	@Override
	public Boolean visit(DisjointnessAxiom disjointnessAxiom, Context context) {
		engine_.markAsNotSaturated(context);
		return true;
	}

}
