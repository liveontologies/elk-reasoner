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
import org.semanticweb.elk.reasoner.saturation.conclusions.DisjointnessAxiom;
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
			SaturationStateWriter writer, Context context) {
		counter_.countBackwardLinkCompositionRule++;
		visitor_.visit(thisCompositionRule, writer, context);
	}

	@Override
	public void visit(ContextRootInitializationRule rootInitRule,
			SaturationStateWriter writer, Context context) {
		counter_.countContextRootInitializationRule++;
		visitor_.visit(rootInitRule, writer, context);
	}

	@Override
	public void visit(
			Contradiction.ContradictionBackwardLinkRule bottomBackwardLinkRule,
			SaturationStateWriter writer, BackwardLink backwardLink) {
		counter_.countContradictionBottomBackwardLinkRule++;
		visitor_.visit(bottomBackwardLinkRule, writer, backwardLink);
	}

	@Override
	public void visit(Contradiction.ThisCompositionRule thisCompositionRule,
			SaturationStateWriter writer, Context context) {
		counter_.countContradictionCompositionRule++;
		visitor_.visit(thisCompositionRule, writer, context);
	}

	@Override
	public void visit(
			DisjointnessAxiom.ThisCompositionRule thisCompositionRule,
			SaturationStateWriter writer, Context context) {
		counter_.countDisjointnessAxiomCompositionRule++;
		visitor_.visit(thisCompositionRule, writer, context);
	}

	@Override
	public void visit(ForwardLink.ThisBackwardLinkRule thisBackwardLinkRule,
			SaturationStateWriter writer, BackwardLink backwardLink) {
		counter_.countForwardLinkBackwardLinkRule++;
		visitor_.visit(thisBackwardLinkRule, writer, backwardLink);

	}

	@Override
	public void visit(ForwardLink.ThisCompositionRule thisCompositionRule,
			SaturationStateWriter writer, Context context) {
		counter_.countForwardLinkCompositionRule++;
		visitor_.visit(thisCompositionRule, writer, context);
	}

	@Override
	public void visit(
			IndexedClass.OwlThingContextInitializationRule owlThingContextInitializationRule,
			SaturationStateWriter writer, Context context) {
		counter_.countOwlThingContextInitializationRule++;
		visitor_.visit(owlThingContextInitializationRule, writer, context);
	}

	@Override
	public void visit(
			IndexedDisjointnessAxiom.ThisCompositionRule thisCompositionRule,
			SaturationStateWriter writer, Context context) {
		counter_.countIndexedDisjointnessAxiomCompositionRule++;
		visitor_.visit(thisCompositionRule, writer, context);
	}

	@Override
	public void visit(
			IndexedDisjointnessAxiom.ThisContradictionRule thisContradictionRule,
			SaturationStateWriter writer, Context context) {
		counter_.countIndexedDisjointnessAxiomContradictionRule++;
		visitor_.visit(thisContradictionRule, writer, context);
	}

	@Override
	public void visit(
			IndexedObjectComplementOf.ThisCompositionRule thisCompositionRule,
			SaturationStateWriter writer, Context context) {
		counter_.countIndexedObjectComplementOfCompositionRule++;
		visitor_.visit(thisCompositionRule, writer, context);
	}

	@Override
	public void visit(
			IndexedObjectIntersectionOf.ThisCompositionRule thisCompositionRule,
			SaturationStateWriter writer, Context context) {
		counter_.countIndexedObjectIntersectionOfCompositionRule++;
		visitor_.visit(thisCompositionRule, writer, context);
	}

	@Override
	public void visit(
			IndexedObjectSomeValuesFrom.ThisCompositionRule thisCompositionRule,
			SaturationStateWriter writer, Context context) {
		counter_.countIndexedObjectSomeValuesFromCompositionRule++;
		visitor_.visit(thisCompositionRule, writer, context);
	}

	@Override
	public void visit(
			IndexedObjectUnionOf.ThisCompositionRule thisCompositionRule,
			SaturationStateWriter writer, Context context) {
		counter_.countIndexedObjectUnionOfCompositionRule++;
		visitor_.visit(thisCompositionRule, writer, context);
	}

	@Override
	public void visit(
			IndexedSubClassOfAxiom.ThisCompositionRule thisCompositionRule,
			SaturationStateWriter writer, Context context) {
		counter_.countIndexedSubClassOfAxiomCompositionRule++;
		visitor_.visit(thisCompositionRule, writer, context);
	}

	@Override
	public void visit(Propagation.ThisBackwardLinkRule thisBackwardLinkRule,
			SaturationStateWriter writer, BackwardLink backwardLink) {
		counter_.countPropagationBackwardLinkRule++;
		visitor_.visit(thisBackwardLinkRule, writer, backwardLink);
	}

}
