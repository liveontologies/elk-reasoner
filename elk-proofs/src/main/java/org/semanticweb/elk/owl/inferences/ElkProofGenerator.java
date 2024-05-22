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
import java.util.Arrays;
import java.util.List;

import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.exceptions.ElkRuntimeException;
import org.semanticweb.elk.matching.Matcher;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkDifferentIndividualsAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasValue;
import org.semanticweb.elk.owl.interfaces.ElkObjectOneOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkSameIndividualAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.visitors.DummyElkAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.entailments.model.DerivedClassInclusionEntailsClassAssertionAxiom;
import org.semanticweb.elk.reasoner.entailments.model.DerivedClassInclusionEntailsObjectPropertyAssertionAxiom;
import org.semanticweb.elk.reasoner.entailments.model.DerivedClassInclusionEntailsObjectPropertyDomainAxiom;
import org.semanticweb.elk.reasoner.entailments.model.DerivedClassInclusionEntailsSubClassOfAxiom;
import org.semanticweb.elk.reasoner.entailments.model.DisjointClassesAxiomEntailment;
import org.semanticweb.elk.reasoner.entailments.model.EntailedClassInclusionCycleEntailsEquivalentClassesAxiom;
import org.semanticweb.elk.reasoner.entailments.model.EntailedDisjointClassesEntailsDifferentIndividualsAxiom;
import org.semanticweb.elk.reasoner.entailments.model.EntailedEquivalentClassesEntailsSameIndividualAxiom;
import org.semanticweb.elk.reasoner.entailments.model.EntailedIntersectionInconsistencyEntailsDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.entailments.model.Entailment;
import org.semanticweb.elk.reasoner.entailments.model.EntailmentInference;
import org.semanticweb.elk.reasoner.entailments.model.EquivalentClassesAxiomEntailment;
import org.semanticweb.elk.reasoner.entailments.model.IndividualInconsistencyEntailsOntologyInconsistency;
import org.semanticweb.elk.reasoner.entailments.model.OntologyInconsistency;
import org.semanticweb.elk.reasoner.entailments.model.OntologyInconsistencyEntailsAnyAxiom;
import org.semanticweb.elk.reasoner.entailments.model.OwlThingInconsistencyEntailsOntologyInconsistency;
import org.semanticweb.elk.reasoner.entailments.model.SubClassInconsistencyEntailsSubClassOfAxiom;
import org.semanticweb.elk.reasoner.entailments.model.SubClassOfAxiomEntailment;
import org.semanticweb.elk.reasoner.entailments.model.TopObjectPropertyInBottomEntailsOntologyInconsistency;
import org.semanticweb.elk.reasoner.proof.ReasonerProof;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassInconsistency;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubPropertyChain;

public class ElkProofGenerator implements EntailmentInference.Visitor<Void> {

	private final ReasonerProof<? extends EntailmentInference> evidence_;

	private final Reasoner reasoner_;

	private final ElkObject.Factory elkFactory_;

	private final ElkInference.Factory inferenceFactory_;

	public ElkProofGenerator(final ReasonerProof<? extends EntailmentInference> evidence,
			final Reasoner reasoner, final ElkObject.Factory elkFactory,
			final ElkInference.Factory inferenceFactory) {
		this.evidence_ = evidence;
		this.reasoner_ = reasoner;
		this.elkFactory_ = elkFactory;
		this.inferenceFactory_ = inferenceFactory;
	}

	public ElkProofGenerator(final ReasonerProof<? extends EntailmentInference> evidence,
			final Reasoner reasoner,
			final ElkInference.Factory inferenceFactory) {
		this(evidence, reasoner, reasoner.getElkFactory(), inferenceFactory);
	}

	public void generate(final Entailment goalEntailment) throws ElkException {
		try {
			for (final EntailmentInference inf : evidence_
					.getInferences(goalEntailment)) {
				inf.accept(this);
			}
		} catch (final TunnelingException e) {
			final ElkException elkException = e.getElkException();
			if (elkException != null) {
				throw e.getElkException();
			}
		}
	}

	@Override
	public Void visit(
			final DerivedClassInclusionEntailsClassAssertionAxiom entailmentInference) {
		final SubClassInclusionComposed conclusion = entailmentInference
				.getReason();
		final ElkIndividual instance = entailmentInference.getConclusion()
				.getAxiom().getIndividual();
		final ElkClassExpression nominal = elkFactory_.getObjectOneOf(instance);
		final ElkClassExpression type = entailmentInference.getConclusion()
				.getAxiom().getClassExpression();
		final Matcher matcher = new Matcher(reasoner_.getProof(), elkFactory_,
				inferenceFactory_);
		matcher.trace(conclusion, nominal, type);
		inferenceFactory_.getElkClassAssertionOfClassInclusion(instance, type);
		return null;
	}

	@Override
	public Void visit(
			final DerivedClassInclusionEntailsObjectPropertyAssertionAxiom entailmentInference) {

		final SubClassInclusionComposed conclusion = entailmentInference
				.getReason();

		final ElkIndividual subject = entailmentInference.getConclusion()
				.getAxiom().getSubject();
		final ElkObjectPropertyExpression property = entailmentInference
				.getConclusion().getAxiom().getProperty();
		final ElkIndividual object = entailmentInference.getConclusion()
				.getAxiom().getObject();

		final ElkClassExpression subClass = elkFactory_.getObjectOneOf(subject);
		final ElkClassExpression superClass = elkFactory_
				.getObjectSomeValuesFrom(property,
						elkFactory_.getObjectOneOf(object));

		final Matcher matcher = new Matcher(reasoner_.getProof(), elkFactory_,
				inferenceFactory_);
		matcher.trace(conclusion, subClass, superClass);

		inferenceFactory_.getElkObjectPropertyAssertionOfClassInclusion(subject,
				property, object);

		return null;
	}

	@Override
	public Void visit(
			final DerivedClassInclusionEntailsObjectPropertyDomainAxiom entailmentInference) {

		final SubClassInclusionComposed conclusion = entailmentInference
				.getReason();

		final ElkObjectPropertyExpression property = entailmentInference
				.getConclusion().getAxiom().getProperty();
		final ElkClassExpression domain = entailmentInference.getConclusion()
				.getAxiom().getDomain();

		final ElkClassExpression subClass = elkFactory_
				.getObjectSomeValuesFrom(property, elkFactory_.getOwlThing());

		final Matcher matcher = new Matcher(reasoner_.getProof(), elkFactory_,
				inferenceFactory_);
		matcher.trace(conclusion, subClass, domain);

		inferenceFactory_.getElkObjectPropertyDomainOfClassInclusion(property,
				domain);

		return null;
	}

	@Override
	public Void visit(
			final DerivedClassInclusionEntailsSubClassOfAxiom entailmentInference) {
		final SubClassInclusionComposed conclusion = entailmentInference
				.getReason();
		final ElkClassExpression subClass = entailmentInference.getConclusion()
				.getAxiom().getSubClassExpression();
		final ElkClassExpression superClass = entailmentInference
				.getConclusion().getAxiom().getSuperClassExpression();
		final Matcher matcher = new Matcher(reasoner_.getProof(), elkFactory_,
				inferenceFactory_);
		matcher.trace(conclusion, subClass, superClass);
		return null;
	}

	@Override
	public Void visit(
			final EntailedClassInclusionCycleEntailsEquivalentClassesAxiom entailmentInference) {
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
			final EntailedDisjointClassesEntailsDifferentIndividualsAxiom entailmentInference) {
		for (final DisjointClassesAxiomEntailment premise : entailmentInference
				.getPremises()) {
			for (final EntailmentInference inf : evidence_
					.getInferences(premise)) {
				inf.accept(this);
			}
		}
		inferenceFactory_
				.getElkDifferentIndividualsOfDisjointClasses(entailmentInference
						.getConclusion().getAxiom().getIndividuals());
		return null;
	}

	@Override
	public Void visit(
			final EntailedEquivalentClassesEntailsSameIndividualAxiom entailmentInference) {
		for (final EquivalentClassesAxiomEntailment premise : entailmentInference
				.getPremises()) {
			for (final EntailmentInference inf : evidence_
					.getInferences(premise)) {
				inf.accept(this);
			}
		}
		inferenceFactory_
				.getElkSameIndividualOfEquivalentClasses(entailmentInference
						.getConclusion().getAxiom().getIndividuals());
		return null;
	}

	@Override
	public Void visit(
			final EntailedIntersectionInconsistencyEntailsDisjointClassesAxiom entailmentInference) {
		for (final SubClassOfAxiomEntailment premise : entailmentInference
				.getPremises()) {
			for (final EntailmentInference inf : evidence_
					.getInferences(premise)) {
				inf.accept(this);
			}
		}
		inferenceFactory_.getElkDisjointClassesIntersectionInconsistencies(
				entailmentInference.getConclusion().getAxiom()
						.getClassExpressions());
		return null;
	}

	@Override
	public Void visit(
			final IndividualInconsistencyEntailsOntologyInconsistency entailmentInference) {
		final ClassInconsistency conclusion = entailmentInference.getReason();
		final ElkIndividual entity = entailmentInference.getIndividual();

		final Matcher matcher = new Matcher(reasoner_.getProof(), elkFactory_,
				inferenceFactory_);
		matcher.trace(conclusion, entity);

		inferenceFactory_.getElkClassInclusionTopObjectHasValue(entity);

		inferenceFactory_.getElkEquivalentClassesObjectHasValue(
				elkFactory_.getOwlTopObjectProperty(), entity);
		final ElkObjectHasValue hasValue = elkFactory_.getObjectHasValue(
				elkFactory_.getOwlTopObjectProperty(), entity);
		final ElkObjectOneOf nominal = elkFactory_.getObjectOneOf(entity);
		final ElkObjectSomeValuesFrom existential = elkFactory_
				.getObjectSomeValuesFrom(elkFactory_.getOwlTopObjectProperty(),
						nominal);
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

			@Override
			public Void visit(final ElkDifferentIndividualsAxiom axiom) {

				final List<? extends ElkIndividual> individuals = axiom
						.getIndividuals();
				final List<ElkClassExpression> nominals = new ArrayList<ElkClassExpression>(
						individuals.size());
				for (final ElkIndividual individual : individuals) {
					nominals.add(elkFactory_.getObjectOneOf(individual));
				}

				elkFactory_.getDisjointClassesAxiom(nominals).accept(this);

				inferenceFactory_.getElkDifferentIndividualsOfDisjointClasses(
						individuals);
				return null;
			}

			@Override
			public Void visit(final ElkDisjointClassesAxiom axiom) {
				final List<? extends ElkClassExpression> disjoint = axiom
						.getClassExpressions();
				final int size = disjoint.size();

				for (int first = 0; first < size - 1; first++) {
					for (int second = first + 1; second < size; second++) {

						final ElkClassExpression intersection = elkFactory_
								.getObjectIntersectionOf(disjoint.get(first),
										disjoint.get(second));

						inferenceFactory_
								.getElkClassInclusionOwlThing(intersection);
						inferenceFactory_.getElkClassInclusionHierarchy(
								intersection, elkFactory_.getOwlThing(),
								elkFactory_.getOwlNothing());
					}
				}

				inferenceFactory_
						.getElkDisjointClassesIntersectionInconsistencies(
								disjoint);
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
			public Void visit(final ElkObjectPropertyAssertionAxiom axiom) {

				final ElkIndividual subject = axiom.getSubject();
				final ElkObjectPropertyExpression property = axiom
						.getProperty();
				final ElkIndividual object = axiom.getObject();

				final ElkClassExpression subClass = elkFactory_
						.getObjectOneOf(subject);
				final ElkClassExpression superClass = elkFactory_
						.getObjectSomeValuesFrom(property,
								elkFactory_.getObjectOneOf(object));

				inferenceFactory_.getElkClassInclusionOwlThing(subClass);
				inferenceFactory_.getElkClassInclusionOwlNothing(superClass);
				inferenceFactory_.getElkClassInclusionHierarchy(subClass,
						elkFactory_.getOwlThing(), elkFactory_.getOwlNothing(),
						superClass);

				inferenceFactory_.getElkObjectPropertyAssertionOfClassInclusion(
						subject, property, object);
				return null;
			}

			@Override
			public Void visit(final ElkObjectPropertyDomainAxiom axiom) {

				final ElkObjectPropertyExpression property = axiom
						.getProperty();
				final ElkClassExpression domain = axiom.getDomain();

				final ElkClassExpression subClass = elkFactory_
						.getObjectSomeValuesFrom(property,
								elkFactory_.getOwlThing());

				inferenceFactory_.getElkClassInclusionOwlThing(subClass);
				inferenceFactory_.getElkClassInclusionOwlNothing(domain);
				inferenceFactory_.getElkClassInclusionHierarchy(subClass,
						elkFactory_.getOwlThing(), elkFactory_.getOwlNothing(),
						domain);

				inferenceFactory_.getElkObjectPropertyDomainOfClassInclusion(
						property, domain);
				return null;
			}

			@Override
			public Void visit(final ElkSameIndividualAxiom axiom) {

				final List<? extends ElkIndividual> individuals = axiom
						.getIndividuals();
				final List<ElkClassExpression> nominals = new ArrayList<ElkClassExpression>(
						individuals.size());
				for (final ElkIndividual individual : individuals) {
					nominals.add(elkFactory_.getObjectOneOf(individual));
				}

				elkFactory_.getEquivalentClassesAxiom(nominals).accept(this);

				inferenceFactory_
						.getElkSameIndividualOfEquivalentClasses(individuals);
				return null;
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

		};

		entailmentInference.getConclusion().getAxiom().accept(axiomVisitor);

		return null;
	}

	@Override
	public Void visit(
			final OwlThingInconsistencyEntailsOntologyInconsistency entailmentInference) {
		final ClassInconsistency conclusion = entailmentInference.getReason();
		final Matcher matcher = new Matcher(reasoner_.getProof(), elkFactory_,
				inferenceFactory_);
		matcher.trace(conclusion, elkFactory_.getOwlThing());
		inferenceFactory_.getElkClassInclusionHierarchy(
				elkFactory_.getOwlThing(), elkFactory_.getOwlNothing());
		return null;
	}

	@Override
	public Void visit(
			final SubClassInconsistencyEntailsSubClassOfAxiom entailmentInference) {
		final ClassInconsistency conclusion = entailmentInference.getReason();
		final ElkClassExpression subClass = entailmentInference.getConclusion()
				.getAxiom().getSubClassExpression();
		final ElkClassExpression superClass = entailmentInference
				.getConclusion().getAxiom().getSuperClassExpression();
		final Matcher matcher = new Matcher(reasoner_.getProof(), elkFactory_,
				inferenceFactory_);
		matcher.trace(conclusion, subClass);
		inferenceFactory_.getElkClassInclusionOwlNothing(superClass);
		inferenceFactory_.getElkClassInclusionHierarchy(subClass,
				elkFactory_.getOwlNothing(), superClass);
		return null;
	}

	@Override
	public Void visit(
			final TopObjectPropertyInBottomEntailsOntologyInconsistency entailmentInference) {
		final SubPropertyChain conclusion = entailmentInference.getReason();

		final Matcher matcher = new Matcher(reasoner_.getProof(), elkFactory_,
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

		return null;
	}

	/**
	 * A {@link RuntimeException} used for reporting {@link ElkException}s
	 * thrown in the visit methods.
	 * 
	 * @author Peter Skocovsky
	 */
	public static class TunnelingException extends RuntimeException {
		private static final long serialVersionUID = -4095792570657375629L;

		private final ElkException elkException_;

		public TunnelingException(final ElkException cause) {
			super(cause);
			this.elkException_ = cause;
		}

		/**
		 * @return The {@link ElkException} thrown in a visit method.
		 */
		public ElkException getElkException() {
			return elkException_;
		}

	}

}
