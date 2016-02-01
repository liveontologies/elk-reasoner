/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.inferences;

import org.semanticweb.elk.reasoner.saturation.properties.inferences.ObjectPropertyInference;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.PropertyRangeInherited;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.SubPropertyChainExpandedSubObjectPropertyOf;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.SubPropertyChainTautology;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

/**
 * A {@link SaturationInference.Visitor} that always returns {@code null}.
 * 
 * @author Yevgeny Kazakov
 */
public class DummySaturationInferenceVisitor<O>
		implements
			SaturationInference.Visitor<O> {

	protected O defaultVisit(ClassInference inference) {
		return defaultVisit((SaturationInference) inference);
	}

	protected O defaultVisit(ObjectPropertyInference inference) {
		return defaultVisit((SaturationInference) inference);
	}

	protected O defaultVisit(
			@SuppressWarnings("unused") SaturationInference inference) {
		// can be overriden in sub-classes
		return null;
	}

	@Override
	public O visit(BackwardLinkComposition inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(BackwardLinkOfObjectHasSelf inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(BackwardLinkOfObjectSomeValuesFrom inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(BackwardLinkReversed inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(BackwardLinkReversedExpanded inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ContextInitializationNoPremises inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ContradictionOfDisjointSubsumers inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ContradictionOfObjectComplementOf inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ContradictionOfOwlNothing inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ContradictionPropagated inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(DisjointSubsumerFromSubsumer inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ForwardLinkComposition inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ForwardLinkOfObjectHasSelf inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ForwardLinkOfObjectSomeValuesFrom inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(PropagationGenerated inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(PropertyRangeInherited inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(SubClassInclusionComposedDefinedClass inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(SubClassInclusionComposedEntity inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(SubClassInclusionComposedObjectIntersectionOf inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(SubClassInclusionComposedObjectSomeValuesFrom inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(SubClassInclusionComposedObjectUnionOf inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(SubClassInclusionDecomposedFirstConjunct inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(SubClassInclusionDecomposedSecondConjunct inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(SubClassInclusionExpandedDefinition inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(SubClassInclusionExpandedSubClassOf inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(SubClassInclusionObjectHasSelfPropertyRange inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(SubClassInclusionOwlThing inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(SubClassInclusionRange inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(SubClassInclusionTautology inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(SubContextInitializationNoPremises inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(SubPropertyChainExpandedSubObjectPropertyOf inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(SubPropertyChainTautology inference) {
		return defaultVisit(inference);
	}

}
