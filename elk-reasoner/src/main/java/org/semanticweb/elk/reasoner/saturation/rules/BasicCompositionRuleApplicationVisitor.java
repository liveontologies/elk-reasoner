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

import org.semanticweb.elk.reasoner.indexing.hierarchy.DirectIndex.ContextRootInitializationRule;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass.OwlThingContextInitializationRule;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointnessAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectComplementOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectUnionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.saturation.BasicSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction.ContradictionBackwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class BasicCompositionRuleApplicationVisitor implements
		CompositionRuleApplicationVisitor {

	@Override
	public void visit(OwlThingContextInitializationRule rule,
			BasicSaturationStateWriter writer, Context context) {
		rule.apply(writer, context);
	}

	@Override
	public void visit(ContextRootInitializationRule rule,
			BasicSaturationStateWriter writer, Context context) {
		rule.apply(writer, context);
	}

	@Override
	public void visit(IndexedDisjointnessAxiom.ThisCompositionRule rule,
			BasicSaturationStateWriter writer, Conclusion premise, Context context) {
		rule.apply(writer, premise, context);
	}

	@Override
	public void visit(IndexedObjectComplementOf.ThisCompositionRule rule,
			BasicSaturationStateWriter writer, Context context) {
		//FIXME figure out the premise
		rule.apply(writer, null, context);
	}

	@Override
	public void visit(IndexedObjectIntersectionOf.ThisCompositionRule rule,
			BasicSaturationStateWriter writer, Conclusion premise, Context context) {
		rule.apply(writer, premise, context);
	}

	@Override
	public void visit(IndexedSubClassOfAxiom.ThisCompositionRule rule,
			BasicSaturationStateWriter writer, Conclusion premise, Context context) {
		rule.apply(writer, premise, context);
	}

	@Override
	public void visit(IndexedObjectSomeValuesFrom.ThisCompositionRule rule,
			BasicSaturationStateWriter writer, Conclusion premise, Context context) {
		rule.apply(writer, premise, context);
	}

	@Override
	public void visit(IndexedObjectUnionOf.ThisCompositionRule rule,
			BasicSaturationStateWriter writer, Conclusion premise, Context context) {
		rule.apply(writer, premise, context);
	}

	@Override
	public void visit(IndexedDisjointnessAxiom.ThisContradictionRule rule,
			BasicSaturationStateWriter writer, Context context) {
		//FIXME figure out the premise
		rule.apply(writer, null, context);
	}

	@Override
	public void visit(ForwardLink.ThisBackwardLinkRule rule,
			BasicSaturationStateWriter writer, BackwardLink backwardLink) {
		rule.apply(writer, backwardLink);
	}

	@Override
	public void visit(Propagation.ThisBackwardLinkRule rule,
			BasicSaturationStateWriter writer, BackwardLink backwardLink) {
		rule.apply(writer, backwardLink);
	}

	@Override
	public void visit(ContradictionBackwardLinkRule rule,
			BasicSaturationStateWriter writer, BackwardLink backwardLink) {
		rule.apply(writer, backwardLink);
	}

}
