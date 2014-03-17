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
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.PossibleSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.DecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ExternalDeterministicConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.NegatedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.NegativePropagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.PossibleSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Subsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.AbstractConclusionVisitor;

public class RevertingVisitor extends AbstractConclusionVisitor<Context, Void> {

	private final ConclusionProducer producer_;

	public RevertingVisitor(ConclusionProducer conclusionProducer) {
		this.producer_ = conclusionProducer;
	}

	@Override
	protected Void defaultVisit(Conclusion conclusion, Context input) {
		// does nothing by default
		return null;
	}

	/**
	 * Called first when visiting any {@link Subsumer}
	 * 
	 * @param conclusion
	 * @param input
	 */
	void visitLocalSubsumer(Subsumer conclusion, Context input) {
		IndexedClassExpression expression = conclusion.getExpression();
		if (input.getDisjunctions().keySet().contains(expression)
				|| input.getMaskedPossibleSubsumers().contains(expression)) {
			producer_.produce(input.getRoot(), new PossibleSubsumerImpl(
					expression));
			return;
		}
	}

	@Override
	public Void visit(ComposedSubsumer conclusion, Context input) {
		visitLocalSubsumer(conclusion, input);
		return null;
	}

	@Override
	public Void visit(DecomposedSubsumer conclusion, Context input) {
		visitLocalSubsumer(conclusion, input);
		return null;
	}

	@Override
	public Void visit(PossibleSubsumer conclusion, Context input) {
		producer_.produce(input.getRoot(), conclusion);
		return null;
	}

	@Override
	public Void visit(NegatedSubsumer conclusion, Context input) {
		IndexedClassExpression negatedExpression = conclusion
				.getNegatedExpression();
		producer_.produce(input.getRoot(), new PossibleSubsumerImpl(
				negatedExpression));
		return null;
	}

	@Override
	public Void visit(ForwardLink conclusion, Context input) {
		IndexedObjectProperty relation = conclusion.getRelation();
		Root root = input.getRoot();
		Root fillerRoot = new Root(conclusion.getTarget(), input
				.getNegativePropagations().get(relation));
		producer_.produce(fillerRoot, new BacktrackedBackwardLinkImpl(root,
				relation));
		input.removePropagatedConclusions(relation, fillerRoot);
		return null;
	}

	@Override
	public Void visit(NegativePropagation conclusion, Context input) {
		IndexedObjectProperty relation = conclusion.getRelation();
		input.removePropagatedConclusions(relation);
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
			producer_.produce(oldTargetRoot, toBacktrack);
			producer_.produce(newTargetRoot, toAdd);
		}
		return null;
	}

}
