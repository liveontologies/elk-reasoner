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
import org.semanticweb.elk.util.logging.CachedTimeThread;

/**
 * A {@link CompositionRuleVisitor} wrapper for a given
 * {@link CompositionRuleVisitor} that additionally records the time spend
 * within methods in the given {@link RuleApplicationTimer}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class RuleApplicationTimerVisitor implements CompositionRuleVisitor {

	/**
	 * the visitor whose methods to be timed
	 */
	private final CompositionRuleVisitor visitor_;

	/**
	 * timer used to time the visitor
	 */
	private final RuleApplicationTimer timer_;

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
	public RuleApplicationTimerVisitor(CompositionRuleVisitor visitor,
			RuleApplicationTimer timer) {
		this.timer_ = timer;
		this.visitor_ = visitor;
	}

	@Override
	public void visit(BackwardLink.ThisCompositionRule thisCompositionRule,
			BackwardLink premise, Context context, SaturationStateWriter writer) {
		timer_.timeBackwardLinkCompositionRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(thisCompositionRule, premise, context, writer);
		timer_.timeBackwardLinkCompositionRule += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public void visit(
			Contradiction.ContradictionBackwardLinkRule bottomBackwardLinkRule,
			BackwardLink premise, Context context, SaturationStateWriter writer) {
		timer_.timeContradictionBottomBackwardLinkRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(bottomBackwardLinkRule, premise, context, writer);
		timer_.timeContradictionBottomBackwardLinkRule += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public void visit(
			Contradiction.ContradictionPropagationRule thisCompositionRule,
			Contradiction premise, Context context, SaturationStateWriter writer) {
		timer_.timeContradictionCompositionRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(thisCompositionRule, premise, context, writer);
		timer_.timeContradictionCompositionRule += CachedTimeThread
				.getCurrentTimeMillis();

	}

	@Override
	public void visit(
			DisjointSubsumer.ContradicitonCompositionRule thisCompositionRule,
			DisjointSubsumer premise, Context context,
			SaturationStateWriter writer) {
		timer_.timeDisjointnessAxiomCompositionRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(thisCompositionRule, premise, context, writer);
		timer_.timeDisjointnessAxiomCompositionRule += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public void visit(
			ForwardLink.BackwardLinkCompositionRule thisCompositionRule,
			ForwardLink premise, Context context, SaturationStateWriter writer) {
		timer_.timeForwardLinkCompositionRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(thisCompositionRule, premise, context, writer);
		timer_.timeForwardLinkCompositionRule += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public void visit(ForwardLink.ThisBackwardLinkRule thisBackwardLinkRule,
			BackwardLink premise, Context context, SaturationStateWriter writer) {
		timer_.timeForwardLinkBackwardLinkRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(thisBackwardLinkRule, premise, context, writer);
		timer_.timeForwardLinkBackwardLinkRule += CachedTimeThread
				.getCurrentTimeMillis();

	}

	@Override
	public void visit(
			IndexedClass.OwlThingContextInitializationRule owlThingContextInitializationRule,
			Context context, SaturationStateWriter writer) {
		timer_.timeOwlThingContextInitializationRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(owlThingContextInitializationRule, context, writer);
		timer_.timeOwlThingContextInitializationRule += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public void visit(
			IndexedDisjointnessAxiom.ContradictionCompositionRule thisContradictionRule,
			IndexedClassExpression premise, Context context,
			SaturationStateWriter writer) {
		timer_.timeIndexedDisjointnessAxiomContradictionRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(thisContradictionRule, premise, context, writer);
		timer_.timeIndexedDisjointnessAxiomContradictionRule += CachedTimeThread
				.getCurrentTimeMillis();

	}

	@Override
	public void visit(
			IndexedDisjointnessAxiom.ThisCompositionRule thisCompositionRule,
			IndexedClassExpression premise, Context context,
			SaturationStateWriter writer) {
		timer_.timeIndexedDisjointnessAxiomCompositionRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(thisCompositionRule, premise, context, writer);
		timer_.timeIndexedDisjointnessAxiomCompositionRule += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public void visit(
			IndexedObjectComplementOf.ContradictionCompositionRule thisCompositionRule,
			IndexedClassExpression premise, Context context,
			SaturationStateWriter writer) {
		timer_.timeObjectIndexedComplementOfCompositionRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(thisCompositionRule, premise, context, writer);
		timer_.timeObjectIndexedComplementOfCompositionRule += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public void visit(
			IndexedObjectIntersectionOf.ThisCompositionRule thisCompositionRule,
			IndexedClassExpression premise, Context context,
			SaturationStateWriter writer) {
		timer_.timeObjectIndexedIntersectionOfCompositionRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(thisCompositionRule, premise, context, writer);
		timer_.timeObjectIndexedIntersectionOfCompositionRule += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public void visit(
			IndexedObjectSomeValuesFrom.PropagationCompositionRule thisCompositionRule,
			IndexedClassExpression premise, Context context,
			SaturationStateWriter writer) {
		timer_.timeIndexedObjectSomeValuesFromCompositionRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(thisCompositionRule, premise, context, writer);
		timer_.timeIndexedObjectSomeValuesFromCompositionRule += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public void visit(
			IndexedObjectUnionOf.ThisCompositionRule thisCompositionRule,
			IndexedClassExpression premise, Context context,
			SaturationStateWriter writer) {
		timer_.timeIndexedObjectUnionOfCompositionRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(thisCompositionRule, premise, context, writer);
		timer_.timeIndexedObjectUnionOfCompositionRule += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public void visit(
			IndexedSubClassOfAxiom.SubsumerCompositionRule thisCompositionRule,
			IndexedClassExpression premise, Context context,
			SaturationStateWriter writer) {
		timer_.timeIndexedSubClassOfAxiomCompositionRule -= CachedTimeThread
				.getCurrentTimeMillis();
		// long ts = System.nanoTime();

		visitor_.visit(thisCompositionRule, premise, context, writer);
		timer_.timeIndexedSubClassOfAxiomCompositionRule += CachedTimeThread
				.getCurrentTimeMillis();

		// timer_.timeSubClassOfAxiomCompositionRule += (System.nanoTime() -
		// ts);
	}

	@Override
	public void visit(
			Propagation.SubsumerBackwardLinkRule thisBackwardLinkRule,
			BackwardLink premise, Context context, SaturationStateWriter writer) {
		timer_.timePropagationBackwardLinkRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(thisBackwardLinkRule, premise, context, writer);
		timer_.timePropagationBackwardLinkRule += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public void visit(RootContextInitializationRule rootInitRule,
			Context context, SaturationStateWriter writer) {
		timer_.timeContextRootInitializationRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(rootInitRule, context, writer);
		timer_.timeContextRootInitializationRule += CachedTimeThread
				.getCurrentTimeMillis();
	}

}
