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
 * A {@link ConclusionVisitor} that removes the visited {@link Conclusion} from
 * the given {@link Context}. The visit method returns {@link true} if the
 * {@link Context} was modified as the result of this operation, i.e., the
 * {@link Conclusion} was contained in the {@link Context}.
 * 
 * @see ConclusionInsertionVisitor
 * @see ConclusionOccurrenceCheckingVisitor
 * 
 * @author "Yevgeny Kazakov"
 */
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
		return context.removeForwardLink(link);
	}

	@Override
	public Boolean visit(Contradiction bot, Context context) {
		return context.removeConradiction();
	}

	@Override
	public Boolean visit(Propagation propagation, Context context) {
		return context.removePropagation(propagation);
	}

	@Override
	public Boolean visit(DisjointSubsumer disjointnessAxiom, Context context) {
		return context.removeDisjointnessAxiom(disjointnessAxiom.getAxiom());
	}

}
