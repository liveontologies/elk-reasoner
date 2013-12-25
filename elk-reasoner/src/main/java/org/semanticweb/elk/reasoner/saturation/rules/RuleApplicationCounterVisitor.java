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
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointnessAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectComplementOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectUnionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.reasoner.saturation.context.Context;

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
	public void visit(BackwardLink.ThisCompositionRule thisCompositionRule,
			BackwardLink premise, Context context, SaturationStateWriter writer) {
		counter_.countBackwardLinkCompositionRule++;
		visitor_.visit(thisCompositionRule, premise, context, writer);
	}

	@Override
	public void visit(
			Contradiction.ContradictionBackwardLinkRule bottomBackwardLinkRule,
			BackwardLink premise, Context context, SaturationStateWriter writer) {
		counter_.countContradictionBottomBackwardLinkRule++;
		visitor_.visit(bottomBackwardLinkRule, premise, context, writer);
	}

	@Override
	public void visit(
			Contradiction.ContradictionPropagationRule thisCompositionRule,
			Contradiction premise, Context context, SaturationStateWriter writer) {
		counter_.countContradictionCompositionRule++;
		visitor_.visit(thisCompositionRule, premise, context, writer);
	}

	@Override
	public void visit(
			DisjointSubsumer.ContradicitonCompositionRule thisCompositionRule,
			DisjointSubsumer premise, Context context,
			SaturationStateWriter writer) {
		counter_.countDisjointnessAxiomCompositionRule++;
		visitor_.visit(thisCompositionRule, premise, context, writer);
	}

	@Override
	public void visit(
			ForwardLink.BackwardLinkCompositionRule thisCompositionRule,
			ForwardLink premise, Context context, SaturationStateWriter writer) {
		counter_.countForwardLinkCompositionRule++;
		visitor_.visit(thisCompositionRule, premise, context, writer);
	}

	@Override
	public void visit(ForwardLink.ThisBackwardLinkRule thisBackwardLinkRule,
			BackwardLink premise, Context context, SaturationStateWriter writer) {
		counter_.countForwardLinkBackwardLinkRule++;
		visitor_.visit(thisBackwardLinkRule, premise, context, writer);

	}

	@Override
	public void visit(
			IndexedClass.OwlThingContextInitializationRule owlThingContextInitializationRule,
			Context context, SaturationStateWriter writer) {
		counter_.countOwlThingContextInitializationRule++;
		visitor_.visit(owlThingContextInitializationRule, context, writer);
	}

	@Override
	public void visit(
			IndexedDisjointnessAxiom.ContradictionCompositionRule thisContradictionRule,
			IndexedClassExpression premise, Context context,
			SaturationStateWriter writer) {
		counter_.countIndexedDisjointnessAxiomContradictionRule++;
		visitor_.visit(thisContradictionRule, premise, context, writer);
	}

	@Override
	public void visit(
			IndexedDisjointnessAxiom.ThisCompositionRule thisCompositionRule,
			IndexedClassExpression premise, Context context,
			SaturationStateWriter writer) {
		counter_.countIndexedDisjointnessAxiomCompositionRule++;
		visitor_.visit(thisCompositionRule, premise, context, writer);
	}

	@Override
	public void visit(
			IndexedObjectComplementOf.ContradictionCompositionRule thisCompositionRule,
			IndexedClassExpression premise, Context context,
			SaturationStateWriter writer) {
		counter_.countIndexedObjectComplementOfCompositionRule++;
		visitor_.visit(thisCompositionRule, premise, context, writer);
	}

	@Override
	public void visit(
			IndexedObjectIntersectionOf.ThisCompositionRule thisCompositionRule,
			IndexedClassExpression premise, Context context,
			SaturationStateWriter writer) {
		counter_.countIndexedObjectIntersectionOfCompositionRule++;
		visitor_.visit(thisCompositionRule, premise, context, writer);
	}

	@Override
	public void visit(
			IndexedObjectSomeValuesFrom.PropagationCompositionRule thisCompositionRule,
			IndexedClassExpression premise, Context context,
			SaturationStateWriter writer) {
		counter_.countIndexedObjectSomeValuesFromCompositionRule++;
		visitor_.visit(thisCompositionRule, premise, context, writer);
	}

	@Override
	public void visit(
			IndexedObjectUnionOf.ThisCompositionRule thisCompositionRule,
			IndexedClassExpression premise, Context context,
			SaturationStateWriter writer) {
		counter_.countIndexedObjectUnionOfCompositionRule++;
		visitor_.visit(thisCompositionRule, premise, context, writer);
	}

	@Override
	public void visit(
			IndexedSubClassOfAxiom.SubsumerCompositionRule thisCompositionRule,
			IndexedClassExpression premise, Context context,
			SaturationStateWriter writer) {
		counter_.countIndexedSubClassOfAxiomCompositionRule++;
		visitor_.visit(thisCompositionRule, premise, context, writer);
	}

	@Override
	public void visit(
			Propagation.SubsumerBackwardLinkRule thisBackwardLinkRule,
			BackwardLink premise, Context context, SaturationStateWriter writer) {
		counter_.countPropagationBackwardLinkRule++;
		visitor_.visit(thisBackwardLinkRule, premise, context, writer);
	}

	@Override
	public void visit(RootContextInitializationRule rootInitRule,
			Context context, SaturationStateWriter writer) {
		counter_.countContextRootInitializationRule++;
		visitor_.visit(rootInitRule, context, writer);
	}

}
