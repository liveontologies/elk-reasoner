package org.semanticweb.elk.reasoner.saturation.rules;

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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectComplementOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.backwardlinks.BackwardLinkChainFromBackwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.backwardlinks.ContradictionOverBackwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.backwardlinks.ForwardLinkFromBackwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.backwardlinks.PropagationFromBackwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.backwardlinks.SubsumerBackwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.contextinit.OwlThingContextInitRule;
import org.semanticweb.elk.reasoner.saturation.rules.contextinit.RootContextInitializationRule;
import org.semanticweb.elk.reasoner.saturation.rules.contradiction.ContradictionPropagationRule;
import org.semanticweb.elk.reasoner.saturation.rules.disjointsubsumer.ContradicitonCompositionRule;
import org.semanticweb.elk.reasoner.saturation.rules.forwardlink.BackwardLinkCompositionRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ContradictionFromDisjointnessRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ContradictionFromNegationRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ContradictionFromOwlNothingRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.DisjointSubsumerFromMemberRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.IndexedObjectComplementOfDecomposition;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.IndexedObjectIntersectionOfDecomposition;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.IndexedObjectSomeValuesFromDecomposition;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ObjectIntersectionFromConjunctRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ObjectUnionFromDisjunctRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.PropagationFromExistentialFillerRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.SubsumerDecompositionVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.SuperClassFromSubClassRule;

/**
 * A {@link RuleVisitor} wrapper for a given {@link RuleVisitor} that
 * additionally records the number of invocations of the methods using the given
 * {@link RuleCounter}.
 * 
 * @author "Yevgeny Kazakov"
 */
public class RuleCounterVisitor implements RuleVisitor {

	/**
	 * the counter used to count the number of method applications of the
	 * visitor
	 */
	private final RuleCounter counter_;
	/**
	 * the visitor whose method applications to be counted
	 */
	private final RuleVisitor visitor_;

	/**
	 * Creates a new {@link RuleCounterVisitor} that executes the corresponding
	 * methods of the given {@link RuleVisitor} and counts the number of
	 * invocations of the corresponding methods using the given
	 * {@link RuleCounter}.
	 * 
	 * @param visitor
	 *            the {@link SubsumerDecompositionVisitor} used to execute the
	 *            methods
	 * @param counter
	 *            the {@link RuleCounter} used to count the number of method
	 *            invocations
	 */
	public RuleCounterVisitor(RuleVisitor visitor, RuleCounter counter) {
		this.visitor_ = visitor;
		this.counter_ = counter;
	}

	@Override
	public void visit(BackwardLinkChainFromBackwardLinkRule rule,
			BackwardLink premise, Context context, ConclusionProducer producer) {
		counter_.countBackwardLinkChainFromBackwardLinkRule++;
		visitor_.visit(rule, premise, context, producer);

	}

	@Override
	public void visit(BackwardLinkCompositionRule rule, ForwardLink premise,
			Context context, ConclusionProducer producer) {
		counter_.countBackwardLinkCompositionRule++;
		visitor_.visit(rule, premise, context, producer);
	}

	@Override
	public void visit(ContradicitonCompositionRule rule,
			DisjointSubsumer premise, Context context,
			ConclusionProducer producer) {
		counter_.countContradicitonCompositionRule++;
		visitor_.visit(rule, premise, context, producer);
	}

	@Override
	public void visit(ContradictionFromDisjointnessRule rule,
			IndexedClassExpression premise, Context context,
			ConclusionProducer producer) {
		counter_.countContradictionFromDisjointnessRule++;
		visitor_.visit(rule, premise, context, producer);
	}

	@Override
	public void visit(ContradictionFromNegationRule rule,
			IndexedClassExpression premise, Context context,
			ConclusionProducer producer) {
		counter_.countContradictionFromNegationRule++;
		visitor_.visit(rule, premise, context, producer);
	}

	@Override
	public void visit(ContradictionFromOwlNothingRule rule,
			IndexedClassExpression premise, Context context,
			ConclusionProducer producer) {
		counter_.countContradictionFromOwlNothingRule++;
		visitor_.visit(rule, premise, context, producer);
	}

	@Override
	public void visit(ContradictionOverBackwardLinkRule rule,
			BackwardLink premise, Context context, ConclusionProducer producer) {
		counter_.countContradictionOverBackwardLinkRule++;
		visitor_.visit(rule, premise, context, producer);
	}

	@Override
	public void visit(ContradictionPropagationRule rule, Contradiction premise,
			Context context, ConclusionProducer producer) {
		counter_.countContradictionPropagationRule++;
		visitor_.visit(rule, premise, context, producer);
	}

	@Override
	public void visit(DisjointSubsumerFromMemberRule rule,
			IndexedClassExpression premise, Context context,
			ConclusionProducer producer) {
		counter_.countDisjointSubsumerFromMemberRule++;
		visitor_.visit(rule, premise, context, producer);
	}

	@Override
	public void visit(ForwardLinkFromBackwardLinkRule rule,
			BackwardLink premise, Context context, ConclusionProducer producer) {
		counter_.countForwardLinkFromBackwardLinkRule++;
		visitor_.visit(rule, premise, context, producer);
	}

	@Override
	public void visit(IndexedObjectComplementOfDecomposition rule,
			IndexedObjectComplementOf premise, Context context,
			ConclusionProducer producer) {
		counter_.countIndexedObjectComplementOfDecomposition++;
		visitor_.visit(rule, premise, context, producer);
	}

	@Override
	public void visit(IndexedObjectIntersectionOfDecomposition rule,
			IndexedObjectIntersectionOf premise, Context context,
			ConclusionProducer producer) {
		counter_.countIndexedObjectIntersectionOfDecomposition++;
		visitor_.visit(rule, premise, context, producer);

	}

	@Override
	public void visit(IndexedObjectSomeValuesFromDecomposition rule,
			IndexedObjectSomeValuesFrom premise, Context context,
			ConclusionProducer producer) {
		counter_.countIndexedObjectSomeValuesFromDecomposition++;
		visitor_.visit(rule, premise, context, producer);
	}

	@Override
	public void visit(ObjectIntersectionFromConjunctRule rule,
			IndexedClassExpression premise, Context context,
			ConclusionProducer producer) {
		counter_.countObjectIntersectionFromConjunctRule++;
		visitor_.visit(rule, premise, context, producer);
	}

	@Override
	public void visit(ObjectUnionFromDisjunctRule rule,
			IndexedClassExpression premise, Context context,
			ConclusionProducer producer) {
		counter_.countObjectUnionFromDisjunctRule++;
		visitor_.visit(rule, premise, context, producer);
	}

	@Override
	public void visit(OwlThingContextInitRule rule, Context context,
			ConclusionProducer producer) {
		counter_.countOwlThingContextInitRule++;
		visitor_.visit(rule, context, producer);
	}

	@Override
	public void visit(PropagationFromBackwardLinkRule rule,
			BackwardLink premise, Context context, ConclusionProducer producer) {
		counter_.countPropagationFromBackwardLinkRule++;
		visitor_.visit(rule, premise, context, producer);
	}

	@Override
	public void visit(PropagationFromExistentialFillerRule rule,
			IndexedClassExpression premise, Context context,
			ConclusionProducer producer) {
		counter_.countPropagationFromExistentialFillerRule++;
		visitor_.visit(rule, premise, context, producer);
	}

	@Override
	public void visit(RootContextInitializationRule rule, Context context,
			ConclusionProducer producer) {
		counter_.countRootContextInitializationRule++;
		visitor_.visit(rule, context, producer);
	}

	@Override
	public void visit(SubsumerBackwardLinkRule rule, BackwardLink premise,
			Context context, ConclusionProducer producer) {
		counter_.countSubsumerBackwardLinkRule++;
		visitor_.visit(rule, premise, context, producer);
	}

	@Override
	public void visit(SuperClassFromSubClassRule rule,
			IndexedClassExpression premise, Context context,
			ConclusionProducer producer) {
		counter_.countSuperClassFromSubClassRule++;
		visitor_.visit(rule, premise, context, producer);
	}

}
