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
import org.semanticweb.elk.reasoner.entailments.model.AxiomEntailment;
import org.semanticweb.elk.reasoner.entailments.model.ClassAssertionAxiomEntailment;
import org.semanticweb.elk.reasoner.entailments.model.Entailment;
import org.semanticweb.elk.reasoner.entailments.model.EquivalentClassesAxiomEntailment;
import org.semanticweb.elk.reasoner.entailments.model.OntologyInconsistency;
import org.semanticweb.elk.reasoner.entailments.model.SubClassOfAxiomEntailment;

public class DefaultEntailmentVisitor<O> implements Entailment.Visitor<O> {

	public O defaultVisit(final Entailment entailment) {
		return null;
	}

	public <A extends ElkAxiom> O defaultAxiomEntailmentVisit(
			final AxiomEntailment<A> axiomEntailment) {
		return defaultVisit(axiomEntailment);
	}

	@Override
	public O visit(final OntologyInconsistency inconsistentOntologyEntailment) {
		return defaultVisit(inconsistentOntologyEntailment);
	}

	@Override
	public O visit(final SubClassOfAxiomEntailment subClassOfAxiomEntailment) {
		return defaultAxiomEntailmentVisit(subClassOfAxiomEntailment);
	}

	@Override
	public O visit(
			final EquivalentClassesAxiomEntailment equivalentClassesAxiomEntailment) {
		return defaultAxiomEntailmentVisit(equivalentClassesAxiomEntailment);
	}

	@Override
	public O visit(
			final ClassAssertionAxiomEntailment classAssertionAxiomEntailment) {
		return defaultAxiomEntailmentVisit(classAssertionAxiomEntailment);
	}

}
