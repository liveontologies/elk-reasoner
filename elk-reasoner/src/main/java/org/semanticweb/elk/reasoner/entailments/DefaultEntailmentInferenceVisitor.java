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
package org.semanticweb.elk.reasoner.entailments;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.entailments.model.AxiomEntailmentInference;
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
import org.semanticweb.elk.reasoner.entailments.model.OntologyInconsistencyEntailmentInference;
import org.semanticweb.elk.reasoner.entailments.model.OntologyInconsistencyEntailsAnyAxiom;
import org.semanticweb.elk.reasoner.entailments.model.OwlThingInconsistencyEntailsOntologyInconsistency;
import org.semanticweb.elk.reasoner.entailments.model.SubClassInconsistencyEntailsSubClassOfAxiom;
import org.semanticweb.elk.reasoner.entailments.model.SubClassOfAxiomEntailmentInference;
import org.semanticweb.elk.reasoner.entailments.model.TopObjectPropertyInBottomEntailsOntologyInconsistency;

public class DefaultEntailmentInferenceVisitor<O>
		implements EntailmentInference.Visitor<O> {

	protected O defaultVisit(final EntailmentInference entailmentInference) {
		return null;
	}

	protected O defaultOntologyInconsistencyEntailmentInferenceVisit(
			final OntologyInconsistencyEntailmentInference ontologyInconsistencyEntailmentInference) {
		return defaultVisit(ontologyInconsistencyEntailmentInference);
	}

	protected <A extends ElkAxiom> O defaultAxiomEntailmentInferenceVisit(
			final AxiomEntailmentInference<A> axiomEntailmentInference) {
		return defaultVisit(axiomEntailmentInference);
	}

	protected O defaultSubClassOfAxiomEntailmentInferenceVisit(
			final SubClassOfAxiomEntailmentInference subClassOfAxiomEntailmentInference) {
		return defaultAxiomEntailmentInferenceVisit(
				subClassOfAxiomEntailmentInference);
	}

	@Override
	public O visit(
			final DerivedClassInclusionEntailsClassAssertionAxiom derivedClassInclusionEntailsClassAssertionAxiom) {
		return defaultAxiomEntailmentInferenceVisit(
				derivedClassInclusionEntailsClassAssertionAxiom);
	}

	@Override
	public O visit(
			final DerivedClassInclusionEntailsObjectPropertyAssertionAxiom derivedClassInclusionEntailsObjectPropertyAssertionAxiom) {
		return defaultAxiomEntailmentInferenceVisit(
				derivedClassInclusionEntailsObjectPropertyAssertionAxiom);
	}

	@Override
	public O visit(
			final DerivedClassInclusionEntailsObjectPropertyDomainAxiom derivedClassInclusionEntailsObjectPropertyDomainAxiom) {
		return defaultAxiomEntailmentInferenceVisit(
				derivedClassInclusionEntailsObjectPropertyDomainAxiom);
	}

	@Override
	public O visit(
			final DerivedClassInclusionEntailsSubClassOfAxiom derivedClassInclusionEntailsSubClassOfAxiom) {
		return defaultSubClassOfAxiomEntailmentInferenceVisit(
				derivedClassInclusionEntailsSubClassOfAxiom);
	}

	@Override
	public O visit(
			final EntailedClassInclusionCycleEntailsEquivalentClassesAxiom derivedClassInclusionCycleEntailsEquivalentClassesAxiom) {
		return defaultAxiomEntailmentInferenceVisit(
				derivedClassInclusionCycleEntailsEquivalentClassesAxiom);
	}

	@Override
	public O visit(
			final EntailedDisjointClassesEntailsDifferentIndividualsAxiom entailedDisjointClassesEntailsDifferentIndividualsAxiom) {
		return defaultAxiomEntailmentInferenceVisit(
				entailedDisjointClassesEntailsDifferentIndividualsAxiom);
	}

	@Override
	public O visit(
			final EntailedEquivalentClassesEntailsSameIndividualAxiom entailedEquivalentClassesEntailsSameIndividualAxiom) {
		return defaultAxiomEntailmentInferenceVisit(
				entailedEquivalentClassesEntailsSameIndividualAxiom);
	}

	@Override
	public O visit(
			final EntailedIntersectionInconsistencyEntailsDisjointClassesAxiom derivedIntersectionInconsistencyEntailsDisjointClassesAxiom) {
		return defaultAxiomEntailmentInferenceVisit(
				derivedIntersectionInconsistencyEntailsDisjointClassesAxiom);
	}

	@Override
	public O visit(
			final IndividualInconsistencyEntailsOntologyInconsistency individualInconsistencyEntailsOntologyInconsistency) {
		return defaultOntologyInconsistencyEntailmentInferenceVisit(
				individualInconsistencyEntailsOntologyInconsistency);
	}

	@Override
	public O visit(
			final OntologyInconsistencyEntailsAnyAxiom ontologyInconsistencyEntailsAnyAxiom) {
		return defaultAxiomEntailmentInferenceVisit(
				ontologyInconsistencyEntailsAnyAxiom);
	}

	@Override
	public O visit(
			final OwlThingInconsistencyEntailsOntologyInconsistency owlThingInconsistencyEntailsOntologyInconsistency) {
		return defaultOntologyInconsistencyEntailmentInferenceVisit(
				owlThingInconsistencyEntailsOntologyInconsistency);
	}

	@Override
	public O visit(
			final SubClassInconsistencyEntailsSubClassOfAxiom subClassInconsistencyEntailsSubClassOfAxiom) {
		return defaultSubClassOfAxiomEntailmentInferenceVisit(
				subClassInconsistencyEntailsSubClassOfAxiom);
	}

	@Override
	public O visit(
			final TopObjectPropertyInBottomEntailsOntologyInconsistency topObjectPropertyInBottomEntailsOntologyInconsistency) {
		return defaultOntologyInconsistencyEntailmentInferenceVisit(
				topObjectPropertyInBottomEntailsOntologyInconsistency);
	}

}
