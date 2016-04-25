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
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch1;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch2;
import org.semanticweb.elk.matching.conclusions.IndexedClassExpressionMatch;
import org.semanticweb.elk.matching.conclusions.IndexedContextRootMatch;
import org.semanticweb.elk.matching.conclusions.IndexedContextRootMatchChain;
import org.semanticweb.elk.matching.conclusions.IndexedEquivalentClassesAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedEquivalentClassesAxiomMatch2;
import org.semanticweb.elk.matching.conclusions.IndexedObjectPropertyRangeAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedObjectPropertyRangeAxiomMatch2;
import org.semanticweb.elk.matching.conclusions.IndexedRangeFillerMatch;
import org.semanticweb.elk.matching.conclusions.IndexedSubClassOfAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedSubClassOfAxiomMatch2;
import org.semanticweb.elk.matching.conclusions.IndexedSubObjectPropertyOfAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedSubObjectPropertyOfAxiomMatch2;
import org.semanticweb.elk.matching.conclusions.PropagationMatch1;
import org.semanticweb.elk.matching.conclusions.PropagationMatch2;
import org.semanticweb.elk.matching.conclusions.PropagationMatch3;
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
import org.semanticweb.elk.matching.inferences.BackwardLinkCompositionMatch6;
import org.semanticweb.elk.matching.inferences.BackwardLinkOfObjectHasSelfMatch1;
import org.semanticweb.elk.matching.inferences.BackwardLinkOfObjectHasSelfMatch2;
import org.semanticweb.elk.matching.inferences.BackwardLinkOfObjectSomeValuesFromMatch1;
import org.semanticweb.elk.matching.inferences.BackwardLinkOfObjectSomeValuesFromMatch2;
import org.semanticweb.elk.matching.inferences.BackwardLinkReversedExpandedMatch1;
import org.semanticweb.elk.matching.inferences.BackwardLinkReversedExpandedMatch2;
import org.semanticweb.elk.matching.inferences.BackwardLinkReversedExpandedMatch3;
import org.semanticweb.elk.matching.inferences.BackwardLinkReversedMatch1;
import org.semanticweb.elk.matching.inferences.BackwardLinkReversedMatch2;
import org.semanticweb.elk.matching.inferences.ElkClassAssertionAxiomConversionMatch1;
import org.semanticweb.elk.matching.inferences.ElkDifferentIndividualsAxiomBinaryConversionMatch1;
import org.semanticweb.elk.matching.inferences.ElkDisjointClassesAxiomBinaryConversionMatch1;
import org.semanticweb.elk.matching.inferences.ElkDisjointUnionAxiomBinaryConversionMatch1;
import org.semanticweb.elk.matching.inferences.ElkDisjointUnionAxiomSubClassConversionMatch1;
import org.semanticweb.elk.matching.inferences.ElkEquivalentClassesAxiomEquivalenceConversionMatch1;
import org.semanticweb.elk.matching.inferences.ElkEquivalentClassesAxiomSubClassConversionMatch1;
import org.semanticweb.elk.matching.inferences.ElkEquivalentObjectPropertiesAxiomConversionMatch1;
import org.semanticweb.elk.matching.inferences.ElkObjectPropertyAssertionAxiomConversionMatch1;
import org.semanticweb.elk.matching.inferences.ElkObjectPropertyDomainAxiomConversionMatch1;
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
import org.semanticweb.elk.matching.inferences.PropagationGeneratedMatch2;
import org.semanticweb.elk.matching.inferences.PropagationGeneratedMatch3;
import org.semanticweb.elk.matching.inferences.PropertyRangeInheritedMatch1;
import org.semanticweb.elk.matching.inferences.PropertyRangeInheritedMatch2;
import org.semanticweb.elk.matching.inferences.PropertyRangeInheritedMatch3;
import org.semanticweb.elk.matching.inferences.SubClassInclusionComposedDefinedClassMatch1;
import org.semanticweb.elk.matching.inferences.SubClassInclusionComposedDefinedClassMatch2;
import org.semanticweb.elk.matching.inferences.SubClassInclusionComposedEntityMatch1;
import org.semanticweb.elk.matching.inferences.SubClassInclusionComposedObjectIntersectionOfMatch1;
import org.semanticweb.elk.matching.inferences.SubClassInclusionComposedObjectSomeValuesFromMatch1;
import org.semanticweb.elk.matching.inferences.SubClassInclusionComposedObjectSomeValuesFromMatch2;
import org.semanticweb.elk.matching.inferences.SubClassInclusionComposedObjectSomeValuesFromMatch3;
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
import org.semanticweb.elk.owl.inferences.ElkInference;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectInverseOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyChain;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectUnionOf;
import org.semanticweb.elk.owl.interfaces.ElkReflexiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkTransitiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.visitors.ElkSubObjectPropertyExpressionVisitor;
import org.semanticweb.elk.reasoner.indexing.model.ElkEquivalentClassesAxiomEquivalenceConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkEquivalentClassesAxiomSubClassConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkEquivalentObjectPropertiesAxiomConversion;

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
		return rootMatch.accept(
				new IndexedContextRootMatch.Visitor<ElkClassExpression>() {

					@Override
					public ElkClassExpression visit(
							IndexedClassExpressionMatch match) {
						return match.getValue();
					}

					@Override
					public ElkClassExpression visit(
							IndexedRangeFillerMatch match) {
						// TODO: get expression for the property
						return match.getValue().getFiller();
					}

				});
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
		SubPropertyChainMatch1 premiseMatch1 = inferenceMatch2
				.getSecondPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch2);
		for (SubPropertyChainMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new SubPropertyChainMatch2InferenceVisitor(inferenceFactory_,
					child)).visit(inferenceMatch2);
		}
		return null;
	}

	@Override
	public Void visit(BackwardLinkCompositionMatch3 inferenceMatch3) {
		BackwardLinkMatch1 premiseMatch1 = inferenceMatch3
				.getFirstPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch3);
		for (BackwardLinkMatch2 child : hierarchy_.getChildren(premiseMatch1)) {
			(new BackwardLinkMatch2InferenceVisitor(inferenceFactory_, child))
					.visit(inferenceMatch3);
		}
		return null;
	}

	@Override
	public Void visit(BackwardLinkCompositionMatch4 inferenceMatch4) {
		SubPropertyChainMatch1 premiseMatch1 = inferenceMatch4
				.getFourthPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch4);
		for (SubPropertyChainMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new SubPropertyChainMatch2InferenceVisitor(inferenceFactory_,
					child)).visit(inferenceMatch4);
		}
		return null;
	}

	@Override
	public Void visit(BackwardLinkCompositionMatch5 inferenceMatch5) {
		ForwardLinkMatch1 premiseMatch1 = inferenceMatch5
				.getThirdPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch5);
		for (ForwardLinkMatch2 child : hierarchy_.getChildren(premiseMatch1)) {
			(new ForwardLinkMatch2InferenceVisitor(inferenceFactory_, child))
					.visit(inferenceMatch5);
		}
		return null;
	}

	@Override
	public Void visit(BackwardLinkCompositionMatch6 inferenceMatch6) {
		inferenceMatch6.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		IndexedContextRootMatch destinationMatch = inferenceMatch6
				.getDestinationMatch();
		IndexedContextRootMatchChain intermediateRoots = inferenceMatch6
				.getIntermediateRoots();
		BackwardLinkCompositionMatch5 inferenceMatch5 = inferenceMatch6
				.getParent();
		int premiseForwardChainStartPos = inferenceMatch5
				.getPremiseForwardChainStartPos();
		ElkSubObjectPropertyExpression premiseFullForwardChainMatch = inferenceMatch5
				.getPremiseFullForwardChainMatch();
		BackwardLinkCompositionMatch4 inferenceMatch4 = inferenceMatch5
				.getParent();
		IndexedContextRootMatch originMatch = inferenceMatch4.getOriginMatch();
		BackwardLinkCompositionMatch3 inferenceMatch3 = inferenceMatch4
				.getParent();
		ElkObjectProperty premiseBackwardRelationMatch = inferenceMatch3
				.getPremiseBackwardRelationMatch();
		BackwardLinkCompositionMatch2 inferenceMatch2 = inferenceMatch3
				.getParent();
		ElkSubObjectPropertyExpression compositionMatch = inferenceMatch2
				.getCompositionMatch();
		BackwardLinkCompositionMatch1 inferenceMatch1 = inferenceMatch2
				.getParent();
		IndexedContextRootMatch conclusionSourceMatch = inferenceMatch1
				.getConclusionSourceMatch();
		ElkObjectProperty conclusionRelationMatch = inferenceMatch1
				.getConclusionRelationMatch();
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
		BackwardLinkOfObjectHasSelfMatch1 inferenceMatch1 = inferenceMatch2
				.getParent();
		IndexedContextRootMatch originMatch = inferenceMatch1.getOriginMatch();
		ElkObjectProperty relationMatch = inferenceMatch1.getRelationMatch();

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
		ForwardLinkMatch1 premiseMatch1 = inferenceMatch2
				.getFirstPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch2);
		for (ForwardLinkMatch2 child : hierarchy_.getChildren(premiseMatch1)) {
			(new ForwardLinkMatch2InferenceVisitor(inferenceFactory_, child))
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
		BackwardLinkReversedExpandedMatch1 inferenceMatch1 = inferenceMatch2
				.getParent();
		IndexedContextRootMatch originMatch = inferenceMatch1.getOriginMatch();
		ElkObjectProperty superPropertyMatch = inferenceMatch1
				.getSuperPropertyMatch();
		elkInferenceFactory_.getElkClassInclusionExistentialPropertyUnfolding(
				toList(originMatch, intermediateRoots, destinationMatch),
				toList(subChainMatch), superPropertyMatch);
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
		inferenceMatch2.getConclusionMatch(conclusionFactory_);
		// no ELK inferences
		return null;
	}

	@Override
	public Void visit(ElkClassAssertionAxiomConversionMatch1 inferenceMatch1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visit(
			ElkDifferentIndividualsAxiomBinaryConversionMatch1 inferenceMatch1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visit(
			ElkDisjointClassesAxiomBinaryConversionMatch1 inferenceMatch1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visit(
			ElkDisjointUnionAxiomBinaryConversionMatch1 inferenceMatch1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visit(
			ElkDisjointUnionAxiomSubClassConversionMatch1 inferenceMatch1) {
		// TODO Auto-generated method stub
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
		elkInferenceFactory_.getElkClassInclusionOfEquivalence(
				originalAxiom.getClassExpressions(),
				parent.getFirstMemberPosition(),
				parent.getSecondMemberPosition());
		elkInferenceFactory_.getElkClassInclusionOfEquivalence(
				originalAxiom.getClassExpressions(),
				parent.getSecondMemberPosition(),
				parent.getFirstMemberPosition());
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
		elkInferenceFactory_.getElkClassInclusionOfEquivalence(
				originalAxiom.getClassExpressions(),
				parent.getSubClassPosition(), parent.getSuperClassPosition());
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
		return null;
	}

	@Override
	public Void visit(
			ElkObjectPropertyAssertionAxiomConversionMatch1 inferenceMatch1) {
		// TODO Auto-generated method stub
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
		return null;

	}

	@Override
	public Void visit(ElkSameIndividualAxiomConversionMatch1 inferenceMatch1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visit(ElkSubClassOfAxiomConversionMatch1 inferenceMatch1) {
		inferenceMatch1.getConclusionMatch(conclusionFactory_);
		// no ELK inference necessary
		return null;
	}

	@Override
	public Void visit(
			ElkSubObjectPropertyOfAxiomConversionMatch1 inferenceMatch1) {
		inferenceMatch1.getConclusionMatch(conclusionFactory_);

		// no ELK inference necessary
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
		return null;
	}

	@Override
	public Void visit(ForwardLinkCompositionMatch1 inferenceMatch1) {
		SubPropertyChainMatch1 premiseMatch1 = inferenceMatch1
				.getSecondPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch1);
		for (SubPropertyChainMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new SubPropertyChainMatch2InferenceVisitor(inferenceFactory_,
					child)).visit(inferenceMatch1);
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
		ForwardLinkMatch1 premiseMatch1 = inferenceMatch4
				.getThirdPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch4);
		for (ForwardLinkMatch2 child : hierarchy_.getChildren(premiseMatch1)) {
			(new ForwardLinkMatch2InferenceVisitor(inferenceFactory_, child))
					.visit(inferenceMatch4);
		}
		return null;
	}

	@Override
	public Void visit(ForwardLinkCompositionMatch5 inferenceMatch5) {
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
		IndexedContextRootMatch originMatch = inferenceMatch3.getOriginMatch();
		ForwardLinkCompositionMatch2 inferenceMatch2 = inferenceMatch3
				.getParent();
		ElkObjectProperty premiseBackwardRelationMatch = inferenceMatch2
				.getPremiseBackwardRelationMatch();
		ForwardLinkCompositionMatch1 inferenceMatch1 = inferenceMatch2
				.getParent();
		int compositionStartPos = inferenceMatch1.getCompositionStartPos();
		ElkSubObjectPropertyExpression fullCompositionMatch = inferenceMatch1
				.getFullCompositionMatch();
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
		ForwardLinkOfObjectHasSelfMatch1 inferenceMatch1 = inferenceMatch2
				.getParent();
		IndexedContextRootMatch originMatch = inferenceMatch1.getOriginMatch();
		int chainStartPos = inferenceMatch1.getChainStartPos();
		ElkSubObjectPropertyExpression fullChainMatch = inferenceMatch1
				.getFullChainMatch();

		elkInferenceFactory_.getElkClassInclusionExistentialOfObjectHasSelf(
				toElkExpression(originMatch),
				toList(fullChainMatch).get(chainStartPos));

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
		SubPropertyChainMatch1 premiseMatch1 = inferenceMatch1
				.getThirdPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch1);
		for (SubPropertyChainMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new SubPropertyChainMatch2InferenceVisitor(inferenceFactory_,
					child)).visit(inferenceMatch1);
		}
		return null;
	}

	@Override
	public Void visit(PropagationGeneratedMatch2 inferenceMatch2) {
		PropagationMatch2 premiseMatch2 = inferenceMatch2
				.getConclusionMatch(conclusionFactory_);
		inferences_.add(premiseMatch2, inferenceMatch2);
		for (PropagationMatch3 child : hierarchy_.getChildren(premiseMatch2)) {
			(new PropagationMatch3InferenceVisitor(inferenceFactory_, child))
					.visit(inferenceMatch2);
		}
		return null;
	}

	@Override
	public Void visit(PropagationGeneratedMatch3 inferenceMatch3) {
		inferenceMatch3.getSecondPremiseMatch(conclusionFactory_);

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
			(new IndexedEquivalentClassesAxiomMatch2InferenceVisitor(inferenceFactory_,
					child)).visit(inferenceMatch1);
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
		PropagationMatch1 premiseMatch1 = inferenceMatch1
				.getSecondPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch1);
		for (PropagationMatch2 child : hierarchy_.getChildren(premiseMatch1)) {
			(new PropagationMatch2InferenceVisitor(inferenceFactory_, child))
					.visit(inferenceMatch1);
		}
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionComposedObjectSomeValuesFromMatch2 inferenceMatch2) {
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
	public Void visit(
			SubClassInclusionComposedObjectSomeValuesFromMatch3 inferenceMatch3) {
		inferenceMatch3.getSecondPremiseMatch(conclusionFactory_);

		// creating ELK inferences
		IndexedContextRootMatch originMatch = inferenceMatch3.getOriginMatch();
		SubClassInclusionComposedObjectSomeValuesFromMatch2 inferenceMatch2 = inferenceMatch3
				.getParent();
		ElkObjectProperty propagationRelationMatch = inferenceMatch2
				.getPropagationRelationMatch();
		SubClassInclusionComposedObjectSomeValuesFromMatch1 inferenceMatch1 = inferenceMatch2
				.getParent();
		IndexedContextRootMatch destinationMatch = inferenceMatch1
				.getDestinationMatch();
		ElkObjectSomeValuesFrom conclusionSubsumerMatch = inferenceMatch1
				.getConclusionSubsumerMatch();

		elkInferenceFactory_.getElkClassInclusionExistentialFillerUnfolding(
				toElkExpression(destinationMatch), propagationRelationMatch,
				toElkExpression(originMatch),
				conclusionSubsumerMatch.getFiller());
		elkInferenceFactory_.getElkClassInclusionExistentialPropertyUnfolding(
				toElkExpression(destinationMatch), propagationRelationMatch,
				conclusionSubsumerMatch.getFiller(),
				conclusionSubsumerMatch.getProperty());

		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionComposedObjectUnionOfMatch1 inferenceMatch1) {
		inferenceMatch1.getPremiseMatch(conclusionFactory_);

		// creating ELK inferences
		IndexedContextRootMatch originMatch = inferenceMatch1.getOriginMatch();
		ElkObjectUnionOf conclusionSubsumerMatch = inferenceMatch1
				.getConclusionSubsumerMatch();
		int positionMatch = inferenceMatch1.getPosition();

		elkInferenceFactory_.getElkClassInclusionObjectUnionOfComposition(
				toElkExpression(originMatch),
				conclusionSubsumerMatch.getClassExpressions(), positionMatch);
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

		elkInferenceFactory_
				.getElkClassInclusionObjectIntersectionOfDecomposition(
						toElkExpression(originMatch),
						fullSubsumerMatch.getClassExpressions(), 0);

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

		elkInferenceFactory_
				.getElkClassInclusionObjectIntersectionOfDecomposition(
						toElkExpression(originMatch),
						fullSubsumerMatch.getClassExpressions(),
						subsumerPrefixLength - 1);

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
			(new IndexedEquivalentClassesAxiomMatch2InferenceVisitor(inferenceFactory_,
					child)).visit(inferenceMatch1);
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
