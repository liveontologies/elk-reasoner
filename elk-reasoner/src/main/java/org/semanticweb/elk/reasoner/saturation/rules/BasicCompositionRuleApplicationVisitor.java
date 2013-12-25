/**
 * 
 */
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
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass.OwlThingContextInitializationRule;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointnessAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointnessAxiom.ThisCompositionRule;
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
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class BasicCompositionRuleApplicationVisitor implements
		CompositionRuleVisitor {

	@Override
	public void visit(BackwardLink.ThisCompositionRule rule,
			SaturationStateWriter writer, Context context) {
		rule.apply(writer, context);

	}

	@Override
	public void visit(RootContextInitializationRule rule,
			SaturationStateWriter writer, Context context) {
		rule.apply(writer, context);
	}

	@Override
	public void visit(Contradiction.ContradictionPropagationRule rule,
			SaturationStateWriter writer, Context context) {
		rule.apply(writer, context);

	}

	@Override
	public void visit(Contradiction.ContradictionBackwardLinkRule rule,
			SaturationStateWriter writer, BackwardLink backwardLink) {
		rule.apply(writer, backwardLink);
	}

	@Override
	public void visit(DisjointSubsumer.ContradicitonCompositionRule rule,
			SaturationStateWriter writer, Context context) {
		rule.apply(writer, context);
	}

	@Override
	public void visit(ForwardLink.ThisBackwardLinkRule rule,
			SaturationStateWriter writer, BackwardLink backwardLink) {
		rule.apply(writer, backwardLink);
	}

	@Override
	public void visit(ForwardLink.BackwardLinkCompositionRule rule,
			SaturationStateWriter writer, Context context) {
		rule.apply(writer, context);

	}

	@Override
	public void visit(
			IndexedDisjointnessAxiom.ContradictionCompositionRule rule,
			SaturationStateWriter writer, Context context) {
		rule.apply(writer, context);
	}

	@Override
	public void visit(
			IndexedObjectComplementOf.ContradictionCompositionRule rule,
			SaturationStateWriter writer, Context context) {
		rule.apply(writer, context);
	}

	@Override
	public void visit(IndexedObjectIntersectionOf.ThisCompositionRule rule,
			SaturationStateWriter writer, Context context) {
		rule.apply(writer, context);
	}

	@Override
	public void visit(IndexedObjectSomeValuesFrom.PropagationCompositionRule rule,
			SaturationStateWriter writer, Context context) {
		rule.apply(writer, context);
	}

	@Override
	public void visit(IndexedObjectUnionOf.ThisCompositionRule rule,
			SaturationStateWriter writer, Context context) {
		rule.apply(writer, context);
	}

	@Override
	public void visit(IndexedSubClassOfAxiom.SubsumerCompositionRule rule,
			SaturationStateWriter writer, Context context) {
		rule.apply(writer, context);
	}

	@Override
	public void visit(OwlThingContextInitializationRule rule,
			SaturationStateWriter writer, Context context) {
		rule.apply(writer, context);
	}

	@Override
	public void visit(Propagation.SubsumerBackwardLinkRule rule,
			SaturationStateWriter writer, BackwardLink backwardLink) {
		rule.apply(writer, backwardLink);
	}

	@Override
	public void visit(ThisCompositionRule rule, SaturationStateWriter writer,
			Context context) {
		rule.apply(writer, context);
	}

}
