package org.semanticweb.elk.alc.saturation;

/*
 * #%L
 * ALC Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import java.util.Collection;

import org.semanticweb.elk.alc.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.alc.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.BacktrackedBackwardLinkImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.BackwardLinkImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.PossibleComposedSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.PossibleDecomposedSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ConjectureNonSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ExternalDeterministicConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.LocalConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.NegatedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.NegativePropagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.PossibleComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.PossibleDecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.AbstractLocalConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.LocalConclusionVisitor;

/**
 * A {@link LocalConclusionVisitor} that reverts the visited
 * {@link LocalConclusion} (and possibly other related {@link Conclusions})
 * using the given {@link ConclusionProducer}. Always returns {@code true}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class RevertingVisitor extends
		AbstractLocalConclusionVisitor<Context, Boolean> {

	private final ConclusionProducer producer_;

	public RevertingVisitor(ConclusionProducer conclusionProducer) {
		this.producer_ = conclusionProducer;
	}

	@Override
	protected Boolean defaultVisit(Conclusion conclusion, Context input) {
		// does nothing by default
		return true;
	}

	@Override
	public Boolean visit(PossibleComposedSubsumer conclusion, Context input) {
		producer_.produce(conclusion);
		return true;
	}

	@Override
	public Boolean visit(PossibleDecomposedSubsumer conclusion, Context input) {
		producer_.produce(conclusion);
		return true;
	}

	void visitNegation(IndexedClassExpression negatedExpression, Context input) {
		if (!input.getPropagatedDisjunctionsByWatched().get(negatedExpression)
				.isEmpty())
			producer_.produce(new PossibleDecomposedSubsumerImpl(
					negatedExpression));
		else if (input.getPossibleExistentials().contains(negatedExpression))
			producer_.produce(new PossibleComposedSubsumerImpl(
					negatedExpression));
	}

	@Override
	public Boolean visit(NegatedSubsumer conclusion, Context input) {
		visitNegation(conclusion.getNegatedExpression(), input);
		return true;
	}

	@Override
	public Boolean visit(ConjectureNonSubsumer conclusion, Context input) {
		visitNegation(conclusion.getExpression(), input);
		return true;
	}

	@Override
	public Boolean visit(ForwardLink conclusion, Context input) {
		IndexedObjectProperty relation = conclusion.getRelation();
		Root root = input.getRoot();
		Root fillerRoot = new Root(conclusion.getTarget(), input
				.getNegativePropagations().get(relation));
		producer_.produce(fillerRoot, new BacktrackedBackwardLinkImpl(root,
				relation));
		input.removePropagatedConclusions(fillerRoot);
		return true;
	}

	@Override
	public Boolean visit(NegativePropagation conclusion, Context input) {
		IndexedObjectProperty relation = conclusion.getRelation();
		Root root = input.getRoot();
		IndexedClassExpression negatedCarry = conclusion.getNegatedCarry();
		Collection<IndexedClassExpression> oldNegativeRootMembers = input
				.getNegativePropagations().get(relation);
		ExternalDeterministicConclusion toAdd = new BackwardLinkImpl(root,
				relation);
		ExternalDeterministicConclusion toBacktrack = new BacktrackedBackwardLinkImpl(
				root, relation);
		for (IndexedClassExpression positiveMember : input.getForwardLinks()
				.get(relation)) {
			Root oldTargetRoot = new Root(positiveMember,
					oldNegativeRootMembers);
			Root newTargetRoot = Root.removeNegativeMember(oldTargetRoot,
					negatedCarry);
			input.removePropagatedConclusions(oldTargetRoot);
			producer_.produce(oldTargetRoot, toBacktrack);
			producer_.produce(newTargetRoot, toAdd);
		}
		return true;
	}

	final ConclusionProducer getProducer() {
		return producer_;
	}
}
