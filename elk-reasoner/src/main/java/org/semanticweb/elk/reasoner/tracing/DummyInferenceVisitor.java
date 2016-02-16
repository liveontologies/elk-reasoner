/**
 * 
 */
package org.semanticweb.elk.reasoner.tracing;

import org.semanticweb.elk.reasoner.indexing.model.ElkClassAssertionAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkDeclarationAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkDifferentIndividualsAxiomBinaryConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkDifferentIndividualsAxiomNaryConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkDisjointClassesAxiomBinaryConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkDisjointClassesAxiomNaryConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkDisjointUnionAxiomBinaryConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkDisjointUnionAxiomDefinitionConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkDisjointUnionAxiomNaryConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkDisjointUnionAxiomSubClassConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkEquivalentClassesAxiomDefinitionConversion;
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
import org.semanticweb.elk.reasoner.saturation.inferences.BackwardLinkReversed;
import org.semanticweb.elk.reasoner.saturation.inferences.BackwardLinkReversedExpanded;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.inferences.ContextInitializationNoPremises;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInconsistencyOfDisjointSubsumers;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInconsistencyOfObjectComplementOf;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInconsistencyOfOwlNothing;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInconsistencyPropagated;
import org.semanticweb.elk.reasoner.saturation.inferences.DisjointSubsumerFromSubsumer;
import org.semanticweb.elk.reasoner.saturation.inferences.ForwardLinkComposition;
import org.semanticweb.elk.reasoner.saturation.inferences.ForwardLinkOfObjectHasSelf;
import org.semanticweb.elk.reasoner.saturation.inferences.ForwardLinkOfObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.inferences.PropagationGenerated;
import org.semanticweb.elk.reasoner.saturation.inferences.SaturationInference;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionComposedDefinedClass;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionComposedEntity;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionComposedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionComposedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionComposedObjectUnionOf;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionDecomposedFirstConjunct;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionDecomposedSecondConjunct;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionExpandedDefinition;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionExpandedSubClassOf;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionObjectHasSelfPropertyRange;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionOwlThing;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionRange;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionTautology;
import org.semanticweb.elk.reasoner.saturation.inferences.SubContextInitializationNoPremises;
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
 * An {@link Inference.Visitor} that always returns {@code null}.
 * 
 * @author Yevgeny Kazakov
 */
public class DummyInferenceVisitor<O> implements Inference.Visitor<O> {

	protected O defaultVisit(ClassInference inference) {
		return defaultVisit((SaturationInference) inference);
	}

	protected O defaultVisit(@SuppressWarnings("unused") Inference inference) {
		// can be overriden in sub-classes
		return null;
	}

	protected O defaultVisit(ObjectPropertyInference inference) {
		return defaultVisit((SaturationInference) inference);
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
	public O visit(ClassInconsistencyOfDisjointSubsumers inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ClassInconsistencyOfObjectComplementOf inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ClassInconsistencyOfOwlNothing inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ClassInconsistencyPropagated inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(DisjointSubsumerFromSubsumer inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkClassAssertionAxiomConversion inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkDeclarationAxiomConversion inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkDifferentIndividualsAxiomBinaryConversion inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkDifferentIndividualsAxiomNaryConversion inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkDisjointClassesAxiomBinaryConversion inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkDisjointClassesAxiomNaryConversion inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkDisjointUnionAxiomBinaryConversion inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkDisjointUnionAxiomDefinitionConversion inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkDisjointUnionAxiomNaryConversion inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkDisjointUnionAxiomSubClassConversion inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkEquivalentClassesAxiomDefinitionConversion inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkEquivalentClassesAxiomSubClassConversion inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkEquivalentObjectPropertiesAxiomConversion inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkObjectPropertyAssertionAxiomConversion inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkObjectPropertyDomainAxiomConversion inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkObjectPropertyRangeAxiomConversion inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkReflexiveObjectPropertyAxiomConversion inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkSameIndividualAxiomConversion inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkSubClassOfAxiomConversion inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkSubObjectPropertyOfAxiomConversion inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkTransitiveObjectPropertyAxiomConversion inference) {
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
