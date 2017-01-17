package org.semanticweb.elk.reasoner.tracing;

/*
 * #%L
 * ELK Reasoner
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

import org.semanticweb.elk.reasoner.indexing.model.ElkClassAssertionAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkDeclarationAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkDifferentIndividualsAxiomBinaryConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkDifferentIndividualsAxiomNaryConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkDisjointClassesAxiomBinaryConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkDisjointClassesAxiomNaryConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkDisjointUnionAxiomBinaryConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkDisjointUnionAxiomEquivalenceConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkDisjointUnionAxiomNaryConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkDisjointUnionAxiomOwlNothingConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkDisjointUnionAxiomSubClassConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkEquivalentClassesAxiomEquivalenceConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkEquivalentClassesAxiomSubClassConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkEquivalentObjectPropertiesAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkObjectPropertyAssertionAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkObjectPropertyDomainAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkObjectPropertyRangeAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkReflexiveObjectPropertyAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkSameIndividualAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkSubClassOfAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkSubObjectPropertyOfAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkTransitiveObjectPropertyAxiomConversion;
import org.semanticweb.elk.reasoner.saturation.inferences.BackwardLinkComposition;
import org.semanticweb.elk.reasoner.saturation.inferences.BackwardLinkOfObjectHasSelf;
import org.semanticweb.elk.reasoner.saturation.inferences.BackwardLinkOfObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.inferences.BackwardLinkReversedExpanded;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInconsistencyOfDisjointSubsumers;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInconsistencyOfObjectComplementOf;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInconsistencyOfOwlNothing;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInconsistencyPropagated;
import org.semanticweb.elk.reasoner.saturation.inferences.ContextInitializationNoPremises;
import org.semanticweb.elk.reasoner.saturation.inferences.DisjointSubsumerFromSubsumer;
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
import org.semanticweb.elk.reasoner.saturation.inferences.SubContextInitializationNoPremises;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.PropertyRangeInherited;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.SubPropertyChainExpandedSubObjectPropertyOf;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.SubPropertyChainTautology;

public class TracingInferencePrinter implements TracingInference.Visitor<String> {

	private static TracingInferencePrinter INSTANCE_ = new TracingInferencePrinter();

	static TracingInference.Visitor<String> getPrinterVisitor() {
		return INSTANCE_;
	}

	public static String toString(TracingInference inference) {
		return inference.accept(INSTANCE_);
	}

	private final Conclusion.Factory factory_ = new ConclusionBaseFactory();

	private TracingInferencePrinter() {

	}

	@Override
	public String visit(BackwardLinkComposition inference) {
		return String.format("%s -| %s; %s; %s; %s; %s",
				inference.getConclusion(factory_),
				inference.getFirstPremise(factory_),
				inference.getSecondPremise(factory_),
				inference.getThirdPremise(factory_),
				inference.getFourthPremise(factory_),
				inference.getFifthPremise(factory_));
	}

	@Override
	public String visit(BackwardLinkOfObjectHasSelf inference) {
		return String.format("%s -| %s", inference.getConclusion(factory_),
				inference.getPremise(factory_));
	}

	@Override
	public String visit(BackwardLinkOfObjectSomeValuesFrom inference) {
		return String.format("%s -| %s", inference.getConclusion(factory_),
				inference.getPremise(factory_));
	}

	@Override
	public String visit(BackwardLinkReversedExpanded inference) {
		return String.format("%s -| %s; %s", inference.getConclusion(factory_),
				inference.getFirstPremise(factory_),
				inference.getSecondPremise(factory_));
	}

	@Override
	public String visit(ClassInconsistencyOfDisjointSubsumers inference) {
		return String.format("%s -| %s; %s",
				inference.getConclusion(factory_),
				inference.getFirstPremise(factory_),
				inference.getSecondPremise(factory_));
	}

	@Override
	public String visit(ClassInconsistencyOfObjectComplementOf inference) {
		return String.format("%s -| %s; %s", inference.getConclusion(factory_),
				inference.getFirstPremise(factory_),
				inference.getSecondPremise(factory_));
	}

	@Override
	public String visit(ClassInconsistencyOfOwlNothing inference) {
		return String.format("%s -| %s", inference.getConclusion(factory_),
				inference.getPremise(factory_));
	}

	@Override
	public String visit(ClassInconsistencyPropagated inference) {
		return String.format("%s -| %s; %s", inference.getConclusion(factory_),
				inference.getFirstPremise(factory_),
				inference.getSecondPremise(factory_));
	}

	@Override
	public String visit(ContextInitializationNoPremises inference) {
		return String.format("%s -|", inference.getConclusion(factory_));
	}

	@Override
	public String visit(DisjointSubsumerFromSubsumer inference) {
		return String.format("%s -| %s; %s", inference.getConclusion(factory_),
				inference.getFirstPremise(factory_),
				inference.getSecondPremise(factory_));
	}

	@Override
	public String visit(ElkClassAssertionAxiomConversion inference) {
		return String.format("%s -| %s", inference.getConclusion(factory_),
				inference.getOriginalAxiom());
	}

	@Override
	public String visit(ElkDeclarationAxiomConversion inference) {
		return String.format("%s -| %s", inference.getConclusion(factory_),
				inference.getOriginalAxiom());
	}

	@Override
	public String visit(
			ElkDifferentIndividualsAxiomBinaryConversion inference) {
		return String.format("%s -| %s", inference.getConclusion(factory_),
				inference.getOriginalAxiom());
	}

	@Override
	public String visit(ElkDifferentIndividualsAxiomNaryConversion inference) {
		return String.format("%s -| %s", inference.getConclusion(factory_),
				inference.getOriginalAxiom());
	}

	@Override
	public String visit(ElkDisjointClassesAxiomBinaryConversion inference) {
		return String.format("%s -| %s", inference.getConclusion(factory_),
				inference.getOriginalAxiom());
	}

	@Override
	public String visit(ElkDisjointClassesAxiomNaryConversion inference) {
		return String.format("%s -| %s", inference.getConclusion(factory_),
				inference.getOriginalAxiom());
	}

	@Override
	public String visit(ElkDisjointUnionAxiomBinaryConversion inference) {
		return String.format("%s -| %s", inference.getConclusion(factory_),
				inference.getOriginalAxiom());
	}

	@Override
	public String visit(ElkDisjointUnionAxiomEquivalenceConversion inference) {
		return String.format("%s -| %s", inference.getConclusion(factory_),
				inference.getOriginalAxiom());
	}

	@Override
	public String visit(ElkDisjointUnionAxiomNaryConversion inference) {
		return String.format("%s -| %s", inference.getConclusion(factory_),
				inference.getOriginalAxiom());
	}

	@Override
	public String visit(ElkDisjointUnionAxiomOwlNothingConversion inference) {
		return String.format("%s -| %s", inference.getConclusion(factory_),
				inference.getOriginalAxiom());
	}

	@Override
	public String visit(ElkDisjointUnionAxiomSubClassConversion inference) {
		return String.format("%s -| %s", inference.getConclusion(factory_),
				inference.getOriginalAxiom());
	}

	@Override
	public String visit(
			ElkEquivalentClassesAxiomEquivalenceConversion inference) {
		return String.format("%s -| %s", inference.getConclusion(factory_),
				inference.getOriginalAxiom());
	}

	@Override
	public String visit(ElkEquivalentClassesAxiomSubClassConversion inference) {
		return String.format("%s -| %s", inference.getConclusion(factory_),
				inference.getOriginalAxiom());
	}

	@Override
	public String visit(
			ElkEquivalentObjectPropertiesAxiomConversion inference) {
		return String.format("%s -| %s", inference.getConclusion(factory_),
				inference.getOriginalAxiom());
	}

	@Override
	public String visit(ElkObjectPropertyAssertionAxiomConversion inference) {
		return String.format("%s -| %s", inference.getConclusion(factory_),
				inference.getOriginalAxiom());
	}

	@Override
	public String visit(ElkObjectPropertyDomainAxiomConversion inference) {
		return String.format("%s -| %s", inference.getConclusion(factory_),
				inference.getOriginalAxiom());
	}

	@Override
	public String visit(ElkObjectPropertyRangeAxiomConversion inference) {
		return String.format("%s -| %s", inference.getConclusion(factory_),
				inference.getOriginalAxiom());
	}

	@Override
	public String visit(ElkReflexiveObjectPropertyAxiomConversion inference) {
		return String.format("%s -| %s", inference.getConclusion(factory_),
				inference.getOriginalAxiom());
	}

	@Override
	public String visit(ElkSameIndividualAxiomConversion inference) {
		return String.format("%s -| %s", inference.getConclusion(factory_),
				inference.getOriginalAxiom());
	}

	@Override
	public String visit(ElkSubClassOfAxiomConversion inference) {
		return String.format("%s -| %s", inference.getConclusion(factory_),
				inference.getOriginalAxiom());
	}

	@Override
	public String visit(ElkSubObjectPropertyOfAxiomConversion inference) {
		return String.format("%s -| %s", inference.getConclusion(factory_),
				inference.getOriginalAxiom());
	}

	@Override
	public String visit(ElkTransitiveObjectPropertyAxiomConversion inference) {
		return String.format("%s -| %s", inference.getConclusion(factory_),
				inference.getOriginalAxiom());
	}

	@Override
	public String visit(ForwardLinkComposition inference) {
		return String.format("%s -| %s; %s; %s; %s",
				inference.getConclusion(factory_),
				inference.getFirstPremise(factory_),
				inference.getSecondPremise(factory_),
				inference.getThirdPremise(factory_),
				inference.getFourthPremise(factory_));
	}

	@Override
	public String visit(ForwardLinkOfObjectHasSelf inference) {
		return String.format("%s -| %s", inference.getConclusion(factory_),
				inference.getPremise(factory_));
	}

	@Override
	public String visit(ForwardLinkOfObjectSomeValuesFrom inference) {
		return String.format("%s -| %s", inference.getConclusion(factory_),
				inference.getPremise(factory_));
	}

	@Override
	public String visit(PropagationGenerated inference) {
		return String.format("%s -| %s; %s; %s",
				inference.getConclusion(factory_),
				inference.getFirstPremise(factory_),
				inference.getSecondPremise(factory_),
				inference.getThirdPremise(factory_));
	}

	@Override
	public String visit(PropertyRangeInherited inference) {
		return String.format("%s -| %s; %s", inference.getConclusion(factory_),
				inference.getFirstPremise(factory_),
				inference.getSecondPremise(factory_));
	}

	@Override
	public String visit(SubClassInclusionComposedDefinedClass inference) {
		return String.format("%s -| %s; %s", inference.getConclusion(factory_),
				inference.getFirstPremise(factory_),
				inference.getSecondPremise(factory_));
	}

	@Override
	public String visit(SubClassInclusionComposedEntity inference) {
		return String.format("%s -| %s", inference.getConclusion(factory_),
				inference.getPremise(factory_));
	}

	@Override
	public String visit(
			SubClassInclusionComposedObjectIntersectionOf inference) {
		return String.format("%s -| %s; %s", inference.getConclusion(factory_),
				inference.getFirstPremise(factory_),
				inference.getSecondPremise(factory_));
	}

	@Override
	public String visit(
			SubClassInclusionComposedObjectSomeValuesFrom inference) {
		return String.format("%s -| %s; %s", inference.getConclusion(factory_),
				inference.getFirstPremise(factory_),
				inference.getSecondPremise(factory_));
	}

	@Override
	public String visit(SubClassInclusionComposedObjectUnionOf inference) {
		return String.format("%s -| %s", inference.getConclusion(factory_),
				inference.getPremise(factory_));
	}

	@Override
	public String visit(SubClassInclusionDecomposedFirstConjunct inference) {
		return String.format("%s -| %s", inference.getConclusion(factory_),
				inference.getPremise(factory_));
	}

	@Override
	public String visit(SubClassInclusionDecomposedSecondConjunct inference) {
		return String.format("%s -| %s", inference.getConclusion(factory_),
				inference.getPremise(factory_));
	}

	@Override
	public String visit(SubClassInclusionExpandedDefinition inference) {
		return String.format("%s -| %s; %s", inference.getConclusion(factory_),
				inference.getFirstPremise(factory_),
				inference.getSecondPremise(factory_));
	}

	@Override
	public String visit(
			SubClassInclusionExpandedFirstEquivalentClass inference) {
		return String.format("%s -| %s; %s", inference.getConclusion(factory_),
				inference.getFirstPremise(factory_),
				inference.getSecondPremise(factory_));
	}

	@Override
	public String visit(
			SubClassInclusionExpandedSecondEquivalentClass inference) {
		return String.format("%s -| %s; %s", inference.getConclusion(factory_),
				inference.getFirstPremise(factory_),
				inference.getSecondPremise(factory_));
	}

	@Override
	public String visit(SubClassInclusionExpandedSubClassOf inference) {
		return String.format("%s -| %s; %s", inference.getConclusion(factory_),
				inference.getFirstPremise(factory_),
				inference.getSecondPremise(factory_));
	}

	@Override
	public String visit(SubClassInclusionObjectHasSelfPropertyRange inference) {
		return String.format("%s -| %s; %s", inference.getConclusion(factory_),
				inference.getFirstPremise(factory_),
				inference.getSecondPremise(factory_));
	}

	@Override
	public String visit(SubClassInclusionOwlThing inference) {
		return String.format("%s -| %s", inference.getConclusion(factory_),
				inference.getPremise(factory_));
	}

	@Override
	public String visit(SubClassInclusionRange inference) {
		return String.format("%s -| %s; %s", inference.getConclusion(factory_),
				inference.getFirstPremise(factory_),
				inference.getSecondPremise(factory_));
	}

	@Override
	public String visit(SubClassInclusionTautology inference) {
		return String.format("%s -| %s", inference.getConclusion(factory_),
				inference.getPremise(factory_));
	}

	@Override
	public String visit(SubContextInitializationNoPremises inference) {
		return String.format("%s -|", inference.getConclusion(factory_));
	}

	@Override
	public String visit(SubPropertyChainExpandedSubObjectPropertyOf inference) {
		return String.format("%s -| %s; %s", inference.getConclusion(factory_),
				inference.getFirstPremise(factory_),
				inference.getSecondPremise(factory_));
	}

	@Override
	public String visit(SubPropertyChainTautology inference) {
		return String.format("%s -|", inference.getConclusion(factory_));
	}

}
