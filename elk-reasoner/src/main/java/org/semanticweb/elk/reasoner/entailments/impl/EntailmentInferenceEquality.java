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

import org.semanticweb.elk.reasoner.entailments.DefaultEntailmentInferenceVisitor;
import org.semanticweb.elk.reasoner.entailments.model.EntailedClassInclusionCycleEntailsEquivalentClassesAxiom;
import org.semanticweb.elk.reasoner.entailments.model.DerivedClassInclusionEntailsClassAssertionAxiom;
import org.semanticweb.elk.reasoner.entailments.model.DerivedClassInclusionEntailsSubClassOfAxiom;
import org.semanticweb.elk.reasoner.entailments.model.EntailedIntersectionInconsistencyEntailsDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.entailments.model.EntailedEquivalentClassesEntailsSameIndividualAxiom;
import org.semanticweb.elk.reasoner.entailments.model.EntailmentInference;
import org.semanticweb.elk.reasoner.entailments.model.IndividualInconsistencyEntailsOntologyInconsistency;
import org.semanticweb.elk.reasoner.entailments.model.OntologyInconsistencyEntailsAnyAxiom;
import org.semanticweb.elk.reasoner.entailments.model.OwlThingInconsistencyEntailsOntologyInconsistency;
import org.semanticweb.elk.reasoner.entailments.model.SubClassInconsistencyEntailsSubClassOfAxiom;
import org.semanticweb.elk.reasoner.entailments.model.TopObjectPropertyInBottomEntailsOntologyInconsistency;

class EntailmentInferenceEquality
		implements EntailmentInference.Visitor<Boolean> {

	private static class DefaultVisitor
			extends DefaultEntailmentInferenceVisitor<Boolean> {

		@Override
		public Boolean defaultVisit(final EntailmentInference entailment) {
			return false;
		}

		static boolean equals(final Object first, final Object second) {
			return first.equals(second);
		}

	}

	private final EntailmentInference other_;

	private EntailmentInferenceEquality(final EntailmentInference other) {
		this.other_ = other;
	}

	public static boolean equals(final EntailmentInference first,
			final EntailmentInference second) {
		return first.accept(new EntailmentInferenceEquality(second));
	}

	@Override
	public Boolean visit(
			final DerivedClassInclusionEntailsClassAssertionAxiom derivedClassInclusionEntailsClassAssertionAxiom) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(
					final DerivedClassInclusionEntailsClassAssertionAxiom other) {
				return equals(other.getConclusion(),
						derivedClassInclusionEntailsClassAssertionAxiom
								.getConclusion())
						&& equals(other.getReason(),
								derivedClassInclusionEntailsClassAssertionAxiom
										.getReason());
			}
		});
	}

	@Override
	public Boolean visit(
			final DerivedClassInclusionEntailsSubClassOfAxiom derivedClassInclusionEntailsSubClassOfAxiom) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(
					final DerivedClassInclusionEntailsSubClassOfAxiom other) {
				return equals(other.getConclusion(),
						derivedClassInclusionEntailsSubClassOfAxiom
								.getConclusion())
						&& equals(other.getReason(),
								derivedClassInclusionEntailsSubClassOfAxiom
										.getReason());
			}
		});
	}

	@Override
	public Boolean visit(
			final EntailedClassInclusionCycleEntailsEquivalentClassesAxiom derivedClassInclusionCycleEntailsEquivalentClassesAxiom) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(
					final EntailedClassInclusionCycleEntailsEquivalentClassesAxiom other) {
				return equals(other.getConclusion(),
						derivedClassInclusionCycleEntailsEquivalentClassesAxiom
								.getConclusion())
						&& equals(other.getPremises(),
								derivedClassInclusionCycleEntailsEquivalentClassesAxiom
										.getPremises());
			}
		});
	}

	@Override
	public Boolean visit(
			final EntailedIntersectionInconsistencyEntailsDisjointClassesAxiom derivedIntersectionInconsistencyEntailsDisjointClassesAxiom) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(
					final EntailedIntersectionInconsistencyEntailsDisjointClassesAxiom other) {
				return equals(other.getConclusion(),
						derivedIntersectionInconsistencyEntailsDisjointClassesAxiom
								.getConclusion())
						&& equals(other.getPremises(),
								derivedIntersectionInconsistencyEntailsDisjointClassesAxiom
										.getPremises());
			}
		});
	}

	@Override
	public Boolean visit(
			final EntailedEquivalentClassesEntailsSameIndividualAxiom entailedEquivalentClassesEntailsSameIndividualAxiom) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(
					final EntailedEquivalentClassesEntailsSameIndividualAxiom other) {
				return equals(other.getConclusion(),
						entailedEquivalentClassesEntailsSameIndividualAxiom
								.getConclusion())
						&& equals(other.getPremises(),
								entailedEquivalentClassesEntailsSameIndividualAxiom
										.getPremises());
			}
		});
	}

	@Override
	public Boolean visit(
			final IndividualInconsistencyEntailsOntologyInconsistency individualInconsistencyEntailsOntologyInconsistency) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(
					final IndividualInconsistencyEntailsOntologyInconsistency other) {
				return equals(other.getReason(),
						individualInconsistencyEntailsOntologyInconsistency
								.getReason())
						&& equals(other.getIndividual(),
								individualInconsistencyEntailsOntologyInconsistency
										.getIndividual());
			}
		});
	}

	@Override
	public Boolean visit(
			final OntologyInconsistencyEntailsAnyAxiom ontologyInconsistencyEntailsAnyAxiom) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(
					final OntologyInconsistencyEntailsAnyAxiom other) {
				return equals(other.getConclusion(),
						ontologyInconsistencyEntailsAnyAxiom.getConclusion());
			}
		});
	}

	@Override
	public Boolean visit(
			final OwlThingInconsistencyEntailsOntologyInconsistency owlThingInconsistencyEntailsOntologyInconsistency) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(
					final OwlThingInconsistencyEntailsOntologyInconsistency other) {
				return equals(other.getReason(),
						owlThingInconsistencyEntailsOntologyInconsistency
								.getReason());
			}
		});
	}

	@Override
	public Boolean visit(
			final SubClassInconsistencyEntailsSubClassOfAxiom subClassInconsistencyEntailsSubClassOfAxiom) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(
					final SubClassInconsistencyEntailsSubClassOfAxiom other) {
				return equals(other.getConclusion(),
						subClassInconsistencyEntailsSubClassOfAxiom
								.getConclusion())
						&& equals(other.getReason(),
								subClassInconsistencyEntailsSubClassOfAxiom
										.getReason());
			}
		});
	}

	@Override
	public Boolean visit(
			final TopObjectPropertyInBottomEntailsOntologyInconsistency topObjectPropertyInBottomEntailsOntologyInconsistency) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(
					final TopObjectPropertyInBottomEntailsOntologyInconsistency other) {
				return equals(other.getReason(),
						topObjectPropertyInBottomEntailsOntologyInconsistency
								.getReason());
			}
		});
	}

}
