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


public class CombinedConclusionVisitor<C> implements ConclusionVisitor<Boolean, C> {

	final private ConclusionVisitor<Boolean, C> first_;
	final private ConclusionVisitor<Boolean, C> second_;

	public CombinedConclusionVisitor(ConclusionVisitor<Boolean, C> first,
			ConclusionVisitor<Boolean, C> second) {
		this.first_ = first;
		this.second_ = second;
	}

	@Override
	public Boolean visit(ComposedSubsumer negSCE, C context) {
		return first_.visit(negSCE, context) && second_.visit(negSCE, context);
	}

	@Override
	public Boolean visit(DecomposedSubsumer posSCE, C context) {
		return first_.visit(posSCE, context) && second_.visit(posSCE, context);
	}

	@Override
	public Boolean visit(BackwardLink link, C context) {
		return first_.visit(link, context) && second_.visit(link, context);
	}

	@Override
	public Boolean visit(ForwardLink link, C context) {
		return first_.visit(link, context) && second_.visit(link, context);
	}

	@Override
	public Boolean visit(Contradiction bot, C context) {
		return first_.visit(bot, context) && second_.visit(bot, context);
	}

	@Override
	public Boolean visit(Propagation propagation, C context) {
		return first_.visit(propagation, context)
				&& second_.visit(propagation, context);
	}

	@Override
	public Boolean visit(DisjointnessAxiom disjointnessAxiom, C context) {
		return first_.visit(disjointnessAxiom, context)
				&& second_.visit(disjointnessAxiom, context);
	}

}
