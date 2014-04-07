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
import org.semanticweb.elk.alc.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.alc.saturation.conclusions.implementation.BacktrackedBackwardLinkImpl;
import org.semanticweb.elk.alc.saturation.conclusions.implementation.BackwardLinkImpl;
import org.semanticweb.elk.alc.saturation.conclusions.implementation.ClashImpl;
import org.semanticweb.elk.alc.saturation.conclusions.implementation.ComposedSubsumerImpl;
import org.semanticweb.elk.alc.saturation.conclusions.implementation.DecomposedSubsumerImpl;
import org.semanticweb.elk.alc.saturation.conclusions.implementation.NegatedSubsumerImpl;
import org.semanticweb.elk.alc.saturation.conclusions.implementation.NegativePropagationImpl;
import org.semanticweb.elk.alc.saturation.conclusions.implementation.PossibleComposedSubsumerImpl;
import org.semanticweb.elk.alc.saturation.conclusions.implementation.PossibleDecomposedSubsumerImpl;
import org.semanticweb.elk.alc.saturation.conclusions.implementation.PossiblePropagatedExistentialImpl;
import org.semanticweb.elk.alc.saturation.conclusions.implementation.PropagatedClashImpl;
import org.semanticweb.elk.alc.saturation.conclusions.implementation.PropagatedComposedSubsumerImpl;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.BackwardLink;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.Clash;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.ComposedSubsumer;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.ConjectureNonSubsumer;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.ContextInitialization;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.DecomposedSubsumer;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.DisjointSubsumer;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.Disjunction;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.ExternalDeterministicConclusion;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.ForwardLink;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.NegatedSubsumer;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.NegativePropagation;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.PossibleComposedSubsumer;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.PossibleDecomposedSubsumer;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.PossiblePropagatedExistential;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.PropagatedClash;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.PropagatedComposedSubsumer;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.Propagation;
import org.semanticweb.elk.alc.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.util.collections.LazySetIntersection;
import org.semanticweb.elk.util.collections.Multimap;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.collections.Operations.Condition;

public class RuleApplicationVisitor implements ConclusionVisitor<Context, Void> {

	private final ConclusionProducer producer_;

	private final IndexedClassExpressionVisitor<Void> subsumerDecompositionVisitor_;

	RuleApplicationVisitor(ConclusionProducer conclusionProducer) {
		this.producer_ = conclusionProducer;
		this.subsumerDecompositionVisitor_ = new SubsumerDecompositionVisitor(
				conclusionProducer);
	}

	@Override
	public Void visit(ContextInitialization conclusion, Context input) {
		Root root = input.getRoot();
		producer_.produce(new DecomposedSubsumerImpl(root.getPositiveMember()));
		for (IndexedClassExpression negativeMember : root.getNegativeMembers())
			producer_.produce(new NegatedSubsumerImpl(negativeMember));
		return null;
	}

	@Override
	public Void visit(ComposedSubsumer conclusion, Context input) {
		// IndexedClassExpression.applyCompositionRules(conclusion.getExpression(),
		// input, producer_);
		conclusion.getExpression().applyCompositionRules(input, producer_);
		return null;
	}

	@Override
	public Void visit(DecomposedSubsumer conclusion, Context input) {
		IndexedClassExpression expression = conclusion.getExpression();
		expression.accept(subsumerDecompositionVisitor_);
		if (input.isDeterministic()) {
			// IndexedClassExpression.applyCompositionRules(expression, input,
			// producer_);
			conclusion.getExpression().applyCompositionRules(input, producer_);
		} else {
			producer_.produce(new ComposedSubsumerImpl(expression));
		}
		return null;
	}

	@Override
	public Void visit(PropagatedComposedSubsumer conclusion, Context input) {
		producer_.produce(new ComposedSubsumerImpl(conclusion.getExpression()));

		return null;
	}

	@Override
	public Void visit(PossibleComposedSubsumer conclusion, Context input) {
		producer_.produce(new ComposedSubsumerImpl(conclusion.getExpression()));
		return null;
	}

	@Override
	public Void visit(PossibleDecomposedSubsumer conclusion, Context input) {
		producer_
				.produce(new DecomposedSubsumerImpl(conclusion.getExpression()));
		return null;
	}

	void visitNegation(IndexedClassExpression negatedExpression, Context input) {
		if (input.getSubsumers().contains(negatedExpression)) {
			producer_.produce(ClashImpl.getInstance());
			return;
		}
		
		for (IndexedClassExpression propagatedDisjunct : input
				.getPropagatedDisjunctionsByWatched().get(negatedExpression)) {
			producer_.produce(new DecomposedSubsumerImpl(propagatedDisjunct));
		}
		
		if (input.getPossibleExistentials().contains(negatedExpression)) {
			IndexedObjectSomeValuesFrom negatedExistential = (IndexedObjectSomeValuesFrom) negatedExpression;
			producer_.produce(new NegativePropagationImpl(negatedExistential));
		}

	}

	@Override
	public Void visit(NegatedSubsumer conclusion, Context input) {
		visitNegation(conclusion.getNegatedExpression(), input);
		return null;
	}

	@Override
	public Void visit(ForwardLink conclusion, Context input) {
		Root root = input.getRoot();
		IndexedObjectProperty linkRelation = conclusion.getRelation();
		Multimap<IndexedObjectProperty, IndexedClassExpression> negativePropagations = input.getNegativePropagations();
		
		if (negativePropagations.isEmpty()) {
			producer_.produce(new Root(conclusion.getTarget()), new BackwardLinkImpl(root, linkRelation));
			return null;
		}
		// there're some stored negative propagations, need to create backward links in the corresponding contexts 
		for (IndexedObjectProperty relation : new LazySetIntersection<IndexedObjectProperty>(linkRelation.getSaturatedProperty().getSuperProperties(), negativePropagations.keySet())) {
			Root fillerRoot = new Root(conclusion.getTarget(), negativePropagations.get(relation));

			producer_.produce(fillerRoot, new BackwardLinkImpl(root, linkRelation));
		}
		
		
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

		Root root = conclusion.getSource();

		if (Saturation.DEFERRED_PROPAGATION_GENERATION) {
			// generate propagations if this is the first backward link for this
			// relation
			if (input.getBackwardLinks().get(relation).size() == 1) {
				IndexedClassExpression.generatePropagations(producer_, input,
						relation);
			}
			// apply previously generated propagations
			applyAllPropagationsForRelation(relation, relation, input, root);
		} else {
			// apply all stored propagations over the super-roles of this link
			for (IndexedObjectProperty superProperty : new LazySetIntersection<IndexedObjectProperty>(
					relation.getSaturatedProperty().getSuperProperties(), input
							.getPropagations().keySet())) {
				applyAllPropagationsForRelation(superProperty, relation, input,
						root);
			}
		}

		return null;
	}

	void applyAllPropagationsForRelation(IndexedObjectProperty relation,
			IndexedObjectProperty linkRelation, Context input, Root target) {
		// propagations are done over the link's relation otherwise they will be
		// filtered as not relevant
		if (input.isDeterministic()) {
			for (IndexedObjectSomeValuesFrom propagatedSubsumer : input
					.getPropagations().get(relation)) {
				producer_.produce(target, new PropagatedComposedSubsumerImpl(
						linkRelation, input.getRoot(), propagatedSubsumer));
			}
		} else {
			for (IndexedObjectSomeValuesFrom propagatedSubsumer : input
					.getPropagations().get(relation)) {
				producer_.produce(target,
						new PossiblePropagatedExistentialImpl(linkRelation,
								input.getRoot(), propagatedSubsumer));
			}
		}
	}

	void applyPropagationForRelation(Propagation propagation,
			IndexedObjectProperty relation, Context input) {
		if (input.isDeterministic()) {
			for (Root root : input.getBackwardLinks().get(relation)) {
				producer_.produce(root, new PropagatedComposedSubsumerImpl(
						relation, input.getRoot(), propagation.getCarry()));
			}
		} else {
			for (Root root : input.getBackwardLinks().get(relation)) {
				producer_.produce(root, new PossiblePropagatedExistentialImpl(
						relation, input.getRoot(), propagation.getCarry()));
			}
		}
	}

	@Override
	public Void visit(Propagation conclusion, Context input) {
		// propagate over all backward links
		// TODO: for the future: propagations of universals should be decomposed
		// subsumer!
		IndexedObjectProperty relation = conclusion.getRelation();

		if (Saturation.DEFERRED_PROPAGATION_GENERATION) {
			applyPropagationForRelation(conclusion, conclusion.getRelation(),
					input);
		} else {
			// apply this propagation over the stored links for sub-roles of the
			// propagations' relation
			for (IndexedObjectProperty subProperty : new LazySetIntersection<IndexedObjectProperty>(
					relation.getSaturatedProperty().getSubProperties(), input
							.getBackwardLinks().keySet())) {
				applyPropagationForRelation(conclusion, subProperty, input);
			}
		}

		return null;
	}

	@Override
	public Void visit(Clash conclusion, Context input) {
		if (!input.isDeterministic())
			return null;
		// propagate to backward links only if the clash is derived
		// deterministically
		Multimap<IndexedObjectProperty, Root> backwardLinks = input.getBackwardLinks();
		Root root = input.getRoot();
		
		for (IndexedObjectProperty relation : backwardLinks.keySet()) {
			ExternalDeterministicConclusion propagatedClash = new PropagatedClashImpl(relation, root);
			
			for (Root target : backwardLinks.get(relation)) {
				producer_.produce(target, propagatedClash);
			}
		}
		return null;
	}

	//TODO doc required
	@Override
	public Void visit(NegativePropagation conclusion, Context input) {
		Root root = input.getRoot();
		IndexedObjectProperty relation = conclusion.getRelation();
		final IndexedClassExpression negatedCarry = conclusion.getNegatedCarry();

		for (IndexedObjectProperty linkRelation : new LazySetIntersection<IndexedObjectProperty>(relation.getSaturatedProperty().getSubProperties(), input.getForwardLinks().keySet())) {
			Collection<IndexedClassExpression> newNegativeRootMembers = input.getFillersInNegativePropagations(linkRelation);
			ExternalDeterministicConclusion toBacktrack = new BacktrackedBackwardLinkImpl(root, linkRelation);
			ExternalDeterministicConclusion toAdd = new BackwardLinkImpl(root, linkRelation);
			
			for (IndexedClassExpression positiveMember : input.getForwardLinks().get(linkRelation)) {
				Root newTargetRoot = new Root(positiveMember, newNegativeRootMembers);
				// fillers from all other negative propagations excluding this
				// one. The same expression can be excluded only once, 2nd
				// occurrence means that there's another propagation with the
				// same filler
				Root oldTargetRoot = new Root(positiveMember, Operations.filter(newNegativeRootMembers, new Condition<IndexedClassExpression>(){
					private boolean excluded = false;
					
					@Override
					public boolean holds(IndexedClassExpression element) {
						if (element == negatedCarry && !excluded) {
							excluded = true;
							
							return false;
						}
						
						return true;
					}}));
						
				input.removePropagatedConclusions(oldTargetRoot);

				if (oldTargetRoot.getNegativeMembers().size() != newTargetRoot.getNegativeMembers().size()) {
					producer_.produce(oldTargetRoot, toBacktrack);
					producer_.produce(newTargetRoot, toAdd);
				}
			}
		}

		return null;
	}
	

	@Override
	public Void visit(Disjunction conclusion, Context input) {
		IndexedClassExpression watchedDisjunct = conclusion
				.getWatchedDisjunct();
		IndexedClassExpression propagatedDisjunct = conclusion
				.getPropagatedDisjunct();
		if (input.getNegativeSubsumers().contains(watchedDisjunct)) {
			producer_.produce(new DecomposedSubsumerImpl(propagatedDisjunct));
			return null;
		}
		if (input.getNegativeSubsumers().contains(propagatedDisjunct)) {
			producer_.produce(new DecomposedSubsumerImpl(watchedDisjunct));
			return null;
		}
		// else
		producer_.produce(new PossibleDecomposedSubsumerImpl(watchedDisjunct));
		return null;
	}

	@Override
	public Void visit(PropagatedClash conclusion, Context input) {
		producer_.produce(ClashImpl.getInstance());
		return null;
	}

	@Override
	public Void visit(PossiblePropagatedExistential conclusion, Context input) {
		IndexedObjectSomeValuesFrom expression = conclusion.getExpression();

		if (input.getNegativeSubsumers().contains(expression)) {
			// this is basically a clash right there so instead of producing the
			// expression, we immediately produce the negative propagation
			producer_.produce(new NegativePropagationImpl(expression));
		} else {
			producer_.produce(new PossibleComposedSubsumerImpl(expression));
		}
		
		//producer_.produce(new PossibleComposedSubsumerImpl(expression));
		
		return null;
	}

	@Override
	public Void visit(ConjectureNonSubsumer conclusion, Context input) {
		visitNegation(conclusion.getExpression(), input);
		return null;
	}

	@Override
	public Void visit(DisjointSubsumer conclusion, Context input) {
		IndexedClassExpression[] disjointSubsumers = input
				.getDisjointSubsumers(conclusion.getAxiom());

		if (disjointSubsumers != null && disjointSubsumers[1] != null) {
			producer_.produce(ClashImpl.getInstance());
		}

		return null;
	}

}
