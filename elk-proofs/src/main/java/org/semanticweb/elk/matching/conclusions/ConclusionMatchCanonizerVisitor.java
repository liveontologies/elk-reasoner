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

import org.semanticweb.elk.exceptions.ElkRuntimeException;
import org.semanticweb.elk.matching.root.IndexedContextRootMatch;
import org.semanticweb.elk.matching.subsumers.SubsumerEmptyObjectIntersectionOfMatch;
import org.semanticweb.elk.matching.subsumers.SubsumerEmptyObjectOneOfMatch;
import org.semanticweb.elk.matching.subsumers.SubsumerEmptyObjectUnionOfMatch;
import org.semanticweb.elk.matching.subsumers.SubsumerMatch;
import org.semanticweb.elk.matching.subsumers.SubsumerMatchDummyVisitor;
import org.semanticweb.elk.matching.subsumers.SubsumerNonCanonicalMatch;
import org.semanticweb.elk.matching.subsumers.SubsumerObjectHasValueMatch;
import org.semanticweb.elk.matching.subsumers.SubsumerSingletonObjectIntersectionOfMatch;
import org.semanticweb.elk.matching.subsumers.SubsumerSingletonObjectOneOfMatch;
import org.semanticweb.elk.matching.subsumers.SubsumerSingletonObjectUnionOfMatch;
import org.semanticweb.elk.owl.inferences.ElkInference;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasValue;
import org.semanticweb.elk.owl.interfaces.ElkObjectOneOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectUnionOf;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;

public class ConclusionMatchCanonizerVisitor
		extends ConclusionMatchDummyVisitor<Boolean> {

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
		SubsumerMatch subsumerMatch = conclusionMatch.getSubsumerMatch();
		return subsumerMatch.accept(new SubsumerMatchDummyVisitor<Boolean>() {

			@Override
			protected Boolean defaultVisit(SubsumerMatch match) {
				return false;
			}

			@Override
			protected Boolean defaultVisit(SubsumerNonCanonicalMatch match) {
				// fail fast if some case is forgotten
				throw new ElkRuntimeException(match + ": missing case");
			}

			@Override
			public Boolean visit(SubsumerEmptyObjectIntersectionOfMatch match) {
				conclusionFactory_.getSubClassInclusionComposedMatch1(parent,
						destinationMatch, conclusionFactory_.getOwlThing());
				// create ELK inferences
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
			public Boolean visit(SubsumerEmptyObjectOneOfMatch match) {
				ElkClass owlNothing = conclusionFactory_.getOwlNothing();
				ElkObjectOneOf emptyOneOf = conclusionFactory_.getObjectOneOf(
						Collections.<ElkIndividual> emptyList());
				ElkClassExpression subExpression = toElkExpression(
						destinationMatch);
				conclusionFactory_.getSubClassInclusionComposedMatch1(parent,
						destinationMatch, conclusionFactory_.getOwlNothing());
				// create ELK inferences
				elkInferenceFactory_.getElkClassInclusionOwlNothing(emptyOneOf);
				elkInferenceFactory_.getElkClassInclusionHierarchy(
						subExpression, owlNothing, emptyOneOf);
				return true;
			}

			@Override
			public Boolean visit(SubsumerEmptyObjectUnionOfMatch match) {
				ElkClass owlNothing = conclusionFactory_.getOwlNothing();
				ElkObjectUnionOf emptyUnionOf = conclusionFactory_
						.getObjectUnionOf(
								Collections.<ElkClassExpression> emptyList());
				ElkClassExpression subExpression = toElkExpression(
						destinationMatch);
				conclusionFactory_.getSubClassInclusionComposedMatch1(parent,
						destinationMatch, owlNothing);
				// create ELK inferences
				elkInferenceFactory_
						.getElkClassInclusionOwlNothing(emptyUnionOf);
				elkInferenceFactory_.getElkClassInclusionHierarchy(
						subExpression, owlNothing, emptyUnionOf);
				return true;
			}

			@Override
			public Boolean visit(SubsumerObjectHasValueMatch match) {
				ElkObjectHasValue existential = match.getValue();
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
			public Boolean visit(
					SubsumerSingletonObjectIntersectionOfMatch match) {
				ElkClassExpression conjunct = match.getValue()
						.getClassExpressions().get(0);
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
			public Boolean visit(SubsumerSingletonObjectOneOfMatch match) {
				ElkIndividual member = match.getValue().getIndividuals().get(0);
				conclusionFactory_.getSubClassInclusionComposedMatch1(parent,
						destinationMatch, member);
				// no ELK inferences since indexed individuals are
				// converted to ObjectOneOf
				return true;
			}

			@Override
			public Boolean visit(SubsumerSingletonObjectUnionOfMatch match) {
				ElkClassExpression disjunct = match.getValue()
						.getClassExpressions().get(0);
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
		SubsumerMatch subsumerMatch = conclusionMatch.getSubsumerMatch();
		return subsumerMatch.accept(new SubsumerMatchDummyVisitor<Boolean>() {

			@Override
			protected Boolean defaultVisit(SubsumerMatch match) {
				return false;
			}

			@Override
			protected Boolean defaultVisit(SubsumerNonCanonicalMatch match) {
				// fail fast if some case is forgotten
				throw new ElkRuntimeException(match + ": mssing case");
			}

			@Override
			public Boolean visit(SubsumerEmptyObjectIntersectionOfMatch match) {
				conclusionFactory_.getSubClassInclusionDecomposedMatch2(parent,
						conclusionFactory_.getOwlThing());
				// create ELK inference
				elkInferenceFactory_.getElkClassInclusionOwlThing(
						toElkExpression(destinationMatch));
				return true;
			}

			@Override
			public Boolean visit(SubsumerEmptyObjectOneOfMatch match) {
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
			public Boolean visit(SubsumerEmptyObjectUnionOfMatch match) {
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
			public Boolean visit(SubsumerObjectHasValueMatch match) {
				ElkObjectHasValue existential = match.getValue();
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
			public Boolean visit(
					SubsumerSingletonObjectIntersectionOfMatch match) {
				ElkClassExpression conjunct = match.getValue()
						.getClassExpressions().get(0);
				conclusionFactory_.getSubClassInclusionDecomposedMatch2(parent,
						conjunct);
				// create ELK inferences
				List<ElkClassExpression> conjuncts = Collections
						.singletonList(conjunct);
				elkInferenceFactory_.getElkClassInclusionHierarchy(
						toElkExpression(destinationMatch),
						conclusionFactory_.getObjectIntersectionOf(conjuncts),
						conjunct);
				elkInferenceFactory_
						.getElkClassInclusionObjectIntersectionOfDecomposition(
								conjuncts, 0);
				return true;
			}

			@Override
			public Boolean visit(SubsumerSingletonObjectOneOfMatch match) {
				ElkIndividual member = match.getValue().getIndividuals().get(0);
				conclusionFactory_.getSubClassInclusionDecomposedMatch2(parent,
						member);
				// no ELK inferences
				return true;
			}

			@Override
			public Boolean visit(SubsumerSingletonObjectUnionOfMatch match) {
				ElkClassExpression disjunct = match.getValue()
						.getClassExpressions().get(0);
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

}
