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

import org.semanticweb.elk.reasoner.saturation.context.Context;

public class CountingConclusionVisitor implements ConclusionVisitor<Integer, Context> {

	private final ConclusionCounter counter_;

	public CountingConclusionVisitor(ConclusionCounter counter) {
		this.counter_ = counter;
	}

	@Override
	public Integer visit(NegativeSubsumer negSCE, Context context) {
		return counter_.countNegativeSubsumers++;
	}

	@Override
	public Integer visit(PositiveSubsumer posSCE, Context context) {
		return counter_.countPositiveSubsumers++;
	}

	@Override
	public Integer visit(BackwardLink link, Context context) {
		return counter_.countBackwardLinks++;
	}

	@Override
	public Integer visit(ForwardLink link, Context context) {
		return counter_.countForwardLinks++;
	}

	@Override
	public Integer visit(Contradiction bot, Context context) {
		return counter_.countContradictions++;
	}

	@Override
	public Integer visit(Propagation propagation, Context context) {
		return counter_.countPropagations++;
	}

	@Override
	public Integer visit(DisjointnessAxiom disjointnessAxiom, Context context) {
		return counter_.countDisjointnessAxioms++;
	}

}
