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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;
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
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionTautology;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.SubPropertyChainExpandedSubObjectPropertyOf;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.SubPropertyChainTautology;

/**
 * Creates all {@link Conclusion}s for premises of the visited {@link Inference}
 * s using the provided {@link Conclusion.Factory} and visits all
 * {@link ElkAxiom} side conditions using the provided {@link ElkAxiomVisitor}
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author Yevgeny Kazakov
 */
public class InferencePremiseVisitor<O> implements Inference.Visitor<O> {

	private final Conclusion.Factory conclusionFactory_;

	private final ElkAxiomVisitor<?> axiomVisitor_;

	public InferencePremiseVisitor(Conclusion.Factory conclusionFactory,
			ElkAxiomVisitor<?> axiomVisitor) {
		this.conclusionFactory_ = conclusionFactory;
		this.axiomVisitor_ = axiomVisitor;
	}

	@Override
	public O visit(SubClassInclusionTautology inference) {
		return null;
	}

	@Override
	public O visit(SubClassInclusionExpandedSubClassOf inference) {
		inference.getPremise(conclusionFactory_);
		inference.getSideCondition(conclusionFactory_);
		return null;
	}

	@Override
	public O visit(SubClassInclusionComposedObjectIntersectionOf inference) {
		inference.getFirstPremise(conclusionFactory_);
		inference.getSecondPremise(conclusionFactory_);
		return null;
	}

	@Override
	public O visit(SubClassInclusionDecomposedFirstConjunct inference) {
		inference.getPremise(conclusionFactory_);
		return null;
	}

	@Override
	public O visit(SubClassInclusionDecomposedSecondConjunct inference) {
		inference.getPremise(conclusionFactory_);
		return null;
	}

	@Override
	public O visit(SubClassInclusionComposedObjectSomeValuesFrom inference) {
		inference.getFirstPremise(conclusionFactory_);
		inference.getSecondPremise(conclusionFactory_);
		return null;
	}

	@Override
	public O visit(BackwardLinkComposition inference) {
		inference.getFirstPremise(conclusionFactory_);
		inference.getSecondPremise(conclusionFactory_);
		inference.getThirdPremise(conclusionFactory_);
		inference.getFourthPremise(conclusionFactory_);
		inference.getSideCondition(conclusionFactory_);
		return null;
	}

	@Override
	public O visit(ForwardLinkComposition inference) {
		inference.getFirstPremise(conclusionFactory_);
		inference.getSecondPremise(conclusionFactory_);
		inference.getThirdPremise(conclusionFactory_);
		inference.getFourthPremise(conclusionFactory_);
		return null;
	}

	@Override
	public O visit(BackwardLinkReversed inference) {
		inference.getPremise(conclusionFactory_);
		return null;
	}

	@Override
	public O visit(BackwardLinkReversedExpanded inference) {
		inference.getFirstPremise(conclusionFactory_);
		inference.getSecondPremise(conclusionFactory_);
		inference.getSideCondition(conclusionFactory_);
		return null;
	}

	@Override
	public O visit(BackwardLinkOfObjectSomeValuesFrom inference) {
		inference.getPremise(conclusionFactory_);
		return null;
	}

	@Override
	public O visit(ForwardLinkOfObjectSomeValuesFrom inference) {
		inference.getPremise(conclusionFactory_);
		return null;
	}

	@Override
	public O visit(BackwardLinkOfObjectHasSelf inference) {
		inference.getPremise(conclusionFactory_);
		return null;
	}

	@Override
	public O visit(ForwardLinkOfObjectHasSelf inference) {
		inference.getPremise(conclusionFactory_);
		return null;
	}

	@Override
	public O visit(PropagationGenerated inference) {
		inference.getFirstPremise(conclusionFactory_);
		inference.getSecondPremise(conclusionFactory_);
		return null;
	}

	@Override
	public O visit(ContradictionOfDisjointSubsumers inference) {
		inference.getFirstPremise(conclusionFactory_);
		inference.getSecondPremise(conclusionFactory_);
		inference.getSideCondition(conclusionFactory_);
		return null;
	}

	@Override
	public O visit(ContradictionOfObjectComplementOf inference) {
		inference.getFirstPremise(conclusionFactory_);
		inference.getSecondPremise(conclusionFactory_);
		return null;
	}

	@Override
	public O visit(ContradictionOfOwlNothing inference) {
		inference.getPremise(conclusionFactory_);
		return null;
	}

	@Override
	public O visit(ContradictionPropagated inference) {
		inference.getFirstPremise(conclusionFactory_);
		inference.getSecondPremise(conclusionFactory_);
		return null;
	}

	@Override
	public O visit(DisjointSubsumerFromSubsumer inference) {
		inference.getPremise(conclusionFactory_);
		return null;
	}

	@Override
	public O visit(SubClassInclusionComposedObjectUnionOf inference) {
		inference.getPremise(conclusionFactory_);
		return null;
	}

	@Override
	public O visit(SubClassInclusionComposedEntity inference) {
		inference.getPremise(conclusionFactory_);
		return null;
	}

	@Override
	public O visit(SubClassInclusionComposedDefinedClass inference) {
		inference.getPremise(conclusionFactory_);
		inference.getSideCondition(conclusionFactory_);
		return null;
	}

	@Override
	public O visit(SubClassInclusionExpandedDefinition inference) {
		inference.getPremise(conclusionFactory_);
		inference.getSideCondition(conclusionFactory_);
		return null;
	}

	@Override
	public O visit(SubPropertyChainTautology inference) {
		// no premises
		return null;
	}

	@Override
	public O visit(SubPropertyChainExpandedSubObjectPropertyOf inference) {
		inference.getPremise(conclusionFactory_);
		inference.getSideCondition(conclusionFactory_);
		return null;
	}

	@Override
	public O visit(SubClassInclusionObjectHasSelfPropertyRange inference) {
		inference.getPremise(conclusionFactory_);
		// TODO: process the property range premise
		return null;
	}

	@Override
	public O visit(ElkDifferentIndividualsAxiomNaryConversion inference) {
		axiomVisitor_.visit(inference.getOriginalAxiom());
		return null;
	}

	@Override
	public O visit(ElkDisjointClassesAxiomNaryConversion inference) {
		axiomVisitor_.visit(inference.getOriginalAxiom());
		return null;
	}

	@Override
	public O visit(ElkDisjointUnionAxiomNaryConversion inference) {
		axiomVisitor_.visit(inference.getOriginalAxiom());
		return null;
	}

	@Override
	public O visit(ElkClassAssertionAxiomConversion inference) {
		axiomVisitor_.visit(inference.getOriginalAxiom());
		return null;
	}

	@Override
	public O visit(ElkDifferentIndividualsAxiomBinaryConversion inference) {
		axiomVisitor_.visit(inference.getOriginalAxiom());
		return null;
	}

	@Override
	public O visit(ElkDisjointClassesAxiomBinaryConversion inference) {
		axiomVisitor_.visit(inference.getOriginalAxiom());
		return null;
	}

	@Override
	public O visit(ElkDisjointUnionAxiomSubClassConversion inference) {
		axiomVisitor_.visit(inference.getOriginalAxiom());
		return null;
	}

	@Override
	public O visit(ElkDisjointUnionAxiomBinaryConversion inference) {
		axiomVisitor_.visit(inference.getOriginalAxiom());
		return null;
	}

	@Override
	public O visit(ElkEquivalentClassesAxiomSubClassConversion inference) {
		axiomVisitor_.visit(inference.getOriginalAxiom());
		return null;
	}

	@Override
	public O visit(ElkObjectPropertyAssertionAxiomConversion inference) {
		axiomVisitor_.visit(inference.getOriginalAxiom());
		return null;
	}

	@Override
	public O visit(ElkObjectPropertyDomainAxiomConversion inference) {
		axiomVisitor_.visit(inference.getOriginalAxiom());
		return null;
	}

	@Override
	public O visit(ElkReflexiveObjectPropertyAxiomConversion inference) {
		axiomVisitor_.visit(inference.getOriginalAxiom());
		return null;
	}

	@Override
	public O visit(ElkSameIndividualAxiomConversion inference) {
		axiomVisitor_.visit(inference.getOriginalAxiom());
		return null;
	}

	@Override
	public O visit(ElkSubClassOfAxiomConversion inference) {
		axiomVisitor_.visit(inference.getOriginalAxiom());
		return null;
	}

	@Override
	public O visit(ElkDisjointUnionAxiomDefinitionConversion inference) {
		axiomVisitor_.visit(inference.getOriginalAxiom());
		return null;
	}

	@Override
	public O visit(ElkEquivalentClassesAxiomDefinitionConversion inference) {
		axiomVisitor_.visit(inference.getOriginalAxiom());
		return null;
	}

	@Override
	public O visit(ElkEquivalentObjectPropertiesAxiomConversion inference) {
		axiomVisitor_.visit(inference.getOriginalAxiom());
		return null;
	}

	@Override
	public O visit(ElkSubObjectPropertyOfAxiomConversion inference) {
		axiomVisitor_.visit(inference.getOriginalAxiom());
		return null;
	}

	@Override
	public O visit(ElkTransitiveObjectPropertyAxiomConversion inference) {
		axiomVisitor_.visit(inference.getOriginalAxiom());
		return null;
	}

	@Override
	public O visit(ElkObjectPropertyRangeAxiomConversion inference) {
		axiomVisitor_.visit(inference.getOriginalAxiom());
		return null;
	}

	@Override
	public O visit(ElkDeclarationAxiomConversion inference) {
		axiomVisitor_.visit(inference.getOriginalAxiom());
		return null;
	}

}
