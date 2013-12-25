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

public class ConclusionDeletionVisitor implements ConclusionVisitor<Boolean> {

	@Override
	public Boolean visit(ComposedSubsumer negSCE, Context context) {
		return context.removeSubsumer(negSCE.getExpression());
	}

	@Override
	public Boolean visit(DecomposedSubsumer posSCE, Context context) {
		return context.removeSubsumer(posSCE.getExpression());
	}

	@Override
	public Boolean visit(BackwardLink link, Context context) {
		return context.removeBackwardLink(link);
	}

	@Override
	public Boolean visit(ForwardLink link, Context context) {
		return link.removeFromContextBackwardLinkRule(context);
	}

	@Override
	public Boolean visit(Contradiction bot, Context context) {
		return context.removeConradiction();
	}

	@Override
	public Boolean visit(Propagation propagation, Context context) {
		return propagation.removeFromContextBackwardLinkRule(context);
	}

	@Override
	public Boolean visit(DisjointSubsumer disjointnessAxiom, Context context) {
		return context.removeDisjointnessAxiom(disjointnessAxiom.getAxiom());
	}

}
