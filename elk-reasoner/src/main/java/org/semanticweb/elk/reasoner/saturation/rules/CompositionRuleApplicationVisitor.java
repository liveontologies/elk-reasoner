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
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointnessAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectComplementOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectUnionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.saturation.BasicSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ContradictionImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLinkImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.PropagationImpl;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 */
public interface CompositionRuleApplicationVisitor {

	void visit(
			IndexedClass.OwlThingContextInitializationRule owlThingContextInitializationRule,
			BasicSaturationStateWriter writer, Context context);

	void visit(DirectIndex.ContextRootInitializationRule rootInitRule,
			BasicSaturationStateWriter writer, Context context);

	void visit(
			IndexedDisjointnessAxiom.ThisCompositionRule thisCompositionRule,
			BasicSaturationStateWriter writer, Conclusion premise, Context context);

	void visit(
			IndexedDisjointnessAxiom.ThisContradictionRule thisContradictionRule,
			BasicSaturationStateWriter writer, Context context);

	void visit(
			IndexedObjectComplementOf.ThisCompositionRule thisCompositionRule,
			BasicSaturationStateWriter writer, Context context);

	void visit(
			IndexedObjectIntersectionOf.ThisCompositionRule thisCompositionRule,
			BasicSaturationStateWriter writer, Conclusion premise, Context context);

	void visit(IndexedSubClassOfAxiom.ThisCompositionRule thisCompositionRule,
			BasicSaturationStateWriter writer, Conclusion premise, Context context);

	void visit(
			IndexedObjectSomeValuesFrom.ThisCompositionRule thisCompositionRule,
			BasicSaturationStateWriter writer, Conclusion premise, Context context);

	void visit(IndexedObjectUnionOf.ThisCompositionRule thisCompositionRule,
			BasicSaturationStateWriter writer, Conclusion premise, Context context);

	void visit(ForwardLinkImpl.ThisBackwardLinkRule thisBackwardLinkRule,
			BasicSaturationStateWriter writer, BackwardLink backwardLink, Context context);

	void visit(PropagationImpl.ThisBackwardLinkRule thisBackwardLinkRule,
			BasicSaturationStateWriter writer, BackwardLink backwardLink, Context context);

	void visit(ContradictionImpl.ContradictionBackwardLinkRule bottomBackwardLinkRule,
			BasicSaturationStateWriter writer, BackwardLink backwardLink, Context context);

}
