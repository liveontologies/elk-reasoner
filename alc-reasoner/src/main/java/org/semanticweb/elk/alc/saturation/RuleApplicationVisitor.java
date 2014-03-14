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
import org.semanticweb.elk.alc.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.BacktrackedBackwardLinkImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.BackwardLinkImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.ClashImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.DecomposedSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.NegatedSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.NegativePropagationImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.PossibleComposedSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.PossibleDecomposedSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.PropagatedClashImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Clash;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.DecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Disjunction;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.NegatedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.NegativePropagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.PropagatedClash;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.collections.Multimap;

public class RuleApplicationVisitor implements ConclusionVisitor<Context, Void> {

	private final ConclusionProducer producer_;

	RuleApplicationVisitor(ConclusionProducer conclusionProducer) {
		this.producer_ = conclusionProducer;
	}

	@Override
	public Void visit(ContextInitialization conclusion, Context input) {
		Root root = input.getRoot();
		producer_.produce(root,
				new DecomposedSubsumerImpl(root.getPositiveSubsumer()));
		for (IndexedClassExpression init : root.getNegatitveSubsumers())
			producer_.produce(root, new NegatedSubsumerImpl(init));
		return null;
	}

	@Override
	public Void visit(ComposedSubsumer conclusion, Context input) {
		IndexedClassExpression.applyCompositionRules(
				conclusion.getExpression(), input, producer_);
		return null;
	}

	@Override
	public Void visit(PropagatedClash conclusion, Context input) {
		producer_.produce(input.getRoot(), ClashImpl.getInstance());
		return null;
	}

	@Override
	public Void visit(DecomposedSubsumer conclusion, Context input) {
		IndexedClassExpression subsumer = conclusion.getExpression();
		IndexedClassExpression
				.applyCompositionRules(subsumer, input, producer_);
		subsumer.accept(new SubsumerDecompositionVisitor(input.getRoot(),
				producer_));
		return null;
	}

	@Override
	public Void visit(NegatedSubsumer conclusion, Context input) {
		IndexedClassExpression negatedExpression = conclusion
				.getNegatedExpression();
		Root root = input.getRoot();
		if (input.getSubsumers().contains(negatedExpression))
			producer_.produce(root, ClashImpl.getInstance());
		for (IndexedClassExpression propagatedDisjunct : input
				.getDisjunctions().get(negatedExpression)) {
			producer_.produce(root, new DecomposedSubsumerImpl(
					propagatedDisjunct));
		}
		if (negatedExpression instanceof IndexedObjectSomeValuesFrom) {
			IndexedObjectSomeValuesFrom negatedExistential = (IndexedObjectSomeValuesFrom) negatedExpression;
			if (negatedExistential.occursNegatively()) {
				producer_.produce(root, new NegativePropagationImpl(
						negatedExistential));
			}
		}
		return null;
	}

	@Override
	public Void visit(ForwardLink conclusion, Context input) {
		Root root = input.getRoot();
		IndexedObjectProperty relation = conclusion.getRelation();
		Root fillerRoot = new Root(conclusion.getTarget(), input
				.getNegativePropagations().get(relation));
		producer_.produce(fillerRoot,
				new BackwardLinkImpl(root, conclusion.getRelation()));
		return null;
	}

	@Override
	public Void visit(BackwardLink conclusion, Context input) {
		IndexedObjectProperty relation = conclusion.getRelation();
		if (input.isInconsistent()) {
			// propagate clash
			producer_.produce(conclusion.getSource(), new PropagatedClashImpl(
					relation, input.getRoot()));
		}
		if (input.getBackwardLinks().get(relation).size() == 1)
			// first link; generating propagations
			IndexedClassExpression.generatePropagations(relation, input,
					producer_);
		// apply propagations
		Root root = conclusion.getSource();
		for (IndexedClassExpression propagatedSubsumer : input
				.getPropagations().get(relation)) {
			// TODO: for propagations of universals should be decomposed
			// subsumer!
			producer_.produce(root, new PossibleComposedSubsumerImpl(
					propagatedSubsumer));
		}
		return null;
	}

	@Override
	public Void visit(Propagation conclusion, Context input) {
		// propagate over all backward links
		for (Root root : input.getBackwardLinks().get(conclusion.getRelation())) {
			// TODO: for propagations of universals should be decomposed
			// subsumer!
			producer_.produce(root,
					new PossibleComposedSubsumerImpl(conclusion.getCarry()));
		}
		return null;
	}

	@Override
	public Void visit(Clash conclusion, Context input) {
		if (!input.isDeterministic())
			return null;
		// propagated to backward links only if the clash is derived
		// deterministically
		Multimap<IndexedObjectProperty, Root> backwardLinks = input
				.getBackwardLinks();
		Root root = input.getRoot();
		for (IndexedObjectProperty relation : backwardLinks.keySet())
			for (Root target : backwardLinks.get(relation))
				producer_.produce(target, new PropagatedClashImpl(relation,
						root));
		return null;
	}

	@Override
	public Void visit(NegativePropagation conclusion, Context input) {
		Root root = input.getRoot();
		IndexedObjectProperty relation = conclusion.getRelation();
		Collection<IndexedClassExpression> newNegativeRootMembers = input
				.getNegativePropagations().get(relation);
		Conclusion newLink = new BackwardLinkImpl(root,
				conclusion.getRelation());
		Conclusion oldLink = new BacktrackedBackwardLinkImpl(root,
				conclusion.getRelation());
		Collection<IndexedClassExpression> oldNegativeRootMembers = new ArrayHashSet<IndexedClassExpression>(
				newNegativeRootMembers.size());
		oldNegativeRootMembers.addAll(newNegativeRootMembers);
		oldNegativeRootMembers.remove(conclusion.getNegatedCarry());
		for (IndexedClassExpression positiveMember : input.getForwardLinks()
				.get(relation)) {
			Root newTargetRoot = new Root(positiveMember,
					newNegativeRootMembers);
			producer_.produce(newTargetRoot, newLink);
			Root oldTargetRoot = new Root(positiveMember,
					oldNegativeRootMembers);
			producer_.produce(oldTargetRoot, oldLink);
		}
		return null;
	}

	@Override
	public Void visit(Disjunction conclusion, Context input) {
		IndexedClassExpression watchedDisjunct = conclusion
				.getWatchedDisjunct();
		Root root = input.getRoot();
		if (input.getNegativeSubsumers().contains(watchedDisjunct)) {
			producer_.produce(
					root,
					new DecomposedSubsumerImpl(conclusion
							.getPropagatedDisjunct()));
		} else {
			producer_.produce(root, new PossibleDecomposedSubsumerImpl(
					watchedDisjunct));
		}
		return null;
	}

}
