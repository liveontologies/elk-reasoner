package org.semanticweb.elk.matching;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import org.semanticweb.elk.matching.conclusions.ConclusionMatchExpressionFactory;
import org.semanticweb.elk.matching.conclusions.ConclusionMatchHierarchy;
import org.semanticweb.elk.matching.conclusions.DisjointSubsumerMatch1;
import org.semanticweb.elk.matching.conclusions.DisjointSubsumerMatch2;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch1;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch2;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch3;
import org.semanticweb.elk.matching.conclusions.IndexedDisjointClassesAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedDisjointClassesAxiomMatch2;
import org.semanticweb.elk.matching.conclusions.IndexedEquivalentClassesAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedEquivalentClassesAxiomMatch2;
import org.semanticweb.elk.matching.conclusions.IndexedObjectPropertyRangeAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedObjectPropertyRangeAxiomMatch2;
import org.semanticweb.elk.matching.conclusions.IndexedSubClassOfAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedSubClassOfAxiomMatch2;
import org.semanticweb.elk.matching.conclusions.IndexedSubObjectPropertyOfAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedSubObjectPropertyOfAxiomMatch2;
import org.semanticweb.elk.matching.conclusions.PropertyRangeMatch1;
import org.semanticweb.elk.matching.conclusions.PropertyRangeMatch2;
import org.semanticweb.elk.matching.conclusions.SubClassInclusionDecomposedMatch1;
import org.semanticweb.elk.matching.conclusions.SubClassInclusionDecomposedMatch2;
import org.semanticweb.elk.matching.conclusions.SubPropertyChainMatch1;
import org.semanticweb.elk.matching.conclusions.SubPropertyChainMatch2;
import org.semanticweb.elk.matching.inferences.BackwardLinkCompositionMatch1;
import org.semanticweb.elk.matching.inferences.BackwardLinkCompositionMatch2;
import org.semanticweb.elk.matching.inferences.BackwardLinkCompositionMatch3;
import org.semanticweb.elk.matching.inferences.BackwardLinkCompositionMatch4;
import org.semanticweb.elk.matching.inferences.BackwardLinkCompositionMatch5;
import org.semanticweb.elk.matching.inferences.BackwardLinkOfObjectHasSelfMatch1;
import org.semanticweb.elk.matching.inferences.BackwardLinkOfObjectHasSelfMatch2;
import org.semanticweb.elk.matching.inferences.BackwardLinkOfObjectSomeValuesFromMatch1;
import org.semanticweb.elk.matching.inferences.BackwardLinkOfObjectSomeValuesFromMatch2;
import org.semanticweb.elk.matching.inferences.BackwardLinkReversedExpandedMatch1;
import org.semanticweb.elk.matching.inferences.BackwardLinkReversedExpandedMatch2;
import org.semanticweb.elk.matching.inferences.BackwardLinkReversedExpandedMatch3;
import org.semanticweb.elk.matching.inferences.BackwardLinkReversedMatch1;
import org.semanticweb.elk.matching.inferences.BackwardLinkReversedMatch2;
import org.semanticweb.elk.matching.inferences.BackwardLinkReversedMatch3;
import org.semanticweb.elk.matching.inferences.ClassInconsistencyOfDisjointSubsumersMatch1;
import org.semanticweb.elk.matching.inferences.ClassInconsistencyOfDisjointSubsumersMatch2;
import org.semanticweb.elk.matching.inferences.ClassInconsistencyOfObjectComplementOfMatch1;
import org.semanticweb.elk.matching.inferences.ClassInconsistencyOfObjectComplementOfMatch2;
import org.semanticweb.elk.matching.inferences.ClassInconsistencyOfOwlNothingMatch1;
import org.semanticweb.elk.matching.inferences.ClassInconsistencyPropagatedMatch1;
import org.semanticweb.elk.matching.inferences.ClassInconsistencyPropagatedMatch2;
import org.semanticweb.elk.matching.inferences.DisjointSubsumerFromSubsumerMatch1;
import org.semanticweb.elk.matching.inferences.DisjointSubsumerFromSubsumerMatch2;
import org.semanticweb.elk.matching.inferences.ElkClassAssertionAxiomConversionMatch1;
import org.semanticweb.elk.matching.inferences.ElkDifferentIndividualsAxiomBinaryConversionMatch1;
import org.semanticweb.elk.matching.inferences.ElkDifferentIndividualsAxiomNaryConversionMatch1;
import org.semanticweb.elk.matching.inferences.ElkDisjointClassesAxiomBinaryConversionMatch1;
import org.semanticweb.elk.matching.inferences.ElkDisjointClassesAxiomNaryConversionMatch1;
import org.semanticweb.elk.matching.inferences.ElkDisjointUnionAxiomBinaryConversionMatch1;
import org.semanticweb.elk.matching.inferences.ElkDisjointUnionAxiomEquivalenceConversionMatch1;
import org.semanticweb.elk.matching.inferences.ElkDisjointUnionAxiomNaryConversionMatch1;
import org.semanticweb.elk.matching.inferences.ElkDisjointUnionAxiomOwlNothingConversionMatch1;
import org.semanticweb.elk.matching.inferences.ElkDisjointUnionAxiomSubClassConversionMatch1;
import org.semanticweb.elk.matching.inferences.ElkEquivalentClassesAxiomEquivalenceConversionMatch1;
import org.semanticweb.elk.matching.inferences.ElkEquivalentClassesAxiomSubClassConversionMatch1;
import org.semanticweb.elk.matching.inferences.ElkEquivalentObjectPropertiesAxiomConversionMatch1;
import org.semanticweb.elk.matching.inferences.ElkObjectPropertyAssertionAxiomConversionMatch1;
import org.semanticweb.elk.matching.inferences.ElkObjectPropertyDomainAxiomConversionMatch1;
import org.semanticweb.elk.matching.inferences.ElkObjectPropertyRangeAxiomConversionMatch1;
import org.semanticweb.elk.matching.inferences.ElkReflexiveObjectPropertyAxiomConversionMatch1;
import org.semanticweb.elk.matching.inferences.ElkSameIndividualAxiomConversionMatch1;
import org.semanticweb.elk.matching.inferences.ElkSubClassOfAxiomConversionMatch1;
import org.semanticweb.elk.matching.inferences.ElkSubObjectPropertyOfAxiomConversionMatch1;
import org.semanticweb.elk.matching.inferences.ElkTransitiveObjectPropertyAxiomConversionMatch1;
import org.semanticweb.elk.matching.inferences.ForwardLinkCompositionMatch1;
import org.semanticweb.elk.matching.inferences.ForwardLinkCompositionMatch2;
import org.semanticweb.elk.matching.inferences.ForwardLinkCompositionMatch3;
import org.semanticweb.elk.matching.inferences.ForwardLinkCompositionMatch4;
import org.semanticweb.elk.matching.inferences.ForwardLinkCompositionMatch5;
import org.semanticweb.elk.matching.inferences.ForwardLinkOfObjectHasSelfMatch1;
import org.semanticweb.elk.matching.inferences.ForwardLinkOfObjectHasSelfMatch2;
import org.semanticweb.elk.matching.inferences.ForwardLinkOfObjectSomeValuesFromMatch1;
import org.semanticweb.elk.matching.inferences.ForwardLinkOfObjectSomeValuesFromMatch2;
import org.semanticweb.elk.matching.inferences.InferenceMatch;
import org.semanticweb.elk.matching.inferences.PropagationGeneratedMatch1;
import org.semanticweb.elk.matching.inferences.PropertyRangeInheritedMatch1;
import org.semanticweb.elk.matching.inferences.PropertyRangeInheritedMatch2;
import org.semanticweb.elk.matching.inferences.PropertyRangeInheritedMatch3;
import org.semanticweb.elk.matching.inferences.SubClassInclusionComposedDefinedClassMatch1;
import org.semanticweb.elk.matching.inferences.SubClassInclusionComposedDefinedClassMatch2;
import org.semanticweb.elk.matching.inferences.SubClassInclusionComposedEntityMatch1;
import org.semanticweb.elk.matching.inferences.SubClassInclusionComposedObjectIntersectionOfMatch1;
import org.semanticweb.elk.matching.inferences.SubClassInclusionComposedObjectSomeValuesFromMatch1;
import org.semanticweb.elk.matching.inferences.SubClassInclusionComposedObjectSomeValuesFromMatch2;
import org.semanticweb.elk.matching.inferences.SubClassInclusionComposedObjectUnionOfMatch1;
import org.semanticweb.elk.matching.inferences.SubClassInclusionDecomposedFirstConjunctMatch1;
import org.semanticweb.elk.matching.inferences.SubClassInclusionDecomposedFirstConjunctMatch2;
import org.semanticweb.elk.matching.inferences.SubClassInclusionDecomposedSecondConjunctMatch1;
import org.semanticweb.elk.matching.inferences.SubClassInclusionDecomposedSecondConjunctMatch2;
import org.semanticweb.elk.matching.inferences.SubClassInclusionExpandedDefinitionMatch1;
import org.semanticweb.elk.matching.inferences.SubClassInclusionExpandedDefinitionMatch2;
import org.semanticweb.elk.matching.inferences.SubClassInclusionExpandedFirstEquivalentClassMatch1;
import org.semanticweb.elk.matching.inferences.SubClassInclusionExpandedFirstEquivalentClassMatch2;
import org.semanticweb.elk.matching.inferences.SubClassInclusionExpandedSecondEquivalentClassMatch1;
import org.semanticweb.elk.matching.inferences.SubClassInclusionExpandedSecondEquivalentClassMatch2;
import org.semanticweb.elk.matching.inferences.SubClassInclusionExpandedSubClassOfMatch1;
import org.semanticweb.elk.matching.inferences.SubClassInclusionExpandedSubClassOfMatch2;
import org.semanticweb.elk.matching.inferences.SubClassInclusionObjectHasSelfPropertyRangeMatch1;
import org.semanticweb.elk.matching.inferences.SubClassInclusionObjectHasSelfPropertyRangeMatch2;
import org.semanticweb.elk.matching.inferences.SubClassInclusionObjectHasSelfPropertyRangeMatch3;
import org.semanticweb.elk.matching.inferences.SubClassInclusionOwlThingMatch1;
import org.semanticweb.elk.matching.inferences.SubClassInclusionRangeMatch1;
import org.semanticweb.elk.matching.inferences.SubClassInclusionRangeMatch2;
import org.semanticweb.elk.matching.inferences.SubClassInclusionTautologyMatch1;
import org.semanticweb.elk.matching.inferences.SubPropertyChainExpandedSubObjectPropertyOfMatch1;
import org.semanticweb.elk.matching.inferences.SubPropertyChainExpandedSubObjectPropertyOfMatch2;
import org.semanticweb.elk.matching.inferences.SubPropertyChainTautologyMatch1;
import org.semanticweb.elk.matching.root.IndexedContextRootMatch;
import org.semanticweb.elk.matching.root.IndexedContextRootMatchChain;
import org.semanticweb.elk.matching.subsumers.IndexedObjectSomeValuesFromMatch;
import org.semanticweb.elk.matching.subsumers.IndexedObjectUnionOfMatch;
import org.semanticweb.elk.matching.subsumers.SubsumerObjectHasValueMatch;
import org.semanticweb.elk.matching.subsumers.SubsumerObjectOneOfMatch;
import org.semanticweb.elk.matching.subsumers.SubsumerObjectSomeValuesFromMatch;
import org.semanticweb.elk.matching.subsumers.SubsumerObjectUnionOfMatch;
import org.semanticweb.elk.owl.inferences.ElkInference;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkDifferentIndividualsAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointUnionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectComplementOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectInverseOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectOneOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyChain;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectUnionOf;
import org.semanticweb.elk.owl.interfaces.ElkReflexiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSameIndividualAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkTransitiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.visitors.ElkSubObjectPropertyExpressionVisitor;
import org.semanticweb.elk.reasoner.indexing.model.ElkClassAssertionAxiomConversion;
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
import org.semanticweb.elk.reasoner.indexing.model.ElkSameIndividualAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkSubClassOfAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkSubObjectPropertyOfAxiomConversion;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInconsistencyOfDisjointSubsumers;

class InferenceMatchVisitor implements InferenceMatch.Visitor<Void> {

	private final ConclusionMatchExpressionFactory conclusionFactory_;

	private final ElkInference.Factory elkInferenceFactory_;

	private final ConclusionMatchHierarchy hierarchy_;

	private final InferenceMatch.Factory inferenceFactory_;

	private final InferenceMatchMapWriter inferences_;

	InferenceMatchVisitor(InferenceMatchMapWriter inferences,
			ConclusionMatchHierarchy hierarchy,
			ConclusionMatchExpressionFactory conclusionFactory,
			InferenceMatch.Factory inferenceFactory,
			ElkInference.Factory elkInferenceFactory) {
		this.inferences_ = inferences;
		this.hierarchy_ = hierarchy;
		this.conclusionFactory_ = conclusionFactory;
		this.inferenceFactory_ = inferenceFactory;
		this.elkInferenceFactory_ = elkInferenceFactory;
	}

	List<ElkClassExpression> addToList(List<ElkClassExpression> result,
			IndexedContextRootMatchChain intermediate,
			IndexedContextRootMatch last) {
		for (IndexedContextRootMatchChain todo = intermediate; todo != null; todo = todo
				.getTail()) {
			result.add(toElkExpression(todo.getHead()));
		}
		result.add(toElkExpression(last));
		return result;
	}

	ElkClassExpression toElkExpression(IndexedContextRootMatch rootMatch) {
		return rootMatch.toElkExpression(conclusionFactory_);
	}

	List<? extends ElkObjectPropertyExpression> toList(
			ElkSubObjectPropertyExpression chain) {
		return chain.accept(
				new ElkSubObjectPropertyExpressionVisitor<List<? extends ElkObjectPropertyExpression>>() {

					@Override
					public List<? extends ElkObjectPropertyExpression> visit(
							ElkObjectInverseOf expression) {
						return Collections.singletonList(expression);
					}

					@Override
					public List<? extends ElkObjectPropertyExpression> visit(
							ElkObjectProperty expression) {
						return Collections.singletonList(expression);
					}

					@Override
					public List<? extends ElkObjectPropertyExpression> visit(
							ElkObjectPropertyChain expression) {
						return expression.getObjectPropertyExpressions();
					}

				});
	}

	List<? extends ElkClassExpression> toList(IndexedContextRootMatch first,
			IndexedContextRootMatch second, IndexedContextRootMatch last) {
		return toList(first, second, null, last);
	}

	List<? extends ElkClassExpression> toList(IndexedContextRootMatch first,
			IndexedContextRootMatch second,
			IndexedContextRootMatchChain intermediate,
			IndexedContextRootMatch last) {
		List<ElkClassExpression> result = new ArrayList<ElkClassExpression>();
		result.add(toElkExpression(first));
		result.add(toElkExpression(second));
		return addToList(result, intermediate, last);
	}

	List<? extends ElkClassExpression> toList(IndexedContextRootMatch first,
			IndexedContextRootMatchChain intermediate,
			IndexedContextRootMatch last) {
		List<ElkClassExpression> result = new ArrayList<ElkClassExpression>();
		result.add(toElkExpression(first));
		return addToList(result, intermediate, last);
	}

	@Override
	public Void visit(BackwardLinkCompositionMatch1 inferenceMatch1) {
		IndexedSubObjectPropertyOfAxiomMatch1 premiseMatch1 = inferenceMatch1
				.getFifthPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch1);
		for (IndexedSubObjectPropertyOfAxiomMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new IndexedSubObjectPropertyOfAxiomMatch2InferenceVisitor(
					inferenceFactory_, child)).visit(inferenceMatch1);
		}
		return null;
	}

	@Override
	public Void visit(BackwardLinkCompositionMatch2 inferenceMatch2) {
		BackwardLinkMatch1 premiseMatch1 = inferenceMatch2
				.getFirstPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch2);
		for (BackwardLinkMatch2 child : hierarchy_.getChildren(premiseMatch1)) {
			(new BackwardLinkMatch2InferenceVisitor(inferenceFactory_, child))
					.visit(inferenceMatch2);
		}
		return null;
	}

	@Override
	public Void visit(BackwardLinkCompositionMatch3 inferenceMatch3) {
		SubPropertyChainMatch1 premiseMatch1 = inferenceMatch3
				.getFourthPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch3);
		for (SubPropertyChainMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new SubPropertyChainMatch2InferenceVisitor(inferenceFactory_,
					child)).visit(inferenceMatch3);
		}
		return null;
	}

	@Override
	public Void visit(BackwardLinkCompositionMatch4 inferenceMatch4) {
		ForwardLinkMatch2 premiseMatch2 = inferenceMatch4
				.getThirdPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch2, inferenceMatch4);
		for (ForwardLinkMatch3 child : hierarchy_.getChildren(premiseMatch2)) {
			(new ForwardLinkMatch3InferenceVisitor(inferenceFactory_, child))
					.visit(inferenceMatch4);
		}
		return null;
	}

	@Override
	public Void visit(BackwardLinkCompositionMatch5 inferenceMatch5) {
		inferenceMatch5.getSecondPremiseMatch(conclusionFactory_);
		inferenceMatch5.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		IndexedContextRootMatch destinationMatch = inferenceMatch5
				.getDestinationMatch();
		IndexedContextRootMatchChain intermediateRoots = inferenceMatch5
				.getIntermediateRoots();
		BackwardLinkCompositionMatch4 inferenceMatch4 = inferenceMatch5
				.getParent();
		int premiseForwardChainStartPos = inferenceMatch4
				.getPremiseForwardChainStartPos();
		ElkSubObjectPropertyExpression premiseFullForwardChainMatch = inferenceMatch4
				.getPremiseFullForwardChainMatch();
		BackwardLinkCompositionMatch3 inferenceMatch3 = inferenceMatch4
				.getParent();
		IndexedContextRootMatch originMatch = inferenceMatch3.getOriginMatch();
		BackwardLinkCompositionMatch2 inferenceMatch2 = inferenceMatch3
				.getParent();
		ElkObjectProperty premiseBackwardRelationMatch = inferenceMatch3
				.getPremiseBackwardRelationMatch();
		ElkSubObjectPropertyExpression compositionMatch = inferenceMatch2
				.getCompositionMatch();
		ElkObjectProperty conclusionRelationMatch = inferenceMatch2
				.getConclusionRelationMatch();
		BackwardLinkCompositionMatch1 inferenceMatch1 = inferenceMatch2
				.getParent();
		IndexedContextRootMatch conclusionSourceMatch = inferenceMatch1
				.getConclusionSourceMatch();
		// unfolding the first premise under the second premise
		List<? extends ElkObjectPropertyExpression> compositionList = toList(
				compositionMatch);
		elkInferenceFactory_.getElkClassInclusionExistentialPropertyUnfolding(
				toElkExpression(conclusionSourceMatch),
				premiseBackwardRelationMatch, toElkExpression(originMatch),
				compositionList.get(0));
		List<? extends ElkObjectPropertyExpression> forwardChainList = toList(
				premiseFullForwardChainMatch);
		if (premiseForwardChainStartPos == 0) {
			// composing and unfolding the third premise with the fourth premise
			elkInferenceFactory_
					.getElkClassInclusionExistentialPropertyUnfolding(
							toList(originMatch, intermediateRoots,
									destinationMatch),
							forwardChainList, compositionList.get(1));
			// composing the unfolded first premise with the result under the
			// fifth premise
			elkInferenceFactory_
					.getElkClassInclusionExistentialPropertyUnfolding(
							toList(conclusionSourceMatch, originMatch,
									destinationMatch),
							compositionList, conclusionRelationMatch);
		} else {
			// composing the unfolded first premise with the third premise under
			// the fifth premise
			elkInferenceFactory_
					.getElkClassInclusionExistentialPropertyUnfolding(
							toList(conclusionSourceMatch, originMatch,
									intermediateRoots, destinationMatch),
							compositionList, conclusionRelationMatch);
		}

		return null;

	}

	@Override
	public Void visit(BackwardLinkOfObjectHasSelfMatch1 inferenceMatch1) {
		SubClassInclusionDecomposedMatch1 premiseMatch1 = inferenceMatch1
				.getPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch1);
		for (SubClassInclusionDecomposedMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new SubClassInclusionDecomposedMatch2InferenceVisitor(
					inferenceFactory_, child)).visit(inferenceMatch1);
		}
		return null;
	}

	@Override
	public Void visit(BackwardLinkOfObjectHasSelfMatch2 inferenceMatch2) {
		inferenceMatch2.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		ElkObjectProperty relationMatch = inferenceMatch2.getRelationMatch();
		BackwardLinkOfObjectHasSelfMatch1 inferenceMatch1 = inferenceMatch2
				.getParent();
		IndexedContextRootMatch originMatch = inferenceMatch1.getOriginMatch();

		elkInferenceFactory_.getElkClassInclusionExistentialOfObjectHasSelf(
				toElkExpression(originMatch), relationMatch);
		return null;
	}

	@Override
	public Void visit(
			BackwardLinkOfObjectSomeValuesFromMatch1 inferenceMatch1) {
		SubClassInclusionDecomposedMatch1 premiseMatch1 = inferenceMatch1
				.getPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch1);
		for (SubClassInclusionDecomposedMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new SubClassInclusionDecomposedMatch2InferenceVisitor(
					inferenceFactory_, child)).visit(inferenceMatch1);
		}
		return null;
	}

	@Override
	public Void visit(
			BackwardLinkOfObjectSomeValuesFromMatch2 inferenceMatch2) {
		inferenceMatch2.getConclusionMatch(conclusionFactory_);

		// no ELK inferences
		return null;
	}

	@Override
	public Void visit(BackwardLinkReversedExpandedMatch1 inferenceMatch1) {
		IndexedSubObjectPropertyOfAxiomMatch1 premiseMatch1 = inferenceMatch1
				.getSecondPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch1);
		for (IndexedSubObjectPropertyOfAxiomMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new IndexedSubObjectPropertyOfAxiomMatch2InferenceVisitor(
					inferenceFactory_, child)).visit(inferenceMatch1);
		}
		return null;
	}

	@Override
	public Void visit(BackwardLinkReversedExpandedMatch2 inferenceMatch2) {
		ForwardLinkMatch2 premiseMatch1 = inferenceMatch2
				.getFirstPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch2);
		for (ForwardLinkMatch3 child : hierarchy_.getChildren(premiseMatch1)) {
			(new ForwardLinkMatch3InferenceVisitor(inferenceFactory_, child))
					.visit(inferenceMatch2);
		}
		return null;
	}

	@Override
	public Void visit(BackwardLinkReversedExpandedMatch3 inferenceMatch3) {
		inferenceMatch3.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		IndexedContextRootMatchChain intermediateRoots = inferenceMatch3
				.getIntermediateRoots();
		IndexedContextRootMatch destinationMatch = inferenceMatch3
				.getDestinationMatch();
		BackwardLinkReversedExpandedMatch2 inferenceMatch2 = inferenceMatch3
				.getParent();
		ElkSubObjectPropertyExpression subChainMatch = inferenceMatch2
				.getSubChainMatch();
		ElkObjectProperty relationMatch = inferenceMatch2.getRelationMatch();
		BackwardLinkReversedExpandedMatch1 inferenceMatch1 = inferenceMatch2
				.getParent();
		IndexedContextRootMatch originMatch = inferenceMatch1.getOriginMatch();
		elkInferenceFactory_.getElkClassInclusionExistentialPropertyUnfolding(
				toList(originMatch, intermediateRoots, destinationMatch),
				toList(subChainMatch), relationMatch);
		return null;
	}

	@Override
	public Void visit(BackwardLinkReversedMatch1 inferenceMatch1) {
		ForwardLinkMatch1 premiseMatch1 = inferenceMatch1
				.getPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch1);
		for (ForwardLinkMatch2 child : hierarchy_.getChildren(premiseMatch1)) {
			(new ForwardLinkMatch2InferenceVisitor(inferenceFactory_, child))
					.visit(inferenceMatch1);
		}
		return null;
	}

	@Override
	public Void visit(BackwardLinkReversedMatch2 inferenceMatch2) {
		ForwardLinkMatch2 premiseMatch2 = inferenceMatch2
				.getPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch2, inferenceMatch2);
		for (ForwardLinkMatch3 child : hierarchy_.getChildren(premiseMatch2)) {
			(new ForwardLinkMatch3InferenceVisitor(inferenceFactory_, child))
					.visit(inferenceMatch2);
		}
		return null;
	}

	@Override
	public Void visit(BackwardLinkReversedMatch3 inferenceMatch2) {
		inferenceMatch2.getConclusionMatch(conclusionFactory_);
		// no ELK inferences
		return null;
	}

	@Override
	public Void visit(
			ClassInconsistencyOfDisjointSubsumersMatch1 inferenceMatch1) {
		DisjointSubsumerMatch1 premiseMatch1 = inferenceMatch1
				.getFirstPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch1);
		for (DisjointSubsumerMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new DisjointSubsumerMatch2InferenceVisitor(inferenceFactory_,
					child)).visit(inferenceMatch1);
		}
		return null;
	}

	@Override
	public Void visit(
			ClassInconsistencyOfDisjointSubsumersMatch2 inferenceMatch2) {
		inferenceMatch2.getSecondPremiseMatch(conclusionFactory_);

		// creating ELK inferences
		List<? extends ElkClassExpression> disjoint = inferenceMatch2
				.getDisjointExpressionsMatch();
		ClassInconsistencyOfDisjointSubsumersMatch1 inferenceMatch1 = inferenceMatch2
				.getParent();
		IndexedContextRootMatch originMatch = inferenceMatch1.getOriginMatch();
		ClassInconsistencyOfDisjointSubsumers parent = inferenceMatch1
				.getParent();
		int firstPos = parent.getFirstDisjointPosition();
		int secondPos = parent.getSecondDisjointPosition();
		ElkClassExpression firstDisjoint = disjoint.get(firstPos);
		ElkClassExpression secondDisjoint = disjoint.get(secondPos);
		ElkClassExpression subExpression = toElkExpression(originMatch);
		elkInferenceFactory_
				.getElkClassInclusionObjectIntersectionOfComposition(
						subExpression, firstDisjoint, secondDisjoint);
		elkInferenceFactory_.getElkClassInclusionOfDisjointClasses(disjoint,
				firstPos, secondPos);
		elkInferenceFactory_.getElkClassInclusionHierarchy(
				subExpression, conclusionFactory_
						.getObjectIntersectionOf(firstDisjoint, secondDisjoint),
				conclusionFactory_.getOwlNothing());
		return null;
	}

	@Override
	public Void visit(
			ClassInconsistencyOfObjectComplementOfMatch1 inferenceMatch1) {
		SubClassInclusionDecomposedMatch1 premiseMatch1 = inferenceMatch1
				.getSecondPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch1);
		for (SubClassInclusionDecomposedMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new SubClassInclusionDecomposedMatch2InferenceVisitor(
					inferenceFactory_, child)).visit(inferenceMatch1);
		}
		return null;
	}

	@Override
	public Void visit(
			ClassInconsistencyOfObjectComplementOfMatch2 inferenceMatch2) {
		inferenceMatch2.getFirstPremiseMatch(conclusionFactory_);

		// creating ELK inferences
		ElkObjectComplementOf negationMatch = inferenceMatch2
				.getNegationMatch();
		ClassInconsistencyOfObjectComplementOfMatch1 inferenceMatch1 = inferenceMatch2
				.getParent();
		IndexedContextRootMatch originMatch = inferenceMatch1.getOriginMatch();
		ElkClassExpression subExpression = toElkExpression(originMatch);
		elkInferenceFactory_
				.getElkClassInclusionObjectIntersectionOfComposition(
						subExpression, negationMatch.getClassExpression(),
						negationMatch);
		elkInferenceFactory_.getElkClassInclusionNegationClash(
				negationMatch.getClassExpression());
		elkInferenceFactory_.getElkClassInclusionHierarchy(subExpression,
				conclusionFactory_.getObjectIntersectionOf(
						negationMatch.getClassExpression(), negationMatch),
				conclusionFactory_.getOwlNothing());
		return null;
	}

	@Override
	public Void visit(ClassInconsistencyOfOwlNothingMatch1 inferenceMatch1) {
		inferenceMatch1.getPremiseMatch(conclusionFactory_);

		// no ELK inferences
		return null;
	}

	@Override
	public Void visit(ClassInconsistencyPropagatedMatch1 inferenceMatch1) {
		BackwardLinkMatch1 premiseMatch1 = inferenceMatch1
				.getFirstPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch1);
		for (BackwardLinkMatch2 child : hierarchy_.getChildren(premiseMatch1)) {
			(new BackwardLinkMatch2InferenceVisitor(inferenceFactory_, child))
					.visit(inferenceMatch1);
		}
		return null;
	}

	@Override
	public Void visit(ClassInconsistencyPropagatedMatch2 inferenceMatch2) {
		inferenceMatch2.getSecondPremiseMatch(conclusionFactory_);

		// creating ELK inferences
		IndexedContextRootMatch originMatch = inferenceMatch2.getOriginMatch();
		ElkObjectProperty premiseRelationMatch = inferenceMatch2
				.getPremiseRelationMatch();
		ClassInconsistencyPropagatedMatch1 inferenceMatch1 = inferenceMatch2
				.getParent();
		IndexedContextRootMatch destinationMatch = inferenceMatch1
				.getDestinationMatch();
		elkInferenceFactory_.getElkClassInclusionExistentialFillerUnfolding(
				toElkExpression(destinationMatch), premiseRelationMatch,
				toElkExpression(originMatch),
				conclusionFactory_.getOwlNothing());
		elkInferenceFactory_.getElkClassInclusionExistentialOwlNothing(
				premiseRelationMatch);
		elkInferenceFactory_.getElkClassInclusionHierarchy(
				toElkExpression(destinationMatch),
				conclusionFactory_.getObjectSomeValuesFrom(premiseRelationMatch,
						conclusionFactory_.getOwlNothing()),
				conclusionFactory_.getOwlNothing());
		return null;
	}

	@Override
	public Void visit(DisjointSubsumerFromSubsumerMatch1 inferenceMatch1) {
		IndexedDisjointClassesAxiomMatch1 premiseMatch1 = inferenceMatch1
				.getSecondPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch1);
		for (IndexedDisjointClassesAxiomMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new IndexedDisjointClassesAxiomMatch2InferenceVisitor(
					inferenceFactory_, child)).visit(inferenceMatch1);
		}
		return null;
	}

	@Override
	public Void visit(DisjointSubsumerFromSubsumerMatch2 inferenceMatch2) {
		inferenceMatch2.getFirstPremiseMatch(conclusionFactory_);
		inferenceMatch2.getConclusionMatch(conclusionFactory_);

		// no ELK inferences
		return null;
	}

	@Override
	public Void visit(ElkClassAssertionAxiomConversionMatch1 inferenceMatch1) {
		inferenceMatch1.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		ElkClassAssertionAxiomConversion parent = inferenceMatch1.getParent();
		ElkClassAssertionAxiom originalAxiom = parent.getOriginalAxiom();
		elkInferenceFactory_.getElkClassInclusionOfClassAssertion(
				originalAxiom.getIndividual(),
				originalAxiom.getClassExpression());
		elkInferenceFactory_.getElkToldAxiom(originalAxiom);
		return null;
	}

	@Override
	public Void visit(
			ElkDifferentIndividualsAxiomBinaryConversionMatch1 inferenceMatch1) {
		inferenceMatch1.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		ElkDifferentIndividualsAxiomBinaryConversion parent = inferenceMatch1
				.getParent();
		ElkDifferentIndividualsAxiom originalAxiom = parent.getOriginalAxiom();
		List<? extends ElkIndividual> different = originalAxiom
				.getIndividuals();
		ArrayList<ElkObjectOneOf> disjoint = new ArrayList<ElkObjectOneOf>(
				different.size());
		for (ElkIndividual ind : different) {
			disjoint.add(conclusionFactory_
					.getObjectOneOf(Collections.singletonList(ind)));
		}
		elkInferenceFactory_
				.getElkDisjointClassesOfDifferentIndividuals(different);
		elkInferenceFactory_.getElkClassInclusionOfDisjointClasses(disjoint,
				parent.getFirstIndividualPosition(),
				parent.getSecondIndividualPosition());
		elkInferenceFactory_.getElkToldAxiom(originalAxiom);
		return null;
	}

	@Override
	public Void visit(
			ElkDifferentIndividualsAxiomNaryConversionMatch1 inferenceMatch1) {
		inferenceMatch1.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		ElkDifferentIndividualsAxiomNaryConversion parent = inferenceMatch1
				.getParent();
		ElkDifferentIndividualsAxiom originalAxiom = parent.getOriginalAxiom();
		List<? extends ElkIndividual> different = originalAxiom
				.getIndividuals();
		ArrayList<ElkObjectOneOf> disjoint = new ArrayList<ElkObjectOneOf>(
				different.size());
		for (ElkIndividual ind : different) {
			disjoint.add(conclusionFactory_
					.getObjectOneOf(Collections.singletonList(ind)));
		}
		elkInferenceFactory_
				.getElkDisjointClassesOfDifferentIndividuals(different);
		elkInferenceFactory_.getElkToldAxiom(originalAxiom);
		return null;
	}

	@Override
	public Void visit(
			ElkDisjointClassesAxiomBinaryConversionMatch1 inferenceMatch1) {
		inferenceMatch1.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		ElkDisjointClassesAxiomBinaryConversion parent = inferenceMatch1
				.getParent();
		ElkDisjointClassesAxiom originalAxiom = parent.getOriginalAxiom();
		elkInferenceFactory_.getElkClassInclusionOfDisjointClasses(
				originalAxiom.getClassExpressions(),
				parent.getFirstClassPosition(),
				parent.getSecondClassPosition());
		elkInferenceFactory_.getElkToldAxiom(originalAxiom);
		return null;
	}

	@Override
	public Void visit(
			ElkDisjointClassesAxiomNaryConversionMatch1 inferenceMatch1) {
		inferenceMatch1.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		ElkDisjointClassesAxiomNaryConversion parent = inferenceMatch1
				.getParent();
		ElkDisjointClassesAxiom originalAxiom = parent.getOriginalAxiom();
		elkInferenceFactory_.getElkToldAxiom(originalAxiom);
		return null;
	}

	@Override
	public Void visit(
			ElkDisjointUnionAxiomBinaryConversionMatch1 inferenceMatch1) {
		inferenceMatch1.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		ElkDisjointUnionAxiomBinaryConversion parent = inferenceMatch1
				.getParent();
		ElkDisjointUnionAxiom originalAxiom = parent.getOriginalAxiom();
		elkInferenceFactory_.getElkDisjointClassesOfDisjointUnion(
				originalAxiom.getDefinedClass(),
				originalAxiom.getClassExpressions());
		elkInferenceFactory_.getElkClassInclusionOfDisjointClasses(
				originalAxiom.getClassExpressions(),
				parent.getFirstDisjunctPosition(),
				parent.getSecondDisjunctPosition());
		elkInferenceFactory_.getElkToldAxiom(originalAxiom);
		return null;
	}

	@Override
	public Void visit(
			ElkDisjointUnionAxiomEquivalenceConversionMatch1 inferenceMatch1) {
		inferenceMatch1.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		ElkDisjointUnionAxiomEquivalenceConversion parent = inferenceMatch1
				.getParent();
		ElkDisjointUnionAxiom originalAxiom = parent.getOriginalAxiom();
		ElkClass defined = originalAxiom.getDefinedClass();
		List<? extends ElkClassExpression> disjoint = originalAxiom
				.getClassExpressions();
		ElkClassExpression member = disjoint.get(0);
		elkInferenceFactory_.getElkEquivalentClassesOfDisjointUnion(defined,
				disjoint);
		elkInferenceFactory_.getElkClassInclusionOfEquivaletClasses(defined,
				conclusionFactory_.getObjectUnionOf(disjoint), true);
		elkInferenceFactory_.getElkClassInclusionOfEquivaletClasses(defined,
				conclusionFactory_.getObjectUnionOf(disjoint), false);
		elkInferenceFactory_
				.getElkClassInclusionSingletonObjectUnionOfDecomposition(
						member);
		elkInferenceFactory_
				.getElkClassInclusionObjectUnionOfComposition(disjoint, 0);
		elkInferenceFactory_.getElkClassInclusionHierarchy(member,
				conclusionFactory_.getObjectUnionOf(disjoint), defined);
		elkInferenceFactory_.getElkClassInclusionHierarchy(defined,
				conclusionFactory_.getObjectUnionOf(disjoint), member);
		elkInferenceFactory_.getElkToldAxiom(originalAxiom);
		return null;
	}

	@Override
	public Void visit(
			ElkDisjointUnionAxiomNaryConversionMatch1 inferenceMatch1) {
		inferenceMatch1.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		ElkDisjointUnionAxiomNaryConversion parent = inferenceMatch1
				.getParent();
		ElkDisjointUnionAxiom originalAxiom = parent.getOriginalAxiom();
		elkInferenceFactory_.getElkDisjointClassesOfDisjointUnion(
				originalAxiom.getDefinedClass(),
				originalAxiom.getClassExpressions());
		elkInferenceFactory_.getElkToldAxiom(originalAxiom);
		return null;
	}

	@Override
	public Void visit(
			ElkDisjointUnionAxiomOwlNothingConversionMatch1 inferenceMatch1) {
		inferenceMatch1.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		ElkDisjointUnionAxiomOwlNothingConversion parent = inferenceMatch1
				.getParent();
		ElkDisjointUnionAxiom originalAxiom = parent.getOriginalAxiom();
		ElkClass defined = originalAxiom.getDefinedClass();
		List<? extends ElkClassExpression> disjoint = originalAxiom
				.getClassExpressions();
		elkInferenceFactory_.getElkEquivalentClassesOfDisjointUnion(defined,
				disjoint);
		elkInferenceFactory_.getElkClassInclusionOfEquivaletClasses(defined,
				conclusionFactory_.getObjectUnionOf(disjoint), true);
		elkInferenceFactory_.getElkClassInclusionHierarchy(defined,
				conclusionFactory_.getObjectUnionOf(disjoint),
				conclusionFactory_.getOwlNothing());
		elkInferenceFactory_.getElkClassInclusionEmptyObjectUnionOfOwlNothing();
		elkInferenceFactory_.getElkToldAxiom(originalAxiom);
		return null;

	}

	@Override
	public Void visit(
			ElkDisjointUnionAxiomSubClassConversionMatch1 inferenceMatch1) {
		inferenceMatch1.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		ElkDisjointUnionAxiomSubClassConversion parent = inferenceMatch1
				.getParent();
		ElkDisjointUnionAxiom originalAxiom = parent.getOriginalAxiom();
		ElkClass defined = originalAxiom.getDefinedClass();
		List<? extends ElkClassExpression> disjoint = originalAxiom
				.getClassExpressions();
		int disjunctPos = parent.getDisjunctPosition();
		ElkObjectUnionOf union = conclusionFactory_.getObjectUnionOf(disjoint);
		elkInferenceFactory_.getElkEquivalentClassesOfDisjointUnion(defined,
				disjoint);
		elkInferenceFactory_.getElkClassInclusionOfEquivaletClasses(defined,
				union, false);
		elkInferenceFactory_.getElkClassInclusionObjectUnionOfComposition(
				disjoint, disjunctPos);
		elkInferenceFactory_.getElkClassInclusionHierarchy(
				disjoint.get(disjunctPos), union, defined);
		elkInferenceFactory_.getElkToldAxiom(originalAxiom);
		return null;
	}

	@Override
	public Void visit(
			ElkEquivalentClassesAxiomEquivalenceConversionMatch1 inferenceMatch1) {
		inferenceMatch1.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		ElkEquivalentClassesAxiomEquivalenceConversion parent = inferenceMatch1
				.getParent();
		ElkEquivalentClassesAxiom originalAxiom = parent.getOriginalAxiom();
		// we do not know which direction is going to be used
		elkInferenceFactory_.getElkClassInclusionOfEquivaletClasses(
				originalAxiom.getClassExpressions(),
				parent.getFirstMemberPosition(),
				parent.getSecondMemberPosition());
		elkInferenceFactory_.getElkClassInclusionOfEquivaletClasses(
				originalAxiom.getClassExpressions(),
				parent.getSecondMemberPosition(),
				parent.getFirstMemberPosition());
		elkInferenceFactory_.getElkToldAxiom(originalAxiom);
		return null;
	}

	@Override
	public Void visit(
			ElkEquivalentClassesAxiomSubClassConversionMatch1 inferenceMatch1) {
		inferenceMatch1.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		ElkEquivalentClassesAxiomSubClassConversion parent = inferenceMatch1
				.getParent();
		ElkEquivalentClassesAxiom originalAxiom = parent.getOriginalAxiom();
		elkInferenceFactory_.getElkClassInclusionOfEquivaletClasses(
				originalAxiom.getClassExpressions(),
				parent.getSubClassPosition(), parent.getSuperClassPosition());
		elkInferenceFactory_.getElkToldAxiom(originalAxiom);
		return null;
	}

	@Override
	public Void visit(
			ElkEquivalentObjectPropertiesAxiomConversionMatch1 inferenceMatch1) {
		inferenceMatch1.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		ElkEquivalentObjectPropertiesAxiomConversion parent = inferenceMatch1
				.getParent();
		ElkEquivalentObjectPropertiesAxiom originalAxiom = parent
				.getOriginalAxiom();
		elkInferenceFactory_.getElkPropertyInclusionOfEquivalence(
				originalAxiom.getObjectPropertyExpressions(),
				parent.getSubPropertyPosition(),
				parent.getSuperPropertyPosition());
		elkInferenceFactory_.getElkToldAxiom(originalAxiom);
		return null;
	}

	@Override
	public Void visit(
			ElkObjectPropertyAssertionAxiomConversionMatch1 inferenceMatch1) {
		inferenceMatch1.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		ElkObjectPropertyAssertionAxiomConversion parent = inferenceMatch1
				.getParent();
		ElkObjectPropertyAssertionAxiom originalAxiom = parent
				.getOriginalAxiom();
		elkInferenceFactory_.getElkClassInclusionOfObjectPropertyAssertion(
				originalAxiom.getSubject(), originalAxiom.getProperty(),
				originalAxiom.getObject());
		elkInferenceFactory_.getElkToldAxiom(originalAxiom);
		return null;
	}

	@Override
	public Void visit(
			ElkObjectPropertyDomainAxiomConversionMatch1 inferenceMatch1) {
		inferenceMatch1.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		ElkObjectPropertyDomainAxiom originalAxiom = inferenceMatch1.getParent()
				.getOriginalAxiom();
		elkInferenceFactory_.getElkClassInclusionOfObjectPropertyDomain(
				originalAxiom.getProperty(), originalAxiom.getDomain());
		elkInferenceFactory_.getElkToldAxiom(originalAxiom);
		return null;
	}

	@Override
	public Void visit(
			ElkObjectPropertyRangeAxiomConversionMatch1 inferenceMatch1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visit(
			ElkReflexiveObjectPropertyAxiomConversionMatch1 inferenceMatch1) {
		inferenceMatch1.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		ElkReflexiveObjectPropertyAxiom originalAxiom = inferenceMatch1
				.getParent().getOriginalAxiom();
		elkInferenceFactory_.getElkClassInclusionOfReflexiveObjectProperty(
				originalAxiom.getProperty());
		elkInferenceFactory_.getElkToldAxiom(originalAxiom);
		return null;

	}

	@Override
	public Void visit(ElkSameIndividualAxiomConversionMatch1 inferenceMatch1) {
		inferenceMatch1.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		ElkSameIndividualAxiomConversion parent = inferenceMatch1.getParent();
		ElkSameIndividualAxiom originalAxiom = parent.getOriginalAxiom();
		List<? extends ElkIndividual> same = originalAxiom.getIndividuals();
		ArrayList<ElkObjectOneOf> equivalent = new ArrayList<ElkObjectOneOf>(
				same.size());
		for (ElkIndividual ind : same) {
			equivalent.add(conclusionFactory_
					.getObjectOneOf(Collections.singletonList(ind)));
		}
		elkInferenceFactory_.getElkEquivalentClassesOfSameIndividual(same);
		elkInferenceFactory_.getElkClassInclusionOfEquivaletClasses(equivalent,
				parent.getSubIndividualPosition(),
				parent.getSuperIndividualPosition());
		elkInferenceFactory_.getElkToldAxiom(originalAxiom);
		return null;
	}

	@Override
	public Void visit(ElkSubClassOfAxiomConversionMatch1 inferenceMatch1) {
		inferenceMatch1.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		ElkSubClassOfAxiomConversion parent = inferenceMatch1.getParent();
		ElkSubClassOfAxiom originalAxiom = parent.getOriginalAxiom();
		elkInferenceFactory_.getElkToldAxiom(originalAxiom);
		return null;
	}

	@Override
	public Void visit(
			ElkSubObjectPropertyOfAxiomConversionMatch1 inferenceMatch1) {
		inferenceMatch1.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		ElkSubObjectPropertyOfAxiomConversion parent = inferenceMatch1
				.getParent();
		ElkSubObjectPropertyOfAxiom originalAxiom = parent.getOriginalAxiom();
		elkInferenceFactory_.getElkToldAxiom(originalAxiom);
		return null;
	}

	@Override
	public Void visit(
			ElkTransitiveObjectPropertyAxiomConversionMatch1 inferenceMatch1) {
		inferenceMatch1.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		ElkTransitiveObjectPropertyAxiom originalAxiom = inferenceMatch1
				.getParent().getOriginalAxiom();
		elkInferenceFactory_.getElkPropertyInclusionOfTransitiveObjectProperty(
				originalAxiom.getProperty());
		elkInferenceFactory_.getElkToldAxiom(originalAxiom);
		return null;
	}

	@Override
	public Void visit(ForwardLinkCompositionMatch1 inferenceMatch1) {
		ForwardLinkMatch1 conclusionMatch1 = inferenceMatch1
				.getConclusionMatch(conclusionFactory_);
		inferences_.add(conclusionMatch1, inferenceMatch1);
		for (ForwardLinkMatch2 child : hierarchy_
				.getChildren(conclusionMatch1)) {
			(new ForwardLinkMatch2InferenceVisitor(inferenceFactory_, child))
					.visit(inferenceMatch1);
		}
		return null;
	}

	@Override
	public Void visit(ForwardLinkCompositionMatch2 inferenceMatch2) {
		BackwardLinkMatch1 premiseMatch1 = inferenceMatch2
				.getFirstPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch2);
		for (BackwardLinkMatch2 child : hierarchy_.getChildren(premiseMatch1)) {
			(new BackwardLinkMatch2InferenceVisitor(inferenceFactory_, child))
					.visit(inferenceMatch2);
		}
		return null;
	}

	@Override
	public Void visit(ForwardLinkCompositionMatch3 inferenceMatch3) {
		SubPropertyChainMatch1 premiseMatch1 = inferenceMatch3
				.getFourthPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch3);
		for (SubPropertyChainMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new SubPropertyChainMatch2InferenceVisitor(inferenceFactory_,
					child)).visit(inferenceMatch3);
		}
		return null;
	}

	@Override
	public Void visit(ForwardLinkCompositionMatch4 inferenceMatch4) {
		ForwardLinkMatch2 premiseMatch2 = inferenceMatch4
				.getThirdPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch2, inferenceMatch4);
		for (ForwardLinkMatch3 child : hierarchy_.getChildren(premiseMatch2)) {
			(new ForwardLinkMatch3InferenceVisitor(inferenceFactory_, child))
					.visit(inferenceMatch4);
		}
		return null;
	}

	@Override
	public Void visit(ForwardLinkCompositionMatch5 inferenceMatch5) {
		inferenceMatch5.getSecondPremiseMatch(conclusionFactory_);
		inferenceMatch5.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		IndexedContextRootMatchChain premiseIntermediateRoots = inferenceMatch5
				.getPremiseIntermediateRoots();
		IndexedContextRootMatch conclusionTargetMatch = inferenceMatch5
				.getConclusionTargetMatch();
		ForwardLinkCompositionMatch4 inferenceMatch4 = inferenceMatch5
				.getParent();
		int premiseForwardChainStartPos = inferenceMatch4
				.getPremiseForwardChainStartPos();
		ElkSubObjectPropertyExpression premiseFullForwardChainMatch = inferenceMatch4
				.getPremiseFullForwardChainMatch();
		ForwardLinkCompositionMatch3 inferenceMatch3 = inferenceMatch4
				.getParent();
		ElkObjectProperty premiseBackwardRelationMatch = inferenceMatch3
				.getPremiseBackwardRelationMatch();
		IndexedContextRootMatch originMatch = inferenceMatch3.getOriginMatch();
		ForwardLinkCompositionMatch2 inferenceMatch2 = inferenceMatch3
				.getParent();
		int compositionStartPos = inferenceMatch2.getCompositionStartPos();
		ElkSubObjectPropertyExpression fullCompositionMatch = inferenceMatch2
				.getFullCompositionMatch();
		ForwardLinkCompositionMatch1 inferenceMatch1 = inferenceMatch2
				.getParent();
		IndexedContextRootMatch destinationMatch = inferenceMatch1
				.getDestinationMatch();

		// unfolding the first premise under the second premise
		List<? extends ElkObjectPropertyExpression> fullCompositionList = toList(
				fullCompositionMatch);
		elkInferenceFactory_.getElkClassInclusionExistentialPropertyUnfolding(
				toElkExpression(destinationMatch), premiseBackwardRelationMatch,
				toElkExpression(originMatch),
				fullCompositionList.get(compositionStartPos));
		List<? extends ElkObjectPropertyExpression> forwardChainList = toList(
				premiseFullForwardChainMatch);
		if (premiseForwardChainStartPos == 0) {
			// composing and unfolding the third premise with the fourth premise
			elkInferenceFactory_
					.getElkClassInclusionExistentialPropertyUnfolding(
							toList(originMatch, premiseIntermediateRoots,
									conclusionTargetMatch),
							forwardChainList,
							fullCompositionList.get(compositionStartPos + 1));
		}

		return null;
	}

	@Override
	public Void visit(ForwardLinkOfObjectHasSelfMatch1 inferenceMatch1) {
		SubClassInclusionDecomposedMatch1 premiseMatch1 = inferenceMatch1
				.getPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch1);
		for (SubClassInclusionDecomposedMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new SubClassInclusionDecomposedMatch2InferenceVisitor(
					inferenceFactory_, child)).visit(inferenceMatch1);
		}
		return null;
	}

	@Override
	public Void visit(ForwardLinkOfObjectHasSelfMatch2 inferenceMatch2) {
		inferenceMatch2.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		ElkObjectProperty relationMatch = inferenceMatch2.getRelationMatch();
		ForwardLinkOfObjectHasSelfMatch1 inferenceMatch1 = inferenceMatch2
				.getParent();
		IndexedContextRootMatch originMatch = inferenceMatch1.getOriginMatch();

		elkInferenceFactory_.getElkClassInclusionExistentialOfObjectHasSelf(
				toElkExpression(originMatch), relationMatch);

		return null;
	}

	@Override
	public Void visit(ForwardLinkOfObjectSomeValuesFromMatch1 inferenceMatch1) {
		SubClassInclusionDecomposedMatch1 premiseMatch1 = inferenceMatch1
				.getPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch1);
		for (SubClassInclusionDecomposedMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new SubClassInclusionDecomposedMatch2InferenceVisitor(
					inferenceFactory_, child)).visit(inferenceMatch1);
		}
		return null;
	}

	@Override
	public Void visit(ForwardLinkOfObjectSomeValuesFromMatch2 inferenceMatch2) {
		inferenceMatch2.getConclusionMatch(conclusionFactory_);

		// no ELK inferences
		return null;
	}

	@Override
	public Void visit(PropagationGeneratedMatch1 inferenceMatch1) {
		inferenceMatch1.getSecondPremiseMatch(conclusionFactory_);
		inferenceMatch1.getThirdPremiseMatch(conclusionFactory_);

		// no ELK inferences
		return null;
	}

	@Override
	public Void visit(PropertyRangeInheritedMatch1 inferenceMatch1) {
		IndexedObjectPropertyRangeAxiomMatch1 premiseMatch1 = inferenceMatch1
				.getSecondPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch1);
		for (IndexedObjectPropertyRangeAxiomMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new IndexedObjectPropertyRangeAxiomMatch2InferenceVisitor(
					inferenceFactory_, child)).visit(inferenceMatch1);
		}
		return null;
	}

	@Override
	public Void visit(PropertyRangeInheritedMatch2 inferenceMatch2) {
		SubPropertyChainMatch1 premiseMatch1 = inferenceMatch2
				.getFirstPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch2);
		for (SubPropertyChainMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new SubPropertyChainMatch2InferenceVisitor(inferenceFactory_,
					child)).visit(inferenceMatch2);
		}
		return null;
	}

	@Override
	public Void visit(PropertyRangeInheritedMatch3 inferenceMatch3) {
		inferenceMatch3.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		ElkObjectProperty superPropertyMatch = inferenceMatch3
				.getSuperPropertyMatch();
		PropertyRangeInheritedMatch2 inferenceMatch2 = inferenceMatch3
				.getParent();
		ElkClassExpression rangeMatch = inferenceMatch2.getRangeMatch();
		PropertyRangeInheritedMatch1 inferenceMatch1 = inferenceMatch2
				.getParent();
		ElkObjectProperty subPropertyMatch = inferenceMatch1
				.getSubPropertyMatch();

		elkInferenceFactory_.getElkPropertyRangePropertyUnfolding(
				superPropertyMatch, rangeMatch, subPropertyMatch);
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionComposedDefinedClassMatch1 inferenceMatch1) {
		IndexedEquivalentClassesAxiomMatch1 premiseMatch1 = inferenceMatch1
				.getSecondPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch1);
		for (IndexedEquivalentClassesAxiomMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new IndexedEquivalentClassesAxiomMatch2InferenceVisitor(
					inferenceFactory_, child)).visit(inferenceMatch1);
		}
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionComposedDefinedClassMatch2 inferenceMatch2) {
		inferenceMatch2.getFirstPremiseMatch(conclusionFactory_);

		// creating ELK inferences
		ElkClassExpression definitionMatch = inferenceMatch2
				.getDefinitionMatch();
		SubClassInclusionComposedDefinedClassMatch1 inferenceMatch1 = inferenceMatch2
				.getParent();
		ElkClass definedClassMatch = inferenceMatch1.getDefinedClassMatch();
		IndexedContextRootMatch originMatch = inferenceMatch1.getOriginMatch();

		elkInferenceFactory_.getElkClassInclusionHierarchy(
				toElkExpression(originMatch), definitionMatch,
				definedClassMatch);

		return null;
	}

	@Override
	public Void visit(SubClassInclusionComposedEntityMatch1 inferenceMatch1) {
		inferenceMatch1.getPremiseMatch(conclusionFactory_);
		// no ELK inferences
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionComposedObjectIntersectionOfMatch1 inferenceMatch1) {
		inferenceMatch1.getFirstPremiseMatch(conclusionFactory_);
		inferenceMatch1.getSecondPremiseMatch(conclusionFactory_);

		// creating ELK inferences
		int prefixLength = inferenceMatch1.getConclusionSubsumerPrefixLength();
		ElkObjectIntersectionOf fullSubsumerMatch = inferenceMatch1
				.getFullSubsumerMatch();
		List<? extends ElkClassExpression> conjuncts = fullSubsumerMatch
				.getClassExpressions();

		if (prefixLength < conjuncts.size()) {
			// the conjunction is not fully composed yet
			return null;
		}
		// else
		IndexedContextRootMatch originMatch = inferenceMatch1.getOriginMatch();
		elkInferenceFactory_
				.getElkClassInclusionObjectIntersectionOfComposition(
						toElkExpression(originMatch), conjuncts);

		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionComposedObjectSomeValuesFromMatch1 inferenceMatch1) {
		BackwardLinkMatch1 premiseMatch1 = inferenceMatch1
				.getFirstPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch1);
		for (BackwardLinkMatch2 child : hierarchy_.getChildren(premiseMatch1)) {
			(new BackwardLinkMatch2InferenceVisitor(inferenceFactory_, child))
					.visit(inferenceMatch1);
		}
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionComposedObjectSomeValuesFromMatch2 inferenceMatch2) {
		inferenceMatch2.getSecondPremiseMatch(conclusionFactory_);

		// creating ELK inferences
		IndexedContextRootMatch originMatch = inferenceMatch2.getOriginMatch();
		ElkObjectProperty propagationRelationMatch = inferenceMatch2
				.getPropagationRelationMatch();
		SubClassInclusionComposedObjectSomeValuesFromMatch1 inferenceMatch1 = inferenceMatch2
				.getParent();
		IndexedContextRootMatch destinationMatch = inferenceMatch1
				.getDestinationMatch();
		IndexedObjectSomeValuesFromMatch conclusionSubsumerMatch = inferenceMatch1
				.getConclusionSubsumerMatch();

		ElkClassExpression fillerMatch = conclusionSubsumerMatch.accept(
				new IndexedObjectSomeValuesFromMatch.Visitor<ElkClassExpression>() {

					@Override
					public ElkClassExpression visit(
							SubsumerObjectHasValueMatch match) {
						return conclusionFactory_.getObjectOneOf(Collections
								.singletonList(match.getValue().getFiller()));
					}

					@Override
					public ElkClassExpression visit(
							SubsumerObjectSomeValuesFromMatch match) {
						return match.getValue().getFiller();
					}
				});

		elkInferenceFactory_.getElkClassInclusionExistentialFillerUnfolding(
				toElkExpression(destinationMatch), propagationRelationMatch,
				toElkExpression(originMatch), fillerMatch);
		elkInferenceFactory_.getElkClassInclusionExistentialPropertyUnfolding(
				toElkExpression(destinationMatch), propagationRelationMatch,
				fillerMatch, conclusionSubsumerMatch.getPropertyMatch());

		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionComposedObjectUnionOfMatch1 inferenceMatch1) {
		inferenceMatch1.getPremiseMatch(conclusionFactory_);

		// creating ELK inferences
		final IndexedContextRootMatch originMatch = inferenceMatch1
				.getOriginMatch();
		IndexedObjectUnionOfMatch disjunctionMatch = inferenceMatch1
				.getConclusionSubsumerMatch();
		final int pos = inferenceMatch1.getPosition();
		final ElkClassExpression subExpression = toElkExpression(originMatch);
		disjunctionMatch.accept(new IndexedObjectUnionOfMatch.Visitor<Void>() {

			@Override
			public Void visit(SubsumerObjectOneOfMatch match) {
				ElkObjectOneOf enumeration = match.getValue();
				List<? extends ElkIndividual> members = enumeration
						.getIndividuals();

				ElkIndividual element = members.get(pos);
				elkInferenceFactory_.getElkClassInclusionObjectOneOfInclusion(
						members, Collections.singletonList(pos));
				elkInferenceFactory_.getElkClassInclusionHierarchy(
						subExpression,
						conclusionFactory_.getObjectOneOf(element),
						enumeration);
				return null;
			}

			@Override
			public Void visit(SubsumerObjectUnionOfMatch match) {
				ElkObjectUnionOf disjunction = match.getValue();
				List<? extends ElkClassExpression> disjuncts = disjunction
						.getClassExpressions();

				ElkClassExpression disjunct = disjuncts.get(pos);
				elkInferenceFactory_
						.getElkClassInclusionObjectUnionOfComposition(disjuncts,
								pos);
				elkInferenceFactory_.getElkClassInclusionHierarchy(
						subExpression, disjunct, disjunction);
				return null;
			}

		});
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionDecomposedFirstConjunctMatch1 inferenceMatch1) {
		SubClassInclusionDecomposedMatch1 premiseMatch1 = inferenceMatch1
				.getPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch1);
		for (SubClassInclusionDecomposedMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new SubClassInclusionDecomposedMatch2InferenceVisitor(
					inferenceFactory_, child)).visit(inferenceMatch1);
		}
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionDecomposedFirstConjunctMatch2 inferenceMatch2) {
		inferenceMatch2.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		int premiseSubsumerPrefixLength = inferenceMatch2
				.getPremiseSubsumerPrefixLength();

		if (premiseSubsumerPrefixLength > 2) {
			// no ELK inference
			return null;
		}

		ElkObjectIntersectionOf fullSubsumerMatch = inferenceMatch2
				.getFullSubsumerMatch();
		SubClassInclusionDecomposedFirstConjunctMatch1 inferenceMatch1 = inferenceMatch2
				.getParent();
		IndexedContextRootMatch originMatch = inferenceMatch1.getOriginMatch();
		List<? extends ElkClassExpression> conjuncts = fullSubsumerMatch
				.getClassExpressions();
		int pos = 0; // of the decomposed conjunct
		elkInferenceFactory_.getElkClassInclusionHierarchy(
				toElkExpression(originMatch),
				conclusionFactory_.getObjectIntersectionOf(conjuncts),
				conjuncts.get(pos));
		elkInferenceFactory_
				.getElkClassInclusionObjectIntersectionOfDecomposition(
						conjuncts, pos);
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionDecomposedSecondConjunctMatch1 inferenceMatch1) {
		SubClassInclusionDecomposedMatch1 premiseMatch1 = inferenceMatch1
				.getPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch1);
		for (SubClassInclusionDecomposedMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new SubClassInclusionDecomposedMatch2InferenceVisitor(
					inferenceFactory_, child)).visit(inferenceMatch1);
		}
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionDecomposedSecondConjunctMatch2 inferenceMatch2) {
		inferenceMatch2.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		ElkObjectIntersectionOf fullSubsumerMatch = inferenceMatch2
				.getFullSubsumerMatch();
		int subsumerPrefixLength = inferenceMatch2
				.getPremiseSubsumerPrefixLength();
		SubClassInclusionDecomposedSecondConjunctMatch1 inferenceMatch1 = inferenceMatch2
				.getParent();
		IndexedContextRootMatch originMatch = inferenceMatch1.getOriginMatch();

		List<? extends ElkClassExpression> conjuncts = fullSubsumerMatch
				.getClassExpressions();
		int pos = subsumerPrefixLength - 1; // of the decomposed conjunct
		elkInferenceFactory_.getElkClassInclusionHierarchy(
				toElkExpression(originMatch),
				conclusionFactory_.getObjectIntersectionOf(conjuncts),
				conjuncts.get(pos));
		elkInferenceFactory_
				.getElkClassInclusionObjectIntersectionOfDecomposition(
						conjuncts, pos);
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionExpandedDefinitionMatch1 inferenceMatch1) {
		IndexedEquivalentClassesAxiomMatch1 premiseMatch1 = inferenceMatch1
				.getSecondPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch1);
		for (IndexedEquivalentClassesAxiomMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new IndexedEquivalentClassesAxiomMatch2InferenceVisitor(
					inferenceFactory_, child)).visit(inferenceMatch1);
		}
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionExpandedDefinitionMatch2 inferenceMatch2) {
		inferenceMatch2.getFirstPremiseMatch(conclusionFactory_);
		inferenceMatch2.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		ElkClass definedClassMatch = inferenceMatch2.getDefinedClassMatch();
		ElkClassExpression definitionMatch = inferenceMatch2
				.getDefinitionMatch();
		SubClassInclusionExpandedDefinitionMatch1 inferenceMatch1 = inferenceMatch2
				.getParent();
		IndexedContextRootMatch originMatch = inferenceMatch1.getOriginMatch();

		elkInferenceFactory_.getElkClassInclusionHierarchy(
				toElkExpression(originMatch), definedClassMatch,
				definitionMatch);

		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionExpandedFirstEquivalentClassMatch1 inferenceMatch1) {
		IndexedEquivalentClassesAxiomMatch1 premiseMatch1 = inferenceMatch1
				.getSecondPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch1);
		for (IndexedEquivalentClassesAxiomMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new IndexedEquivalentClassesAxiomMatch2InferenceVisitor(
					inferenceFactory_, child)).visit(inferenceMatch1);
		}
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionExpandedFirstEquivalentClassMatch2 inferenceMatch2) {
		inferenceMatch2.getFirstPremiseMatch(conclusionFactory_);
		inferenceMatch2.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		ElkClassExpression conclusionSubsumerMatch = inferenceMatch2
				.getConclusionSubsumerMatch();
		ElkClassExpression premiseSubsumerMatch = inferenceMatch2
				.getPremiseSubsumerMatch();
		SubClassInclusionExpandedFirstEquivalentClassMatch1 inferenceMatch1 = inferenceMatch2
				.getParent();
		IndexedContextRootMatch originMatch = inferenceMatch1.getOriginMatch();

		elkInferenceFactory_.getElkClassInclusionHierarchy(
				toElkExpression(originMatch), premiseSubsumerMatch,
				conclusionSubsumerMatch);

		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionExpandedSecondEquivalentClassMatch1 inferenceMatch1) {
		IndexedEquivalentClassesAxiomMatch1 premiseMatch1 = inferenceMatch1
				.getSecondPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch1);
		for (IndexedEquivalentClassesAxiomMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new IndexedEquivalentClassesAxiomMatch2InferenceVisitor(
					inferenceFactory_, child)).visit(inferenceMatch1);
		}
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionExpandedSecondEquivalentClassMatch2 inferenceMatch2) {
		inferenceMatch2.getFirstPremiseMatch(conclusionFactory_);
		inferenceMatch2.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		ElkClassExpression conclusionSubsumerMatch = inferenceMatch2
				.getConclusionSubsumerMatch();
		ElkClassExpression premiseSubsumerMatch = inferenceMatch2
				.getPremiseSubsumerMatch();
		SubClassInclusionExpandedSecondEquivalentClassMatch1 inferenceMatch1 = inferenceMatch2
				.getParent();
		IndexedContextRootMatch originMatch = inferenceMatch1.getOriginMatch();

		elkInferenceFactory_.getElkClassInclusionHierarchy(
				toElkExpression(originMatch), premiseSubsumerMatch,
				conclusionSubsumerMatch);

		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionExpandedSubClassOfMatch1 inferenceMatch1) {
		IndexedSubClassOfAxiomMatch1 premiseMatch1 = inferenceMatch1
				.getSecondPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch1);
		for (IndexedSubClassOfAxiomMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new IndexedSubClassOfAxiomMatch2InferenceVisitor(inferenceFactory_,
					child)).visit(inferenceMatch1);
		}
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionExpandedSubClassOfMatch2 inferenceMatch2) {
		inferenceMatch2.getFirstPremiseMatch(conclusionFactory_);
		inferenceMatch2.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		ElkClassExpression conclusionSubsumerMatch = inferenceMatch2
				.getConclusionSubsumerMatch();
		ElkClassExpression premiseSubsumerMatch = inferenceMatch2
				.getPremiseSubsumerMatch();
		SubClassInclusionExpandedSubClassOfMatch1 inferenceMatch1 = inferenceMatch2
				.getParent();
		IndexedContextRootMatch originMatch = inferenceMatch1.getOriginMatch();

		elkInferenceFactory_.getElkClassInclusionHierarchy(
				toElkExpression(originMatch), premiseSubsumerMatch,
				conclusionSubsumerMatch);

		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionObjectHasSelfPropertyRangeMatch1 inferenceMatch1) {
		SubClassInclusionDecomposedMatch1 premiseMatch1 = inferenceMatch1
				.getFirstPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch1);
		for (SubClassInclusionDecomposedMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new SubClassInclusionDecomposedMatch2InferenceVisitor(
					inferenceFactory_, child)).visit(inferenceMatch1);
		}
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionObjectHasSelfPropertyRangeMatch2 inferenceMatch2) {
		PropertyRangeMatch1 premiseMatch1 = inferenceMatch2
				.getSecondPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch2);
		for (PropertyRangeMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new PropertyRangeMatch2InferenceVisitor(inferenceFactory_, child))
					.visit(inferenceMatch2);
		}
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionObjectHasSelfPropertyRangeMatch3 inferenceMatch3) {
		inferenceMatch3.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		ElkClassExpression rangeMatch = inferenceMatch3.getRangeMatch();
		SubClassInclusionObjectHasSelfPropertyRangeMatch2 inferenceMatch2 = inferenceMatch3
				.getParent();
		ElkObjectProperty propertyMatch = inferenceMatch2.getPropertyMatch();
		SubClassInclusionObjectHasSelfPropertyRangeMatch1 inferenceMatch1 = inferenceMatch2
				.getParent();
		IndexedContextRootMatch originMatch = inferenceMatch1.getOriginMatch();

		elkInferenceFactory_.getElkClassInclusionReflexivePropertyRange(
				toElkExpression(originMatch), propertyMatch, rangeMatch);

		return null;
	}

	@Override
	public Void visit(SubClassInclusionOwlThingMatch1 inferenceMatch1) {
		inferenceMatch1.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		IndexedContextRootMatch originMatch = inferenceMatch1.getOriginMatch();

		elkInferenceFactory_
				.getElkClassInclusionOwlThing(toElkExpression(originMatch));
		return null;
	}

	@Override
	public Void visit(SubClassInclusionRangeMatch1 inferenceMatch1) {
		PropertyRangeMatch1 premiseMatch1 = inferenceMatch1
				.getPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch1);
		for (PropertyRangeMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new PropertyRangeMatch2InferenceVisitor(inferenceFactory_, child))
					.visit(inferenceMatch1);
		}
		return null;
	}

	@Override
	public Void visit(SubClassInclusionRangeMatch2 inferenceMatch2) {
		inferenceMatch2.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		// TODO
		return null;
	}

	@Override
	public Void visit(SubClassInclusionTautologyMatch1 inferenceMatch1) {
		inferenceMatch1.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		IndexedContextRootMatch originMatch = inferenceMatch1.getOriginMatch();

		elkInferenceFactory_
				.getElkClassInclusionTautology(toElkExpression(originMatch));
		return null;
	}

	@Override
	public Void visit(
			SubPropertyChainExpandedSubObjectPropertyOfMatch1 inferenceMatch1) {
		IndexedSubObjectPropertyOfAxiomMatch1 premiseMatch1 = inferenceMatch1
				.getFirstPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch1);
		for (IndexedSubObjectPropertyOfAxiomMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new IndexedSubObjectPropertyOfAxiomMatch2InferenceVisitor(
					inferenceFactory_, child)).visit(inferenceMatch1);
		}
		return null;
	}

	@Override
	public Void visit(
			SubPropertyChainExpandedSubObjectPropertyOfMatch2 inferenceMatch2) {
		inferenceMatch2.getSecondPremiseMatch(conclusionFactory_);
		inferenceMatch2.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		ElkObjectProperty interPropertyMatch = inferenceMatch2
				.getInterPropertyMatch();
		ElkSubObjectPropertyExpression subChainMatch = inferenceMatch2
				.getSubChainMatch();
		final SubPropertyChainExpandedSubObjectPropertyOfMatch1 inferenceMatch1 = inferenceMatch2
				.getParent();
		final ElkSubObjectPropertyExpression fullSuperChainMatch = inferenceMatch1
				.getFullSuperChainMatch();
		final int superChainStartPos = inferenceMatch1.getSuperChainStartPos();
		ElkObjectPropertyExpression superPropertyMatch = fullSuperChainMatch
				.accept(new ElkSubObjectPropertyExpressionVisitor<ElkObjectPropertyExpression>() {

					@Override
					public ElkObjectPropertyExpression visit(
							ElkObjectInverseOf expression) {
						return expression;
					}

					@Override
					public ElkObjectPropertyExpression visit(
							ElkObjectProperty expression) {
						return expression;
					}

					@Override
					public ElkObjectPropertyExpression visit(
							ElkObjectPropertyChain expression) {
						List<? extends ElkObjectPropertyExpression> chain = expression
								.getObjectPropertyExpressions();
						if (superChainStartPos == chain.size() - 1) {
							return chain.get(superChainStartPos);
						}
						// else
						throw new ElkMatchException(
								inferenceMatch1.getParent().getSuperProperty(),
								fullSuperChainMatch, superChainStartPos);
					}
				});

		elkInferenceFactory_.getElkPropertyInclusionHierarchy(subChainMatch,
				interPropertyMatch, superPropertyMatch);
		return null;
	}

	@Override
	public Void visit(SubPropertyChainTautologyMatch1 inferenceMatch1) {
		inferenceMatch1.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		ElkObjectPropertyExpression expression = inferenceMatch1
				.getPropertyExpressionMatch();
		if (expression != null) {
			elkInferenceFactory_.getElkPropertyInclusionTautology(expression);
		}

		return null;
	}

}
