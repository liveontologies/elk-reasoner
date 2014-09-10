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
import org.semanticweb.elk.reasoner.saturation.rules.propagations.NonReflexivePropagationRule;
import org.semanticweb.elk.reasoner.saturation.rules.propagations.ReflexivePropagationRule;
import org.semanticweb.elk.reasoner.saturation.rules.subcontextinit.PropagationInitializationRule;
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
import org.semanticweb.elk.util.logging.CachedTimeThread;

/**
 * A {@link RuleVisitor} wrapper for a given {@link RuleVisitor} that
 * additionally records the time spend within methods in the given
 * {@link RuleApplicationTimer}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class RuleApplicationTimerVisitor implements RuleVisitor {

	/**
	 * timer used to time the visitor
	 */
	private final RuleApplicationTimer timer_;

	/**
	 * the visitor whose methods to be timed
	 */
	private final RuleVisitor visitor_;

	/**
	 * Creates a new {@link SubsumerDecompositionVisitor} that executes the
	 * corresponding methods of the given {@link SubsumerDecompositionVisitor}
	 * and measures the time spent within the corresponding methods using the
	 * given {@link RuleApplicationTimer}.
	 * 
	 * @param visitor
	 *            the {@link SubsumerDecompositionVisitor} used to execute the
	 *            methods
	 * @param timer
	 *            the {@link RuleApplicationTimer} used to mesure the time spent
	 *            within the methods
	 */
	public RuleApplicationTimerVisitor(RuleVisitor visitor,
			RuleApplicationTimer timer) {
		this.timer_ = timer;
		this.visitor_ = visitor;
	}

	@Override
	public void visit(BackwardLinkChainFromBackwardLinkRule rule,
			BackwardLink premise, ContextPremises premises,
			ConclusionProducer producer) {
		timer_.timeBackwardLinkChainFromBackwardLinkRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(rule, premise, premises, producer);
		timer_.timeBackwardLinkChainFromBackwardLinkRule += CachedTimeThread
				.getCurrentTimeMillis();

	}

	@Override
	public void visit(BackwardLinkFromForwardLinkRule rule,
			ForwardLink premise, ContextPremises premises,
			ConclusionProducer producer) {
		timer_.timeBackwardLinkFromForwardLinkRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(rule, premise, premises, producer);
		timer_.timeBackwardLinkFromForwardLinkRule += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public void visit(ContradictionCompositionRule rule,
			DisjointSubsumer premise, ContextPremises premises,
			ConclusionProducer producer) {
		timer_.timeContradicitonCompositionRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(rule, premise, premises, producer);
		timer_.timeContradicitonCompositionRule += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public void visit(ContradictionFromDisjointnessRule rule,
			IndexedClassExpression premise, ContextPremises premises,
			ConclusionProducer producer) {
		timer_.timeContradictionFromDisjointnessRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(rule, premise, premises, producer);
		timer_.timeContradictionFromDisjointnessRule += CachedTimeThread
				.getCurrentTimeMillis();

	}

	@Override
	public void visit(ContradictionFromNegationRule rule,
			IndexedClassExpression premise, ContextPremises premises,
			ConclusionProducer producer) {
		timer_.timeContradictionFromNegationRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(rule, premise, premises, producer);
		timer_.timeContradictionFromNegationRule += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public void visit(ContradictionFromOwlNothingRule rule,
			IndexedClassExpression premise, ContextPremises premises,
			ConclusionProducer producer) {
		timer_.timeContradictionFromOwlNothingRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(rule, premise, premises, producer);
		timer_.timeContradictionFromOwlNothingRule += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public void visit(ContradictionOverBackwardLinkRule rule,
			BackwardLink premise, ContextPremises premises,
			ConclusionProducer producer) {
		timer_.timeContradictionOverBackwardLinkRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(rule, premise, premises, producer);
		timer_.timeContradictionOverBackwardLinkRule += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public void visit(ContradictionPropagationRule rule, Contradiction premise,
			ContextPremises premises, ConclusionProducer producer) {
		timer_.timeContradictionPropagationRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(rule, premise, premises, producer);
		timer_.timeContradictionPropagationRule += CachedTimeThread
				.getCurrentTimeMillis();

	}

	@Override
	public void visit(DisjointSubsumerFromMemberRule rule,
			IndexedClassExpression premise, ContextPremises premises,
			ConclusionProducer producer) {
		timer_.timeDisjointSubsumerFromMemberRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(rule, premise, premises, producer);
		timer_.timeDisjointSubsumerFromMemberRule += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public void visit(IndexedObjectComplementOfDecomposition rule,
			IndexedObjectComplementOf premise, ContextPremises premises,
			ConclusionProducer producer) {
		timer_.timeIndexedObjectComplementOfDecomposition -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(rule, premise, premises, producer);
		timer_.timeIndexedObjectComplementOfDecomposition += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public void visit(IndexedObjectIntersectionOfDecomposition rule,
			IndexedObjectIntersectionOf premise, ContextPremises premises,
			ConclusionProducer producer) {
		timer_.timeIndexedObjectIntersectionOfDecomposition -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(rule, premise, premises, producer);
		timer_.timeIndexedObjectIntersectionOfDecomposition += CachedTimeThread
				.getCurrentTimeMillis();

	}

	@Override
	public void visit(IndexedObjectSomeValuesFromDecomposition rule,
			IndexedObjectSomeValuesFrom premise, ContextPremises premises,
			ConclusionProducer producer) {
		timer_.timeIndexedObjectSomeValuesFromDecomposition -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(rule, premise, premises, producer);
		timer_.timeIndexedObjectSomeValuesFromDecomposition += CachedTimeThread
				.getCurrentTimeMillis();

	}

	@Override
	public void visit(NonReflexiveBackwardLinkCompositionRule rule,
			ForwardLink premise, ContextPremises premises,
			ConclusionProducer producer) {
		timer_.timeNonReflexiveBackwardLinkCompositionRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(rule, premise, premises, producer);
		timer_.timeNonReflexiveBackwardLinkCompositionRule += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public void visit(NonReflexivePropagationRule rule, Propagation premise,
			ContextPremises premises, ConclusionProducer producer) {
		timer_.timeNonReflexivePropagationRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(rule, premise, premises, producer);
		timer_.timeNonReflexivePropagationRule += CachedTimeThread
				.getCurrentTimeMillis();

	}

	@Override
	public void visit(ObjectIntersectionFromConjunctRule rule,
			IndexedClassExpression premise, ContextPremises premises,
			ConclusionProducer producer) {
		timer_.timeObjectIntersectionFromConjunctRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(rule, premise, premises, producer);
		timer_.timeObjectIntersectionFromConjunctRule += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public void visit(ObjectUnionFromDisjunctRule rule,
			IndexedClassExpression premise, ContextPremises premises,
			ConclusionProducer producer) {
		timer_.timeObjectUnionFromDisjunctRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(rule, premise, premises, producer);
		timer_.timeObjectUnionFromDisjunctRule += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public void visit(OwlThingContextInitRule rule,
			ContextInitialization premise, ContextPremises premises,
			ConclusionProducer producer) {
		timer_.timeOwlThingContextInitRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(rule, premise, premises, producer);
		timer_.timeOwlThingContextInitRule += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public void visit(PropagationFromExistentialFillerRule rule,
			IndexedClassExpression premise, ContextPremises premises,
			ConclusionProducer producer) {
		timer_.timePropagationFromExistentialFillerRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(rule, premise, premises, producer);
		timer_.timePropagationFromExistentialFillerRule += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public void visit(PropagationInitializationRule rule,
			SubContextInitialization premise, ContextPremises premises,
			ConclusionProducer producer) {
		timer_.timePropagationInitializationRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(rule, premise, premises, producer);
		timer_.timePropagationInitializationRule += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public void visit(ReflexiveBackwardLinkCompositionRule rule,
			ForwardLink premise, ContextPremises premises,
			ConclusionProducer producer) {
		timer_.timeReflexiveBackwardLinkCompositionRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(rule, premise, premises, producer);
		timer_.timeReflexiveBackwardLinkCompositionRule += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public void visit(ReflexivePropagationRule rule, Propagation premise,
			ContextPremises premises, ConclusionProducer producer) {
		timer_.timeReflexivePropagationRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(rule, premise, premises, producer);
		timer_.timeReflexivePropagationRule += CachedTimeThread
				.getCurrentTimeMillis();

	}

	@Override
	public void visit(RootContextInitializationRule rule,
			ContextInitialization premise, ContextPremises premises,
			ConclusionProducer producer) {
		timer_.timeRootContextInitializationRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(rule, premise, premises, producer);
		timer_.timeRootContextInitializationRule += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public void visit(SubsumerBackwardLinkRule rule, BackwardLink premise,
			ContextPremises premises, ConclusionProducer producer) {
		timer_.timeSubsumerBackwardLinkRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(rule, premise, premises, producer);
		timer_.timeSubsumerBackwardLinkRule += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public void visit(SuperClassFromSubClassRule rule,
			IndexedClassExpression premise, ContextPremises premises,
			ConclusionProducer producer) {
		timer_.timeSuperClassFromSubClassRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(rule, premise, premises, producer);
		timer_.timeSuperClassFromSubClassRule += CachedTimeThread
				.getCurrentTimeMillis();
	}

}
