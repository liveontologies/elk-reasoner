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
 * A {@link ConclusionVisitor} that checks if visited {@link Conclusion} is
 * contained the given {@link Context}. The visit method returns {@link true} if
 * the {@link Context} is occurs in the {@link Context} and {@link false}
 * otherwise.
 * 
 * @see ConclusionInsertionVisitor
 * @see ConclusionDeletionVisitor
 * 
 * @author "Yevgeny Kazakov"
 */
public class ConclusionOccurranceCheckingVisitor implements
		ConclusionVisitor<Boolean> {

	@Override
	public Boolean visit(ComposedSubsumer negSCE, Context context) {
		return context.containsSubsumer(negSCE.getExpression());
	}

	@Override
	public Boolean visit(DecomposedSubsumer posSCE, Context context) {
		return context.containsSubsumer(posSCE.getExpression());
	}

	@Override
	public Boolean visit(BackwardLink link, Context context) {
		return context.containsBackwardLink(link);
	}

	@Override
	public Boolean visit(ForwardLink link, Context context) {
		return context.containsForwardLink(link);
	}

	@Override
	public Boolean visit(Contradiction bot, Context context) {
		return context.containsContradiction();
	}

	@Override
	public Boolean visit(Propagation propagation, Context context) {
		return context.containsPropagation(propagation);
	}

	@Override
	public Boolean visit(DisjointSubsumer disjointnessAxiom, Context context) {
		return context.containsDisjointnessAxiom(disjointnessAxiom.getAxiom());
	}

}
