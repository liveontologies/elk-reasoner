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

public class ConclusionDeapplicationVisitor implements
		ConclusionVisitor<Boolean> {

	private final SaturationState.Writer engine_;

	public ConclusionDeapplicationVisitor(SaturationState.Writer engine) {
		this.engine_ = engine;
	}

	@Override
	public Boolean visit(NegativeSuperClassExpression negSCE, Context context) {
		negSCE.deapply(engine_, context);
		return true;
	}

	@Override
	public Boolean visit(PositiveSuperClassExpression posSCE, Context context) {
		posSCE.deapply(engine_, context);
		return true;
	}

	@Override
	public Boolean visit(BackwardLink link, Context context) {
		link.deapply(engine_, context);
		return true;
	}

	@Override
	public Boolean visit(ForwardLink link, Context context) {
		link.deapply(engine_, context);
		return true;
	}

	@Override
	public Boolean visit(Bottom bot, Context context) {
		bot.deapply(engine_, context);
		return true;
	}

	@Override
	public Boolean visit(Propagation propagation, Context context) {
		propagation.deapply(engine_, context);
		return true;
	}

	@Override
	public Boolean visit(DisjointnessAxiom disjointnessAxiom, Context context) {
		disjointnessAxiom.deapply(engine_, context);
		return true;
	}

}
