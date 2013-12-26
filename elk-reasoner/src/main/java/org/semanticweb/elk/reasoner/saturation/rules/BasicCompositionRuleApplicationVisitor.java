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
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class BasicCompositionRuleApplicationVisitor implements
		CompositionRuleVisitor {

	@Override
	public void visit(BackwardLink.ThisCompositionRule rule,
			BackwardLink premise, Context context, SaturationStateWriter writer) {
		rule.apply(premise, context, writer);

	}

	@Override
	public void visit(Contradiction.ContradictionBackwardLinkRule rule,
			BackwardLink premise, Context context, SaturationStateWriter writer) {
		rule.apply(premise, context, writer);
	}

	@Override
	public void visit(Contradiction.ContradictionPropagationRule rule,
			Contradiction premise, Context context, SaturationStateWriter writer) {
		rule.apply(premise, context, writer);

	}

	@Override
	public void visit(DisjointSubsumer.ContradicitonCompositionRule rule,
			DisjointSubsumer premise, Context context,
			SaturationStateWriter writer) {
		rule.apply(premise, context, writer);
	}

	@Override
	public void visit(ForwardLink.BackwardLinkCompositionRule rule,
			ForwardLink premise, Context context, SaturationStateWriter writer) {
		rule.apply(premise, context, writer);
	}

	@Override
	public void visit(ForwardLink.ThisBackwardLinkRule rule,
			BackwardLink premise, Context context, SaturationStateWriter writer) {
		rule.apply(premise, context, writer);
	}

	@Override
	public void visit(ContradictionFromDisjointnessRule rule,
			IndexedClassExpression premise, Context context,
			SaturationStateWriter writer) {
		rule.apply(premise, context, writer);
	}

	@Override
	public void visit(DisjointSubsumerFromMemberRule rule,
			IndexedClassExpression premise, Context context,
			SaturationStateWriter writer) {
		rule.apply(premise, context, writer);
	}

	@Override
	public void visit(ContradictionFromNegationRule rule,
			IndexedClassExpression premise, Context context,
			SaturationStateWriter writer) {
		rule.apply(premise, context, writer);
	}

	@Override
	public void visit(ObjectIntersectionFromConjunctRule rule,
			IndexedClassExpression premise, Context context,
			SaturationStateWriter writer) {
		rule.apply(premise, context, writer);
	}

	@Override
	public void visit(PropagationFromExistentialFillerRule rule,
			IndexedClassExpression premise, Context context,
			SaturationStateWriter writer) {
		rule.apply(premise, context, writer);
	}

	@Override
	public void visit(ObjectUnionFromDisjunctRule rule,
			IndexedClassExpression premise, Context context,
			SaturationStateWriter writer) {
		rule.apply(premise, context, writer);
	}

	@Override
	public void visit(SuperClassFromSubClassRule rule,
			IndexedClassExpression premise, Context context,
			SaturationStateWriter writer) {
		rule.apply(premise, context, writer);
	}

	@Override
	public void visit(OwlThingContextInitializationRule rule, Context context,
			SaturationStateWriter writer) {
		rule.apply(null, context, writer);
	}

	@Override
	public void visit(Propagation.SubsumerBackwardLinkRule rule,
			BackwardLink premise, Context context, SaturationStateWriter writer) {
		rule.apply(premise, context, writer);
	}

	@Override
	public void visit(RootContextInitializationRule rule, Context context,
			SaturationStateWriter writer) {
		rule.apply(null, context, writer);
	}

}
