package org.semanticweb.elk.matching.inferences;

/*
 * #%L
 * ELK Proofs Package
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.matching.conclusions.BackwardLinkMatch1;
import org.semanticweb.elk.matching.conclusions.BackwardLinkMatch2;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch1;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch2;
import org.semanticweb.elk.matching.conclusions.IndexedEquivalentClassesAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedEquivalentClassesAxiomMatch2;
import org.semanticweb.elk.matching.conclusions.IndexedObjectPropertyRangeAxiomMatch2;
import org.semanticweb.elk.matching.conclusions.IndexedSubClassOfAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedSubClassOfAxiomMatch2;
import org.semanticweb.elk.matching.conclusions.IndexedSubObjectPropertyOfAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedSubObjectPropertyOfAxiomMatch2;
import org.semanticweb.elk.matching.conclusions.PropagationMatch1;
import org.semanticweb.elk.matching.conclusions.PropagationMatch2;
import org.semanticweb.elk.matching.conclusions.PropagationMatch3;
import org.semanticweb.elk.matching.conclusions.PropertyRangeMatch1;
import org.semanticweb.elk.matching.conclusions.PropertyRangeMatch2;
import org.semanticweb.elk.matching.conclusions.SubClassInclusionComposedMatch1;
import org.semanticweb.elk.matching.conclusions.SubClassInclusionDecomposedMatch1;
import org.semanticweb.elk.matching.conclusions.SubClassInclusionDecomposedMatch2;
import org.semanticweb.elk.matching.conclusions.SubPropertyChainMatch1;
import org.semanticweb.elk.matching.conclusions.SubPropertyChainMatch2;
import org.semanticweb.elk.reasoner.indexing.model.ElkClassAssertionAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkDifferentIndividualsAxiomBinaryConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkDisjointClassesAxiomBinaryConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkDisjointUnionAxiomBinaryConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkDisjointUnionAxiomOwlNothingConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkDisjointUnionAxiomSubClassConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkEquivalentClassesAxiomEquivalenceConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkEquivalentClassesAxiomSubClassConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkEquivalentObjectPropertiesAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkObjectPropertyAssertionAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkObjectPropertyDomainAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkReflexiveObjectPropertyAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkSameIndividualAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkSubClassOfAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkSubObjectPropertyOfAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkTransitiveObjectPropertyAxiomConversion;
import org.semanticweb.elk.reasoner.saturation.inferences.BackwardLinkComposition;
import org.semanticweb.elk.reasoner.saturation.inferences.BackwardLinkOfObjectHasSelf;
import org.semanticweb.elk.reasoner.saturation.inferences.BackwardLinkOfObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.inferences.BackwardLinkReversed;
import org.semanticweb.elk.reasoner.saturation.inferences.BackwardLinkReversedExpanded;
import org.semanticweb.elk.reasoner.saturation.inferences.ForwardLinkComposition;
import org.semanticweb.elk.reasoner.saturation.inferences.ForwardLinkOfObjectHasSelf;
import org.semanticweb.elk.reasoner.saturation.inferences.ForwardLinkOfObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.inferences.PropagationGenerated;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionComposedDefinedClass;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionComposedEntity;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionComposedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionComposedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionComposedObjectUnionOf;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionDecomposedFirstConjunct;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionDecomposedSecondConjunct;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionExpandedDefinition;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionExpandedFirstEquivalentClass;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionExpandedSecondEquivalentClass;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionExpandedSubClassOf;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionObjectHasSelfPropertyRange;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionOwlThing;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionRange;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionTautology;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.PropertyRangeInherited;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.SubPropertyChainExpandedSubObjectPropertyOf;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.SubPropertyChainTautology;

public class InferenceMatchDelegatingFactory implements InferenceMatch.Factory {

	private final InferenceMatch.Factory mainFactory_;

	protected InferenceMatchDelegatingFactory(
			InferenceMatch.Factory mainFactory) {
		this.mainFactory_ = mainFactory;
	}

	protected <C extends InferenceMatch> C filter(C candidate) {
		return candidate;
	}

	@Override
	public BackwardLinkCompositionMatch1 getBackwardLinkCompositionMatch1(
			BackwardLinkComposition parent,
			BackwardLinkMatch1 conclusionMatch) {
		return filter(mainFactory_.getBackwardLinkCompositionMatch1(parent,
				conclusionMatch));
	}

	@Override
	public BackwardLinkCompositionMatch2 getBackwardLinkCompositionMatch2(
			BackwardLinkCompositionMatch1 parent,
			IndexedSubObjectPropertyOfAxiomMatch2 conclusionMatch) {
		return filter(mainFactory_.getBackwardLinkCompositionMatch2(parent,
				conclusionMatch));
	}

	@Override
	public BackwardLinkCompositionMatch3 getBackwardLinkCompositionMatch3(
			BackwardLinkCompositionMatch2 parent,
			SubPropertyChainMatch2 secondPremiseMatch) {
		return filter(mainFactory_.getBackwardLinkCompositionMatch3(parent,
				secondPremiseMatch));
	}

	@Override
	public BackwardLinkCompositionMatch4 getBackwardLinkCompositionMatch4(
			BackwardLinkCompositionMatch3 parent,
			BackwardLinkMatch2 secondPremiseMatch) {
		return filter(mainFactory_.getBackwardLinkCompositionMatch4(parent,
				secondPremiseMatch));
	}

	@Override
	public BackwardLinkCompositionMatch5 getBackwardLinkCompositionMatch5(
			BackwardLinkCompositionMatch4 parent,
			SubPropertyChainMatch2 fourthPremiseMatch) {
		return filter(mainFactory_.getBackwardLinkCompositionMatch5(parent,
				fourthPremiseMatch));
	}

	@Override
	public BackwardLinkCompositionMatch6 getBackwardLinkCompositionMatch6(
			BackwardLinkCompositionMatch5 parent,
			ForwardLinkMatch2 thirdPremiseMatch) {
		return filter(mainFactory_.getBackwardLinkCompositionMatch6(parent,
				thirdPremiseMatch));
	}

	@Override
	public BackwardLinkOfObjectHasSelfMatch1 getBackwardLinkOfObjectHasSelfMatch1(
			BackwardLinkOfObjectHasSelf parent,
			BackwardLinkMatch1 conclusionMatch) {
		return filter(mainFactory_.getBackwardLinkOfObjectHasSelfMatch1(parent,
				conclusionMatch));
	}

	@Override
	public BackwardLinkOfObjectHasSelfMatch2 getBackwardLinkOfObjectHasSelfMatch2(
			BackwardLinkOfObjectHasSelfMatch1 parent,
			SubClassInclusionDecomposedMatch2 premiseMatch) {
		return filter(mainFactory_.getBackwardLinkOfObjectHasSelfMatch2(parent,
				premiseMatch));
	}

	@Override
	public BackwardLinkOfObjectSomeValuesFromMatch1 getBackwardLinkOfObjectSomeValuesFromMatch1(
			BackwardLinkOfObjectSomeValuesFrom parent,
			BackwardLinkMatch1 conclusionMatch) {
		return filter(mainFactory_.getBackwardLinkOfObjectSomeValuesFromMatch1(
				parent, conclusionMatch));
	}

	@Override
	public BackwardLinkOfObjectSomeValuesFromMatch2 getBackwardLinkOfObjectSomeValuesFromMatch2(
			BackwardLinkOfObjectSomeValuesFromMatch1 parent,
			SubClassInclusionDecomposedMatch2 premiseMatch) {
		return filter(mainFactory_.getBackwardLinkOfObjectSomeValuesFromMatch2(
				parent, premiseMatch));
	}

	@Override
	public BackwardLinkReversedExpandedMatch1 getBackwardLinkReversedExpandedMatch1(
			BackwardLinkReversedExpanded parent,
			BackwardLinkMatch1 conclusionMatch) {
		return filter(mainFactory_.getBackwardLinkReversedExpandedMatch1(parent,
				conclusionMatch));
	}

	@Override
	public BackwardLinkReversedExpandedMatch2 getBackwardLinkReversedExpandedMatch2(
			BackwardLinkReversedExpandedMatch1 parent,
			IndexedSubObjectPropertyOfAxiomMatch2 secondPremiseMatch) {
		return filter(mainFactory_.getBackwardLinkReversedExpandedMatch2(parent,
				secondPremiseMatch));
	}

	@Override
	public BackwardLinkReversedExpandedMatch3 getBackwardLinkReversedExpandedMatch3(
			BackwardLinkReversedExpandedMatch2 parent,
			ForwardLinkMatch2 firstPremiseMatch) {
		return filter(mainFactory_.getBackwardLinkReversedExpandedMatch3(parent,
				firstPremiseMatch));
	}

	@Override
	public BackwardLinkReversedMatch1 getBackwardLinkReversedMatch1(
			BackwardLinkReversed parent, BackwardLinkMatch1 conclusionMatch) {
		return filter(mainFactory_.getBackwardLinkReversedMatch1(parent,
				conclusionMatch));
	}

	@Override
	public BackwardLinkReversedMatch2 getBackwardLinkReversedMatch2(
			BackwardLinkReversedMatch1 parent, ForwardLinkMatch2 premiseMatch) {
		return filter(mainFactory_.getBackwardLinkReversedMatch2(parent,
				premiseMatch));
	}

	@Override
	public ElkClassAssertionAxiomConversionMatch1 getElkClassAssertionAxiomConversionMatch1(
			ElkClassAssertionAxiomConversion parent,
			IndexedSubClassOfAxiomMatch1 conclusionMatch) {
		return filter(mainFactory_.getElkClassAssertionAxiomConversionMatch1(
				parent, conclusionMatch));
	}

	@Override
	public ElkDifferentIndividualsAxiomBinaryConversionMatch1 getElkDifferentIndividualsAxiomBinaryConversionMatch1(
			ElkDifferentIndividualsAxiomBinaryConversion parent,
			IndexedSubClassOfAxiomMatch1 conclusionMatch) {
		return filter(mainFactory_
				.getElkDifferentIndividualsAxiomBinaryConversionMatch1(parent,
						conclusionMatch));
	}

	@Override
	public ElkDisjointClassesAxiomBinaryConversionMatch1 getElkDisjointClassesAxiomBinaryConversionMatch1(
			ElkDisjointClassesAxiomBinaryConversion parent,
			IndexedSubClassOfAxiomMatch1 conclusionMatch) {
		return filter(
				mainFactory_.getElkDisjointClassesAxiomBinaryConversionMatch1(
						parent, conclusionMatch));
	}

	@Override
	public ElkDisjointUnionAxiomBinaryConversionMatch1 getElkDisjointUnionAxiomBinaryConversionMatch1(
			ElkDisjointUnionAxiomBinaryConversion parent,
			IndexedSubClassOfAxiomMatch1 conclusionMatch) {
		return filter(
				mainFactory_.getElkDisjointUnionAxiomBinaryConversionMatch1(
						parent, conclusionMatch));
	}

	@Override
	public ElkDisjointUnionAxiomOwlNothingConversionMatch1 getElkDisjointUnionAxiomOwlNothingConversionMatch1(
			ElkDisjointUnionAxiomOwlNothingConversion parent,
			IndexedSubClassOfAxiomMatch1 conclusionMatch) {
		return filter(
				mainFactory_.getElkDisjointUnionAxiomOwlNothingConversionMatch1(
						parent, conclusionMatch));
	}

	@Override
	public ElkDisjointUnionAxiomSubClassConversionMatch1 getElkDisjointUnionAxiomSubClassConversionMatch1(
			ElkDisjointUnionAxiomSubClassConversion parent,
			IndexedSubClassOfAxiomMatch1 conclusionMatch) {
		return filter(
				mainFactory_.getElkDisjointUnionAxiomSubClassConversionMatch1(
						parent, conclusionMatch));
	}

	@Override
	public ElkEquivalentClassesAxiomEquivalenceConversionMatch1 getElkEquivalentClassesAxiomEquivalenceConversionMatch1(
			ElkEquivalentClassesAxiomEquivalenceConversion parent,
			IndexedEquivalentClassesAxiomMatch1 conclusionMatch) {
		return filter(mainFactory_
				.getElkEquivalentClassesAxiomEquivalenceConversionMatch1(parent,
						conclusionMatch));
	}

	@Override
	public ElkEquivalentClassesAxiomSubClassConversionMatch1 getElkEquivalentClassesAxiomSubClassConversionMatch1(
			ElkEquivalentClassesAxiomSubClassConversion parent,
			IndexedSubClassOfAxiomMatch1 conclusionMatch) {
		return filter(mainFactory_
				.getElkEquivalentClassesAxiomSubClassConversionMatch1(parent,
						conclusionMatch));
	}

	@Override
	public ElkEquivalentObjectPropertiesAxiomConversionMatch1 getElkEquivalentObjectPropertiesAxiomConversionMatch1(
			ElkEquivalentObjectPropertiesAxiomConversion parent,
			IndexedSubObjectPropertyOfAxiomMatch1 conclusionMatch) {
		return filter(mainFactory_
				.getElkEquivalentObjectPropertiesAxiomConversionMatch1(parent,
						conclusionMatch));
	}

	@Override
	public ElkObjectPropertyAssertionAxiomConversionMatch1 getElkObjectPropertyAssertionAxiomConversionMatch1(
			ElkObjectPropertyAssertionAxiomConversion parent,
			IndexedSubClassOfAxiomMatch1 conclusionMatch) {
		return filter(
				mainFactory_.getElkObjectPropertyAssertionAxiomConversionMatch1(
						parent, conclusionMatch));
	}

	@Override
	public ElkObjectPropertyDomainAxiomConversionMatch1 getElkObjectPropertyDomainAxiomConversionMatch1(
			ElkObjectPropertyDomainAxiomConversion parent,
			IndexedSubClassOfAxiomMatch1 conclusionMatch) {
		return filter(
				mainFactory_.getElkObjectPropertyDomainAxiomConversionMatch1(
						parent, conclusionMatch));
	}

	@Override
	public ElkReflexiveObjectPropertyAxiomConversionMatch1 getElkReflexiveObjectPropertyAxiomConversionMatch1(
			ElkReflexiveObjectPropertyAxiomConversion parent,
			IndexedSubClassOfAxiomMatch1 conclusionMatch) {
		return filter(
				mainFactory_.getElkReflexiveObjectPropertyAxiomConversionMatch1(
						parent, conclusionMatch));
	}

	@Override
	public ElkSameIndividualAxiomConversionMatch1 getElkSameIndividualAxiomConversionMatch1(
			ElkSameIndividualAxiomConversion parent,
			IndexedSubClassOfAxiomMatch1 conclusionMatch) {
		return filter(mainFactory_.getElkSameIndividualAxiomConversionMatch1(
				parent, conclusionMatch));
	}

	@Override
	public ElkSubClassOfAxiomConversionMatch1 getElkSubClassOfAxiomConversionMatch1(
			ElkSubClassOfAxiomConversion parent,
			IndexedSubClassOfAxiomMatch1 conclusionMatch) {
		return filter(mainFactory_.getElkSubClassOfAxiomConversionMatch1(parent,
				conclusionMatch));
	}

	@Override
	public ElkSubObjectPropertyOfAxiomConversionMatch1 getElkSubObjectPropertyOfAxiomConversionMatch1(
			ElkSubObjectPropertyOfAxiomConversion parent,
			IndexedSubObjectPropertyOfAxiomMatch1 conclusionMatch) {
		return filter(
				mainFactory_.getElkSubObjectPropertyOfAxiomConversionMatch1(
						parent, conclusionMatch));
	}

	@Override
	public ElkTransitiveObjectPropertyAxiomConversionMatch1 getElkTransitiveObjectPropertyAxiomConversionMatch1(
			ElkTransitiveObjectPropertyAxiomConversion parent,
			IndexedSubObjectPropertyOfAxiomMatch1 conclusionMatch) {
		return filter(mainFactory_
				.getElkTransitiveObjectPropertyAxiomConversionMatch1(parent,
						conclusionMatch));
	}

	@Override
	public ForwardLinkCompositionMatch1 getForwardLinkCompositionMatch1(
			ForwardLinkComposition parent, ForwardLinkMatch1 conclusionMatch) {
		return filter(mainFactory_.getForwardLinkCompositionMatch1(parent,
				conclusionMatch));
	}

	@Override
	public ForwardLinkCompositionMatch2 getForwardLinkCompositionMatch2(
			ForwardLinkCompositionMatch1 parent,
			SubPropertyChainMatch2 secondPremiseMatch) {
		return filter(mainFactory_.getForwardLinkCompositionMatch2(parent,
				secondPremiseMatch));
	}

	@Override
	public ForwardLinkCompositionMatch3 getForwardLinkCompositionMatch3(
			ForwardLinkCompositionMatch2 parent,
			BackwardLinkMatch2 secondPremiseMatch) {
		return filter(mainFactory_.getForwardLinkCompositionMatch3(parent,
				secondPremiseMatch));
	}

	@Override
	public ForwardLinkCompositionMatch4 getForwardLinkCompositionMatch4(
			ForwardLinkCompositionMatch3 parent,
			SubPropertyChainMatch2 fourthPremiseMatch) {
		return filter(mainFactory_.getForwardLinkCompositionMatch4(parent,
				fourthPremiseMatch));
	}

	@Override
	public ForwardLinkCompositionMatch5 getForwardLinkCompositionMatch5(
			ForwardLinkCompositionMatch4 parent,
			ForwardLinkMatch2 thirdPremiseMatch) {
		return filter(mainFactory_.getForwardLinkCompositionMatch5(parent,
				thirdPremiseMatch));
	}

	@Override
	public ForwardLinkOfObjectHasSelfMatch1 getForwardLinkOfObjectHasSelfMatch1(
			ForwardLinkOfObjectHasSelf parent,
			ForwardLinkMatch1 conclusionMatch) {
		return filter(mainFactory_.getForwardLinkOfObjectHasSelfMatch1(parent,
				conclusionMatch));
	}

	@Override
	public ForwardLinkOfObjectHasSelfMatch2 getForwardLinkOfObjectHasSelfMatch2(
			ForwardLinkOfObjectHasSelfMatch1 parent,
			SubClassInclusionDecomposedMatch2 premiseMatch) {
		return filter(mainFactory_.getForwardLinkOfObjectHasSelfMatch2(parent,
				premiseMatch));
	}

	@Override
	public ForwardLinkOfObjectSomeValuesFromMatch1 getForwardLinkOfObjectSomeValuesFromMatch1(
			ForwardLinkOfObjectSomeValuesFrom parent,
			ForwardLinkMatch1 conclusionMatch) {
		return filter(mainFactory_.getForwardLinkOfObjectSomeValuesFromMatch1(
				parent, conclusionMatch));
	}

	@Override
	public ForwardLinkOfObjectSomeValuesFromMatch2 getForwardLinkOfObjectSomeValuesFromMatch2(
			ForwardLinkOfObjectSomeValuesFromMatch1 parent,
			SubClassInclusionDecomposedMatch2 premiseMatch) {
		return filter(mainFactory_.getForwardLinkOfObjectSomeValuesFromMatch2(
				parent, premiseMatch));
	}

	@Override
	public PropagationGeneratedMatch1 getPropagationGeneratedMatch1(
			PropagationGenerated parent, PropagationMatch1 conclusionMatch) {
		return filter(mainFactory_.getPropagationGeneratedMatch1(parent,
				conclusionMatch));
	}

	@Override
	public PropagationGeneratedMatch2 getPropagationGeneratedMatch2(
			PropagationGeneratedMatch1 parent,
			SubPropertyChainMatch2 thirdPremiseMatch) {
		return filter(mainFactory_.getPropagationGeneratedMatch2(parent,
				thirdPremiseMatch));
	}

	@Override
	public PropagationGeneratedMatch3 getPropagationGeneratedMatch3(
			PropagationGeneratedMatch2 parent,
			PropagationMatch3 conclusionMatch) {
		return filter(mainFactory_.getPropagationGeneratedMatch3(parent,
				conclusionMatch));
	}

	@Override
	public PropertyRangeInheritedMatch1 getPropertyRangeInheritedMatch1(
			PropertyRangeInherited parent,
			PropertyRangeMatch1 conclusionMatch) {
		return filter(mainFactory_.getPropertyRangeInheritedMatch1(parent,
				conclusionMatch));
	}

	@Override
	public PropertyRangeInheritedMatch2 getPropertyRangeInheritedMatch2(
			PropertyRangeInheritedMatch1 parent,
			IndexedObjectPropertyRangeAxiomMatch2 secondPremiseMatch) {
		return filter(mainFactory_.getPropertyRangeInheritedMatch2(parent,
				secondPremiseMatch));
	}

	@Override
	public PropertyRangeInheritedMatch3 getPropertyRangeInheritedMatch3(
			PropertyRangeInheritedMatch2 parent,
			SubPropertyChainMatch2 firstPremiseMatch) {
		return filter(mainFactory_.getPropertyRangeInheritedMatch3(parent,
				firstPremiseMatch));
	}

	@Override
	public SubClassInclusionComposedDefinedClassMatch1 getSubClassInclusionComposedDefinedClassMatch1(
			SubClassInclusionComposedDefinedClass parent,
			SubClassInclusionComposedMatch1 conclusionMatch) {
		return filter(
				mainFactory_.getSubClassInclusionComposedDefinedClassMatch1(
						parent, conclusionMatch));
	}

	@Override
	public SubClassInclusionComposedDefinedClassMatch2 getSubClassInclusionComposedDefinedClassMatch2(
			SubClassInclusionComposedDefinedClassMatch1 parent,
			IndexedEquivalentClassesAxiomMatch2 secondPremiseMatch) {
		return filter(
				mainFactory_.getSubClassInclusionComposedDefinedClassMatch2(
						parent, secondPremiseMatch));
	}

	@Override
	public SubClassInclusionComposedEntityMatch1 getSubClassInclusionComposedEntityMatch1(
			SubClassInclusionComposedEntity parent,
			SubClassInclusionComposedMatch1 conclusionMatch) {
		return filter(mainFactory_.getSubClassInclusionComposedEntityMatch1(
				parent, conclusionMatch));
	}

	@Override
	public SubClassInclusionComposedObjectIntersectionOfMatch1 getSubClassInclusionComposedObjectIntersectionOfMatch1(
			SubClassInclusionComposedObjectIntersectionOf parent,
			SubClassInclusionComposedMatch1 conclusionMatch) {
		return filter(mainFactory_
				.getSubClassInclusionComposedObjectIntersectionOfMatch1(parent,
						conclusionMatch));
	}

	@Override
	public SubClassInclusionComposedObjectSomeValuesFromMatch1 getSubClassInclusionComposedObjectSomeValuesFromMatch1(
			SubClassInclusionComposedObjectSomeValuesFrom parent,
			SubClassInclusionComposedMatch1 conclusionMatch) {
		return filter(mainFactory_
				.getSubClassInclusionComposedObjectSomeValuesFromMatch1(parent,
						conclusionMatch));
	}

	@Override
	public SubClassInclusionComposedObjectSomeValuesFromMatch2 getSubClassInclusionComposedObjectSomeValuesFromMatch2(
			SubClassInclusionComposedObjectSomeValuesFromMatch1 parent,
			PropagationMatch2 secondPremiseMatch) {
		return filter(mainFactory_
				.getSubClassInclusionComposedObjectSomeValuesFromMatch2(parent,
						secondPremiseMatch));
	}

	@Override
	public SubClassInclusionComposedObjectSomeValuesFromMatch3 getSubClassInclusionComposedObjectSomeValuesFromMatch3(
			SubClassInclusionComposedObjectSomeValuesFromMatch2 parent,
			BackwardLinkMatch2 firstPremiseMatch) {
		return filter(mainFactory_
				.getSubClassInclusionComposedObjectSomeValuesFromMatch3(parent,
						firstPremiseMatch));
	}

	@Override
	public SubClassInclusionComposedObjectUnionOfMatch1 getSubClassInclusionComposedObjectUnionOfMatch1(
			SubClassInclusionComposedObjectUnionOf parent,
			SubClassInclusionComposedMatch1 conclusionMatch) {
		return filter(
				mainFactory_.getSubClassInclusionComposedObjectUnionOfMatch1(
						parent, conclusionMatch));
	}

	@Override
	public SubClassInclusionDecomposedFirstConjunctMatch1 getSubClassInclusionDecomposedFirstConjunctMatch1(
			SubClassInclusionDecomposedFirstConjunct parent,
			SubClassInclusionDecomposedMatch1 conclusionMatch) {
		return filter(
				mainFactory_.getSubClassInclusionDecomposedFirstConjunctMatch1(
						parent, conclusionMatch));
	}

	@Override
	public SubClassInclusionDecomposedFirstConjunctMatch2 getSubClassInclusionDecomposedFirstConjunctMatch2(
			SubClassInclusionDecomposedFirstConjunctMatch1 parent,
			SubClassInclusionDecomposedMatch2 premiseMatch) {
		return filter(
				mainFactory_.getSubClassInclusionDecomposedFirstConjunctMatch2(
						parent, premiseMatch));
	}

	@Override
	public SubClassInclusionDecomposedSecondConjunctMatch1 getSubClassInclusionDecomposedSecondConjunctMatch1(
			SubClassInclusionDecomposedSecondConjunct parent,
			SubClassInclusionDecomposedMatch1 conclusionMatch) {
		return filter(
				mainFactory_.getSubClassInclusionDecomposedSecondConjunctMatch1(
						parent, conclusionMatch));
	}

	@Override
	public SubClassInclusionDecomposedSecondConjunctMatch2 getSubClassInclusionDecomposedSecondConjunctMatch2(
			SubClassInclusionDecomposedSecondConjunctMatch1 parent,
			SubClassInclusionDecomposedMatch2 premiseMatch) {
		return filter(
				mainFactory_.getSubClassInclusionDecomposedSecondConjunctMatch2(
						parent, premiseMatch));
	}

	@Override
	public SubClassInclusionExpandedDefinitionMatch1 getSubClassInclusionExpandedDefinitionMatch1(
			SubClassInclusionExpandedDefinition parent,
			SubClassInclusionDecomposedMatch1 conclusionMatch) {
		return filter(mainFactory_.getSubClassInclusionExpandedDefinitionMatch1(
				parent, conclusionMatch));
	}

	@Override
	public SubClassInclusionExpandedDefinitionMatch2 getSubClassInclusionExpandedDefinitionMatch2(
			SubClassInclusionExpandedDefinitionMatch1 parent,
			IndexedEquivalentClassesAxiomMatch2 secondPremiseMatch) {
		return filter(mainFactory_.getSubClassInclusionExpandedDefinitionMatch2(
				parent, secondPremiseMatch));
	}

	@Override
	public SubClassInclusionExpandedFirstEquivalentClassMatch1 getSubClassInclusionExpandedFirstEquivalentClassMatch1(
			SubClassInclusionExpandedFirstEquivalentClass parent,
			SubClassInclusionDecomposedMatch1 conclusionMatch) {
		return filter(mainFactory_
				.getSubClassInclusionExpandedFirstEquivalentClassMatch1(parent,
						conclusionMatch));
	}

	@Override
	public SubClassInclusionExpandedFirstEquivalentClassMatch2 getSubClassInclusionExpandedFirstEquivalentClassMatch2(
			SubClassInclusionExpandedFirstEquivalentClassMatch1 parent,
			IndexedEquivalentClassesAxiomMatch2 secondPremiseMatch) {
		return filter(mainFactory_
				.getSubClassInclusionExpandedFirstEquivalentClassMatch2(parent,
						secondPremiseMatch));
	}

	@Override
	public SubClassInclusionExpandedSecondEquivalentClassMatch1 getSubClassInclusionExpandedSecondEquivalentClassMatch1(
			SubClassInclusionExpandedSecondEquivalentClass parent,
			SubClassInclusionDecomposedMatch1 conclusionMatch) {
		return filter(mainFactory_
				.getSubClassInclusionExpandedSecondEquivalentClassMatch1(parent,
						conclusionMatch));
	}

	@Override
	public SubClassInclusionExpandedSecondEquivalentClassMatch2 getSubClassInclusionExpandedSecondEquivalentClassMatch2(
			SubClassInclusionExpandedSecondEquivalentClassMatch1 parent,
			IndexedEquivalentClassesAxiomMatch2 secondPremiseMatch) {
		return filter(mainFactory_
				.getSubClassInclusionExpandedSecondEquivalentClassMatch2(parent,
						secondPremiseMatch));
	}

	@Override
	public SubClassInclusionExpandedSubClassOfMatch1 getSubClassInclusionExpandedSubClassOfMatch1(
			SubClassInclusionExpandedSubClassOf parent,
			SubClassInclusionDecomposedMatch1 conclusionMatch) {
		return filter(mainFactory_.getSubClassInclusionExpandedSubClassOfMatch1(
				parent, conclusionMatch));
	}

	@Override
	public SubClassInclusionExpandedSubClassOfMatch2 getSubClassInclusionExpandedSubClassOfMatch2(
			SubClassInclusionExpandedSubClassOfMatch1 parent,
			IndexedSubClassOfAxiomMatch2 secondPremiseMatch) {
		return filter(mainFactory_.getSubClassInclusionExpandedSubClassOfMatch2(
				parent, secondPremiseMatch));
	}

	@Override
	public SubClassInclusionObjectHasSelfPropertyRangeMatch1 getSubClassInclusionObjectHasSelfPropertyRangeMatch1(
			SubClassInclusionObjectHasSelfPropertyRange parent,
			SubClassInclusionDecomposedMatch1 conclusionMatch) {
		return filter(mainFactory_
				.getSubClassInclusionObjectHasSelfPropertyRangeMatch1(parent,
						conclusionMatch));
	}

	@Override
	public SubClassInclusionObjectHasSelfPropertyRangeMatch2 getSubClassInclusionObjectHasSelfPropertyRangeMatch2(
			SubClassInclusionObjectHasSelfPropertyRangeMatch1 parent,
			SubClassInclusionDecomposedMatch2 firstPremiseMatch) {
		return filter(mainFactory_
				.getSubClassInclusionObjectHasSelfPropertyRangeMatch2(parent,
						firstPremiseMatch));
	}

	@Override
	public SubClassInclusionObjectHasSelfPropertyRangeMatch3 getSubClassInclusionObjectHasSelfPropertyRangeMatch3(
			SubClassInclusionObjectHasSelfPropertyRangeMatch2 parent,
			PropertyRangeMatch2 secondPremiseMatch) {
		return filter(mainFactory_
				.getSubClassInclusionObjectHasSelfPropertyRangeMatch3(parent,
						secondPremiseMatch));
	}

	@Override
	public SubClassInclusionOwlThingMatch1 getSubClassInclusionOwlThingMatch1(
			SubClassInclusionOwlThing parent,
			SubClassInclusionComposedMatch1 conclusionMatch) {
		return filter(mainFactory_.getSubClassInclusionOwlThingMatch1(parent,
				conclusionMatch));
	}

	@Override
	public SubClassInclusionRangeMatch1 getSubClassInclusionRangeMatch1(
			SubClassInclusionRange parent,
			SubClassInclusionDecomposedMatch1 conclusionMatch) {
		return filter(mainFactory_.getSubClassInclusionRangeMatch1(parent,
				conclusionMatch));
	}

	@Override
	public SubClassInclusionRangeMatch2 getSubClassInclusionRangeMatch2(
			SubClassInclusionRangeMatch1 parent,
			PropertyRangeMatch2 premiseMatch) {
		return filter(mainFactory_.getSubClassInclusionRangeMatch2(parent,
				premiseMatch));
	}

	@Override
	public SubClassInclusionTautologyMatch1 getSubClassInclusionTautologyMatch1(
			SubClassInclusionTautology parent,
			SubClassInclusionDecomposedMatch1 conclusionMatch) {
		return filter(mainFactory_.getSubClassInclusionTautologyMatch1(parent,
				conclusionMatch));
	}

	@Override
	public SubPropertyChainExpandedSubObjectPropertyOfMatch1 getSubPropertyChainExpandedSubObjectPropertyOfMatch1(
			SubPropertyChainExpandedSubObjectPropertyOf parent,
			SubPropertyChainMatch1 conclusionMatch) {
		return filter(mainFactory_
				.getSubPropertyChainExpandedSubObjectPropertyOfMatch1(parent,
						conclusionMatch));
	}

	@Override
	public SubPropertyChainExpandedSubObjectPropertyOfMatch2 getSubPropertyChainExpandedSubObjectPropertyOfMatch2(
			SubPropertyChainExpandedSubObjectPropertyOfMatch1 parent,
			IndexedSubObjectPropertyOfAxiomMatch2 secondPremiseMatch) {
		return filter(mainFactory_
				.getSubPropertyChainExpandedSubObjectPropertyOfMatch2(parent,
						secondPremiseMatch));
	}

	@Override
	public SubPropertyChainTautologyMatch1 getSubPropertyChainTautologyMatch1(
			SubPropertyChainTautology parent,
			SubPropertyChainMatch1 conclusionMatch) {
		return filter(mainFactory_.getSubPropertyChainTautologyMatch1(parent,
				conclusionMatch));
	}

}
