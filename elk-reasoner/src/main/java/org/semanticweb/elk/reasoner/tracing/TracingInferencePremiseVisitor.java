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

/**
 * Visits all {@link Conclusion} premises of the visited
 * {@link TracingInference}s using the provided {@link Conclusion.Visitor};
 * additionally, visits all {@link ElkAxiom} premises using the provided
 * {@link ElkAxiomVisitor}
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author Yevgeny Kazakov
 * 
 * @param <O>
 *            the type of the output
 */
public class TracingInferencePremiseVisitor<O>
		implements TracingInference.Visitor<O> {

	private final ElkAxiomVisitor<?> axiomVisitor_;

	private final Conclusion.Factory conclusionFactory_;

	private final Conclusion.Visitor<?> conclusionVisitor_;

	public TracingInferencePremiseVisitor(Conclusion.Factory conclusionFactory,
			Conclusion.Visitor<?> conclusionVisitor,
			ElkAxiomVisitor<?> axiomVisitor) {
		this.conclusionFactory_ = conclusionFactory;
		this.conclusionVisitor_ = conclusionVisitor;
		this.axiomVisitor_ = axiomVisitor;
	}

	public TracingInferencePremiseVisitor(
			Conclusion.Visitor<?> conclusionVisitor,
			ElkAxiomVisitor<?> axiomVisitor) {
		this(new ConclusionBaseFactory(), conclusionVisitor, axiomVisitor);
	}

	@Override
	public O visit(BackwardLinkComposition inference) {
		conclusionVisitor_.visit(inference.getFirstPremise(conclusionFactory_));
		conclusionVisitor_
				.visit(inference.getSecondPremise(conclusionFactory_));
		conclusionVisitor_.visit(inference.getThirdPremise(conclusionFactory_));
		conclusionVisitor_
				.visit(inference.getFourthPremise(conclusionFactory_));
		conclusionVisitor_.visit(inference.getFifthPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(BackwardLinkOfObjectHasSelf inference) {
		conclusionVisitor_.visit(inference.getPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(BackwardLinkOfObjectSomeValuesFrom inference) {
		conclusionVisitor_.visit(inference.getPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(BackwardLinkReversedExpanded inference) {
		conclusionVisitor_.visit(inference.getFirstPremise(conclusionFactory_));
		conclusionVisitor_
				.visit(inference.getSecondPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(ClassInconsistencyOfDisjointSubsumers inference) {
		conclusionVisitor_.visit(inference.getFirstPremise(conclusionFactory_));
		conclusionVisitor_
				.visit(inference.getSecondPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(ClassInconsistencyOfObjectComplementOf inference) {
		conclusionVisitor_.visit(inference.getFirstPremise(conclusionFactory_));
		conclusionVisitor_
				.visit(inference.getSecondPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(ClassInconsistencyOfOwlNothing inference) {
		conclusionVisitor_.visit(inference.getPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(ClassInconsistencyPropagated inference) {
		conclusionVisitor_.visit(inference.getFirstPremise(conclusionFactory_));
		conclusionVisitor_
				.visit(inference.getSecondPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(ContextInitializationNoPremises inference) {
		// no premises
		return null;
	}

	@Override
	public O visit(DisjointSubsumerFromSubsumer inference) {
		conclusionVisitor_.visit(inference.getFirstPremise(conclusionFactory_));
		conclusionVisitor_
				.visit(inference.getSecondPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(ElkClassAssertionAxiomConversion inference) {
		axiomVisitor_.visit(inference.getOriginalAxiom());
		return null;
	}

	@Override
	public O visit(ElkDeclarationAxiomConversion inference) {
		axiomVisitor_.visit(inference.getOriginalAxiom());
		return null;
	}

	@Override
	public O visit(ElkDifferentIndividualsAxiomBinaryConversion inference) {
		axiomVisitor_.visit(inference.getOriginalAxiom());
		return null;
	}

	@Override
	public O visit(ElkDifferentIndividualsAxiomNaryConversion inference) {
		axiomVisitor_.visit(inference.getOriginalAxiom());
		return null;
	}

	@Override
	public O visit(ElkDisjointClassesAxiomBinaryConversion inference) {
		axiomVisitor_.visit(inference.getOriginalAxiom());
		return null;
	}

	@Override
	public O visit(ElkDisjointClassesAxiomNaryConversion inference) {
		axiomVisitor_.visit(inference.getOriginalAxiom());
		return null;
	}

	@Override
	public O visit(ElkDisjointUnionAxiomBinaryConversion inference) {
		axiomVisitor_.visit(inference.getOriginalAxiom());
		return null;
	}

	@Override
	public O visit(ElkDisjointUnionAxiomEquivalenceConversion inference) {
		axiomVisitor_.visit(inference.getOriginalAxiom());
		return null;
	}

	@Override
	public O visit(ElkDisjointUnionAxiomNaryConversion inference) {
		axiomVisitor_.visit(inference.getOriginalAxiom());
		return null;
	}

	@Override
	public O visit(ElkDisjointUnionAxiomOwlNothingConversion inference) {
		axiomVisitor_.visit(inference.getOriginalAxiom());
		return null;
	}

	@Override
	public O visit(ElkDisjointUnionAxiomSubClassConversion inference) {
		axiomVisitor_.visit(inference.getOriginalAxiom());
		return null;
	}

	@Override
	public O visit(ElkEquivalentClassesAxiomEquivalenceConversion inference) {
		axiomVisitor_.visit(inference.getOriginalAxiom());
		return null;
	}

	@Override
	public O visit(ElkEquivalentClassesAxiomSubClassConversion inference) {
		axiomVisitor_.visit(inference.getOriginalAxiom());
		return null;
	}

	@Override
	public O visit(ElkEquivalentObjectPropertiesAxiomConversion inference) {
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
	public O visit(ElkObjectPropertyRangeAxiomConversion inference) {
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
	public O visit(ForwardLinkComposition inference) {
		conclusionVisitor_.visit(inference.getFirstPremise(conclusionFactory_));
		conclusionVisitor_
				.visit(inference.getSecondPremise(conclusionFactory_));
		conclusionVisitor_.visit(inference.getThirdPremise(conclusionFactory_));
		conclusionVisitor_
				.visit(inference.getFourthPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(ForwardLinkOfObjectHasSelf inference) {
		conclusionVisitor_.visit(inference.getPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(ForwardLinkOfObjectSomeValuesFrom inference) {
		conclusionVisitor_.visit(inference.getPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(PropagationGenerated inference) {
		conclusionVisitor_.visit(inference.getFirstPremise(conclusionFactory_));
		conclusionVisitor_
				.visit(inference.getSecondPremise(conclusionFactory_));
		conclusionVisitor_.visit(inference.getThirdPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(PropertyRangeInherited inference) {
		conclusionVisitor_.visit(inference.getFirstPremise(conclusionFactory_));
		conclusionVisitor_
				.visit(inference.getSecondPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(SubClassInclusionComposedDefinedClass inference) {
		conclusionVisitor_.visit(inference.getFirstPremise(conclusionFactory_));
		conclusionVisitor_
				.visit(inference.getSecondPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(SubClassInclusionComposedEntity inference) {
		conclusionVisitor_.visit(inference.getPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(SubClassInclusionComposedObjectIntersectionOf inference) {
		conclusionVisitor_.visit(inference.getFirstPremise(conclusionFactory_));
		conclusionVisitor_
				.visit(inference.getSecondPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(SubClassInclusionComposedObjectSomeValuesFrom inference) {
		conclusionVisitor_.visit(inference.getFirstPremise(conclusionFactory_));
		conclusionVisitor_
				.visit(inference.getSecondPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(SubClassInclusionComposedObjectUnionOf inference) {
		conclusionVisitor_.visit(inference.getPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(SubClassInclusionDecomposedFirstConjunct inference) {
		conclusionVisitor_.visit(inference.getPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(SubClassInclusionDecomposedSecondConjunct inference) {
		conclusionVisitor_.visit(inference.getPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(SubClassInclusionExpandedDefinition inference) {
		conclusionVisitor_.visit(inference.getFirstPremise(conclusionFactory_));
		conclusionVisitor_
				.visit(inference.getSecondPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(SubClassInclusionExpandedFirstEquivalentClass inference) {
		conclusionVisitor_.visit(inference.getFirstPremise(conclusionFactory_));
		conclusionVisitor_
				.visit(inference.getSecondPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(SubClassInclusionExpandedSecondEquivalentClass inference) {
		conclusionVisitor_.visit(inference.getFirstPremise(conclusionFactory_));
		conclusionVisitor_
				.visit(inference.getSecondPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(SubClassInclusionExpandedSubClassOf inference) {
		conclusionVisitor_.visit(inference.getFirstPremise(conclusionFactory_));
		conclusionVisitor_
				.visit(inference.getSecondPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(SubClassInclusionObjectHasSelfPropertyRange inference) {
		conclusionVisitor_.visit(inference.getFirstPremise(conclusionFactory_));
		conclusionVisitor_
				.visit(inference.getSecondPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(SubClassInclusionOwlThing inference) {
		conclusionVisitor_.visit(inference.getPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(SubClassInclusionRange inference) {
		conclusionVisitor_.visit(inference.getFirstPremise(conclusionFactory_));
		conclusionVisitor_
				.visit(inference.getSecondPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(SubClassInclusionTautology inference) {
		conclusionVisitor_.visit(inference.getPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(SubContextInitializationNoPremises inference) {
		// no premises
		return null;
	}

	@Override
	public O visit(SubPropertyChainExpandedSubObjectPropertyOf inference) {
		conclusionVisitor_.visit(inference.getFirstPremise(conclusionFactory_));
		conclusionVisitor_
				.visit(inference.getSecondPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(SubPropertyChainTautology inference) {
		// no premises
		return null;
	}

}
