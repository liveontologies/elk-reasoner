package org.semanticweb.elk.matching.inferences;

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

public interface InferenceMatch {

	<O> O accept(Visitor<O> visitor);

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory extends BackwardLinkCompositionMatch1.Factory,
			BackwardLinkCompositionMatch2.Factory,
			BackwardLinkCompositionMatch3.Factory,
			BackwardLinkCompositionMatch4.Factory,
			BackwardLinkCompositionMatch5.Factory,
			BackwardLinkCompositionMatch6.Factory,
			BackwardLinkOfObjectHasSelfMatch1.Factory,
			BackwardLinkOfObjectHasSelfMatch2.Factory,
			BackwardLinkOfObjectSomeValuesFromMatch1.Factory,
			BackwardLinkOfObjectSomeValuesFromMatch2.Factory,
			BackwardLinkReversedExpandedMatch1.Factory,
			BackwardLinkReversedExpandedMatch2.Factory,
			BackwardLinkReversedExpandedMatch3.Factory,
			BackwardLinkReversedMatch1.Factory,
			BackwardLinkReversedMatch2.Factory,
			ElkClassAssertionAxiomConversionMatch1.Factory,
			ElkDifferentIndividualsAxiomBinaryConversionMatch1.Factory,
			ElkDisjointClassesAxiomBinaryConversionMatch1.Factory,
			ElkDisjointUnionAxiomBinaryConversionMatch1.Factory,
			ElkDisjointUnionAxiomSubClassConversionMatch1.Factory,
			ElkEquivalentClassesAxiomEquivalenceConversionMatch1.Factory,
			ElkEquivalentClassesAxiomSubClassConversionMatch1.Factory,
			ElkObjectPropertyAssertionAxiomConversionMatch1.Factory,
			ElkObjectPropertyDomainAxiomConversionMatch1.Factory,
			ElkReflexiveObjectPropertyAxiomConversionMatch1.Factory,
			ElkSameIndividualAxiomConversionMatch1.Factory,
			ElkSubClassOfAxiomConversionMatch1.Factory,
			ElkSubObjectPropertyOfAxiomConversionMatch1.Factory,
			ElkTransitiveObjectPropertyAxiomConversionMatch1.Factory,
			ForwardLinkCompositionMatch1.Factory,
			ForwardLinkCompositionMatch2.Factory,
			ForwardLinkCompositionMatch3.Factory,
			ForwardLinkCompositionMatch4.Factory,
			ForwardLinkCompositionMatch5.Factory,
			ForwardLinkOfObjectHasSelfMatch1.Factory,
			ForwardLinkOfObjectHasSelfMatch2.Factory,
			ForwardLinkOfObjectSomeValuesFromMatch1.Factory,
			ForwardLinkOfObjectSomeValuesFromMatch2.Factory,
			PropagationGeneratedMatch1.Factory,
			PropagationGeneratedMatch2.Factory,
			PropagationGeneratedMatch3.Factory,
			PropertyRangeInheritedMatch1.Factory,
			PropertyRangeInheritedMatch2.Factory,
			PropertyRangeInheritedMatch3.Factory,
			SubClassInclusionComposedDefinedClassMatch1.Factory,
			SubClassInclusionComposedDefinedClassMatch2.Factory,
			SubClassInclusionComposedEntityMatch1.Factory,
			SubClassInclusionComposedObjectIntersectionOfMatch1.Factory,
			SubClassInclusionComposedObjectSomeValuesFromMatch1.Factory,
			SubClassInclusionComposedObjectSomeValuesFromMatch2.Factory,
			SubClassInclusionComposedObjectSomeValuesFromMatch3.Factory,
			SubClassInclusionComposedObjectUnionOfMatch1.Factory,
			SubClassInclusionDecomposedFirstConjunctMatch1.Factory,
			SubClassInclusionDecomposedFirstConjunctMatch2.Factory,
			SubClassInclusionDecomposedSecondConjunctMatch1.Factory,
			SubClassInclusionDecomposedSecondConjunctMatch2.Factory,
			SubClassInclusionExpandedDefinitionMatch1.Factory,
			SubClassInclusionExpandedDefinitionMatch2.Factory,
			SubClassInclusionExpandedFirstEquivalentClassMatch1.Factory,
			SubClassInclusionExpandedFirstEquivalentClassMatch2.Factory,
			SubClassInclusionExpandedSecondEquivalentClassMatch1.Factory,
			SubClassInclusionExpandedSecondEquivalentClassMatch2.Factory,
			SubClassInclusionExpandedSubClassOfMatch1.Factory,
			SubClassInclusionExpandedSubClassOfMatch2.Factory,
			SubClassInclusionObjectHasSelfPropertyRangeMatch1.Factory,
			SubClassInclusionObjectHasSelfPropertyRangeMatch2.Factory,
			SubClassInclusionObjectHasSelfPropertyRangeMatch3.Factory,
			SubClassInclusionOwlThingMatch1.Factory,
			SubClassInclusionRangeMatch1.Factory,
			SubClassInclusionRangeMatch2.Factory,
			SubClassInclusionTautologyMatch1.Factory,
			SubPropertyChainExpandedSubObjectPropertyOfMatch1.Factory,
			SubPropertyChainExpandedSubObjectPropertyOfMatch2.Factory,
			SubPropertyChainTautologyMatch1.Factory {

		// combined interface

	}

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O> extends BackwardLinkCompositionMatch1.Visitor<O>,
			BackwardLinkCompositionMatch2.Visitor<O>,
			BackwardLinkCompositionMatch3.Visitor<O>,
			BackwardLinkCompositionMatch4.Visitor<O>,
			BackwardLinkCompositionMatch5.Visitor<O>,
			BackwardLinkCompositionMatch6.Visitor<O>,
			BackwardLinkOfObjectHasSelfMatch1.Visitor<O>,
			BackwardLinkOfObjectHasSelfMatch2.Visitor<O>,
			BackwardLinkOfObjectSomeValuesFromMatch1.Visitor<O>,
			BackwardLinkOfObjectSomeValuesFromMatch2.Visitor<O>,
			BackwardLinkReversedExpandedMatch1.Visitor<O>,
			BackwardLinkReversedExpandedMatch2.Visitor<O>,
			BackwardLinkReversedExpandedMatch3.Visitor<O>,
			BackwardLinkReversedMatch1.Visitor<O>,
			BackwardLinkReversedMatch2.Visitor<O>,
			ElkClassAssertionAxiomConversionMatch1.Visitor<O>,
			ElkDifferentIndividualsAxiomBinaryConversionMatch1.Visitor<O>,
			ElkDisjointClassesAxiomBinaryConversionMatch1.Visitor<O>,
			ElkDisjointUnionAxiomBinaryConversionMatch1.Visitor<O>,
			ElkDisjointUnionAxiomSubClassConversionMatch1.Visitor<O>,
			ElkEquivalentClassesAxiomEquivalenceConversionMatch1.Visitor<O>,
			ElkEquivalentClassesAxiomSubClassConversionMatch1.Visitor<O>,
			ElkObjectPropertyAssertionAxiomConversionMatch1.Visitor<O>,
			ElkObjectPropertyDomainAxiomConversionMatch1.Visitor<O>,
			ElkReflexiveObjectPropertyAxiomConversionMatch1.Visitor<O>,
			ElkSameIndividualAxiomConversionMatch1.Visitor<O>,
			ElkSubClassOfAxiomConversionMatch1.Visitor<O>,
			ElkSubObjectPropertyOfAxiomConversionMatch1.Visitor<O>,
			ElkTransitiveObjectPropertyAxiomConversionMatch1.Visitor<O>,
			ForwardLinkCompositionMatch1.Visitor<O>,
			ForwardLinkCompositionMatch2.Visitor<O>,
			ForwardLinkCompositionMatch3.Visitor<O>,
			ForwardLinkCompositionMatch4.Visitor<O>,
			ForwardLinkCompositionMatch5.Visitor<O>,
			ForwardLinkOfObjectHasSelfMatch1.Visitor<O>,
			ForwardLinkOfObjectHasSelfMatch2.Visitor<O>,
			ForwardLinkOfObjectSomeValuesFromMatch1.Visitor<O>,
			ForwardLinkOfObjectSomeValuesFromMatch2.Visitor<O>,
			PropagationGeneratedMatch1.Visitor<O>,
			PropagationGeneratedMatch2.Visitor<O>,
			PropagationGeneratedMatch3.Visitor<O>,
			PropertyRangeInheritedMatch1.Visitor<O>,
			PropertyRangeInheritedMatch2.Visitor<O>,
			PropertyRangeInheritedMatch3.Visitor<O>,
			SubClassInclusionComposedDefinedClassMatch1.Visitor<O>,
			SubClassInclusionComposedDefinedClassMatch2.Visitor<O>,
			SubClassInclusionComposedEntityMatch1.Visitor<O>,
			SubClassInclusionComposedObjectIntersectionOfMatch1.Visitor<O>,
			SubClassInclusionComposedObjectSomeValuesFromMatch1.Visitor<O>,
			SubClassInclusionComposedObjectSomeValuesFromMatch2.Visitor<O>,
			SubClassInclusionComposedObjectSomeValuesFromMatch3.Visitor<O>,
			SubClassInclusionComposedObjectUnionOfMatch1.Visitor<O>,
			SubClassInclusionDecomposedFirstConjunctMatch1.Visitor<O>,
			SubClassInclusionDecomposedFirstConjunctMatch2.Visitor<O>,
			SubClassInclusionDecomposedSecondConjunctMatch1.Visitor<O>,
			SubClassInclusionDecomposedSecondConjunctMatch2.Visitor<O>,
			SubClassInclusionExpandedDefinitionMatch1.Visitor<O>,
			SubClassInclusionExpandedDefinitionMatch2.Visitor<O>,
			SubClassInclusionExpandedFirstEquivalentClassMatch1.Visitor<O>,
			SubClassInclusionExpandedFirstEquivalentClassMatch2.Visitor<O>,
			SubClassInclusionExpandedSecondEquivalentClassMatch1.Visitor<O>,
			SubClassInclusionExpandedSecondEquivalentClassMatch2.Visitor<O>,
			SubClassInclusionExpandedSubClassOfMatch1.Visitor<O>,
			SubClassInclusionExpandedSubClassOfMatch2.Visitor<O>,
			SubClassInclusionObjectHasSelfPropertyRangeMatch1.Visitor<O>,
			SubClassInclusionObjectHasSelfPropertyRangeMatch2.Visitor<O>,
			SubClassInclusionObjectHasSelfPropertyRangeMatch3.Visitor<O>,
			SubClassInclusionOwlThingMatch1.Visitor<O>,
			SubClassInclusionRangeMatch1.Visitor<O>,
			SubClassInclusionRangeMatch2.Visitor<O>,
			SubClassInclusionTautologyMatch1.Visitor<O>,
			SubPropertyChainExpandedSubObjectPropertyOfMatch1.Visitor<O>,
			SubPropertyChainExpandedSubObjectPropertyOfMatch2.Visitor<O>,
			SubPropertyChainTautologyMatch1.Visitor<O> {

		// combined interface

	}

}
