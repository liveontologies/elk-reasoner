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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointnessAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction.ContradictionBackwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public interface RuleApplicationVisitor {

	void visit(
			IndexedClass.OwlThingContextInitializationRule owlThingContextInitializationRule,
			SaturationState.Writer writer, Context context);

	void visit(
			IndexedDisjointnessAxiom.ThisCompositionRule thisCompositionRule,
			SaturationState.Writer writer, Context context);

	void visit(
			IndexedDisjointnessAxiom.ThisContradictionRule thisContradictionRule,
			SaturationState.Writer writer, Context context);

	void visit(
			IndexedObjectIntersectionOf.ThisCompositionRule thisCompositionRule,
			SaturationState.Writer writer, Context context);

	void visit(IndexedSubClassOfAxiom.ThisCompositionRule thisCompositionRule,
			SaturationState.Writer writer, Context context);

	void visit(
			IndexedObjectSomeValuesFrom.ThisCompositionRule thisCompositionRule,
			SaturationState.Writer writer, Context context);

	void visit(ForwardLink.ThisBackwardLinkRule thisBackwardLinkRule,
			SaturationState.Writer writer, BackwardLink backwardLink);

	void visit(Propagation.ThisBackwardLinkRule thisBackwardLinkRule,
			SaturationState.Writer writer, BackwardLink backwardLink);

	void visit(ContradictionBackwardLinkRule bottomBackwardLinkRule, SaturationState.Writer writer,
			BackwardLink backwardLink);

}
