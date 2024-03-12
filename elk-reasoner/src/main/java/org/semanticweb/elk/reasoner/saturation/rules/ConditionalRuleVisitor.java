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
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectComplementOf;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectHasSelf;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassInconsistency;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ContextInitialization;
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
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.EquivalentClassFirstFromSecondRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.EquivalentClassSecondFromFirstRule;
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

/**
 * A {@link RuleVisitor} that delegates the calls to the provided
 * {@link RuleVisitor} when the condition checked using another
 * {@link RuleVisitor} returns {@code true}. Otherwise the {@link RuleVisitor}
 * returns {@code null}.
 * 
 * @author "Yevgeny Kazakov"
 *
 * @param <O>
 *            the type of output parameter with which this visitor works
 */
public class ConditionalRuleVisitor<O> implements RuleVisitor<O> {

	private final RuleVisitor<Boolean> condition_;

	private final RuleVisitor<O> visitor_;

	public ConditionalRuleVisitor(RuleVisitor<O> visitor,
			RuleVisitor<Boolean> condition) {
		this.visitor_ = visitor;
		this.condition_ = condition;
	}

	@Override
	public O visit(BackwardLinkChainFromBackwardLinkRule rule,
			BackwardLink premise, ContextPremises premises,
			ClassInferenceProducer producer) {
		if (condition_.visit(rule, premise, premises, producer))
			return visitor_.visit(rule, premise, premises, producer);
		// else
		return null;
	}

	@Override
	public O visit(BackwardLinkFromForwardLinkRule rule, ForwardLink premise,
			ContextPremises premises, ClassInferenceProducer producer) {
		if (condition_.visit(rule, premise, premises, producer))
			return visitor_.visit(rule, premise, premises, producer);
		// else
		return null;
	}

	@Override
	public O visit(ComposedFromDecomposedSubsumerRule rule,
			IndexedClassExpression premise, ContextPremises premises,
			ClassInferenceProducer producer) {
		if (condition_.visit(rule, premise, premises, producer))
			return visitor_.visit(rule, premise, premises, producer);
		// else
		return null;
	}

	@Override
	public O visit(ContradictionCompositionRule rule, DisjointSubsumer premise,
			ContextPremises premises, ClassInferenceProducer producer) {
		if (condition_.visit(rule, premise, premises, producer))
			return visitor_.visit(rule, premise, premises, producer);
		// else
		return null;
	}

	@Override
	public O visit(ContradictionFromNegationRule rule,
			IndexedClassExpression premise, ContextPremises premises,
			ClassInferenceProducer producer) {
		if (condition_.visit(rule, premise, premises, producer))
			return visitor_.visit(rule, premise, premises, producer);
		// else
		return null;
	}

	@Override
	public O visit(ContradictionFromOwlNothingRule rule,
			IndexedClassExpression premise, ContextPremises premises,
			ClassInferenceProducer producer) {
		if (condition_.visit(rule, premise, premises, producer))
			return visitor_.visit(rule, premise, premises, producer);
		// else
		return null;
	}

	@Override
	public O visit(ContradictionOverBackwardLinkRule rule,
			BackwardLink premise, ContextPremises premises,
			ClassInferenceProducer producer) {
		if (condition_.visit(rule, premise, premises, producer))
			return visitor_.visit(rule, premise, premises, producer);
		// else
		return null;
	}

	@Override
	public O visit(ContradictionPropagationRule rule, ClassInconsistency premise,
			ContextPremises premises, ClassInferenceProducer producer) {
		if (condition_.visit(rule, premise, premises, producer))
			return visitor_.visit(rule, premise, premises, producer);
		// else
		return null;
	}

	@Override
	public O visit(DisjointSubsumerFromMemberRule rule,
			IndexedClassExpression premise, ContextPremises premises,
			ClassInferenceProducer producer) {
		if (condition_.visit(rule, premise, premises, producer))
			return visitor_.visit(rule, premise, premises, producer);
		// else
		return null;
	}

	@Override
	public O visit(EquivalentClassFirstFromSecondRule rule,
			IndexedClassExpression premise, ContextPremises premises,
			ClassInferenceProducer producer) {
		if (condition_.visit(rule, premise, premises, producer))
			return visitor_.visit(rule, premise, premises, producer);
		// else
		return null;
	}

	@Override
	public O visit(EquivalentClassSecondFromFirstRule rule,
			IndexedClassExpression premise, ContextPremises premises,
			ClassInferenceProducer producer) {
		if (condition_.visit(rule, premise, premises, producer))
			return visitor_.visit(rule, premise, premises, producer);
		// else
		return null;
	}

	@Override
	public O visit(IndexedClassDecompositionRule rule, IndexedClass premise,
			ContextPremises premises, ClassInferenceProducer producer) {
		if (condition_.visit(rule, premise, premises, producer))
			return visitor_.visit(rule, premise, premises, producer);
		// else
		return null;
	}

	@Override
	public O visit(IndexedClassFromDefinitionRule rule,
			IndexedClassExpression premise, ContextPremises premises,
			ClassInferenceProducer producer) {
		if (condition_.visit(rule, premise, premises, producer))
			return visitor_.visit(rule, premise, premises, producer);
		// else
		return null;
	}

	@Override
	public O visit(IndexedObjectComplementOfDecomposition rule,
			IndexedObjectComplementOf premise, ContextPremises premises,
			ClassInferenceProducer producer) {
		if (condition_.visit(rule, premise, premises, producer))
			return visitor_.visit(rule, premise, premises, producer);
		// else
		return null;
	}

	@Override
	public O visit(IndexedObjectHasSelfDecomposition rule,
			IndexedObjectHasSelf premise, ContextPremises premises,
			ClassInferenceProducer producer) {
		if (condition_.visit(rule, premise, premises, producer))
			return visitor_.visit(rule, premise, premises, producer);
		// else
		return null;
	}

	@Override
	public O visit(IndexedObjectIntersectionOfDecomposition rule,
			IndexedObjectIntersectionOf premise, ContextPremises premises,
			ClassInferenceProducer producer) {
		if (condition_.visit(rule, premise, premises, producer))
			return visitor_.visit(rule, premise, premises, producer);
		// else
		return null;
	}

	@Override
	public O visit(IndexedObjectSomeValuesFromDecomposition rule,
			IndexedObjectSomeValuesFrom premise, ContextPremises premises,
			ClassInferenceProducer producer) {
		if (condition_.visit(rule, premise, premises, producer))
			return visitor_.visit(rule, premise, premises, producer);
		// else
		return null;
	}

	@Override
	public O visit(NonReflexiveBackwardLinkCompositionRule rule,
			ForwardLink premise, ContextPremises premises,
			ClassInferenceProducer producer) {
		if (condition_.visit(rule, premise, premises, producer))
			return visitor_.visit(rule, premise, premises, producer);
		// else
		return null;
	}

	@Override
	public O visit(ObjectIntersectionFromFirstConjunctRule rule,
			IndexedClassExpression premise, ContextPremises premises,
			ClassInferenceProducer producer) {
		if (condition_.visit(rule, premise, premises, producer))
			return visitor_.visit(rule, premise, premises, producer);
		// else
		return null;
	}

	@Override
	public O visit(ObjectIntersectionFromSecondConjunctRule rule,
			IndexedClassExpression premise, ContextPremises premises,
			ClassInferenceProducer producer) {
		if (condition_.visit(rule, premise, premises, producer))
			return visitor_.visit(rule, premise, premises, producer);
		// else
		return null;
	}

	@Override
	public O visit(ObjectUnionFromDisjunctRule rule,
			IndexedClassExpression premise, ContextPremises premises,
			ClassInferenceProducer producer) {
		if (condition_.visit(rule, premise, premises, producer))
			return visitor_.visit(rule, premise, premises, producer);
		// else
		return null;
	}

	@Override
	public O visit(OwlThingContextInitRule rule, ContextInitialization premise,
			ContextPremises premises, ClassInferenceProducer producer) {
		if (condition_.visit(rule, premise, premises, producer))
			return visitor_.visit(rule, premise, premises, producer);
		// else
		return null;
	}

	@Override
	public O visit(PropagationFromExistentialFillerRule rule,
			IndexedClassExpression premise, ContextPremises premises,
			ClassInferenceProducer producer) {
		if (condition_.visit(rule, premise, premises, producer))
			return visitor_.visit(rule, premise, premises, producer);
		// else
		return null;
	}

	@Override
	public O visit(PropagationInitializationRule rule,
			SubContextInitialization premise, ContextPremises premises,
			ClassInferenceProducer producer) {
		if (condition_.visit(rule, premise, premises, producer))
			return visitor_.visit(rule, premise, premises, producer);
		// else
		return null;
	}

	@Override
	public O visit(ReflexiveBackwardLinkCompositionRule rule,
			ForwardLink premise, ContextPremises premises,
			ClassInferenceProducer producer) {
		if (condition_.visit(rule, premise, premises, producer))
			return visitor_.visit(rule, premise, premises, producer);
		// else
		return null;
	}

	@Override
	public O visit(RootContextInitializationRule rule,
			ContextInitialization premise, ContextPremises premises,
			ClassInferenceProducer producer) {
		if (condition_.visit(rule, premise, premises, producer))
			return visitor_.visit(rule, premise, premises, producer);
		// else
		return null;
	}

	@Override
	public O visit(SubsumerBackwardLinkRule rule, BackwardLink premise,
			ContextPremises premises, ClassInferenceProducer producer) {
		if (condition_.visit(rule, premise, premises, producer))
			return visitor_.visit(rule, premise, premises, producer);
		// else
		return null;
	}

	@Override
	public O visit(SubsumerPropagationRule rule, Propagation premise,
			ContextPremises premises, ClassInferenceProducer producer) {
		if (condition_.visit(rule, premise, premises, producer))
			return visitor_.visit(rule, premise, premises, producer);
		// else
		return null;
	}

	@Override
	public O visit(SuperClassFromSubClassRule rule,
			IndexedClassExpression premise, ContextPremises premises,
			ClassInferenceProducer producer) {
		if (condition_.visit(rule, premise, premises, producer))
			return visitor_.visit(rule, premise, premises, producer);
		// else
		return null;
	}

}
