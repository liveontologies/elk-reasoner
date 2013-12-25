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

import org.semanticweb.elk.reasoner.indexing.hierarchy.DirectIndex;
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
 * A visitor pattern for all types of composition rules together with the
 * parameters for which these rules are applied.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 */
public interface CompositionRuleVisitor {

	void visit(BackwardLink.ThisCompositionRule thisCompositionRule,
			BackwardLink premise, Context context, SaturationStateWriter writer);

	void visit(Contradiction.ContradictionBackwardLinkRule rule,
			BackwardLink premise, Context context, SaturationStateWriter writer);

	void visit(Contradiction.ContradictionPropagationRule rule,
			Contradiction premise, Context context, SaturationStateWriter writer);

	void visit(DirectIndex.RootContextInitializationRule rootInitRule,
			Context context, SaturationStateWriter writer);

	void visit(DisjointSubsumer.ContradicitonCompositionRule rule,
			DisjointSubsumer premise, Context context,
			SaturationStateWriter writer);

	void visit(ForwardLink.ThisBackwardLinkRule thisBackwardLinkRule,
			BackwardLink premise, Context context, SaturationStateWriter writer);

	void visit(ForwardLink.BackwardLinkCompositionRule thisCompositionRule,
			ForwardLink premise, Context context, SaturationStateWriter writer);

	void visit(
			IndexedClass.OwlThingContextInitializationRule owlThingContextInitializationRule,
			Context context, SaturationStateWriter writer);

	void visit(
			IndexedDisjointnessAxiom.ContradictionCompositionRule thisContradictionRule,
			IndexedClassExpression premise, Context context,
			SaturationStateWriter writer);

	void visit(
			IndexedDisjointnessAxiom.ThisCompositionRule thisCompositionRule,
			IndexedClassExpression premise, Context context,
			SaturationStateWriter writer);

	void visit(
			IndexedObjectComplementOf.ContradictionCompositionRule thisCompositionRule,
			IndexedClassExpression premise, Context context,
			SaturationStateWriter writer);

	void visit(
			IndexedObjectIntersectionOf.ThisCompositionRule thisCompositionRule,
			IndexedClassExpression premise, Context context,
			SaturationStateWriter writer);

	void visit(
			IndexedObjectSomeValuesFrom.PropagationCompositionRule thisCompositionRule,
			IndexedClassExpression premise, Context context,
			SaturationStateWriter writer);

	void visit(IndexedObjectUnionOf.ThisCompositionRule thisCompositionRule,
			IndexedClassExpression premise, Context context,
			SaturationStateWriter writer);

	void visit(
			IndexedSubClassOfAxiom.SubsumerCompositionRule thisCompositionRule,
			IndexedClassExpression premise, Context context,
			SaturationStateWriter writer);

	void visit(Propagation.SubsumerBackwardLinkRule thisBackwardLinkRule,
			BackwardLink premise, Context context, SaturationStateWriter writer);

}
