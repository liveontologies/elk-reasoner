package org.semanticweb.elk.matching;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.semanticweb.elk.matching.conclusions.BackwardLinkMatch3;
import org.semanticweb.elk.matching.conclusions.BackwardLinkMatch4;
import org.semanticweb.elk.matching.conclusions.ClassInconsistencyMatch1;
import org.semanticweb.elk.matching.conclusions.ClassInconsistencyMatch2;
import org.semanticweb.elk.matching.conclusions.ConclusionMatchExpressionFactory;
import org.semanticweb.elk.matching.conclusions.ConclusionMatchHierarchy;
import org.semanticweb.elk.matching.conclusions.DisjointSubsumerMatch1;
import org.semanticweb.elk.matching.conclusions.DisjointSubsumerMatch2;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch1;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch2;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch3;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch4;
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
import org.semanticweb.elk.matching.conclusions.PropagationMatch1;
import org.semanticweb.elk.matching.conclusions.PropagationMatch2;
import org.semanticweb.elk.matching.conclusions.PropertyRangeMatch1;
import org.semanticweb.elk.matching.conclusions.PropertyRangeMatch2;
import org.semanticweb.elk.matching.conclusions.SubClassInclusionComposedMatch1;
import org.semanticweb.elk.matching.conclusions.SubClassInclusionComposedMatch2;
import org.semanticweb.elk.matching.conclusions.SubClassInclusionDecomposedMatch1;
import org.semanticweb.elk.matching.conclusions.SubClassInclusionDecomposedMatch2;
import org.semanticweb.elk.matching.conclusions.SubPropertyChainMatch1;
import org.semanticweb.elk.matching.conclusions.SubPropertyChainMatch2;
import org.semanticweb.elk.matching.inferences.*;
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
import org.semanticweb.elk.owl.interfaces.ElkObjectHasValue;
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectInverseOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectOneOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyChain;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyRangeAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
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

	private List<ElkClassExpression> addToList(List<ElkClassExpression> result,
			IndexedContextRootMatchChain intermediate,
			IndexedContextRootMatch last) {
		for (IndexedContextRootMatchChain todo = intermediate; todo != null; todo = todo
				.getTail()) {
			result.add(toElkExpression(todo.getHead()));
		}
		result.add(toElkExpression(last));
		return result;
	}

	/**
	 * Generates inferences for SubClassInclusion(extendedRootMatchExpression
	 * rootMatchExpression ) where the arguments correspond to the values of
	 * respectively extendedRootMatch and rootMatch under
	 * {@link #toElkExpression(IndexedContextRootMatch)}
	 * 
	 * @param extendedRootMatch
	 *            must be an extension of rootMatch (i.e., obtained by sequence
	 *            of calls
	 *            {@link IndexedContextRootMatch#extend(ElkClassExpression)}
	 * @param rootMatch
	 */
	private void deriveInclusion(IndexedContextRootMatch extendedRootMatch,
			IndexedContextRootMatch rootMatch) {
		if (rootMatch.equals(extendedRootMatch)) {
			// nothing to do
			return;
		}
		List<? extends ElkClassExpression> rootFillers = getFillerRanges(
				rootMatch);
		List<? extends ElkClassExpression> extendedRootFillers = getFillerRanges(
				extendedRootMatch);
		int rootFillersCount = rootFillers.size();
		if (rootFillersCount == 1) {
			elkInferenceFactory_
					.getElkClassInclusionObjectIntersectionOfDecomposition(
							extendedRootFillers, 0);
		} else {
			List<Integer> positions = new ArrayList<Integer>(rootFillersCount);
			for (int i = 0; i < rootFillersCount; i++) {
				positions.add(i);
			}
			elkInferenceFactory_
					.getElkClassInclusionObjectIntersectionOfInclusion(
							extendedRootFillers, positions);
		}
	}

	private ElkClassExpression getFillerMatch(
			IndexedObjectSomeValuesFromMatch match) {
		return match.accept(
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
	}

	private List<? extends ElkClassExpression> getFillerRanges(
			IndexedContextRootMatch rootMatch) {
		return rootMatch.getFillerMatches(conclusionFactory_);
	}

	private ElkClassExpression toElkExpression(
			IndexedContextRootMatch rootMatch) {
		return rootMatch.toElkExpression(conclusionFactory_);
	}

	private List<? extends ElkObjectPropertyExpression> toList(
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

	private List<? extends ElkClassExpression> toList(
			IndexedContextRootMatch first, IndexedContextRootMatch second,
			IndexedContextRootMatch last) {
		return Arrays.asList(toElkExpression(first), toElkExpression(second),
				toElkExpression(last));
	}

	private List<? extends ElkClassExpression> toList(
			IndexedContextRootMatch first,
			IndexedContextRootMatchChain intermediate,
			IndexedContextRootMatch last) {
		List<ElkClassExpression> result = new ArrayList<ElkClassExpression>();
		result.add(toElkExpression(first));
		return addToList(result, intermediate, last);
	}

	private List<? extends ElkClassExpression> toList(
			IndexedContextRootMatchChain chain, IndexedContextRootMatch last) {
		List<ElkClassExpression> result = new ArrayList<ElkClassExpression>();
		return addToList(result, chain, last);
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
		inferenceMatch3.getSecondPremiseMatch(conclusionFactory_);
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
		ForwardLinkMatch1 premiseMatch2 = inferenceMatch4
				.getThirdPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch2, inferenceMatch4);
		for (ForwardLinkMatch2 child : hierarchy_.getChildren(premiseMatch2)) {
			(new ForwardLinkMatch2InferenceVisitor(inferenceFactory_, child))
					.visit(inferenceMatch4);
		}
		return null;
	}

	@Override
	public Void visit(BackwardLinkCompositionMatch5 inferenceMatch5) {
		BackwardLinkMatch2 conclusionMatch2 = inferenceMatch5
				.getConclusionMatch(conclusionFactory_);
		inferences_.add(conclusionMatch2, inferenceMatch5);
		for (BackwardLinkMatch3 child : hierarchy_
				.getChildren(conclusionMatch2)) {
			(new BackwardLinkMatch3InferenceVisitor(inferenceFactory_, child))
					.visit(inferenceMatch5);
		}
		return null;
	}

	@Override
	public Void visit(BackwardLinkCompositionMatch6 inferenceMatch6) {
		ForwardLinkMatch3 premiseMatch3 = inferenceMatch6
				.getThirdPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch3, inferenceMatch6);
		for (ForwardLinkMatch4 child : hierarchy_.getChildren(premiseMatch3)) {
			(new ForwardLinkMatch4InferenceVisitor(inferenceFactory_, child))
					.visit(inferenceMatch6);
		}
		return null;
	}

	@Override
	public Void visit(BackwardLinkCompositionMatch7 inferenceMatch7) {
		BackwardLinkMatch3 premiseMatch3 = inferenceMatch7
				.getFirstPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch3, inferenceMatch7);
		for (BackwardLinkMatch4 child : hierarchy_.getChildren(premiseMatch3)) {
			(new BackwardLinkMatch4InferenceVisitor(inferenceFactory_, child))
					.visit(inferenceMatch7);
		}
		return null;
	}

	@Override
	public Void visit(BackwardLinkCompositionMatch8 inferenceMatch8) {
		inferenceMatch8.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		IndexedContextRootMatch extendedConclusionSourceMatch = inferenceMatch8
				.getExtendedConclusionSourceMatch();
		BackwardLinkCompositionMatch7 inferenceMatch7 = inferenceMatch8
				.getParent();
		IndexedContextRootMatchChain forwardChainExtendedDomains = inferenceMatch7
				.getForwardChainExtendedDomains();
		BackwardLinkCompositionMatch6 inferenceMatch6 = inferenceMatch7
				.getParent();
		IndexedContextRootMatch extendedDestinationMatch = inferenceMatch6
				.getExtendedDestinationMatch();
		BackwardLinkCompositionMatch5 inferenceMatch5 = inferenceMatch6
				.getParent();
		BackwardLinkCompositionMatch4 inferenceMatch4 = inferenceMatch5
				.getParent();
		ElkSubObjectPropertyExpression premiseFullForwardChainMatch = inferenceMatch4
				.getPremiseFullForwardChainMatch();
		int premiseForwardChainStartPos = inferenceMatch4
				.getPremiseForwardChainStartPos();
		BackwardLinkCompositionMatch3 inferenceMatch3 = inferenceMatch4
				.getParent();
		ElkObjectProperty premiseBackwardRelationMatch = inferenceMatch3
				.getPremiseBackwardRelationMatch();
		BackwardLinkCompositionMatch2 inferenceMatch2 = inferenceMatch3
				.getParent();
		ElkSubObjectPropertyExpression compositionMatch = inferenceMatch2
				.getCompositionMatch();
		ElkObjectProperty conclusionRelationMatch = inferenceMatch2
				.getConclusionRelationMatch();

		// unfolding the first premise under the second premise
		List<? extends ElkObjectPropertyExpression> compositionList = toList(
				compositionMatch);
		IndexedContextRootMatch extendedOriginMatch = forwardChainExtendedDomains
				.getHead();
		ElkClassExpression extendedOriginExpression = toElkExpression(
				extendedOriginMatch);
		ElkObjectPropertyExpression compositionHead = compositionList.get(0);
		elkInferenceFactory_.getElkClassInclusionExistentialPropertyExpansion(
				premiseBackwardRelationMatch, compositionHead,
				extendedOriginExpression);
		elkInferenceFactory_.getElkClassInclusionHierarchy(
				toElkExpression(extendedConclusionSourceMatch),
				conclusionFactory_.getObjectSomeValuesFrom(
						premiseBackwardRelationMatch, extendedOriginExpression),
				conclusionFactory_.getObjectSomeValuesFrom(compositionHead,
						extendedOriginExpression));
		List<? extends ElkObjectPropertyExpression> forwardChainList = toList(
				premiseFullForwardChainMatch);
		if (premiseForwardChainStartPos == 0) {
			// composing and unfolding the third premise with the fourth premise
			elkInferenceFactory_.getElkClassInclusionExistentialComposition(
					toList(forwardChainExtendedDomains,
							extendedDestinationMatch),
					forwardChainList, compositionList.get(1));
			// composing the unfolded first premise with the result under the
			// fifth premise
			elkInferenceFactory_.getElkClassInclusionExistentialComposition(
					toList(extendedConclusionSourceMatch, extendedOriginMatch,
							extendedDestinationMatch),
					compositionList, conclusionRelationMatch);
		} else {
			// composing the unfolded first premise with the third premise under
			// the fifth premise
			elkInferenceFactory_.getElkClassInclusionExistentialComposition(
					toList(extendedConclusionSourceMatch,
							forwardChainExtendedDomains,
							extendedDestinationMatch),
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
		BackwardLinkMatch2 conclusionMatch2 = inferenceMatch2
				.getConclusionMatch(conclusionFactory_);
		inferences_.add(conclusionMatch2, inferenceMatch2);
		for (BackwardLinkMatch3 child : hierarchy_
				.getChildren(conclusionMatch2)) {
			(new BackwardLinkMatch3InferenceVisitor(inferenceFactory_, child))
					.visit(inferenceMatch2);
		}
		return null;
	}

	@Override
	public Void visit(BackwardLinkOfObjectHasSelfMatch3 inferenceMatch3) {
		inferenceMatch3.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		IndexedContextRootMatch extendedTargetMatch = inferenceMatch3
				.getExtendedTargetMatch();
		BackwardLinkOfObjectHasSelfMatch2 inferenceMatch2 = inferenceMatch3
				.getParent();
		IndexedContextRootMatch extendedOriginMatch = inferenceMatch2
				.getExtendedOriginMatch();
		ElkObjectProperty relationMatch = inferenceMatch2.getRelationMatch();
		ElkClassExpression extendedOriginExpression = toElkExpression(
				extendedOriginMatch);
		ElkClassExpression extendedTargetMatchExpression = toElkExpression(
				extendedTargetMatch);

		deriveInclusion(extendedTargetMatch, extendedOriginMatch);
		elkInferenceFactory_.getElkClassInclusionHierarchy(
				extendedTargetMatchExpression, extendedOriginExpression,
				conclusionFactory_.getObjectHasSelf(relationMatch));
		elkInferenceFactory_.getElkClassInclusionExistentialOfObjectHasSelf(
				extendedTargetMatchExpression, relationMatch);

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
		BackwardLinkMatch2 conclusionMatch2 = inferenceMatch2
				.getConclusionMatch(conclusionFactory_);
		inferences_.add(conclusionMatch2, inferenceMatch2);
		for (BackwardLinkMatch3 child : hierarchy_
				.getChildren(conclusionMatch2)) {
			(new BackwardLinkMatch3InferenceVisitor(inferenceFactory_, child))
					.visit(inferenceMatch2);
		}
		return null;
	}

	@Override
	public Void visit(
			BackwardLinkOfObjectSomeValuesFromMatch3 inferenceMatch3) {
		inferenceMatch3.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		IndexedContextRootMatch extendedDestinationMatch = inferenceMatch3
				.getExtendedDestinationMatch();
		BackwardLinkOfObjectSomeValuesFromMatch2 inferenceMatch2 = inferenceMatch3
				.getParent();
		IndexedObjectSomeValuesFromMatch premiseSuperExpressionMatch = inferenceMatch2
				.getPremiseSuperExpressionMatch();
		IndexedContextRootMatch extendedOriginMatch = inferenceMatch2
				.getExtendedOriginMatch();
		ElkObjectPropertyExpression propertyMatch = premiseSuperExpressionMatch
				.getPropertyMatch();
		ElkClassExpression fillerMatch = getFillerMatch(
				premiseSuperExpressionMatch);

		elkInferenceFactory_.getElkClassInclusionExistentialRange(propertyMatch,
				fillerMatch, extendedDestinationMatch.getRangeMatches());
		elkInferenceFactory_.getElkClassInclusionHierarchy(
				toElkExpression(extendedOriginMatch),
				conclusionFactory_.getObjectSomeValuesFrom(propertyMatch,
						fillerMatch),
				conclusionFactory_.getObjectSomeValuesFrom(propertyMatch,
						toElkExpression(extendedDestinationMatch)));
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
		BackwardLinkMatch2 concluisonMatch2 = inferenceMatch3
				.getConclusionMatch(conclusionFactory_);
		inferences_.add(concluisonMatch2, inferenceMatch3);
		for (BackwardLinkMatch3 child : hierarchy_
				.getChildren(concluisonMatch2)) {
			(new BackwardLinkMatch3InferenceVisitor(inferenceFactory_, child))
					.visit(inferenceMatch3);
		}
		return null;
	}

	@Override
	public Void visit(BackwardLinkReversedExpandedMatch4 inferenceMatch4) {
		ForwardLinkMatch3 premiseMatch3 = inferenceMatch4
				.getFirstPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch3, inferenceMatch4);
		for (ForwardLinkMatch4 child : hierarchy_.getChildren(premiseMatch3)) {
			(new ForwardLinkMatch4InferenceVisitor(inferenceFactory_, child))
					.visit(inferenceMatch4);
		}
		return null;
	}

	@Override
	public Void visit(BackwardLinkReversedExpandedMatch5 inferenceMatch5) {
		inferenceMatch5.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		IndexedContextRootMatchChain extendedDomains = inferenceMatch5
				.getExtendedDomains();
		BackwardLinkReversedExpandedMatch4 inferenceMatch4 = inferenceMatch5
				.getParent();
		IndexedContextRootMatch extendedDestinationMatch = inferenceMatch4
				.getExtendedDestinationMatch();
		BackwardLinkReversedExpandedMatch3 inferenceMatch3 = inferenceMatch4
				.getParent();
		BackwardLinkReversedExpandedMatch2 inferenceMatch2 = inferenceMatch3
				.getParent();
		ElkSubObjectPropertyExpression subChainMatch = inferenceMatch2
				.getSubChainMatch();
		ElkObjectProperty relationMatch = inferenceMatch2.getRelationMatch();
		elkInferenceFactory_.getElkClassInclusionExistentialComposition(
				toList(extendedDomains, extendedDestinationMatch),
				toList(subChainMatch), relationMatch);
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
		DisjointSubsumerMatch1 premiseMatch1 = inferenceMatch2
				.getSecondPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch2);
		for (DisjointSubsumerMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new DisjointSubsumerMatch2InferenceVisitor(inferenceFactory_,
					child)).visit(inferenceMatch2);
		}
		return null;
	}

	@Override
	public Void visit(
			ClassInconsistencyOfDisjointSubsumersMatch3 inferenceMatch3) {
		inferenceMatch3.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		IndexedContextRootMatch extendedExtendedOriginMatch = inferenceMatch3
				.getExtendedExtendedOriginMatch();
		ClassInconsistencyOfDisjointSubsumersMatch2 inferenceMatch2 = inferenceMatch3
				.getParent();
		IndexedContextRootMatch extendedOriginMatch = inferenceMatch2
				.getExtendedOriginMatch();
		List<? extends ElkClassExpression> disjoint = inferenceMatch2
				.getDisjointExpressionsMatch();
		ClassInconsistencyOfDisjointSubsumersMatch1 inferenceMatch1 = inferenceMatch2
				.getParent();
		ClassInconsistencyOfDisjointSubsumers parent = inferenceMatch1
				.getParent();
		int firstPos = parent.getFirstDisjointPosition();
		int secondPos = parent.getSecondDisjointPosition();
		ElkClassExpression firstDisjoint = disjoint.get(firstPos);
		ElkClassExpression secondDisjoint = disjoint.get(secondPos);
		ElkClassExpression subExpression = toElkExpression(extendedOriginMatch);
		ElkClassExpression extendedSubExpression = toElkExpression(
				extendedExtendedOriginMatch);
		deriveInclusion(extendedExtendedOriginMatch, extendedOriginMatch);
		elkInferenceFactory_.getElkClassInclusionHierarchy(
				extendedSubExpression, subExpression, firstDisjoint);
		elkInferenceFactory_
				.getElkClassInclusionObjectIntersectionOfComposition(
						extendedSubExpression, firstDisjoint, secondDisjoint);
		elkInferenceFactory_.getElkClassInclusionOfDisjointClasses(disjoint,
				firstPos, secondPos);
		elkInferenceFactory_.getElkClassInclusionHierarchy(
				extendedSubExpression, conclusionFactory_
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
		SubClassInclusionComposedMatch1 premiseMatch1 = inferenceMatch2
				.getFirstPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch2);
		for (SubClassInclusionComposedMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new SubClassInclusionComposedMatch2InferenceVisitor(
					inferenceFactory_, child)).visit(inferenceMatch2);
		}
		return null;
	}

	@Override
	public Void visit(
			ClassInconsistencyOfObjectComplementOfMatch3 inferenceMatch3) {
		inferenceMatch3.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		IndexedContextRootMatch extendedExtendedOriginMatch = inferenceMatch3
				.getExtendedExtendedOriginMatch();
		ClassInconsistencyOfObjectComplementOfMatch2 inferenceMatch2 = inferenceMatch3
				.getParent();
		IndexedContextRootMatch extendedOriginMatch = inferenceMatch2
				.getExtendedOriginMatch();
		ElkObjectComplementOf negationMatch = inferenceMatch2
				.getNegationMatch();
		ElkClassExpression subExpression = toElkExpression(extendedOriginMatch);
		ElkClassExpression extendedSubExpression = toElkExpression(
				extendedExtendedOriginMatch);
		deriveInclusion(extendedExtendedOriginMatch, extendedOriginMatch);

		elkInferenceFactory_.getElkClassInclusionHierarchy(
				extendedSubExpression, subExpression, negationMatch);
		elkInferenceFactory_
				.getElkClassInclusionObjectIntersectionOfComposition(
						extendedSubExpression,
						negationMatch.getClassExpression(), negationMatch);
		elkInferenceFactory_.getElkClassInclusionNegationClash(
				negationMatch.getClassExpression());
		elkInferenceFactory_.getElkClassInclusionHierarchy(
				extendedSubExpression,
				conclusionFactory_.getObjectIntersectionOf(
						negationMatch.getClassExpression(), negationMatch),
				conclusionFactory_.getOwlNothing());
		return null;
	}

	@Override
	public Void visit(ClassInconsistencyOfOwlNothingMatch1 inferenceMatch1) {
		SubClassInclusionComposedMatch1 premiseMatch1 = inferenceMatch1
				.getPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch1);
		for (SubClassInclusionComposedMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new SubClassInclusionComposedMatch2InferenceVisitor(
					inferenceFactory_, child)).visit(inferenceMatch1);
		}
		return null;
	}

	@Override
	public Void visit(ClassInconsistencyOfOwlNothingMatch2 inferenceMatch2) {
		inferenceMatch2.getConclusionMatch(conclusionFactory_);

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
		ClassInconsistencyMatch1 premiseMatch1 = inferenceMatch2
				.getSecondPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch2);
		for (ClassInconsistencyMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new ClassInconsistencyMatch2InferenceVisitor(inferenceFactory_,
					child)).visit(inferenceMatch2);
		}
		return null;
	}

	@Override
	public Void visit(ClassInconsistencyPropagatedMatch3 inferenceMatch3) {
		BackwardLinkMatch3 premiseMatch3 = inferenceMatch3
				.getFirstPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch3, inferenceMatch3);
		for (BackwardLinkMatch4 child : hierarchy_.getChildren(premiseMatch3)) {
			(new BackwardLinkMatch4InferenceVisitor(inferenceFactory_, child))
					.visit(inferenceMatch3);
		}
		return null;
	}

	@Override
	public Void visit(ClassInconsistencyPropagatedMatch4 inferenceMatch4) {
		inferenceMatch4.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		IndexedContextRootMatch extendedDestinationMatch = inferenceMatch4
				.getExtendedDestinationMatch();
		ClassInconsistencyPropagatedMatch3 inferenceMatch3 = inferenceMatch4
				.getParent();
		IndexedContextRootMatch extendedOriginMatch = inferenceMatch3
				.getExtendedOriginMatch();
		ClassInconsistencyPropagatedMatch2 inferenceMatch2 = inferenceMatch3
				.getParent();
		ElkObjectProperty premiseRelationMatch = inferenceMatch2
				.getPremiseRelationMatch();

		elkInferenceFactory_.getElkClassInclusionExistentialFillerExpansion(
				premiseRelationMatch, toElkExpression(extendedOriginMatch),
				conclusionFactory_.getOwlNothing());
		elkInferenceFactory_.getElkClassInclusionExistentialOwlNothing(
				premiseRelationMatch);
		elkInferenceFactory_.getElkClassInclusionHierarchy(
				toElkExpression(extendedDestinationMatch),
				conclusionFactory_.getObjectSomeValuesFrom(premiseRelationMatch,
						toElkExpression(extendedOriginMatch)),
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
		SubClassInclusionComposedMatch1 premiseMatch1 = inferenceMatch2
				.getFirstPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch2);
		for (SubClassInclusionComposedMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new SubClassInclusionComposedMatch2InferenceVisitor(
					inferenceFactory_, child)).visit(inferenceMatch2);
		}
		return null;
	}

	@Override
	public Void visit(DisjointSubsumerFromSubsumerMatch3 inferenceMatch3) {
		inferenceMatch3.getConclusionMatch(conclusionFactory_);

		// no ELK inferences
		return null;
	}

	@Override
	public Void visit(ElkClassAssertionAxiomConversionMatch1 inferenceMatch1) {
		inferenceMatch1.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		ElkClassAssertionAxiomConversion parent = inferenceMatch1.getParent();
		ElkClassAssertionAxiom originalAxiom = parent.getOriginalAxiom();
		elkInferenceFactory_.getElkToldAxiom(originalAxiom);
		elkInferenceFactory_.getElkClassInclusionOfClassAssertion(
				originalAxiom.getIndividual(),
				originalAxiom.getClassExpression());
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
		elkInferenceFactory_.getElkToldAxiom(originalAxiom);
		elkInferenceFactory_
				.getElkDisjointClassesOfDifferentIndividuals(different);
		elkInferenceFactory_.getElkClassInclusionOfDisjointClasses(disjoint,
				parent.getFirstIndividualPosition(),
				parent.getSecondIndividualPosition());
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
		elkInferenceFactory_.getElkToldAxiom(originalAxiom);
		elkInferenceFactory_
				.getElkDisjointClassesOfDifferentIndividuals(different);
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
		elkInferenceFactory_.getElkToldAxiom(originalAxiom);
		elkInferenceFactory_.getElkClassInclusionOfDisjointClasses(
				originalAxiom.getClassExpressions(),
				parent.getFirstClassPosition(),
				parent.getSecondClassPosition());
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
		elkInferenceFactory_.getElkToldAxiom(originalAxiom);
		elkInferenceFactory_.getElkDisjointClassesOfDisjointUnion(
				originalAxiom.getDefinedClass(),
				originalAxiom.getClassExpressions());
		elkInferenceFactory_.getElkClassInclusionOfDisjointClasses(
				originalAxiom.getClassExpressions(),
				parent.getFirstDisjunctPosition(),
				parent.getSecondDisjunctPosition());
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
		elkInferenceFactory_.getElkToldAxiom(originalAxiom);
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
		elkInferenceFactory_.getElkToldAxiom(originalAxiom);
		elkInferenceFactory_.getElkDisjointClassesOfDisjointUnion(
				originalAxiom.getDefinedClass(),
				originalAxiom.getClassExpressions());
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
		elkInferenceFactory_.getElkToldAxiom(originalAxiom);
		elkInferenceFactory_.getElkEquivalentClassesOfDisjointUnion(defined,
				disjoint);
		elkInferenceFactory_.getElkClassInclusionOfEquivaletClasses(defined,
				conclusionFactory_.getObjectUnionOf(disjoint), true);
		elkInferenceFactory_.getElkClassInclusionEmptyObjectUnionOfOwlNothing();
		elkInferenceFactory_.getElkClassInclusionHierarchy(defined,
				conclusionFactory_.getObjectUnionOf(disjoint),
				conclusionFactory_.getOwlNothing());
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
		elkInferenceFactory_.getElkToldAxiom(originalAxiom);
		elkInferenceFactory_.getElkEquivalentClassesOfDisjointUnion(defined,
				disjoint);
		elkInferenceFactory_.getElkClassInclusionOfEquivaletClasses(defined,
				union, false);
		elkInferenceFactory_.getElkClassInclusionObjectUnionOfComposition(
				disjoint, disjunctPos);
		elkInferenceFactory_.getElkClassInclusionHierarchy(
				disjoint.get(disjunctPos), union, defined);
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
		elkInferenceFactory_.getElkToldAxiom(originalAxiom);
		// we do not know which direction is going to be used
		elkInferenceFactory_.getElkClassInclusionOfEquivaletClasses(
				originalAxiom.getClassExpressions(),
				parent.getFirstMemberPosition(),
				parent.getSecondMemberPosition());
		elkInferenceFactory_.getElkClassInclusionOfEquivaletClasses(
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
		elkInferenceFactory_.getElkToldAxiom(originalAxiom);
		elkInferenceFactory_.getElkClassInclusionOfEquivaletClasses(
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
		elkInferenceFactory_.getElkToldAxiom(originalAxiom);
		elkInferenceFactory_.getElkPropertyInclusionOfEquivalence(
				originalAxiom.getObjectPropertyExpressions(),
				parent.getSubPropertyPosition(),
				parent.getSuperPropertyPosition());
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
		elkInferenceFactory_.getElkToldAxiom(originalAxiom);
		elkInferenceFactory_.getElkClassInclusionOfObjectPropertyAssertion(
				originalAxiom.getSubject(), originalAxiom.getProperty(),
				originalAxiom.getObject());
		return null;
	}

	@Override
	public Void visit(
			ElkObjectPropertyDomainAxiomConversionMatch1 inferenceMatch1) {
		inferenceMatch1.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		ElkObjectPropertyDomainAxiom originalAxiom = inferenceMatch1.getParent()
				.getOriginalAxiom();
		elkInferenceFactory_.getElkToldAxiom(originalAxiom);
		elkInferenceFactory_.getElkClassInclusionOfObjectPropertyDomain(
				originalAxiom.getProperty(), originalAxiom.getDomain());
		return null;
	}

	@Override
	public Void visit(
			ElkObjectPropertyRangeAxiomConversionMatch1 inferenceMatch1) {
		inferenceMatch1.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		ElkObjectPropertyRangeAxiom originalAxiom = inferenceMatch1.getParent()
				.getOriginalAxiom();
		elkInferenceFactory_.getElkToldAxiom(originalAxiom);
		return null;
	}

	@Override
	public Void visit(
			ElkReflexiveObjectPropertyAxiomConversionMatch1 inferenceMatch1) {
		inferenceMatch1.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		ElkReflexiveObjectPropertyAxiom originalAxiom = inferenceMatch1
				.getParent().getOriginalAxiom();
		elkInferenceFactory_.getElkToldAxiom(originalAxiom);
		elkInferenceFactory_.getElkClassInclusionOfReflexiveObjectProperty(
				originalAxiom.getProperty());
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
		elkInferenceFactory_.getElkToldAxiom(originalAxiom);
		elkInferenceFactory_.getElkEquivalentClassesOfSameIndividual(same);
		elkInferenceFactory_.getElkClassInclusionOfEquivaletClasses(equivalent,
				parent.getSubIndividualPosition(),
				parent.getSuperIndividualPosition());
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
		elkInferenceFactory_.getElkToldAxiom(originalAxiom);
		elkInferenceFactory_.getElkPropertyInclusionOfTransitiveObjectProperty(
				originalAxiom.getProperty());
		return null;
	}

	@Override
	public Void visit(ForwardLinkCompositionMatch1 inferenceMatch1) {
		BackwardLinkMatch1 conclusionMatch1 = inferenceMatch1
				.getFirstPremiseMatch(conclusionFactory_);
		inferences_.add(conclusionMatch1, inferenceMatch1);
		for (BackwardLinkMatch2 child : hierarchy_
				.getChildren(conclusionMatch1)) {
			(new BackwardLinkMatch2InferenceVisitor(inferenceFactory_, child))
					.visit(inferenceMatch1);
		}
		return null;
	}

	@Override
	public Void visit(ForwardLinkCompositionMatch2 inferenceMatch2) {
		inferenceMatch2.getSecondPremiseMatch(conclusionFactory_);
		SubPropertyChainMatch1 premiseMatch1 = inferenceMatch2
				.getFourthPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch2);
		for (SubPropertyChainMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new SubPropertyChainMatch2InferenceVisitor(inferenceFactory_,
					child)).visit(inferenceMatch2);
		}
		return null;
	}

	@Override
	public Void visit(ForwardLinkCompositionMatch3 inferenceMatch3) {
		ForwardLinkMatch1 premiseMatch2 = inferenceMatch3
				.getThirdPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch2, inferenceMatch3);
		for (ForwardLinkMatch2 child : hierarchy_.getChildren(premiseMatch2)) {
			(new ForwardLinkMatch2InferenceVisitor(inferenceFactory_, child))
					.visit(inferenceMatch3);
		}
		return null;
	}

	@Override
	public Void visit(ForwardLinkCompositionMatch4 inferenceMatch4) {
		ForwardLinkMatch2 conclusionMatch2 = inferenceMatch4
				.getConclusionMatch(conclusionFactory_);
		inferences_.add(conclusionMatch2, inferenceMatch4);
		for (ForwardLinkMatch3 child : hierarchy_
				.getChildren(conclusionMatch2)) {
			(new ForwardLinkMatch3InferenceVisitor(inferenceFactory_, child))
					.visit(inferenceMatch4);
		}
		return null;
	}

	@Override
	public Void visit(ForwardLinkCompositionMatch5 inferenceMatch5) {
		ForwardLinkMatch3 conclusionMatch2 = inferenceMatch5
				.getThirdPremiseMatch(conclusionFactory_);
		inferences_.add(conclusionMatch2, inferenceMatch5);
		for (ForwardLinkMatch4 child : hierarchy_
				.getChildren(conclusionMatch2)) {
			(new ForwardLinkMatch4InferenceVisitor(inferenceFactory_, child))
					.visit(inferenceMatch5);
		}
		return null;
	}

	@Override
	public Void visit(ForwardLinkCompositionMatch6 inferenceMatch6) {
		BackwardLinkMatch3 conclusionMatch2 = inferenceMatch6
				.getFirstPremiseMatch(conclusionFactory_);
		inferences_.add(conclusionMatch2, inferenceMatch6);
		for (BackwardLinkMatch4 child : hierarchy_
				.getChildren(conclusionMatch2)) {
			(new BackwardLinkMatch4InferenceVisitor(inferenceFactory_, child))
					.visit(inferenceMatch6);
		}
		return null;
	}

	@Override
	public Void visit(ForwardLinkCompositionMatch7 inferenceMatch7) {
		inferenceMatch7.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		IndexedContextRootMatch extendedDestinationMatch = inferenceMatch7
				.getExtendedDestinationMatch();
		ForwardLinkCompositionMatch6 inferenceMatch6 = inferenceMatch7
				.getParent();
		IndexedContextRootMatchChain forwardChainExtendedDomains = inferenceMatch6
				.getForwardChainExtendedDomains();
		ForwardLinkCompositionMatch5 inferenceMatch5 = inferenceMatch6
				.getParent();
		IndexedContextRootMatch conclusionExtendedTargetMatch = inferenceMatch5
				.getConclusionExtendedTargetMatch();
		ForwardLinkCompositionMatch4 inferenceMatch4 = inferenceMatch5
				.getParent();
		ForwardLinkCompositionMatch3 inferenceMatch3 = inferenceMatch4
				.getParent();
		ElkSubObjectPropertyExpression premiseFullForwardChainMatch = inferenceMatch3
				.getPremiseFullForwardChainMatch();
		int premiseForwardChainStartPos = inferenceMatch3
				.getPremiseForwardChainStartPos();
		ForwardLinkCompositionMatch2 inferenceMatch2 = inferenceMatch3
				.getParent();
		ElkObjectProperty premiseBackwardRelationMatch = inferenceMatch2
				.getPremiseBackwardRelationMatch();
		ForwardLinkCompositionMatch1 inferenceMatch1 = inferenceMatch2
				.getParent();
		ElkSubObjectPropertyExpression fullCompositionMatch = inferenceMatch1
				.getFullCompositionMatch();
		int compositionStartPos = inferenceMatch1.getCompositionStartPos();

		// unfolding the first premise under the second premise
		List<? extends ElkObjectPropertyExpression> fullCompositionList = toList(
				fullCompositionMatch);
		ElkObjectPropertyExpression compositionHead = fullCompositionList
				.get(compositionStartPos);
		IndexedContextRootMatch extendedOriginMatch = forwardChainExtendedDomains
				.getHead();
		ElkClassExpression extendedOriginExpression = toElkExpression(
				extendedOriginMatch);
		elkInferenceFactory_.getElkClassInclusionExistentialPropertyExpansion(
				premiseBackwardRelationMatch, compositionHead,
				extendedOriginExpression);
		elkInferenceFactory_.getElkClassInclusionHierarchy(
				toElkExpression(extendedDestinationMatch),
				conclusionFactory_.getObjectSomeValuesFrom(
						premiseBackwardRelationMatch, extendedOriginExpression),
				conclusionFactory_.getObjectSomeValuesFrom(compositionHead,
						extendedOriginExpression));
		List<? extends ElkObjectPropertyExpression> forwardChainList = toList(
				premiseFullForwardChainMatch);
		if (premiseForwardChainStartPos == 0) {
			// composing and unfolding the third premise with the fourth premise
			elkInferenceFactory_.getElkClassInclusionExistentialComposition(
					toList(forwardChainExtendedDomains,
							conclusionExtendedTargetMatch),
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
		ForwardLinkMatch2 conclusionMatch2 = inferenceMatch2
				.getConclusionMatch(conclusionFactory_);
		inferences_.add(conclusionMatch2, inferenceMatch2);
		for (ForwardLinkMatch3 child : hierarchy_
				.getChildren(conclusionMatch2)) {
			(new ForwardLinkMatch3InferenceVisitor(inferenceFactory_, child))
					.visit(inferenceMatch2);
		}
		return null;
	}

	@Override
	public Void visit(ForwardLinkOfObjectHasSelfMatch3 inferenceMatch3) {
		inferenceMatch3.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		IndexedContextRootMatch extendedTargetMatch = inferenceMatch3
				.getExtendedTargetMatch();
		ForwardLinkOfObjectHasSelfMatch2 inferenceMatch2 = inferenceMatch3
				.getParent();
		IndexedContextRootMatch extendedOriginMatch = inferenceMatch2
				.getExtendedOriginMatch();
		ForwardLinkOfObjectHasSelfMatch1 inferenceMatch1 = inferenceMatch2
				.getParent();
		ElkObjectProperty forwardRelationMatch = inferenceMatch1
				.getForwardRelationMatch();

		ElkClassExpression extendedTargetMatchExpression = toElkExpression(
				extendedTargetMatch);
		ElkClassExpression extendedOriginExpression = toElkExpression(
				extendedOriginMatch);
		deriveInclusion(extendedTargetMatch, extendedOriginMatch);
		elkInferenceFactory_.getElkClassInclusionHierarchy(
				extendedTargetMatchExpression, extendedOriginExpression,
				conclusionFactory_.getObjectHasSelf(forwardRelationMatch));
		elkInferenceFactory_.getElkClassInclusionExistentialOfObjectHasSelf(
				extendedTargetMatchExpression, forwardRelationMatch);
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
		ForwardLinkMatch2 conclusionMatch2 = inferenceMatch2
				.getConclusionMatch(conclusionFactory_);
		inferences_.add(conclusionMatch2, inferenceMatch2);
		for (ForwardLinkMatch3 child : hierarchy_
				.getChildren(conclusionMatch2)) {
			(new ForwardLinkMatch3InferenceVisitor(inferenceFactory_, child))
					.visit(inferenceMatch2);
		}
		return null;
	}

	@Override
	public Void visit(ForwardLinkOfObjectSomeValuesFromMatch3 inferenceMatch3) {
		inferenceMatch3.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		IndexedContextRootMatch extendedTargetMatch = inferenceMatch3
				.getExtendedTargetMatch();
		ForwardLinkOfObjectSomeValuesFromMatch2 inferenceMatch2 = inferenceMatch3
				.getParent();
		IndexedObjectSomeValuesFromMatch premiseSuperExpressionMatch = inferenceMatch2
				.getPremiseSuperExpressionMatch();
		IndexedContextRootMatch extendedOriginMatch = inferenceMatch2
				.getExtendedOriginMatch();
		ElkObjectPropertyExpression propertyMatch = premiseSuperExpressionMatch
				.getPropertyMatch();
		ElkClassExpression fillerMatch = getFillerMatch(
				premiseSuperExpressionMatch);

		elkInferenceFactory_.getElkClassInclusionExistentialRange(propertyMatch,
				fillerMatch, extendedTargetMatch.getRangeMatches());
		elkInferenceFactory_.getElkClassInclusionHierarchy(
				toElkExpression(extendedOriginMatch),
				conclusionFactory_.getObjectSomeValuesFrom(propertyMatch,
						fillerMatch),
				conclusionFactory_.getObjectSomeValuesFrom(propertyMatch,
						toElkExpression(extendedTargetMatch)));
		return null;
	}

	@Override
	public Void visit(PropagationGeneratedMatch1 inferenceMatch1) {
		SubClassInclusionComposedMatch1 premiseMatch1 = inferenceMatch1
				.getSecondPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch1);
		for (SubClassInclusionComposedMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new SubClassInclusionComposedMatch2InferenceVisitor(
					inferenceFactory_, child)).visit(inferenceMatch1);
		}
		return null;
	}

	@Override
	public Void visit(PropagationGeneratedMatch2 inferenceMatch2) {
		SubPropertyChainMatch1 premiseMatch1 = inferenceMatch2
				.getThirdPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch2);
		for (SubPropertyChainMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new SubPropertyChainMatch2InferenceVisitor(inferenceFactory_,
					child)).visit(inferenceMatch2);
		}
		return null;
	}

	@Override
	public Void visit(PropagationGeneratedMatch3 inferenceMatch3) {
		inferenceMatch3.getConclusionMatch(conclusionFactory_);

		// no ELK inferences
		PropagationGeneratedMatch2 inferenceMatch2 = inferenceMatch3
				.getParent();
		IndexedContextRootMatch extendedDestinationMatch = inferenceMatch2
				.getExtendedDestinationMatch();
		PropagationGeneratedMatch1 inferenceMatch1 = inferenceMatch2
				.getParent();
		IndexedObjectSomeValuesFromMatch conclusionCarryMatch = inferenceMatch1
				.getConclusionCarryMatch();
		ElkObjectProperty subDestinationMatch = inferenceMatch1
				.getSubDestinationMatch();

		ElkClassExpression extendedDestinationExpression = toElkExpression(
				extendedDestinationMatch);
		ElkClassExpression conclusionFillerMatch = getFillerMatch(
				conclusionCarryMatch);
		ElkObjectPropertyExpression conclusionPropertyMatch = conclusionCarryMatch
				.getPropertyMatch();

		elkInferenceFactory_.getElkClassInclusionExistentialPropertyExpansion(
				subDestinationMatch, conclusionPropertyMatch,
				extendedDestinationExpression);
		elkInferenceFactory_.getElkClassInclusionExistentialFillerExpansion(
				conclusionPropertyMatch, extendedDestinationExpression,
				conclusionFillerMatch);
		elkInferenceFactory_.getElkClassInclusionHierarchy(
				conclusionFactory_.getObjectSomeValuesFrom(subDestinationMatch,
						extendedDestinationExpression),
				conclusionFactory_.getObjectSomeValuesFrom(
						conclusionPropertyMatch, extendedDestinationExpression),
				conclusionFactory_.getObjectSomeValuesFrom(
						conclusionPropertyMatch, conclusionFillerMatch));
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
		ElkObjectProperty subPropertyMatch = inferenceMatch3
				.getSubPropertyMatch();
		PropertyRangeInheritedMatch2 inferenceMatch2 = inferenceMatch3
				.getParent();
		ElkObjectProperty superPropertyMatch = inferenceMatch2
				.getSuperPropertyMatch();
		ElkClassExpression rangeMatch = inferenceMatch2.getRangeMatch();

		elkInferenceFactory_.getElkPropertyRangePropertyUnfolding(
				subPropertyMatch, superPropertyMatch, rangeMatch);
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
		SubClassInclusionComposedMatch1 premiseMatch1 = inferenceMatch2
				.getFirstPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch2);
		for (SubClassInclusionComposedMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new SubClassInclusionComposedMatch2InferenceVisitor(
					inferenceFactory_, child)).visit(inferenceMatch2);
		}
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionComposedDefinedClassMatch3 inferenceMatch3) {
		inferenceMatch3.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		IndexedContextRootMatch extendedOriginMatch = inferenceMatch3
				.getExtendedOriginMatch();
		SubClassInclusionComposedDefinedClassMatch2 inferenceMatch2 = inferenceMatch3
				.getParent();
		ElkClassExpression definitionMatch = inferenceMatch2
				.getDefinitionMatch();
		SubClassInclusionComposedDefinedClassMatch1 inferenceMatch1 = inferenceMatch2
				.getParent();
		ElkClass definedClassMatch = inferenceMatch1.getDefinedClassMatch();

		elkInferenceFactory_.getElkClassInclusionHierarchy(
				toElkExpression(extendedOriginMatch), definitionMatch,
				definedClassMatch);

		return null;

	}

	@Override
	public Void visit(
			SubClassInclusionComposedEmptyObjectIntersectionOfMatch1 inferenceMatch1) {
		SubClassInclusionComposedMatch1 premiseMatch1 = inferenceMatch1
				.getPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch1);
		for (SubClassInclusionComposedMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new SubClassInclusionComposedMatch2InferenceVisitor(
					inferenceFactory_, child)).visit(inferenceMatch1);
		}
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionComposedEmptyObjectIntersectionOfMatch2 inferenceMatch2) {
		inferenceMatch2.getConclusionMatch(conclusionFactory_);

		// create ELK inferences
		IndexedContextRootMatch extendedDestinationMatch = inferenceMatch2
				.getExtendedDestinationMatch();
		ElkClassExpression subExpression = toElkExpression(
				extendedDestinationMatch);
		elkInferenceFactory_.getElkClassInclusionOwlThing(subExpression);
		elkInferenceFactory_
				.getElkClassInclusionOwlThingEmptyObjectIntersectionOf();
		elkInferenceFactory_.getElkClassInclusionHierarchy(subExpression,
				conclusionFactory_.getOwlThing(),
				conclusionFactory_.getObjectIntersectionOf(
						Collections.<ElkClassExpression> emptyList()));
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionComposedEmptyObjectOneOfMatch1 inferenceMatch1) {
		SubClassInclusionComposedMatch1 premiseMatch1 = inferenceMatch1
				.getPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch1);
		for (SubClassInclusionComposedMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new SubClassInclusionComposedMatch2InferenceVisitor(
					inferenceFactory_, child)).visit(inferenceMatch1);
		}
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionComposedEmptyObjectOneOfMatch2 inferenceMatch2) {
		inferenceMatch2.getConclusionMatch(conclusionFactory_);

		// create ELK inferences
		IndexedContextRootMatch extendedDestinationMatch = inferenceMatch2
				.getExtendedDestinationMatch();
		ElkClassExpression subExpression = toElkExpression(
				extendedDestinationMatch);
		ElkObjectOneOf emptyOneOf = conclusionFactory_
				.getObjectOneOf(Collections.<ElkIndividual> emptyList());
		elkInferenceFactory_.getElkClassInclusionOwlNothing(emptyOneOf);
		elkInferenceFactory_.getElkClassInclusionHierarchy(subExpression,
				conclusionFactory_.getOwlNothing(), emptyOneOf);
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionComposedEmptyObjectUnionOfMatch1 inferenceMatch1) {
		SubClassInclusionComposedMatch1 premiseMatch1 = inferenceMatch1
				.getPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch1);
		for (SubClassInclusionComposedMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new SubClassInclusionComposedMatch2InferenceVisitor(
					inferenceFactory_, child)).visit(inferenceMatch1);
		}
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionComposedEmptyObjectUnionOfMatch2 inferenceMatch2) {
		inferenceMatch2.getConclusionMatch(conclusionFactory_);

		// create ELK inferences
		IndexedContextRootMatch extendedDestinationMatch = inferenceMatch2
				.getExtendedDestinationMatch();
		ElkClassExpression subExpression = toElkExpression(
				extendedDestinationMatch);
		ElkObjectUnionOf emptyUnionOf = conclusionFactory_
				.getObjectUnionOf(Collections.<ElkClassExpression> emptyList());
		elkInferenceFactory_.getElkClassInclusionOwlNothing(emptyUnionOf);
		elkInferenceFactory_.getElkClassInclusionHierarchy(subExpression,
				conclusionFactory_.getOwlNothing(), emptyUnionOf);
		return null;
	}

	@Override
	public Void visit(SubClassInclusionComposedEntityMatch1 inferenceMatch1) {
		SubClassInclusionDecomposedMatch1 premiseMatch2 = inferenceMatch1
				.getPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch2, inferenceMatch1);
		for (SubClassInclusionDecomposedMatch2 child : hierarchy_
				.getChildren(premiseMatch2)) {
			(new SubClassInclusionDecomposedMatch2InferenceVisitor(
					inferenceFactory_, child)).visit(inferenceMatch1);
		}
		return null;
	}

	@Override
	public Void visit(SubClassInclusionComposedEntityMatch2 inferenceMatch2) {
		inferenceMatch2.getConclusionMatch(conclusionFactory_);

		// no ELK inferences
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionComposedObjectHasValueMatch1 inferenceMatch1) {
		SubClassInclusionComposedMatch1 premiseMatch1 = inferenceMatch1
				.getPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch1);
		for (SubClassInclusionComposedMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new SubClassInclusionComposedMatch2InferenceVisitor(
					inferenceFactory_, child)).visit(inferenceMatch1);
		}
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionComposedObjectHasValueMatch2 inferenceMatch2) {
		inferenceMatch2.getConclusionMatch(conclusionFactory_);

		// create ELK inferences
		IndexedContextRootMatch extendedDestinationMatch = inferenceMatch2
				.getExtendedDestinationMatch();
		SubClassInclusionComposedObjectHasValueMatch1 inferenceMatch1 = inferenceMatch2
				.getParent();
		ElkObjectHasValue conclusionSubsumer = inferenceMatch1
				.getConclusionSubsumerMatch();
		ElkObjectPropertyExpression property = conclusionSubsumer.getProperty();
		ElkIndividual value = conclusionSubsumer.getFiller();
		ElkObjectSomeValuesFrom premiseSubsumer = conclusionFactory_
				.getObjectSomeValuesFrom(property,
						conclusionFactory_.getObjectOneOf(value));
		elkInferenceFactory_.getElkEquivalentClassesObjectHasValue(property,
				value);
		elkInferenceFactory_.getElkClassInclusionOfEquivaletClasses(
				conclusionSubsumer, premiseSubsumer, false);
		elkInferenceFactory_.getElkClassInclusionHierarchy(
				toElkExpression(extendedDestinationMatch), premiseSubsumer,
				conclusionSubsumer);
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionComposedObjectIntersectionOfMatch1 inferenceMatch1) {
		SubClassInclusionComposedMatch1 premiseMatch1 = inferenceMatch1
				.getSecondPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch1);
		for (SubClassInclusionComposedMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new SubClassInclusionComposedMatch2InferenceVisitor(
					inferenceFactory_, child)).visit(inferenceMatch1);
		}
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionComposedObjectIntersectionOfMatch2 inferenceMatch2) {
		SubClassInclusionComposedMatch1 premiseMatch1 = inferenceMatch2
				.getFirstPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch2);
		for (SubClassInclusionComposedMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new SubClassInclusionComposedMatch2InferenceVisitor(
					inferenceFactory_, child)).visit(inferenceMatch2);
		}
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionComposedObjectIntersectionOfMatch3 inferenceMatch3) {
		inferenceMatch3.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		IndexedContextRootMatch extendedExtendedOriginMatch = inferenceMatch3
				.getExtendedExtendedOriginMatch();
		SubClassInclusionComposedObjectIntersectionOfMatch2 inferenceMatch2 = inferenceMatch3
				.getParent();
		IndexedContextRootMatch extendedOriginMatch = inferenceMatch2
				.getExtendedOriginMatch();
		SubClassInclusionComposedObjectIntersectionOfMatch1 inferenceMatch1 = inferenceMatch2
				.getParent();
		int prefixLength = inferenceMatch1.getConclusionSubsumerPrefixLength();
		ElkObjectIntersectionOf fullSubsumerMatch = inferenceMatch1
				.getFullSubsumerMatch();
		List<? extends ElkClassExpression> conjuncts = fullSubsumerMatch
				.getClassExpressions();

		deriveInclusion(extendedExtendedOriginMatch, extendedOriginMatch);
		elkInferenceFactory_.getElkClassInclusionHierarchy(
				toElkExpression(extendedExtendedOriginMatch),
				toElkExpression(extendedOriginMatch),
				conjuncts.get(prefixLength - 1));

		if (prefixLength < conjuncts.size()) {
			// the conjunction is not fully composed yet
			return null;
		}
		// else
		elkInferenceFactory_
				.getElkClassInclusionObjectIntersectionOfComposition(
						toElkExpression(extendedExtendedOriginMatch),
						conjuncts);

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
		PropagationMatch1 premiseMatch1 = inferenceMatch2
				.getSecondPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch2);
		for (PropagationMatch2 child : hierarchy_.getChildren(premiseMatch1)) {
			(new PropagationMatch2InferenceVisitor(inferenceFactory_, child))
					.visit(inferenceMatch2);
		}
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionComposedObjectSomeValuesFromMatch3 inferenceMatch3) {
		BackwardLinkMatch3 premiseMatch1 = inferenceMatch3
				.getFirstPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch3);
		for (BackwardLinkMatch4 child : hierarchy_.getChildren(premiseMatch1)) {
			(new BackwardLinkMatch4InferenceVisitor(inferenceFactory_, child))
					.visit(inferenceMatch3);
		}
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionComposedObjectSomeValuesFromMatch4 inferenceMatch4) {
		inferenceMatch4.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		IndexedContextRootMatch extendedDestinationMatch = inferenceMatch4
				.getExtendedDestinationMatch();
		SubClassInclusionComposedObjectSomeValuesFromMatch3 inferenceMatch3 = inferenceMatch4
				.getParent();
		IndexedContextRootMatch extendedOriginMatch = inferenceMatch3
				.getExtendedOriginMatch();
		SubClassInclusionComposedObjectSomeValuesFromMatch2 inferenceMatch2 = inferenceMatch3
				.getParent();
		ElkObjectProperty propagationRelationMatch = inferenceMatch2
				.getPropagationRelationMatch();
		SubClassInclusionComposedObjectSomeValuesFromMatch1 inferenceMatch1 = inferenceMatch2
				.getParent();
		IndexedObjectSomeValuesFromMatch conclusionSubsumerMatch = inferenceMatch1
				.getConclusionSubsumerMatch();

		ElkClassExpression fillerMatch = getFillerMatch(
				conclusionSubsumerMatch);

		ElkClassExpression extendedOriginExpression = toElkExpression(
				extendedOriginMatch);
		ElkClassExpression extendedDestinationExpression = toElkExpression(
				extendedDestinationMatch);
		ElkObjectPropertyExpression conclusionPropertyMatch = conclusionSubsumerMatch
				.getPropertyMatch();
		elkInferenceFactory_.getElkClassInclusionHierarchy(
				extendedDestinationExpression,
				conclusionFactory_.getObjectSomeValuesFrom(
						propagationRelationMatch, extendedOriginExpression),
				conclusionFactory_.getObjectSomeValuesFrom(
						conclusionPropertyMatch, fillerMatch));
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionComposedObjectUnionOfMatch1 inferenceMatch1) {
		SubClassInclusionComposedMatch1 premiseMatch1 = inferenceMatch1
				.getPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch1);
		for (SubClassInclusionComposedMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new SubClassInclusionComposedMatch2InferenceVisitor(
					inferenceFactory_, child)).visit(inferenceMatch1);
		}
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionComposedObjectUnionOfMatch2 inferenceMatch2) {
		inferenceMatch2.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		IndexedContextRootMatch extendedOriginMatch = inferenceMatch2
				.getExtendedOriginMatch();
		SubClassInclusionComposedObjectUnionOfMatch1 inferenceMatch1 = inferenceMatch2
				.getParent();
		IndexedObjectUnionOfMatch disjunctionMatch = inferenceMatch1
				.getConclusionSubsumerMatch();
		final int pos = inferenceMatch1.getPosition();
		final ElkClassExpression subExpression = toElkExpression(
				extendedOriginMatch);
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
			SubClassInclusionComposedSingletonObjectIntersectionOfMatch1 inferenceMatch1) {
		SubClassInclusionComposedMatch1 premiseMatch1 = inferenceMatch1
				.getPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch1);
		for (SubClassInclusionComposedMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new SubClassInclusionComposedMatch2InferenceVisitor(
					inferenceFactory_, child)).visit(inferenceMatch1);
		}
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionComposedSingletonObjectIntersectionOfMatch2 inferenceMatch2) {
		inferenceMatch2.getConclusionMatch(conclusionFactory_);

		// create ELK inference
		IndexedContextRootMatch extendedDestinationMatch = inferenceMatch2
				.getExtendedDestinationMatch();
		SubClassInclusionComposedSingletonObjectIntersectionOfMatch1 inferenceMatch1 = inferenceMatch2
				.getParent();
		ElkClassExpression conjunctMatch = inferenceMatch1.getConjunctMatch();
		elkInferenceFactory_
				.getElkClassInclusionObjectIntersectionOfComposition(
						toElkExpression(extendedDestinationMatch),
						Collections.singletonList(conjunctMatch));
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionComposedSingletonObjectOneOfMatch1 inferenceMatch1) {
		SubClassInclusionComposedMatch1 premiseMatch1 = inferenceMatch1
				.getPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch1);
		for (SubClassInclusionComposedMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new SubClassInclusionComposedMatch2InferenceVisitor(
					inferenceFactory_, child)).visit(inferenceMatch1);
		}
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionComposedSingletonObjectOneOfMatch2 inferenceMatch2) {
		inferenceMatch2.getConclusionMatch(conclusionFactory_);

		// no ELK inferences since indexed individuals are
		// converted to ObjectOneOf
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionComposedSingletonObjectUnionOfMatch1 inferenceMatch1) {
		SubClassInclusionComposedMatch1 premiseMatch1 = inferenceMatch1
				.getPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch1);
		for (SubClassInclusionComposedMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new SubClassInclusionComposedMatch2InferenceVisitor(
					inferenceFactory_, child)).visit(inferenceMatch1);
		}
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionComposedSingletonObjectUnionOfMatch2 inferenceMatch2) {
		inferenceMatch2.getConclusionMatch(conclusionFactory_);

		// create ELK inferences
		IndexedContextRootMatch extendedDestinationMatch = inferenceMatch2
				.getExtendedDestinationMatch();
		SubClassInclusionComposedSingletonObjectUnionOfMatch1 inferenceMatch1 = inferenceMatch2
				.getParent();
		ElkClassExpression disjunctMatch = inferenceMatch1.getDisjunctMatch();
		List<ElkClassExpression> disjuncts = Collections
				.singletonList(disjunctMatch);
		elkInferenceFactory_
				.getElkClassInclusionObjectUnionOfComposition(disjuncts, 0);
		elkInferenceFactory_.getElkClassInclusionHierarchy(
				toElkExpression(extendedDestinationMatch), disjunctMatch,
				conclusionFactory_.getObjectUnionOf(disjuncts));
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionDecomposedEmptyObjectIntersectionOfMatch1 inferenceMatch1) {
		inferenceMatch1.getConclusionMatch(conclusionFactory_);

		// create ELK inference
		IndexedContextRootMatch extendedDestinationMatch = inferenceMatch1
				.getExtendedDestinationMatch();
		elkInferenceFactory_.getElkClassInclusionOwlThing(
				toElkExpression(extendedDestinationMatch));
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionDecomposedEmptyObjectOneOfMatch1 inferenceMatch1) {
		inferenceMatch1.getConclusionMatch(conclusionFactory_);

		// create ELK inference
		IndexedContextRootMatch extendedDestinationMatch = inferenceMatch1
				.getExtendedDestinationMatch();
		elkInferenceFactory_.getElkClassInclusionEmptyObjectOneOfOwlNothing();
		elkInferenceFactory_.getElkClassInclusionHierarchy(
				toElkExpression(extendedDestinationMatch),
				conclusionFactory_.getObjectOneOf(
						Collections.<ElkIndividual> emptyList()),
				conclusionFactory_.getOwlNothing());
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionDecomposedEmptyObjectUnionOfMatch1 inferenceMatch1) {
		inferenceMatch1.getConclusionMatch(conclusionFactory_);

		// create ELK inferences
		IndexedContextRootMatch extendedDestinationMatch = inferenceMatch1
				.getExtendedDestinationMatch();
		elkInferenceFactory_.getElkClassInclusionEmptyObjectUnionOfOwlNothing();
		elkInferenceFactory_.getElkClassInclusionHierarchy(
				toElkExpression(extendedDestinationMatch),
				conclusionFactory_.getObjectUnionOf(
						Collections.<ElkClassExpression> emptyList()),
				conclusionFactory_.getOwlNothing());
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
		IndexedContextRootMatch extendedOriginMatch = inferenceMatch2
				.getExtendedOriginMatch();
		List<? extends ElkClassExpression> conjuncts = fullSubsumerMatch
				.getClassExpressions();
		int pos = 0; // of the decomposed conjunct
		elkInferenceFactory_
				.getElkClassInclusionObjectIntersectionOfDecomposition(
						conjuncts, pos);
		elkInferenceFactory_.getElkClassInclusionHierarchy(
				toElkExpression(extendedOriginMatch),
				conclusionFactory_.getObjectIntersectionOf(conjuncts),
				conjuncts.get(pos));
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionDecomposedObjectHasValueMatch1 inferenceMatch1) {
		inferenceMatch1.getConclusionMatch(conclusionFactory_);

		// create ELK inferences
		IndexedContextRootMatch extendedDestinationMatch = inferenceMatch1
				.getExtendedDestinationMatch();
		ElkObjectHasValue premiseSubsumer = inferenceMatch1
				.getPremiseSubsumerMatch();
		ElkObjectPropertyExpression property = premiseSubsumer.getProperty();
		ElkIndividual value = premiseSubsumer.getFiller();
		ElkObjectSomeValuesFrom conclusionSubsumer = conclusionFactory_
				.getObjectSomeValuesFrom(property,
						conclusionFactory_.getObjectOneOf(value));
		elkInferenceFactory_.getElkEquivalentClassesObjectHasValue(property,
				value);
		elkInferenceFactory_.getElkClassInclusionOfEquivaletClasses(
				premiseSubsumer, conclusionSubsumer, true);
		elkInferenceFactory_.getElkClassInclusionHierarchy(
				toElkExpression(extendedDestinationMatch), premiseSubsumer,
				conclusionSubsumer);
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
		IndexedContextRootMatch extendedOriginMatch = inferenceMatch2
				.getExtendedOriginMatch();
		List<? extends ElkClassExpression> conjuncts = fullSubsumerMatch
				.getClassExpressions();
		int pos = subsumerPrefixLength - 1; // of the decomposed conjunct
		elkInferenceFactory_
				.getElkClassInclusionObjectIntersectionOfDecomposition(
						conjuncts, pos);
		elkInferenceFactory_.getElkClassInclusionHierarchy(
				toElkExpression(extendedOriginMatch),
				conclusionFactory_.getObjectIntersectionOf(conjuncts),
				conjuncts.get(pos));
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionDecomposedSingletonObjectIntersectionOfMatch1 inferenceMatch1) {
		inferenceMatch1.getConclusionMatch(conclusionFactory_);

		// create ELK inferences
		IndexedContextRootMatch extendedDestinationMatch = inferenceMatch1
				.getExtendedDestinationMatch();
		ElkClassExpression conjunct = inferenceMatch1.getConjunctMatch();
		List<ElkClassExpression> conjuncts = Collections
				.singletonList(conjunct);
		elkInferenceFactory_
				.getElkClassInclusionObjectIntersectionOfDecomposition(
						conjuncts, 0);
		elkInferenceFactory_.getElkClassInclusionHierarchy(
				toElkExpression(extendedDestinationMatch),
				conclusionFactory_.getObjectIntersectionOf(conjuncts),
				conjunct);
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionDecomposedSingletonObjectOneOfMatch1 inferenceMatch1) {
		inferenceMatch1.getConclusionMatch(conclusionFactory_);

		// no ELK inferences
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionDecomposedSingletonObjectUnionOfMatch1 inferenceMatch1) {
		inferenceMatch1.getConclusionMatch(conclusionFactory_);

		// create ELK inference
		ElkClassExpression disjunct = inferenceMatch1.getDisjunctMatch();
		elkInferenceFactory_
				.getElkClassInclusionSingletonObjectUnionOfDecomposition(
						disjunct);
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
		SubClassInclusionDecomposedMatch1 premiseMatch1 = inferenceMatch2
				.getFirstPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch2);
		for (SubClassInclusionDecomposedMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new SubClassInclusionDecomposedMatch2InferenceVisitor(
					inferenceFactory_, child)).visit(inferenceMatch2);
		}
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionExpandedDefinitionMatch3 inferenceMatch3) {
		inferenceMatch3.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		IndexedContextRootMatch extendedOriginMatch = inferenceMatch3
				.getExtendedOriginMatch();
		SubClassInclusionExpandedDefinitionMatch2 inferenceMatch2 = inferenceMatch3
				.getParent();
		ElkClass definedClassMatch = inferenceMatch2.getDefinedClassMatch();
		ElkClassExpression definitionMatch = inferenceMatch2
				.getDefinitionMatch();

		elkInferenceFactory_.getElkClassInclusionHierarchy(
				toElkExpression(extendedOriginMatch), definedClassMatch,
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
		SubClassInclusionComposedMatch1 premiseMatch1 = inferenceMatch2
				.getFirstPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch2);
		for (SubClassInclusionComposedMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new SubClassInclusionComposedMatch2InferenceVisitor(
					inferenceFactory_, child)).visit(inferenceMatch2);
		}
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionExpandedFirstEquivalentClassMatch3 inferenceMatch3) {
		inferenceMatch3.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		IndexedContextRootMatch extendedOriginMatch = inferenceMatch3
				.getExtendedOriginMatch();
		SubClassInclusionExpandedFirstEquivalentClassMatch2 inferenceMatch2 = inferenceMatch3
				.getParent();
		ElkClassExpression conclusionSubsumerMatch = inferenceMatch2
				.getConclusionSubsumerMatch();
		ElkClassExpression premiseSubsumerMatch = inferenceMatch2
				.getPremiseSubsumerMatch();

		elkInferenceFactory_.getElkClassInclusionHierarchy(
				toElkExpression(extendedOriginMatch), premiseSubsumerMatch,
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
		SubClassInclusionComposedMatch1 premiseMatch1 = inferenceMatch2
				.getFirstPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch2);
		for (SubClassInclusionComposedMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new SubClassInclusionComposedMatch2InferenceVisitor(
					inferenceFactory_, child)).visit(inferenceMatch2);
		}
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionExpandedSecondEquivalentClassMatch3 inferenceMatch3) {
		inferenceMatch3.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		IndexedContextRootMatch extendedOriginMatch = inferenceMatch3
				.getExtendedOriginMatch();
		SubClassInclusionExpandedSecondEquivalentClassMatch2 inferenceMatch2 = inferenceMatch3
				.getParent();
		ElkClassExpression conclusionSubsumerMatch = inferenceMatch2
				.getConclusionSubsumerMatch();
		ElkClassExpression premiseSubsumerMatch = inferenceMatch2
				.getPremiseSubsumerMatch();

		elkInferenceFactory_.getElkClassInclusionHierarchy(
				toElkExpression(extendedOriginMatch), premiseSubsumerMatch,
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
		SubClassInclusionComposedMatch1 premiseMatch1 = inferenceMatch2
				.getFirstPremiseMatch(conclusionFactory_);
		inferences_.add(premiseMatch1, inferenceMatch2);
		for (SubClassInclusionComposedMatch2 child : hierarchy_
				.getChildren(premiseMatch1)) {
			(new SubClassInclusionComposedMatch2InferenceVisitor(
					inferenceFactory_, child)).visit(inferenceMatch2);
		}
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionExpandedSubClassOfMatch3 inferenceMatch3) {
		inferenceMatch3.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		IndexedContextRootMatch extendedOriginMatch = inferenceMatch3
				.getExtendedOriginMatch();
		SubClassInclusionExpandedSubClassOfMatch2 inferenceMatch2 = inferenceMatch3
				.getParent();
		ElkClassExpression conclusionSubsumerMatch = inferenceMatch2
				.getConclusionSubsumerMatch();
		ElkClassExpression premiseSubsumerMatch = inferenceMatch2
				.getPremiseSubsumerMatch();

		elkInferenceFactory_.getElkClassInclusionHierarchy(
				toElkExpression(extendedOriginMatch), premiseSubsumerMatch,
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
		IndexedContextRootMatch extendedOriginMatch = inferenceMatch2
				.getExtendedOriginMatch();
		ElkObjectProperty propertyMatch = inferenceMatch2.getPropertyMatch();

		elkInferenceFactory_.getElkClassInclusionReflexivePropertyRange(
				toElkExpression(extendedOriginMatch), propertyMatch,
				rangeMatch);

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
				.getSecondPremiseMatch(conclusionFactory_);
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
		ElkClassExpression conclusionSubsumerMatch = inferenceMatch2
				.getConclusionSubsumerMatch();
		SubClassInclusionRangeMatch1 inferenceMatch1 = inferenceMatch2
				.getParent();
		IndexedContextRootMatch originMatch = inferenceMatch1.getOriginMatch();
		List<? extends ElkClassExpression> rangeMatches = originMatch
				.getRangeMatches();
		List<ElkClassExpression> fillerRanges = new ArrayList<ElkClassExpression>(
				rangeMatches.size() + 2);
		fillerRanges.add(originMatch.getMainFillerMatch(conclusionFactory_));
		fillerRanges.addAll(originMatch.getRangeMatches());
		fillerRanges.add(conclusionSubsumerMatch);
		elkInferenceFactory_
				.getElkClassInclusionObjectIntersectionOfDecomposition(
						fillerRanges, fillerRanges.size() - 1);

		return null;
	}

	@Override
	public Void visit(SubClassInclusionTautologyMatch1 inferenceMatch1) {
		inferenceMatch1.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		IndexedContextRootMatch originMatch = inferenceMatch1.getOriginMatch();

		if (originMatch.getRangeMatches().isEmpty()) {
			elkInferenceFactory_.getElkClassInclusionTautology(
					toElkExpression(originMatch));
		} else {
			elkInferenceFactory_
					.getElkClassInclusionObjectIntersectionOfDecomposition(
							getFillerRanges(originMatch), 0);
		}
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
	public Void visit(
			SubPropertyChainExpandedSubObjectPropertyOfMatch3 inferenceMatch3) {
		inferenceMatch3.getConclusionMatch(conclusionFactory_);

		// creating ELK inferences
		SubPropertyChainExpandedSubObjectPropertyOfMatch2 inferenceMatch2 = inferenceMatch3
				.getParent();
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
