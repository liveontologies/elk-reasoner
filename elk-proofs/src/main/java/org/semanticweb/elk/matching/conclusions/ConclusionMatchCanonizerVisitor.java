package org.semanticweb.elk.matching.conclusions;

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

import java.util.Collections;
import java.util.List;

import org.semanticweb.elk.owl.inferences.ElkInference;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectOneOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectUnionOf;
import org.semanticweb.elk.owl.visitors.DummyElkClassExpressionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;

public class ConclusionMatchCanonizerVisitor
		extends ConclusionMatchDummyVisitor<Boolean> {

	private static interface SubsumerCasesCanonizer {

		void emptyElkObjectIntersectionOf();

		void emptyElkObjectOneOf();

		void emptyElkObjectUnionOf();

		void singletonElkObjectIntersectionOf(ElkClassExpression conjunct);

		void singletonElkObjectOneOf(ElkIndividual member);

		void singletonElkObjectUnionOf(ElkClassExpression disjunct);

	}

	private final ConclusionMatchExpressionFactory conclusionFactory_;

	private final ElkInference.Factory elkInferenceFactory_;

	public ConclusionMatchCanonizerVisitor(
			ConclusionMatchExpressionFactory conclusionFactory,
			ElkInference.Factory elkInferenceFactory) {
		this.conclusionFactory_ = conclusionFactory;
		this.elkInferenceFactory_ = elkInferenceFactory;
	}

	@Override
	protected Boolean defaultVisit(ConclusionMatch conclusionMatch) {
		return false;
	}

	ElkClassExpression toElkExpression(IndexedContextRootMatch rootMatch) {
		return rootMatch.toElkExpression(conclusionFactory_);
	}

	@Override
	public Boolean visit(
			final SubClassInclusionComposedMatch1 conclusionMatch) {
		final SubClassInclusionComposed parent = conclusionMatch.getParent();
		final IndexedContextRootMatch destinationMatch = conclusionMatch
				.getDestinationMatch();
		return visit(conclusionMatch, new SubsumerCasesCanonizer() {

			@Override
			public void emptyElkObjectIntersectionOf() {
				conclusionFactory_.getSubClassInclusionComposedMatch1(parent,
						destinationMatch, conclusionFactory_.getOwlThing());
				// create ELK inference
				elkInferenceFactory_
						.getElkClassInclusionEmptyObjectIntersectionOfComposition(
								toElkExpression(destinationMatch));
			}

			@Override
			public void emptyElkObjectOneOf() {
				conclusionFactory_.getSubClassInclusionComposedMatch1(parent,
						destinationMatch, conclusionFactory_.getOwlNothing());
				// no ELK inference necessary due to class inconsistency
			}

			@Override
			public void emptyElkObjectUnionOf() {
				conclusionFactory_.getSubClassInclusionComposedMatch1(parent,
						destinationMatch, conclusionFactory_.getOwlNothing());
				// no ELK inference necessary due to class inconsistency
			}

			@Override
			public void singletonElkObjectIntersectionOf(
					ElkClassExpression conjunct) {
				conclusionFactory_.getSubClassInclusionComposedMatch1(parent,
						destinationMatch, conjunct);
				// create ELK inference
				elkInferenceFactory_
						.getElkClassInclusionObjectIntersectionOfComposition(
								toElkExpression(destinationMatch),
								Collections.singletonList(conjunct));
			}

			@Override
			public void singletonElkObjectOneOf(ElkIndividual member) {
				// TODO
			}

			@Override
			public void singletonElkObjectUnionOf(ElkClassExpression disjunct) {
				conclusionFactory_.getSubClassInclusionComposedMatch1(parent,
						destinationMatch, disjunct);
				// create ELK inference
				elkInferenceFactory_
						.getElkClassInclusionObjectUnionOfComposition(
								toElkExpression(destinationMatch),
								Collections.singletonList(disjunct), 0);
			}

		});

	}

	@Override
	public Boolean visit(SubClassInclusionDecomposedMatch2 conclusionMatch) {
		final SubClassInclusionDecomposedMatch1 parent = conclusionMatch
				.getParent();
		final IndexedContextRootMatch destinationMatch = parent
				.getDestinationMatch();
		return visit(conclusionMatch, new SubsumerCasesCanonizer() {

			@Override
			public void emptyElkObjectIntersectionOf() {
				conclusionFactory_.getSubClassInclusionDecomposedMatch2(parent,
						conclusionFactory_.getOwlThing());
				// create ELK inference
				elkInferenceFactory_.getElkClassInclusionOwlThing(
						toElkExpression(destinationMatch));

			}

			@Override
			public void emptyElkObjectOneOf() {
				conclusionFactory_.getSubClassInclusionDecomposedMatch2(parent,
						conclusionFactory_.getOwlNothing());
				// create ELK inference
				elkInferenceFactory_
						.getElkClassInclusionEmptyObjectOneOfDecomposition(
								toElkExpression(destinationMatch));
			}

			@Override
			public void emptyElkObjectUnionOf() {
				conclusionFactory_.getSubClassInclusionDecomposedMatch2(parent,
						conclusionFactory_.getOwlNothing());
				// create ELK inference
				elkInferenceFactory_
						.getElkClassInclusionEmptyObjectUnionOfDecomposition(
								toElkExpression(destinationMatch));
			}

			@Override
			public void singletonElkObjectIntersectionOf(
					ElkClassExpression conjunct) {
				conclusionFactory_.getSubClassInclusionDecomposedMatch2(parent,
						conjunct);
				// create ELK inference
				elkInferenceFactory_
						.getElkClassInclusionObjectIntersectionOfDecomposition(
								toElkExpression(destinationMatch),
								Collections.singletonList(conjunct), 0);
			}

			@Override
			public void singletonElkObjectOneOf(ElkIndividual member) {
				// TODO
			}

			@Override
			public void singletonElkObjectUnionOf(ElkClassExpression disjunct) {
				conclusionFactory_.getSubClassInclusionDecomposedMatch2(parent,
						disjunct);
				// create ELK inference
				elkInferenceFactory_
						.getElkClassInclusionSingletonObjectUnionOfDecomposition(
								toElkExpression(destinationMatch), disjunct);
			}

		});
	}

	private boolean visit(final SubClassInclusionMatch<?> conclusionMatch,
			final SubsumerCasesCanonizer canonizer) {

		return conclusionMatch.getSubsumerMatch()
				.accept(new SubsumerMatch.Visitor<Boolean>() {

					@Override
					public Boolean visit(SubsumerGeneralMatch subsumerMatch) {
						return subsumerMatch.getGeneralMatch().accept(
								new DummyElkClassExpressionVisitor<Boolean>() {

									@Override
									protected Boolean defaultVisit(
											ElkClassExpression ce) {
										return false;
									}

									@Override
									public Boolean visit(
											ElkObjectIntersectionOf expression) {

										List<? extends ElkClassExpression> conjuncts = expression
												.getClassExpressions();

										switch (conjuncts.size()) {

										case 0:
											canonizer
													.emptyElkObjectIntersectionOf();
											return true;

										case 1:
											canonizer
													.singletonElkObjectIntersectionOf(
															conjuncts.get(0));
											return true;
										default:
											return false;

										}
									}

									@Override
									public Boolean visit(
											ElkObjectOneOf expression) {

										List<? extends ElkIndividual> individuals = expression
												.getIndividuals();

										switch (individuals.size()) {

										case 0:
											canonizer.emptyElkObjectOneOf();
											return true;

										case 1:
											canonizer.singletonElkObjectOneOf(
													individuals.get(0));
											return true;

										default:
											return false;

										}

									}

									@Override
									public Boolean visit(
											ElkObjectUnionOf expression) {

										List<? extends ElkClassExpression> disjuncts = expression
												.getClassExpressions();

										switch (disjuncts.size()) {

										case 0:
											canonizer.emptyElkObjectUnionOf();
											return true;

										case 1:
											canonizer.singletonElkObjectUnionOf(
													disjuncts.get(0));
											return true;

										default:
											return false;

										}

									}

								});
					}

					@Override
					public Boolean visit(
							SubsumerPartialConjunctionMatch subsumerMatch) {
						return false;
					}

				});

	}

}
