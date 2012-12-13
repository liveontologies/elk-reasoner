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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass.OwlThingContextInitializationRule;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointnessAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.saturation.SaturationState.Writer;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction.BottomBackwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class CountingRuleApplicationVisitor implements RuleApplicationVisitor {

	private final RuleStatistics ruleStats_;
	
	CountingRuleApplicationVisitor(RuleStatistics stats) {
		ruleStats_ = stats;
	}
	
	@Override
	public void visit(
			OwlThingContextInitializationRule rule,
			Writer writer, Context context) {
		ruleStats_.countOwlThingContextInitializationRule++;
	}

	@Override
	public void visit(IndexedDisjointnessAxiom.ThisCompositionRule rule, Writer writer,
			Context context) {
		ruleStats_.countDisjointnessAxiomCompositionRule++;
	}

	@Override
	public void visit(
			IndexedObjectIntersectionOf.ThisCompositionRule rule,
			Writer writer, Context context) {
		ruleStats_.countObjectIntersectionOfCompositionRule++;
	}

	@Override
	public void visit(
			IndexedSubClassOfAxiom.ThisCompositionRule rule,
			Writer writer, Context context) {
		ruleStats_.countSubClassOfRule++;
	}

	@Override
	public void visit(
			IndexedObjectSomeValuesFrom.ThisCompositionRule rule,
			Writer writer, Context context) {
		ruleStats_.countObjectSomeValuesFromCompositionRule++;
	}

	@Override
	public void visit(IndexedDisjointnessAxiom.ThisContradictionRule rule,
			Writer writer, Context context) {
		ruleStats_.countDisjointnessAxiomContradictionRule++;
	}

	@Override
	public void visit(ForwardLink.ThisBackwardLinkRule rule, Writer writer,
			BackwardLink backwardLink) {
		ruleStats_.countBackwardLinkFromForwardLinkRule++;
	}

	@Override
	public void visit(
			Propagation.ThisBackwardLinkRule rule,
			Writer writer, BackwardLink backwardLink) {
		ruleStats_.countPropagationBackwardLinkRule++;
	}

	@Override
	public void visit(BottomBackwardLinkRule rule,
			Writer writer, BackwardLink backwardLink) {
		ruleStats_.countContradictionBackwardLinkRule++;	
	}	
}
