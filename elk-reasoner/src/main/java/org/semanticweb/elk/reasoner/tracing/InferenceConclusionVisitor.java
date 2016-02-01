/**
 * 
 */
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
import org.semanticweb.elk.reasoner.saturation.inferences.ContextInitializationNoPremises;
import org.semanticweb.elk.reasoner.saturation.inferences.ContradictionOfDisjointSubsumers;
import org.semanticweb.elk.reasoner.saturation.inferences.ContradictionOfObjectComplementOf;
import org.semanticweb.elk.reasoner.saturation.inferences.ContradictionOfOwlNothing;
import org.semanticweb.elk.reasoner.saturation.inferences.ContradictionPropagated;
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
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionExpandedSubClassOf;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionObjectHasSelfPropertyRange;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionOwlThing;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionRange;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionTautology;
import org.semanticweb.elk.reasoner.saturation.inferences.SubContextInitializationNoPremises;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.PropertyRangeInherited;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.SubPropertyChainExpandedSubObjectPropertyOf;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.SubPropertyChainTautology;

/**
 * Creates all {@link Conclusion}s for conclusions of the visited
 * {@link Inference}s using the provided {@link Conclusion.Factory} and visits
 * them using the provided {@link Conclusion.Visitor}.
 * 
 * @author Yevgeny Kazakov
 */
public class InferenceConclusionVisitor<O> implements Inference.Visitor<O> {

	private final Conclusion.Factory conclusionFactory_;

	private final Conclusion.Visitor<O> conclusionVisitor_;

	public InferenceConclusionVisitor(Conclusion.Factory conclusionFactory,
			Conclusion.Visitor<O> conclusionVisitor) {
		this.conclusionFactory_ = conclusionFactory;
		this.conclusionVisitor_ = conclusionVisitor;
	}

	public InferenceConclusionVisitor(Conclusion.Visitor<O> conclusionVisitor) {
		this(new ConclusionBaseFactory(), conclusionVisitor);
	}

	@Override
	public O visit(BackwardLinkComposition inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(BackwardLinkOfObjectHasSelf inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(BackwardLinkOfObjectSomeValuesFrom inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(BackwardLinkReversed inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(BackwardLinkReversedExpanded inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(ContextInitializationNoPremises inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(ContradictionOfDisjointSubsumers inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(ContradictionOfObjectComplementOf inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(ContradictionOfOwlNothing inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(ContradictionPropagated inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(DisjointSubsumerFromSubsumer inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(ElkClassAssertionAxiomConversion inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(ElkDeclarationAxiomConversion inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(ElkDifferentIndividualsAxiomBinaryConversion inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(ElkDifferentIndividualsAxiomNaryConversion inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(ElkDisjointClassesAxiomBinaryConversion inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(ElkDisjointClassesAxiomNaryConversion inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(ElkDisjointUnionAxiomBinaryConversion inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(ElkDisjointUnionAxiomDefinitionConversion inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(ElkDisjointUnionAxiomNaryConversion inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(ElkDisjointUnionAxiomSubClassConversion inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(ElkEquivalentClassesAxiomDefinitionConversion inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(ElkEquivalentClassesAxiomSubClassConversion inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(ElkEquivalentObjectPropertiesAxiomConversion inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(ElkObjectPropertyAssertionAxiomConversion inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(ElkObjectPropertyDomainAxiomConversion inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(ElkObjectPropertyRangeAxiomConversion inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(ElkReflexiveObjectPropertyAxiomConversion inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(ElkSameIndividualAxiomConversion inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(ElkSubClassOfAxiomConversion inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(ElkSubObjectPropertyOfAxiomConversion inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(ElkTransitiveObjectPropertyAxiomConversion inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(ForwardLinkComposition inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(ForwardLinkOfObjectHasSelf inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(ForwardLinkOfObjectSomeValuesFrom inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(PropagationGenerated inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(PropertyRangeInherited inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(SubClassInclusionComposedDefinedClass inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(SubClassInclusionComposedEntity inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(SubClassInclusionComposedObjectIntersectionOf inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(SubClassInclusionComposedObjectSomeValuesFrom inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(SubClassInclusionComposedObjectUnionOf inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(SubClassInclusionDecomposedFirstConjunct inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(SubClassInclusionDecomposedSecondConjunct inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(SubClassInclusionExpandedDefinition inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(SubClassInclusionExpandedSubClassOf inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(SubClassInclusionObjectHasSelfPropertyRange inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(SubClassInclusionOwlThing inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(SubClassInclusionRange inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(SubClassInclusionTautology inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(SubContextInitializationNoPremises inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(SubPropertyChainExpandedSubObjectPropertyOf inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(SubPropertyChainTautology inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

}
