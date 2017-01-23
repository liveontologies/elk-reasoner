package org.semanticweb.elk.reasoner.entailments.impl;

/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2017 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.entailments.DefaultEntailmentVisitor;
import org.semanticweb.elk.reasoner.entailments.model.AxiomEntailment;
import org.semanticweb.elk.reasoner.entailments.model.Entailment;
import org.semanticweb.elk.reasoner.entailments.model.OntologyInconsistency;

class EntailmentPrinter extends DefaultEntailmentVisitor<String> {

	public static final EntailmentPrinter INSTANCE = new EntailmentPrinter();

	public static String toString(final Entailment entailment) {
		return entailment.accept(INSTANCE);
	}

	private EntailmentPrinter() {
		// private default constructor
	}

	@Override
	public String defaultVisit(final Entailment entailment) {
		return entailment.toString();
	}

	@Override
	public <A extends ElkAxiom> String defaultAxiomEntailmentVisit(
			final AxiomEntailment<A> axiomEntailment) {
		return axiomEntailment.getAxiom().toString();
	}

	@Override
	public String visit(
			final OntologyInconsistency inconsistentOntologyEntailment) {
		return OntologyInconsistency.class.getSimpleName();
	}

}
