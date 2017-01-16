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

import java.util.Arrays;
import java.util.List;

import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.exceptions.ElkRuntimeException;
import org.semanticweb.elk.matching.Matcher;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasValue;
import org.semanticweb.elk.owl.interfaces.ElkObjectOneOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.visitors.DummyElkAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.entailments.model.DerivedClassInclusionCycleEntailsEquivalentClassesAxiom;
import org.semanticweb.elk.reasoner.entailments.model.DerivedClassInclusionEntailsClassAssertionAxiom;
import org.semanticweb.elk.reasoner.entailments.model.DerivedClassInclusionEntailsSubClassOfAxiom;
import org.semanticweb.elk.reasoner.entailments.model.Entailment;
import org.semanticweb.elk.reasoner.entailments.model.EntailmentInference;
import org.semanticweb.elk.reasoner.entailments.model.EntailmentInferenceSet;
import org.semanticweb.elk.reasoner.entailments.model.IndividualInconsistencyEntailsOntologyInconsistency;
import org.semanticweb.elk.reasoner.entailments.model.OntologyInconsistency;
import org.semanticweb.elk.reasoner.entailments.model.OntologyInconsistencyEntailsAnyAxiom;
import org.semanticweb.elk.reasoner.entailments.model.OwlThingInconsistencyEntailsOntologyInconsistency;
import org.semanticweb.elk.reasoner.entailments.model.SubClassInconsistencyEntailsSubClassOfAxiom;
import org.semanticweb.elk.reasoner.entailments.model.SubClassOfAxiomEntailment;
import org.semanticweb.elk.reasoner.entailments.model.TopObjectPropertyInBottomEntailsOntologyInconsistency;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassInconsistency;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubPropertyChain;

public class ElkProofGenerator implements EntailmentInference.Visitor<Void> {

	private final EntailmentInferenceSet evidence_;

	private final Reasoner reasoner_;

	private final ElkObject.Factory elkFactory_;

	private final ElkInference.Factory inferenceFactory_;

	private ElkException caughtException_;

	public ElkProofGenerator(final EntailmentInferenceSet evidence,
			final Reasoner reasoner, final ElkObject.Factory elkFactory,
			final ElkInference.Factory inferenceFactory) {
		this.evidence_ = evidence;
		this.reasoner_ = reasoner;
		this.elkFactory_ = elkFactory;
		this.inferenceFactory_ = inferenceFactory;
		this.caughtException_ = null;
	}

	public ElkProofGenerator(final EntailmentInferenceSet evidence,
			final Reasoner reasoner,
			final ElkInference.Factory inferenceFactory) {
		this(evidence, reasoner, reasoner.getElkFactory(), inferenceFactory);
	}

	public void generate(final Entailment goalEntailment) throws ElkException {
		for (final EntailmentInference inf : evidence_
				.getInferences(goalEntailment)) {
			inf.accept(this);
		}
		if (caughtException_ != null) {
			throw caughtException_;
		}
	}

	@Override
	public Void visit(
			final OwlThingInconsistencyEntailsOntologyInconsistency entailmentInference) {
		try {
			final ClassInconsistency conclusion = entailmentInference
					.getReason();
			final Matcher matcher = new Matcher(
					reasoner_.explainConclusion(conclusion), elkFactory_,
					inferenceFactory_);
			matcher.trace(conclusion, elkFactory_.getOwlThing());
			inferenceFactory_.getElkClassInclusionHierarchy(
					elkFactory_.getOwlThing(), elkFactory_.getOwlNothing());
		} catch (final ElkException e) {
			caughtException_ = e;
		}
		return null;
	}

	@Override
	public Void visit(
			final TopObjectPropertyInBottomEntailsOntologyInconsistency entailmentInference) {
		final SubPropertyChain conclusion = entailmentInference.getReason();
		try {

			final Matcher matcher = new Matcher(
					reasoner_.explainConclusion(conclusion), elkFactory_,
					inferenceFactory_);
			matcher.trace(conclusion, elkFactory_.getOwlTopObjectProperty(),
					elkFactory_.getOwlBottomObjectProperty());

			inferenceFactory_.getElkClassInclusionOwlTopObjectProperty();
			inferenceFactory_.getElkClassInclusionExistentialOfObjectHasSelf(
					elkFactory_.getOwlThing(),
					elkFactory_.getOwlTopObjectProperty());
			inferenceFactory_.getElkClassInclusionExistentialComposition(
					Arrays.asList(elkFactory_.getOwlThing(),
							elkFactory_.getOwlThing()),
					Arrays.asList(elkFactory_.getOwlTopObjectProperty()),
					elkFactory_.getOwlBottomObjectProperty());

			inferenceFactory_.getElkClassInclusionOwlBottomObjectProperty();
			inferenceFactory_.getElkClassInclusionHierarchy(
					elkFactory_.getOwlThing(),
					elkFactory_.getObjectSomeValuesFrom(
							elkFactory_.getOwlBottomObjectProperty(),
							elkFactory_.getOwlThing()),
					elkFactory_.getOwlNothing());

		} catch (final ElkException e) {
			caughtException_ = e;
		}
		return null;
	}

	@Override
	public Void visit(
			final IndividualInconsistencyEntailsOntologyInconsistency entailmentInference) {
		try {
			final ClassInconsistency conclusion = entailmentInference
					.getReason();
			final ElkIndividual entity = entailmentInference.getIndividual();

			final Matcher matcher = new Matcher(
					reasoner_.explainConclusion(conclusion), elkFactory_,
					inferenceFactory_);
			matcher.trace(conclusion, entity);

			inferenceFactory_.getElkClassInclusionTopObjectHasValue(entity);

			inferenceFactory_.getElkEquivalentClassesObjectHasValue(
					elkFactory_.getOwlTopObjectProperty(), entity);
			final ElkObjectHasValue hasValue = elkFactory_.getObjectHasValue(
					elkFactory_.getOwlTopObjectProperty(), entity);
			final ElkObjectOneOf nominal = elkFactory_.getObjectOneOf(entity);
			final ElkObjectSomeValuesFrom existential = elkFactory_
					.getObjectSomeValuesFrom(
							elkFactory_.getOwlTopObjectProperty(), nominal);
			inferenceFactory_.getElkClassInclusionOfEquivaletClasses(hasValue,
					existential, true);

			inferenceFactory_.getElkClassInclusionExistentialFillerExpansion(
					elkFactory_.getOwlTopObjectProperty(), nominal,
					elkFactory_.getOwlNothing());

			inferenceFactory_.getElkClassInclusionExistentialOwlNothing(
					elkFactory_.getOwlTopObjectProperty());

			inferenceFactory_.getElkClassInclusionHierarchy(
					elkFactory_.getOwlThing(), hasValue, existential,
					elkFactory_.getObjectSomeValuesFrom(
							elkFactory_.getOwlTopObjectProperty(),
							elkFactory_.getOwlNothing()),
					elkFactory_.getOwlNothing());

		} catch (final ElkException e) {
			caughtException_ = e;
		}
		return null;
	}

	@Override
	public Void visit(
			final OntologyInconsistencyEntailsAnyAxiom entailmentInference) {

		// Show that ⊤ ⊑ ⊥
		for (final OntologyInconsistency premise : entailmentInference
				.getPremises()) {
			for (final EntailmentInference inf : evidence_
					.getInferences(premise)) {
				inf.accept(this);
			}
		}

		// Show how the axiom follows from that.
		final ElkAxiomVisitor<Void> axiomVisitor = new DummyElkAxiomVisitor<Void>() {

			@Override
			public Void defaultVisit(final ElkAxiom axiom) {
				throw new ElkRuntimeException(
						"Cannot generate proof for " + axiom);
			}

			@Override
			public Void visit(final ElkSubClassOfAxiom axiom) {
				final ElkClassExpression subClass = axiom
						.getSubClassExpression();
				final ElkClassExpression superClass = axiom
						.getSuperClassExpression();
				inferenceFactory_.getElkClassInclusionOwlThing(subClass);
				inferenceFactory_.getElkClassInclusionOwlNothing(superClass);
				inferenceFactory_.getElkClassInclusionHierarchy(subClass,
						elkFactory_.getOwlThing(), elkFactory_.getOwlNothing(),
						superClass);
				return null;
			}

			@Override
			public Void visit(final ElkEquivalentClassesAxiom axiom) {
				final List<? extends ElkClassExpression> equivalent = axiom
						.getClassExpressions();
				ElkClassExpression subClass = equivalent
						.get(equivalent.size() - 1);
				for (final ElkClassExpression superClass : equivalent) {
					inferenceFactory_.getElkClassInclusionOwlThing(subClass);
					inferenceFactory_
							.getElkClassInclusionOwlNothing(superClass);
					inferenceFactory_.getElkClassInclusionHierarchy(subClass,
							elkFactory_.getOwlThing(),
							elkFactory_.getOwlNothing(), superClass);
					subClass = superClass;
				}
				inferenceFactory_.getElkEquivalentClassesCycle(equivalent);
				return null;
			}

			@Override
			public Void visit(final ElkClassAssertionAxiom axiom) {
				final ElkIndividual instance = axiom.getIndividual();
				final ElkClassExpression nominal = elkFactory_
						.getObjectOneOf(instance);
				final ElkClassExpression type = axiom.getClassExpression();
				inferenceFactory_.getElkClassInclusionOwlThing(nominal);
				inferenceFactory_.getElkClassInclusionOwlNothing(type);
				inferenceFactory_.getElkClassInclusionHierarchy(nominal,
						elkFactory_.getOwlThing(), elkFactory_.getOwlNothing(),
						type);
				inferenceFactory_.getElkClassAssertionOfClassInclusion(instance,
						type);
				return null;
			}

		};

		entailmentInference.getConclusion().getAxiom().accept(axiomVisitor);

		return null;
	}

	@Override
	public Void visit(
			final SubClassInconsistencyEntailsSubClassOfAxiom entailmentInference) {
		try {
			final ClassInconsistency conclusion = entailmentInference
					.getReason();
			final ElkClassExpression subClass = entailmentInference
					.getConclusion().getAxiom().getSubClassExpression();
			final ElkClassExpression superClass = entailmentInference
					.getConclusion().getAxiom().getSuperClassExpression();
			final Matcher matcher = new Matcher(
					reasoner_.explainConclusion(conclusion), elkFactory_,
					inferenceFactory_);
			matcher.trace(conclusion, subClass);
			inferenceFactory_.getElkClassInclusionOwlNothing(superClass);
			inferenceFactory_.getElkClassInclusionHierarchy(subClass,
					elkFactory_.getOwlNothing(), superClass);
		} catch (final ElkException e) {
			caughtException_ = e;
		}
		return null;
	}

	@Override
	public Void visit(
			final DerivedClassInclusionEntailsSubClassOfAxiom entailmentInference) {
		try {
			final SubClassInclusionComposed conclusion = entailmentInference
					.getReason();
			final ElkClassExpression subClass = entailmentInference
					.getConclusion().getAxiom().getSubClassExpression();
			final ElkClassExpression superClass = entailmentInference
					.getConclusion().getAxiom().getSuperClassExpression();
			final Matcher matcher = new Matcher(
					reasoner_.explainConclusion(conclusion), elkFactory_,
					inferenceFactory_);
			matcher.trace(conclusion, subClass, superClass);
		} catch (final ElkException e) {
			caughtException_ = e;
		}
		return null;
	}

	@Override
	public Void visit(
			final DerivedClassInclusionCycleEntailsEquivalentClassesAxiom entailmentInference) {
		for (final SubClassOfAxiomEntailment premise : entailmentInference
				.getPremises()) {
			for (final EntailmentInference inf : evidence_
					.getInferences(premise)) {
				inf.accept(this);
			}
		}
		inferenceFactory_.getElkEquivalentClassesCycle(entailmentInference
				.getConclusion().getAxiom().getClassExpressions());
		return null;
	}

	@Override
	public Void visit(
			final DerivedClassInclusionEntailsClassAssertionAxiom entailmentInference) {
		try {
			final SubClassInclusionComposed conclusion = entailmentInference
					.getReason();
			final ElkIndividual instance = entailmentInference.getConclusion()
					.getAxiom().getIndividual();
			final ElkClassExpression nominal = elkFactory_
					.getObjectOneOf(instance);
			final ElkClassExpression type = entailmentInference.getConclusion()
					.getAxiom().getClassExpression();
			final Matcher matcher = new Matcher(
					reasoner_.explainConclusion(conclusion), elkFactory_,
					inferenceFactory_);
			matcher.trace(conclusion, nominal, type);
			inferenceFactory_.getElkClassAssertionOfClassInclusion(instance,
					type);
		} catch (final ElkException e) {
			caughtException_ = e;
		}
		return null;
	}

}
