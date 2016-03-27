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

public class InferenceMatchPrinter implements InferenceMatch.Visitor<String> {

	private static InferenceMatchPrinter INSTANCE_ = new InferenceMatchPrinter();

	static InferenceMatch.Visitor<String> getPrinterVisitor() {
		return INSTANCE_;
	}

	public static String toString(InferenceMatch inferenceMatch) {
		return inferenceMatch.accept(INSTANCE_);
	}

	private InferenceMatchPrinter() {

	}

	@Override
	public String visit(BackwardLinkCompositionMatch1 inferenceMatch1) {
		return inferenceMatch1.getParent() + " | ";
	}

	@Override
	public String visit(BackwardLinkCompositionMatch2 inferenceMatch2) {
		return inferenceMatch2.getParent() + " | ";
	}

	@Override
	public String visit(BackwardLinkCompositionMatch3 inferenceMatch3) {
		return inferenceMatch3.getParent() + " | ";
	}

	@Override
	public String visit(BackwardLinkCompositionMatch4 inferenceMatch4) {
		return inferenceMatch4.getParent() + " | ";
	}

	@Override
	public String visit(BackwardLinkCompositionMatch5 inferenceMatch5) {
		return inferenceMatch5.getParent() + " | ";
	}

	@Override
	public String visit(BackwardLinkCompositionMatch6 inferenceMatch6) {
		return inferenceMatch6.getParent() + " | ";
	}

	@Override
	public String visit(BackwardLinkOfObjectHasSelfMatch1 inferenceMatch1) {
		return inferenceMatch1.getParent() + " | ";
	}

	@Override
	public String visit(BackwardLinkOfObjectHasSelfMatch2 inferenceMatch2) {
		return inferenceMatch2.getParent() + " | ";
	}

	@Override
	public String visit(
			BackwardLinkOfObjectSomeValuesFromMatch1 inferenceMatch1) {
		return inferenceMatch1.getParent() + " | ";
	}

	@Override
	public String visit(
			BackwardLinkOfObjectSomeValuesFromMatch2 inferenceMatch2) {
		return inferenceMatch2.getParent() + " | ";
	}

	@Override
	public String visit(BackwardLinkReversedExpandedMatch1 inferenceMatch1) {
		return inferenceMatch1.getParent() + " | ";
	}

	@Override
	public String visit(BackwardLinkReversedExpandedMatch2 inferenceMatch2) {
		return inferenceMatch2.getParent() + " | ";
	}

	@Override
	public String visit(BackwardLinkReversedExpandedMatch3 inferenceMatch3) {
		return inferenceMatch3.getParent() + " | ";
	}

	@Override
	public String visit(BackwardLinkReversedMatch1 inferenceMatch1) {
		return inferenceMatch1.getParent() + " | ";
	}

	@Override
	public String visit(BackwardLinkReversedMatch2 inferenceMatch2) {
		return inferenceMatch2.getParent() + " | ";
	}

	@Override
	public String visit(
			ElkClassAssertionAxiomConversionMatch1 inferenceMatch1) {
		return inferenceMatch1.getParent() + " | ";
	}

	@Override
	public String visit(
			ElkDifferentIndividualsAxiomBinaryConversionMatch1 inferenceMatch1) {
		return inferenceMatch1.getParent() + " | ";
	}

	@Override
	public String visit(
			ElkDisjointClassesAxiomBinaryConversionMatch1 inferenceMatch1) {
		return inferenceMatch1.getParent() + " | ";
	}

	@Override
	public String visit(
			ElkDisjointUnionAxiomBinaryConversionMatch1 inferenceMatch1) {
		return inferenceMatch1.getParent() + " | ";
	}

	@Override
	public String visit(
			ElkDisjointUnionAxiomSubClassConversionMatch1 inferenceMatch1) {
		return inferenceMatch1.getParent() + " | ";
	}

	@Override
	public String visit(
			ElkEquivalentClassesAxiomDefinitionConversionMatch1 inferenceMatch1) {
		return inferenceMatch1.getParent() + " | ";
	}

	@Override
	public String visit(
			ElkEquivalentClassesAxiomSubClassConversionMatch1 inferenceMatch1) {
		return inferenceMatch1.getParent() + " | ";
	}

	@Override
	public String visit(
			ElkObjectPropertyAssertionAxiomConversionMatch1 inferenceMatch1) {
		return inferenceMatch1.getParent() + " | ";
	}

	@Override
	public String visit(
			ElkObjectPropertyDomainAxiomConversionMatch1 inferenceMatch1) {
		return inferenceMatch1.getParent() + " | ";
	}

	@Override
	public String visit(
			ElkReflexiveObjectPropertyAxiomConversionMatch1 inferenceMatch1) {
		return inferenceMatch1.getParent() + " | ";
	}

	@Override
	public String visit(
			ElkSameIndividualAxiomConversionMatch1 inferenceMatch1) {
		return inferenceMatch1.getParent() + " | ";
	}

	@Override
	public String visit(ElkSubClassOfAxiomConversionMatch1 inferenceMatch1) {
		return inferenceMatch1.getParent() + " | ";
	}

	@Override
	public String visit(
			ElkSubObjectPropertyOfAxiomConversionMatch1 inferenceMatch1) {
		return inferenceMatch1.getParent() + " | ";
	}

	@Override
	public String visit(
			ElkTransitiveObjectPropertyAxiomConversionMatch1 inferenceMatch1) {
		return inferenceMatch1.getParent() + " | ";
	}

	@Override
	public String visit(ForwardLinkCompositionMatch1 inferenceMatch1) {
		return inferenceMatch1.getParent() + " | ";
	}

	@Override
	public String visit(ForwardLinkCompositionMatch2 inferenceMatch2) {
		return inferenceMatch2.getParent() + " | ";
	}

	@Override
	public String visit(ForwardLinkCompositionMatch3 inferenceMatch3) {
		return inferenceMatch3.getParent() + " | ";
	}

	@Override
	public String visit(ForwardLinkCompositionMatch4 inferenceMatch4) {
		return inferenceMatch4.getParent() + " | ";
	}

	@Override
	public String visit(ForwardLinkCompositionMatch5 inferenceMatch5) {
		return inferenceMatch5.getParent() + " | ";
	}

	@Override
	public String visit(ForwardLinkOfObjectHasSelfMatch1 inferenceMatch1) {
		return inferenceMatch1.getParent() + " | ";
	}

	@Override
	public String visit(ForwardLinkOfObjectHasSelfMatch2 inferenceMatch2) {
		return inferenceMatch2.getParent() + " | ";
	}

	@Override
	public String visit(
			ForwardLinkOfObjectSomeValuesFromMatch1 inferenceMatch1) {
		return inferenceMatch1.getParent() + " | ";
	}

	@Override
	public String visit(
			ForwardLinkOfObjectSomeValuesFromMatch2 inferenceMatch2) {
		return inferenceMatch2.getParent() + " | ";
	}

	@Override
	public String visit(PropagationGeneratedMatch1 inferenceMatch1) {
		return inferenceMatch1.getParent() + " | ";
	}

	@Override
	public String visit(PropagationGeneratedMatch2 inferenceMatch2) {
		return inferenceMatch2.getParent() + " | ";
	}

	@Override
	public String visit(PropagationGeneratedMatch3 inferenceMatch3) {
		return inferenceMatch3.getParent() + " | ";
	}

	@Override
	public String visit(PropertyRangeInheritedMatch1 inferenceMatch1) {
		return inferenceMatch1.getParent() + " | ";
	}

	@Override
	public String visit(PropertyRangeInheritedMatch2 inferenceMatch2) {
		return inferenceMatch2.getParent() + " | ";
	}

	@Override
	public String visit(PropertyRangeInheritedMatch3 inferenceMatch3) {
		return inferenceMatch3.getParent() + " | ";
	}

	@Override
	public String visit(
			SubClassInclusionComposedDefinedClassMatch1 inferenceMatch1) {
		return inferenceMatch1.getParent() + " | ";
	}

	@Override
	public String visit(
			SubClassInclusionComposedDefinedClassMatch2 inferenceMatch2) {
		return inferenceMatch2.getParent() + " | ";
	}

	@Override
	public String visit(SubClassInclusionComposedEntityMatch1 inferenceMatch1) {
		return inferenceMatch1.getParent() + " | ";
	}

	@Override
	public String visit(
			SubClassInclusionComposedObjectIntersectionOfMatch1 inferenceMatch1) {
		return inferenceMatch1.getParent() + " | ";
	}

	@Override
	public String visit(
			SubClassInclusionComposedObjectSomeValuesFromMatch1 inferenceMatch1) {
		return inferenceMatch1.getParent() + " | ";
	}

	@Override
	public String visit(
			SubClassInclusionComposedObjectSomeValuesFromMatch2 inferenceMatch2) {
		return inferenceMatch2.getParent() + " | ";
	}

	@Override
	public String visit(
			SubClassInclusionComposedObjectSomeValuesFromMatch3 inferenceMatch3) {
		return inferenceMatch3.getParent() + " | ";
	}

	@Override
	public String visit(
			SubClassInclusionComposedObjectUnionOfMatch1 inferenceMatch1) {
		return inferenceMatch1.getParent() + " | ";
	}

	@Override
	public String visit(
			SubClassInclusionDecomposedFirstConjunctMatch1 inferenceMatch1) {
		return inferenceMatch1.getParent() + " | ";
	}

	@Override
	public String visit(
			SubClassInclusionDecomposedFirstConjunctMatch2 inferenceMatch2) {
		return inferenceMatch2.getParent() + " | ";
	}

	@Override
	public String visit(
			SubClassInclusionDecomposedSecondConjunctMatch1 inferenceMatch1) {
		return inferenceMatch1.getParent() + " | ";
	}

	@Override
	public String visit(
			SubClassInclusionDecomposedSecondConjunctMatch2 inferenceMatch2) {
		return inferenceMatch2.getParent() + " | ";
	}

	@Override
	public String visit(
			SubClassInclusionExpandedDefinitionMatch1 inferenceMatch1) {
		return inferenceMatch1.getParent() + " | ";
	}

	@Override
	public String visit(
			SubClassInclusionExpandedDefinitionMatch2 inferenceMatch2) {
		return inferenceMatch2.getParent() + " | ";
	}

	@Override
	public String visit(
			SubClassInclusionExpandedSubClassOfMatch1 inferenceMatch1) {
		return inferenceMatch1.getParent() + " | ";
	}

	@Override
	public String visit(
			SubClassInclusionExpandedSubClassOfMatch2 inferenceMatch2) {
		return inferenceMatch2.getParent() + " | ";
	}

	@Override
	public String visit(
			SubClassInclusionObjectHasSelfPropertyRangeMatch1 inferenceMatch1) {
		return inferenceMatch1.getParent() + " | ";
	}

	@Override
	public String visit(
			SubClassInclusionObjectHasSelfPropertyRangeMatch2 inferenceMatch2) {
		return inferenceMatch2.getParent() + " | ";
	}

	@Override
	public String visit(
			SubClassInclusionObjectHasSelfPropertyRangeMatch3 inferenceMatch3) {
		return inferenceMatch3.getParent() + " | ";
	}

	@Override
	public String visit(SubClassInclusionOwlThingMatch1 inferenceMatch1) {
		return inferenceMatch1.getParent() + " | ";
	}

	@Override
	public String visit(SubClassInclusionRangeMatch1 inferenceMatch1) {
		return inferenceMatch1.getParent() + " | ";
	}

	@Override
	public String visit(SubClassInclusionRangeMatch2 inferenceMatch2) {
		return inferenceMatch2.getParent() + " | ";
	}

	@Override
	public String visit(SubClassInclusionTautologyMatch1 inferenceMatch1) {
		return inferenceMatch1.getParent() + " | ";
	}

	@Override
	public String visit(
			SubPropertyChainExpandedSubObjectPropertyOfMatch1 inferenceMatch1) {
		return inferenceMatch1.getParent() + " | ";
	}

	@Override
	public String visit(
			SubPropertyChainExpandedSubObjectPropertyOfMatch2 inferenceMatch2) {
		return inferenceMatch2.getParent() + " | ";
	}

	@Override
	public String visit(SubPropertyChainTautologyMatch1 inferenceMatch1) {
		return inferenceMatch1.getParent() + " | ";
	}

}
