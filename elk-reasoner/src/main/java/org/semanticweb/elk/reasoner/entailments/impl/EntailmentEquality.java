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

import org.semanticweb.elk.reasoner.entailments.DefaultEntailmentVisitor;
import org.semanticweb.elk.reasoner.entailments.model.ClassAssertionAxiomEntailment;
import org.semanticweb.elk.reasoner.entailments.model.DifferentIndividualsAxiomEntailment;
import org.semanticweb.elk.reasoner.entailments.model.DisjointClassesAxiomEntailment;
import org.semanticweb.elk.reasoner.entailments.model.Entailment;
import org.semanticweb.elk.reasoner.entailments.model.EquivalentClassesAxiomEntailment;
import org.semanticweb.elk.reasoner.entailments.model.OntologyInconsistency;
import org.semanticweb.elk.reasoner.entailments.model.SameIndividualAxiomEntailment;
import org.semanticweb.elk.reasoner.entailments.model.SubClassOfAxiomEntailment;

class EntailmentEquality implements Entailment.Visitor<Boolean> {

	private static class DefaultVisitor
			extends DefaultEntailmentVisitor<Boolean> {

		@Override
		public Boolean defaultVisit(final Entailment entailment) {
			return false;
		}

		static boolean equals(final Object first, final Object second) {
			return first.equals(second);
		}

	}

	private final Entailment other_;

	private EntailmentEquality(final Entailment other) {
		this.other_ = other;
	}

	public static boolean equals(final Entailment first,
			final Entailment second) {
		return first.accept(new EntailmentEquality(second));
	}

	@Override
	public Boolean visit(
			final ClassAssertionAxiomEntailment classAssertionAxiomEntailment) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(final ClassAssertionAxiomEntailment other) {
				return equals(other.getAxiom(),
						classAssertionAxiomEntailment.getAxiom());
			}
		});
	}

	@Override
	public Boolean visit(
			final DifferentIndividualsAxiomEntailment differentIndividualsEntailment) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(
					final DifferentIndividualsAxiomEntailment other) {
				return equals(other.getAxiom(),
						differentIndividualsEntailment.getAxiom());
			}
		});
	}

	@Override
	public Boolean visit(
			final DisjointClassesAxiomEntailment disjointClassesAxiomEntailment) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(final DisjointClassesAxiomEntailment other) {
				return equals(other.getAxiom(),
						disjointClassesAxiomEntailment.getAxiom());
			}
		});
	}

	@Override
	public Boolean visit(
			final EquivalentClassesAxiomEntailment equivalentClassesAxiomEntailment) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(final EquivalentClassesAxiomEntailment other) {
				return equals(other.getAxiom(),
						equivalentClassesAxiomEntailment.getAxiom());
			}
		});
	}

	@Override
	public Boolean visit(
			final OntologyInconsistency inconsistentOntologyEntailment) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(final OntologyInconsistency other) {
				return true;
			}
		});
	}

	@Override
	public Boolean visit(
			final SameIndividualAxiomEntailment sameIndividualAxiomEntailment) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(final SameIndividualAxiomEntailment other) {
				return equals(other.getAxiom(),
						sameIndividualAxiomEntailment.getAxiom());
			}
		});
	}

	@Override
	public Boolean visit(
			final SubClassOfAxiomEntailment subClassOfAxiomEntailment) {
		return other_.accept(new DefaultVisitor() {
			@Override
			public Boolean visit(final SubClassOfAxiomEntailment other) {
				return equals(other.getAxiom(),
						subClassOfAxiomEntailment.getAxiom());
			}
		});
	}

}
