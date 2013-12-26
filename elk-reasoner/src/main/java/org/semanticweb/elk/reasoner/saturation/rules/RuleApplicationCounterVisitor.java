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

import org.semanticweb.elk.reasoner.indexing.hierarchy.DirectIndex.RootContextInitializationRule;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ContradictionFromDisjointnessRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ContradictionFromNegationRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.DisjointSubsumerFromMemberRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ObjectIntersectionFromConjunctRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.PropagationFromExistentialFillerRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ObjectUnionFromDisjunctRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.SuperClassFromSubClassRule;

/**
 * A {@link CompositionRuleVisitor} wrapper for a given
 * {@link CompositionRuleVisitor} that additionally records the number of
 * invocations of the methods using the given {@link RuleApplicationCounter}.
 * 
 * @author "Yevgeny Kazakov"
 */
public class RuleApplicationCounterVisitor implements CompositionRuleVisitor {

	/**
	 * the visitor whose method applications to be counted
	 */
	private final CompositionRuleVisitor visitor_;
	/**
	 * the counter used to count the number of method applications of the
	 * visitor
	 */
	private final RuleApplicationCounter counter_;

	/**
	 * Creates a new {@link SubsumerDecompositionVisitor} that executes the
	 * corresponding methods of the given {@link SubsumerDecompositionVisitor}
	 * and counts the number of invocations of the corresponding methods using
	 * the given {@link RuleApplicationCounter}.
	 * 
	 * @param visitor
	 *            the {@link SubsumerDecompositionVisitor} used to execute the
	 *            methods
	 * @param counter
	 *            the {@link RuleApplicationCounter} used to count the number of
	 *            method invocations
	 */
	public RuleApplicationCounterVisitor(CompositionRuleVisitor visitor,
			RuleApplicationCounter counter) {
		this.visitor_ = visitor;
		this.counter_ = counter;
	}

	@Override
	public void visit(BackwardLink.ThisCompositionRule rule,
			BackwardLink premise, Context context, SaturationStateWriter writer) {
		counter_.countBackwardLinkCompositionRule++;
		visitor_.visit(rule, premise, context, writer);
	}

	@Override
	public void visit(Contradiction.ContradictionBackwardLinkRule rule,
			BackwardLink premise, Context context, SaturationStateWriter writer) {
		counter_.countContradictionBottomBackwardLinkRule++;
		visitor_.visit(rule, premise, context, writer);
	}

	@Override
	public void visit(Contradiction.ContradictionPropagationRule rule,
			Contradiction premise, Context context, SaturationStateWriter writer) {
		counter_.countContradictionCompositionRule++;
		visitor_.visit(rule, premise, context, writer);
	}

	@Override
	public void visit(DisjointSubsumer.ContradicitonCompositionRule rule,
			DisjointSubsumer premise, Context context,
			SaturationStateWriter writer) {
		counter_.countDisjointnessAxiomCompositionRule++;
		visitor_.visit(rule, premise, context, writer);
	}

	@Override
	public void visit(ForwardLink.BackwardLinkCompositionRule rule,
			ForwardLink premise, Context context, SaturationStateWriter writer) {
		counter_.countForwardLinkCompositionRule++;
		visitor_.visit(rule, premise, context, writer);
	}

	@Override
	public void visit(ForwardLink.ThisBackwardLinkRule rule,
			BackwardLink premise, Context context, SaturationStateWriter writer) {
		counter_.countForwardLinkBackwardLinkRule++;
		visitor_.visit(rule, premise, context, writer);

	}

	@Override
	public void visit(IndexedClass.OwlThingContextInitializationRule rule,
			Context context, SaturationStateWriter writer) {
		counter_.countOwlThingContextInitializationRule++;
		visitor_.visit(rule, context, writer);
	}

	@Override
	public void visit(ContradictionFromDisjointnessRule rule,
			IndexedClassExpression premise, Context context,
			SaturationStateWriter writer) {
		counter_.countIndexedDisjointnessAxiomContradictionRule++;
		visitor_.visit(rule, premise, context, writer);
	}

	@Override
	public void visit(DisjointSubsumerFromMemberRule rule,
			IndexedClassExpression premise, Context context,
			SaturationStateWriter writer) {
		counter_.countIndexedDisjointnessAxiomCompositionRule++;
		visitor_.visit(rule, premise, context, writer);
	}

	@Override
	public void visit(ContradictionFromNegationRule rule,
			IndexedClassExpression premise, Context context,
			SaturationStateWriter writer) {
		counter_.countIndexedObjectComplementOfCompositionRule++;
		visitor_.visit(rule, premise, context, writer);
	}

	@Override
	public void visit(ObjectIntersectionFromConjunctRule rule,
			IndexedClassExpression premise, Context context,
			SaturationStateWriter writer) {
		counter_.countIndexedObjectIntersectionOfCompositionRule++;
		visitor_.visit(rule, premise, context, writer);
	}

	@Override
	public void visit(PropagationFromExistentialFillerRule rule,
			IndexedClassExpression premise, Context context,
			SaturationStateWriter writer) {
		counter_.countIndexedObjectSomeValuesFromCompositionRule++;
		visitor_.visit(rule, premise, context, writer);
	}

	@Override
	public void visit(ObjectUnionFromDisjunctRule rule,
			IndexedClassExpression premise, Context context,
			SaturationStateWriter writer) {
		counter_.countIndexedObjectUnionOfCompositionRule++;
		visitor_.visit(rule, premise, context, writer);
	}

	@Override
	public void visit(SuperClassFromSubClassRule rule,
			IndexedClassExpression premise, Context context,
			SaturationStateWriter writer) {
		counter_.countIndexedSubClassOfAxiomCompositionRule++;
		visitor_.visit(rule, premise, context, writer);
	}

	@Override
	public void visit(Propagation.SubsumerBackwardLinkRule rule,
			BackwardLink premise, Context context, SaturationStateWriter writer) {
		counter_.countPropagationBackwardLinkRule++;
		visitor_.visit(rule, premise, context, writer);
	}

	@Override
	public void visit(RootContextInitializationRule rule, Context context,
			SaturationStateWriter writer) {
		counter_.countContextRootInitializationRule++;
		visitor_.visit(rule, context, writer);
	}

}
