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
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectHasSelf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.SubContextInitialization;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.backwardlinks.BackwardLinkChainFromBackwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.backwardlinks.ContradictionOverBackwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.backwardlinks.SubsumerBackwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.contextinit.OwlThingContextInitRule;
import org.semanticweb.elk.reasoner.saturation.rules.contextinit.RootContextInitializationRule;
import org.semanticweb.elk.reasoner.saturation.rules.contradiction.ContradictionPropagationRule;
import org.semanticweb.elk.reasoner.saturation.rules.disjointsubsumer.ContradictionCompositionRule;
import org.semanticweb.elk.reasoner.saturation.rules.forwardlink.BackwardLinkFromForwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.forwardlink.NonReflexiveBackwardLinkCompositionRule;
import org.semanticweb.elk.reasoner.saturation.rules.forwardlink.ReflexiveBackwardLinkCompositionRule;
import org.semanticweb.elk.reasoner.saturation.rules.propagations.SubsumerPropagationRule;
import org.semanticweb.elk.reasoner.saturation.rules.subcontextinit.PropagationInitializationRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ContradictionFromDisjointnessRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ContradictionFromNegationRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ContradictionFromOwlNothingRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.DisjointSubsumerFromMemberRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.IndexedObjectComplementOfDecomposition;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.IndexedObjectHasSelfDecomposition;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.IndexedObjectIntersectionOfDecomposition;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.IndexedObjectSomeValuesFromDecomposition;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ObjectIntersectionFromConjunctRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ObjectUnionFromDisjunctRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.PropagationFromExistentialFillerRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.SuperClassFromSubClassRule;

/**
 * A {@link RuleVisitor} wrapper for a given {@link RuleVisitor} that
 * additionally records the number of invocations of the methods using the given
 * {@link RuleCounter}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <O>
 *            the type of output parameter with which this visitor works
 */
class RuleCounterVisitor<O> implements RuleVisitor<O> {

	/**
	 * the counter used to count the number of method applications of the
	 * visitor
	 */
	private final RuleCounter counter_;
	/**
	 * the visitor whose method applications to be counted
	 */
	private final RuleVisitor<O> visitor_;

	public RuleCounterVisitor(RuleVisitor<O> visitor, RuleCounter counter) {
		this.visitor_ = visitor;
		this.counter_ = counter;
	}

	@Override
	public O visit(BackwardLinkChainFromBackwardLinkRule rule,
			BackwardLink premise, ContextPremises premises,
			ConclusionProducer producer) {
		counter_.countBackwardLinkChainFromBackwardLinkRule++;
		return visitor_.visit(rule, premise, premises, producer);

	}

	@Override
	public O visit(BackwardLinkFromForwardLinkRule rule, ForwardLink premise,
			ContextPremises premises, ConclusionProducer producer) {
		counter_.countBackwardLinkFromForwardLinkRule++;
		return visitor_.visit(rule, premise, premises, producer);
	}

	@Override
	public O visit(ContradictionCompositionRule rule, DisjointSubsumer premise,
			ContextPremises premises, ConclusionProducer producer) {
		counter_.countContradicitonCompositionRule++;
		return visitor_.visit(rule, premise, premises, producer);
	}

	@Override
	public O visit(ContradictionFromDisjointnessRule rule,
			IndexedClassExpression premise, ContextPremises premises,
			ConclusionProducer producer) {
		counter_.countContradictionFromDisjointnessRule++;
		return visitor_.visit(rule, premise, premises, producer);
	}

	@Override
	public O visit(ContradictionFromNegationRule rule,
			IndexedClassExpression premise, ContextPremises premises,
			ConclusionProducer producer) {
		counter_.countContradictionFromNegationRule++;
		return visitor_.visit(rule, premise, premises, producer);
	}

	@Override
	public O visit(ContradictionFromOwlNothingRule rule,
			IndexedClassExpression premise, ContextPremises premises,
			ConclusionProducer producer) {
		counter_.countContradictionFromOwlNothingRule++;
		return visitor_.visit(rule, premise, premises, producer);
	}

	@Override
	public O visit(ContradictionOverBackwardLinkRule rule,
			BackwardLink premise, ContextPremises premises,
			ConclusionProducer producer) {
		counter_.countContradictionOverBackwardLinkRule++;
		return visitor_.visit(rule, premise, premises, producer);
	}

	@Override
	public O visit(ContradictionPropagationRule rule, Contradiction premise,
			ContextPremises premises, ConclusionProducer producer) {
		counter_.countContradictionPropagationRule++;
		return visitor_.visit(rule, premise, premises, producer);
	}

	@Override
	public O visit(DisjointSubsumerFromMemberRule rule,
			IndexedClassExpression premise, ContextPremises premises,
			ConclusionProducer producer) {
		counter_.countDisjointSubsumerFromMemberRule++;
		return visitor_.visit(rule, premise, premises, producer);
	}

	@Override
	public O visit(IndexedObjectComplementOfDecomposition rule,
			IndexedObjectComplementOf premise, ContextPremises premises,
			ConclusionProducer producer) {
		counter_.countIndexedObjectComplementOfDecomposition++;
		return visitor_.visit(rule, premise, premises, producer);
	}

	@Override
	public O visit(IndexedObjectHasSelfDecomposition rule,
			IndexedObjectHasSelf premise, ContextPremises premises,
			ConclusionProducer producer) {
		counter_.countIndexedObjectHasSelfDecomposition++;
		return visitor_.visit(rule, premise, premises, producer);
	}

	@Override
	public O visit(IndexedObjectIntersectionOfDecomposition rule,
			IndexedObjectIntersectionOf premise, ContextPremises premises,
			ConclusionProducer producer) {
		counter_.countIndexedObjectIntersectionOfDecomposition++;
		return visitor_.visit(rule, premise, premises, producer);

	}

	@Override
	public O visit(IndexedObjectSomeValuesFromDecomposition rule,
			IndexedObjectSomeValuesFrom premise, ContextPremises premises,
			ConclusionProducer producer) {
		counter_.countIndexedObjectSomeValuesFromDecomposition++;
		return visitor_.visit(rule, premise, premises, producer);
	}

	@Override
	public O visit(NonReflexiveBackwardLinkCompositionRule rule,
			ForwardLink premise, ContextPremises premises,
			ConclusionProducer producer) {
		counter_.countNonReflexiveBackwardLinkCompositionRule++;
		return visitor_.visit(rule, premise, premises, producer);
	}

	@Override
	public O visit(SubsumerPropagationRule rule, Propagation premise,
			ContextPremises premises, ConclusionProducer producer) {
		counter_.countSubsumerPropagationRule++;
		return visitor_.visit(rule, premise, premises, producer);
	}

	@Override
	public O visit(ObjectIntersectionFromConjunctRule rule,
			IndexedClassExpression premise, ContextPremises premises,
			ConclusionProducer producer) {
		counter_.countObjectIntersectionFromConjunctRule++;
		return visitor_.visit(rule, premise, premises, producer);
	}

	@Override
	public O visit(ObjectUnionFromDisjunctRule rule,
			IndexedClassExpression premise, ContextPremises premises,
			ConclusionProducer producer) {
		counter_.countObjectUnionFromDisjunctRule++;
		return visitor_.visit(rule, premise, premises, producer);
	}

	@Override
	public O visit(OwlThingContextInitRule rule, ContextInitialization premise,
			ContextPremises premises, ConclusionProducer producer) {
		counter_.countOwlThingContextInitRule++;
		return visitor_.visit(rule, premise, premises, producer);
	}

	@Override
	public O visit(PropagationFromExistentialFillerRule rule,
			IndexedClassExpression premise, ContextPremises premises,
			ConclusionProducer producer) {
		counter_.countPropagationFromExistentialFillerRule++;
		return visitor_.visit(rule, premise, premises, producer);
	}

	@Override
	public O visit(PropagationInitializationRule rule,
			SubContextInitialization premise, ContextPremises premises,
			ConclusionProducer producer) {
		counter_.countPropagationInitializationRule++;
		return visitor_.visit(rule, premise, premises, producer);
	}

	@Override
	public O visit(ReflexiveBackwardLinkCompositionRule rule,
			ForwardLink premise, ContextPremises premises,
			ConclusionProducer producer) {
		counter_.countReflexiveBackwardLinkCompositionRule++;
		return visitor_.visit(rule, premise, premises, producer);
	}

	@Override
	public O visit(RootContextInitializationRule rule,
			ContextInitialization premise, ContextPremises premises,
			ConclusionProducer producer) {
		counter_.countRootContextInitializationRule++;
		return visitor_.visit(rule, premise, premises, producer);
	}

	@Override
	public O visit(SubsumerBackwardLinkRule rule, BackwardLink premise,
			ContextPremises premises, ConclusionProducer producer) {
		counter_.countSubsumerBackwardLinkRule++;
		return visitor_.visit(rule, premise, premises, producer);
	}

	@Override
	public O visit(SuperClassFromSubClassRule rule,
			IndexedClassExpression premise, ContextPremises premises,
			ConclusionProducer producer) {
		counter_.countSuperClassFromSubClassRule++;
		return visitor_.visit(rule, premise, premises, producer);
	}

}
