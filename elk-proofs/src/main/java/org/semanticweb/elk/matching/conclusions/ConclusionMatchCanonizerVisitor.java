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

import org.semanticweb.elk.matching.root.IndexedContextRootMatch;
import org.semanticweb.elk.matching.subsumers.IndexedObjectIntersectionOfMatch;
import org.semanticweb.elk.matching.subsumers.IndexedObjectSomeValuesFromHasValueMatch;
import org.semanticweb.elk.matching.subsumers.IndexedObjectUnionOfOneOfMatch;
import org.semanticweb.elk.matching.subsumers.IndexedObjectUnionOfUnionOfMatch;
import org.semanticweb.elk.matching.subsumers.SubsumerMatch;
import org.semanticweb.elk.matching.subsumers.SubsumerMatchDummyVisitor;
import org.semanticweb.elk.owl.inferences.ElkInference;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasValue;
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;

public class ConclusionMatchCanonizerVisitor
		extends ConclusionMatchDummyVisitor<Boolean> {

	private static interface SubsumerCasesCanonizer {

		boolean anyElkObjectHasValue(ElkObjectHasValue existential);

		boolean emptyElkObjectIntersectionOf();

		boolean emptyElkObjectOneOf();

		boolean emptyElkObjectUnionOf();

		boolean singletonElkObjectIntersectionOf(ElkClassExpression conjunct);

		boolean singletonElkObjectOneOf(ElkIndividual member);

		boolean singletonElkObjectUnionOf(ElkClassExpression disjunct);

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
			public boolean anyElkObjectHasValue(ElkObjectHasValue existential) {
				ElkObjectPropertyExpression property = existential
						.getProperty();
				ElkIndividual value = existential.getFiller();
				ElkObjectSomeValuesFrom translated = conclusionFactory_
						.getObjectSomeValuesFrom(property,
								conclusionFactory_.getObjectOneOf(value));
				conclusionFactory_.getSubClassInclusionComposedMatch1(parent,
						destinationMatch, translated);

				// create ELK inferences
				elkInferenceFactory_
						.getElkEquivalentClassesObjectHasValue(property, value);
				elkInferenceFactory_.getElkClassInclusionOfEquivaletClasses(
						existential, translated, false);
				elkInferenceFactory_.getElkClassInclusionHierarchy(
						toElkExpression(destinationMatch), translated,
						existential);
				return true;
			}

			@Override
			public boolean emptyElkObjectIntersectionOf() {
				conclusionFactory_.getSubClassInclusionComposedMatch1(parent,
						destinationMatch, conclusionFactory_.getOwlThing());
				// create ELK inference
				ElkClassExpression subExpression = toElkExpression(
						destinationMatch);
				elkInferenceFactory_
						.getElkClassInclusionOwlThing(subExpression);
				elkInferenceFactory_.getElkClassInclusionHierarchy(
						subExpression, conclusionFactory_.getOwlThing(),
						conclusionFactory_.getObjectIntersectionOf(
								Collections.<ElkClassExpression> emptyList()));
				elkInferenceFactory_
						.getElkClassInclusionOwlThingEmptyObjectIntersectionOf();
				return true;
			}

			@Override
			public boolean emptyElkObjectOneOf() {
				conclusionFactory_.getSubClassInclusionComposedMatch1(parent,
						destinationMatch, conclusionFactory_.getOwlNothing());
				// no ELK inference necessary due to class inconsistency
				return true;
			}

			@Override
			public boolean emptyElkObjectUnionOf() {
				conclusionFactory_.getSubClassInclusionComposedMatch1(parent,
						destinationMatch, conclusionFactory_.getOwlNothing());
				// no ELK inference necessary due to class inconsistency
				return true;
			}

			@Override
			public boolean singletonElkObjectIntersectionOf(
					ElkClassExpression conjunct) {
				conclusionFactory_.getSubClassInclusionComposedMatch1(parent,
						destinationMatch, conjunct);
				// create ELK inference
				elkInferenceFactory_
						.getElkClassInclusionObjectIntersectionOfComposition(
								toElkExpression(destinationMatch),
								Collections.singletonList(conjunct));
				return true;
			}

			@Override
			public boolean singletonElkObjectOneOf(ElkIndividual member) {
				conclusionFactory_.getSubClassInclusionComposedMatch1(parent,
						destinationMatch, member);
				// no ELK inferences
				return true;
			}

			@Override
			public boolean singletonElkObjectUnionOf(
					ElkClassExpression disjunct) {
				conclusionFactory_.getSubClassInclusionComposedMatch1(parent,
						destinationMatch, disjunct);
				// create ELK inferences
				List<ElkClassExpression> disjuncts = Collections
						.singletonList(disjunct);
				elkInferenceFactory_
						.getElkClassInclusionObjectUnionOfComposition(disjuncts,
								0);
				elkInferenceFactory_.getElkClassInclusionHierarchy(
						toElkExpression(destinationMatch), disjunct,
						conclusionFactory_.getObjectUnionOf(disjuncts));
				return true;
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
			public boolean anyElkObjectHasValue(ElkObjectHasValue existential) {
				ElkObjectPropertyExpression property = existential
						.getProperty();
				ElkIndividual value = existential.getFiller();
				ElkObjectSomeValuesFrom translated = conclusionFactory_
						.getObjectSomeValuesFrom(property,
								conclusionFactory_.getObjectOneOf(value));
				conclusionFactory_.getSubClassInclusionDecomposedMatch2(parent,
						translated);

				// create ELK inferences
				elkInferenceFactory_
						.getElkEquivalentClassesObjectHasValue(property, value);
				elkInferenceFactory_.getElkClassInclusionOfEquivaletClasses(
						existential, translated, true);
				elkInferenceFactory_.getElkClassInclusionHierarchy(
						toElkExpression(destinationMatch), existential,
						translated);
				return true;
			}

			@Override
			public boolean emptyElkObjectIntersectionOf() {
				conclusionFactory_.getSubClassInclusionDecomposedMatch2(parent,
						conclusionFactory_.getOwlThing());
				// create ELK inference
				elkInferenceFactory_.getElkClassInclusionOwlThing(
						toElkExpression(destinationMatch));
				return true;
			}

			@Override
			public boolean emptyElkObjectOneOf() {
				conclusionFactory_.getSubClassInclusionDecomposedMatch2(parent,
						conclusionFactory_.getOwlNothing());
				// create ELK inference
				elkInferenceFactory_.getElkClassInclusionHierarchy(
						toElkExpression(destinationMatch),
						conclusionFactory_.getObjectOneOf(
								Collections.<ElkIndividual> emptyList()),
						conclusionFactory_.getOwlNothing());
				elkInferenceFactory_
						.getElkClassInclusionEmptyObjectOneOfOwlNothing();
				return true;
			}

			@Override
			public boolean emptyElkObjectUnionOf() {
				conclusionFactory_.getSubClassInclusionDecomposedMatch2(parent,
						conclusionFactory_.getOwlNothing());
				// create ELK inferences
				elkInferenceFactory_.getElkClassInclusionHierarchy(
						toElkExpression(destinationMatch),
						conclusionFactory_.getObjectUnionOf(
								Collections.<ElkClassExpression> emptyList()),
						conclusionFactory_.getOwlNothing());
				elkInferenceFactory_
						.getElkClassInclusionEmptyObjectUnionOfOwlNothing();
				return true;
			}

			@Override
			public boolean singletonElkObjectIntersectionOf(
					ElkClassExpression conjunct) {
				conclusionFactory_.getSubClassInclusionDecomposedMatch2(parent,
						conjunct);
				// create ELK inference
				elkInferenceFactory_
						.getElkClassInclusionObjectIntersectionOfDecomposition(
								toElkExpression(destinationMatch),
								Collections.singletonList(conjunct), 0);
				return true;
			}

			@Override
			public boolean singletonElkObjectOneOf(ElkIndividual member) {
				conclusionFactory_.getSubClassInclusionDecomposedMatch2(parent,
						member);
				// no ELK inferences
				return true;
			}

			@Override
			public boolean singletonElkObjectUnionOf(
					ElkClassExpression disjunct) {
				conclusionFactory_.getSubClassInclusionDecomposedMatch2(parent,
						disjunct);
				// create ELK inference
				elkInferenceFactory_
						.getElkClassInclusionSingletonObjectUnionOfDecomposition(
								disjunct);
				return true;
			}

		});
	}

	private boolean visit(final SubClassInclusionMatch<?> conclusionMatch,
			final SubsumerCasesCanonizer canonizer) {

		return conclusionMatch.getSubsumerMatch()
				.accept(new SubsumerMatchDummyVisitor<Boolean>() {

					@Override
					protected Boolean defaultVisit(SubsumerMatch match) {
						return false;
					}

					@Override
					public Boolean visit(
							IndexedObjectIntersectionOfMatch subsumerMatch) {

						ElkObjectIntersectionOf expression = subsumerMatch
								.getFullValue();
						List<? extends ElkClassExpression> conjuncts = expression
								.getClassExpressions();
						if (subsumerMatch.getPrefixLength() < conjuncts
								.size()) {
							return false;
						}
						// else
						switch (conjuncts.size()) {

						case 0:
							return canonizer.emptyElkObjectIntersectionOf();

						case 1:
							return canonizer.singletonElkObjectIntersectionOf(
									conjuncts.get(0));
						default:
							return false;

						}
					}

					@Override
					public Boolean visit(
							IndexedObjectSomeValuesFromHasValueMatch match) {
						return canonizer.anyElkObjectHasValue(match.getValue());
					}

					@Override
					public Boolean visit(IndexedObjectUnionOfOneOfMatch match) {
						List<? extends ElkIndividual> individuals = match
								.getValue().getIndividuals();

						switch (individuals.size()) {

						case 0:
							return canonizer.emptyElkObjectOneOf();

						case 1:
							return canonizer.singletonElkObjectOneOf(
									individuals.get(0));

						default:
							return false;

						}

					}

					@Override
					public Boolean visit(
							IndexedObjectUnionOfUnionOfMatch match) {
						List<? extends ElkClassExpression> disjuncts = match
								.getValue().getClassExpressions();

						switch (disjuncts.size()) {

						case 0:
							return canonizer.emptyElkObjectUnionOf();
						case 1:
							return canonizer.singletonElkObjectUnionOf(
									disjuncts.get(0));
						default:
							return false;
						}

					}

				});

	}

}
