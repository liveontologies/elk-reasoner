package org.semanticweb.elk.reasoner.saturation.rules;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.model.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassEntity;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectComplementOf;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectHasSelf;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubContextInitialization;
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
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ComposedFromDecomposedSubsumerRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ContradictionFromNegationRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ContradictionFromOwlNothingRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.DisjointSubsumerFromMemberRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.IndexedClassDecompositionRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.IndexedClassFromDefinitionRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.IndexedObjectComplementOfDecomposition;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.IndexedObjectHasSelfDecomposition;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.IndexedObjectIntersectionOfDecomposition;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.IndexedObjectSomeValuesFromDecomposition;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ObjectIntersectionFromFirstConjunctRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ObjectIntersectionFromSecondConjunctRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ObjectUnionFromDisjunctRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.PropagationFromExistentialFillerRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.SuperClassFromSubClassRule;
import org.semanticweb.elk.util.logging.CachedTimeThread;

/**
 * A {@link RuleVisitor} wrapper for a given {@link RuleVisitor} that
 * additionally records the time spend within methods in the given
 * {@link RuleApplicationTimer}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <O>
 *            the type of output parameter with which this visitor works
 */
class RuleApplicationTimerVisitor<O> implements RuleVisitor<O> {

	/**
	 * timer used to time the visitor
	 */
	private final RuleApplicationTimer timer_;

	/**
	 * the visitor whose methods to be timed
	 */
	private final RuleVisitor<O> visitor_;

	public RuleApplicationTimerVisitor(RuleVisitor<O> visitor,
			RuleApplicationTimer timer) {
		this.timer_ = timer;
		this.visitor_ = visitor;
	}

	@Override
	public O visit(BackwardLinkChainFromBackwardLinkRule rule,
			BackwardLink premise, ContextPremises premises,
			ClassConclusionProducer producer) {
		timer_.timeBackwardLinkChainFromBackwardLinkRule -= CachedTimeThread
				.getCurrentTimeMillis();
		O result = visitor_.visit(rule, premise, premises, producer);
		timer_.timeBackwardLinkChainFromBackwardLinkRule += CachedTimeThread
				.getCurrentTimeMillis();
		return result;

	}

	@Override
	public O visit(BackwardLinkFromForwardLinkRule rule, ForwardLink premise,
			ContextPremises premises, ClassConclusionProducer producer) {
		timer_.timeBackwardLinkFromForwardLinkRule -= CachedTimeThread
				.getCurrentTimeMillis();
		O result = visitor_.visit(rule, premise, premises, producer);
		timer_.timeBackwardLinkFromForwardLinkRule += CachedTimeThread
				.getCurrentTimeMillis();
		return result;
	}

	@Override
	public O visit(ComposedFromDecomposedSubsumerRule rule,
			IndexedClassEntity premise, ContextPremises premises,
			ClassConclusionProducer producer) {
		timer_.timeComposedFromDecomposedSubsumerRule -= CachedTimeThread
				.getCurrentTimeMillis();
		O result = visitor_.visit(rule, premise, premises, producer);
		timer_.timeComposedFromDecomposedSubsumerRule += CachedTimeThread
				.getCurrentTimeMillis();
		return result;
	}

	@Override
	public O visit(ContradictionCompositionRule rule, DisjointSubsumer premise,
			ContextPremises premises, ClassConclusionProducer producer) {
		timer_.timeContradictionCompositionRule -= CachedTimeThread
				.getCurrentTimeMillis();
		O result = visitor_.visit(rule, premise, premises, producer);
		timer_.timeContradictionCompositionRule += CachedTimeThread
				.getCurrentTimeMillis();
		return result;
	}

	@Override
	public O visit(ContradictionFromNegationRule rule,
			IndexedClassExpression premise, ContextPremises premises,
			ClassConclusionProducer producer) {
		timer_.timeContradictionFromNegationRule -= CachedTimeThread
				.getCurrentTimeMillis();
		O result = visitor_.visit(rule, premise, premises, producer);
		timer_.timeContradictionFromNegationRule += CachedTimeThread
				.getCurrentTimeMillis();
		return result;
	}

	@Override
	public O visit(ContradictionFromOwlNothingRule rule,
			IndexedClassExpression premise, ContextPremises premises,
			ClassConclusionProducer producer) {
		timer_.timeContradictionFromOwlNothingRule -= CachedTimeThread
				.getCurrentTimeMillis();
		O result = visitor_.visit(rule, premise, premises, producer);
		timer_.timeContradictionFromOwlNothingRule += CachedTimeThread
				.getCurrentTimeMillis();
		return result;
	}

	@Override
	public O visit(ContradictionOverBackwardLinkRule rule,
			BackwardLink premise, ContextPremises premises,
			ClassConclusionProducer producer) {
		timer_.timeContradictionOverBackwardLinkRule -= CachedTimeThread
				.getCurrentTimeMillis();
		O result = visitor_.visit(rule, premise, premises, producer);
		timer_.timeContradictionOverBackwardLinkRule += CachedTimeThread
				.getCurrentTimeMillis();
		return result;
	}

	@Override
	public O visit(ContradictionPropagationRule rule, Contradiction premise,
			ContextPremises premises, ClassConclusionProducer producer) {
		timer_.timeContradictionPropagationRule -= CachedTimeThread
				.getCurrentTimeMillis();
		O result = visitor_.visit(rule, premise, premises, producer);
		timer_.timeContradictionPropagationRule += CachedTimeThread
				.getCurrentTimeMillis();
		return result;
	}

	@Override
	public O visit(DisjointSubsumerFromMemberRule rule,
			IndexedClassExpression premise, ContextPremises premises,
			ClassConclusionProducer producer) {
		timer_.timeDisjointSubsumerFromMemberRule -= CachedTimeThread
				.getCurrentTimeMillis();
		O result = visitor_.visit(rule, premise, premises, producer);
		timer_.timeDisjointSubsumerFromMemberRule += CachedTimeThread
				.getCurrentTimeMillis();
		return result;
	}

	@Override
	public O visit(IndexedClassDecompositionRule rule, IndexedClass premise,
			ContextPremises premises, ClassConclusionProducer producer) {
		timer_.timeIndexedClassDecompositionRule -= CachedTimeThread
				.getCurrentTimeMillis();
		O result = visitor_.visit(rule, premise, premises, producer);
		timer_.timeIndexedClassDecompositionRule += CachedTimeThread
				.getCurrentTimeMillis();
		return result;
	}

	@Override
	public O visit(IndexedClassFromDefinitionRule rule,
			IndexedClassExpression premise, ContextPremises premises,
			ClassConclusionProducer producer) {
		timer_.timeIndexedClassFromDefinitionRule -= CachedTimeThread
				.getCurrentTimeMillis();
		O result = visitor_.visit(rule, premise, premises, producer);
		timer_.timeIndexedClassFromDefinitionRule += CachedTimeThread
				.getCurrentTimeMillis();
		return result;
	}

	@Override
	public O visit(IndexedObjectComplementOfDecomposition rule,
			IndexedObjectComplementOf premise, ContextPremises premises,
			ClassConclusionProducer producer) {
		timer_.timeIndexedObjectComplementOfDecomposition -= CachedTimeThread
				.getCurrentTimeMillis();
		O result = visitor_.visit(rule, premise, premises, producer);
		timer_.timeIndexedObjectComplementOfDecomposition += CachedTimeThread
				.getCurrentTimeMillis();
		return result;
	}

	@Override
	public O visit(IndexedObjectHasSelfDecomposition rule,
			IndexedObjectHasSelf premise, ContextPremises premises,
			ClassConclusionProducer producer) {
		timer_.timeIndexedObjectHasSelfDecomposition -= CachedTimeThread
				.getCurrentTimeMillis();
		O result = visitor_.visit(rule, premise, premises, producer);
		timer_.timeIndexedObjectHasSelfDecomposition += CachedTimeThread
				.getCurrentTimeMillis();
		return result;
	}

	@Override
	public O visit(IndexedObjectIntersectionOfDecomposition rule,
			IndexedObjectIntersectionOf premise, ContextPremises premises,
			ClassConclusionProducer producer) {
		timer_.timeIndexedObjectIntersectionOfDecomposition -= CachedTimeThread
				.getCurrentTimeMillis();
		O result = visitor_.visit(rule, premise, premises, producer);
		timer_.timeIndexedObjectIntersectionOfDecomposition += CachedTimeThread
				.getCurrentTimeMillis();
		return result;
	}

	@Override
	public O visit(IndexedObjectSomeValuesFromDecomposition rule,
			IndexedObjectSomeValuesFrom premise, ContextPremises premises,
			ClassConclusionProducer producer) {
		timer_.timeIndexedObjectSomeValuesFromDecomposition -= CachedTimeThread
				.getCurrentTimeMillis();
		O result = visitor_.visit(rule, premise, premises, producer);
		timer_.timeIndexedObjectSomeValuesFromDecomposition += CachedTimeThread
				.getCurrentTimeMillis();
		return result;
	}

	@Override
	public O visit(NonReflexiveBackwardLinkCompositionRule rule,
			ForwardLink premise, ContextPremises premises,
			ClassConclusionProducer producer) {
		timer_.timeNonReflexiveBackwardLinkCompositionRule -= CachedTimeThread
				.getCurrentTimeMillis();
		O result = visitor_.visit(rule, premise, premises, producer);
		timer_.timeNonReflexiveBackwardLinkCompositionRule += CachedTimeThread
				.getCurrentTimeMillis();
		return result;
	}

	@Override
	public O visit(ObjectIntersectionFromFirstConjunctRule rule,
			IndexedClassExpression premise, ContextPremises premises,
			ClassConclusionProducer producer) {
		timer_.timeObjectIntersectionFromFirstConjunctRule -= CachedTimeThread
				.getCurrentTimeMillis();
		O result = visitor_.visit(rule, premise, premises, producer);
		timer_.timeObjectIntersectionFromFirstConjunctRule += CachedTimeThread
				.getCurrentTimeMillis();
		return result;
	}

	@Override
	public O visit(ObjectIntersectionFromSecondConjunctRule rule,
			IndexedClassExpression premise, ContextPremises premises,
			ClassConclusionProducer producer) {
		timer_.timeObjectIntersectionFromSecondConjunctRule -= CachedTimeThread
				.getCurrentTimeMillis();
		O result = visitor_.visit(rule, premise, premises, producer);
		timer_.timeObjectIntersectionFromSecondConjunctRule += CachedTimeThread
				.getCurrentTimeMillis();
		return result;
	}

	@Override
	public O visit(ObjectUnionFromDisjunctRule rule,
			IndexedClassExpression premise, ContextPremises premises,
			ClassConclusionProducer producer) {
		timer_.timeObjectUnionFromDisjunctRule -= CachedTimeThread
				.getCurrentTimeMillis();
		O result = visitor_.visit(rule, premise, premises, producer);
		timer_.timeObjectUnionFromDisjunctRule += CachedTimeThread
				.getCurrentTimeMillis();
		return result;
	}

	@Override
	public O visit(OwlThingContextInitRule rule, ContextInitialization premise,
			ContextPremises premises, ClassConclusionProducer producer) {
		timer_.timeOwlThingContextInitRule -= CachedTimeThread
				.getCurrentTimeMillis();
		O result = visitor_.visit(rule, premise, premises, producer);
		timer_.timeOwlThingContextInitRule += CachedTimeThread
				.getCurrentTimeMillis();
		return result;
	}

	@Override
	public O visit(PropagationFromExistentialFillerRule rule,
			IndexedClassExpression premise, ContextPremises premises,
			ClassConclusionProducer producer) {
		timer_.timePropagationFromExistentialFillerRule -= CachedTimeThread
				.getCurrentTimeMillis();
		O result = visitor_.visit(rule, premise, premises, producer);
		timer_.timePropagationFromExistentialFillerRule += CachedTimeThread
				.getCurrentTimeMillis();
		return result;
	}

	@Override
	public O visit(PropagationInitializationRule rule,
			SubContextInitialization premise, ContextPremises premises,
			ClassConclusionProducer producer) {
		timer_.timePropagationInitializationRule -= CachedTimeThread
				.getCurrentTimeMillis();
		O result = visitor_.visit(rule, premise, premises, producer);
		timer_.timePropagationInitializationRule += CachedTimeThread
				.getCurrentTimeMillis();
		return result;
	}

	@Override
	public O visit(ReflexiveBackwardLinkCompositionRule rule,
			ForwardLink premise, ContextPremises premises,
			ClassConclusionProducer producer) {
		timer_.timeReflexiveBackwardLinkCompositionRule -= CachedTimeThread
				.getCurrentTimeMillis();
		O result = visitor_.visit(rule, premise, premises, producer);
		timer_.timeReflexiveBackwardLinkCompositionRule += CachedTimeThread
				.getCurrentTimeMillis();
		return result;
	}

	@Override
	public O visit(RootContextInitializationRule rule,
			ContextInitialization premise, ContextPremises premises,
			ClassConclusionProducer producer) {
		timer_.timeRootContextInitializationRule -= CachedTimeThread
				.getCurrentTimeMillis();
		O result = visitor_.visit(rule, premise, premises, producer);
		timer_.timeRootContextInitializationRule += CachedTimeThread
				.getCurrentTimeMillis();
		return result;
	}

	@Override
	public O visit(SubsumerBackwardLinkRule rule, BackwardLink premise,
			ContextPremises premises, ClassConclusionProducer producer) {
		timer_.timeSubsumerBackwardLinkRule -= CachedTimeThread
				.getCurrentTimeMillis();
		O result = visitor_.visit(rule, premise, premises, producer);
		timer_.timeSubsumerBackwardLinkRule += CachedTimeThread
				.getCurrentTimeMillis();
		return result;
	}

	@Override
	public O visit(SubsumerPropagationRule rule, Propagation premise,
			ContextPremises premises, ClassConclusionProducer producer) {
		timer_.timeSubsumerPropagationRule -= CachedTimeThread
				.getCurrentTimeMillis();
		O result = visitor_.visit(rule, premise, premises, producer);
		timer_.timeSubsumerPropagationRule += CachedTimeThread
				.getCurrentTimeMillis();
		return result;

	}

	@Override
	public O visit(SuperClassFromSubClassRule rule,
			IndexedClassExpression premise, ContextPremises premises,
			ClassConclusionProducer producer) {
		timer_.timeSuperClassFromSubClassRule -= CachedTimeThread
				.getCurrentTimeMillis();
		O result = visitor_.visit(rule, premise, premises, producer);
		timer_.timeSuperClassFromSubClassRule += CachedTimeThread
				.getCurrentTimeMillis();
		return result;
	}

}
