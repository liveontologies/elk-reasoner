/*-
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
package org.semanticweb.elk.owl.inferences;

import java.util.ArrayList;
import java.util.List;

import org.liveontologies.proof.util.Inference;
import org.semanticweb.elk.owl.implementation.ElkObjectBaseFactory;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectInverseOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyChain;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.iris.ElkAbbreviatedIri;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.owl.iris.ElkIri;
import org.semanticweb.elk.owl.iris.ElkPrefix;
import org.semanticweb.elk.owl.iris.ElkPrefixImpl;
import org.semanticweb.elk.owl.visitors.ElkSubObjectPropertyExpressionVisitor;

/**
 * An {@link ElkInference.Visitor} that produces simple examples for the visited
 * inferences, which can be used for explanation purpose. Usually an example is
 * an inference instantiated with some generic parameters. Returns {@code null},
 * no example can be provided for the inference.
 * 
 * @author Yevgeny Kazakov
 */
public class ElkInferenceExamples
		implements ElkInference.Visitor<ElkInference> {

	private final static ElkInferenceExamples INSTANCE_ = new ElkInferenceExamples();

	private final ElkObject.Factory elkFactory_ = new ElkObjectBaseFactory();

	private final ElkPrefix examplePrefix = new ElkPrefixImpl("",
			new ElkFullIri("http://org.example/"));

	private final ElkInference.Factory inferenceFactory_ = new ElkInferenceBaseFactory();

	private ElkInferenceExamples() {

	}

	public static Inference<ElkAxiom> getExample(
			Inference<ElkAxiom> inference) {
		if (inference instanceof ElkInference) {
			return ((ElkInference) inference).accept(INSTANCE_);
		}
		// else
		return null;
	}

	private ElkClass getClass(String name) {
		return elkFactory_.getClass(getIri(name));
	}

	private List<ElkClass> getClasses(String prefix, int count) {
		List<ElkClass> result = new ArrayList<ElkClass>(count);
		for (int i = 1; i <= count; i++) {
			result.add(getClass(prefix + i));
		}
		return result;
	}

	private ElkIndividual getIndividual(String name) {
		return elkFactory_.getNamedIndividual(getIri(name));
	}

	private List<ElkIndividual> getIndividuals(String prefix, int count) {
		List<ElkIndividual> result = new ArrayList<ElkIndividual>(count);
		for (int i = 1; i <= count; i++) {
			result.add(getIndividual(prefix + i));
		}
		return result;
	}

	private ElkIri getIri(String name) {
		return new ElkAbbreviatedIri(examplePrefix, name);
	}

	private List<ElkObjectProperty> getObjectProperties(String prefix,
			int count) {
		return getObjectProperties(prefix, 0, count);
	}

	private List<ElkObjectProperty> getObjectProperties(String prefix,
			int suffix, int count) {
		List<ElkObjectProperty> result = new ArrayList<ElkObjectProperty>(
				count);
		for (int i = 1; i <= count; i++) {
			result.add(getObjectProperty(prefix + suffix++));
		}
		return result;
	}

	private ElkObjectProperty getObjectProperty(String name) {
		return elkFactory_.getObjectProperty(getIri(name));
	}

	@Override
	public ElkClassAssertionOfClassInclusion visit(
			final ElkClassAssertionOfClassInclusion inference) {
		return inferenceFactory_.getElkClassAssertionOfClassInclusion(
				getIndividual("a"), getClass("C"));
	}

	@Override
	public ElkClassInclusionEmptyObjectOneOfOwlNothing visit(
			ElkClassInclusionEmptyObjectOneOfOwlNothing inference) {
		return inferenceFactory_
				.getElkClassInclusionEmptyObjectOneOfOwlNothing();
	}

	@Override
	public ElkClassInclusionEmptyObjectUnionOfOwlNothing visit(
			ElkClassInclusionEmptyObjectUnionOfOwlNothing inference) {
		return inferenceFactory_
				.getElkClassInclusionEmptyObjectUnionOfOwlNothing();
	}

	@Override
	public ElkClassInclusionExistentialComposition visit(
			ElkClassInclusionExistentialComposition inference) {
		return inferenceFactory_.getElkClassInclusionExistentialComposition(
				getClasses("C", inference.getClassExpressions().size()),
				getObjectProperties("R", inference.getSubChain().size()),
				getObjectProperty("S"));
	}

	@Override
	public ElkClassInclusionExistentialFillerExpansion visit(
			ElkClassInclusionExistentialFillerExpansion inference) {
		return inferenceFactory_.getElkClassInclusionExistentialFillerExpansion(
				getObjectProperty("R"), getClass("C"), getClass("D"));
	}

	@Override
	public ElkClassInclusionExistentialOfObjectHasSelf visit(
			ElkClassInclusionExistentialOfObjectHasSelf inference) {
		return inferenceFactory_.getElkClassInclusionExistentialOfObjectHasSelf(
				getClass("C"), getObjectProperty("R"));
	}

	@Override
	public ElkClassInclusionExistentialOwlNothing visit(
			ElkClassInclusionExistentialOwlNothing inference) {
		return inferenceFactory_.getElkClassInclusionExistentialOwlNothing(
				getObjectProperty("R"));
	}

	@Override
	public ElkClassInclusionExistentialPropertyExpansion visit(
			ElkClassInclusionExistentialPropertyExpansion inference) {
		return inferenceFactory_
				.getElkClassInclusionExistentialPropertyExpansion(
						getObjectProperty("R"), getObjectProperty("S"),
						getClass("C"));
	}

	@Override
	public ElkClassInclusionExistentialRange visit(
			ElkClassInclusionExistentialRange inference) {
		return inferenceFactory_.getElkClassInclusionExistentialRange(
				getObjectProperty("R"), getClass("D"),
				getClasses("E", inference.getRanges().size()));
	}

	@Override
	public ElkClassInclusionExistentialTransitivity visit(
			ElkClassInclusionExistentialTransitivity inference) {
		return inferenceFactory_.getElkClassInclusionExistentialTransitivity(
				getObjectProperty("T"),
				getClasses("C", inference.getClassExpressions().size()));
	}

	@Override
	public ElkClassInclusionHierarchy visit(
			ElkClassInclusionHierarchy inference) {
		return inferenceFactory_.getElkClassInclusionHierarchy(
				getClasses("C", inference.getExpressions().size()));
	}

	@Override
	public ElkClassInclusionNegationClash visit(
			ElkClassInclusionNegationClash inference) {
		return inferenceFactory_
				.getElkClassInclusionNegationClash(getClass("C"));
	}

	@Override
	public ElkClassInclusionObjectIntersectionOfComposition visit(
			ElkClassInclusionObjectIntersectionOfComposition inference) {
		return inferenceFactory_
				.getElkClassInclusionObjectIntersectionOfComposition(
						getClass("C"),
						getClasses("D", inference.getConjuncts().size()));
	}

	@Override
	public ElkClassInclusionObjectIntersectionOfDecomposition visit(
			ElkClassInclusionObjectIntersectionOfDecomposition inference) {
		return inferenceFactory_
				.getElkClassInclusionObjectIntersectionOfDecomposition(
						getClasses("C", inference.getConjuncts().size()),
						inference.getConjunctPos());
	}

	@Override
	public ElkClassInclusionObjectIntersectionOfInclusion visit(
			ElkClassInclusionObjectIntersectionOfInclusion inference) {
		return inferenceFactory_
				.getElkClassInclusionObjectIntersectionOfInclusion(
						getClasses("C", inference.getSubClasses().size()),
						inference.getSuperPositions());
	}

	@Override
	public ElkClassInclusionObjectOneOfInclusion visit(
			ElkClassInclusionObjectOneOfInclusion inference) {
		return inferenceFactory_.getElkClassInclusionObjectOneOfInclusion(
				getIndividuals("a", inference.getSuperIndividuals().size()),
				inference.getSubIndividualPositions());
	}

	@Override
	public ElkClassInclusionObjectUnionOfComposition visit(
			ElkClassInclusionObjectUnionOfComposition inference) {
		return inferenceFactory_.getElkClassInclusionObjectUnionOfComposition(
				getClasses("C", inference.getDisjuncts().size()),
				inference.getDisjunctPos());
	}

	@Override
	public ElkClassInclusionOfClassAssertion visit(
			ElkClassInclusionOfClassAssertion inference) {
		return inferenceFactory_.getElkClassInclusionOfClassAssertion(
				getIndividual("a"), getClass("C"));
	}

	@Override
	public ElkClassInclusionOfDisjointClasses visit(
			ElkClassInclusionOfDisjointClasses inference) {
		return inferenceFactory_.getElkClassInclusionOfDisjointClasses(
				getClasses("C", inference.getExpressions().size()),
				inference.getFirstPos(), inference.getSecondPos());
	}

	@Override
	public ElkClassInclusionOfEquivaletClasses visit(
			ElkClassInclusionOfEquivaletClasses inference) {
		return inferenceFactory_.getElkClassInclusionOfEquivaletClasses(
				getClasses("C", inference.getExpressions().size()),
				inference.getSubPos(), inference.getSuperPos());
	}

	@Override
	public ElkClassInclusionOfObjectPropertyAssertion visit(
			ElkClassInclusionOfObjectPropertyAssertion inference) {
		return inferenceFactory_.getElkClassInclusionOfObjectPropertyAssertion(
				getIndividual("a"), getObjectProperty("R"), getIndividual("b"));
	}

	@Override
	public ElkClassInclusionOfObjectPropertyDomain visit(
			ElkClassInclusionOfObjectPropertyDomain inference) {
		return inferenceFactory_.getElkClassInclusionOfObjectPropertyDomain(
				getObjectProperty("R"), getClass("C"));
	}

	@Override
	public ElkClassInclusionOfReflexiveObjectProperty visit(
			ElkClassInclusionOfReflexiveObjectProperty inference) {
		return inferenceFactory_.getElkClassInclusionOfReflexiveObjectProperty(
				getObjectProperty("R"));
	}

	@Override
	public ElkInference visit(
			final ElkClassInclusionOwlBottomObjectProperty inference) {
		return inferenceFactory_.getElkClassInclusionOwlBottomObjectProperty();
	}

	@Override
	public ElkClassInclusionOwlNothing visit(
			ElkClassInclusionOwlNothing inference) {
		return inferenceFactory_.getElkClassInclusionOwlNothing(getClass("C"));
	}

	@Override
	public ElkClassInclusionOwlThing visit(
			ElkClassInclusionOwlThing inference) {
		return inferenceFactory_.getElkClassInclusionOwlThing(getClass("C"));
	}

	@Override
	public ElkInference visit(
			final ElkClassInclusionOwlTopObjectProperty inference) {
		return inferenceFactory_.getElkClassInclusionOwlTopObjectProperty();
	}

	@Override
	public ElkClassInclusionOwlThingEmptyObjectIntersectionOf visit(
			ElkClassInclusionOwlThingEmptyObjectIntersectionOf inference) {
		return inferenceFactory_
				.getElkClassInclusionOwlThingEmptyObjectIntersectionOf();
	}

	@Override
	public ElkClassInclusionReflexivePropertyRange visit(
			ElkClassInclusionReflexivePropertyRange inference) {
		return inferenceFactory_.getElkClassInclusionReflexivePropertyRange(
				getClass("C"), getObjectProperty("R"), getClass("D"));
	}

	@Override
	public ElkClassInclusionSingletonObjectUnionOfDecomposition visit(
			ElkClassInclusionSingletonObjectUnionOfDecomposition inference) {
		return inferenceFactory_
				.getElkClassInclusionSingletonObjectUnionOfDecomposition(
						getClass("C"));
	}

	@Override
	public ElkClassInclusionTautology visit(
			ElkClassInclusionTautology inference) {
		return inferenceFactory_.getElkClassInclusionTautology(getClass("C"));
	}

	@Override
	public ElkClassInclusionTopObjectHasValue visit(
			ElkClassInclusionTopObjectHasValue inference) {
		return inferenceFactory_
				.getElkClassInclusionTopObjectHasValue(getIndividual("a"));
	}

	@Override
	public ElkInference visit(
			final ElkDifferentIndividualsOfDisjointClasses inference) {
		return inferenceFactory_.getElkDifferentIndividualsOfDisjointClasses(
				getIndividuals("a", inference.getDifferent().size()));
	}

	@Override
	public ElkInference visit(
			final ElkDisjointClassesIntersectionInconsistencies inference) {
		return inferenceFactory_
				.getElkDisjointClassesIntersectionInconsistencies(
						getClasses("C", inference.getExpressions().size()));
	}

	@Override
	public ElkDisjointClassesOfDifferentIndividuals visit(
			ElkDisjointClassesOfDifferentIndividuals inference) {
		return inferenceFactory_.getElkDisjointClassesOfDifferentIndividuals(
				getIndividuals("a", inference.getDifferent().size()));
	}

	@Override
	public ElkDisjointClassesOfDisjointUnion visit(
			ElkDisjointClassesOfDisjointUnion inference) {
		return inferenceFactory_.getElkDisjointClassesOfDisjointUnion(
				getClass("C"), getClasses("D", inference.getDisjoint().size()));
	}

	@Override
	public ElkEquivalentClassesCycle visit(
			ElkEquivalentClassesCycle inference) {
		return inferenceFactory_.getElkEquivalentClassesCycle(
				getClasses("C", inference.getExpressions().size()));
	}

	@Override
	public ElkEquivalentClassesObjectHasValue visit(
			ElkEquivalentClassesObjectHasValue inference) {
		return inferenceFactory_.getElkEquivalentClassesObjectHasValue(
				getObjectProperty("R"), getIndividual("a"));
	}

	@Override
	public ElkEquivalentClassesObjectOneOf visit(
			ElkEquivalentClassesObjectOneOf inference) {
		return inferenceFactory_.getElkEquivalentClassesObjectOneOf(
				getIndividuals("a", inference.getMembers().size()));
	}

	@Override
	public ElkEquivalentClassesOfDisjointUnion visit(
			ElkEquivalentClassesOfDisjointUnion inference) {
		return inferenceFactory_.getElkEquivalentClassesOfDisjointUnion(
				getClass("C"), getClasses("D", inference.getDisjoint().size()));
	}

	@Override
	public ElkEquivalentClassesOfSameIndividual visit(
			ElkEquivalentClassesOfSameIndividual inference) {
		return inferenceFactory_.getElkEquivalentClassesOfSameIndividual(
				getIndividuals("a", inference.getSame().size()));
	}

	@Override
	public ElkInference visit(
			final ElkObjectPropertyAssertionOfClassInclusion inference) {
		return inferenceFactory_.getElkObjectPropertyAssertionOfClassInclusion(
				getIndividual("a"), getObjectProperty("R"), getIndividual("b"));
	}

	@Override
	public ElkPropertyInclusionHierarchy visit(
			final ElkPropertyInclusionHierarchy inference) {
		final int hierarchySize = inference.getExpressions().size();
		return inference.getSubExpression().accept(
				new ElkSubObjectPropertyExpressionVisitor<ElkPropertyInclusionHierarchy>() {

					ElkPropertyInclusionHierarchy defaultVisit(
							ElkObjectPropertyExpression expression) {
						return inferenceFactory_
								.getElkPropertyInclusionHierarchy(
										getObjectProperty("R1"),
										getObjectProperties("R", 2,
												hierarchySize));
					}

					@Override
					public ElkPropertyInclusionHierarchy visit(
							ElkObjectInverseOf expression) {
						return defaultVisit(expression);
					}

					@Override
					public ElkPropertyInclusionHierarchy visit(
							ElkObjectProperty expression) {
						return defaultVisit(expression);
					}

					@Override
					public ElkPropertyInclusionHierarchy visit(
							ElkObjectPropertyChain expression) {
						int chainSize = expression
								.getObjectPropertyExpressions().size();
						return inferenceFactory_
								.getElkPropertyInclusionHierarchy(
										elkFactory_.getObjectPropertyChain(
												getObjectProperties("R",
														chainSize)),
										getObjectProperties("S",
												hierarchySize));
					}
				});

	}

	@Override
	public ElkPropertyInclusionOfEquivalence visit(
			ElkPropertyInclusionOfEquivalence inference) {
		return inferenceFactory_.getElkPropertyInclusionOfEquivalence(
				getObjectProperties("R", inference.getExpressions().size()),
				inference.getSubPos(), inference.getSuperPos());
	}

	@Override
	public ElkPropertyInclusionOfTransitiveObjectProperty visit(
			ElkPropertyInclusionOfTransitiveObjectProperty inference) {
		return inferenceFactory_
				.getElkPropertyInclusionOfTransitiveObjectProperty(
						getObjectProperty("R"));
	}

	@Override
	public ElkPropertyInclusionTautology visit(
			ElkPropertyInclusionTautology inference) {
		return inferenceFactory_
				.getElkPropertyInclusionTautology(getObjectProperty("R"));
	}

	@Override
	public ElkPropertyRangePropertyExpansion visit(
			ElkPropertyRangePropertyExpansion inference) {
		return inferenceFactory_.getElkPropertyRangePropertyExpansion(
				getObjectProperty("R"), getObjectProperty("S"), getClass("C"));
	}

	@Override
	public ElkInference visit(
			final ElkSameIndividualOfEquivalentClasses inference) {
		return inferenceFactory_.getElkSameIndividualOfEquivalentClasses(
				getIndividuals("a", inference.getSame().size()));
	}

	@Override
	public ElkToldAxiom visit(ElkToldAxiom inference) {
		// this inference should not be normally shown
		return null;
	}

}
