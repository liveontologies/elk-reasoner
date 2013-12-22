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

import org.semanticweb.elk.reasoner.indexing.hierarchy.DirectIndex.ContextRootInitializationRule;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointnessAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectComplementOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectUnionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.util.logging.CachedTimeThread;

/**
 * A {@link RuleApplicationVisitor} wrapper for a given
 * {@link RuleApplicationVisitor} that additionally records the time spend
 * within methods in the given {@link RuleApplicationTimer}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class RuleApplicationTimerVisitor implements RuleApplicationVisitor {

	/**
	 * the visitor whose methods to be timed
	 */
	private final RuleApplicationVisitor visitor_;

	/**
	 * timer used to time the visitor
	 */
	private final RuleApplicationTimer timer_;

	/**
	 * Creates a new {@link SubsumerDecompositionVisitor} that executes
	 * the corresponding methods of the given
	 * {@link SubsumerDecompositionVisitor} and measures the time spent
	 * within the corresponding methods using the given
	 * {@link RuleApplicationTimer}.
	 * 
	 * @param visitor
	 *            the {@link SubsumerDecompositionVisitor} used to
	 *            execute the methods
	 * @param timer
	 *            the {@link RuleApplicationTimer} used to mesure the time spent
	 *            within the methods
	 */
	public RuleApplicationTimerVisitor(RuleApplicationVisitor visitor,
			RuleApplicationTimer timer) {
		this.timer_ = timer;
		this.visitor_ = visitor;
	}

	@Override
	public void visit(
			IndexedClass.OwlThingContextInitializationRule owlThingContextInitializationRule,
			SaturationStateWriter writer, Context context) {
		timer_.timeOwlThingContextInitializationRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(owlThingContextInitializationRule, writer, context);
		timer_.timeOwlThingContextInitializationRule += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public void visit(
			IndexedDisjointnessAxiom.ThisCompositionRule thisCompositionRule,
			SaturationStateWriter writer, Context context) {
		timer_.timeDisjointnessAxiomCompositionRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(thisCompositionRule, writer, context);
		timer_.timeDisjointnessAxiomCompositionRule += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public void visit(
			IndexedDisjointnessAxiom.ThisContradictionRule thisContradictionRule,
			SaturationStateWriter writer, Context context) {
		timer_.timeDisjointnessAxiomContradictionRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(thisContradictionRule, writer, context);
		timer_.timeDisjointnessAxiomContradictionRule += CachedTimeThread
				.getCurrentTimeMillis();

	}

	@Override
	public void visit(
			IndexedObjectComplementOf.ThisCompositionRule thisCompositionRule,
			SaturationStateWriter writer, Context context) {
		timer_.timeObjectComplementOfCompositionRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(thisCompositionRule, writer, context);
		timer_.timeObjectComplementOfCompositionRule += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public void visit(
			IndexedObjectIntersectionOf.ThisCompositionRule thisCompositionRule,
			SaturationStateWriter writer, Context context) {
		timer_.timeObjectIntersectionOfCompositionRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(thisCompositionRule, writer, context);
		timer_.timeObjectIntersectionOfCompositionRule += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public void visit(
			IndexedSubClassOfAxiom.ThisCompositionRule thisCompositionRule,
			SaturationStateWriter writer, Context context) {
		timer_.timeSubClassOfAxiomCompositionRule -= CachedTimeThread
				.getCurrentTimeMillis();
		// long ts = System.nanoTime();

		visitor_.visit(thisCompositionRule, writer, context);
		timer_.timeSubClassOfAxiomCompositionRule += CachedTimeThread
				.getCurrentTimeMillis();

		// timer_.timeSubClassOfAxiomCompositionRule += (System.nanoTime() -
		// ts);
	}

	@Override
	public void visit(
			IndexedObjectSomeValuesFrom.ThisCompositionRule thisCompositionRule,
			SaturationStateWriter writer, Context context) {
		timer_.timeObjectSomeValuesFromCompositionRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(thisCompositionRule, writer, context);
		timer_.timeObjectSomeValuesFromCompositionRule += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public void visit(
			IndexedObjectUnionOf.ThisCompositionRule thisCompositionRule,
			SaturationStateWriter writer, Context context) {
		timer_.timeObjectUnionOfCompositionRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(thisCompositionRule, writer, context);
		timer_.timeObjectUnionOfCompositionRule += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public void visit(ForwardLink.ThisBackwardLinkRule thisBackwardLinkRule,
			SaturationStateWriter writer, BackwardLink backwardLink) {
		timer_.timeForwardLinkBackwardLinkRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(thisBackwardLinkRule, writer, backwardLink);
		timer_.timeForwardLinkBackwardLinkRule += CachedTimeThread
				.getCurrentTimeMillis();

	}

	@Override
	public void visit(Propagation.ThisBackwardLinkRule thisBackwardLinkRule,
			SaturationStateWriter writer, BackwardLink backwardLink) {
		timer_.timePropagationBackwardLinkRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(thisBackwardLinkRule, writer, backwardLink);
		timer_.timePropagationBackwardLinkRule += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public void visit(
			Contradiction.ContradictionBackwardLinkRule bottomBackwardLinkRule,
			SaturationStateWriter writer, BackwardLink backwardLink) {
		timer_.timeContradictionBottomBackwardLinkRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(bottomBackwardLinkRule, writer, backwardLink);
		timer_.timeContradictionBottomBackwardLinkRule += CachedTimeThread
				.getCurrentTimeMillis();
	}

	@Override
	public void visit(ContextRootInitializationRule rootInitRule,
			SaturationStateWriter writer, Context context) {
		timer_.timeContextRootInitializationRule -= CachedTimeThread
				.getCurrentTimeMillis();
		visitor_.visit(rootInitRule, writer, context);
		timer_.timeContextRootInitializationRule += CachedTimeThread
				.getCurrentTimeMillis();
	}

}
