/*-
 * #%L
 * ELK Reasoner Core
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
package org.semanticweb.elk.reasoner.entailments.impl;

import org.semanticweb.elk.reasoner.entailments.model.EntailedClassInclusionCycleEntailsEquivalentClassesAxiom;
import org.semanticweb.elk.reasoner.entailments.model.EntailedDisjointClassesEntailsDifferentIndividualsAxiom;
import org.semanticweb.elk.reasoner.entailments.model.DerivedClassInclusionEntailsClassAssertionAxiom;
import org.semanticweb.elk.reasoner.entailments.model.DerivedClassInclusionEntailsObjectPropertyAssertionAxiom;
import org.semanticweb.elk.reasoner.entailments.model.DerivedClassInclusionEntailsObjectPropertyDomainAxiom;
import org.semanticweb.elk.reasoner.entailments.model.DerivedClassInclusionEntailsSubClassOfAxiom;
import org.semanticweb.elk.reasoner.entailments.model.EntailedIntersectionInconsistencyEntailsDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.entailments.model.EntailedEquivalentClassesEntailsSameIndividualAxiom;
import org.semanticweb.elk.reasoner.entailments.model.EntailmentInference;
import org.semanticweb.elk.reasoner.entailments.model.IndividualInconsistencyEntailsOntologyInconsistency;
import org.semanticweb.elk.reasoner.entailments.model.OntologyInconsistencyEntailsAnyAxiom;
import org.semanticweb.elk.reasoner.entailments.model.OwlThingInconsistencyEntailsOntologyInconsistency;
import org.semanticweb.elk.reasoner.entailments.model.SubClassInconsistencyEntailsSubClassOfAxiom;
import org.semanticweb.elk.reasoner.entailments.model.TopObjectPropertyInBottomEntailsOntologyInconsistency;
import org.semanticweb.elk.util.hashing.HashGenerator;
import org.semanticweb.elk.util.hashing.Hasher;

class EntailmentInferenceHasher implements Hasher<EntailmentInference>,
		EntailmentInference.Visitor<Integer> {

	private static final EntailmentInferenceHasher INSTANCE = new EntailmentInferenceHasher();

	private EntailmentInferenceHasher() {
		// forbid instantiation
	}

	public static int hashCode(final EntailmentInference entailment) {
		return entailment == null ? 0 : entailment.accept(INSTANCE);
	}

	private static int combinedHashCode(final int... hashes) {
		return HashGenerator.combineListHash(hashes);
	}

	private static int hashCode(final Class<?> c) {
		return c.hashCode();
	}

	private static int hashCode(final Object object) {
		return object.hashCode();
	}

	@Override
	public int hash(final EntailmentInference entailmentInference) {
		return hashCode(entailmentInference);
	}

	@Override
	public Integer visit(
			final DerivedClassInclusionEntailsClassAssertionAxiom derivedClassInclusionEntailsClassAssertionAxiom) {
		return combinedHashCode(
				hashCode(DerivedClassInclusionEntailsClassAssertionAxiom.class),
				hashCode(derivedClassInclusionEntailsClassAssertionAxiom
						.getConclusion()),
				hashCode(derivedClassInclusionEntailsClassAssertionAxiom
						.getReason()));
	}

	@Override
	public Integer visit(
			final DerivedClassInclusionEntailsObjectPropertyAssertionAxiom derivedClassInclusionEntailsObjectPropertyAssertionAxiom) {
		return combinedHashCode(
				hashCode(
						DerivedClassInclusionEntailsObjectPropertyAssertionAxiom.class),
				hashCode(
						derivedClassInclusionEntailsObjectPropertyAssertionAxiom
								.getConclusion()),
				hashCode(
						derivedClassInclusionEntailsObjectPropertyAssertionAxiom
								.getReason()));
	}

	@Override
	public Integer visit(
			final DerivedClassInclusionEntailsObjectPropertyDomainAxiom derivedClassInclusionEntailsObjectPropertyDomainAxiom) {
		return combinedHashCode(
				hashCode(
						DerivedClassInclusionEntailsObjectPropertyDomainAxiom.class),
				hashCode(derivedClassInclusionEntailsObjectPropertyDomainAxiom
						.getConclusion()),
				hashCode(derivedClassInclusionEntailsObjectPropertyDomainAxiom
						.getReason()));
	}

	@Override
	public Integer visit(
			final DerivedClassInclusionEntailsSubClassOfAxiom derivedClassInclusionEntailsSubClassOfAxiom) {
		return combinedHashCode(
				hashCode(DerivedClassInclusionEntailsSubClassOfAxiom.class),
				hashCode(derivedClassInclusionEntailsSubClassOfAxiom
						.getConclusion()),
				hashCode(derivedClassInclusionEntailsSubClassOfAxiom
						.getReason()));
	}

	@Override
	public Integer visit(
			final EntailedClassInclusionCycleEntailsEquivalentClassesAxiom derivedClassInclusionCycleEntailsEquivalentClassesAxiom) {
		return combinedHashCode(
				hashCode(
						EntailedClassInclusionCycleEntailsEquivalentClassesAxiom.class),
				hashCode(derivedClassInclusionCycleEntailsEquivalentClassesAxiom
						.getConclusion()),
				hashCode(derivedClassInclusionCycleEntailsEquivalentClassesAxiom
						.getPremises()));
	}

	@Override
	public Integer visit(
			final EntailedDisjointClassesEntailsDifferentIndividualsAxiom entailedDisjointClassesEntailsDifferentIndividualsAxiom) {
		return combinedHashCode(
				hashCode(
						EntailedDisjointClassesEntailsDifferentIndividualsAxiom.class),
				hashCode(entailedDisjointClassesEntailsDifferentIndividualsAxiom
						.getConclusion()),
				hashCode(entailedDisjointClassesEntailsDifferentIndividualsAxiom
						.getPremises()));
	}

	@Override
	public Integer visit(
			final EntailedEquivalentClassesEntailsSameIndividualAxiom entailedEquivalentClassesEntailsSameIndividualAxiom) {
		return combinedHashCode(
				hashCode(
						EntailedEquivalentClassesEntailsSameIndividualAxiom.class),
				hashCode(entailedEquivalentClassesEntailsSameIndividualAxiom
						.getConclusion()),
				hashCode(entailedEquivalentClassesEntailsSameIndividualAxiom
						.getPremises()));
	}

	@Override
	public Integer visit(
			final EntailedIntersectionInconsistencyEntailsDisjointClassesAxiom derivedIntersectionInconsistencyEntailsDisjointClassesAxiom) {
		return combinedHashCode(
				hashCode(
						EntailedIntersectionInconsistencyEntailsDisjointClassesAxiom.class),
				hashCode(
						derivedIntersectionInconsistencyEntailsDisjointClassesAxiom
								.getConclusion()),
				hashCode(
						derivedIntersectionInconsistencyEntailsDisjointClassesAxiom
								.getPremises()));
	}

	@Override
	public Integer visit(
			final IndividualInconsistencyEntailsOntologyInconsistency individualInconsistencyEntailsOntologyInconsistency) {
		return combinedHashCode(
				hashCode(
						IndividualInconsistencyEntailsOntologyInconsistency.class),
				hashCode(individualInconsistencyEntailsOntologyInconsistency
						.getReason()),
				hashCode(individualInconsistencyEntailsOntologyInconsistency
						.getIndividual()));
	}

	@Override
	public Integer visit(
			final OntologyInconsistencyEntailsAnyAxiom ontologyInconsistencyEntailsAnyAxiom) {
		return combinedHashCode(
				hashCode(OntologyInconsistencyEntailsAnyAxiom.class),
				hashCode(ontologyInconsistencyEntailsAnyAxiom.getConclusion()));
	}

	@Override
	public Integer visit(
			final OwlThingInconsistencyEntailsOntologyInconsistency owlThingInconsistencyEntailsOntologyInconsistency) {
		return combinedHashCode(
				hashCode(
						OwlThingInconsistencyEntailsOntologyInconsistency.class),
				hashCode(owlThingInconsistencyEntailsOntologyInconsistency
						.getReason()));
	}

	@Override
	public Integer visit(
			final SubClassInconsistencyEntailsSubClassOfAxiom subClassInconsistencyEntailsSubClassOfAxiom) {
		return combinedHashCode(
				hashCode(SubClassInconsistencyEntailsSubClassOfAxiom.class),
				hashCode(subClassInconsistencyEntailsSubClassOfAxiom
						.getConclusion()),
				hashCode(subClassInconsistencyEntailsSubClassOfAxiom
						.getReason()));
	}

	@Override
	public Integer visit(
			final TopObjectPropertyInBottomEntailsOntologyInconsistency topObjectPropertyInBottomEntailsOntologyInconsistency) {
		return combinedHashCode(
				hashCode(
						TopObjectPropertyInBottomEntailsOntologyInconsistency.class),
				hashCode(topObjectPropertyInBottomEntailsOntologyInconsistency
						.getReason()));
	}

}
