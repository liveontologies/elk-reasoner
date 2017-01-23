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

import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.reasoner.entailments.model.ClassAssertionAxiomEntailment;
import org.semanticweb.elk.reasoner.entailments.model.DifferentIndividualsAxiomEntailment;
import org.semanticweb.elk.reasoner.entailments.model.DisjointClassesAxiomEntailment;
import org.semanticweb.elk.reasoner.entailments.model.Entailment;
import org.semanticweb.elk.reasoner.entailments.model.EquivalentClassesAxiomEntailment;
import org.semanticweb.elk.reasoner.entailments.model.OntologyInconsistency;
import org.semanticweb.elk.reasoner.entailments.model.SameIndividualAxiomEntailment;
import org.semanticweb.elk.reasoner.entailments.model.SubClassOfAxiomEntailment;
import org.semanticweb.elk.util.hashing.HashGenerator;
import org.semanticweb.elk.util.hashing.Hasher;

class EntailmentHasher
		implements Hasher<Entailment>, Entailment.Visitor<Integer> {

	private static final EntailmentHasher INSTANCE = new EntailmentHasher();

	private EntailmentHasher() {
		// forbid instantiation
	}

	public static int hashCode(final Entailment entailment) {
		return entailment == null ? 0 : entailment.accept(INSTANCE);
	}

	private static int combinedHashCode(final int... hashes) {
		return HashGenerator.combineListHash(hashes);
	}

	private static int hashCode(final Class<?> c) {
		return c.hashCode();
	}

	private static int hashCode(final ElkObject elkObject) {
		return elkObject.hashCode();
	}

	@Override
	public int hash(final Entailment entailment) {
		return hashCode(entailment);
	}

	@Override
	public Integer visit(
			final ClassAssertionAxiomEntailment classAssertionAxiomEntailment) {
		return combinedHashCode(hashCode(ClassAssertionAxiomEntailment.class),
				hashCode(classAssertionAxiomEntailment.getAxiom()));
	}

	@Override
	public Integer visit(
			final DifferentIndividualsAxiomEntailment differentIndividualsEntailment) {
		return combinedHashCode(
				hashCode(DifferentIndividualsAxiomEntailment.class),
				hashCode(differentIndividualsEntailment.getAxiom()));
	}

	@Override
	public Integer visit(
			final DisjointClassesAxiomEntailment disjointClassesAxiomEntailment) {
		return combinedHashCode(hashCode(DisjointClassesAxiomEntailment.class),
				hashCode(disjointClassesAxiomEntailment.getAxiom()));
	}

	@Override
	public Integer visit(
			final EquivalentClassesAxiomEntailment equivalentClassesAxiomEntailment) {
		return combinedHashCode(
				hashCode(EquivalentClassesAxiomEntailment.class),
				hashCode(equivalentClassesAxiomEntailment.getAxiom()));
	}

	@Override
	public Integer visit(
			final OntologyInconsistency inconsistentOntologyEntailment) {
		return combinedHashCode(hashCode(OntologyInconsistency.class));
	}

	@Override
	public Integer visit(
			final SameIndividualAxiomEntailment sameIndividualAxiomEntailment) {
		return combinedHashCode(hashCode(SameIndividualAxiomEntailment.class),
				hashCode(sameIndividualAxiomEntailment.getAxiom()));
	}

	@Override
	public Integer visit(
			final SubClassOfAxiomEntailment subClassOfAxiomEntailment) {
		return combinedHashCode(hashCode(SubClassOfAxiomEntailment.class),
				hashCode(subClassOfAxiomEntailment.getAxiom()));
	}

}
